package com.wylie.iapetus;

import java.util.ArrayList;
import java.util.Collection;

public class BlobFactory
{
	public static Collection<Blob> buildBlobs(int[][] temp)
	{
		ArrayList<Blob> blobs = new ArrayList<Blob>();
		int numBlobs = findNumBlobs(temp);
		System.out.println("Num blobs :" + numBlobs);
		for (int i = 0; i < numBlobs + 1; i++)
		{
			Blob blob = new Blob();
			blob.setIndex(i + 1);
			blobs.add(blob);
		}

		for (int y = 0; y < temp.length; y++)
		{
			for (int x = 0; x < temp[0].length; x++)
			{
				if (temp[y][x] > 0)
				{
					blobs.get(temp[y][x] - 1).addPoint(y, x);
				}
			}
		}

		System.out.println("Blobs size : " + blobs.size());

		for (Blob blob : blobs)
		{
			System.out.println("" + blob.getIndex() + " (" + blob.getCenter().x + "," + blob.getCenter().y + ")");
		}

		return blobs;
	}

	private static int findNumBlobs(int[][] temp)
	{
		int max = -1;
		for (int y = 0; y < temp.length; y++)
		{
			for (int x = 0; x < temp[0].length; x++)
			{
				if (temp[y][x] > max)
				{
					max = temp[y][x];
				}
			}
		}
		return max;
	}

}
