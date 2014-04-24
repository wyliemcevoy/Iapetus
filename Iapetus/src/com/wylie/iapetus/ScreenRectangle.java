package com.wylie.iapetus;

import java.awt.Rectangle;

import org.opencv.core.Point;

public class ScreenRectangle
{
	private Point topLeft, bottomRight;
	private int height, width;
	private String name;

	public ScreenRectangle(double minX, double minY, double maxX, double maxY)
	{
		topLeft = new Point(minX, minY);
		this.bottomRight = new Point(maxX, maxY);
		this.height = Math.abs((int) (maxY - minY));
		this.width = (int) Math.abs(maxX - minX);
	}

	public Point getTopLeft()
	{
		return topLeft;
	}

	public void setTopLeft(Point topLeft)
	{
		this.topLeft = topLeft;
	}

	public Point getBottomRight()
	{
		return bottomRight;
	}

	public void setBottomRight(Point bottomRight)
	{
		this.bottomRight = bottomRight;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public Rectangle toRectangle()
	{
		Rectangle rect = new Rectangle();
		rect.height = this.height;
		rect.width = this.width;
		rect.x = (int) topLeft.x;
		rect.y = (int) topLeft.y;
		return rect;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

}
