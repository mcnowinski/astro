package com.astrofizzbizz.stoneedge3.client.imaging;

import com.astrofizzbizz.stoneedge3.client.Se3TabLayoutScrollPanel;
import com.astrofizzbizz.stoneedge3.client.StoneEdge3;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ImagingPanel extends Se3TabLayoutScrollPanel
{
	private FilterPanel filterPanel;
	private FocusPanel focusPanel;
	private ImageParameterPanel imageParameterPanel;
	private DataPathInfoPanel dataPathInfoPanel;
	private PngImagePanel pngImagePanel;
	private SaveImagePanel saveImagePanel;

	boolean pngImagePanelVisible = false;

	public FilterPanel getFilterPanel() {return filterPanel;}
	public FocusPanel getFocusPanel() {return focusPanel;}
	public ImageParameterPanel getImageParameterPanel() {return imageParameterPanel;}
	public DataPathInfoPanel getDataPathInfoPanel() {return dataPathInfoPanel;}
	public PngImagePanel getPngImagePanel() {return pngImagePanel;}
	public SaveImagePanel getSaveImagePanel() {return saveImagePanel;}

	public boolean isPngImagePanelVisible() {return pngImagePanelVisible;}
	public void setPngImagePanelVisible(boolean pngImagePanelVisible) 
	{
		this.pngImagePanelVisible = pngImagePanelVisible;
		if (pngImagePanelVisible) pngImagePanel.refreshImage();
		pngImagePanel.setVisible(pngImagePanelVisible);
	}

	public ImagingPanel(StoneEdge3 stoneEdge3)
	{
		super(stoneEdge3);
		VerticalPanel mainPanel = new VerticalPanel();
		
		HorizontalPanel hp1 = new HorizontalPanel();
		HorizontalPanel hp2 = new HorizontalPanel();
		VerticalPanel vp1 = new VerticalPanel();
		VerticalPanel vp2 = new VerticalPanel();
		
		filterPanel = new FilterPanel(stoneEdge3);
		focusPanel = new FocusPanel(stoneEdge3);
		imageParameterPanel = new ImageParameterPanel(stoneEdge3);
		dataPathInfoPanel = new DataPathInfoPanel(stoneEdge3);
		pngImagePanel = new PngImagePanel(stoneEdge3);
		saveImagePanel = new SaveImagePanel(stoneEdge3);
		
		vp1.add(filterPanel);
		vp1.add(focusPanel);
		vp1.add(dataPathInfoPanel);
		hp1.add(vp1);
		hp1.add(imageParameterPanel);
		vp2.add(hp1);
		vp2.add(saveImagePanel);
		hp2.add(vp2);
		
		hp2.add(pngImagePanel);
		
		setPngImagePanelVisible(false);
		mainPanel.add(hp2);
		add(mainPanel);
		
	}

}
