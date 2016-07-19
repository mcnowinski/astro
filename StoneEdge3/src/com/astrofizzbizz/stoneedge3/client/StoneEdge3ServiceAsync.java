package com.astrofizzbizz.stoneedge3.client;

import com.astrofizzbizz.stoneedge3.shared.ImagingReturnInfo;
import com.astrofizzbizz.stoneedge3.shared.ImagingSendInfo;
import com.astrofizzbizz.stoneedge3.shared.ObsCommandReturnInfo;
import com.astrofizzbizz.stoneedge3.shared.SimbadReturnInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StoneEdge3ServiceAsync 
{
	void readFile(String fileLocation, boolean debug, String[] debugResponse, AsyncCallback<String[]> callback);
	void getTargetRaDec(String targetName, AsyncCallback<SimbadReturnInfo> callback);
	void getImage(ImagingSendInfo imagingSendInfo, AsyncCallback<ImagingReturnInfo> callback);
	void saveImagetoStoneEdgeServer(ImagingSendInfo imagingSendInfo, AsyncCallback<String[]> callback);
	void obsCommand(String command, boolean getInfo, boolean debug, String[] debugResponse, AsyncCallback<ObsCommandReturnInfo> callback);
	void writeFile(String fileLocation, String[] lines, boolean debug, AsyncCallback<String[]> callback);
	void getSessionId(boolean debug, String debugResponse, AsyncCallback<String> callback);
}
