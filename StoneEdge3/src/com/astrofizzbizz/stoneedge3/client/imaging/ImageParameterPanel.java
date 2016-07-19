package com.astrofizzbizz.stoneedge3.client.imaging;

import com.astrofizzbizz.stoneedge3.client.Se3ProgressBar;
import com.astrofizzbizz.stoneedge3.client.StoneEdge3;
import com.astrofizzbizz.stoneedge3.client.Utilities;
import com.astrofizzbizz.stoneedge3.shared.ImagingReturnInfo;
import com.astrofizzbizz.stoneedge3.shared.ImagingSendInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

public class ImageParameterPanel extends CaptionPanel
{
	private StoneEdge3 stoneEdge3;
	private TextBox shutterTimeTextBox = new TextBox();
	private TextBox binTextBox = new TextBox();
	private TextBox imageNoTextBox = new TextBox();
	private CheckBox darkCheckBox = new CheckBox("");
	private Button takeImageButton = new Button("Take Image");
	private Se3ProgressBar imageExposureProgressBar;
	private ImageExposurePanelTimer imageExposurePanelTimer;
	private HorizontalPanel imageExposureProgressBarPanel;
	private ImagingSendInfo imagingSendInfo = new ImagingSendInfo();
	private StarsServerAtUchicagoEdu starsServerAtUchicagoEdu = null;

	
	String[] debugResponse = {"Star found in field","1.23pixels star size","78.3% Exposure level"};

	public Button getTakeImageButton() {return takeImageButton;}
	
	public ImageParameterPanel(StoneEdge3 stoneEdge3)
	{
		super("Parameters");
		this.stoneEdge3 = stoneEdge3;
		VerticalPanel vp1 = new VerticalPanel();
		Grid parameterGrid = new Grid(5, 3);
		
		parameterGrid.setWidget(0, 0, new Label("Shutter Time: "));
		parameterGrid.setWidget(0, 1, shutterTimeTextBox);
		parameterGrid.setWidget(0, 2, new Label(" sec"));
		shutterTimeTextBox.setValue("10");

		parameterGrid.setWidget(1, 0, new Label("Binning"));
		parameterGrid.setWidget(1, 1, binTextBox);
		binTextBox.setValue("2");
		
		parameterGrid.setWidget(2, 0, new Label("Image No."));
		parameterGrid.setWidget(2, 1, imageNoTextBox);
		imageNoTextBox.setValue("1");
		
		parameterGrid.setWidget(3, 0, new Label("Dark"));
		parameterGrid.setWidget(3, 1, darkCheckBox);
		darkCheckBox.setValue(false);
		
		vp1.add(parameterGrid);
		parameterGrid.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_MIDDLE);
		parameterGrid.getCellFormatter().setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_MIDDLE);
		parameterGrid.getCellFormatter().setVerticalAlignment(2, 1, HasVerticalAlignment.ALIGN_MIDDLE);
		vp1.add(takeImageButton);
		vp1.setCellVerticalAlignment(takeImageButton, HasVerticalAlignment.ALIGN_MIDDLE);
		vp1.setCellHorizontalAlignment(takeImageButton, HasHorizontalAlignment.ALIGN_CENTER);
		takeImageButton.addClickHandler(new TakeImageButtonClickHandler());
		
		imageExposureProgressBarPanel = new HorizontalPanel();
		Label label = new Label("Exposure Progress: ");
		imageExposureProgressBarPanel.add(label);
		imageExposureProgressBarPanel.setCellHorizontalAlignment(label, HasHorizontalAlignment.ALIGN_CENTER);
		imageExposureProgressBarPanel.setCellVerticalAlignment(label, HasVerticalAlignment.ALIGN_MIDDLE);
		imageExposureProgressBar = new Se3ProgressBar(0, 100);
		imageExposureProgressBarPanel.add(imageExposureProgressBar);
		imageExposureProgressBarPanel.setCellVerticalAlignment(imageExposureProgressBar, HasVerticalAlignment.ALIGN_MIDDLE);
		imageExposureProgressBarPanel.setCellHorizontalAlignment(imageExposureProgressBar, HasHorizontalAlignment.ALIGN_CENTER);
		imageExposureProgressBarPanel.setVisible(false);
		
		vp1.add(imageExposureProgressBarPanel);
		vp1.setCellVerticalAlignment(imageExposureProgressBarPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		vp1.setCellHorizontalAlignment(imageExposureProgressBarPanel, HasHorizontalAlignment.ALIGN_CENTER);
		add(vp1);
	}
	public void setupToTakeImage()
	{
		stoneEdge3.getSe3TabLayoutPanel().getImagingPanel().setPngImagePanelVisible(false);
		takeImageButton.setVisible(true);	

	}
	public void saveImage()
	{
		stoneEdge3.getSe3TabLayoutPanel().getImagingPanel().getSaveImagePanel().setVisible(false);
		String newImageName = stoneEdge3.getSe3TabLayoutPanel().getImagingPanel().getSaveImagePanel().getImageNameTextBox().getText();
		newImageName = Utilities.stripWhiteSpaces(newImageName);
		imagingSendInfo.setImageName(newImageName);
		stoneEdge3.getStatusTextArea().addStatus("Saving image...");
		starsServerAtUchicagoEdu = new StarsServerAtUchicagoEdu(imagingSendInfo, stoneEdge3);
//For new Server
		starsServerAtUchicagoEdu.setSaveImagetoStoneEdgeServer(true);
		starsServerAtUchicagoEdu.createDirectories();
	}
	public void advanceImageNumber()
	{
		String imageNoString = Utilities.stripWhiteSpaces(imageNoTextBox.getValue());
		if (!Utilities.isIntNumber(imageNoString)) 
		{
			stoneEdge3.getStatusTextArea().addStatus("Error: Image number is not an integer");
			return;
		}
		else
		{
			int imageNo = Integer.parseInt(imageNoString);
			imageNo = imageNo + 1;
			imageNoTextBox.setValue(Integer.toString(imageNo));
		}
	}
	private class TakeImageButtonClickHandler implements ClickHandler
	{
		@Override
		public void onClick(ClickEvent event) 
		{
			String shutterTimeString = Utilities.stripWhiteSpaces(shutterTimeTextBox.getValue());
			if (!Utilities.isDoubleNumber(shutterTimeString)) 
			{
				stoneEdge3.getStatusTextArea().addStatus("Error: Shutter time is not a number");
				return;
			}
			String binString = Utilities.stripWhiteSpaces(binTextBox.getValue());
			if (!Utilities.isIntNumber(binString)) 
			{
				stoneEdge3.getStatusTextArea().addStatus("Error: Bin setting is not an integer");
				return;
			}
			String imageNoString = Utilities.stripWhiteSpaces(imageNoTextBox.getValue());
			if (!Utilities.isIntNumber(imageNoString)) 
			{
				stoneEdge3.getStatusTextArea().addStatus("Error: Image number is not an integer");
				return;
			}
//			takeImageButton.setVisible(false);
			String imageName = "";
			int idecimal = shutterTimeString.indexOf(".");
			if (idecimal >= 0)
			{
				shutterTimeString = shutterTimeString.replace(".", "p");
			}
			if (darkCheckBox.getValue()) imageName = imageName + "dark_";
			imageName = imageName + stoneEdge3.getSe3TabLayoutPanel().getImagingPanel().getDataPathInfoPanel().getTargetLabel().getText();
			imageName = imageName + "_" + stoneEdge3.getSe3TabLayoutPanel().getImagingPanel().getFilterPanel().getSelectedFilter();
			imageName = imageName + "_" + shutterTimeString + "sec";
			imageName = imageName + "_bin" + binString;
			imageName = imageName + "_" 
					+ stoneEdge3.getSe3TabLayoutPanel().getImagingPanel().getDataPathInfoPanel().getSessionLabel().getText();
			imageName = imageName + "_" 
					+ stoneEdge3.getSe3TabLayoutPanel().getImagingPanel().getDataPathInfoPanel().getUserLabel().getText();
			imageName = imageName + "_num" + imageNoString;
			imageName = imageName + "_seo.fits";
			imageName = imageName.toLowerCase();
			stoneEdge3.getStatusTextArea().addStatus("Taking Image...");
			imagingSendInfo.setBin(Integer.parseInt(binString));
			imagingSendInfo.setDark(darkCheckBox.getValue());
			imagingSendInfo.setDebug(stoneEdge3.isDebug());
			imagingSendInfo.setDebugResponse(debugResponse);
			imagingSendInfo.setImageName(imageName);
			imagingSendInfo.setSession(stoneEdge3.getSe3TabLayoutPanel().getImagingPanel().getDataPathInfoPanel().getSessionLabel().getText());
			imagingSendInfo.setShutterTime(Double.parseDouble(Utilities.stripWhiteSpaces(shutterTimeTextBox.getValue())));
			imagingSendInfo.setTargetName(Utilities.stripWhiteSpaces(stoneEdge3.getSe3TabLayoutPanel().getImagingPanel().getDataPathInfoPanel().getTargetLabel().getText()));
			imagingSendInfo.setUser(stoneEdge3.getSe3TabLayoutPanel().getImagingPanel().getDataPathInfoPanel().getUserLabel().getText());
			imageExposurePanelTimer = new ImageExposurePanelTimer();
			takeImageButton.setVisible(false);	

//			stoneEdge3.getObsCommandBuffer().pauseBuffer();
			stoneEdge3.getStoneEdge3Service().getImage(imagingSendInfo, new ImagingServiceAsyncCallback());
		
		}
	}
	private class ImagingServiceAsyncCallback implements AsyncCallback<ImagingReturnInfo>
	{
		@Override
		public void onFailure(Throwable caught) 
		{
			stoneEdge3.getObsCommandBuffer().continueBuffer();
			stoneEdge3.getStatusTextArea().addStatus("Error: Server error on take image command. " + caught.getMessage());
			imageExposurePanelTimer.cancel();
			takeImageButton.setVisible(true);	
			
		}
		@Override
		public void onSuccess(ImagingReturnInfo result) 
		{
			stoneEdge3.getObsCommandBuffer().continueBuffer();
			stoneEdge3.getStatusTextArea().addStatus("Finished taking image.");
			imageExposurePanelTimer.cancel();
			stoneEdge3.getSe3TabLayoutPanel().getImagingPanel().getPngImagePanel().setStarFoundLabel(result.isStarFoundInField());
			stoneEdge3.getSe3TabLayoutPanel().getImagingPanel().getPngImagePanel().setPixelSizeLabel(result.getStarPixelSize());
			stoneEdge3.getSe3TabLayoutPanel().getImagingPanel().getPngImagePanel().setExposureLevelLabel(result.getExposureLevel());
			stoneEdge3.getSe3TabLayoutPanel().getImagingPanel().setPngImagePanelVisible(true);
			stoneEdge3.getSe3TabLayoutPanel().getImagingPanel().getSaveImagePanel().getImageNameTextBox().setText(result.imagingSendInfo.getImageName());
			stoneEdge3.getSe3TabLayoutPanel().getImagingPanel().getSaveImagePanel().setVisible(true);
		}
		
	}
	private class ImageExposurePanelTimer extends Timer
	{
		double dtime;
		double shutterTime;
		double sign = 1;
		private ImageExposurePanelTimer()
		{
			scheduleRepeating(1000);
			shutterTime = Double.parseDouble(Utilities.stripWhiteSpaces(shutterTimeTextBox.getValue()));
			dtime = shutterTime * 1.0;
			imageExposureProgressBar.setProgress(100);
			imageExposureProgressBarPanel.setVisible(true);
		}

		@Override
		public void run() 
		{
			dtime = dtime - 1 * sign;
			if (dtime >= 0)
			{
				imageExposureProgressBar.setProgress(100.0 * dtime /  shutterTime);
			}
			else
			{
				sign = -sign;
//				cancel();
			}
		}
		@Override
		public void cancel()
		{
			super.cancel();
			imageExposureProgressBarPanel.setVisible(false);
		}
	}

}
