package com.astrofizzbizz.stoneedge3.client;

import com.google.gwt.user.client.Command;

public class MainMenuActionListeners implements Command
{
	StoneEdge3 stoneEdge3;
	String actionCommand;
	
	public MainMenuActionListeners(StoneEdge3 stoneEdge3, String actionCommand)
	{
		this.actionCommand = actionCommand;
		this.stoneEdge3 = stoneEdge3;
	}
	@Override
	public void execute() 
	{
		if (actionCommand.equals("Help.About"))
		{
			String message = "StoneEdge III " + stoneEdge3.version + "\n" + "Last Updated " + stoneEdge3.versionDate;
			stoneEdge3.messageDialog.setMessage("About", message, true);
		}
		if (actionCommand.equals("Help.Contact"))
		{
			String message = "Stone Edge Obervatory Coordinator:";
			message = message + "\n          Vivian Hoette of Yerkes Observatory";
			message = message + "\n          email: vhoette@yerkes.uchicago.edu";
			message = message + "\n          phone: 262-215-1599";
			message = message + "\n Program written by:";
			message = message + "\n          Dave McGinnis";
			message = message + "\n          email: dmcginnis427@gmail.com";
			message = message + "\n          phone: 630-457-4205";
			stoneEdge3.messageDialog.setMessage("Contact", message, true);
		}
		if (actionCommand.equals("Lock.Change Lock"))
		{
			stoneEdge3.lockDialog.showDialog();
		}
	}

}
