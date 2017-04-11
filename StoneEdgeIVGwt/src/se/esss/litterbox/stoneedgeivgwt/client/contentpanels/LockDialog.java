package se.esss.litterbox.stoneedgeivgwt.client.contentpanels;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import se.esss.litterbox.stoneedgeivgwt.client.EntryPointApp;
import se.esss.litterbox.stoneedgeivgwt.client.gskel.GskelOptionDialog.GskelOptionDialogInterface;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;

public class LockDialog  extends DialogBox implements GskelOptionDialogInterface
{
	EntryPointApp entryPointApp;
	
	TextBox userNameTextBox = new TextBox();
	TextBox emailAddressTextBox = new TextBox();
	TextBox phoneNumberTextBox = new TextBox();
	
	private Button lockButton = new Button("Lock");
	private Button unlockButton = new Button("Unlock");
	private Button cancelButton = new Button("Cancel");
	
	private boolean isShowing = false;

	private String[] backDoor1 = {"d", "dmcginnis427", "dmcginnis427@gmail.com", "+1-630-457-4205"};
	private String[] backDoor2 = {"v", "viv", "vhoette@yerkes.uchicago.edu", "+1-262-215-1599"};
	
	public boolean isShowing() {return isShowing;}

	public LockDialog(EntryPointApp entryPointApp) 
	{
		super();
		this.entryPointApp = entryPointApp;
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
		phoneNumberTextBox.setText(phoneNumberTextBox.getText().replaceAll(" ", ""));
		emailAddressTextBox.setText(emailAddressTextBox.getText().replaceAll(" ", ""));
		if (userNameTextBox.getText().equals(backDoor1[0]))
		{
			userNameTextBox.setText(backDoor1[1]);
			emailAddressTextBox.setText(backDoor1[2]);
			phoneNumberTextBox.setText(backDoor1[3]);
		}
		if (userNameTextBox.getText().equals(backDoor2[0]))
		{
			userNameTextBox.setText(backDoor2[1]);
			emailAddressTextBox.setText(backDoor2[2]);
			phoneNumberTextBox.setText(backDoor2[3]);
		}
		boolean userNameEntryOk = false;
		String[] userName = userNameTextBox.getText().split(" ");
		if (userName.length > 0)
		{
			userNameEntryOk = true;
		}
		else
		{
			entryPointApp.getSetup().getMessageDialog().setMessage("Error", "Enter a user name please.", true);
		}
		boolean phoneEntryOk = false;
		if (phoneNumberTextBox.getText().length() > 0)
		{
			phoneEntryOk = true;
		}
		else
		{
			entryPointApp.getSetup().getMessageDialog().setMessage("Error", "Enter a phone number please.", true);
		}
		boolean emailEntryOk = false;
		if (emailAddressTextBox.getText().length() > 0) 
		{
			if (emailAddressTextBox.getText().indexOf("@") > 0)
			{
				emailEntryOk = true;
			}
			else
			{
				entryPointApp.getSetup().getMessageDialog().setMessage("Error", "Bad Email Address", true);
			}
		}
		else
		{
			entryPointApp.getSetup().getMessageDialog().setMessage("Error", "Enter an email address please.", true);
		}
		
		if (userNameEntryOk && phoneEntryOk && emailEntryOk)
		{
			setLock();
			hide();
		}

	}
	private void unlock()
	{
		clearLock();
		hide();
 	}
	public void clearLock()
	{
		hide();
	}
	private void setLock()
	{
		hide();
 	}
	public void showDialog()
	{
		center();
        show();
        isShowing = true;
	}
	@Override
	public void hide()
	{
		super.hide();
	    isShowing = false;
	}
	@Override
	public void optionDialogInterfaceAction(String choiceButtonText) 
	{
		if (choiceButtonText.equals("Cancel"))
		{
		}
		if (choiceButtonText.equals("Break Lock"))
		{
			clearLock();
		}
	}

}
