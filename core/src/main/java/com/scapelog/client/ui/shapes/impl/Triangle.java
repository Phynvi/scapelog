package com.scapelog.client.ui.shapes.impl;

import com.scapelog.client.ui.shapes.Edge;
import com.scapelog.client.ui.shapes.Point3D;
import com.scapelog.client.ui.shapes.Shape3d;

import java.awt.Color;
import java.awt.Graphics;

public final class Triangle extends Shape3d {

	public Triangle(int width, int height) {
		super(width, height, createVertices(), createEdges());
	}

	@Override
	public void update(Graphics g, Color foregroundColor, Color backgroundColor, int x, int y) {
		azimuth -= 1;
		super.update(g, foregroundColor, backgroundColor, x, y);
	}

	private static Point3D[] createVertices() {
		Point3D[] vertices = new Point3D[5];
		vertices[0] = new Point3D(-1, -1, -1);  //0: bottom left back
		vertices[1] = new Point3D(-1, -1, 1);   //1: bottom left front
		vertices[2] = new Point3D(1, -1, -1);   //4: bottom right back
		vertices[3] = new Point3D(1, -1, 1);    //5: bottom right front
		vertices[4] = new Point3D(0, 1, 0);    //6: top middle
		return vertices;
	}

	private static Edge[] createEdges() {
		Edge[] edges = new Edge[8];
		edges[0] = new Edge(0, 1); //bottom left back to bottom left front
		edges[1] = new Edge(0, 4); //bottom left back to top middle
		edges[2] = new Edge(0, 2); //bottom left back to bottom right back
		edges[3] = new Edge(1, 4); //bottom left front to top middle
		edges[4] = new Edge(1, 3); //bottom left front to bottom right front
		edges[5] = new Edge(2, 3); //bottom right back to bottom right front
		edges[6] = new Edge(2, 4); //bottom right back to top middle
		edges[7] = new Edge(3, 4);//bottom right front to top middle
		return edges;
	}

}