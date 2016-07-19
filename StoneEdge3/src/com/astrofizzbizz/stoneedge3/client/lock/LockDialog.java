package com.astrofizzbizz.stoneedge3.client.lock;

import java.util.Date;

import com.astrofizzbizz.stoneedge3.client.StoneEdge3;
import com.astrofizzbizz.stoneedge3.client.OptionDialog.OptionDialogInterface;
import com.astrofizzbizz.stoneedge3.client.Utilities;
import com.astrofizzbizz.stoneedge3.shared.ObsCommandReturnInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

public class LockDialog  extends DialogBox implements OptionDialogInterface
{
	StoneEdge3 stoneEdge3;
	private TextBox userNameTextBox = new TextBox();
	private TextBox emailAddressTextBox = new TextBox();
	private TextBox phoneNumberTextBox = new TextBox();
	
	private String userNameEntry = null;
	private String phoneEntry = null;
	private String emailEntry = null;

	
	private Button lockButton = new Button("Lock");
	private Button unlockButton = new Button("Unlock");
	private Button cancelButton = new Button("Cancel");

	private String[] backDoor1 = {"d", "Dave McGinnis", "dmcginnis427@gmail.com", "+1-630-457-4205"};
	private String[] backDoor2 = {"v", "Vivian Hoette", "vhoette@yerkes.uchicago.edu", "+1-262-215-1599"};
	
	private String txClearLockCommand = "tx lock clear";
	private String[] txClearLockDebugResponse = {"done lock "};
	
	private String txSetLockCommand = "tx lock";
	private String[] txSetLockDebugResponse = {"done lock "};

	private String tinInterruptCommand = "tin interrupt";
	private String[] tinInterruptDebugResponse = {"done telco daemon=0 status=sleeping reqid=0 wake=1970-01-01T00:00:00Z name= nfail=0"};

	private String tinResumeCommand = "tin resume";
//	private String[] tinResumeDebugResponse = {"done telco daemon=1 status=sleeping reqid=1 wake=2013-11-19T14:00:00Z name=Darks nfail=0"};

	private String tinCommand = "tin";
//	private String[] tinDebugResponse = {"done telco daemon=1 status=sleeping reqid=1 wake=2013-11-19T14:00:00Z name=Darks nfail=0"};
	
	public LockDialog(StoneEdge3 stoneEdge3) 
	{
		super();
		this.stoneEdge3 = stoneEdge3;
		setText("Lock");
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		add(dialogVPanel);
		
		Grid lockGrid = new Grid(3, 2);
		dialogVPanel.add(lockGrid);
		lockGrid.setWidget(0, 0, new Label("User"));
		lockGrid.setWidget(1, 0, new Label("Email"));
		lockGrid.setWidget(2, 0, new Label("Phone"));

		lockGrid.setWidget(0, 1, userNameTextBox);
		lockGrid.setWidget(1, 1, emailAddressTextBox);
		lockGrid.setWidget(2, 1, phoneNumberTextBox);
		dialogVPanel.add(buttonPanel());
	}
	HorizontalPanel buttonPanel()
	{
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		buttonPanel.add(lockButton);
		buttonPanel.add(unlockButton);
		buttonPanel.add(cancelButton);
		lockButton.addClickHandler(new ClickHandler() {public void onClick(ClickEvent event) {lock();}});
		unlockButton.addClickHandler(new ClickHandler() {public void onClick(ClickEvent event) {unlock();}});
		cancelButton.addClickHandler(new ClickHandler() {public void onClick(ClickEvent event) {hide();}});
		
		return buttonPanel;
	}
	private void lock()
	{
		userNameEntry = userNameTextBox.getText();
		phoneEntry = phoneNumberTextBox.getText();
		phoneEntry = Utilities.stripWhiteSpaces(phoneEntry);
		emailEntry = emailAddressTextBox.getText();
		emailEntry = Utilities.stripWhiteSpaces(emailEntry);
		if (userNameEntry.equals(backDoor1[0]))
		{
			userNameEntry = backDoor1[1];
			emailEntry = backDoor1[2];
			phoneEntry = backDoor1[3];
		}
		if (userNameEntry.equals(backDoor2[0]))
		{
			userNameEntry = backDoor2[1];
			emailEntry = backDoor2[2];
			phoneEntry = backDoor2[3];
		}
		boolean userNameEntryOk = false;
		String[] userName = userNameEntry.split(" ");
		if (userName.length > 0)
		{
			if (userName.length == 2)
			{
				userNameEntryOk = true;
			}
			else
			{
				if (userName.length < 2)
					stoneEdge3.getMessageDialog().setMessage("Error", "Need a first and last name.", true);
				if (userName.length > 2)
					stoneEdge3.getMessageDialog().setMessage("Error", "Need ONLY a first and last name.", true);
			}
		}
		else
		{
			stoneEdge3.getMessageDialog().setMessage("Error", "Enter a user name please.", true);
		}
		boolean phoneEntryOk = false;
		if (phoneEntry.replaceAll(" ", "").length() > 0)
		{
			phoneEntryOk = true;
		}
		else
		{
			stoneEdge3.getMessageDialog().setMessage("Error", "Enter a phone number please.", true);
		}
		boolean emailEntryOk = false;
		if (emailEntry.replaceAll(" ", "").length() > 0) 
		{
			if (emailEntry.indexOf("@") > 0)
			{
				emailEntryOk = true;
			}
			else
			{
				stoneEdge3.getMessageDialog().setMessage("Error", "Bad Email Address", true);
			}
		}
		else
		{
			stoneEdge3.getMessageDialog().setMessage("Error", "Enter an email address please.", true);
		}
		
		if (userNameEntryOk && phoneEntryOk && emailEntryOk)
		{
			setLock();
			hide();
		}

	}
	private void unlock()
	{
		if (stoneEdge3.getLockPanel().isLockedOwnByMe())
		{
			if (stoneEdge3.getTelescopeStatusPanel().isSlitOpen())
			{
			    String message = "The Dome is still open!";
			    message = message + "\nDo you really want to clear the lock?";
				stoneEdge3.getOptionDialog().setOption("Dome Open! Clear Lock?", message, "Break Lock", "Cancel", this);
			}
			else
			{
				clearLock();
				hide();
			}
		}
		else
		{
		    String message = "Lock is held by " + userNameTextBox.getText();
		    message = message + "\nEmail: " + emailAddressTextBox.getText();
		    message = message + "\nPhone: " + phoneNumberTextBox.getText();
		    message = message + "\nPlease contact before breaking.";
			stoneEdge3.getOptionDialog().setOption("Break Lock?", message, "Break Lock", "Cancel", this);
		}
		
	}
	public void clearLock()
	{
		stoneEdge3.getStatusTextArea().addStatus("Clearing Lock...");
		stoneEdge3.getObsCommandBuffer().add(txClearLockCommand, true, stoneEdge3.isDebug(), txClearLockDebugResponse,
				new txClearLockAsyncCallback());

	}
	private void setLock()
	{
		stoneEdge3.getStatusTextArea().addStatus("Setting Lock...");
		String comment = "se3id." + stoneEdge3.getUserKey() + "." + userNameEntry.split(" ")[0] + "." + userNameEntry.split(" ")[1];
		txSetLockCommand = "tx lock user=remote" + " email=" + emailEntry + " phone=" + phoneEntry + " comment=" + comment;
		txSetLockDebugResponse[0] = "done lock user=remote" + " email=" + emailEntry + " phone=" + phoneEntry + " comment=" + comment + " timestamp=Sometime";
		stoneEdge3.getObsCommandBuffer().add(txSetLockCommand, true, stoneEdge3.isDebug(), txSetLockDebugResponse,
				new txSetLockAsyncCallback());
	}
	public void showDialog()
	{
		if (stoneEdge3.getLockPanel().isLockOn())
		{
			lockButton.setVisible(false);
			unlockButton.setVisible(true);
			
			userNameTextBox.setText(stoneEdge3.getLockPanel().getUserNameLabel().getText());
			emailAddressTextBox.setText(stoneEdge3.getLockPanel().getEmailAddressLabel().getText());
			phoneNumberTextBox.setText(stoneEdge3.getLockPanel().getPhoneNumberLabel().getText());
			
			userNameTextBox.setEnabled(false);
			emailAddressTextBox.setEnabled(false);
			phoneNumberTextBox.setEnabled(false);
		}
		else
		{
			lockButton.setVisible(true);
			unlockButton.setVisible(false);

			userNameTextBox.setText(stoneEdge3.getLockPanel().getUserNameLabel().getText());
			emailAddressTextBox.setText(stoneEdge3.getLockPanel().getEmailAddressLabel().getText());
			phoneNumberTextBox.setText(stoneEdge3.getLockPanel().getPhoneNumberLabel().getText());
			
			userNameTextBox.setEnabled(true);
			emailAddressTextBox.setEnabled(true);
			phoneNumberTextBox.setEnabled(true);

		}
		center();
        show();
	}
	public void stopDicksDaemon(boolean stop)
	{
		if (stop)
		{
			stoneEdge3.getStatusTextArea().addStatus("Stopping Daemon");
			stoneEdge3.getObsCommandBuffer().add(tinInterruptCommand, true, stoneEdge3.isDebug(), tinInterruptDebugResponse,
					new tinAsyncCallback());
			return;
		}
		else
		{
//TODO Fix this when Dick fixes Daemon
//			stoneEdge3.getStatusTextArea().addStatus("Starting Daemon");
//			stoneEdge3.getObsCommandBuffer().add(tinResumeCommand, true, stoneEdge3.isDebug(), tinResumeDebugResponse,
//					new tinAsyncCallback());
			return;
		}
		
	}
	class txClearLockAsyncCallback implements AsyncCallback<ObsCommandReturnInfo>
	{
		@Override
		public void onFailure(Throwable caught) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			stoneEdge3.getStatusTextArea().addStatus("Error: Failed to clear Lock.");
		}
		@Override
		public void onSuccess(ObsCommandReturnInfo info) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();

			if (!txClearLockCommand.equals(info.getCommand()))
			{
				stoneEdge3.getStatusTextArea().addStatus("Error: tx lock command and callback do not match.");
				return;
			}
			String[] splitResponse = info.getResponse()[0].split(" ");
			if (splitResponse.length < 2)
			{
				stoneEdge3.getStatusTextArea().addStatus("Error: tx clear lock gets error response from Server");
				return;
			}
			if (splitResponse.length > 2)
			{
				stoneEdge3.getStatusTextArea().addStatus("Error: tx clear lock did not clear.");
				stoneEdge3.getLockPanel().setTxLockDebugResponse(txClearLockDebugResponse);
				return;
			}
			if (splitResponse.length == 2)
			{
				stopDicksDaemon(false);
				stoneEdge3.getLockPanel().setTxLockDebugResponse(txClearLockDebugResponse);
				stoneEdge3.getStatusTextArea().addStatus("Lock cleared");
				stoneEdge3.getLockPanel().refreshLockStatus();
				stoneEdge3.getTelescopeStatusPanel().setUnknownStatus();
				// Show imaging panel
				stoneEdge3.getSe3TabLayoutPanel().showImagingTab(false);
				stoneEdge3.getSe3TabLayoutPanel().selectDomeTab();
				return;
			}
		}
	}
	class txSetLockAsyncCallback implements AsyncCallback<ObsCommandReturnInfo>
	{
		@Override
		public void onFailure(Throwable caught) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			stoneEdge3.getStatusTextArea().addStatus("Error: Failed to set Lock.");

		}
		@Override
		public void onSuccess(ObsCommandReturnInfo info) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			if (!txSetLockCommand.equals(info.getCommand()))
			{
				stoneEdge3.getStatusTextArea().addStatus("Error: tx lock command and callback do not match.");
				return;
			}
			String[] splitResponse = info.getResponse()[0].split(" ");
			if (splitResponse.length < 2)
			{
				stoneEdge3.getStatusTextArea().addStatus("Error: tx set lock gets error response from Server");
				return;
			}
			if (splitResponse.length == 2)
			{
				stoneEdge3.getStatusTextArea().addStatus("Error: tx set lock did not set.");
				stoneEdge3.getLockPanel().setTxLockDebugResponse(txClearLockDebugResponse);
				return;
			}
			if (splitResponse.length > 2)
			{
				stopDicksDaemon(true);
				stoneEdge3.getLockPanel().setTxLockDebugResponse(txSetLockDebugResponse);
				stoneEdge3.getStatusTextArea().addStatus("Lock set");
				stoneEdge3.getLockPanel().refreshLockStatus();
				boolean overRideLock = true;
				stoneEdge3.getTelescopeStatusPanel().refreshTelescopeStatus(overRideLock);
// allow User to see hidden panels and tabs
				stoneEdge3.getSe3TabLayoutPanel().showImagingTab(true);
//				stoneEdge3.getSe3TabLayoutPanel().getTargetingPanel().getFindAutoFocusStarPanel().setVisible(true);

				String fileLocation = "/files/logFile.txt";
				String[] debugResponse = {""};
				stoneEdge3.getStoneEdge3Service().readFile(fileLocation, stoneEdge3.isDebug(), debugResponse, new ReadLogFileAsyncCallback());
				return;
			}
		}
	}
	private class ReadLogFileAsyncCallback implements AsyncCallback<String[]>
	{
		@Override
		public void onFailure(Throwable caught) 
		{		
			stoneEdge3.getStatusTextArea().addStatus("Error: Reading log file");
		}
		@Override
		public void onSuccess(String[] result) 
		{
			stoneEdge3.getStatusTextArea().addStatus("Success Reading log file");
			String[] updateFile = new String[result.length + 1];
			updateFile[0] = new Date().toString() + "\t" + emailEntry;
			for (int ii = 0; ii < result.length; ++ii)
			{
				updateFile[ii + 1] = result[ii];
			}			
			String fileLocation = "/files/logFile.txt";
			stoneEdge3.getStoneEdge3Service().writeFile(fileLocation, updateFile, stoneEdge3.isDebug(), new WriteLogFileAsyncCallback());
			stoneEdge3.getSe3TabLayoutPanel().getUserLogFilePanel().updateUserLogFile(updateFile);
		}
		
	}
	private class WriteLogFileAsyncCallback implements AsyncCallback<String[]>
	{
		@Override
		public void onFailure(Throwable caught) 
		{
			stoneEdge3.getStatusTextArea().addStatus("Error: Writing log file");
		}

		@Override
		public void onSuccess(String[] result) 
		{
			stoneEdge3.getStatusTextArea().addStatus("Success: Writing log file");
		}

	}

	class tinAsyncCallback implements AsyncCallback<ObsCommandReturnInfo>
	{
		@Override
		public void onFailure(Throwable caught) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			stoneEdge3.getStatusTextArea().addStatus("Error: Failed to set daemon.");
		}
		@Override
		public void onSuccess(ObsCommandReturnInfo info) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			int iindex = info.getResponse()[0].indexOf("done telco daemon");
			if (tinInterruptCommand.equals(info.getCommand()))
			{
				if (iindex >= 0)
				{
					stoneEdge3.getStatusTextArea().addStatus("Daemon successfully interrupted.");
				}
				else
				{
					stoneEdge3.getStatusTextArea().addStatus("Error: Fail to stop Daemon.");
				}
				return;
			}
			if (tinResumeCommand.equals(info.getCommand()))
			{
				if (iindex >= 0)
				{
					stoneEdge3.getStatusTextArea().addStatus("Daemon successfully resumed.");
				}
				else
				{
					stoneEdge3.getStatusTextArea().addStatus("Error: Fail to resume Daemon.");
				}
				return;
			}
			if (tinCommand.equals(info.getCommand()))
			{
				if (iindex >= 0)
				{
					stoneEdge3.getStatusTextArea().addStatus("Daemon successfully resumed.");
				}
				else
				{
					stoneEdge3.getStatusTextArea().addStatus(info.getResponse()[0]);
				}
				return;
			}

		}
	}
	@Override
	public void optionDialogChoice(String choiceButtonText) 
	{
		if (choiceButtonText.equals("Cancel"))
		{
			hide();
		}
		if (choiceButtonText.equals("Break Lock"))
		{
			clearLock();
			hide();
		}
		
	}

}
