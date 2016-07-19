package com.astrofizzbizz.stoneedge3.shared;

@SuppressWarnings("serial")
public class ObsCommandException extends Exception
{
	private ObsCommandReturnInfo obsCommandReturnInfo;
	public ObsCommandException()
	{
		super();
	}
	public ObsCommandException(ObsCommandReturnInfo obsCommandReturnInfo)
	{
		super();
		this.obsCommandReturnInfo = obsCommandReturnInfo;
	}
	public ObsCommandException(Exception e, ObsCommandReturnInfo obsCommandReturnInfo)
	{
		super(e);
		this.obsCommandReturnInfo = obsCommandReturnInfo;
	}
	public ObsCommandException(String smessage, ObsCommandReturnInfo obsCommandReturnInfo)
	{
		super(smessage);
		this.obsCommandReturnInfo = obsCommandReturnInfo;
	}
	public ObsCommandException(String smessage, Throwable cause, ObsCommandReturnInfo obsCommandReturnInfo)
	{
		super(smessage, cause);
		this.obsCommandReturnInfo = obsCommandReturnInfo;
	}
	public ObsCommandException(Throwable cause, ObsCommandReturnInfo obsCommandReturnInfo)
	{
		super(cause);
		this.obsCommandReturnInfo = obsCommandReturnInfo;
	}
	public void printErrorMessage()
	{
		System.out.println("ObsCommandException: " + " " + getMessage());
	}
	public ObsCommandReturnInfo getObsCommandReturnInfo() {return obsCommandReturnInfo;}

}
