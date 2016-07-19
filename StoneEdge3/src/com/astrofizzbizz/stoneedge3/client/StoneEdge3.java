package com.astrofizzbizz.stoneedge3.client;

import java.util.Random;

import com.astrofizzbizz.stoneedge3.client.dome.DomeControlPanel;
import com.astrofizzbizz.stoneedge3.client.dome.TelescopeStatusPanel;
import com.astrofizzbizz.stoneedge3.client.lock.LockDialog;
import com.astrofizzbizz.stoneedge3.client.lock.LockPanel;
import com.astrofizzbizz.stoneedge3.client.obscommand.ObsCommandBuffer;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
public class StoneEdge3 implements EntryPoint 
{
	boolean debug = true;
	String version = "v1.4";
	String versionDate = "June 13, 2014";
	int statusintervalSeconds = 60;
	int trackingintervalSeconds = 180;
	int commandBufferDelayMilliSec = 200;
	private String starsServerloginName = "dmcginnis427@stars.uchicago.edu";
	private String starsServerdataDirPath = "/data/images/StoneEdge/0.5meter";
	private String webAppLocalPath = "/home/remote/tomcat/webapps/StoneEdge3"; //For new Server

	Se3TabLayoutPanel se3TabLayoutPanel;
	Se3MenuBar menuBar;
	String userKey;
	StatusTextArea statusTextArea;
	MessageDialog messageDialog;
	TelescopeStatusPanel telescopeStatusPanel;
	LockPanel lockPanel;
	LockDialog lockDialog;
	OptionDialog optionDialog;
	EnterTextDialog enterTextDialog;
	DomeControlPanel domeControlPanel;
	ScrollPanel leftSideBarScrollPanel;
	String menuText[] = {"Help"};
    String itemText[][] =
    {
		{"Help", "About", "Contact"}
	};
    ObsCommandBuffer obsCommandBuffer;
	private double obsLatitude = 38.29341507;
	private double obsLongitude = -122.47058868;
	private double altLimit = 10.0;
	StoneEdge3ServiceAsync stoneEdge3Service = GWT.create(StoneEdge3Service.class);
	
// Display constants
	public int getTelescopeStatusPanelWidth() {return telescopeStatusPanelWidth;}
	public int getTelescopeStatusPanelHeight() {return telescopeStatusPanelHeight;}
	public int getDomeControlPanelHeight() {return domeControlPanelHeight;}
	public int getStatusTextAreaHeight() {return statusTextAreaHeight;}
	public int getSe3TabLayoutPanelHeightBarHeightPx() {return se3TabLayoutPanelHeightBarHeightPx;}

	private int telescopeStatusPanelWidth = 290;
	private int telescopeStatusPanelHeight = 135;
	private int domeControlPanelHeight = 160;
	private int statusTextAreaHeight = 75;
	private int se3TabLayoutPanelHeightBarHeightPx = 30;
	
	public StoneEdge3ServiceAsync getStoneEdge3Service() {return stoneEdge3Service;}
	public boolean isDebug() {return debug;}
	public Se3TabLayoutPanel getSe3TabLayoutPanel() {return se3TabLayoutPanel;}
	public StatusTextArea getStatusTextArea() {return statusTextArea;}
	public MessageDialog getMessageDialog() {return messageDialog;}
	public TelescopeStatusPanel getTelescopeStatusPanel() {return telescopeStatusPanel;}
	public ObsCommandBuffer getObsCommandBuffer() {return obsCommandBuffer;}
	public LockPanel getLockPanel() {return lockPanel;}
	public LockDialog getLockDialog() {return lockDialog;}
	public String getUserKey() {return userKey;}
	public OptionDialog getOptionDialog() {return optionDialog;}
	public EnterTextDialog getEnterTextDialog() {return enterTextDialog;}
	public DomeControlPanel getDomeControlPanel() {return domeControlPanel;}
	public Se3MenuBar getMenuBaR() {return menuBar;}
	public double getObsLatitude() {return obsLatitude;}
	public double getObsLongitude() {return obsLongitude;}
	public String getStarsServerloginName() {return starsServerloginName;}
	public String getStarsServerdataDirPath() {return starsServerdataDirPath;}
	public String getWebAppLocalPath() {return webAppLocalPath;}
	public double getAltLimit() {return altLimit;}

	
	public void onModuleLoad() 
	{
		obsCommandBuffer = new ObsCommandBuffer(this, commandBufferDelayMilliSec);

		statusTextArea = new StatusTextArea(Window.getClientWidth() - 10, statusTextAreaHeight);
	    statusTextArea.setMaxBufferSize(100);
	    statusTextArea.addStatus("Welcome!");
		
		VerticalPanel statusAndControlPanel = new VerticalPanel();
	    lockPanel =  new LockPanel(this, telescopeStatusPanelWidth, telescopeStatusPanelHeight);
	    telescopeStatusPanel =  new TelescopeStatusPanel(this, statusintervalSeconds * 1000, trackingintervalSeconds * 1000, 
	    		telescopeStatusPanelWidth, telescopeStatusPanelHeight);
	    domeControlPanel = new DomeControlPanel(this, telescopeStatusPanelWidth, domeControlPanelHeight);
	    statusAndControlPanel.add(telescopeStatusPanel);
	    statusAndControlPanel.add(lockPanel);
	    statusAndControlPanel.add(domeControlPanel);

		se3TabLayoutPanel = new Se3TabLayoutPanel(se3TabLayoutPanelHeightBarHeightPx, this, se3TabLayoutPanelWidth(), se3TabLayoutPanelHeight());
		
        userKey = "A" + new Integer(new Random().nextInt(9999999)).toString() + "Z";
        statusTextArea.addStatus("UserKey = " + userKey);

        messageDialog = new MessageDialog();
        lockDialog =  new LockDialog(this);
        optionDialog =  new OptionDialog();
        enterTextDialog =  new EnterTextDialog();

		lockPanel.refreshLockStatus();
	    
		VerticalPanel vp1 = new VerticalPanel();
		VerticalPanel vp2 = new VerticalPanel();
		HorizontalPanel hp1 = new HorizontalPanel();
	    Label titleLabel = new Label("StoneEdge 3");
	    titleLabel.setStyleName("titleLabel");
		vp2.add(titleLabel);
		leftSideBarScrollPanel = new ScrollPanel();
		leftSideBarScrollPanel.setAlwaysShowScrollBars(true);

		leftSideBarScrollPanel.add(statusAndControlPanel);
		leftSideBarScrollPanel.setSize(telescopeStatusPanel.getPanelWidth() + "px" + 15, 
				Window.getClientHeight() - statusTextArea.getPanelHeight() - 45 + "px");
		vp2.add(leftSideBarScrollPanel);
		hp1.add(vp2);
		hp1.add(se3TabLayoutPanel);
		vp1.add(hp1);
		vp1.add(statusTextArea);
		RootLayoutPanel.get().add(vp1);

		Window.addResizeHandler(new MyResizeHandler());
	}
	public int se3TabLayoutPanelWidth()
	{
		return Window.getClientWidth() - telescopeStatusPanelWidth - 25 - 15;
	}
	public int se3TabLayoutPanelHeight()
	{
		return Window.getClientHeight() - statusTextAreaHeight - 15;
	}
	public class MyResizeHandler implements ResizeHandler
	{
		@Override
		public void onResize(ResizeEvent event) 
		{
			statusTextArea.setSize(Window.getClientWidth(), statusTextAreaHeight);
			se3TabLayoutPanel.setSize(se3TabLayoutPanelWidth(), se3TabLayoutPanelHeight());			
			leftSideBarScrollPanel.setSize(telescopeStatusPanel.getPanelWidth() + "px" + 15, 
					Window.getClientHeight() - statusTextArea.getPanelHeight() - 45 + "px");
		}
		
	}
}
