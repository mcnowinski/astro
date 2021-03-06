package se.esss.litterbox.stoneedgeivgwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import se.esss.litterbox.stoneedgeivgwt.client.callbacks.CheckIpAddresslAsyncCallback;
import se.esss.litterbox.stoneedgeivgwt.client.contentpanels.DomePanel;
import se.esss.litterbox.stoneedgeivgwt.client.contentpanels.SkyStatusPanel;
import se.esss.litterbox.stoneedgeivgwt.client.gskel.GskelSetupApp;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class EntryPointApp implements EntryPoint 
{
	private GskelSetupApp setup;

	// Getters
	public GskelSetupApp getSetup() {return setup;}
	
	public void onModuleLoad() 
	{
		setup = new GskelSetupApp(this, true);
		setup.setDebug(true);
		getSetup().setLogoImage("images/gwtLogo.jpg");
		
		Label titleLabel = new Label("StoneEdge IV v1.5");
		titleLabel.setStyleName("titleLabel");
		
		getSetup().getTitlePanel().add(titleLabel);
		getSetup().getEntryPointAppService().checkIpAddress(new CheckIpAddresslAsyncCallback(this));		
		getSetup().resize();
		RootLayoutPanel.get().setWidth("980px");
		RootLayoutPanel.get().setHeight("665px");
	}
	public void initializeTabs()
	{
		DomePanel dsp = new DomePanel(this);
		SkyStatusPanel ssp = new SkyStatusPanel(this);
		getSetup().addPanel(dsp,"Dome Status");
		getSetup().addPanel(ssp,"Sky Status");
	}
}
