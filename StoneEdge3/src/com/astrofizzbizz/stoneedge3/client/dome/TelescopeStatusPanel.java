package com.astrofizzbizz.stoneedge3.client.dome;

import java.util.Date;

import com.astrofizzbizz.stoneedge3.client.StoneEdge3;
import com.astrofizzbizz.stoneedge3.client.targeting.StarCoordUtilities;
import com.astrofizzbizz.stoneedge3.shared.ObsCommandReturnInfo;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.CaptionPanel;

public class TelescopeStatusPanel extends VerticalPanel
{
	private int panelWidth = 285;
	private int panelHeight = 130;
	private StoneEdge3 stoneEdge3;
	private TelescopeStatusPanelTimer telescopeStatusPanelTimer = null;
	private KeepTrackingOnTimer keepTrackingOnTimer = null;
	private Date updateDate = new Date();
	private boolean domeCanOpen = false;
	private boolean slitOpen = false;
	private boolean trackingOn = false;
	private boolean keepTrackingOn = false;
	private double domeLocation = -1;
	private boolean lampsOn;
	private double raDeg = 0.0;
	private double decDeg = 0.0;
	private double altDeg = 0.0;
	private double azDeg = 0.0;
	private double equinox = 0.0;
	private String localSideralTime = "00:00:00.0";
	private String obsLocalTime = "Sat Jun 15 23:24:59 PDT 2013";
	private String univeralTime = "Sun Jun 16 06:33:48 UTC 2013";

	private String statusCommand = "tx slit; tx track; tx dome; tx lamps; tx where; lst; date; date -u";
	private String keepOpenCommand = "tx slit keepopen; tx track local; tx dome center";
	
	private String[] statusDebugResponse = { 
//			"done slit slit=closed cantopen=sun,nomets",
			"done slit slit=closed",
			"done track ha=0.0000 dec=0.0000",
			"done dome az=313.7 slewing=0",
			"done lamps status one=off two=off three=off four=off",
			"done where ra=01:31:01.10 dec=+00:17:18.1 equinox=2013.434 ha=0.003 secz=1.00 alt=90.0 az=260.7 slewing=0",
			"01:31:00",
			"Sat Jun 15 23:24:59 PDT 2013",
			"Sun Jun 16 06:33:48 UTC 2013"
		};
	private String[] keepOpenDebugResponse = { 
			"done slit slit=open",
			"done track ha=15.0293 dec=-0.0138",
			"done dome az=316.2  main center=270.2 slewing=0",
		};
	
	private Label domeStatusLabel = new Label("Unknown");
	private Label domeLocationLabel = new Label("Unknown");
	private Label slitStatusLabel = new Label("Unknown");
	private Label trackingStatusLabel = new Label("Unknown");
	private Label lampsStatusLabel = new Label("Unknown");

	public boolean isDomeCanOpen() {return domeCanOpen;}
	public boolean isSlitOpen() {return slitOpen;}
	public boolean isTrackingOn() {return trackingOn;}
	public double getDomeLocation() {return domeLocation;}
	public boolean isLampsOn() {return lampsOn;}
	public Label getLampsStatusLabel() {return lampsStatusLabel;}
	public int getPanelWidth() {return panelWidth;}
	public int getPanelHeight() {return panelHeight;}

	public Date getUpdateDate() {return updateDate;}
	public double getRaDeg() {return raDeg;}
	public double getDecDeg() {return decDeg;}
	public double getAltDeg() {return altDeg;}
	public double getAzDeg() {return azDeg;}
	public double getEquinox() {return equinox;}
	public String getLocalSideralTime() {return localSideralTime;}
	public String getObsLocalTime() {return obsLocalTime;}
	public String getUniveralTime() {return univeralTime;}
	public String[] getStatusDebugResponse() {return statusDebugResponse;}
	public TelescopeStatusPanelTimer getTelescopeStatusPanelTimer() {return telescopeStatusPanelTimer;}
	public KeepTrackingOnTimer getKeepTrackingOnTimer() {return keepTrackingOnTimer;}
	public void setLampsOn(boolean lampsOn) {this.lampsOn = lampsOn;}
	public void setKeepTrackingOn(boolean keepTrackingOn) {this.keepTrackingOn = keepTrackingOn;}
	
	public TelescopeStatusPanel(StoneEdge3 stoneEdge3, int updateIntervalmS, int trackingIntervalmS, int panelWidth, int panelHeight)
	{
		super();
		this.stoneEdge3 = stoneEdge3;
		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
		
		CaptionPanel cptnpnlTelescopeStatus = new CaptionPanel("Telescope Status");
//		cptnpnlTelescopeStatus.setSize("22em", "10em");
		setSize(panelWidth + "px", panelHeight+ "px");
		cptnpnlTelescopeStatus.setSize(panelWidth - 5 + "px", panelHeight - 5 + "px");
		
		add(cptnpnlTelescopeStatus);
		Grid statusGrid = new Grid(5, 2);
		cptnpnlTelescopeStatus.setContentWidget(statusGrid);
		statusGrid.setWidget(0, 0, new Label("Dome Status"));
		statusGrid.setWidget(1, 0, new Label("Dome Location"));
		statusGrid.setWidget(2, 0, new Label("Slit Status"));
		statusGrid.setWidget(3, 0, new Label("Tracking Status"));
		statusGrid.setWidget(4, 0, new Label("Lamp Status"));
		
		statusGrid.setWidget(0, 1, domeStatusLabel);
		statusGrid.setWidget(1, 1, domeLocationLabel);
		statusGrid.setWidget(2, 1, slitStatusLabel);
		statusGrid.setWidget(3, 1, trackingStatusLabel);
		statusGrid.setWidget(4, 1, lampsStatusLabel);
				
		telescopeStatusPanelTimer = new TelescopeStatusPanelTimer(updateIntervalmS);
		keepTrackingOnTimer = new KeepTrackingOnTimer(trackingIntervalmS);
	}
	public void refreshTelescopeStatus(boolean overideLock)
	{
		if (!stoneEdge3.getLockPanel().isLockedOwnByMe() && !overideLock) 
		{
			setUnknownStatus();
			return;
		}
		stoneEdge3.getObsCommandBuffer().add(statusCommand, true, stoneEdge3.isDebug(), statusDebugResponse,
				new statusAsyncCallback());
	}
	public void setUnknownStatus()
	{
		domeStatusLabel.setText("Unknown");
		domeLocationLabel.setText("Unknown");
		slitStatusLabel.setText("Unknown");
		trackingStatusLabel.setText("Unknown");
		lampsStatusLabel.setText("Unknown");
		stoneEdge3.getSe3TabLayoutPanel().getTargetingPanel().getSkyc().setPresentRaString(null);
		stoneEdge3.getSe3TabLayoutPanel().getTargetingPanel().getSkyc().setPresentDecString(null);
		stoneEdge3.getSe3TabLayoutPanel().getTargetingPanel().update();
		stoneEdge3.getSe3TabLayoutPanel().getTargetingPanel().setUnknownStatus();
	}
	public void keepTrackingOn()
	{
		if (!keepTrackingOn) return;
		stoneEdge3.getStatusTextArea().addStatus("Keeping Tracking On.");
		stoneEdge3.getObsCommandBuffer().add(keepOpenCommand, true, stoneEdge3.isDebug(), keepOpenDebugResponse,
				new keepOpenAsyncCallback());
	}
	class keepOpenAsyncCallback implements AsyncCallback<ObsCommandReturnInfo>
	{
		@Override
		public void onFailure(Throwable caught) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			stoneEdge3.getStatusTextArea().addStatus("Error: Server error on keep open command.");
		}
		@Override
		public void onSuccess(ObsCommandReturnInfo info) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			if (!keepOpenCommand.equals(info.getCommand()))
			{
				stoneEdge3.getStatusTextArea().addStatus("Error: keep open command and callback do not match.");
				return;
			}
// tx slit keepopen
		    if (info.getResponse()[0].indexOf("done slit") < 0 )
		    {
				stoneEdge3.getStatusTextArea().addStatus("Error: tx slit keepopen not responding");
		    }
		    else 
		    {
		    	statusDebugResponse[0] = "done slit slit=open";
		    }
// tx track local
		    if (info.getResponse()[1].indexOf("done track") < 0 )
		    {
				stoneEdge3.getStatusTextArea().addStatus("Error: tx track local not responding");
		    }
		    else
		    {
		    	statusDebugResponse[1] = "done track ha=11.0000 dec=0.0000";
		    }
// tx dome center
		    if (info.getResponse()[2].indexOf("done dome") < 0 )
		    {
				stoneEdge3.getStatusTextArea().addStatus("Error: tx dome center not responding");
		    }
		}
	}
	class statusAsyncCallback implements AsyncCallback<ObsCommandReturnInfo>
	{
		@Override
		public void onFailure(Throwable caught) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
// tx slit
			slitOpen = false;
			domeCanOpen = false;
			domeStatusLabel.setText("Unknown");
			slitStatusLabel.setText("Unknown");
// tx dome
			domeLocation =  -1001.0;
			domeLocationLabel.setText("Unknown");
// tx track
			trackingOn = false;
			trackingStatusLabel.setText("Unknown");
// tx lamps
			lampsOn = false;
			lampsStatusLabel.setText("Unknown");
// tx where
			raDeg = 0.0;
			decDeg = 0.0;
			altDeg = 0.0;
			azDeg = 0.0;
			equinox = 0.0;
// lst
			localSideralTime = "00:00:00.0";
			
		}
		@Override
		public void onSuccess(ObsCommandReturnInfo info) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			if (!statusCommand.equals(info.getCommand()))
			{
				stoneEdge3.getStatusTextArea().addStatus("Error: status command and callback do not match.");
	// tx slit
				slitOpen = false;
				domeCanOpen = false;
				domeStatusLabel.setText("Unknown");
				slitStatusLabel.setText("Unknown");
	// tx dome
				domeLocation =  -1001.0;
				domeLocationLabel.setText("Unknown");
	// tx track
				trackingOn = false;
				trackingStatusLabel.setText("Unknown");
	// tx lamps
				lampsOn = false;
				lampsStatusLabel.setText("Unknown");
	// tx where
				raDeg = 0.0;
				decDeg = 0.0;
				altDeg = 0.0;
				azDeg = 0.0;
				equinox = 0.0;
	// lst
				localSideralTime = "00:00:00.0";

				return;
			}
// tx slit
			slitOpen = false;
			domeCanOpen = false;
			String[] splitResponse = info.getResponse()[0].split(" ");
			if (splitResponse.length < 3)
			{
				domeStatusLabel.setText("Unknown");
				slitStatusLabel.setText("Unknown");
			}
			else
			{
				slitOpen = true;
				domeCanOpen = true;
				if (splitResponse[2].equals("slit=closed")) slitOpen = false;
				if(!slitOpen)
				{
					int indexCant = info.getResponse()[0].indexOf("cantopen");
					if (indexCant < 0) 
					{
						domeStatusLabel.setText("Can Open");
						domeCanOpen = true;
						stoneEdge3.getDomeControlPanel().getOpenDomeButton().setEnabled(true);
//						stoneEdge3.getSe3TabLayoutPanel().getDomePanel().getCloseDomeButton().setEnabled(false);
					}
					else
					{
						String cantOpenReason = info.getResponse()[0].substring(indexCant + 9);
						domeStatusLabel.setText("CANNOT OPEN: " + cantOpenReason);
						domeCanOpen = false;
						stoneEdge3.getDomeControlPanel().getOpenDomeButton().setEnabled(false);
//						stoneEdge3.getSe3TabLayoutPanel().getDomePanel().getCloseDomeButton().setEnabled(false);
					}
					slitStatusLabel.setText("Closed");
				}
				else
				{
					domeStatusLabel.setText("Open");
					slitStatusLabel.setText("Open");
				}
				if (slitOpen)
				{
					stoneEdge3.getDomeControlPanel().setVisible(true);
					stoneEdge3.getDomeControlPanel().getOpenDomeButton().setEnabled(false);
					stoneEdge3.getDomeControlPanel().getCloseDomeButton().setEnabled(true);
				}
				else
				{
					if (domeCanOpen)
					{
						stoneEdge3.getDomeControlPanel().setVisible(true);
						stoneEdge3.getDomeControlPanel().getOpenDomeButton().setEnabled(true);
//						stoneEdge3.getSe3TabLayoutPanel().getDomePanel().getCloseDomeButton().setEnabled(false);
					}
				}

			}
// tx track			
			trackingOn = false;
			splitResponse = info.getResponse()[1].split(" ");
			if (splitResponse.length < 3)
			{
				trackingOn = false;
				trackingStatusLabel.setText("Unknown");
			}
			else
			{
				trackingOn = false;
				trackingStatusLabel.setText("Off");
				double track = Double.parseDouble(splitResponse[2].substring(3));
				if (track > 10.0) 
				{
					trackingOn = true;
					trackingStatusLabel.setText("On");
				}
			}
// tx dome
			splitResponse = info.getResponse()[2].split(" ");
			if (splitResponse[0].toUpperCase().equals("ERROR"))
			{
				domeLocation =  -1001.0;
				domeLocationLabel.setText("Unknown");
			}
			else if (splitResponse.length < 3)
			{
				domeLocation =  -1001.0;
				domeLocationLabel.setText("Unknown");
			}
			else
			{
				domeLocation = Double.parseDouble(splitResponse[2].substring(3));
				domeLocationLabel.setText(Double.toString(domeLocation));
			}
// tx lamps
			lampsOn = false;
			splitResponse = info.getResponse()[3].split(" ");
			if (splitResponse.length < 3)
			{
				lampsOn = false;
				lampsStatusLabel.setText("Unknown");
			}
			else
			{
				lampsOn = false;
				lampsStatusLabel.setText("Off");
				int indexon = info.getResponse()[0].indexOf("=on");
				if (indexon >= 0) 
				{
					lampsOn = true;
					lampsStatusLabel.setText("On");
				}
			}
// tx where
			stoneEdge3.getSe3TabLayoutPanel().getTargetingPanel().getSkyc().setPresentRaString(null);
			stoneEdge3.getSe3TabLayoutPanel().getTargetingPanel().getSkyc().setPresentDecString(null);
		    if (info.getResponse()[4].indexOf("done") < 0 )
		    {
				stoneEdge3.getStatusTextArea().addStatus("Error: tx where not responding");
		    }
		    else if (info.getResponse()[4].toUpperCase().indexOf("ERROR") >= 0 )
		    {
				stoneEdge3.getStatusTextArea().addStatus("Error: Cannot find where is telescope.");
		    }
		    else
		    {
				splitResponse = info.getResponse()[4].split(" ");
				String raString = splitResponse[2].substring(3);
				String decString = splitResponse[3].substring(4);
				String altString = splitResponse[7].substring(4);
				String azString = splitResponse[8].substring(3);
				String equinoxString = splitResponse[4].substring(8);
				raDeg = StarCoordUtilities.raDeg( raString);
				decDeg = StarCoordUtilities.decDeg(decString);
				altDeg = new Double(altString).doubleValue();
				azDeg = new Double(azString).doubleValue();
				equinox = new Double(equinoxString).doubleValue();
				stoneEdge3.getSe3TabLayoutPanel().getTargetingPanel().getSkyc().setPresentRaString(raString);
				stoneEdge3.getSe3TabLayoutPanel().getTargetingPanel().getSkyc().setPresentDecString(decString);
				
		    }
// lst
			localSideralTime = info.getResponse()[5];
// date
			obsLocalTime = info.getResponse()[6];
// date -u
			univeralTime = info.getResponse()[7];

			updateDate = new Date();
			stoneEdge3.getSe3TabLayoutPanel().getTargetingPanel().update();
		}
		
	}
	class TelescopeStatusPanelTimer extends Timer
	{
		private boolean pause = true;
		private TelescopeStatusPanelTimer(int refresh_interval)
		{
			pause = false;
			scheduleRepeating(refresh_interval);
		}

		@Override
		public void run() 
		{
			if (pause) return;
			updateDate = new Date();
			stoneEdge3.getLockPanel().refreshLockStatus();
			if (stoneEdge3.getLockPanel().isLockedOwnByMe())
			{
				boolean overRideLock = false;
				refreshTelescopeStatus(overRideLock);
			}
			else
			{
				setUnknownStatus();
			}
			
		}
		public void setPause(boolean pause) {this.pause = pause;}
		
	}
	class KeepTrackingOnTimer extends Timer
	{
		private boolean pause = true;
		private KeepTrackingOnTimer(int refresh_interval)
		{
			pause = false;
			scheduleRepeating(refresh_interval);
		}

		@Override
		public void run() 
		{
			if (pause) return;
			keepTrackingOn();
			
		}
		public void setPause(boolean pause) {this.pause = pause;}
		
	}

}
