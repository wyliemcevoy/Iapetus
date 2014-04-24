package com.wylie.iapetus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.opencv.core.Mat;

public class VisHel
{
	
	public static Collection<Blob> lableConnectedComponents(Mat input) {
		SetOfSets sos = new SetOfSets();
		int width = input.width() + 2;
		int height = input.height() + 2;
		int[][] temp = convertToIntMat(input);
		
		int nextIndex = 2;
		
		for (int y = 1; y < height - 1; y++)
		{
			for (int x = 1; x < width - 1; x++)
			{
				
				int t = temp[y][x];
				if (t > 0)
				{
					ArrayList<Integer> connected = getConnected(temp, x, y);
					
					if (connected.size() > 0)
					{
						int min = getMin(connected);
						temp[y][x] = min;
						
						sos.add(connected);
					} else
					{
						temp[y][x] = nextIndex;
						nextIndex++;
						ArrayList<Integer> newSet = new ArrayList<Integer>();
						newSet.add(nextIndex);
						sos.add(newSet);
					}
					
				} else
				{
					
				}
			}
			
		}
		
		// Second pass to merge all the labels
		for (int y = 1; y < height - 1; y++)
		{
			for (int x = 1; x < width - 1; x++)
			{
				int t = temp[y][x];
				if (t > 0)
				{
					temp[y][x] = sos.getIndex(temp[y][x]) + 1;
					// System.out.println("" + temp[y][x]);
				}
			}
		}
		
		return BlobFactory.buildBlobs(temp);
	}
	
	private static ArrayList<Integer> getConnected(int[][] temp, int x, int y) {
		ArrayList<Integer> connected = new ArrayList<Integer>();
		connected.add(temp[y - 1][x - 1]);
		connected.add(temp[y - 1][x + 0]);
		connected.add(temp[y - 1][x + 1]);
		connected.add(temp[y][x - 1]);
		
		Iterator<Integer> iterator = connected.iterator();
		while (iterator.hasNext())
		{
			Integer next = iterator.next();
			if (next == 0)
			{
				iterator.remove();
			}
		}
		
		return connected;
	}
	
	private static int getMin(ArrayList<Integer> ints) {
		if (ints.size() > 0)
		{
			int min = ints.get(0);
			
			for (int i = 1; i < ints.size(); i++)
			{
				if (ints.get(i) < min)
				{
					min = ints.get(i);
				}
			}
			
			return min;
		} else
		{
			return -1;
		}
	}
	
	private static int[][] convertToIntMat(Mat mat) {
		int width = mat.width() + 2;
		int height = mat.height() + 2;
		int[][] temp = new int[height][width];
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				if (x == 0 || x == width || y == 0 || y == height)
				{
					temp[y][x] = 0;
				} else
				{
					if (mat.get(y - 1, x - 1) == null || (int) mat.get(y - 1, x - 1)[0] == 0)
					{
						temp[y][x] = 0;
					} else
					{
						temp[y][x] = 1;
					}
				}
			}
		}
		
		return temp;
	}
	
	public static void print(int[][] ints) {
		int height = ints.length;
		int width = ints[0].length;
		
		for (int y = 1; y < height - 1; y++)
		{
			
			for (int x = 1; x < width - 1; x++)
			{
				if (ints[y][x] > 0)
				{
					if (ints[y][x] > 9)
					{
						System.out.print((int) (ints[y][x] - 10 * Math.floor(ints[y][x] / 10)) + "");
					} else
					{
						System.out.print(ints[y][x]);
					}
				} else
				{
					System.out.print(" ");
				}
			}
			
			System.out.println();
			
		}
	}
	
}
