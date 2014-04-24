package com.wylie.iapetus;

import org.opencv.core.Point;

public class VertacleScrollArea extends ScreenRectangle
{
	private Point up, down;

	public VertacleScrollArea(double minX, double minY, double maxX, double maxY)
	{
		super(minX, minY, maxX, maxY);
	}

	/**
	 * @return the down
	 */
	public Point getDown()
	{
		return down;
	}

	/**
	 * @param down the down to set
	 */
	public void setDown(Point down)
	{
		this.down = down;
	}

	/**
	 * @return the up
	 */
	public Point getUp()
	{
		return up;
	}

	/**
	 * @param up the up to set
	 */
	public void setUp(Point up)
	{
		this.up = up;
	}

}
