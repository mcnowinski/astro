package com.astrofizzbizz.stoneedge3.client.dome;

import com.astrofizzbizz.stoneedge3.client.Se3TabLayoutScrollPanel;
import com.astrofizzbizz.stoneedge3.client.StoneEdge3;
import com.google.gwt.user.client.ui.Frame;

public class DomePanel extends Se3TabLayoutScrollPanel
{

	public DomePanel(StoneEdge3 stoneEdge3) 
	{
		super(stoneEdge3);
		String cameraLink;
		if (stoneEdge3.isDebug()) 
		{
			cameraLink = "http://192.203.224.2:8090/";
		}
		else
		{
			cameraLink = "http://localhost:8082/";
		}
		Frame cameraFrame = new Frame(cameraLink);
		cameraFrame.setSize(850 + "px", 850 + "px");
		add(cameraFrame);

	}
}
