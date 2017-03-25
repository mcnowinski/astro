package se.esss.litterbox.stoneedgeivgwt.client.contentpanels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Grid;

import se.esss.litterbox.stoneedgeivgwt.client.EntryPointApp;
import se.esss.litterbox.stoneedgeivgwt.client.googleplots.GaugeCaptionPanel;
import se.esss.litterbox.stoneedgeivgwt.client.gskel.GskelLoadWaiter;
import se.esss.litterbox.stoneedgeivgwt.client.gskel.GskelVerticalPanel;
import se.esss.litterbox.stoneedgeivgwt.client.mqttdata.MqttData;

public class GaugeShowCasePanel extends GskelVerticalPanel
{
	Grid gaugeGrid;
	GaugeCaptionPanel gaugeCaptionPanel1;
	GaugeCaptionPanel gaugeCaptionPanel2;
	GaugeCaptionPanel gaugeCaptionPanel3;
	GaugeMqttData gaugeMqttData;


	public GaugeShowCasePanel(EntryPointApp entryPointApp) 
	{
		super(true, entryPointApp);
		gaugeGrid = new Grid(1, 3);
		add(gaugeGrid);
		addGauge1();
	}
	private void addGauge1()
	{
		gaugeCaptionPanel1 = new GaugeCaptionPanel("testDevice01", "testDevice01", 0, 100, 0.0, 30.0, 30.0, 60.0, 60.0, 100.0, "100px", "100px");
		new GaugePlotWaiter(100, 1);
	}
	private void addGauge2()
	{
		gaugeGrid.setWidget(0, 0, gaugeCaptionPanel1);
		gaugeCaptionPanel2 = new GaugeCaptionPanel("testDevice02", "testDevice02", 0, 100, 0.0, 30.0, 30.0, 60.0, 60.0, 100.0, "100px", "100px");
		new GaugePlotWaiter(100, 2);
	}
	private void addGauge3()
	{
		gaugeGrid.setWidget(0, 1, gaugeCaptionPanel2);
		gaugeCaptionPanel3 = new GaugeCaptionPanel("testDevice03", "testDevice03", 0, 100, 0.0, 30.0, 30.0, 60.0, 60.0, 100.0, "100px", "100px");
		new GaugePlotWaiter(100, 3);
	}
	private void startMqtt()
	{
		gaugeGrid.setWidget(0, 2, gaugeCaptionPanel3);
		gaugeMqttData = new GaugeMqttData(getEntryPointApp());
	}
	class GaugePlotWaiter extends GskelLoadWaiter
	{
		public GaugePlotWaiter(int loopTimeMillis, int itask) {super(loopTimeMillis, itask);}
		@Override
		public boolean isLoaded() 
		{
			boolean loaded = false;
			if (getItask() == 1) loaded = gaugeCaptionPanel1.isLoaded();
			if (getItask() == 2) loaded = gaugeCaptionPanel2.isLoaded();
			if (getItask() == 3) loaded = gaugeCaptionPanel3.isLoaded();
			return loaded;
		}
		@Override
		public void taskAfterLoad() 
		{
			if (getItask() == 1) addGauge2();
			if (getItask() == 2) addGauge3();
			if (getItask() == 3) startMqtt();
		}
		
	}
	class GaugeMqttData extends MqttData
	{

		public GaugeMqttData(EntryPointApp entryPointApp) 
		{
			super("mqttTester/get/gauges", MqttData.JSONDATA, 1000, entryPointApp);
		}

		@Override
		public void doSomethingWithData() 
		{
			try
			{
				gaugeCaptionPanel1.updateReadings(Double.parseDouble(getJsonValue("testDevice01")));
				gaugeCaptionPanel2.updateReadings(Double.parseDouble(getJsonValue("testDevice02")));
				gaugeCaptionPanel3.updateReadings(Double.parseDouble(getJsonValue("testDevice03")));
			}
			catch (Exception e)
			{
				GWT.log(e.getMessage());
			}
			
		}
	}

}
