package se.esss.litterbox.stoneedgeivgwt.client.contentpanels;

import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;

import se.esss.litterbox.stoneedgeivgwt.client.EntryPointApp;
import se.esss.litterbox.stoneedgeivgwt.client.gskel.GskelVerticalPanel;

public class TestPicPanel  extends GskelVerticalPanel
{

	public TestPicPanel(boolean scrollable, EntryPointApp entryPointApp, String imagePath) 
	{
		super(scrollable, entryPointApp);
		Image im = new Image(imagePath);
		im.setSize("1600px", "900px");
//		add(im);
		Frame shiftrFrame = new Frame("https://shiftr.io/dmcginnis427/ess-integration-test-stand/embed?zoom=1");
		shiftrFrame.setSize(705 + "px", 705 + "px");
		add(shiftrFrame);
	}

}
