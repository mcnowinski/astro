package com.astrofizzbizz.stoneedge3.client.imaging;

import com.astrofizzbizz.stoneedge3.client.StoneEdge3;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Image;

public class PngImagePanel extends VerticalPanel
{
	StoneEdge3 stoneEdge3;
	private int version = 1;
	private boolean imageFlipperForDebug = false;
	private Label starFoundLabel = new Label("Star Found in Field");
	private Label pixelSizeLabel = new Label("Star Size 1.23 pixels");
	private Label exposureLevelLabel = new Label("Exposure Level 23.5%");
	Image image;
	public PngImagePanel(StoneEdge3 stoneEdge3)
	{
		super();
		this.stoneEdge3 = stoneEdge3;
		
		image = new Image("tempImages/temp.png?v" + version);
		image.setSize("512px", "512px");
		add(image);
		VerticalPanel vp1 = new VerticalPanel();
		vp1.add(starFoundLabel);
		vp1.add(pixelSizeLabel);
		vp1.add(exposureLevelLabel);
		add(vp1);
	}
	public void setStarFoundLabel(boolean starFound)
	{
		if (starFound) starFoundLabel.setText("Star Found in Field");
		if (!starFound) starFoundLabel.setText("Star NOT Found in Field");
	}
	public void setPixelSizeLabel(double pixelSize)
	{
		pixelSizeLabel.setText("Star Size " + Double.toString(pixelSize) + " pixels");
	}
	public void setExposureLevelLabel(double exposureLevel)
	{
		exposureLevelLabel.setText("Exposure Level " + Double.toString(exposureLevel) + "%");
	}
	public void refreshImage()
	{
		version++;
		String imageUrl = "tempImages/temp.png?v" + version;
		if (stoneEdge3.isDebug())
		{
			if (imageFlipperForDebug)
			{
				imageUrl = "tempImages/temp1.png?v" + version;				
			}
			else
			{
				imageUrl = "tempImages/temp2.png?v" + version;				
			}
		}
		imageFlipperForDebug = !imageFlipperForDebug;
		image.setUrl(imageUrl);
	}
}
