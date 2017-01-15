package com.astrofizzbizz.stoneedge3.client;

import com.astrofizzbizz.stoneedge3.client.dome.DomePanel;
import com.astrofizzbizz.stoneedge3.client.imaging.ImagingPanel;
import com.astrofizzbizz.stoneedge3.client.obscommand.ObsCommandPanel;
import com.astrofizzbizz.stoneedge3.client.targeting.TargetingPanel;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Se3TabLayoutPanel extends TabLayoutPanel
{
	private int panelWidth = 285;
	private int panelHeight = 130;

	ObsCommandPanel obsCommandPanel;
	StoneEdge3 stoneEdge3;
	TargetingPanel targetingPanel;
	ImagingPanel imagingPanel;
	SkyStatusPanel skyStatusPanel;
	UserLogFilePanel userLogFilePanel;
	DomePanel domePanel;
	InfoPanel infoPanel;
	
	int targetingPanelTabId = -1;
	int imagePanelTabId = -1;
	int skyStatusPanelTabId = -1;
	int userLogFilePanelTabId = -1;
	int obsCommandPanelTabId = -1;
	int obsCommandBufferPanelTabId = -1;
	int domePanelTabId = -1;
	int infoPanelTabId = -1;

	public TargetingPanel getTargetingPanel() {return targetingPanel;}
	public ImagingPanel getImagingPanel() {return imagingPanel;}
	public UserLogFilePanel getUserLogFilePanel() {return userLogFilePanel;}
	public DomePanel getDomePanel() {return domePanel;}

	public int getPanelWidth() {return panelWidth;}
	public int getPanelHeight() {return panelHeight;}

	public Se3TabLayoutPanel(int barHeightPx, StoneEdge3 stoneEdge3, int panelWidth, int panelHeight) 
	{
		super((double) barHeightPx, Unit.PX);
		this.stoneEdge3 = stoneEdge3;
		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
		setSize(panelWidth + "px", panelHeight + "px");
		
//	    add(new  DomeCameraPanel(stoneEdge3), "Dome");
		domePanel = new  DomePanel(stoneEdge3);
	    add(domePanel, "Dome");
	    domePanelTabId = 0;
	    
	    targetingPanel = new TargetingPanel(stoneEdge3);
	    add(targetingPanel,"Targeting");
	    targetingPanelTabId = 1;
	    
	    imagingPanel = new ImagingPanel(stoneEdge3);
	    add(imagingPanel,"Imaging");
	    imagePanelTabId = 2;
	    showImagingTab(false);
	    	    
	    skyStatusPanel = new SkyStatusPanel(stoneEdge3);
	    add(skyStatusPanel, "Sky Status");
	    skyStatusPanelTabId = 3;
	    
	    userLogFilePanel = new UserLogFilePanel(stoneEdge3);
	    add(userLogFilePanel, "User Log");
	    userLogFilePanelTabId = 4;
	    
	    add(stoneEdge3.obsCommandBuffer.getObsCommandBufferMainPanel(),"Command Buffer");
	    obsCommandBufferPanelTabId = 5;

//	    obsCommandPanel = new ObsCommandPanel(stoneEdge3);
//	    add(obsCommandPanel, "Command");
//	    obsCommandPanelTabId = 6;
	    
	    infoPanel = new InfoPanel(stoneEdge3);
	    add(infoPanel,"Info");
	    infoPanelTabId = 6;

	    addSelectionHandler(new Se3TabLayoutPanelSelectionHandler(this));
	    	    
	}
	public void setSize(int panelWidth, int panelHeight)
	{
		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
		setSize(panelWidth + "px", panelHeight+ "px");
	}
	public void showImagingTab(boolean showTab) 
	{
		showTab(imagePanelTabId, showTab);
		if (showTab)
		{
			imagingPanel.getFilterPanel().filterStatus();
			imagingPanel.getFocusPanel().focusStatus();
		}
		else
		{
			int itab = getSelectedIndex();
			if (itab == imagePanelTabId) selectDomeTab();
		}
	}
	private void showTab(int itab, boolean showTab)
	{
	    getTabWidget(itab).setVisible(showTab);
	    getTabWidget(itab).getParent().setVisible(showTab);
	}
	public void selectDomeTab() {selectTab(domePanelTabId);}
	class Se3TabLayoutPanelSelectionHandler implements SelectionHandler<Integer>
	{
		Se3TabLayoutPanel se3TabLayoutPanel;
		Se3TabLayoutPanelSelectionHandler(Se3TabLayoutPanel se3TabLayoutPanel)
		{
			this.se3TabLayoutPanel = se3TabLayoutPanel;
		}
		@Override
		public void onSelection(SelectionEvent<Integer> event) 
		{
			  int tabId = event.getSelectedItem();
			  if (tabId == domePanelTabId) 
			  {
				  
			  }
			  if (tabId == imagePanelTabId) 
			  {
				  imagingPanel.getDataPathInfoPanel().updateImageDataPath();
			  }
			  if (tabId == skyStatusPanelTabId) 
			  {
				  skyStatusPanel.update();
			  }
			  if (tabId == userLogFilePanelTabId) 
			  {
				  userLogFilePanel.updateUserLogFile();
			  }
		}
		
	}

}
