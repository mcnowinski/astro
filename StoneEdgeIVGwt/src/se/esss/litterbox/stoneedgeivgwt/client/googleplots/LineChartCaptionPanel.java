package se.esss.litterbox.stoneedgeivgwt.client.googleplots;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.CaptionPanel;

public class LineChartCaptionPanel extends CaptionPanel
{
	LineChartPlotPanel lineChartPlot;
	int numPts = 300;
	int numTraces = -1;
	private boolean loaded = false;
	
	public boolean isLoaded() {return loaded;}
	
	public LineChartCaptionPanel(int numPts, String title, String xaxisLabel, String yaxisLabel, String[] lineChartLegend, String plotWidth, String plotHeight)
	{
		super(title);
		this.numPts = numPts;
		loaded = false;
		numTraces = lineChartLegend.length;
		lineChartPlot = new LineChartPlotPanel(numPts, numTraces, title, xaxisLabel, yaxisLabel, lineChartLegend, plotWidth, plotHeight);
		LoadPlotTimer lpt = new LoadPlotTimer(this);
		lpt.scheduleRepeating(50);
	}
	public void updateReadings(double[] xaxisData, double[][] yaxisData)
	{
		if (!loaded) return;
		lineChartPlot.setXaxis(xaxisData);
		lineChartPlot.setTraces(yaxisData);
		lineChartPlot.draw();
	}
	private static class LoadPlotTimer extends Timer
	{
		LineChartCaptionPanel lineChartCaptionPanel;
		LoadPlotTimer(LineChartCaptionPanel lineChartCaptionPanel)
		{
			this.lineChartCaptionPanel = lineChartCaptionPanel;
			lineChartCaptionPanel.loaded = false;
			lineChartCaptionPanel.lineChartPlot.initialize();
			lineChartCaptionPanel.add(lineChartCaptionPanel.lineChartPlot);
		}
		@Override
		public void run() 
		{
			if (!lineChartCaptionPanel.lineChartPlot.isLoaded()) 
			{
				return;
			}
			else
			{
				lineChartCaptionPanel.loaded = true;
				this.cancel();
			}

		}
	}
}
