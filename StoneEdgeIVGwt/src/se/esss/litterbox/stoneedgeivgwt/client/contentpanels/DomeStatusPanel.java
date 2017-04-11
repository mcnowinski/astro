package se.esss.litterbox.stoneedgeivgwt.client.contentpanels;

import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

import se.esss.litterbox.stoneedgeivgwt.client.EntryPointApp;
import se.esss.litterbox.stoneedgeivgwt.client.mqttdata.MqttData;

public class DomeStatusPanel extends HorizontalPanel
{
	EntryPointApp entryPointApp;
	
	Label lstLabel = new Label("");
	Label localLabel = new Label("");
	Label gmtLabel = new Label("");
	Label raLabel = new Label("");
	Label decLabel = new Label("");
	Label slewingLabel = new Label("");
	Label altLabel = new Label("");
	Label azLabel = new Label("");
	Label haLabel = new Label("");
	Label slitLabel = new Label("");
	Label cantopenLabel = new Label("");
	Label domeLabel = new Label("");
	Label haTrackLabel = new Label("");
	Label decTrackLabel = new Label("");
	Label filterLabel = new Label("");
	Label focusLabel = new Label("");
	
	public DomeStatusPanel(EntryPointApp entryPointApp)
	{
		this.entryPointApp = entryPointApp;
		Grid timeGrid = new Grid(3,2);
		timeGrid.setWidget(0, 0, new Label("lst"));
		timeGrid.setWidget(1, 0, new Label("local"));
		timeGrid.setWidget(2, 0, new Label("gmt"));
		timeGrid.setWidget(0, 1, lstLabel);
		timeGrid.setWidget(1, 1, localLabel);
		timeGrid.setWidget(2, 1, gmtLabel);
		CaptionPanel timeCaptionPanel = new CaptionPanel("Time");
		timeCaptionPanel.add(timeGrid);

		Grid raDecGrid = new Grid(3,2);
		raDecGrid.setWidget(0, 0, new Label("R.A."));
		raDecGrid.setWidget(1, 0, new Label("DEC"));
		raDecGrid.setWidget(2, 0, new Label("ha"));
		raDecGrid.setWidget(0, 1, raLabel);
		raDecGrid.setWidget(1, 1, decLabel);
		raDecGrid.setWidget(2, 1, haLabel);
		CaptionPanel raDecCaptionPanel = new CaptionPanel("R.A. & DEC");
		raDecCaptionPanel.add(raDecGrid);

		Grid altAzGrid = new Grid(3,2);
		altAzGrid.setWidget(0, 0, new Label("Alt."));
		altAzGrid.setWidget(1, 0, new Label("Az."));
		altAzGrid.setWidget(2, 0, new Label("Slew"));
		altAzGrid.setWidget(0, 1, altLabel);
		altAzGrid.setWidget(1, 1, azLabel);
		altAzGrid.setWidget(2, 1, slewingLabel);
		CaptionPanel altAzCaptionPanel = new CaptionPanel("Alt. & Az.");
		altAzCaptionPanel.add(altAzGrid);

		Grid slitGrid = new Grid(3,2);
		slitGrid.setWidget(0, 0, new Label("State"));
		slitGrid.setWidget(1, 0, new Label("Limit"));
		slitGrid.setWidget(2, 0, new Label("Dome"));
		slitGrid.setWidget(0, 1, slitLabel);
		slitGrid.setWidget(1, 1, cantopenLabel);
		slitGrid.setWidget(2, 1, domeLabel);
		CaptionPanel slitCaptionPanel = new CaptionPanel("Slit");
		slitCaptionPanel.add(slitGrid);

		Grid trackGrid = new Grid(3,2);
		trackGrid.setWidget(0, 0, new Label("ha"));
		trackGrid.setWidget(1, 0, new Label("dec"));
		trackGrid.setWidget(2, 0, new Label("-"));
		trackGrid.setWidget(0, 1, haTrackLabel);
		trackGrid.setWidget(1, 1, decTrackLabel);
		trackGrid.setWidget(2, 1, new Label(" "));
		CaptionPanel trackCaptionPanel = new CaptionPanel("Track");
		trackCaptionPanel.add(trackGrid);

		Grid filterGrid = new Grid(3,2);
		filterGrid.setWidget(0, 0, new Label("Filter"));
		filterGrid.setWidget(1, 0, new Label("Focus"));
		filterGrid.setWidget(2, 0, new Label("-"));
		filterGrid.setWidget(0, 1, filterLabel);
		filterGrid.setWidget(1, 1, focusLabel);
		filterGrid.setWidget(2, 1, new Label(" "));
		CaptionPanel filterCaptionPanel = new CaptionPanel("Filter & Focus");
		filterCaptionPanel.add(filterGrid);

		add(timeCaptionPanel);
		add(raDecCaptionPanel);
		add(altAzCaptionPanel);
		add(slitCaptionPanel);
		add(trackCaptionPanel);
		add(filterCaptionPanel);
		
		new TimeMqttData(entryPointApp);
		new WhereMqttData(entryPointApp);
		new SlitMqttData(entryPointApp);
		new DomeMqttData(entryPointApp);
		new TrackMqttData(entryPointApp);
		new FilterMqttData(entryPointApp);
		new FocusMqttData(entryPointApp);
		
	}
	class TimeMqttData extends MqttData
	{
		public TimeMqttData(EntryPointApp entryPointApp) 
		{
			super("tel/done/time", MqttData.JSONDATA, 1000, entryPointApp);
		}
		@Override
		public void doSomethingWithData() 
		{
			try 
			{
				lstLabel.setText(getJsonValue("lst"));
				localLabel.setText(getJsonValue("local"));
				gmtLabel.setText(getJsonValue("gmt"));
			} catch (Exception e) {}
			
		}
	}
	class WhereMqttData extends MqttData
	{
		public WhereMqttData(EntryPointApp entryPointApp) 
		{
			super("tel/done/tx/where", MqttData.JSONDATA, 1000, entryPointApp);
		}
		@Override
		public void doSomethingWithData() 
		{
			try 
			{
				raLabel.setText(getJsonValue("ra"));
				decLabel.setText(getJsonValue("dec"));
				haLabel.setText(getJsonValue("ha"));
				altLabel.setText(getJsonValue("alt"));
				azLabel.setText(getJsonValue("az"));
				slewingLabel.setText(getJsonValue("slewing"));
			} catch (Exception e) {}
			
		}
	}
	class SlitMqttData extends MqttData
	{
		public SlitMqttData(EntryPointApp entryPointApp) 
		{
			super("tel/done/tx/slit", MqttData.JSONDATA, 1000, entryPointApp);
		}
		@Override
		public void doSomethingWithData() 
		{
			try 
			{
				slitLabel.setText(getJsonValue("slit"));
				cantopenLabel.setText("");
				cantopenLabel.setText(getJsonValue("cantopen"));
			} catch (Exception e) {}
			
		}
	}
	class DomeMqttData extends MqttData
	{
		public DomeMqttData(EntryPointApp entryPointApp) 
		{
			super("tel/done/tx/dome", MqttData.JSONDATA, 1000, entryPointApp);
		}
		@Override
		public void doSomethingWithData() 
		{
			try 
			{
				domeLabel.setText(getJsonValue("az"));
			} catch (Exception e) {}
			
		}
	}
	class TrackMqttData extends MqttData
	{
		public TrackMqttData(EntryPointApp entryPointApp) 
		{
			super("tel/done/tx/track", MqttData.JSONDATA, 1000, entryPointApp);
		}
		@Override
		public void doSomethingWithData() 
		{
			try 
			{
				haTrackLabel.setText(getJsonValue("ha"));
				decTrackLabel.setText(getJsonValue("dec"));
			} catch (Exception e) {}
			
		}
	}
	class FilterMqttData extends MqttData
	{
		public FilterMqttData(EntryPointApp entryPointApp) 
		{
			super("tel/done/pfilter", MqttData.JSONDATA, 1000, entryPointApp);
		}
		@Override
		public void doSomethingWithData() 
		{
			try 
			{
				filterLabel.setText(getJsonValue("filter"));
			} catch (Exception e) {}
			
		}
	}
	class FocusMqttData extends MqttData
	{
		public FocusMqttData(EntryPointApp entryPointApp) 
		{
			super("tel/done/tx/focus", MqttData.JSONDATA, 1000, entryPointApp);
		}
		@Override
		public void doSomethingWithData() 
		{
			try 
			{
				focusLabel.setText(getJsonValue("pos"));
			} catch (Exception e) {}
			
		}
	}
}
