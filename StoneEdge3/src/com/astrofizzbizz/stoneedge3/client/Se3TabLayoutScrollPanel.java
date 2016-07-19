package com.astrofizzbizz.stoneedge3.client;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ScrollPanel;

public class Se3TabLayoutScrollPanel extends ScrollPanel
{
	private StoneEdge3 stoneEdge3;

	public StoneEdge3 getStoneEdge3() {return stoneEdge3;}

	public Se3TabLayoutScrollPanel(StoneEdge3 stoneEdge3)
	{
		super();
		this.stoneEdge3 = stoneEdge3;

		setAlwaysShowScrollBars(true);
		setSize(stoneEdge3.se3TabLayoutPanelWidth() - 15 + "px", 
				stoneEdge3.se3TabLayoutPanelHeight() - stoneEdge3.getSe3TabLayoutPanelHeightBarHeightPx() - 15 + "px");
		
		Window.addResizeHandler(new MyResizeHandler());
	}
	public class MyResizeHandler implements ResizeHandler
	{
		@Override
		public void onResize(ResizeEvent event) 
		{
			setSize(stoneEdge3.se3TabLayoutPanelWidth() - 15 + "px", 
					stoneEdge3.se3TabLayoutPanelHeight() - stoneEdge3.getSe3TabLayoutPanelHeightBarHeightPx() - 15 + "px");
		}
		
	}

}
