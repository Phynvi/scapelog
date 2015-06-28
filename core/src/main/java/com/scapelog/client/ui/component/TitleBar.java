package com.scapelog.client.ui.component;

import com.scapelog.api.util.Components;
import com.scapelog.client.ui.ScapeFrame;
import com.scapelog.client.ui.StyleConstants;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.awt.Point;
import java.security.AccessController;
import java.security.PrivilegedActionException;

public final class TitleBar extends HBox {

	public static final int HEIGHT = 30;

	private double toolbarOffsetX = 0;
	private double toolbarOffsetY = 0;

	private final Label logo;
	private final Label beta = new Label("");
	private final WindowControls windowControls;
	private final Region spacer = Components.createSpacer();
	private final ScrollPane contentScroll;

	private final HBox content;
	private final HBox staticContent;

	private final SimpleBooleanProperty draggabilityProperty = new SimpleBooleanProperty(false);

	private boolean isMovingWindow = false;

	private TitleBar(int height, String title, WindowControls windowControls) {
		this.content = new HBox();
		this.staticContent = new HBox();
		this.windowControls = windowControls;

		setId("title-bar");
		setMinHeight(height);
		setMaxHeight(height);

		logo = new Label(title);
		logo.setId("logo");
		logo.setMinWidth(67);
		logo.setPrefWidth(67);

		contentScroll = new ScrollPane(content);
		contentScroll.setPadding(new Insets(0, 0, 0, 0));
		contentScroll.setPannable(true);
		contentScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		contentScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		logo.impl_processCSS(true);
		spacer.impl_processCSS(true);
		contentScroll.impl_processCSS(true);

		getChildren().addAll(logo, spacer, contentScroll, staticContent, windowControls);
	}

	public TitleBar(Stage stage) {
		this(StyleConstants.TITLEBAR_DIMENSIONS.height, stage.getTitle(), new WindowControls(stage));
		stage.titleProperty().bindBidirectional(logo.textProperty());

		setStyle("-fx-border-width: 1 1 0 1;");

		// todo: resize listener?

		setDraggable(stage);
	}

	public TitleBar(ScapeFrame frame) {
		this(StyleConstants.TITLEBAR_DIMENSIONS.height, frame.getTitle(), new WindowControls(frame));

		beta.setText("Beta");
		beta.setId("beta");
		beta.setMinWidth(40);
		beta.setPrefWidth(40);
		beta.impl_processCSS(true);
		getChildren().add(1, beta);

		frame.addResizeListener((observable, oldValue, newValue) -> {
			int frameWidth = newValue.width;
			double logoWidth = logo.prefWidth(-1);
			double betaWidth = beta.prefWidth(-1);
			double controlsWidth = windowControls.getWidth();
			double staticWidth = staticContent.getWidth();
			double emptySpace = frameWidth - logoWidth - betaWidth - controlsWidth - staticWidth;

			contentScroll.setMaxWidth(emptySpace);
		});

		setOnMousePressed(e -> {
			if (isDraggableTarget(e)) {
				if (e.isPrimaryButtonDown()) {
					isMovingWindow = true;
				}
				toolbarOffsetX = e.getX();
				toolbarOffsetY = e.getY();

				if (e.getClickCount() % 2 == 0 && frame.isResizable()) {
					frame.toggleMaximize();
				}
			}
		});
		setOnMouseReleased(e -> isMovingWindow = false);
		setOnMouseDragged(e -> {
			if (isMovingWindow && isDraggableTarget(e)) {
				Point windowPoint;
				try {
					windowPoint = AccessController.doPrivileged(Components.GET_LOCATION_ACTION);
					if (windowPoint != null) {
						windowPoint.x = (int) (windowPoint.x - toolbarOffsetX);
						windowPoint.y = (int) (windowPoint.y - toolbarOffsetY);
						frame.setLocation(windowPoint);
					}
				} catch (PrivilegedActionException ignored) {
					/**/
				}
			}
		});
	}

	private boolean isDraggableTarget(javafx.scene.input.MouseEvent e) {
		return e.getTarget().equals(logo) || e.getTarget().equals(beta) || e.getTarget().equals(spacer);
	}

	public HBox getContent() {
		return content;
	}

	public HBox getStaticContent() {
		return staticContent;
	}

	public boolean isDraggable() {
		return draggabilityProperty.get();
	}

	public void setDraggable(boolean draggable) {
		draggabilityProperty.set(draggable);
	}

	public SimpleBooleanProperty draggabilityProperty() {
		return draggabilityProperty;
	}

	private void setDraggable(Stage stage) {
		class Delta {
			double x, y;
		}
		final Delta delta = new Delta();

		addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
			if (e.isPrimaryButtonDown() && isDraggable()) {
				isMovingWindow = true;
			}
			delta.x = stage.getX() - e.getScreenX();
			delta.y = stage.getY() - e.getScreenY();
		});
		addEventFilter(MouseEvent.MOUSE_RELEASED, e -> isMovingWindow = false);
		addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
			if (isMovingWindow && isDraggable()) {
				stage.setX(e.getScreenX() + delta.x);
				stage.setY(e.getScreenY() + delta.y);
			}
		});
	}

}