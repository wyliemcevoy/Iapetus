package com.wylie.iapetus;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class Runner
{
	public Runner()
	{
		TesterBot bot = new TesterBot();
		
		Mat mat = Highgui.imread("map.jpg");
		
		Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
		Mat grey = mat.clone();
		Imgproc.threshold(grey, grey, 15, 255, Imgproc.THRESH_BINARY_INV);
		
		Imgproc.dilate(grey, grey, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));
		Imgproc.erode(grey, grey, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(4, 4)));
		
		for (int y = 0; y < grey.height(); y++)
		{
			for (int x = 0; x < grey.width(); x++)
			{
				double[] data = grey.get(y, x);
				if (data[0] > 1)
				{
					data[0] = 0;
					
				} else
				{
					data[0] = 255;
				}
				grey.put(y, x, data);
			}
		}
		
		Point start = new Point(38, 58);
		
		Pather pather = new Pather(grey, start);
		PathCircle p = pather.expand(start);
		Core.circle(mat, p.getPoint(), (int) p.getRadius(), new Scalar(255, 0, 0));
		
		Core.rectangle(mat, start, new Point(100, 100), new Scalar(255, 255, 255));
		
		bot.show(grey);
		bot.show(mat);
		
	}
	
}
