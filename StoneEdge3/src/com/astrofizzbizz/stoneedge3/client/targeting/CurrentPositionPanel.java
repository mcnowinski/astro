package com.astrofizzbizz.stoneedge3.client.targeting;

import com.astrofizzbizz.stoneedge3.client.EnterTextDialog.EnterTextDialogInterface;
import com.astrofizzbizz.stoneedge3.client.StoneEdge3;
import com.astrofizzbizz.stoneedge3.client.Utilities;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;

public class CurrentPositionPanel extends CaptionPanel implements EnterTextDialogInterface
{
	private Label currentTargetLabel;
	private Button editTargetButton = new Button("Edit");
	private StoneEdge3 stoneEdge3;
	public CurrentPositionPanel(StoneEdge3 stoneEdge3, Label currentTargetLabel, Label currentRaLabel, Label currentDecLabel)
	{
		super("Current Position");
		this.currentTargetLabel = currentTargetLabel;
		this.stoneEdge3 = stoneEdge3;
		Grid currentPositionGrid = new Grid(3, 3);
		currentPositionGrid.setWidget(0, 0, new Label("Name: "));
		currentPositionGrid.setWidget(1, 0, new Label("R.A. "));
		currentPositionGrid.setWidget(2, 0, new Label("Dec. "));

		currentPositionGrid.setWidget(0, 1, currentTargetLabel);
		currentPositionGrid.setWidget(1, 1, currentRaLabel);
		currentPositionGrid.setWidget(2, 1, currentDecLabel);

		currentPositionGrid.setWidget(0, 2, editTargetButton);
		editTargetButton.addClickHandler(new EditButtonClickHandler(this));
		setContentWidget(currentPositionGrid);

	}
	private class EditButtonClickHandler implements ClickHandler
	{
		CurrentPositionPanel currentPositionPanel;
		private EditButtonClickHandler(CurrentPositionPanel currentPositionPanel)
		{
			this.currentPositionPanel = currentPositionPanel;
		}

		@Override
		public void onClick(ClickEvent event) 
		{
			editTargetButton.setVisible(false);
			int ix = event.getClientX();
			int iy = event.getClientY();
			stoneEdge3.getEnterTextDialog().setOption("New Current Position Name", "Name: ", currentPositionPanel.currentTargetLabel.getText(), "", currentPositionPanel, ix, iy);
		}
	}
	@Override
	public void enterTextDialogChoice(String enterText, boolean useText) 
	{
		if (useText) 
		{
			currentTargetLabel.setText(Utilities.stripWhiteSpaces(enterText));
		}
		editTargetButton.setVisible(true);
	}

}
