package com.astrofizzbizz.stoneedge3.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ObsCommandReturnInfo implements Serializable
{
	private String command =null;
	private int bufferIndex = -1;
	private String[] response = null;
	public ObsCommandReturnInfo()
	{
	}

	public ObsCommandReturnInfo(String command, String[] response, int bufferIndex)
	{
		this.bufferIndex = bufferIndex;
		this.command = command;
		this.response = response;
	}
	public String getCommand() {return command;}
	public int getBufferIndex() {return bufferIndex;}
	public String[] getResponse() {return response;}
	public void setCommand(String command) {this.command = command;}
	public void setBufferIndex(int bufferIndex) {this.bufferIndex = bufferIndex;}
	public void setResponse(String[] response) {this.response = response;}
}
