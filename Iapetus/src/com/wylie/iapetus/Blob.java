package com.wylie.iapetus;

import org.opencv.core.Point;

public class Blob
{
	private Point topLeft, bottomRight, center;
	private int xMin, xMax, yMin, yMax;
	private int height, width;
	private int xTotal, yTotal, pTotal;
	private double mass;
	private int[][] blobRepresentation;
	private int index;
	private boolean initialized;

	public Blob()
	{
		pTotal = 0;
	}

	public double getDensity()
	{
		return mass / (height * width);
	}

	public Point getTopLeft()
	{
		return new Point(xMin, yMin);
	}

	public void setTopLeft(Point topLeft)
	{
		this.topLeft = topLeft;
		this.xMin = (int) topLeft.x;
		this.yMin = (int) topLeft.y;
	}

	public Point getBottomRight()
	{
		return new Point(xMax, yMax);
	}

	public void setBottomRight(Point bottomRight)
	{
		this.bottomRight = bottomRight;
		this.xMax = (int) bottomRight.x;
		this.yMax = (int) bottomRight.y;
	}

	public void setCenter(Point center)
	{
		this.center = center;
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

	public double getMass()
	{
		return pTotal;
	}

	public void setMass(double mass)
	{
		this.mass = mass;
	}

	public int[][] getBlobRepresentation()
	{
		return blobRepresentation;
	}

	public void setBlobRepresentation(int[][] blobRepresentation)
	{
		this.blobRepresentation = blobRepresentation;
	}

	public int getIndex()
	{
		return index;
	}

	public void setIndex(int index)
	{
		this.index = index;
	}

	public void addPoint(int y, int x)
	{
		xTotal += x;
		yTotal += y;
		pTotal++;

		if (!initialized)
		{
			xMin = x;
			xMax = x;
			yMin = y;
			yMax = y;
			initialized = true;
		} else
		{
			if (x < xMin)
			{
				xMin = x;
			}
			if (y < yMin)
			{
				yMin = y;
			}
			if (x > xMax)
			{
				xMax = x;
			}
			if (y > yMax)
			{
				yMax = y;
			}
		}
	}

	public void buildCenter()
	{
		if (pTotal > 10)
		{
			this.center = new Point(Math.floor(xTotal / pTotal), Math.floor(yTotal / pTotal));
		} else
		{
			this.center = new Point(11, 11);
		}

	}

	public Point getCenter()
	{
		buildCenter();
		return center;
	}

	public int getSize()
	{
		return (xMax - xMin) * (yMax - yMin);
	}
}
