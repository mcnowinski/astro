package se.esss.litterbox.stoneedgeivgwt.client.contentpanels;


import com.google.gwt.core.client.GWT;

import se.esss.litterbox.stoneedgeivgwt.client.EntryPointApp;
import se.esss.litterbox.stoneedgeivgwt.client.googleplots.ScatterPlotCaptionPanel;
import se.esss.litterbox.stoneedgeivgwt.client.gskel.GskelLoadWaiter;
import se.esss.litterbox.stoneedgeivgwt.client.gskel.GskelVerticalPanel;
import se.esss.litterbox.stoneedgeivgwt.client.mqttdata.MqttData;

public class ScatterChartShowCasePanel extends GskelVerticalPanel
{
	ScatterPlotCaptionPanel scatterPlotCaptionPanel;
	ScatterPlotMqttData lineChartMqttData;

	public ScatterChartShowCasePanel(EntryPointApp entryPointApp) 
	{
		super(true, entryPointApp);
		addLineChart();
	}
	private void addLineChart()
	{
		int numPts = 5;
		String title = "ScatterPlot";
		String xaxisLabel = "Time (fortnights)";
		String yaxisLabel  = "Mass (slugs)";
		String[] lineChartLegend  = {"Thingy 1", "Thingy 2"};
		scatterPlotCaptionPanel = new ScatterPlotCaptionPanel(numPts, title, xaxisLabel, yaxisLabel, lineChartLegend, "600px", "400px");
		scatterPlotCaptionPanel.setWidth("600px");
		new ScatterPlotPlotWaiter(100, 1);
	}
	private void startMqtt()
	{
		add(scatterPlotCaptionPanel);
		lineChartMqttData = new ScatterPlotMqttData(getEntryPointApp());
	}
	class ScatterPlotPlotWaiter extends GskelLoadWaiter
	{
		public ScatterPlotPlotWaiter(int loopTimeMillis, int itask) {super(loopTimeMillis, itask);}
		@Override
		public boolean isLoaded() 
		{
			boolean loaded = false;
			if (getItask() == 1) loaded = scatterPlotCaptionPanel.isLoaded();
			return loaded;
		}
		@Override
		public void taskAfterLoad() 
		{
			if (getItask() == 1) startMqtt();
		}
		
	}
	class ScatterPlotMqttData extends MqttData
	{

		public ScatterPlotMqttData(EntryPointApp entryPointApp) 
		{
			super("mqttTester/get/scatter", MqttData.BYTEDATA, 1000, entryPointApp);
		}

		@Override
		public void doSomethingWithData() 
		{
			try
			{
				String messageString = new String(getMessage());
				String[] dataString = messageString.split(",");
				double[][] xaxisData = new double[2][5];
				double[][] yaxisData = new double[2][5];
				for (int ii = 0; ii < 5; ++ii)
				{
					xaxisData[0][ii] = Double.parseDouble(dataString[ii]);
					yaxisData[0][ii] = Double.parseDouble(dataString[ii + 5]);
					xaxisData[1][ii] = Double.parseDouble(dataString[ii + 10]);
					yaxisData[1][ii] = Double.parseDouble(dataString[ii + 15]);
				}
				scatterPlotCaptionPanel.updateReadings(xaxisData, yaxisData);
				
			}
			catch (Exception e)
			{
				GWT.log(e.getMessage());
			}
			
		}
	}
}
