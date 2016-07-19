package com.astrofizzbizz.stoneedge3.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ImagingReturnInfo implements Serializable
{
	public ImagingSendInfo imagingSendInfo;
	private boolean starFoundInField = false;
	private double starPixelSize = -1.0;
	private double exposureLevel = -1.0;
	public String[] data = null;
	
	public boolean isStarFoundInField() {return starFoundInField;}
	public double getStarPixelSize() {return starPixelSize;}
	public double getExposureLevel() {return exposureLevel;}

	public void setStarFoundInField(boolean starFoundInField) {this.starFoundInField = starFoundInField;}
	public void setStarPixelSize(double starPixelSize) {this.starPixelSize = starPixelSize;}
	public void setExposureLevel(double exposureLevel) {this.exposureLevel = exposureLevel;}
	public ImagingReturnInfo()
	{
		
	}
	public void addData(String[] data)
	{
		if (data == null)
		{
			this.data = null;
			return;
		}
		if (data.length < 1)
		{
			this.data = null;
			return;
		}
		this.data = new String[data.length];
		for (int ii = 0; ii < data.length; ++ii )	
		{
			this.data[ii] = data[ii];
		}
		return;
	}
	
}
