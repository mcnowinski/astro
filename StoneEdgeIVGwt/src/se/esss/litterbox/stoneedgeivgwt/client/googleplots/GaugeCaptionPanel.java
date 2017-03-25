package se.esss.litterbox.stoneedgeivgwt.client.googleplots;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.CaptionPanel;

public class GaugeCaptionPanel extends CaptionPanel
{
	private GaugePlotPanel gaugePlot;
	private boolean loaded = false;
	
	public boolean isLoaded() {return loaded;}
	
	public GaugeCaptionPanel(String captionTitle,  String gaugeLabel, double min, double max, double greenMin, double greenMax, double yellowMin, double yellowMax, double redMin, double redMax, String plotWidth, String plotHeight)
	{
		super(captionTitle);
		loaded = false;
		gaugePlot = new GaugePlotPanel(gaugeLabel, min, max, greenMin, greenMax, yellowMin, yellowMax, redMin, redMax, plotWidth, plotHeight);
		LoadPlotTimer lpt = new LoadPlotTimer(this);
		lpt.scheduleRepeating(50);
		
	}
	public void updateReadings(double value)
	{
		if (!loaded) return;
		gaugePlot.setValue(value);
		gaugePlot.draw();
	}
	private static class LoadPlotTimer extends Timer
	{
		GaugeCaptionPanel gaugeCaptionPanel;
		LoadPlotTimer(GaugeCaptionPanel gaugeCaptionPanel)
		{
			this.gaugeCaptionPanel = gaugeCaptionPanel;
			gaugeCaptionPanel.loaded = false;
			gaugeCaptionPanel.gaugePlot.initialize();
			gaugeCaptionPanel.add(gaugeCaptionPanel.gaugePlot);
		}
		@Override
		public void run() 
		{
			if (!gaugeCaptionPanel.gaugePlot.isLoaded()) 
			{
				return;
			}
			else
			{
				gaugeCaptionPanel.loaded = true;
				this.cancel();
			}

		}
	}

}
