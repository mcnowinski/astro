package com.astrofizzbizz.stoneedge3.client.obscommand;

import com.astrofizzbizz.stoneedge3.client.Se3TabLayoutScrollPanel;
import com.astrofizzbizz.stoneedge3.client.StoneEdge3;
import com.astrofizzbizz.stoneedge3.shared.ObsCommandReturnInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class ObsCommandPanel extends Se3TabLayoutScrollPanel
{
	private TextBox commandTextBox = new TextBox();
	private Button sendCommandButton;
	private Label commandEchoLabel;
	private TextArea responseTextArea = new TextArea();
	
	public ObsCommandPanel(StoneEdge3 stoneEdge3)
	{
		super(stoneEdge3);
		VerticalPanel verticalPanel = new VerticalPanel();
		add(verticalPanel);
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		verticalPanel.add(horizontalPanel);
		
		horizontalPanel.add(new Label("Command: "));
		commandTextBox = new TextBox();
		commandTextBox.setSize("59em", "1em");
		horizontalPanel.add(commandTextBox);
		
		sendCommandButton = new Button("Send");
		sendCommandButton.addClickHandler(new ObsCommandPanelClickHandler("sendCommandButton"));
		horizontalPanel.add(sendCommandButton);
		
		commandEchoLabel = new Label();
		verticalPanel.add(commandEchoLabel);
		CaptionPanel responseCaptionPanel = new CaptionPanel("Server Response");
		verticalPanel.add(responseCaptionPanel);
		responseTextArea.setSize("65em", "30em");
		responseCaptionPanel.setContentWidget(responseTextArea);
	}
	class ObsCommandPanelClickHandler implements ClickHandler
	{
		String actionString;
		ObsCommandPanelClickHandler(String actionString)
		{
			this.actionString = actionString;
		}
		public void onClick(ClickEvent event) 
		{
			if (actionString.equals("sendCommandButton"))
			{
				commandEchoLabel.setText("");
				String[] debugResponse = {"Got " + commandTextBox.getText()};
				sendCommandButton.setVisible(false);
				responseTextArea.setText("");
				getStoneEdge3().getObsCommandBuffer().add(
						commandTextBox.getText(), 
						true, 
						getStoneEdge3().isDebug(),
						debugResponse,
						new ObsCommandPanelAsyncCallback()); 
				commandTextBox.setText("");
			}
		}
	}
	class ObsCommandPanelAsyncCallback implements AsyncCallback<ObsCommandReturnInfo>
	{
		@Override
		public void onFailure(Throwable caught) 
		{
			sendCommandButton.setVisible(true);
			getStoneEdge3().getObsCommandBuffer().setCommandComplete();
			getStoneEdge3().getStatusTextArea().addStatus(caught.getMessage());
		}
		@Override
		public void onSuccess(ObsCommandReturnInfo info) 
		{
			sendCommandButton.setVisible(true);
			getStoneEdge3().getObsCommandBuffer().setCommandComplete();
			commandEchoLabel.setText("Sent Command: " + info.getCommand());
			String responseString = "";
			if (info.getResponse() != null)
			{
				for (int ii = 0; ii < info.getResponse().length; ++ii)
				{
					responseString = responseString + info.getResponse()[ii] + "\n";
				}
			}
			responseTextArea.setText(responseString);
			
		}
		
	}

}
