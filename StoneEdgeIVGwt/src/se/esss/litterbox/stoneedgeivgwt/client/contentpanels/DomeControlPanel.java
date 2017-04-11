package se.esss.litterbox.stoneedgeivgwt.client.contentpanels;

import com.google.gwt.user.client.ui.VerticalPanel;

import se.esss.litterbox.stoneedgeivgwt.client.EntryPointApp;
import se.esss.litterbox.stoneedgeivgwt.client.gskel.GskelSettingButtonGrid;

public class DomeControlPanel  extends VerticalPanel
{
	EntryPointApp entryPointApp;
	LampControlButton lampControlButton;
	LockControlButton lockControlButton;

	public DomeControlPanel(EntryPointApp entryPointApp) 
	{
		this.entryPointApp = entryPointApp;
		lockControlButton = new LockControlButton(entryPointApp);
		lampControlButton = new LampControlButton(entryPointApp);
		ControlEnablePanel cep = new ControlEnablePanel(entryPointApp);
		add(cep);
		add(lockControlButton);
		add(lampControlButton);
	}
	class ControlEnablePanel extends GskelSettingButtonGrid
	{
		ControlEnablePanel(EntryPointApp entryPointApp) 
		{
			super(entryPointApp.getSetup().isSettingsPermitted());
		}
		@Override
		public void enableSettingsInput(boolean enabled) 
		{
			lampControlButton.enableSettingsInput(enabled);
			lockControlButton.enableSettingsInput(enabled);
		}
		@Override
		public void doSettings() 
		{
			lampControlButton.doSettings();
			lockControlButton.doSettings();
		}
	}
}
