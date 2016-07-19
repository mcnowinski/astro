package com.astrofizzbizz.stoneedge3.client.targeting;

import java.util.Date;

import com.astrofizzbizz.stoneedge3.client.Se3TabLayoutScrollPanel;
import com.astrofizzbizz.stoneedge3.client.StoneEdge3;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TargetingPanel extends Se3TabLayoutScrollPanel
{
 	SkyTargetChart skyc;
	public SkyTargetChart getSkyc() {return skyc;}
	private Label lstTimeLabel = new Label("");
	private Label obsTimeLabel = new Label("");
	private Label gmtTimeLabel = new Label("");
	private Label locTimeLabel = new Label(new Date().toString());
	private Label currentTargetLabel = new Label("Unknown");
	private Label currentRaLabel = new Label("");
	private Label currentDecLabel = new Label("");
	AstroTargetPanel astroTargetPanel;
	SimbadSearchPanel simbadSearchPanel;
	VerticalPanel skyChartVerticalPanel = new VerticalPanel();

	public VerticalPanel getSkyChartVerticalPanel() {return skyChartVerticalPanel;}
	public Label getCurrentTargetLabel() {return currentTargetLabel;}
	public AstroTargetPanel getAstroTargetPanel() {return astroTargetPanel;}
	public SimbadSearchPanel getSimbadSearchPanel() {return simbadSearchPanel;}

	public TargetingPanel(StoneEdge3 stoneEdge3)
	{
		super(stoneEdge3);
		HorizontalPanel horizontalPanel1 = new HorizontalPanel();

	    skyc = new SkyTargetChart(this);
		skyChartVerticalPanel.add(skyc);
		horizontalPanel1.add(skyChartVerticalPanel);
		VerticalPanel verticalPanel1 = new VerticalPanel();
		horizontalPanel1.add(verticalPanel1);
		
		verticalPanel1.add(timeCaptionPanel());
		verticalPanel1.add(new CurrentPositionPanel(stoneEdge3, currentTargetLabel, currentRaLabel, currentDecLabel));
		astroTargetPanel = new AstroTargetPanel(stoneEdge3);
		verticalPanel1.add(astroTargetPanel);
		simbadSearchPanel = new SimbadSearchPanel(stoneEdge3);		
		verticalPanel1.add(simbadSearchPanel);
		
		add(horizontalPanel1);
	}
	private CaptionPanel timeCaptionPanel()
	{
		CaptionPanel timeCaptionPanel = new CaptionPanel("Time");
//		timeCaptionPanel.setSize("17em", "10em");
		Grid timeGrid = new Grid(4, 2);
		timeGrid.setWidget(0, 0, new Label("Local Sidereal "));
		timeGrid.setWidget(1, 0, new Label("Observatory "));
		timeGrid.setWidget(2, 0, new Label("Greenwich Mean "));
		timeGrid.setWidget(3, 0, new Label("Local "));

		timeGrid.setWidget(0, 1, lstTimeLabel);
		timeGrid.setWidget(1, 1, obsTimeLabel);
		timeGrid.setWidget(2, 1, gmtTimeLabel);
		timeGrid.setWidget(3, 1, locTimeLabel);
		timeCaptionPanel.setContentWidget(timeGrid);
		
		return timeCaptionPanel;
		
	}
	public void setUnknownStatus()
	{
		lstTimeLabel.setText("");
		obsTimeLabel.setText("");
		gmtTimeLabel.setText("");
		currentRaLabel.setText("");
		currentDecLabel.setText("");
		currentTargetLabel.setText("Unknown");
	}
	public void update()
	{
		skyc.updateChart(getStoneEdge3().getTelescopeStatusPanel().getUpdateDate());	
		astroTargetPanel.update();
		locTimeLabel.setText(getStoneEdge3().getTelescopeStatusPanel().getUpdateDate().toString());
		if (getStoneEdge3().getLockPanel().isLockedOwnByMe())
		{
			lstTimeLabel.setText(getStoneEdge3().getTelescopeStatusPanel().getLocalSideralTime());
			obsTimeLabel.setText(getStoneEdge3().getTelescopeStatusPanel().getObsLocalTime());
			gmtTimeLabel.setText(getStoneEdge3().getTelescopeStatusPanel().getUniveralTime());
			currentRaLabel.setText(StarCoordUtilities.raString(getStoneEdge3().getTelescopeStatusPanel().getRaDeg()));
			currentDecLabel.setText(StarCoordUtilities.decString(getStoneEdge3().getTelescopeStatusPanel().getDecDeg()));
		}
		else
		{
			setUnknownStatus();
		}
		
	}

}
