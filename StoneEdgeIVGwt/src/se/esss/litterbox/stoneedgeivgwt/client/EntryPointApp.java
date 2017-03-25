package se.esss.litterbox.stoneedgeivgwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Label;

import se.esss.litterbox.stoneedgeivgwt.client.callbacks.CheckIpAddresslAsyncCallback;
import se.esss.litterbox.stoneedgeivgwt.client.contentpanels.GaugeShowCasePanel;
import se.esss.litterbox.stoneedgeivgwt.client.contentpanels.LineChartShowCasePanel;
import se.esss.litterbox.stoneedgeivgwt.client.contentpanels.ScatterChartShowCasePanel;
import se.esss.litterbox.stoneedgeivgwt.client.contentpanels.TestPicPanel;
import se.esss.litterbox.stoneedgeivgwt.client.gskel.GskelLoadWaiter;
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
		getSetup().setLogoImage("images/gwtLogo.jpg");
		
		Label titleLabel = new Label("GWT Skeleton");
		titleLabel.setStyleName("titleLabel");
		
		getSetup().getTitlePanel().add(titleLabel);
		getSetup().getEntryPointAppService().checkIpAddress(new CheckIpAddresslAsyncCallback(this));		
		getSetup().resize();
	}
	public void initializeTabs()
	{
		TestPicPanel tpp1 = new TestPicPanel(false, this, "images/gwtLogo.jpg");
		getSetup().addPanel(tpp1,"Shiftr");
		new TabLoadWaiter(100, 1);
	}
	private void loadTab2()
	{
		getSetup().addPanel(new GaugeShowCasePanel(this), "Gauge");
		new TabLoadWaiter(100, 2);
	}
	private void loadTab3()
	{
		getSetup().addPanel(new LineChartShowCasePanel(this), "Line");
		new TabLoadWaiter(100, 3);
	}
	private void loadTab4()
	{
		getSetup().addPanel(new ScatterChartShowCasePanel(this), "Scatter");
	}
	class TabLoadWaiter extends GskelLoadWaiter
	{
		public TabLoadWaiter(int loopTimeMillis, int itask) {super(loopTimeMillis, itask);}
		@Override
		public boolean isLoaded() 
		{
			return true;
		}
		@Override
		public void taskAfterLoad() 
		{
			GWT.log(Integer.toString(getItask()));
			if (getItask() == 1) loadTab2();
			if (getItask() == 2) loadTab3();
			if (getItask() == 3) loadTab4();
		}
	}
}
