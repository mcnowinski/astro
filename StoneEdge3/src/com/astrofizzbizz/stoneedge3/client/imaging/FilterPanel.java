package com.astrofizzbizz.stoneedge3.client.imaging;

import com.astrofizzbizz.stoneedge3.client.StoneEdge3;
import com.astrofizzbizz.stoneedge3.shared.ObsCommandReturnInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.RadioButton;

public class FilterPanel extends CaptionPanel
{
	private StoneEdge3 stoneEdge3;
	private String pfilterMoveCommand = null;
	private String[] pfilterMoveDebugResponse = {""};
	private String pfilterStatusCommand = "pfilter";
	private String[] pfilterStatusDebugResponse = {"clear"};
	private String selectedFilter = null;
	
	private RadioButton[] bandRadioButton;
	
	public String getSelectedFilter() {return selectedFilter;}
	
	public FilterPanel(StoneEdge3 stoneEdge3)
	{
		super("Filter");
		this.stoneEdge3 = stoneEdge3;
		
		Grid filterRadioButtonGrid = new Grid(7, 1);
		setContentWidget(filterRadioButtonGrid);
		
		bandRadioButton = new RadioButton[7];
		bandRadioButton[0] = new RadioButton("filterRadioButtonGroup", "U-band (ultraviolet)");
		bandRadioButton[1] = new RadioButton("filterRadioButtonGroup", "G-band (blue)");
		bandRadioButton[2] = new RadioButton("filterRadioButtonGroup", "R-band (green)");
		bandRadioButton[3] = new RadioButton("filterRadioButtonGroup", "I-band (red)");
		bandRadioButton[4] = new RadioButton("filterRadioButtonGroup", "Z-band (infrared)");
		bandRadioButton[5] = new RadioButton("filterRadioButtonGroup", "Clear");
		bandRadioButton[6] = new RadioButton("filterRadioButtonGroup", "H-alpha");
		
		for (int ib = 0; ib < 7; ++ib) filterRadioButtonGrid.setWidget(ib, 0, bandRadioButton[ib]);
		
		bandRadioButton[0].addClickHandler(new FilterRadioButtonClickHandler("u-band", 0));
		bandRadioButton[1].addClickHandler(new FilterRadioButtonClickHandler("g-band", 1));
		bandRadioButton[2].addClickHandler(new FilterRadioButtonClickHandler("r-band", 2));
		bandRadioButton[3].addClickHandler(new FilterRadioButtonClickHandler("i-band", 3));
		bandRadioButton[4].addClickHandler(new FilterRadioButtonClickHandler("z-band", 4));
		bandRadioButton[5].addClickHandler(new FilterRadioButtonClickHandler("clear", 5));
		bandRadioButton[6].addClickHandler(new FilterRadioButtonClickHandler("h-alpha", 6));
		
	}
	public void enableButtons(boolean enable)
	{
		for (int ib = 0; ib < 7; ++ib) 
		{
			bandRadioButton[ib].setValue(false);
			bandRadioButton[ib].setEnabled(enable);
		}
	}
	public void filterStatus()
	{
		stoneEdge3.getObsCommandBuffer().add(pfilterStatusCommand, true, stoneEdge3.isDebug(),  pfilterStatusDebugResponse,
				new PfilterStatusAsyncCallback());
	}
	class FilterRadioButtonClickHandler implements ClickHandler
	{
		String band = null;
		int ibutton = -1;
		FilterRadioButtonClickHandler(String band, int ibutton)
		{
			this.band = band;
			this.ibutton = ibutton;
		}
		@Override
		public void onClick(ClickEvent event) 
		{
			enableButtons(false);
			stoneEdge3.getStatusTextArea().addStatus("Setting Filter to " + band);
			pfilterMoveCommand = "pfilter " + band;
			pfilterMoveDebugResponse[0] = "";
			pfilterStatusDebugResponse[0] = band;
			stoneEdge3.getObsCommandBuffer().add(pfilterMoveCommand, true, stoneEdge3.isDebug(),  pfilterMoveDebugResponse,
					new PfilterMoveAsyncCallback());
		}
	}
	class PfilterStatusAsyncCallback implements AsyncCallback<ObsCommandReturnInfo>
	{
		@Override
		public void onFailure(Throwable caught) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			stoneEdge3.getStatusTextArea().addStatus("Error: Failed to read filter wheel.");
			enableButtons(true);
		}
		@Override
		public void onSuccess(ObsCommandReturnInfo info) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			enableButtons(true);
			if (!pfilterStatusCommand.equals(info.getCommand()))
			{
				stoneEdge3.getStatusTextArea().addStatus("Error: pfilter read command and callback do not match.");
				return;
			}
			if (info.getResponse()[0].indexOf("u-band") >= 0) bandRadioButton[0].setValue(true);
			if (info.getResponse()[0].indexOf("g-band") >= 0) bandRadioButton[1].setValue(true);
			if (info.getResponse()[0].indexOf("r-band") >= 0) bandRadioButton[2].setValue(true);
			if (info.getResponse()[0].indexOf("i-band") >= 0) bandRadioButton[3].setValue(true);
			if (info.getResponse()[0].indexOf("z-band") >= 0) bandRadioButton[4].setValue(true);
			if (info.getResponse()[0].indexOf("clear") >= 0)  bandRadioButton[5].setValue(true);
			if (info.getResponse()[0].indexOf("h-alpha") >= 0)  bandRadioButton[6].setValue(true);
			stoneEdge3.getStatusTextArea().addStatus("Filter at " + info.getResponse()[0]);
			selectedFilter = info.getResponse()[0]; 
		}
	}
	class PfilterMoveAsyncCallback implements AsyncCallback<ObsCommandReturnInfo>
	{
		@Override
		public void onFailure(Throwable caught) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			stoneEdge3.getStatusTextArea().addStatus("Error: Failed to set filter wheel.");
			enableButtons(true);
		}
		@Override
		public void onSuccess(ObsCommandReturnInfo info) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			enableButtons(false);
			if (!pfilterMoveCommand.equals(info.getCommand()))
			{
				stoneEdge3.getStatusTextArea().addStatus("Error: pfilter move command and callback do not match.");
				return;
			}
			int iindex = info.getResponse()[0].indexOf("pfilter");
			if (iindex >= 0) stoneEdge3.getStatusTextArea().addStatus("Error: " + info.getResponse()[0]);
			filterStatus();
		}
	}
}
