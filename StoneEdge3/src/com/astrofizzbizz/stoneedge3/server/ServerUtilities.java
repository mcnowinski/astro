package com.astrofizzbizz.stoneedge3.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.astrofizzbizz.stoneedge3.shared.StoneEdge3Exception;

public class ServerUtilities 
{
	public static String[] runExternalProcess(String command, boolean linux, boolean getInfo, boolean debug) throws StoneEdge3Exception 
	{
    	Process p = null;
		String[] status = null;
    	String[] cmd = null;
    	if (!linux)
    	{ 
    		cmd = new String[3];
    		cmd[0] = command;
    		cmd[1] = "";
    		cmd[2] = "";
    	}
    	else
    	{
    		cmd = new String[3];
    		cmd[0] = "/bin/sh";
    		cmd[1] = "-c";
    		cmd[2] = command;
    	}
		if (debug) System.out.println("runExternalProcess Input " + cmd[0] + " " + cmd[1] + " "+ cmd[2]);
		try 
		{
			p = Runtime.getRuntime().exec(cmd);
			if (!getInfo)
			{
				status  = new String[1];
				status[0] = "";
				return status;
			}
			InputStream iserr = p.getErrorStream();
			InputStreamReader isrerr = new InputStreamReader(iserr);
	    	BufferedReader err = new BufferedReader(isrerr);  
	    	String errline = null;  
    		errline = err.readLine();
			err.close();
			isrerr.close();
			iserr.close();
			if (errline != null)
			{
				if (debug) System.out.println("runExternalProcess: errline = " + errline);
				throw new StoneEdge3Exception(errline);
			}
			InputStream is = p.getInputStream();
			if (is == null)
			{
				status  = new String[1];
				status[0] = "";
				if (debug) System.out.println("runExternalProcess Output = " + "NO OUTPUT");
				return status;
			}
			InputStreamReader isr = new InputStreamReader(is);
	    	BufferedReader in = new BufferedReader(isr);  
	    	String line = null;  
	    	ArrayList<String> outputBuffer = new ArrayList<String>();
			while ((line = in.readLine()) != null) 
			{  
				outputBuffer.add(line);
			}
			int nlines = outputBuffer.size();
			if (nlines < 1) 
			{
				status  = new String[1];
				status[0] = "";
				if (debug) System.out.println("runExternalProcess Output = " + "NO OUTPUT");
			}
			else
			{
				status = new String[nlines];
				for (int il = 0; il < nlines; ++il)
				{
					status[il] = outputBuffer.get(il);
					if (debug) System.out.println("runExternalProcess Output = " + status[il]);
				}
			}
			in.close();
			isr.close();
			is.close();
			return status;
		} 
		catch (IOException e) { throw new StoneEdge3Exception(e.getMessage());} 

	}

}
