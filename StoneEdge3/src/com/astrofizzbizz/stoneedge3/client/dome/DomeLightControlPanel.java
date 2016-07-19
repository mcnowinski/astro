package com.astrofizzbizz.stoneedge3.client.dome;

import com.astrofizzbizz.stoneedge3.client.Se3ProgressBar;
import com.astrofizzbizz.stoneedge3.client.StoneEdge3;
import com.astrofizzbizz.stoneedge3.shared.ObsCommandReturnInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

public class DomeLightControlPanel extends VerticalPanel
{
	private int panelWidth = 285;
	private int panelHeight = 130;
	private Button onButton;
	private Button offButton;
	private String txLampOnCommand = "tx lamps all=on";
	private String txLampOffCommand = "tx lamps all=off";
	private String[] txLampOnDebugResponse = {"done lamps status one=on two=on three=on four=on"};
	private String[] txLampOffDebugResponse = {"done lamps status one=off two=off three=off four=off"};
	
	StoneEdge3 stoneEdge3;
	Se3ProgressBar lightOnProgressBar;
	DomeLightControlPanelTimer domeLightControlPanelTimer;
	int domeLightControlPanelTimerMaxTime = 60;

	public int getPanelWidth() {return panelWidth;}
	public int getPanelHeight() {return panelHeight;}
	
	public DomeLightControlPanel(StoneEdge3 stoneEdge3, int panelWidth, int panelHeight)
	{
		super();
		this.stoneEdge3 = stoneEdge3;
		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
		setSize(panelWidth + "px", panelHeight+ "px");

		CaptionPanel domeLightControlCaptionPanel = new CaptionPanel("Dome Light");
		domeLightControlCaptionPanel.setSize(panelWidth  - 5 + "px", panelHeight - 5 + "px");
		add(domeLightControlCaptionPanel);
		
		VerticalPanel vp1 = new VerticalPanel();
		vp1.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		domeLightControlCaptionPanel.setContentWidget(vp1);
		vp1.setSize(panelWidth  - 10 + "px", panelHeight - 10 + "px");;
		Grid buttonGrid = new Grid(1, 2);

		vp1.add(buttonGrid);
		
		onButton = new Button("On");
		onButton.addClickHandler(new DomeLightControlPanelClickHandler("onButton"));
		buttonGrid.setWidget(0, 0, onButton);

		offButton = new Button("Off");
		offButton.addClickHandler(new DomeLightControlPanelClickHandler("offButton"));
		buttonGrid.setWidget(0, 1, offButton);
		
		lightOnProgressBar = new Se3ProgressBar(0.0, domeLightControlPanelTimerMaxTime);
		lightOnProgressBar.setVisible(false);
		vp1.add(lightOnProgressBar);
		domeLightControlPanelTimer = new DomeLightControlPanelTimer();
		domeLightControlPanelTimer.cancel();
	}
	class DomeLightControlPanelClickHandler implements ClickHandler
	{
		String actionString;
		DomeLightControlPanelClickHandler(String actionString)
		{
			this.actionString = actionString;
		}
		public void onClick(ClickEvent event) 
		{
			if (actionString.equals("onButton"))
			{
				stoneEdge3.getObsCommandBuffer().add(txLampOnCommand, true, stoneEdge3.isDebug(), 
						txLampOnDebugResponse,
						new txLampOnAsyncCallback());
			}
			if (actionString.equals("offButton"))
			{
				stoneEdge3.getObsCommandBuffer().add(txLampOffCommand, true, stoneEdge3.isDebug(), 
						txLampOffDebugResponse,
						new txLampOffAsyncCallback());
			}
		}
	}
	class txLampOnAsyncCallback implements AsyncCallback<ObsCommandReturnInfo>
	{
		@Override
		public void onFailure(Throwable caught) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			stoneEdge3.getTelescopeStatusPanel().setLampsOn(false);
			stoneEdge3.getTelescopeStatusPanel().getLampsStatusLabel().setText("Unknown");
			stoneEdge3.getStatusTextArea().addStatus("Error: Failed to send turn Lamps on.");
		}
		@Override
		public void onSuccess(ObsCommandReturnInfo info) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			if (!txLampOnCommand.equals(info.getCommand()))
			{
				stoneEdge3.getTelescopeStatusPanel().setLampsOn(false);
				stoneEdge3.getTelescopeStatusPanel().getLampsStatusLabel().setText("Unknown");
				stoneEdge3.getStatusTextArea().addStatus("Error: tx lamps command and callback do not match.");
				return;
			}
			stoneEdge3.getTelescopeStatusPanel().setLampsOn(false);
			String[] splitResponse = info.getResponse()[0].split(" ");
			if (splitResponse.length < 3)
			{
				stoneEdge3.getTelescopeStatusPanel().getLampsStatusLabel().setText("Unknown");
				stoneEdge3.getStatusTextArea().addStatus("Error: Failed to get sufficient status turn Lamps on.");
				return;
			}
			
			stoneEdge3.getTelescopeStatusPanel().setLampsOn(false);
			stoneEdge3.getTelescopeStatusPanel().getLampsStatusLabel().setText("Off");
			int indexon = info.getResponse()[0].indexOf("=on");
			if (indexon >= 0) 
			{
				stoneEdge3.getTelescopeStatusPanel().setLampsOn(true);
				stoneEdge3.getTelescopeStatusPanel().getLampsStatusLabel().setText("On");
				stoneEdge3.getStatusTextArea().addStatus("Dome Lamps have been turned on");
				domeLightControlPanelTimer = new DomeLightControlPanelTimer();
			}
			else
			{
				stoneEdge3.getStatusTextArea().addStatus("Error: Lamps did not turn on");
			}
		}
	}
	class txLampOffAsyncCallback implements AsyncCallback<ObsCommandReturnInfo>
	{
		@Override
		public void onFailure(Throwable caught) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			stoneEdge3.getTelescopeStatusPanel().setLampsOn(true);
			stoneEdge3.getTelescopeStatusPanel().getLampsStatusLabel().setText("Unknown");
			stoneEdge3.getStatusTextArea().addStatus("Error: Failed to send turn Lamps off.");
		}
		@Override
		public void onSuccess(ObsCommandReturnInfo info) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			if (!txLampOffCommand.equals(info.getCommand()))
			{
				stoneEdge3.getTelescopeStatusPanel().setLampsOn(true);
				stoneEdge3.getTelescopeStatusPanel().getLampsStatusLabel().setText("Unknown");
				stoneEdge3.getStatusTextArea().addStatus("Error: tx lamps command and callback do not match.");
				return;
			}
			stoneEdge3.getTelescopeStatusPanel().setLampsOn(true);
			String[] splitResponse = info.getResponse()[0].split(" ");
			if (splitResponse.length < 3)
			{
				stoneEdge3.getTelescopeStatusPanel().getLampsStatusLabel().setText("Unknown");
				stoneEdge3.getStatusTextArea().addStatus("Error: Failed to get sufficient status turn Lamps off.");
				return;
			}
			
			stoneEdge3.getTelescopeStatusPanel().setLampsOn(true);
			stoneEdge3.getTelescopeStatusPanel().getLampsStatusLabel().setText("On");
			int indexon = info.getResponse()[0].indexOf("=off");
			if (indexon >= 0) 
			{
				stoneEdge3.getTelescopeStatusPanel().setLampsOn(false);
				stoneEdge3.getTelescopeStatusPanel().getLampsStatusLabel().setText("Off");
				stoneEdge3.getStatusTextArea().addStatus("Dome Lamps have been turned off");
				domeLightControlPanelTimer.cancel();
			}
			else
			{
				stoneEdge3.getStatusTextArea().addStatus("Error: Lamps did not turn off");
			}
		}
	}
	private class DomeLightControlPanelTimer extends Timer
	{
		int itime;
		private DomeLightControlPanelTimer()
		{
			scheduleRepeating(1000);
			itime = domeLightControlPanelTimerMaxTime;
			lightOnProgressBar.setProgress((double) itime);
			lightOnProgressBar.setVisible(true);
		}

		@Override
		public void run() 
		{
			itime = itime - 1;
			if (itime > 0)
			{
				lightOnProgressBar.setProgress((double) itime);
			}
			else
			{
				cancel();
				stoneEdge3.getObsCommandBuffer().add(txLampOffCommand, true, stoneEdge3.isDebug(), 
						txLampOffDebugResponse,
						new txLampOffAsyncCallback());
			}
		}
		@Override
		public void cancel()
		{
			super.cancel();
			lightOnProgressBar.setVisible(false);
		}
	}

}
