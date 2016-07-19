package com.astrofizzbizz.stoneedge3.client;

import com.astrofizzbizz.stoneedge3.shared.ImagingReturnInfo;
import com.astrofizzbizz.stoneedge3.shared.ImagingSendInfo;
import com.astrofizzbizz.stoneedge3.shared.ObsCommandException;
import com.astrofizzbizz.stoneedge3.shared.ObsCommandReturnInfo;
import com.astrofizzbizz.stoneedge3.shared.SimbadReturnInfo;
import com.astrofizzbizz.stoneedge3.shared.StoneEdge3Exception;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("stoneEdge3")
public interface StoneEdge3Service extends RemoteService
{
	String[] readFile(String fileLocation, boolean debug, String[] debugResponse) throws StoneEdge3Exception;
	SimbadReturnInfo getTargetRaDec(String targetName) throws StoneEdge3Exception;
	ImagingReturnInfo getImage(ImagingSendInfo imagingSendInfo) throws StoneEdge3Exception;
	String[] saveImagetoStoneEdgeServer(ImagingSendInfo imagingSendInfo) throws StoneEdge3Exception;
	ObsCommandReturnInfo obsCommand(String command, boolean getInfo, boolean debug, String[] debugResponse) throws ObsCommandException;
	String[] writeFile(String fileLocation, String[] lines, boolean debug) throws StoneEdge3Exception;
	String getSessionId(boolean debug, String debugResponse);
}
