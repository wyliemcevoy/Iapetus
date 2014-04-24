package com.wylie.iapetus;

import java.util.ArrayList;

import org.opencv.core.Point;

public class PathCircle
{
	private Point point;
	private double radius;
	private ArrayList<PathCircle> connected;
	
	public PathCircle(Point point, double radius)
	{
		this.point = point;
		this.radius = radius;
		this.connected = new ArrayList<PathCircle>();
	}
	
	public double getRadius() {
		return radius;
	}
	
	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	public Point getPoint() {
		return point;
	}
	
	public void setPoint(Point point) {
		this.point = point;
	}
	
	public ArrayList<PathCircle> getConnected() {
		return connected;
	}
	
	public void setConnected(ArrayList<PathCircle> connected) {
		this.connected = connected;
	}
	
	public void addConnected(PathCircle pathCircle) {
		connected.add(pathCircle);
	}
	
}
