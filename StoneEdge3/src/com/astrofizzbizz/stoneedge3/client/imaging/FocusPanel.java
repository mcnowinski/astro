package com.astrofizzbizz.stoneedge3.client.imaging;

import com.astrofizzbizz.stoneedge3.client.StoneEdge3;
import com.astrofizzbizz.stoneedge3.client.Utilities;
import com.astrofizzbizz.stoneedge3.shared.ObsCommandReturnInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;

public class FocusPanel extends CaptionPanel
{
	private StoneEdge3 stoneEdge3;
	private TextBox focusTextBox = new TextBox();
	private Button updateButton = new Button("Update");
	
	String focusUpdateCommand = "tx focus";
	String[] focusUpdateDebugResponse = {"done focus pos=5900"};
	String setFocusCommand = "tx focus";

	
	public FocusPanel(StoneEdge3 stoneEdge3)
	{
		super("Focus");
		this.stoneEdge3 = stoneEdge3;
		HorizontalPanel hp1 = new HorizontalPanel();
		hp1.add(focusTextBox);
		hp1.add(updateButton);
		
		add(hp1);
		focusTextBox.addClickHandler(new ShowUpdateButtonClickHandler());
		updateButton.addClickHandler(new UpdateButtonClickHandler());
		updateButton.setVisible(false);
		
	}
	public void focusStatus()
	{
		stoneEdge3.getObsCommandBuffer().add(focusUpdateCommand, true, stoneEdge3.isDebug(),  focusUpdateDebugResponse,
				new FocusStatusAsyncCallback());
		
	}
	public void setFocus()
	{
		String focusString = Utilities.stripWhiteSpaces(focusTextBox.getValue());
		if (!Utilities.isIntNumber(focusString)) 
		{
			stoneEdge3.getStatusTextArea().addStatus("Error: Focus setting is not an integer");
			return;
		}
		stoneEdge3.getStatusTextArea().addStatus("Setting focus to " + focusString + " ...");
		focusTextBox.setValue("");
		focusTextBox.setEnabled(false);
		setFocusCommand = "tx focus pos=" + focusString;
		focusUpdateDebugResponse[0] = "done focus pos=" + focusString;
		stoneEdge3.getObsCommandBuffer().add(setFocusCommand, true, stoneEdge3.isDebug(),  focusUpdateDebugResponse,
				new SetFocusAsyncCallback());
		
	}

	private class ShowUpdateButtonClickHandler implements ClickHandler
	{
		@Override
		public void onClick(ClickEvent event) {updateButton.setVisible(true);}
	}
	private class UpdateButtonClickHandler implements ClickHandler
	{
		@Override
		public void onClick(ClickEvent event) 
		{
			updateButton.setVisible(false);
			setFocus();
		}
	}
	class FocusStatusAsyncCallback implements AsyncCallback<ObsCommandReturnInfo>
	{
		@Override
		public void onFailure(Throwable caught) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			stoneEdge3.getStatusTextArea().addStatus("Error: Failed to read focus position.");
		}
		@Override
		public void onSuccess(ObsCommandReturnInfo info) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			if (!focusUpdateCommand.equals(info.getCommand()))
			{
				stoneEdge3.getStatusTextArea().addStatus("Error: tx focus read command and callback do not match.");
				return;
			}
			if (info.getResponse()[0].indexOf("done focus pos") < 0)
			{
				stoneEdge3.getStatusTextArea().addStatus("Error: in tx focus read.");
				return;
			}
			String filterPosString = info.getResponse()[0].substring(info.getResponse()[0].indexOf("=") + 1);
			stoneEdge3.getStatusTextArea().addStatus("Focus at " + filterPosString);
			focusTextBox.setValue(filterPosString);
		}
	}
	class SetFocusAsyncCallback implements AsyncCallback<ObsCommandReturnInfo>
	{
		@Override
		public void onFailure(Throwable caught) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			stoneEdge3.getStatusTextArea().addStatus("Error: Failed to set focus position.");
			focusTextBox.setEnabled(true);
		}
		@Override
		public void onSuccess(ObsCommandReturnInfo info) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			focusTextBox.setEnabled(true);
			if (!setFocusCommand.equals(info.getCommand()))
			{
				stoneEdge3.getStatusTextArea().addStatus("Error: tx focus set command and callback do not match.");
				return;
			}
			if (info.getResponse()[0].indexOf("done focus pos") < 0)
			{
				stoneEdge3.getStatusTextArea().addStatus("Error: in tx focus set.");
				return;
			}
			String filterPosString = info.getResponse()[0].substring(info.getResponse()[0].indexOf("=") + 1);
			stoneEdge3.getStatusTextArea().addStatus("Focus at " + filterPosString);
			focusTextBox.setValue(filterPosString);
		}
	}

}
