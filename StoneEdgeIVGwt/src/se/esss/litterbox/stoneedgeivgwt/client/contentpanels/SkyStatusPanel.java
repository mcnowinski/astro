package se.esss.litterbox.stoneedgeivgwt.client.contentpanels;


import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import se.esss.litterbox.stoneedgeivgwt.client.EntryPointApp;
import se.esss.litterbox.stoneedgeivgwt.client.gskel.GskelVerticalPanel;
import se.esss.litterbox.stoneedgeivgwt.client.mqttdata.MqttData;

public class SkyStatusPanel extends GskelVerticalPanel
{
	Label ambTempLabel = new Label();
	Label priTempLabel = new Label();
	Label secTempLabel = new Label();
	Label ovoltsLabel = new Label();
	Label irvoltsLabel = new Label();
	Label cloudLabel = new Label();
	Label rainLabel = new Label();
	Label dewLabel = new Label();
	Label oatLabel = new Label();
	Label windspeedLabel = new Label();
	Label winddirLabel = new Label();
	Label humidityLabel = new Label();
	Label barLabel = new Label();
	Label sunLabel = new Label();
	Label rainfallLabel = new Label();
	Label sunAltLabel = new Label();
	Label moonAltLabel = new Label();
	Label moonPhaseLabel = new Label();
	Label lunationLabel = new Label();
	

	public SkyStatusPanel( EntryPointApp entryPointApp) 
	{
		super(true, entryPointApp);
		Image darkSkyimage = new Image("http://cleardarksky.com/csk/getcsk.php?id=SmnCA");
		
		Grid tempsGrid = new Grid(4,2);
		tempsGrid.setWidget(0, 0, new Label("amb"));
		tempsGrid.setWidget(1, 0, new Label("pri"));
		tempsGrid.setWidget(2, 0, new Label("sec"));
		tempsGrid.setWidget(3, 0, new Label("-"));
		tempsGrid.setWidget(0, 1, ambTempLabel);
		tempsGrid.setWidget(1, 1, priTempLabel);
		tempsGrid.setWidget(2, 1, secTempLabel);
		CaptionPanel tempsCaptionPanel = new CaptionPanel("Temps");
		tempsCaptionPanel.add(tempsGrid);

		Grid taux1Grid = new Grid(4,2);
		taux1Grid.setWidget(0, 0, new Label("ovolts"));
		taux1Grid.setWidget(1, 0, new Label("irvolts"));
		taux1Grid.setWidget(2, 0, new Label("-"));
		taux1Grid.setWidget(3, 0, new Label("-"));
		taux1Grid.setWidget(0, 1, ovoltsLabel);
		taux1Grid.setWidget(1, 1, irvoltsLabel);
		taux1Grid.setWidget(2, 1, new Label(" "));
		CaptionPanel taux1CaptionPanel = new CaptionPanel("Taux1");
		taux1CaptionPanel.add(taux1Grid);
		
		Grid taux2Grid = new Grid(4,2);
		taux2Grid.setWidget(0, 0, new Label("cloud"));
		taux2Grid.setWidget(1, 0, new Label("rain"));
		taux2Grid.setWidget(2, 0, new Label("dew"));
		taux2Grid.setWidget(3, 0, new Label("-"));
		taux2Grid.setWidget(0, 1, cloudLabel);
		taux2Grid.setWidget(1, 1, rainLabel);
		taux2Grid.setWidget(2, 1, dewLabel);
		CaptionPanel taux2CaptionPanel = new CaptionPanel("Taux2");
		taux2CaptionPanel.add(taux2Grid);
		
		Grid mets1Grid = new Grid(4,2);
		mets1Grid.setWidget(0, 0, new Label("oat"));
		mets1Grid.setWidget(1, 0, new Label("windspeed"));
		mets1Grid.setWidget(2, 0, new Label("winddir"));
		mets1Grid.setWidget(3, 0, new Label("-"));
		mets1Grid.setWidget(0, 1, oatLabel);
		mets1Grid.setWidget(1, 1, windspeedLabel);
		mets1Grid.setWidget(2, 1, winddirLabel);
		CaptionPanel mets1CaptionPanel = new CaptionPanel("Mets1");
		mets1CaptionPanel.add(mets1Grid);
		
		Grid mets2Grid = new Grid(4,2);
		mets2Grid.setWidget(0, 0, new Label("humidity"));
		mets2Grid.setWidget(1, 0, new Label("bar"));
		mets2Grid.setWidget(2, 0, new Label("sun"));
		mets2Grid.setWidget(3, 0, new Label("rainfall"));
		mets2Grid.setWidget(0, 1, humidityLabel);
		mets2Grid.setWidget(1, 1, barLabel);
		mets2Grid.setWidget(2, 1, sunLabel);
		mets2Grid.setWidget(3, 1, rainfallLabel);
		CaptionPanel mets2CaptionPanel = new CaptionPanel("Mets2");
		mets2CaptionPanel.add(mets2Grid);
		
		Grid sunGrid = new Grid(4,2);
		sunGrid.setWidget(0, 0, new Label("Sun Alt."));
		sunGrid.setWidget(1, 0, new Label("Moon Alt."));
		sunGrid.setWidget(2, 0, new Label("Moon Phase"));
		sunGrid.setWidget(3, 0, new Label("Lunation"));
		sunGrid.setWidget(0, 1, sunAltLabel);
		sunGrid.setWidget(1, 1, moonAltLabel);
		sunGrid.setWidget(2, 1, moonPhaseLabel);
		sunGrid.setWidget(3, 1, lunationLabel);
		CaptionPanel sunCaptionPanel = new CaptionPanel("Sun & Moon");
		sunCaptionPanel.add(sunGrid);
		
		HorizontalPanel statusPanel = new HorizontalPanel();
		statusPanel.add(tempsCaptionPanel);
		statusPanel.add(taux1CaptionPanel);
		statusPanel.add(taux2CaptionPanel);
		statusPanel.add(mets1CaptionPanel);
		statusPanel.add(mets2CaptionPanel);
		statusPanel.add(sunCaptionPanel);
		CaptionPanel statusCaptionPanel = new CaptionPanel("Status");
		statusCaptionPanel.add(statusPanel);
		
		add(darkSkyimage);
		add(statusCaptionPanel);
		new TempsMqttData(entryPointApp);
		new TauxMqttData(entryPointApp);
		new MetsMqttData(entryPointApp);
		new SunMqttData(entryPointApp);
	}
	class TempsMqttData extends MqttData
	{
		public TempsMqttData(EntryPointApp entryPointApp) 
		{
			super("tel/done/tx/temps", MqttData.JSONDATA, 1000, entryPointApp);
		}
		@Override
		public void doSomethingWithData() 
		{
			try 
			{
				ambTempLabel.setText(getJsonValue("amb"));
				priTempLabel.setText(getJsonValue("pri"));
				secTempLabel.setText(getJsonValue("sec"));
			} catch (Exception e) {}
			
		}
	}
	class TauxMqttData extends MqttData
	{
		public TauxMqttData(EntryPointApp entryPointApp) 
		{
			super("tel/done/tx/taux", MqttData.JSONDATA, 1000, entryPointApp);
		}
		@Override
		public void doSomethingWithData() 
		{
			try 
			{
				ovoltsLabel.setText(getJsonValue("ovolts"));
				irvoltsLabel.setText(getJsonValue("irvolts"));
				cloudLabel.setText(getJsonValue("cloud"));
				rainLabel.setText(getJsonValue("rain"));
				dewLabel.setText(getJsonValue("dew"));
			} catch (Exception e) {}
			
		}
	}
	class MetsMqttData extends MqttData
	{
		public MetsMqttData(EntryPointApp entryPointApp) 
		{
			super("tel/done/tx/mets", MqttData.JSONDATA, 1000, entryPointApp);
		}
		@Override
		public void doSomethingWithData() 
		{
			try 
			{
				oatLabel.setText(getJsonValue("oat"));
				windspeedLabel.setText(getJsonValue("windspeed"));
				winddirLabel.setText(getJsonValue("winddir"));
				humidityLabel.setText(getJsonValue("humidity"));
				barLabel.setText(getJsonValue("bar"));
				sunLabel.setText(getJsonValue("sun"));
				rainfallLabel.setText(getJsonValue("rainfall"));
			} catch (Exception e) {}
			
		}
	}
	class SunMqttData extends MqttData
	{
		public SunMqttData(EntryPointApp entryPointApp) 
		{
			super("tel/done/sun", MqttData.JSONDATA, 1000, entryPointApp);
		}
		@Override
		public void doSomethingWithData() 
		{
			try 
			{
				String moonString = getJsonValue("moon");
				String[] parsedString = moonString.split(" ");

				for (int ii = 0; ii < parsedString.length; ++ii)
				{
					int equalPos = parsedString[ii].indexOf("=");
					if (equalPos > 0)
					{
						if (parsedString[ii].indexOf("alt=") >= 0) moonAltLabel.setText(parsedString[ii].substring(equalPos + 1));
						if (parsedString[ii].indexOf("phase=") >= 0) moonPhaseLabel.setText(parsedString[ii].substring(equalPos + 1));
						if (parsedString[ii].indexOf("lunation=") >= 0) lunationLabel.setText(parsedString[ii].substring(equalPos + 1));
					}
				}
				String sunString = getJsonValue("sun");
				parsedString = sunString.split(" ");

				for (int ii = 0; ii < parsedString.length; ++ii)
				{
					int equalPos = parsedString[ii].indexOf("=");
					if (equalPos > 0)
					{
						if (parsedString[ii].indexOf("alt=") >= 0) sunAltLabel.setText(parsedString[ii].substring(equalPos + 1));
					}
				}
			} catch (Exception e) {}
			
		}
	}

}
