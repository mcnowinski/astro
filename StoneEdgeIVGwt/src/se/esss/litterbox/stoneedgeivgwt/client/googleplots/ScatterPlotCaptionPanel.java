package se.esss.litterbox.stoneedgeivgwt.client.googleplots;

import java.util.Date;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.CaptionPanel;

public class ScatterPlotCaptionPanel extends CaptionPanel
{
	ScatterPlotPanel scatterPlot;
	int ipt = 0;
	int numPts = 300;
	int numTraces = -1;
	long startDataDate;
	private boolean loaded = false;
	
	public boolean isLoaded() {return loaded;}
	
	public ScatterPlotCaptionPanel(int numPts, String title, String xaxisLabel, String yaxisLabel, String[] timePlotLegend, String plotWidth, String plotHeight)
	{
		super("TimePlot");
		this.numPts = numPts;
		loaded = false;
		numTraces = timePlotLegend.length;
		scatterPlot = new ScatterPlotPanel(numPts, numTraces, title, xaxisLabel, yaxisLabel, timePlotLegend, plotWidth, plotHeight);
		LoadPlotTimer lpt = new LoadPlotTimer(this);
		lpt.scheduleRepeating(50);
	}
	public void updateReadings(double[][] xaxisData, double[][] yaxisData)
	{
		if (!loaded) return;
		scatterPlot.setXaxisData(xaxisData);
		scatterPlot.setYaxisData(yaxisData);
		scatterPlot.draw();
	}
	private static class LoadPlotTimer extends Timer
	{
		ScatterPlotCaptionPanel scatterePlotCaptionPanel;
		LoadPlotTimer(ScatterPlotCaptionPanel scatterePlotCaptionPanel)
		{
			this.scatterePlotCaptionPanel = scatterePlotCaptionPanel;
			scatterePlotCaptionPanel.loaded = false;
			scatterePlotCaptionPanel.scatterPlot.initialize();
			scatterePlotCaptionPanel.add(scatterePlotCaptionPanel.scatterPlot);
			scatterePlotCaptionPanel.ipt = 0;
		}
		@Override
		public void run() 
		{
			if (!scatterePlotCaptionPanel.scatterPlot.isLoaded()) 
			{
				return;
			}
			else
			{
				scatterePlotCaptionPanel.loaded = true;
				scatterePlotCaptionPanel.startDataDate = new Date().getTime();
				this.cancel();
			}

		}
	}
}
