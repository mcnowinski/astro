package com.astrofizzbizz.stoneedge3.client.imaging;

import com.astrofizzbizz.stoneedge3.client.StoneEdge3;
import com.astrofizzbizz.stoneedge3.shared.ImagingSendInfo;
import com.astrofizzbizz.stoneedge3.shared.ObsCommandReturnInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class StarsServerAtUchicagoEdu 
{
	String delim = "/";
	private String yearDirPath;
	private String dateDirPath;
	private String targetDirPath;
	private String fileNamePath;
	private StoneEdge3 stoneEdge3;
	private ImagingSendInfo imagingSendInfo;
	private boolean saveImagetoStoneEdgeServer = true;
	
	public String getYearDirPath() {return yearDirPath;}
	public String getDateDirPath() {return dateDirPath;}
	public String getTargetDirPath() {return targetDirPath;}
	public String getFileNamePath() {return fileNamePath;}
	public boolean isSaveImagetoStoneEdgeServer() {return saveImagetoStoneEdgeServer;}
	public void setSaveImagetoStoneEdgeServer(boolean saveImagetoStoneEdgeServer) {this.saveImagetoStoneEdgeServer = saveImagetoStoneEdgeServer;}
	
	public StarsServerAtUchicagoEdu(ImagingSendInfo imagingSendInfo, StoneEdge3 stoneEdge3)
	{
		this.stoneEdge3 = stoneEdge3;
		this.imagingSendInfo  = imagingSendInfo;
		String year = imagingSendInfo.getSession().substring(0, 4);
		String month = imagingSendInfo.getSession().substring(4, 7).toLowerCase();
		String day = imagingSendInfo.getSession().substring(7, 9);
		String monthNo = "";
		if (month.equals("jan")) monthNo = "01";
		if (month.equals("feb")) monthNo = "02";
		if (month.equals("mar")) monthNo = "03";
		if (month.equals("apr")) monthNo = "04";
		if (month.equals("may")) monthNo = "05";
		if (month.equals("jun")) monthNo = "06";
		if (month.equals("jul")) monthNo = "07";
		if (month.equals("aug")) monthNo = "08";
		if (month.equals("sep")) monthNo = "09";
		if (month.equals("oct")) monthNo = "10";
		if (month.equals("nov")) monthNo = "11";
		if (month.equals("dec")) monthNo = "12";
		yearDirPath = stoneEdge3.getStarsServerdataDirPath() + delim + year;
		dateDirPath = yearDirPath + delim + year + "-" + monthNo + "-" + day;
		targetDirPath = dateDirPath + delim + imagingSendInfo.getTargetName().toLowerCase();
		fileNamePath = targetDirPath + delim + imagingSendInfo.getImageName();
	}
	public void createDirectories() 
	{
		String command = "ssh -q " + stoneEdge3.getStarsServerloginName() + " 'java -jar MakeDirectoryTree.jar " + targetDirPath + "'";
		String[] debugResponse = {targetDirPath + " EXISTS"};
		stoneEdge3.getObsCommandBuffer().add(command, true, stoneEdge3.isDebug(),  debugResponse, new CreateDirectoriesAsyncCallback());
	}
	private void copyImageToStarsServer()
	{
		String localFilePath = stoneEdge3.getWebAppLocalPath() + "/tempImages/temp.fits";
		String command = "scp -q " + localFilePath + " " + stoneEdge3.getStarsServerloginName() + ":" + fileNamePath;
//		stoneEdge3.getStatusTextArea().addStatus(command);

		stoneEdge3.getObsCommandBuffer().add(command, false, stoneEdge3.isDebug(),  null, new CopyImageToStarsServerAsyncCallback());
	}
	private void saveImageToStoneEdge()
	{
		stoneEdge3.getStoneEdge3Service().saveImagetoStoneEdgeServer(imagingSendInfo, new CopyImageToStoneEdgeServerAsyncCallback());
	}
	class CreateDirectoriesAsyncCallback implements AsyncCallback<ObsCommandReturnInfo>
	{
		@Override
		public void onFailure(Throwable caught) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			stoneEdge3.getStatusTextArea().addStatus("Error: Failed to create directories on STARS server.");
			stoneEdge3.getSe3TabLayoutPanel().getImagingPanel().getImageParameterPanel().setupToTakeImage();
		}
		@Override
		public void onSuccess(ObsCommandReturnInfo result) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			stoneEdge3.getStatusTextArea().addStatus("Created Image directories on STARS server :" + result.getResponse()[0]);
			copyImageToStarsServer();			
		}
	}
	class CopyImageToStarsServerAsyncCallback implements AsyncCallback<ObsCommandReturnInfo>
	{
		@Override
		public void onFailure(Throwable caught) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			stoneEdge3.getStatusTextArea().addStatus("Error: Failed to copy image to STARS server. " + caught.getMessage());
			stoneEdge3.getSe3TabLayoutPanel().getImagingPanel().getImageParameterPanel().setupToTakeImage();
			if (saveImagetoStoneEdgeServer) saveImageToStoneEdge();
		}
		@Override
		public void onSuccess(ObsCommandReturnInfo result) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			stoneEdge3.getSe3TabLayoutPanel().getImagingPanel().getImageParameterPanel().advanceImageNumber();
			stoneEdge3.getSe3TabLayoutPanel().getImagingPanel().getImageParameterPanel().setupToTakeImage();
			stoneEdge3.getStatusTextArea().addStatus("Copied image to STARS server at " + fileNamePath);
			if (saveImagetoStoneEdgeServer) saveImageToStoneEdge();
		}
	}
	class CopyImageToStoneEdgeServerAsyncCallback implements AsyncCallback<String[]>
	{
		@Override
		public void onFailure(Throwable caught) 
		{
			stoneEdge3.getStatusTextArea().addStatus("Error: Failed to copy image to StoneEdge server. " + caught.getMessage());
		}

		@Override
		public void onSuccess(String[] result) 
		{
			stoneEdge3.getStatusTextArea().addStatus("Copied image to StoneEdge server at " + result[0]);
		}
		
	}
}
