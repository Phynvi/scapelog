package com.scapelog.api.ui;

import com.google.common.base.Preconditions;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class Overlay {

	private final SimpleIntegerProperty x = new SimpleIntegerProperty();
	private final SimpleIntegerProperty y = new SimpleIntegerProperty();
	private final SimpleIntegerProperty width = new SimpleIntegerProperty(1);
	private final SimpleIntegerProperty height = new SimpleIntegerProperty(1);
	private final SimpleBooleanProperty visibility = new SimpleBooleanProperty(true);
	private final SimpleBooleanProperty movable = new SimpleBooleanProperty(false);

	private BufferedImage bufferedImage;

	public Overlay() {
		this(1, 1);
	}

	public Overlay(int width, int height) {
		Preconditions.checkArgument(width > 0, "Width must be higher than 0!");
		Preconditions.checkArgument(height > 0, "Height must be higher than 0!");
		setWidth(width);
		setHeight(height);
		this.bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	public abstract void paint(Graphics2D graphics);

	public int getX() {
		return x.get();
	}

	public SimpleIntegerProperty xProperty() {
		return x;
	}

	public int getY() {
		return y.get();
	}

	public SimpleIntegerProperty yProperty() {
		return y;
	}

	public int getHeight() {
		return height.get();
	}

	public SimpleIntegerProperty heightProperty() {
		return height;
	}

	public void setHeight(int height) {
		recreateImage();
		this.height.set(height);
	}

	public int getWidth() {
		return width.get();
	}

	public SimpleIntegerProperty widthProperty() {
		return width;
	}

	public void setWidth(int width) {
		recreateImage();
		this.width.set(width);
	}

	public void setY(int y) {
		this.y.set(y);
	}

	public void setX(int x) {
		this.x.set(x);
	}

	public boolean isVisible() {
		return visibility.get();
	}

	public SimpleBooleanProperty visibilityProperty() {
		return visibility;
	}

	public void setVisible(boolean visibility) {
		this.visibility.set(visibility);
	}

	public boolean isMovable() {
		return movable.get();
	}

	public SimpleBooleanProperty movableProperty() {
		return movable;
	}

	public void setMovable(boolean movable) {
		this.movable.set(movable);
	}

	private void recreateImage() {
		this.bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
	}

	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

}