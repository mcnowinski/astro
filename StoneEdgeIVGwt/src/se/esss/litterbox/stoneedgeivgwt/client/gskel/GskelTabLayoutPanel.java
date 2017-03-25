package se.esss.litterbox.stoneedgeivgwt.client.gskel;

import java.util.ArrayList;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class GskelTabLayoutPanel extends TabLayoutPanel
{
	private ArrayList<GskelVerticalPanel> gskelVerticalPanelList = new ArrayList<GskelVerticalPanel>();

	public ArrayList<GskelVerticalPanel> getGskelVerticalPanel() {return gskelVerticalPanelList;}

	public GskelTabLayoutPanel(int barHeightPx) 
	{
		super((double) barHeightPx, Unit.PX);
	}
	public void setSize(int panelWidth, int panelHeight)
	{
		setSize(panelWidth + "px", panelHeight+ "px");
	}
	public void showTab(int itab, boolean showTab)
	{
	    getTabWidget(itab).setVisible(showTab);
	    getTabWidget(itab).getParent().setVisible(showTab);
	}
	public void addGskelVerticalPanel(GskelVerticalPanel gskelVerticalPanel, String tabTitle)
	{
		gskelVerticalPanelList.add(gskelVerticalPanel);
		if (gskelVerticalPanel.isScrollable())
		{
			add(gskelVerticalPanel.getScrollPanel(),tabTitle);
		}
		else
		{
			add(gskelVerticalPanel,tabTitle);
		}
	}

}
