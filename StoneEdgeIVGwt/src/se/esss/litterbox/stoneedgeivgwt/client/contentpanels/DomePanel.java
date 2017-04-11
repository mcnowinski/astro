package se.esss.litterbox.stoneedgeivgwt.client.contentpanels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

import se.esss.litterbox.stoneedgeivgwt.client.EntryPointApp;
import se.esss.litterbox.stoneedgeivgwt.client.gskel.GskelVerticalPanel;
import se.esss.litterbox.stoneedgeivgwt.client.mqttdata.MqttData;

public class DomePanel extends GskelVerticalPanel
{
	Image domeCamImage = null;
	long oldImageCounter = -1;
	Button lampButton = new Button("Off");
	boolean lampsOn = false;
	DomeStatusPanel domeStatusPanel;
	DomeControlPanel domeControlPanel;

	public DomePanel(EntryPointApp entryPointApp) 
	{
		super(true, entryPointApp);
		domeCamImage = new Image("images/domeCamImage.jpg");
		new DomeCamMqttData(entryPointApp);
		domeControlPanel = new DomeControlPanel(entryPointApp);
		CaptionPanel domeControlCaptionPanel = new CaptionPanel("Dome Control");
		domeControlCaptionPanel.add(domeControlPanel);
		
		
		HorizontalPanel hp1 = new HorizontalPanel();
		hp1.add(domeCamImage);
		hp1.add(domeControlCaptionPanel);
		add(hp1);
		domeStatusPanel = new DomeStatusPanel(entryPointApp);
		CaptionPanel domeStatusCaptionPanel = new CaptionPanel("Dome Status");
		domeStatusCaptionPanel.add(domeStatusPanel);
		add(domeStatusCaptionPanel);
	}

	class DomeCamMqttData extends MqttData
	{
		public DomeCamMqttData(EntryPointApp entryPointApp) 
		{
			super("domeCam/image/date", MqttData.JSONDATA, 2000, entryPointApp);
		}

		@Override
		public void doSomethingWithData() 
		{
			if (getEntryPointApp().getSetup().isDebug()) return;
			try
			{
				long imageCounter = Long.parseLong(getJsonValue("counter"));
				domeCamImage.setUrl("images/domeCamImage" + Long.toString(imageCounter - 2) + ".jpg");
				Image.prefetch("images/domeCamImage" + Long.toString(imageCounter - 1) + ".jpg");
			}
			catch (Exception e)
			{
				GWT.log(e.getMessage());
			}
			
		}
	}

}
