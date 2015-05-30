package com.scapelog.client.ui.shapes.impl;

import com.scapelog.client.ui.shapes.Edge;
import com.scapelog.client.ui.shapes.Point3D;
import com.scapelog.client.ui.shapes.Shape3d;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public final class Cube extends Shape3d {
	private final Random random = new Random();

	public Cube(int width, int height) {
		super(width, height, createVertices(), createEdges());
	}

	@Override
	public void update(Graphics g, Color foregroundColor, Color backgroundColor, int x, int y) {
		int rand = random.nextInt(1) == 0 ? -1 : 1;
		azimuth -= rand;
		elevation += rand;
		super.update(g, foregroundColor, backgroundColor, x, y);
	}

	private static Point3D[] createVertices() {
		Point3D[] vertices = new Point3D[8];
		vertices[0] = new Point3D(-1, -1, -1);  //0: bottom left back
		vertices[1] = new Point3D(-1, -1, 1);   //1: bottom left front
		vertices[2] = new Point3D(-1, 1, -1);   //2: top left back
		vertices[3] = new Point3D(-1, 1, 1);    //3: top left front
		vertices[4] = new Point3D(1, -1, -1);   //4: bottom right back
		vertices[5] = new Point3D(1, -1, 1);    //5: bottom right front
		vertices[6] = new Point3D(1, 1, -1);    //6: top right back
		vertices[7] = new Point3D(1, 1, 1);     //7: top right front
		return vertices;
	}

	private static Edge[] createEdges() {
		Edge[] edges = new Edge[12];
		edges[0] = new Edge(0, 1); //bottom left back to bottom left front
		edges[1] = new Edge(0, 2); //bottom left back to top left back
		edges[2] = new Edge(0, 4); //bottom left back to bottom right back
		edges[3] = new Edge(1, 3); //bottom left front to top left front
		edges[4] = new Edge(1, 5); //bottom left front to bottom right front
		edges[5] = new Edge(2, 3); //top left back to top left front
		edges[6] = new Edge(2, 6); //top left back to top right back
		edges[7] = new Edge(3, 7); //top left front to top right front
		edges[8] = new Edge(4, 5); //bottom right back to bottom right front
		edges[9] = new Edge(4, 6); //bottom right back to top right back
		edges[10] = new Edge(5, 7);//bottom right front to top right front
		edges[11] = new Edge(6, 7);//top right back to top right front
		return edges;
	}

}