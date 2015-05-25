package com.scapelog.client.ui.shapes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public abstract class Shape3d {

	private final int width, height;
	private final Point3D[] vertices;
	private final Edge[] edges;

	private final BufferedImage backBuffer;
	private final Graphics2D graphics;

	protected int azimuth = 35;

	protected int elevation = 30;

	public Shape3d(int width, int height, Point3D[] vertices, Edge[] edges) {
		this.width = width;
		this.height = height;
		this.vertices = vertices;
		this.edges = edges;
		this.backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.graphics = backBuffer.createGraphics();
		this.graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void update(Graphics g, Color backgroundColor, int x, int y) {
		// compute coefficients for the projection
		double theta = Math.PI * azimuth / 180.0;
		double phi = Math.PI * elevation / 180.0;
		float cosT = (float) Math.cos(theta);
		float sinT = (float) Math.sin(theta);
		float cosP = (float) Math.cos(phi);
		float sinP = (float) Math.sin(phi);
		float cosTcosP = cosT * cosP;
		float cosTsinP = cosT * sinP;
		float sinTcosP = sinT * cosP;
		float sinTsinP = sinT * sinP;

		// project vertices onto the 2D viewport
		Point[] points = new Point[vertices.length];
		int scaleFactor = width / 4;
		float near = 3;  // distance from eye to near plane
		float nearToObj = 1.5f;  // distance from near plane to center of object
		for (int j = 0; j < vertices.length; j++) {
			int x0 = vertices[j].x;
			int y0 = vertices[j].y;
			int z0 = vertices[j].z;

			// compute an orthographic projection
			float x1 = cosT * x0 + sinT * z0;
			float y1 = -sinTsinP * x0 + cosP * y0 + cosTsinP * z0;

			// now adjust things to get a perspective projection
			float z1 = cosTcosP * z0 - sinTcosP * x0 - sinP * y0;
			x1 = x1 * near / (z1 + near + nearToObj);
			y1 = y1 * near / (z1 + near + nearToObj);

			// the 0.5 is to round off when converting to int
			points[j] = new Point((int) (width / 2 + scaleFactor * x1 + 0.5), (int) (height / 2 - scaleFactor * y1 + 0.5));
		}

		graphics.setColor(backgroundColor);
		graphics.fillRect(0, 0, width, height);
		Color oldColor = graphics.getColor();
		graphics.setColor(Color.white);
		for (Edge edge : edges) {
			graphics.drawLine(points[edge.a].x, points[edge.a].y, points[edge.b].x, points[edge.b].y);
		}
		graphics.setColor(oldColor);
		g.drawImage(backBuffer, x, y, null);
	}

}