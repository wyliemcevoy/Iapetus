package com.wylie.iapetus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class SetOfSets
{

	private ArrayList<HashSet<Integer>> labels;

	public SetOfSets()
	{
		labels = new ArrayList<HashSet<Integer>>();
	}

	public void add(Collection<Integer> inSet)
	{
		HashSet<Integer> newSet = new HashSet<Integer>();

		for (Integer i : inSet)
		{
			newSet.addAll(getAndRemoveSet(i));
		}
		labels.add(newSet);
	}

	private HashSet<Integer> getAndRemoveSet(int i)
	{
		Iterator<HashSet<Integer>> iter = labels.iterator();
		while (iter.hasNext())
		{
			HashSet<Integer> set = iter.next();
			if (set.contains(i))
			{
				iter.remove();
				return set;
			}
		}

		HashSet<Integer> newSet = new HashSet<Integer>();
		newSet.add(i);
		return newSet;
	}

	public int getLabel(int i)
	{
		for (HashSet<Integer> set : labels)
		{
			if (set.contains(i))
			{
				return getMin(set);
			}
		}
		return -1;
	}

	private int getMin(HashSet<Integer> set)
	{
		int min = set.iterator().next();

		for (Integer i : set)
		{
			if (min > i)
			{
				return min = i;
			}
		}

		return min;
	}

	@Override
	public String toString()
	{
		String build = "";
		for (HashSet<Integer> set : labels)
		{
			build += "[";
			for (Integer i : set)
			{
				build += " " + i;
			}

			build += " ] , ";
		}
		return build;
	}

	public Collection<Integer> getMinRepresentatives()
	{
		ArrayList<Integer> result = new ArrayList<Integer>();

		for (HashSet<Integer> set : labels)
		{
			result.add(this.getMin(set));
		}

		return result;
	}

	public int getIndex(int i)
	{
		for (int j = 0; j < labels.size(); j++)
		{
			if (labels.get(j).contains(i))
			{
				return j;
			}
		}
		return -1;
	}

	public int getNumSets()
	{
		return labels.size();
	}
}
