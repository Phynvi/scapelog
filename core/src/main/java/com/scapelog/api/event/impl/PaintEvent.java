package com.scapelog.api.event.impl;

import com.scapelog.api.event.Event;

import java.awt.Graphics2D;

public final class PaintEvent extends Event {

	private final Graphics2D graphics;

	private final int width;

	private final int height;

	public PaintEvent(Graphics2D graphics, int width, int height) {
		this.graphics = graphics;
		this.width = width;
		this.height = height;
	}

	public Graphics2D getGraphics() {
		return graphics;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

}