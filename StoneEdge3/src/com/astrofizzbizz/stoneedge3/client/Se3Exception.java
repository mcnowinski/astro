package com.astrofizzbizz.stoneedge3.client;


@SuppressWarnings("serial")
public class Se3Exception extends Exception
{
	public Se3Exception()
	{
		super();
	}
	public Se3Exception(String smessage)
	{
		super(smessage);
	}
	public Se3Exception(String smessage, Throwable cause)
	{
		super(smessage, cause);
	}
	public void printErrorMessage()
	{
		System.out.println("StoneEdgeIIIException: " + " " + getMessage());
	}

}
