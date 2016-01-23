package com.scapelog.client.ui.component;

import com.google.common.base.Preconditions;
import com.scapelog.client.ui.ScapeFrame;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Window;

import java.util.List;

public class PopupWindow {
	enum Direction {
		NORTH_WEST,
		NORTH_EAST,
		SOUTH_WEST,
		SOUTH_EAST;

		public static Direction getHorizontalOpposite(Direction direction) {
			switch (direction) {
				case NORTH_EAST:
					return NORTH_WEST;
				case NORTH_WEST:
					return NORTH_EAST;
				case SOUTH_EAST:
					return SOUTH_WEST;
				case SOUTH_WEST:
					return SOUTH_EAST;
			}
			return Direction.NORTH_WEST;
		}

		public static Direction getVerticalOpposite(Direction direction) {
			switch (direction) {
				case NORTH_EAST:
					return SOUTH_EAST;
				case NORTH_WEST:
					return SOUTH_WEST;
				case SOUTH_EAST:
					return NORTH_EAST;
				case SOUTH_WEST:
					return NORTH_WEST;
			}
			return Direction.NORTH_WEST;
		}

	}

	private Popup popup;
	private final BorderPane parent;

	private Window ownerWindow;
	private Direction direction = Direction.SOUTH_WEST;
	private final SimpleBooleanProperty detached = new SimpleBooleanProperty(false);

	private int lastOffset = 0;
	private Node lastOwner = null;

	private final int initialWidth;
	private final int initialHeight;

	private boolean isPrimary;
	private boolean isMovingWindow = false;

	private final InvalidationListener hideListener = observable -> {
		if (!isDetached()) {
			reposition();
		}
	};

	private final ChangeListener<Number> xListener = (observable, oldValue, newValue) -> reposition();
	private final ChangeListener<Number> yListener = (observable, oldValue, newValue) -> reposition();

	private final WeakInvalidationListener weakHideListener = new WeakInvalidationListener(hideListener);
	private final WeakChangeListener<Number> weakXListener = new WeakChangeListener<>(xListener);
	private final WeakChangeListener<Number> weakYListener = new WeakChangeListener<>(yListener);

	private static SimpleObjectProperty<PopupWindow> openPopOver = new SimpleObjectProperty<>();
	private final SimpleBooleanProperty visiblityProperty = new SimpleBooleanProperty(false);
	private final SimpleStringProperty titleProperty = new SimpleStringProperty("");

	static {
		openPopOver.addListener((observable, oldValue, newValue) -> {
			if (oldValue == null || oldValue.isDetached() || !newValue.isPrimary()) {
				return;
			}
			if (oldValue.isPrimary && newValue.isPrimary) {
				oldValue.hide();
			}
		});
	}

	public PopupWindow(Node content, int initialWidth, int initialHeight) {
		this.popup = new Popup();
		this.popup.setAutoFix(false);

		this.initialWidth = initialWidth;
		this.initialHeight = initialHeight;

		parent = new BorderPane();
		parent.getStyleClass().addAll("popup", "frame");
		parent.setCenter(content);

		TitleBar titleBar = new TitleBar(this);

		titleBar.draggabilityProperty().bind(detached);
		detached.addListener((observable, wasDetached, isDetached) -> {
			parent.setTop(isDetached ? titleBar : null);
		});

		popup.setOnCloseRequest(e -> hide());
		popup.getContent().add(parent);
	}

	public PopupWindow(int initialWidth, int initialHeight) {
		this(new Label("no content"), initialWidth, initialHeight);
	}

	public PopupWindow() {
		this(100, 25);
	}

	public final void show(Node owner) {
		show(owner, 0);
	}

	public final void show(Node owner, int offset) {
		if (popup.isShowing()) {
			return;
		}
		Bounds bounds = owner.localToScreen(owner.getBoundsInLocal());
		Point2D point = checkScreenBounds(bounds, owner, offset, false);

		show(owner, point.getX(), point.getY());
		lastOffset = offset;
	}

	private void show(Node owner, double x, double y) {
		if (owner == null || popup.isShowing()) {
			return;
		}

		if (ownerWindow != null) {
			ownerWindow.xProperty().removeListener(weakXListener);
			ownerWindow.xProperty().removeListener(weakYListener);
			ownerWindow.widthProperty().removeListener(weakHideListener);
			ownerWindow.heightProperty().removeListener(weakHideListener);
		}

		ownerWindow = owner.getScene().getWindow();
		ownerWindow.xProperty().addListener(weakXListener);
		ownerWindow.yProperty().addListener(weakYListener);
		ownerWindow.widthProperty().addListener(weakHideListener);
		ownerWindow.heightProperty().addListener(weakHideListener);

		visiblityProperty.set(true);
		if (isPrimary) {
			openPopOver.set(this);
		}

		setDetached(false);

		popup.show(owner, x, y);
		_setX(x);
		_setY(y);

		lastOwner = owner;
	}

	public void show(Window window, double x, double y) {
		if (window == null || popup.isShowing()) {
			return;
		}

		if (ownerWindow != null) {
			ownerWindow.xProperty().removeListener(weakXListener);
			ownerWindow.xProperty().removeListener(weakYListener);
		}

		ownerWindow = window;
		ownerWindow.xProperty().addListener(weakXListener);
		ownerWindow.yProperty().addListener(weakYListener);

		visiblityProperty.set(true);
		if (isPrimary) {
			openPopOver.set(this);
		}

		setDetached(false);

		popup.show(window);
		_setX(x);
		_setY(y);
	}

	public final void setTitle(String title) {
		titleProperty.set(title);
	}

	private Point2D getRelativePoint(Node owner, int offset) {
		Bounds bounds = owner.localToScreen(owner.getBoundsInLocal());
		return checkScreenBounds(bounds, owner, offset, true);
	}

	private Point2D checkScreenBounds(Bounds bounds, Node owner, int offset, boolean dragging) {
		Point2D point = getPointForBounds(direction, bounds, offset);

		Window window = owner.getScene().getWindow();
		ObservableList<Screen> screens = Screen.getScreensForRectangle(window.getX(), window.getY(), window.getWidth(), window.getHeight());
		if (!screens.isEmpty()) {
			Screen screen = screens.get(0);
			Rectangle2D screenBounds = screen.getBounds();

			boolean outside = false;
			if ((direction == Direction.NORTH_WEST || direction == Direction.NORTH_EAST) && point.getY() < 0) {
				outside = true;
			}
			if ((direction == Direction.SOUTH_WEST || direction == Direction.SOUTH_EAST) && point.getY() + initialHeight > screenBounds.getMaxY()) {
				outside = true;
			}
			if (outside) {
				point = getPointForBounds(Direction.getVerticalOpposite(direction), bounds, offset);
			}

			if (point.getX() + initialWidth > screenBounds.getMaxX() && !dragging) {
				point = getPointForBounds(Direction.getHorizontalOpposite(direction), bounds, offset);
			}
		}
		return point;
	}

	private Point2D getPointForBounds(Direction direction, Bounds bounds, int offset) {
		double x = 0;
		double y = 0;

		switch(direction) {
			case NORTH_EAST:
				x = bounds.getMinX();
				y = bounds.getMinY() - initialHeight - offset;
				break;
			case NORTH_WEST:
				x = bounds.getMaxX() - initialWidth;
				y = bounds.getMinY() - initialHeight - offset;
				break;
			case SOUTH_EAST:
				x = bounds.getMinX() + offset;
				y = bounds.getMaxY() + offset;
				break;
			case SOUTH_WEST:
				x = bounds.getMaxX() - initialWidth - offset;
				y = bounds.getMaxY() + offset;
				break;
		}
		return new Point2D(x, y);
	}

	public final void centerToScreen() {
		List<Screen> screens = Screen.getScreensForRectangle(popup.getX(), popup.getY(), popup.getWidth(), popup.getHeight());
		if (screens.size() == 0) {
			System.out.println("No screens found, centering aborted");
			return;
		}
		Screen screen = screens.get(0);
		Rectangle2D bounds = screen.getBounds();

		double width = popup.getWidth();
		double height = popup.getHeight();

		popup.setX(bounds.getMinX() + (bounds.getWidth() - width) / 2);
		popup.setY(bounds.getMinY() + (bounds.getHeight() - height) / 2);
	}

	public final void hide() {
		visiblityProperty.set(false);
		popup.hide();
	}

	public final void setContent(Node content) {
		content.getStyleClass().add("content");
		parent.setCenter(content);
	}

	public final Node getContent() {
		return parent.getCenter();
	}

	public final void setX(double x) {
		Preconditions.checkState(!isDetached(), "Position can only be set for detached popup windows!");
		_setX(x);
	}

	public final void setY(double y) {
		Preconditions.checkState(!isDetached(), "Position can only be set for detached popup windows!");
		_setY(y);
	}

	private void _setX(double x) {
		popup.setX(x);
	}

	private void _setY(double y) {
		popup.setY(y);
	}

	public final double getX() {
		return popup.getX();
	}

	public final double getY() {
		return popup.getY();
	}

	public final Popup getPopup() {
		return popup;
	}

	public final void reposition() {
		if (isDetached()) {
			return;
		}
		Point2D point = getRelativePoint(lastOwner, lastOffset);
		_setX(point.getX());
		_setY(point.getY());
	}

	public final Direction getDirection() {
		return direction;
	}

	public final void setDirection(Direction direction) {
		this.direction = direction;
	}

	public final boolean isDetached() {
		return detached.get();
	}

	public final void setDetached(boolean detached) {
		this.detached.set(detached);
	}

	public boolean isFocused() {
		return popup.isFocused();
	}

	public ReadOnlyBooleanProperty focusedProperty() {
		return popup.focusedProperty();
	}

	public final void toggleDetach() {
		boolean detached = !isDetached();
		setDetached(detached);
		if (!detached) {
			reposition();
		}
	}

	public final SimpleBooleanProperty detachedProperty() {
		return detached;
	}

	public final SimpleBooleanProperty getVisibilityProperty() {
		return visiblityProperty;
	}

	public final SimpleStringProperty getTitleProperty() {
		return titleProperty;
	}

	private void setShowing(boolean value) {
		Toolkit.getToolkit().checkFxUserThread();
		visiblityProperty.set(value);
	}

	public final boolean isShowing() {
		return visiblityProperty.get();
	}

	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public final void addFrameEvents(ScapeFrame frame, Node owner) {
		SimpleBooleanProperty restorePopOver = new SimpleBooleanProperty(false);
		frame.iconifiedPropertyProperty().addListener((observable, oldValue, iconified) -> {
			Platform.runLater(() -> {
				if (isDetached()) {
					return;
				}
				boolean restore = restorePopOver.get();

				// re-open when frame is de-iconified
				restorePopOver.set(isShowing());

				if (!iconified && restore) {
					show(owner, 0);
				}
				if (iconified && isShowing()) {
					hide();
				}
			});
		});
	}

	public final void setDraggable(Node node) {
		class Delta {
			double x, y;
		}
		final Delta delta = new Delta();

		node.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
			if (e.isPrimaryButtonDown()) {
				isMovingWindow = true;
			}
			delta.x = getX() - e.getScreenX();
			delta.y = getY() - e.getScreenY();
		});
		node.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> isMovingWindow = false);
		node.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
			if (isMovingWindow && isDetached()) {
				_setX(e.getScreenX() + delta.x);
				_setY(e.getScreenY() + delta.y);
			}
		});
	}

}