package com.scapelog.client.ui.component;

import com.scapelog.client.ui.DecoratedFrame;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;

import javax.swing.WindowConstants;
import java.util.Objects;

public final class PopupFrame extends DecoratedFrame {

	private final StringProperty titleProperty;
	private final BooleanProperty resizableProperty;
	private final ObjectProperty<Node> contentProperty = new SimpleObjectProperty<>(new Label("<no content>"));
	private final ObjectProperty<Location> locationProperty = new SimpleObjectProperty<>(Location.LEFT_BOTTOM);

	public PopupFrame(String title, int width, int height, boolean resizable) {
		super(title, width, height, resizable);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		this.titleProperty = new SimpleStringProperty(title);
		this.resizableProperty = new SimpleBooleanProperty(resizable);

		titleProperty.addListener((observable, oldTitle, newTitle) -> getFrame().setTitle(newTitle));
		resizableProperty.addListener((observable, oldValue, newValue) -> getFrame().setResizable(newValue));
		contentProperty.addListener((observable, oldContent, newContent) -> getRoot().setCenter(newContent));

		removeTitleBar();
	}

	public void setContent(Node content) {
		contentProperty.set(content);
	}

	public void show(Node owner) {
		show(owner, 4);
	}

	public void show(Node owner, int offset) {
		Objects.requireNonNull(owner);

		Bounds ownerBounds = owner.localToScreen(owner.getBoundsInLocal());
		Bounds contentBounds = getContent().getBoundsInParent();

		double x = 0;
		double y = 0;

		Location location = getLocation();
		if (location == Location.BOTTOM_CENTER || location == Location.TOP_CENTER) {
			x = (ownerBounds.getMinX() + (ownerBounds.getWidth() / 2)) - (contentBounds.getWidth() / 2) + offset;
		} else if (location == Location.BOTTOM_LEFT || location == Location.TOP_LEFT) {
			x = ownerBounds.getMinX() + offset;
		} else if (location == Location.BOTTOM_RIGHT || location == Location.TOP_RIGHT) {
			x = ownerBounds.getMaxX() - contentBounds.getWidth() - offset;
		} else if (location == Location.LEFT_TOP || location == Location.RIGHT_TOP) {
			y = ownerBounds.getMinY() - offset;
		} else if (location == Location.LEFT_CENTER || location == Location.RIGHT_CENTER) {
			y = (ownerBounds.getMaxY() - (ownerBounds.getHeight() / 2)) - (contentBounds.getHeight() / 2) - offset;
		}

		// set y of top and bottom
		if (location == Location.BOTTOM_LEFT || location == Location.BOTTOM_CENTER || location == Location.BOTTOM_RIGHT) {
			y = ownerBounds.getMaxY() + offset;
		} else if (location == Location.TOP_LEFT || location == Location.TOP_CENTER || location == Location.TOP_RIGHT) {
			y = ownerBounds.getMinY() - contentBounds.getHeight() - offset;
		}

		//set x of left and right
		if (location == Location.LEFT_TOP || location == Location.LEFT_CENTER || location == Location.LEFT_BOTTOM) {
			x = ownerBounds.getMinX() - contentBounds.getWidth() - offset;
		} else if (location == Location.RIGHT_TOP || location == Location.RIGHT_CENTER || location == Location.RIGHT_BOTTOM) {
			x = ownerBounds.getMaxX() + offset;
		}

		show(x, y);
	}

	public void show(double x, double y) {
		super.show();
		getFrame().setLocation((int) x, (int) y);
	}

	public Node getContent() {
		return contentProperty.get();
	}

	public Location getLocation() {
		return locationProperty.get();
	}

	public int getX() {
		return getFrame().getX();
	}

	public int getY() {
		return getFrame().getY();
	}

	public void setX(int x) {
		getFrame().setLocation(x, getFrame().getY());
	}

	public void setY(int y) {
		getFrame().setLocation(getFrame().getX(), y);
	}

	public enum Location {
		LEFT_TOP, LEFT_CENTER, LEFT_BOTTOM, RIGHT_TOP, RIGHT_CENTER, RIGHT_BOTTOM, TOP_LEFT, TOP_CENTER, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT
	}
}