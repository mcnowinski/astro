package com.astrofizzbizz.stoneedge3.client.imaging;

import com.astrofizzbizz.stoneedge3.client.StoneEdge3;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

public class SaveImagePanel extends CaptionPanel
{
	private StoneEdge3 stoneEdge3;
	private TextBox imageNameTextBox = new TextBox();
	private Button saveButton = new Button("Save");
	private Button cancelButton = new Button("Cancel");

	public TextBox getImageNameTextBox() {return imageNameTextBox;}
	
	public SaveImagePanel(StoneEdge3 stoneEdge3)
	{
		super("Save Image");
		this.stoneEdge3 = stoneEdge3;
		HorizontalPanel hp1 = new HorizontalPanel();
		HorizontalPanel hp2 = new HorizontalPanel();
		VerticalPanel vp1 = new VerticalPanel();
		vp1.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		imageNameTextBox.setText("emptyimage.fits");
		hp1.add(imageNameTextBox);	
		imageNameTextBox.setWidth("35EM");
		hp2.add(saveButton);	
		hp2.add(cancelButton);	
		vp1.add(hp1);
		vp1.add(hp2);
		
		setContentWidget(vp1);
		vp1.setWidth("100%");
		setVisible(false);
		
		saveButton.addClickHandler(new SaveButtonClickHandler(true));
		cancelButton.addClickHandler(new SaveButtonClickHandler(false));
		
	}
	private class SaveButtonClickHandler implements ClickHandler
	{
		boolean save;
		private SaveButtonClickHandler(boolean save)
		{
			this.save = save;
		}
		@Override
		public void onClick(ClickEvent event) 
		{
			if (save)
			{
				stoneEdge3.getSe3TabLayoutPanel().getImagingPanel().getImageParameterPanel().saveImage();
				return;
			}
			stoneEdge3.getSe3TabLayoutPanel().getImagingPanel().getImageParameterPanel().setupToTakeImage();
			stoneEdge3.getSe3TabLayoutPanel().getImagingPanel().getSaveImagePanel().setVisible(false);
			
		}
		
	}


}
