package com.wylie.iapetus;

import org.opencv.core.Mat;
import org.opencv.core.Point;

public class Pather
{
	private Mat mat;
	private Point start;
	
	public Pather(Mat mat, Point start)
	{
		this.mat = mat;
		this.start = start;
	}
	
	public PathCircle expand(Point point) {
		
		boolean b = true;
		double radius = 0;
		while (b)
		{
			radius++;
			
			double[] left = mat.get((int) (point.y - radius), (int) (point.x));
			double[] right = mat.get((int) (point.y + radius), (int) (point.x));
			if (left[0] == 0 || right[0] == 0)
			{
				b = false;
			}
		}
		System.out.println(radius);
		PathCircle pathCircle = new PathCircle(point, radius);
		return pathCircle;
	}
}
