package com.astrofizzbizz.stoneedge3.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import com.astrofizzbizz.stoneedge3.client.StoneEdge3Service;
import com.astrofizzbizz.stoneedge3.shared.ImagingReturnInfo;
import com.astrofizzbizz.stoneedge3.shared.ImagingSendInfo;
import com.astrofizzbizz.stoneedge3.shared.ObsCommandException;
import com.astrofizzbizz.stoneedge3.shared.ObsCommandReturnInfo;
import com.astrofizzbizz.stoneedge3.shared.SimbadReturnInfo;
import com.astrofizzbizz.stoneedge3.shared.StoneEdge3Exception;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class StoneEdge3ServiceImpl extends RemoteServiceServlet implements StoneEdge3Service
{
	String delim = File.separator;
	@Override
	public String[] readFile(String fileLocation, boolean debug, String[] debugResponse) throws StoneEdge3Exception 
	{
		String fileLocationPath = getServletContext().getRealPath(fileLocation);
		String[] outputData = null;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(fileLocationPath));
			String line;
	    	ArrayList<String> outputBuffer = new ArrayList<String>();
			while ((line = br.readLine()) != null) 
			{  
				outputBuffer.add(line);
			}
			br.close();
			int nlines = outputBuffer.size();
			if (nlines < 1) 
			{
				outputData  = new String[1];
				outputData[0] = "";
			}
			else
			{
				outputData = new String[nlines];
				for (int il = 0; il < nlines; ++il)
				{
					outputData[il] = outputBuffer.get(il);
				}
			}
			
		} 
		catch (FileNotFoundException e) {throw new StoneEdge3Exception(e);}
		catch (IOException e) {throw new StoneEdge3Exception(e);}
		return outputData;
	}
	public String[] writeFile(String fileLocation, String[] lines, boolean debug) throws StoneEdge3Exception
	{
		String fileLocationPath = getServletContext().getRealPath(fileLocation);
		if (debug) return null;
		try 
		{
			PrintWriter pw = new PrintWriter(fileLocationPath);
			for (int ii = 0; ii < lines.length; ++ii) pw.println(lines[ii]);
			pw.close();
		} 
		catch (FileNotFoundException e) 
		{
			throw new StoneEdge3Exception(e);
		}
		return null;
	}
	public SimbadReturnInfo getTargetRaDec(String targetName) throws StoneEdge3Exception 
	{
		SimbadReturnInfo simbadReturnInfo = new SimbadReturnInfo();
		simbadReturnInfo.setTargetName(targetName);
// simbad.u-strasbg.fr = 130.79.128.4
		
		String link = "http://130.79.128.4/simbad/sim-id?output.format=ASCII";
		link = link + "&obj.pmsel=off&obj.bibsel=off&obj.fluxsel=off&obj.coo3=off&obj.coo4=off&obj.notesel=off";
		link = link + "&obj.messel=off&obj.sizesel=off&obj.mtsel=off&Ident=";
		link = link + targetName;
		URL urlLink;
		try 
		{
			urlLink = new URL(link);
			URLConnection yc = urlLink.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			String inputLine = in.readLine();
			int ii = 1;
			while ((inputLine != null) && ii < 9) 
			{
				inputLine = in.readLine();
				ii = ii  + 1;
			}
			in.close();	
			if (ii < 9 )
			{
				simbadReturnInfo.setRaString(null);
				simbadReturnInfo.setDecString(null);
				throw new StoneEdge3Exception(targetName + " not found!");
			}
			String[] ra = new String[3];
			String[] dec = new String[3];

			ii = inputLine.lastIndexOf(":") + 2;
			String raDecString = inputLine.substring(ii);
			ii = raDecString.indexOf(" ");
			ra[0] = raDecString.substring(0, ii);
			
			ii = raDecString.indexOf(" ") + 1;
			raDecString = raDecString.substring(ii);
			ii = raDecString.indexOf(" ");
			ra[1] = raDecString.substring(0, ii);
			
			ii = raDecString.indexOf(" ") + 1;
			raDecString = raDecString.substring(ii);
			ii = raDecString.indexOf(" ");
			ra[2] = raDecString.substring(0, ii);
			
			ii = raDecString.indexOf(" ") + 2;
			raDecString = raDecString.substring(ii);
			ii = raDecString.indexOf(" ");
			String decDegTemp = raDecString.substring(0, ii);
			int iplus = decDegTemp.indexOf("+");
			if (iplus < 0)
			{
				iplus = 0;
			}
			else
			{
				iplus = iplus + 1;
			}
			dec[0] = decDegTemp.substring(iplus);
			
			ii = raDecString.indexOf(" ") + 1;
			raDecString = raDecString.substring(ii);
			ii = raDecString.indexOf(" ");
			dec[1] = raDecString.substring(0, ii);

			ii = raDecString.indexOf(" ") + 1;
			raDecString = raDecString.substring(ii);
			ii = raDecString.indexOf(" ");
			dec[2] = raDecString.substring(0, ii);
			
			simbadReturnInfo.setRaString(ra[0] + ":" + ra[1] + ":" + ra[2]);
			simbadReturnInfo.setDecString(dec[0] + ":" + dec[1] + ":" + dec[2]);
		} catch (Exception e) 
		{
			simbadReturnInfo.setRaString(null);
			simbadReturnInfo.setDecString(null);
			throw new StoneEdge3Exception(e);
		}
		return simbadReturnInfo;
	}
	public ImagingReturnInfo getImage(ImagingSendInfo imagingSendInfo) throws StoneEdge3Exception 
	{
		boolean verbose = false;
		ImagingReturnInfo imagingReturnInfo = new ImagingReturnInfo();
		imagingReturnInfo.imagingSendInfo = new ImagingSendInfo(imagingSendInfo) ;
		String tempImageDir = getServletContext().getRealPath("/tempImages");
		String dark = " ";
		if (imagingSendInfo.isDark()) dark = " dark ";
		String takeImageCommand = "image time=" + Double.toString(imagingSendInfo.getShutterTime()) + " bin=" + Integer.toString(imagingSendInfo.getBin()) + dark + "notime outfile=";
//		takeImageCommand = 	takeImageCommand + tempImageDir + delim + "temp" + " notel";		
		takeImageCommand = 	takeImageCommand + tempImageDir + delim + "temp";		
		String[] info = null;
		if (imagingSendInfo.isDebug())
		{
			double sleepy = imagingSendInfo.getShutterTime() * 1200;
			try {Thread.sleep((long) sleepy);} catch (InterruptedException e) {}
			info = imagingSendInfo.getDebugResponse();
		}
		else
		{
			try {info = ServerUtilities.runExternalProcess(takeImageCommand, true, true, verbose);} catch (Exception e) {throw new StoneEdge3Exception(e);}
			if (!new File(tempImageDir + delim + "temp" + ".fits").exists())
				throw new StoneEdge3Exception("Image not taken. Image file does not exist on server");

			String jarDir = getServletContext().getRealPath("/jarFiles");
			String command = "java -jar " + jarDir + delim + "SingleSimpleImageProcess.jar " 
					+ tempImageDir + delim + "temp" + ".fits " 
					+ tempImageDir + delim + "temp.png -exp";
			try {info = ServerUtilities.runExternalProcess(command, true, true, verbose);} catch (Exception e1) {throw new StoneEdge3Exception(e1);}
		}
		imagingReturnInfo.setStarFoundInField(true);
		if (info[0].toLowerCase().indexOf("not") >= 0) imagingReturnInfo.setStarFoundInField(false);
		String pixelSizeString = info[1].substring(0, info[1].indexOf("pixels"));
		imagingReturnInfo.setStarPixelSize(Double.parseDouble(pixelSizeString));
		String exposureString = info[2].substring(0, info[2].indexOf("%"));
		imagingReturnInfo.setExposureLevel(Double.parseDouble(exposureString));
		
		return imagingReturnInfo;
	}
	@Override
	public String[] saveImagetoStoneEdgeServer(ImagingSendInfo imagingSendInfo) throws StoneEdge3Exception 
	{
		boolean verbose = false;
		String tempImageDir = getServletContext().getRealPath("/tempImages");
		String[] info = new String[3];
		info[0] = "/" + imagingSendInfo.getUser() + "/" + imagingSendInfo.getSession() + "/" + imagingSendInfo.getImageName();
		info[1] = "tempImageDir = " + tempImageDir;
		tempImageDir.indexOf("StoneEdge3");
		String dataDirParent = tempImageDir.substring(0, tempImageDir.indexOf("StoneEdge3")) + "StoneEdgeImageData";
		info[2] = "dataDir = " + dataDirParent;
//		String dataDirParent = "/home/remote/tomcat/apache-tomcat-6.0.29/webapps/StoneEdgeImageData";
		if (imagingSendInfo.isDebug()) return info;
		
		File userDir = new File(dataDirParent + delim  + imagingSendInfo.getUser());
		if (!userDir.exists()) userDir.mkdir();
		File imageTotalPath =  new File(dataDirParent + delim  + imagingSendInfo.getUser() + delim + imagingSendInfo.getSession());
		if (!imageTotalPath.exists()) imageTotalPath.mkdir();
		String imageName = dataDirParent + delim  + imagingSendInfo.getUser() 
				+ delim + imagingSendInfo.getSession() + delim + imagingSendInfo.getImageName();
		String moveImageCommand = "cp " + tempImageDir + delim + "temp" + ".fits" + " " + imageName;
		try {ServerUtilities.runExternalProcess(moveImageCommand, true, false, verbose);} 
		catch (Exception e1) {throw new StoneEdge3Exception(e1);}

		return info;
	}
	public ObsCommandReturnInfo obsCommand(String command, boolean getInfo, boolean debug, String[] debugResponse) throws ObsCommandException 
	{
		ObsCommandReturnInfo obsCommandReturnInfo = new ObsCommandReturnInfo();
		obsCommandReturnInfo.setCommand(command);
//		if (debug) throw new ObsCommandException("Booger", obsCommandReturnInfo);
		String[] status = null;
		if (debug)
		{
			status = new String[1];
			status = debugResponse;
			obsCommandReturnInfo.setResponse(status);
			return obsCommandReturnInfo;
		}
    	Process p = null;
    	String[] cmd = {"/bin/sh", "-c", command};
//System.out.println("Here 1");
		try 
		{
			p = Runtime.getRuntime().exec(cmd);
//System.out.println("Here 2");
			if (!getInfo)
			{
				status  = new String[1];
				status[0] = "";
				obsCommandReturnInfo.setResponse(status);
				return obsCommandReturnInfo;
			}
			InputStream iserr = p.getErrorStream();
			InputStreamReader isrerr = new InputStreamReader(iserr);
	    	BufferedReader err = new BufferedReader(isrerr);  
	    	String errline = null;  
    		errline = err.readLine();
			err.close();
			isrerr.close();
			iserr.close();
    		if (errline != null) throw new ObsCommandException(errline, obsCommandReturnInfo);
//System.out.println("Here 3");
			InputStream is = p.getInputStream();
			if (is == null)
			{
				status  = new String[1];
				status[0] = "";
				obsCommandReturnInfo.setResponse(status);
				return obsCommandReturnInfo;
			}
			//System.out.println("Here 4");
			InputStreamReader isr = new InputStreamReader(is);
			
//System.out.println("Here 5");
	    	BufferedReader in = new BufferedReader(isr);  
//System.out.println("Here 6");
	    	String line = null;  
	    	ArrayList<String> outputBuffer = new ArrayList<String>();
			while ((line = in.readLine()) != null) 
			{  
				outputBuffer.add(line);
			}
//System.out.println("Here 7");
			int nlines = outputBuffer.size();
			if (nlines < 1) 
			{
				status  = new String[1];
				status[0] = "";
			}
			else
			{
				status = new String[nlines];
				for (int il = 0; il < nlines; ++il)
				{
					status[il] = outputBuffer.get(il);
				}
			}
			in.close();
			isr.close();
			is.close();
			obsCommandReturnInfo.setResponse(status);
			return obsCommandReturnInfo;
		} 
		catch (IOException e) { throw new ObsCommandException(e, obsCommandReturnInfo);} 
	}
	@Override
	public String getSessionId(boolean debug, String debugResponse)
	{
		return this.getThreadLocalRequest().getSession().getId();
	}
}
