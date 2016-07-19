package com.astrofizzbizz.stoneedge3.client.targeting;

import java.util.Date;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class SkyTargetChart implements IsWidget
{
    private static final String unsupportedBrowser = "Your browser does not support the HTML5 Canvas";
    private Canvas canvas;
    private Context2d context;
    final Image image = new Image();
    
	static double jan1Offset = 40587.0;
	private int chartSize = 500;
	private double time = 55197.22;
	private String presentRaString = null;
	private String presentDecString = null;
	private String targetRaString = null;
	private String targetDecString = null;
	private int iline = 1;
	TargetingPanel targetingPanel;

	String mapUrl = "http://www.heavens-above.com/wholeskychart.ashx?lat=45.0&lng=-89.0&size=500&SL=1&SN=1&BW=1&time=55197.22";

    public SkyTargetChart(TargetingPanel targetingPanel)
    {
		this.targetingPanel = targetingPanel;
    	canvas = Canvas.createIfSupported();
        if (canvas == null)
        {
            // If not supported then simply show a message
            RootPanel.get().add(new Label(unsupportedBrowser));
            return;
        }
        context = canvas.getContext2d();
        canvas.setWidth(chartSize + "px");
        canvas.setHeight(chartSize + "px");
        canvas.setCoordinateSpaceWidth(chartSize);
        canvas.setCoordinateSpaceHeight(chartSize);
// have to do this because sky chart won't load like static image
        image.addLoadHandler(new SkyChartLoadHandler());
        updateChart(new Date());
    }
    public void updateChart(Date updateDate)
    {
    	setTime(updateDate);
		String mapUrl = "";
		mapUrl = mapUrl + "http://www.heavens-above.com/wholeskychart.ashx?lat=";
		mapUrl = mapUrl + targetingPanel.getStoneEdge3().getObsLatitude();
		mapUrl = mapUrl + "&lng=";
		mapUrl = mapUrl + targetingPanel.getStoneEdge3().getObsLongitude();
		mapUrl = mapUrl + "&size=";
		mapUrl = mapUrl + chartSize;
		mapUrl = mapUrl + "&SL=1&SN=1&BW=1&time=";
		mapUrl = mapUrl + time;
		image.setUrl(mapUrl);
//		context.clearRect(0, 0, chartSize, chartSize);
        targetingPanel.getSkyChartVerticalPanel().setVisible(false);
		targetingPanel.getSkyChartVerticalPanel().add(image);
    }
	public Date getTime() {
		double tempTime = time - jan1Offset;
		tempTime = tempTime * 3600.0 * 24.0 * 1000.0;
		return new Date((long) tempTime);
	}
	public void setTime(Date date) 
	{
		time = ((double) date.getTime())/ 1000.0 / 24.0 / 3600;
		time = time + jan1Offset;
	}
	public void drawTarget(String raString, String decString, int red, int green, int blue)
	{
		if (raString == null) return;
		if (decString == null) return;
		double[] altAz = StarCoordUtilities.getAltAzDeg(StarCoordUtilities.raDeg(raString), StarCoordUtilities.decDeg(decString), 
				targetingPanel.getStoneEdge3().getObsLongitude(), 
				targetingPanel.getStoneEdge3().getObsLatitude(), 
				targetingPanel.getStoneEdge3().getTelescopeStatusPanel().getUpdateDate());
		double thetaTarget = altAz[1] + 180;
		double dx = ((double) chartSize/2) * Math.sin(thetaTarget * Math.PI / 180.0) + chartSize/2;
		double dy = ((double) chartSize/2) * Math.cos(thetaTarget * Math.PI / 180.0) + chartSize/2;
		context.beginPath();
        context.moveTo(chartSize/2, chartSize/2);
        context.lineTo(dx, dy);
        context.setStrokeStyle(CssColor.make(red,green,blue));
        context.stroke();

		int circleNpts = 180 * 1;
		double radius = (1 - altAz[0] / 90.0) * chartSize/2;
		if (radius >= 0.99 * chartSize/2) radius = 0.99 * chartSize/2;
		dx =  chartSize/2;
		dy =  radius  + chartSize/2;;
		context.beginPath();
        context.setStrokeStyle(CssColor.make(red,green,blue));
        context.moveTo(dx, dy);
		for (int ii = 1; ii < circleNpts; ++ii)
		{
			dx = radius * Math.sin(((double) ii) * 2.0 * Math.PI / ((double)(circleNpts - 1)) ) + chartSize/2;
			dy = radius * Math.cos(((double) ii) * 2.0 * Math.PI / ((double)(circleNpts - 1))) + chartSize/2;
	        context.lineTo(dx, dy);
	        context.stroke();
		}
	}

	public String getPresentRaString() {return presentRaString;}
	public String getPresentDecString() {return presentDecString;}
	public String getTargetRaString() {return targetRaString;}
	public String getTargetDecString() {return targetDecString;}
	public void setPresentRaString(String presentRaString) {this.presentRaString = presentRaString;}
	public void setPresentDecString(String presentDecString) {this.presentDecString = presentDecString;}
	public void setTargetRaString(String targetRaString) {this.targetRaString = targetRaString;}
	public void setTargetDecString(String targetDecString) {this.targetDecString = targetDecString;}
	@Override
	public Widget asWidget() 
	{
		return canvas;
	}
// have to do this because sky chart won't load like static image
	private class SkyChartLoadHandler implements LoadHandler
	{
		@Override
		public void onLoad(LoadEvent event) 
		{
			context.drawImage(ImageElement.as(image.getElement()), 0.0, 0.0);
    		drawTarget(targetRaString, targetDecString, 255, 0, 0);
    		drawTarget(presentRaString, presentDecString, 0, 255, 0);
    		context.moveTo(0, iline);
            context.lineTo(500, iline);
            context.setStrokeStyle("red");
//            context.stroke();
            targetingPanel.getSkyChartVerticalPanel().remove(image);
            targetingPanel.getSkyChartVerticalPanel().setVisible(true);
           
            //TODO remove this
//            iline = iline + 1;
//            targetingPanel.stoneEdge3.statusTextArea.addStatus("Sky Chart Updated");
		}
		
	}
}
