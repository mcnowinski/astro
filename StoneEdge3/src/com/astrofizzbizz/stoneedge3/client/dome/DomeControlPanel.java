package com.astrofizzbizz.stoneedge3.client.dome;

import com.astrofizzbizz.stoneedge3.client.StoneEdge3;
import com.astrofizzbizz.stoneedge3.shared.ObsCommandReturnInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DomeControlPanel extends VerticalPanel
{
	private int panelWidth = 285;
	private int panelHeight = 130;
	StoneEdge3 stoneEdge3;
	private Button openDomeButton = new Button("Open Up");
	private Button closeDomeButton = new Button("Close Down");
	private DomeLightControlPanel domeLightControlPanel;
	private String openDomeCommand = "openup nocloud user=remote";
	private String closeDomeCommand = "closedown user=remote";
	private String[] openDomeDebugResponse = {"2013-05-25T07:22:02Z opening observatory user=remote cloud=-0.08"};
	private String[] closeDomeDebugResponse = {"2013-05-25T07:27:30Z closing observatory user=remote"};

	
	public Button getOpenDomeButton() {return openDomeButton;}
	public Button getCloseDomeButton() {return closeDomeButton;}
	public DomeLightControlPanel getDomeLightControlPanel() {return domeLightControlPanel;}
	public int getPanelWidth() {return panelWidth;}
	public int getPanelHeight() {return panelHeight;}
	
	public DomeControlPanel(StoneEdge3 stoneEdge3, int panelWidth, int panelHeight)
	{
		super();
		this.stoneEdge3 = stoneEdge3;
		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
		setSize(panelWidth + "px", panelHeight+ "px");
		
		CaptionPanel domeControlCaptionPanel = new CaptionPanel("Observatory Control");
		add(domeControlCaptionPanel);
		domeControlCaptionPanel.setSize(panelWidth - 5 + "px", panelHeight - 5 + "px");

		HorizontalPanel domeButtonPanel = new HorizontalPanel();
		domeButtonPanel.setSize(panelWidth - 5 + "px", 50 + "px");

	    domeLightControlPanel = new DomeLightControlPanel(stoneEdge3, 160, 80);
		VerticalPanel vp1 = new VerticalPanel();
		vp1.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		vp1.add(domeButtonPanel);
	    vp1.add(domeLightControlPanel);
	    
	    domeControlCaptionPanel.setContentWidget(vp1);

	    domeButtonPanel.add(openDomeButton);
		domeButtonPanel.setCellVerticalAlignment(openDomeButton, HasVerticalAlignment.ALIGN_MIDDLE);
		domeButtonPanel.setCellHorizontalAlignment(openDomeButton, HasHorizontalAlignment.ALIGN_CENTER);
		domeButtonPanel.add(closeDomeButton);
		domeButtonPanel.setCellVerticalAlignment(closeDomeButton, HasVerticalAlignment.ALIGN_MIDDLE);
		domeButtonPanel.setCellHorizontalAlignment(closeDomeButton, HasHorizontalAlignment.ALIGN_CENTER);
		
		openDomeButton.addClickHandler(new OpenDomeButtonClickHandler());
		closeDomeButton.addClickHandler(new CloseDomeButtonClickHandler());
		openDomeButton.setEnabled(false);
		closeDomeButton.setEnabled(true);
		setVisible(false);
	}
	public void openDome()
	{
		stoneEdge3.getStatusTextArea().addStatus("Opening dome...");
		stoneEdge3.getTelescopeStatusPanel().getTelescopeStatusPanelTimer().setPause(true);
		stoneEdge3.getTelescopeStatusPanel().getKeepTrackingOnTimer().setPause(true);
		stoneEdge3.getObsCommandBuffer().add(openDomeCommand, true, stoneEdge3.isDebug(), openDomeDebugResponse,
				new openDomeAsyncCallback());
		getOpenDomeButton().setEnabled(false);
//		stoneEdge3.getSe3TabLayoutPanel().getDomePanel().getCloseDomeButton().setEnabled(false);
	}
	public void closeDome()
	{
		stoneEdge3.getStatusTextArea().addStatus("Closing dome...");
		stoneEdge3.getTelescopeStatusPanel().setKeepTrackingOn(false);
		stoneEdge3.getObsCommandBuffer().add(closeDomeCommand, true, stoneEdge3.isDebug(), closeDomeDebugResponse,
				new closeDomeAsyncCallback());
		getOpenDomeButton().setEnabled(false);
//		stoneEdge3.getSe3TabLayoutPanel().getDomePanel().getCloseDomeButton().setEnabled(false);
	}
	private class OpenDomeButtonClickHandler implements ClickHandler
	{
		@Override
		public void onClick(ClickEvent event) 
		{
			openDome();
		}
	}
	private class CloseDomeButtonClickHandler implements ClickHandler
	{
		@Override
		public void onClick(ClickEvent event) 
		{
			closeDome();
		}
	}
	class openDomeAsyncCallback implements AsyncCallback<ObsCommandReturnInfo>
	{
		@Override
		public void onFailure(Throwable caught) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			stoneEdge3.getStatusTextArea().addStatus("Error: Server error on open dome command.");
			stoneEdge3.getTelescopeStatusPanel().getTelescopeStatusPanelTimer().setPause(false);
			stoneEdge3.getTelescopeStatusPanel().getKeepTrackingOnTimer().setPause(false);
		}
		@Override
		public void onSuccess(ObsCommandReturnInfo info) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			if (!openDomeCommand.equals(info.getCommand()))
			{
				stoneEdge3.getStatusTextArea().addStatus("Error: open dome command and callback do not match.");
				return;
			}
			int ii = info.getResponse()[0].indexOf("opening observatory");
			if (ii < 0)
			{
				stoneEdge3.getStatusTextArea().addStatus("Error: Server did not send back proper status on open dome");
				return;
			}
			stoneEdge3.getTelescopeStatusPanel().getStatusDebugResponse()[0] = "done slit slit=open";
			stoneEdge3.getTelescopeStatusPanel().getStatusDebugResponse()[1] = "done track ha=11.0000 dec=0.0000";
			stoneEdge3.getTelescopeStatusPanel().setKeepTrackingOn(true);
			stoneEdge3.getTelescopeStatusPanel().keepTrackingOn();
			boolean overRideLock = false;
			stoneEdge3.getTelescopeStatusPanel().refreshTelescopeStatus(overRideLock);
			stoneEdge3.getTelescopeStatusPanel().getTelescopeStatusPanelTimer().setPause(false);
			stoneEdge3.getTelescopeStatusPanel().getKeepTrackingOnTimer().setPause(false);
			stoneEdge3.getStatusTextArea().addStatus("Dome Open Command completed");
		}
	}
	class closeDomeAsyncCallback implements AsyncCallback<ObsCommandReturnInfo>
	{
		@Override
		public void onFailure(Throwable caught) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			stoneEdge3.getStatusTextArea().addStatus("Error: Server error on close dome command.");
			stoneEdge3.getTelescopeStatusPanel().getTelescopeStatusPanelTimer().setPause(false);
			stoneEdge3.getTelescopeStatusPanel().getKeepTrackingOnTimer().setPause(false);
		}
		@Override
		public void onSuccess(ObsCommandReturnInfo info) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			if (!closeDomeCommand.equals(info.getCommand()))
			{
				stoneEdge3.getStatusTextArea().addStatus("Error: close dome command and callback do not match.");
				return;
			}
			int ii = info.getResponse()[0].indexOf("closing observatory");
			if (ii < 0)
			{
				stoneEdge3.getStatusTextArea().addStatus("Error: Server did not send back proper status on close dome");
				return;
			}
			stoneEdge3.getTelescopeStatusPanel().getStatusDebugResponse()[0] = "done slit slit=closed";
			stoneEdge3.getTelescopeStatusPanel().getStatusDebugResponse()[1] = "done track ha=0.0000 dec=0.0000";
//Done because closedown command removes lock
			String[] txClearLockDebugResponse = {"done lock "};
			stoneEdge3.getLockPanel().setTxLockDebugResponse(txClearLockDebugResponse);
			stoneEdge3.getLockPanel().refreshLockStatus(); 
//			refreshTelescopeStatus();
			stoneEdge3.getTelescopeStatusPanel().setUnknownStatus();
			stoneEdge3.getDomeControlPanel().setVisible(false);
			
			stoneEdge3.getTelescopeStatusPanel().getTelescopeStatusPanelTimer().setPause(false);
			stoneEdge3.getTelescopeStatusPanel().getKeepTrackingOnTimer().setPause(false);
			stoneEdge3.getStatusTextArea().addStatus("Close Dome command completed");
			stoneEdge3.getLockDialog().stopDicksDaemon(false); // Turn damenon back on
		}
	}
}
