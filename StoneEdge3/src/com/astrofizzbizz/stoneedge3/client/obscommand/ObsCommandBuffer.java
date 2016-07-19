package com.astrofizzbizz.stoneedge3.client.obscommand;
import java.util.ArrayList;
import java.util.Date;

import com.astrofizzbizz.stoneedge3.client.Se3TabLayoutScrollPanel;
import com.astrofizzbizz.stoneedge3.client.StoneEdge3;
import com.astrofizzbizz.stoneedge3.shared.ObsCommandReturnInfo;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ObsCommandBuffer extends Timer
{
	private ArrayList<ObsCommand> commandBuffer = new ArrayList<ObsCommand>();
	boolean executingComand = false;
	boolean pauseBuffer = false;
	StoneEdge3 stoneEdge3;
	private Se3TabLayoutScrollPanel obsCommandBufferMainPanel;
	private FlexTable obsCommandBufferFlexTable = new FlexTable();
	private FlexTable oldObsCommandBufferFlexTable = new FlexTable();

	public ObsCommandBuffer(StoneEdge3 stoneEdge3, int milliSecInterval)
	{
		this.stoneEdge3 = stoneEdge3;
		scheduleRepeating(milliSecInterval);
		
		CaptionPanel waitingCommandsCaptionPanel = new CaptionPanel("Awaiting Commands");
		CaptionPanel completedCommandsCaptionPanel = new CaptionPanel("Completed Commands");
		ScrollPanel completedCommandsScrollPanel = new ScrollPanel(oldObsCommandBufferFlexTable);
		completedCommandsScrollPanel.setAlwaysShowScrollBars(true);
		completedCommandsScrollPanel.setSize("850px", "600px");
		obsCommandBufferFlexTable.getColumnFormatter().setWidth(0, "250px");
		obsCommandBufferFlexTable.getColumnFormatter().setWidth(1, "600px");
		oldObsCommandBufferFlexTable.getColumnFormatter().setWidth(0, "250px");
		oldObsCommandBufferFlexTable.getColumnFormatter().setWidth(1, "600px");

		waitingCommandsCaptionPanel.setContentWidget(obsCommandBufferFlexTable);
		completedCommandsCaptionPanel.setContentWidget(completedCommandsScrollPanel);
		
		VerticalPanel vp1 = new VerticalPanel();
		vp1.add(waitingCommandsCaptionPanel);
		vp1.add(completedCommandsCaptionPanel);
		
		obsCommandBufferMainPanel = new Se3TabLayoutScrollPanel(stoneEdge3);
		obsCommandBufferMainPanel.add(vp1);
	}
	public void add(String command, boolean getInfo, boolean debug, String[] debugResponse, AsyncCallback<ObsCommandReturnInfo> callback)
	{
		ObsCommand obsCommand = new ObsCommand(command, getInfo, debug, debugResponse, callback);
		commandBuffer.add(obsCommand);
	    int row = obsCommandBufferFlexTable.getRowCount();

		obsCommandBufferFlexTable.setText(row, 0, new Date().toString() + ": ");
		obsCommandBufferFlexTable.setText(row, 1, command);

	}
	public void setCommandComplete()
	{
		commandBuffer.remove(0);
		executingComand = false;
		
		oldObsCommandBufferFlexTable.insertRow(0);
		oldObsCommandBufferFlexTable.setText(0, 0, obsCommandBufferFlexTable.getText(0, 0));
		oldObsCommandBufferFlexTable.setText(0, 1, obsCommandBufferFlexTable.getText(0, 1));
		obsCommandBufferFlexTable.removeRow(0);
	    while (oldObsCommandBufferFlexTable.getRowCount() > 200)
	    {
	    	oldObsCommandBufferFlexTable.removeRow(oldObsCommandBufferFlexTable.getRowCount() - 1);
	    }
		
	}
	public void pauseBuffer() {pauseBuffer = true;}
	public void continueBuffer() {pauseBuffer = false;}
	public ScrollPanel getObsCommandBufferMainPanel() {return obsCommandBufferMainPanel;}
	private class ObsCommand
	{
		private String command;
		private boolean getInfo;
		private boolean debug;
		private String[] debugResponse;
		private AsyncCallback<ObsCommandReturnInfo> callback;
		
		private ObsCommand(String command, boolean getInfo, boolean debug, String[] debugResponse, AsyncCallback<ObsCommandReturnInfo> callback)
		{
			this.command = command;
			this.getInfo = getInfo;
			this.debug = debug;
			this.debugResponse = debugResponse;
			this.callback = callback;
		}
		private void executeCommand()
		{
			executingComand = true;
//			stoneEdge3.statusTextArea.addStatus(command);
			stoneEdge3.getStoneEdge3Service().obsCommand(command, getInfo, debug, debugResponse, callback);
		}
	}
	@Override
	public void run() 
	{
		if (!executingComand && !pauseBuffer)
		{
			if (commandBuffer.size() > 0)
			{
				commandBuffer.get(0).executeCommand();
			}
		}
		
	}
	
}
