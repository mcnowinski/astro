package se.esss.litterbox.stoneedgeivgwt.client.gskel;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import se.esss.litterbox.stoneedgeivgwt.client.EntryPointApp;

public class GskelVerticalPanel extends VerticalPanel
{
	private EntryPointApp entryPointApp;
	private ScrollPanel scrollPanel = null;
	boolean scrollable = false;
	
	public EntryPointApp getEntryPointApp() {return entryPointApp;}
	public boolean isScrollable() {return scrollable;}
	public ScrollPanel getScrollPanel() {return scrollPanel;}
	
	public GskelVerticalPanel(boolean scrollable, EntryPointApp entryPointApp)
	{
		super();
		this.entryPointApp = entryPointApp;
		this.scrollable = scrollable;
		if (scrollable)
		{
			scrollPanel = new ScrollPanel();
			scrollPanel.setAlwaysShowScrollBars(true);
			scrollPanel.add(this);
		}
	}
}
