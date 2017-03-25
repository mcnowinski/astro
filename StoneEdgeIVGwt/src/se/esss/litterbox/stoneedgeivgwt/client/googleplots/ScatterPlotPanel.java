package se.esss.litterbox.stoneedgeivgwt.client.googleplots;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.ScatterChart;
import com.googlecode.gwt.charts.client.corechart.ScatterChartOptions;
import com.googlecode.gwt.charts.client.options.HAxis;
import com.googlecode.gwt.charts.client.options.VAxis;


public class ScatterPlotPanel extends HorizontalPanel
{
	private ScatterChart scatterChart;
	private DataTable dataTable;
	private ScatterChartOptions options;
	private ChartLoader chartLoader;
	private ChartRunnable chartRunnable;
	private int numPts;
	private int numTraces;
	private String title;
	private String xaxisLabel;
	private String yaxisLabel;
	private String[] legend;

	private double[][] xaxisData;
	private double[][] yaxisData;
	private String plotWidth;
	private String plotHeight;
	private boolean loaded = false;
	
	public int getNumPts() {return numPts;}
	public int getNumTraces() {return numTraces;}
	public String getTitle() {return title;}
	public String getHaxisLabel() {return xaxisLabel;}
	public String getYaxisLabel() {return yaxisLabel;}
	public String[] getLegend() {return legend;}
	public double[][] getXaxisData() {return xaxisData;}
	public double[][] getYaxisData() {return yaxisData;}
	public String getPlotWidth() {return plotWidth;}
	public String getPlotHeight() {return plotHeight;}
	public boolean isLoaded() {return loaded;}

	public void setXaxisData(double[][] xaxisData) {this.xaxisData = xaxisData;}
	public void setYaxisData(double[][] yaxisData) {this.yaxisData = yaxisData;}

	public ScatterPlotPanel(int numPts, int numTraces, String title, String xaxisLabel, String yaxisLabel, String[] legend, String plotWidth, String plotHeight) 
	{
		loaded = false;
		this.numPts = numPts;
		this.numTraces = numTraces;
		this.title = title;
		this.xaxisLabel = xaxisLabel;
		this.yaxisLabel = yaxisLabel;
		this.plotWidth = plotWidth;
		this.plotHeight = plotHeight;
		this.legend = legend;
		xaxisData = new double[numTraces][numPts];
		yaxisData = new double[numTraces][numPts];
		for (int itrace = 0; itrace < numTraces; ++itrace)
			for (int ii = 0; ii < numPts; ++ii) 
			{
				yaxisData[itrace][ii] = 0;
				xaxisData[itrace][ii] = 0;
			}
	}

	public void initialize() 
	{
		loaded = false;
		chartLoader = new ChartLoader(ChartPackage.CORECHART);
		chartRunnable = new ChartRunnable(this);
		chartLoader.loadApi(chartRunnable);
	}

	private void setup()
	{
		// Prepare the data
		dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.NUMBER, xaxisLabel);
		for (int ii = 0; ii < numTraces; ++ii) dataTable.addColumn(ColumnType.NUMBER, legend[ii]);
		dataTable.addRows(numPts * numTraces);

		// Set options
		options = ScatterChartOptions.create();
		options.setBackgroundColor("#f0f0f0");
		options.setTitle(title);
		options.setHAxis(HAxis.create(xaxisLabel));
		options.setVAxis(VAxis.create(yaxisLabel));

	}
	public void draw() 
	{
		for (int itrace = 0; itrace < numTraces; ++itrace)
		{
			for (int ii = 0; ii < numPts; ++ii) 
			{
				dataTable.setValue(ii + itrace * numPts, 0, xaxisData[itrace][ii]);
				dataTable.setValue(ii + itrace * numPts, itrace + 1, yaxisData[itrace][ii]);
			}
		}

		scatterChart.draw(dataTable, options);
	}

	private static class ChartRunnable implements Runnable
	{
		ScatterPlotPanel timeLinePlotPanel;
		private ChartRunnable(ScatterPlotPanel timeLinePlotPanel)
		{
			this.timeLinePlotPanel = timeLinePlotPanel;
		}
		@Override
		public void run() 
		{
			timeLinePlotPanel.scatterChart = new ScatterChart();
			timeLinePlotPanel.add(timeLinePlotPanel.scatterChart);
			timeLinePlotPanel.setup();
			timeLinePlotPanel.draw();
			timeLinePlotPanel.scatterChart.setWidth(timeLinePlotPanel.plotWidth);
			timeLinePlotPanel.scatterChart.setHeight(timeLinePlotPanel.plotHeight);
			timeLinePlotPanel.loaded = true;
		}
		
	}
}
