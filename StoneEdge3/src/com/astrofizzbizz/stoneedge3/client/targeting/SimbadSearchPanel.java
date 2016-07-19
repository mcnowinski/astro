package com.astrofizzbizz.stoneedge3.client.targeting;

import com.astrofizzbizz.stoneedge3.client.StoneEdge3;
import com.astrofizzbizz.stoneedge3.client.Utilities;
import com.astrofizzbizz.stoneedge3.shared.SimbadReturnInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

public class SimbadSearchPanel extends CaptionPanel
{
	StoneEdge3 stoneEdge3;
	private TextBox targetNameTextBox = new TextBox();
	private Button searchButton = new Button("Search");

	public SimbadSearchPanel(StoneEdge3 stoneEdge3)
	{
		super("Simbad Database");
		this.stoneEdge3 = stoneEdge3;
		HorizontalPanel horizontalPanel1 = new HorizontalPanel();
		targetNameTextBox.setSize("8em", "1em");
		Label label = new Label("Name");
		horizontalPanel1.setCellVerticalAlignment(label, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel1.add(label);
		horizontalPanel1.add(targetNameTextBox);
		horizontalPanel1.setCellHorizontalAlignment(targetNameTextBox, HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel1.setCellVerticalAlignment(targetNameTextBox, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel1.add(searchButton);
		horizontalPanel1.setCellVerticalAlignment(searchButton, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel1.setCellHorizontalAlignment(searchButton, HasHorizontalAlignment.ALIGN_RIGHT);
		searchButton.addClickHandler(new SearchButtonClickHandler());
		setContentWidget(horizontalPanel1);		
		horizontalPanel1.setSize("258px", "32px");
	}
	private class SearchButtonClickHandler implements ClickHandler
	{
		@Override
		public void onClick(ClickEvent event) 
		{
			searchButton.setVisible(false);
			String targetName = Utilities.stripWhiteSpaces(targetNameTextBox.getText());
			targetNameTextBox.setText(targetName);
			stoneEdge3.getStoneEdge3Service().getTargetRaDec(targetName, new simbadAsyncCallback());
		}
	}
	class simbadAsyncCallback implements AsyncCallback<SimbadReturnInfo>
	{
		@Override
		public void onFailure(Throwable caught) 
		{
			searchButton.setVisible(true);
			targetNameTextBox.setText("");
			stoneEdge3.getStatusTextArea().addStatus(caught.getMessage());
		}
		@Override
		public void onSuccess(SimbadReturnInfo result) 
		{
			searchButton.setVisible(true);
			targetNameTextBox.setText("");
			stoneEdge3.getSe3TabLayoutPanel().getTargetingPanel().getAstroTargetPanel().setTargetInfo(result.getTargetName(), result.getRaString(), result.getDecString());
		}
	}
}
