package com.astrofizzbizz.stoneedge3.shared;

import java.io.Serializable;


@SuppressWarnings("serial")
public class SimbadReturnInfo  implements Serializable
{
	private String targetName;
	private String raString = null;
	private String decString = null;
	
	public SimbadReturnInfo() {}
	public SimbadReturnInfo(String targetName, String raString, String decString) 
	{
		this.targetName = targetName;
		this.raString = raString;
		this.decString = decString;
	}
	
	public String getTargetName() {return targetName;}
	public String getRaString() {return raString;}
	public String getDecString() {return decString;}
	public void setTargetName(String targetName) {this.targetName = targetName;}
	public void setRaString(String raString) {this.raString = raString;}
	public void setDecString(String decString) {this.decString = decString;}

}
