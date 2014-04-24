package com.wylie.iapetus;

public class ApplicationModel
{
	private String name;
	private ScreenRectangle bounds, locationOnTaskBar;

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

	/**
	 * @return the bounds
	 */
	public ScreenRectangle getBounds()
	{
		return bounds;
	}

	/**
	 * @param bounds
	 *            the bounds to set
	 */
	public void setBounds(ScreenRectangle bounds)
	{
		this.bounds = bounds;
	}

	/**
	 * @return the locationOnTaskBar
	 */
	public ScreenRectangle getLocationOnTaskBar()
	{
		return locationOnTaskBar;
	}

	/**
	 * @param locationOnTaskBar
	 *            the locationOnTaskBar to set
	 */
	public void setLocationOnTaskBar(ScreenRectangle locationOnTaskBar)
	{
		this.locationOnTaskBar = locationOnTaskBar;
	}

}
