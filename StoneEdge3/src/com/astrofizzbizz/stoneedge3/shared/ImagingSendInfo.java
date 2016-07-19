package com.astrofizzbizz.stoneedge3.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ImagingSendInfo implements Serializable
{
	private String imageName;
	private double shutterTime;
	private int bin;
	private boolean dark;
	private String user;
	private String session;
	private String targetName;
	private boolean debug;
	private String[] debugResponse;
	
	public String getImageName() {return imageName;}
	public double getShutterTime() {return shutterTime;}
	public int getBin() {return bin;}
	public boolean isDark() {return dark;}
	public String getUser() {return user;}
	public String getSession() {return session;}
	public String getTargetName() {return targetName;}
	public boolean isDebug() {return debug;}
	public String[] getDebugResponse() {return debugResponse;}
	
	public void setTargetName(String targetName) {this.targetName = targetName;}
	public void setImageName(String imageName) {this.imageName = imageName;}
	public void setShutterTime(double shutterTime) {this.shutterTime = shutterTime;}
	public void setDark(boolean dark) {this.dark = dark;}
	public void setBin(int bin) {this.bin = bin;}
	public void setUser(String user) {this.user = user;}
	public void setSession(String session) {this.session = session;}
	public void setDebug(boolean debug) {this.debug = debug;}
	public void setDebugResponse(String[] debugResponse) 
	{
		if (debugResponse != null)
		{
			this.debugResponse = new String[debugResponse.length];
			for (int ii = 0; ii < debugResponse.length; ++ ii) this.debugResponse[ii] = debugResponse[ii];
		}
		else
		{
			debugResponse = null;
		}
	}
	
	public ImagingSendInfo()
	{
		
	}
	public ImagingSendInfo(ImagingSendInfo imagingSendInfo)
	{
		setTargetName(imagingSendInfo.getTargetName());
		setImageName(imagingSendInfo.getImageName());
		setShutterTime(imagingSendInfo.getShutterTime());
		setDark(imagingSendInfo.isDark());
		setBin(imagingSendInfo.getBin());
		setUser(imagingSendInfo.getUser());
		setSession(imagingSendInfo.getSession());
		setDebug(imagingSendInfo.isDebug());
		if (imagingSendInfo.debugResponse != null)
		{
			debugResponse = new String[imagingSendInfo.debugResponse.length];
			for (int ii = 0; ii < imagingSendInfo.debugResponse.length; ++ ii)
				debugResponse[ii] = imagingSendInfo.debugResponse[ii];
		}
		else
		{
			debugResponse = null;
		}
	}
}
