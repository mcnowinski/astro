package se.esss.litterbox.stoneedgeivgwt.client.contentpanels;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;

import se.esss.litterbox.stoneedgeivgwt.client.EntryPointApp;
import se.esss.litterbox.stoneedgeivgwt.client.mqttdata.MqttData;

public class LockControlButton extends Button
{
	EntryPointApp entryPointApp;
//done lock user=remote email=dmcginnis427@gmail.com phone=+1-630-457-4205 comment=se3id.A3665117Z.Dave.McGinnis timestamp=2017-03-29T21:30:02Z	
	boolean lockOn = false;
	LockDialog lockDialog;
	
	public LockControlButton(EntryPointApp entryPointApp)
	{
		super("Lock Off");
		this.entryPointApp = entryPointApp;

		setWidth("100%");
		addClickHandler(new LockButtonClickHandler(this));
		new LockMqttData(this);
		lockDialog = new LockDialog(entryPointApp);
	}
	public void enableSettingsInput(boolean enabled)
	{
		setEnabled(enabled);
		if (!enabled)
		{
			if (lockOn)
			{
				setStyleName("lockButtonOnNotEnabled");
			}
			else
			{
				setStyleName("lockButtonOffNotEnabled");
			}
		}
		else
		{
			if (lockOn)
			{
				setStyleName("lockButtonOnEnabled");
			}
			else
			{
				setStyleName("lockButtonOffEnabled");
			}
		}
		
	}
	public void doSettings() 
	{
	}
	static class LockButtonClickHandler implements ClickHandler
	{
		LockControlButton lockControlButton;
		LockButtonClickHandler(LockControlButton lockControlButton)
		{
			this.lockControlButton = lockControlButton;
		}
		@Override
		public void onClick(ClickEvent event) 
		{
			lockControlButton.lockDialog.showDialog();
		}
	}
	static class LockMqttData extends MqttData
	{
		LockControlButton lockControlButton;
		public LockMqttData(LockControlButton lockControlButton) 
		{
			super("tel/done/tx/lock", MqttData.JSONDATA, 1000, lockControlButton.entryPointApp);
			this.lockControlButton = lockControlButton;
		}
		@Override
		public void doSomethingWithData() 
		{
			if (lockControlButton.lockDialog.isShowing()) return;
			lockControlButton.lockOn = false;
			lockControlButton.lockDialog.userNameTextBox.setText("");
			lockControlButton.lockDialog.emailAddressTextBox.setText("");
			lockControlButton.lockDialog.phoneNumberTextBox.setText("");

			try 
			{
				String user = getJsonValue("user");
				if (user.length() > 0)
				{
					lockControlButton.lockDialog.userNameTextBox.setText(user);
					lockControlButton.lockOn = true;
				}
			} catch (Exception e) {lockControlButton.lockOn = false;}
			
			try {lockControlButton.lockDialog.emailAddressTextBox.setText(getJsonValue("email"));} catch (Exception e) {}
			try {lockControlButton.lockDialog.phoneNumberTextBox.setText(getJsonValue("phone"));} catch (Exception e) {}
			if (!lockControlButton.isEnabled())
			{
				if (lockControlButton.lockOn)
				{
					lockControlButton.setStyleName("lockButtonOnNotEnabled");
					lockControlButton.setText("Lock On");
				}
				else
				{
					lockControlButton.setStyleName("lockButtonOffNotEnabled");
					lockControlButton.setText("Lock Off");
				}
			}
		}
	}
	class LockSettingCallback implements AsyncCallback<String>
	{
		@Override
		public void onFailure(Throwable caught) {}
		@Override
		public void onSuccess(String result) {}
	}
}
