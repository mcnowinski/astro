package com.astrofizzbizz.stoneedge3.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UserLogFilePanel extends Se3TabLayoutScrollPanel
{
	private TextArea textArea = new TextArea();

	public UserLogFilePanel(StoneEdge3 stoneEdge3)
	{
		super(stoneEdge3);
		VerticalPanel vp1 = new VerticalPanel();
		vp1.add(textArea);
		textArea.setSize("800px", stoneEdge3.se3TabLayoutPanelHeight() - 15 - 25 + "px");
		updateUserLogFile();
		add(vp1);
	}
	public void updateUserLogFile()
	{
		String fileLocation = "/files/logFile.txt";
		String[] debugResponse = {""};
		getStoneEdge3().getStoneEdge3Service().readFile(fileLocation, getStoneEdge3().isDebug(), debugResponse, new ReadUserLogFileAsyncCallback());
	}
	public void updateUserLogFile(String[] logEntry)
	{
		String text = "";
		for (int ii = 0; ii < logEntry.length; ++ii)
		{
			text = text + logEntry[ii] + "\n";
		}
		textArea.setText(text);
	}
	private class ReadUserLogFileAsyncCallback implements AsyncCallback<String[]>
	{
		@Override
		public void onFailure(Throwable caught) 
		{		
			getStoneEdge3().getStatusTextArea().addStatus("Error: Reading log file");
		}
		@Override
		public void onSuccess(String[] result) 
		{
			updateUserLogFile(result);
		}
		
	}

}
