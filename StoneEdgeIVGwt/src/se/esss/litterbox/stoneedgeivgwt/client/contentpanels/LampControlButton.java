package se.esss.litterbox.stoneedgeivgwt.client.contentpanels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;

import se.esss.litterbox.stoneedgeivgwt.client.EntryPointApp;
import se.esss.litterbox.stoneedgeivgwt.client.mqttdata.MqttData;

public class LampControlButton extends Button
{
	EntryPointApp entryPointApp;
	
	boolean lampsOn = false;
	
	public LampControlButton(EntryPointApp entryPointApp)
	{
		super("Lamps Off");
		this.entryPointApp = entryPointApp;

		setWidth("100%");
		addClickHandler(new LampButtonClickHandler(this));
		new LampMqttData(this);
	}
	public void enableSettingsInput(boolean enabled)
	{
		setEnabled(enabled);
		if (!enabled)
		{
			if (lampsOn)
			{
				setStyleName("lampButtonOnNotEnabled");
			}
			else
			{
				setStyleName("lampButtonOffNotEnabled");
			}
		}
		else
		{
			if (lampsOn)
			{
				setStyleName("lampButtonOnEnabled");
			}
			else
			{
				setStyleName("lampButtonOffEnabled");
			}
		}
		
	}
	public void doSettings() 
	{
		String[][] jsonArray = {{"all","on"},{"debug", "false"}};
		if (getText().equals("Lamps Off"))
		{
			jsonArray[0][1] = "off";
		}
		else
		{
			jsonArray[0][1] = "on";
		}
		if (entryPointApp.getSetup().isDebug()) jsonArray[1][1] = "true";;
		entryPointApp.getSetup().getMqttService().publishJsonArray("tel/set/tx/lamps", jsonArray, entryPointApp.getSetup().isSettingsPermitted(), new LampSettingsCallback());
	}
	static class LampButtonClickHandler implements ClickHandler
	{
		LampControlButton lampControlButton;
		LampButtonClickHandler(LampControlButton lampControlButton)
		{
			this.lampControlButton = lampControlButton;
		}
		@Override
		public void onClick(ClickEvent event) 
		{
			if (lampControlButton.getText().equals("Lamps Off"))
			{
				lampControlButton.setText("Lamps On");
				lampControlButton.setStyleName("lampButtonOnEnabled");
			}
			else
			{
				lampControlButton.setText("Lamps Off");
				lampControlButton.setStyleName("lampButtonOffEnabled");
			}
		}
	}
	static class LampMqttData extends MqttData
	{
		LampControlButton lampControlButton;
		public LampMqttData(LampControlButton lampControlButton) 
		{
			super("tel/done/tx/lamps", MqttData.JSONDATA, 1000, lampControlButton.entryPointApp);
			this.lampControlButton = lampControlButton;
		}
		@Override
		public void doSomethingWithData() 
		{
			try 
			{
				lampControlButton.lampsOn = false;
				if (getJsonValue("one").equals("on")) lampControlButton.lampsOn = true;
				if (!lampControlButton.isEnabled())
				{
					if (lampControlButton.lampsOn)
					{
						lampControlButton.setStyleName("lampButtonOnNotEnabled");
						lampControlButton.setText("Lamps On");
					}
					else
					{
						lampControlButton.setStyleName("lampButtonOffNotEnabled");
						lampControlButton.setText("Lamps Off");
					}
				}
				
			} catch (Exception e) {GWT.log(e.getMessage());}
		}
	}
	class LampSettingsCallback implements AsyncCallback<String>
	{
		@Override
		public void onFailure(Throwable caught) {}
		@Override
		public void onSuccess(String result) {}
	}
}
