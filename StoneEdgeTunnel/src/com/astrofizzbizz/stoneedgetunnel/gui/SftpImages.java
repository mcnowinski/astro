package com.astrofizzbizz.stoneedgetunnel.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Date;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;


import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SftpImages extends JFrame 
{
	private static final long serialVersionUID = 5920082568096002668L;
	private static final String delim = System.getProperty("file.separator");
	Session session;
	ChannelSftp channelSftp = null;
	String homeDirectory = ".";
	private JTree tree;
	DefaultMutableTreeNode top = null;
	JButton downloadButton = null;
	String selectedUser = null;
	String selectedObsDir = null;
	String selectedFile = null;
	int selectedTreeLevel = -1;
    private JTextArea statusTextArea;
	DefaultMutableTreeNode selectedNode = null;
	StoneEdgeTunnel stoneEdgeTunnelFrame = null;
	boolean frameClosed = false;
	File lastOutputDirectory = null;
	Point parentLocation = null;
	
	public SftpImages(Session session, String homeDirectory, StoneEdgeTunnel stoneEdgeTunnelFrameParent)  throws SftpImagesException 
	{
		super();
		this.session = session;
		this.homeDirectory = homeDirectory;
		this.stoneEdgeTunnelFrame = stoneEdgeTunnelFrameParent;
		try 
        {
            UIManager.setLookAndFeel(
            UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {}
        addWindowListener(new java.awt.event.WindowAdapter() 
        {
            public void windowClosing(WindowEvent winEvt) 
            {
            	frameClosed = true; 
            	stoneEdgeTunnelFrame.setLocation(parentLocation);
            }
        });
        
        setIconImage(stoneEdgeTunnelFrame.getIconImage());
		
		setJMenuBar( makeMenu());
		Channel channel;
		try 
		{
			channel = session.openChannel("sftp");
			channel.connect();
		} 
		catch (JSchException e) {throw new SftpImagesException(e.getMessage(), e);}
		channelSftp = (ChannelSftp)channel;
		
		setTitle("Get Images");
	    top = new DefaultMutableTreeNode("Stone Edge");

	    tree = new JTree(top);
	    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	    tree.addTreeSelectionListener(new SftpImagesListeners("treeChanged", this));
	    	    
	    JPanel mainPanel = new JPanel();
	    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		mainPanel.add(treePane());
		mainPanel.add(statusPane());
	    mainPanel.add(downloadButtonPanel());
	    add(mainPanel);
        Dimension minimumSize = new Dimension(500, 600);
        setPreferredSize(minimumSize);
		
        pack();
        downloadButton.setVisible(false);
        
        parentLocation = stoneEdgeTunnelFrame.getLocationOnScreen();
        Dimension psz = stoneEdgeTunnelFrame.getSize();
        stoneEdgeTunnelFrame.setLocation(parentLocation.x - psz.width / 2, parentLocation.y);
        int newy = (getSize().height - psz.height) / 2;
        if (newy < 0 ) newy = -newy;
        setLocation(parentLocation.x + psz.width / 2, parentLocation.y - newy);
//        setLocationRelativeTo(stoneEdgeTunnelFrame);
        setVisible(true);
		
	}
	private JScrollPane statusPane()
	{
		statusTextArea = new JTextArea();
		statusTextArea.setRows(10);
        JScrollPane statusView = new JScrollPane(statusTextArea);
        statusView.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Download Status"),
				BorderFactory.createEmptyBorder(5,5,5,5)));
       Dimension statusViewMinimumSize = new Dimension(500, 150);
//        statusView.setMinimumSize(statusViewMinimumSize);
        statusView.setPreferredSize(statusViewMinimumSize);
       
        return statusView;
	}
	private JScrollPane treePane()
	{

        Dimension treeViewMinimumSize = new Dimension(500, 400);

        JScrollPane treeView = new JScrollPane(tree);
 //       treeView.setMinimumSize(treeViewMinimumSize);
        treeView.setPreferredSize(treeViewMinimumSize);
            
        return treeView;
	}
	private JPanel downloadButtonPanel()
	{
		downloadButton = new JButton("Download");
		downloadButton.addActionListener(new SftpImagesListeners("download", this));
        JPanel downloadButtonPanel = new JPanel(new GridLayout(1,0));
        downloadButtonPanel.add(downloadButton);
		return downloadButtonPanel;

	}
	public void addUsers(DefaultMutableTreeNode top) throws SftpImagesException
	{
		changeDirectory(homeDirectory);
		Vector<LsEntry> usersdir  = getDirectoryListing();
    	for(int ii=0; ii < usersdir.size(); ii++)
    	{
    		LsEntry lsEntry = usersdir.get(ii);
    		if(lsEntry.getAttrs().isDir())
    		{
    			String user = lsEntry.getFilename();
    			if ( !(user.equals("WEB-INF") || user.equals("META-INF") || user.equals("..") || user.equals(".")) )
    			{
    				top.add(new DefaultMutableTreeNode(user));
     			}
    		}
    	}
	}
	public void addObservingDirs(DefaultMutableTreeNode user)  
	{
		changeDirectory(homeDirectory + "/" + user.toString());
		Vector<LsEntry> lsEntry  = getDirectoryListing();
    	for(int ii=0; ii < lsEntry.size(); ii++)
    	{
    		String od = lsEntry.get(ii).getFilename();
			if ( !(od.equals("..") || od.equals(".")) )
			{
				user.add(new DefaultMutableTreeNode(od));
 			}
    	}
	}
	public void addImageNames(DefaultMutableTreeNode obsdir)  
	{
		changeDirectory(homeDirectory + "/" + obsdir.getParent().toString() + "/" + obsdir.toString());
		Vector<LsEntry> lsEntry  = getDirectoryListing();
    	for(int ii=0; ii < lsEntry.size(); ii++)
    	{
    		String image = lsEntry.get(ii).getFilename();
			if ( !(image.equals("..") || image.equals(".")) )
			{
				obsdir.add(new DefaultMutableTreeNode(image));
 			}
    	}
	}
	public void disconnect()
	{
		channelSftp.exit();
	}
	public Vector<LsEntry> getDirectoryListing()  
	{
		String path=".";
		@SuppressWarnings("rawtypes")
		Vector dirVector = new Vector();
		Vector<LsEntry> lsEntryVector = new Vector<LsEntry>();
		try 
		{
			dirVector = channelSftp.ls(path);
		} 
		catch (SftpException e) {new SftpImagesException(e.getMessage()).messageDialog(this) ;}

	    if(dirVector != null)
	    {
	    	for(int ii=0; ii < dirVector.size(); ii++)
	    	{
	    		lsEntryVector.add((LsEntry) dirVector.elementAt(ii));
	    	}
	    }
	    return lsEntryVector;
	}
	public void changeDirectory (String directory) 
	{
		if (directory == null) return;
		try {channelSftp.cd(directory);} 
		catch (SftpException e) {new SftpImagesException(e.getMessage()).messageDialog(this) ;}
	}
	private  JMenuBar makeMenu()
	{
		JMenuBar menuBar = new JMenuBar();
		String menuText[] = {"File", "Help"};
        String itemText[][] =
        {
    		{},
    		{"Help", "About", "Contact"}
    	};

        for (int i = 0; i < menuText.length; i++)
        {
            JMenu menu = new JMenu(menuText[i]);
            menuBar.add (menu);
            
            for (int j = 0; j < itemText[i].length; j++)
            {
                JMenuItem item = new JMenuItem(itemText[i][j]);
                menu.add (item);
                item.addActionListener(new SftpImagesListeners(menuText[i] + "." +itemText[i][j], this));
        		if (itemText[i][j].equals("Exit"))
        		{
        			item.setAccelerator(KeyStroke.getKeyStroke(
        			        KeyEvent.VK_Q, ActionEvent.CTRL_MASK));

        		}
         		if (itemText[i][j].equals("Help"))
        		{
        			item.setAccelerator(KeyStroke.getKeyStroke(
        			        KeyEvent.VK_H, ActionEvent.CTRL_MASK));

        		}
        		if (itemText[i][j].equals("About"))
        		{
        			item.setAccelerator(KeyStroke.getKeyStroke(
        			        KeyEvent.VK_A, ActionEvent.CTRL_MASK));

        		}
            }
        }
        
        return menuBar;
    }
	protected void messageDialog(String string)
	{
		JOptionPane.showMessageDialog(this, string);
	}
	public class SftpImagesException extends Exception 
	{
		private static final long serialVersionUID = 4921756274701482327L;
		public SftpImagesException(String smessage)
		{
			super(smessage);
		}
		public SftpImagesException(String smessage, Throwable cause)
		{
			super(smessage, cause);
		}
		public void messageDialog(JDialog parentDialog)
		{
			JOptionPane.showMessageDialog(parentDialog,"SftpImagesException: " + " " + getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		}
		public void messageDialog(JFrame parentFrame)
		{
			JOptionPane.showMessageDialog(parentFrame,"SftpImagesException: " + " " + getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		}
		public void printErrorMessage()
		{
			System.out.println("SftpImagesException: " + " " + getMessage());
		}
	}
    public File chooseFile() 
    {
    	String dialogTitle = "Pick Location for Download";
    	String delim = System.getProperty("file.separator");
    	String directoryPath = "." + delim;
    	File choosenDirectory = null;
    	if (lastOutputDirectory != null)
    	{
    		directoryPath = lastOutputDirectory.getPath();
    	}
    	JFileChooser fc = null;
		fc = new JFileChooser(directoryPath);
    	fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    	fc.setMultiSelectionEnabled(false);
        fc.setDialogTitle(dialogTitle);
        int returnVal = 0;
        returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) 
        {
        	choosenDirectory = fc.getSelectedFile();
        	lastOutputDirectory = new File(choosenDirectory.getPath());
            return choosenDirectory;
        } 
        else
        {
        	return null;
        }
 	}
	private class SftpImagesListeners implements ActionListener, TreeSelectionListener
	{
		String actionString;
		SftpImages sftpImages;
		private SftpImagesListeners(String actionString, SftpImages sftpImages)
		{
			this.actionString = actionString;
			this.sftpImages = sftpImages;
		}
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			if (actionString.equals("download"))
			{
				File file = chooseFile();
				if (file == null) return;
				TransferFileThread tft = new TransferFileThread(sftpImages, file);
				Thread tftThread = new Thread(tft);
				tftThread.start();
			}
			if (actionString.equals("Help.Help"))
			{
				sftpImages.stoneEdgeTunnelFrame.openHelp();
			}
			if (actionString.equals("Help.About"))
			{
				messageDialog("StoneEdge Tunnel " + stoneEdgeTunnelFrame.version + "\n" + "Last Updated " + stoneEdgeTunnelFrame.versionDate);
			}
			if (actionString.equals("Help.Contact"))
			{
				String message = "Stone Edge Obervatory Coordinator:";
				message = message + "\n          Vivian Hoette of Yerkes Observatory";
				message = message + "\n          email: vhoette@yerkes.uchicago.edu";
				message = message + "\n          phone: 262-215-1599";
				message = message + "\n Program written by:";
				message = message + "\n          Dave McGinnis";
				message = message + "\n          email: dmcginnis427@gmail.com";
				message = message + "\n          phone: 630-457-4205";
				messageDialog(message);
			}
		}
		@Override
		public void valueChanged(TreeSelectionEvent e) 
		{

	       	selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
	        if (selectedNode == null) return;
	        if (selectedNode.getLevel() == 0)
	        {
	        	if (selectedNode.getDepth() == 0)
	        	{
	        	    try {addUsers(top);} 
	        	    catch (SftpImagesException e1) {new SftpImagesException(e1.getMessage()).messageDialog(sftpImages) ;}
	        	    tree.expandRow(0);
	        	}
	        }
	        if (selectedNode.getLevel() == 1)
	        {
	        	if (selectedNode.getDepth() == 0)
	        	{
	    	    	addObservingDirs((DefaultMutableTreeNode) selectedNode);
	        	}
	        }
	        if (selectedNode.getLevel() == 2)
	        {
	        	if (selectedNode.getDepth() == 0)
	        	{
	        		addImageNames((DefaultMutableTreeNode) selectedNode);
	        	}
	        }
	       selectedTreeLevel = selectedNode.getLevel();
	       if (selectedTreeLevel == 0)
	       {
	           downloadButton.setVisible(false);
	    	   selectedUser = null;
	    	   selectedObsDir = null;
	    	   selectedFile = null;
	       }
	       if (selectedTreeLevel == 1)
	       {
	           downloadButton.setVisible(false);
	    	   selectedUser = selectedNode.toString();
	    	   selectedObsDir = null;
	    	   selectedFile = null;
	       }
	       if (selectedTreeLevel == 2)
	       {
	           downloadButton.setVisible(true);
	    	   selectedUser = selectedNode.getParent().toString();
	    	   selectedObsDir = selectedNode.toString();
	    	   selectedFile = null;
	       }
	       if (selectedTreeLevel == 3)
	       {
	           downloadButton.setVisible(true);
	    	   selectedUser = selectedNode.getParent().getParent().toString();
	    	   selectedObsDir = selectedNode.getParent().toString();
	    	   selectedFile = selectedNode.toString();
	       }
		}
	}
	private class TransferFileThread implements Runnable
	{
		JFrame parentFrame;
		File savefile;
		
		private TransferFileThread(JFrame parentFrame, File savefile)
		{
			this.parentFrame = parentFrame;
			this.savefile = savefile;
		}
		@Override
		public void run() 
		{
			downloadButton.setVisible(false);
			if (selectedTreeLevel == 3)
			{
				int mode=ChannelSftp.OVERWRITE;
			    String src = homeDirectory + "/" + selectedUser + "/" + selectedObsDir + "/" + selectedFile;
			    String dst = savefile.getPath() + delim + selectedFile;
			    statusTextArea.insert(new Date().toString() + ": Transferring " + selectedFile + "\n", 0);
			    try {channelSftp.get(src, dst, null, mode);} 
			    catch (SftpException e) {new SftpImagesException(e.getMessage()).messageDialog(parentFrame) ;}
			    statusTextArea.insert("Completed!" + "\n", 0);
			}
			if (selectedTreeLevel == 2)
			{
				File subdirectory = new File(savefile.getPath() + delim + selectedObsDir);
				subdirectory.mkdir();
				int mode=ChannelSftp.OVERWRITE;
				for (int ii = 0; ii < selectedNode.getChildCount(); ++ii)
				{
					String fileName = selectedNode.getChildAt(ii).toString();
				    String src = homeDirectory + "/" + selectedUser + "/" + selectedObsDir + "/" + fileName;
				    String dst = subdirectory.getPath() + delim + fileName;
				    statusTextArea.insert(new Date().toString() + ": Transferring " + fileName + "\n", 0);
				    try {channelSftp.get(src, dst, null, mode);} 
				    catch (SftpException e) {new SftpImagesException(e.getMessage()).messageDialog(parentFrame) ;}
				}
			    statusTextArea.insert("Completed!" + "\n", 0);
			}
			downloadButton.setVisible(true);
		}
	}
}
