package com.astrofizzbizz.stoneedge3.client.imaging;

import com.astrofizzbizz.stoneedge3.client.StoneEdge3;
import com.astrofizzbizz.stoneedge3.client.Utilities;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;

public class DataPathInfoPanel extends CaptionPanel
{
	private StoneEdge3 stoneEdge3;
	private Label userLabel = new Label("User");
	private Label sessionLabel = new Label("Session");
	private Label targetLabel = new Label("Target");
	
	public Label getUserLabel() {return userLabel;}
	public Label getSessionLabel() {return sessionLabel;}
	public Label getTargetLabel() {return targetLabel;}

	public DataPathInfoPanel(StoneEdge3 stoneEdge3)
	{
		super("Data Path");
		this.stoneEdge3 = stoneEdge3;
		Grid infoGrid = new Grid(3, 2);
		infoGrid.setWidget(0, 0, new Label("User:"));
		infoGrid.setWidget(0, 1, userLabel);
		infoGrid.setWidget(1, 0, new Label("Session:"));
		infoGrid.setWidget(1, 1, sessionLabel);
		infoGrid.setWidget(2, 0, new Label("Target:"));
		infoGrid.setWidget(2, 1, targetLabel);


		add(infoGrid);
		
	}
	public void updateImageDataPath()
	{
		String email = Utilities.stripWhiteSpaces(stoneEdge3.getLockPanel().getEmailAddressLabel().getText());
		userLabel.setText(email.substring(0, email.indexOf("@")));
		targetLabel.setText(stoneEdge3.getSe3TabLayoutPanel().getTargetingPanel().getCurrentTargetLabel().getText());
		sessionLabel.setText(getSession());
	}
	public String getSession()
	{
		String ut = stoneEdge3.getTelescopeStatusPanel().getUniveralTime();
		String month = ut.substring(4, 7);
		String day = ut.substring(8, 10);
		day = Utilities.stripWhiteSpaces(day);
		if (day.length() < 2) day = "0" + day;
		String year = ut.substring(24, 28);
		String session = year + month + day;
		return session;
	}
}
