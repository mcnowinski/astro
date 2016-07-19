package com.astrofizzbizz.stoneedge3.client.lock;

import com.astrofizzbizz.stoneedge3.client.StoneEdge3;
import com.astrofizzbizz.stoneedge3.shared.ObsCommandReturnInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LockPanel extends VerticalPanel
{
	private int panelWidth = 285;
	private int panelHeight = 130;
	StoneEdge3 stoneEdge3;
	private boolean lockOn = false;
	private String userID = "";
	private String txLockCommand = "tx lock";
	private String[] txLockDebugResponse = {"done lock user=remote email=dmcginnis427@gmail.com phone=+1-630-457-4205 comment=se3id.A8462335Z.David.McGinnis timestamp=2013-05-05T05:07:35Z"};

	private Label userNameLabel = new Label("Unknown");
	private Label emailAddressLabel = new Label("Unknown");
	private Label phoneNumberLabel = new Label("Unknown");
	private Label timeStampLabel = new Label("Unknown");
	
	public boolean isLockOn() {return lockOn;}
	public Label getUserNameLabel() {return userNameLabel;}
	public Label getEmailAddressLabel() {return emailAddressLabel;}
	public Label getPhoneNumberLabel() {return phoneNumberLabel;}
	public Label getTimeStampLabel() {return timeStampLabel;}
	public String getUserId() {return userID;}
	public int getPanelWidth() {return panelWidth;}
	public int getPanelHeight() {return panelHeight;}
	
	public void setTxLockDebugResponse(String[] txLockDebugResponse) {this.txLockDebugResponse = txLockDebugResponse;}
	public LockPanel(final StoneEdge3 stoneEdge3, int panelWidth, int panelHeight)
	{
		super();
		this.stoneEdge3 = stoneEdge3;
		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
		
		CaptionPanel lockCaptionPanel = new CaptionPanel("Lock Status");
		setSize(panelWidth + "px", panelHeight+ "px");
		lockCaptionPanel.setSize(panelWidth - 5 + "px", panelHeight - 5 + "px");
		add(lockCaptionPanel);
		
		VerticalPanel lockStatusPanel = new VerticalPanel();
		lockCaptionPanel.setContentWidget(lockStatusPanel);


		Grid lockGrid = new Grid(4, 2);
		lockStatusPanel.add(lockGrid);
		lockGrid.getColumnFormatter().setWidth(0, "70px");
		lockGrid.setWidget(0, 0, new Label("User"));
		lockGrid.setWidget(1, 0, new Label("Email"));
		lockGrid.setWidget(2, 0, new Label("Phone"));
		lockGrid.setWidget(3, 0, new Label("Time"));
		
		lockGrid.setWidget(0, 1, userNameLabel);
		lockGrid.setWidget(1, 1, emailAddressLabel);
		lockGrid.setWidget(2, 1, phoneNumberLabel);
		lockGrid.setWidget(3, 1, timeStampLabel);
		
		VerticalPanel buttonPanel = new VerticalPanel();
		buttonPanel.setSize("21em", "2em");
		buttonPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		Button changeLockButton = new Button("Change Lock");
		changeLockButton.addClickHandler(new ClickHandler() 
		{
			public void onClick(ClickEvent event) 
			{
				stoneEdge3.getLockDialog().showDialog();
			}
		});
		buttonPanel.add(changeLockButton);
		lockStatusPanel.add(buttonPanel);

	}
	public void setUnknownStatus(String text)
	{
		lockOn = false;
		userNameLabel.setText(text);
		emailAddressLabel.setText(text);
		phoneNumberLabel.setText(text);
		timeStampLabel.setText(text);
	}
	public void refreshLockStatus()
	{
		stoneEdge3.getObsCommandBuffer().add(txLockCommand, true, stoneEdge3.isDebug(), txLockDebugResponse,
				new txLockAsyncCallback());
	}
	public boolean isLockedOwnByMe() 
	{
		if (!lockOn) return false;
		if (userID.equals(stoneEdge3.getUserKey()))
		{
			return true;
		}
		return false;
	}
	class txLockAsyncCallback implements AsyncCallback<ObsCommandReturnInfo>
	{
		@Override
		public void onFailure(Throwable caught) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			setUnknownStatus("Unknown");
			stoneEdge3.getStatusTextArea().addStatus("Error: Failed to get lock Status. " + caught.getMessage());

		}
		@Override
		public void onSuccess(ObsCommandReturnInfo info) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			if (!txLockCommand.equals(info.getCommand()))
			{
				setUnknownStatus("Unknown");
				stoneEdge3.getStatusTextArea().addStatus("Error: tx lock command and callback do not match.");
				return;
			}
			String[] splitResponse = info.getResponse()[0].split(" ");
			if (splitResponse.length < 2)
			{
				setUnknownStatus("Unknown");
				stoneEdge3.getStatusTextArea().addStatus("Error: tx lock gets error response from Server");
				return;
			}
			lockOn = false;
			setUnknownStatus("");
			int index = info.getResponse()[0].indexOf("user"); 
			userID = " ";
			if (index >= 0)
			{
				setUnknownStatus("");
				lockOn = true;
				String userName = info.getResponse()[0].substring(index + 5).split(" ")[0];
				if (userName.equals("remote"))
				{
					userName = "Old StoneEdge GUI";
				}
				index = info.getResponse()[0].indexOf("email"); 
				if (index >= 0)
				{
					emailAddressLabel.setText(info.getResponse()[0].substring(index + 6).split(" ")[0]);
				}
				index = info.getResponse()[0].indexOf("phone"); 
				if (index >= 0)
				{
					phoneNumberLabel.setText(info.getResponse()[0].substring(index + 6).split(" ")[0]);
				}
				index = info.getResponse()[0].indexOf("comment"); 
				if (index >= 0)
				{
					String infoTemp = info.getResponse()[0].substring(index + 8).split(" ")[0];
					String infoString[] = infoTemp.split("\\.");
					if (infoString[0].equals("se3id"))
					{
						userID = infoString[1];
						String fname="Galileo";
						String lname = "Galilei";
						if (infoString.length > 2) fname = infoString[2];
						if (infoString.length > 3) lname = infoString[3];
						userName = fname + " " + lname;
					}
				}
				userNameLabel.setText(userName);
				index = info.getResponse()[0].indexOf("timestamp"); 
				if (index >= 0)
				{
					timeStampLabel.setText(info.getResponse()[0].substring(index + 10).split(" ")[0]);
				}
			}
			if (userID.equals(stoneEdge3.getUserKey()))
			{
				stoneEdge3.getDomeControlPanel().setVisible(true);
			}
			else
			{
				stoneEdge3.getDomeControlPanel().setVisible(false);
				stoneEdge3.getSe3TabLayoutPanel().showImagingTab(false);
			}

		}
	}

}
