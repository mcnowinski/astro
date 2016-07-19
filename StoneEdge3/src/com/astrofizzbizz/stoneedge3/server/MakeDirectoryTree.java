package com.astrofizzbizz.stoneedge3.server;

import java.io.File;
import java.util.ArrayList;

public class MakeDirectoryTree 
{
	static String delim = File.separator;
	public static void main(String[] args) 
	{
		if (args.length < 1) return;
		String dirPath = args[0]; //"C:\\Dropbox\\EclipseWorkSpaceKeplerIBM\\Utilities\\dir0\\dir1\\dir2";
		ArrayList<File> dirTree = new ArrayList<File>();
		String parentPath = dirPath;
		while(parentPath != null)
		{
			File parentFile = new File(parentPath);
			dirTree.add(parentFile);
			parentPath = parentFile.getParent();
		}
		int treeSize = dirTree.size();
		for (int ii = 0; ii < treeSize; ++ii)
		{
			if (!dirTree.get(treeSize - ii - 1).exists()) dirTree.get(treeSize - ii - 1).mkdir();
		}
		try {Thread.sleep(1000);} catch (InterruptedException e) {}
		if (dirTree.get(0).exists())
		{
			System.out.println("Directory " + dirTree.get(0).getPath() + " EXISTS");
		}
		else
		{
			System.out.println("Directory " + dirTree.get(0).getPath() + " DOES NOT EXIST");
		}
	}

}
