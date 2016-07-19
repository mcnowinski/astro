package com.astrofizzbizz.stoneedge3.client;

import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InfoPanel extends Se3TabLayoutScrollPanel 
{
	public InfoPanel(StoneEdge3 stoneEdge3)
	{
		super(stoneEdge3);
		
		CaptionPanel versionCaptionPanel = new CaptionPanel("StoneEdge III " + stoneEdge3.version);
		VerticalPanel versionVerticalPanel = new VerticalPanel();
		versionCaptionPanel.setContentWidget(versionVerticalPanel);
		versionVerticalPanel.add(new Label("Last Updated " + stoneEdge3.versionDate));

		CaptionPanel coordinatorCaptionPanel = new CaptionPanel("Stone Edge Obervatory Coordinator");
		VerticalPanel coordinatorVerticalPanel = new VerticalPanel();
		coordinatorCaptionPanel.setContentWidget(coordinatorVerticalPanel);
		coordinatorVerticalPanel.add(new Label("Vivian Hoette of Yerkes Observatory"));
		coordinatorVerticalPanel.add(new Label("email: vhoette@yerkes.uchicago.edu"));
		coordinatorVerticalPanel.add(new Label("phone: 262-215-1599"));

		CaptionPanel programmerCaptionPanel = new CaptionPanel("Program written by");
		VerticalPanel programmerVerticalPanel = new VerticalPanel();
		programmerCaptionPanel.setContentWidget(programmerVerticalPanel);
		programmerVerticalPanel.add(new Label("Dave McGinnis"));
		programmerVerticalPanel.add(new Label("email: dmcginnis427@gmail.com"));
		programmerVerticalPanel.add(new Label("phone: 630-457-4205"));

		VerticalPanel vp1 = new VerticalPanel();
		vp1.add(versionCaptionPanel);
		vp1.add(coordinatorCaptionPanel);
		vp1.add(programmerCaptionPanel);
		
		add(vp1);
		
	}

}
