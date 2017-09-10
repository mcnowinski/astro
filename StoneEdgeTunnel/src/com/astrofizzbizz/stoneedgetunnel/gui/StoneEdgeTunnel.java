package com.astrofizzbizz.stoneedgetunnel.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;
import javax.swing.UIManager;

import com.astrofizzbizz.stoneedgetunnel.gui.SftpImages.SftpImagesException;
import com.jcraft.jsch.*;

import se.esss.litterbox.utilities.CodeUpdateInformer;
import se.esss.litterbox.utilities.WaitFrame;

public class StoneEdgeTunnel  extends JFrame 
{
	private static final long serialVersionUID = 8463893137035729060L;
	protected String version = "v2.3";
	protected String versionDate = "September 8, 2017";
    //String host = "telescope.stoneedgeobservatory.com";
    String host = "stoneedgelocal.com";
	String user = "remote";
    String imageDirName = "/home/remote/tomcat/webapps/StoneEdgeImageData";
    Session session;
    boolean tunnelConnected = false;
    JLabel screenBackground;
    ClassLoader loader;
    JMenuBar mainMenuBar;
    SftpImages sftpImages = null;
    HelpFrame helpFrame = null;
    
 	public StoneEdgeTunnel() throws IOException
	{
		super("Stone Edge Tunnel");
        try 
        {
            UIManager.setLookAndFeel(
            UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {}
        mainMenuBar = makeMenu();
        setJMenuBar(mainMenuBar);
        findMenuItem(findMenu(mainMenuBar, "Connection"), "Disconnect").setEnabled(false);
        findMenuItem(findMenu(mainMenuBar, "File"), "Transfer Images").setEnabled(false);

//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() 
        {
            public void windowClosing(WindowEvent winEvt) 
            {
            	quitProgram();            
            }
        });
 		loader = Thread.currentThread().getContextClassLoader();
		URL resourceURL = loader.getResource("com/astrofizzbizz/stoneedgetunnel/files/iris.jpg");
		ImageIcon iconImage = new ImageIcon(resourceURL);
        setIconImage(iconImage.getImage());
        screenBackground = new JLabel(iconImage);
        JPanel panel1 = new JPanel();
        panel1.add(screenBackground);
        getContentPane().add(panel1);
        pack();
        setVisible(true);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        
        // Determine the new location of the window
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = (dim.width-w)/2;
        int y = (dim.height-h)/2;
        
        // Move the window
        this.setLocation(x, y);
/*
        Date stopDate = new Date(new Date().getTime() + 1000 * 60 * 60 * 12);
        while (new Date().getTime() < stopDate.getTime())
        {
        	try {Thread.sleep(1000 * 60 * 5);} catch (InterruptedException e) {}
        }
		System.exit(0);
*/
	}
 	private void connect()
 	{
        try {
			makePortsNew();
			tunnelConnected = true;
			URL resourceURL = loader.getResource("com/astrofizzbizz/stoneedgetunnel/files/portal.jpg");
			ImageIcon iconImage = new ImageIcon(resourceURL);
	        setIconImage(iconImage.getImage());
	        screenBackground.setIcon(iconImage);
	        findMenuItem(findMenu(mainMenuBar, "Connection"), "Disconnect").setEnabled(true);
	        findMenuItem(findMenu(mainMenuBar, "File"), "Transfer Images").setEnabled(true);
	        findMenuItem(findMenu(mainMenuBar, "Connection"), "Connect").setEnabled(false);

			JOptionPane.showMessageDialog(this,
				    "Tunnel connected to observatory",
				    "Information",
				    JOptionPane.INFORMATION_MESSAGE);			
        } catch (JSchException e) {
			JOptionPane.showMessageDialog(this,
				    "Could not connect tunnel to observatory\n" + e.getMessage(),
				    "Error",
				    JOptionPane.ERROR_MESSAGE);			
		}
 	}
 	private void disconnect()
 	{
 		if (!tunnelConnected) return;
		session.disconnect();
		tunnelConnected = false;
		URL resourceURL = loader.getResource("com/astrofizzbizz/stoneedgetunnel/files/iris.jpg");
		ImageIcon iconImage = new ImageIcon(resourceURL);
        setIconImage(iconImage.getImage());
        screenBackground.setIcon(iconImage);
        findMenuItem(findMenu(mainMenuBar, "Connection"), "Disconnect").setEnabled(false);
        findMenuItem(findMenu(mainMenuBar, "File"), "Transfer Images").setEnabled(false);
        findMenuItem(findMenu(mainMenuBar, "Connection"), "Connect").setEnabled(true);
		if (sftpImages != null) 
		{
			if (!sftpImages.frameClosed) 
			{
				sftpImages.dispose();
				sftpImages.frameClosed = true;
			}
		}

 	}
	private void makePortsNew() throws JSchException
	{
	    JSch jsch=new JSch();
 
		File privateKey = chooseFile("Open StoneEdge Key");
		if (privateKey == null) throw new JSchException();


        jsch.addIdentity(privateKey.getPath());
//        System.out.println("identity added ");

        session = jsch.getSession(user, host, 22);
//        System.out.println("session created.");

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        session.connect();
//        System.out.println("session connected.....");
		    	
		session.setPortForwardingL(8080, "localhost", 8080);
		session.setPortForwardingL(8081, "localhost", 80);
		session.setPortForwardingL(8082, "axis", 80);
		session.setPortForwardingL(8083, "axis1", 80);
		
	}
    public File chooseFile(String dialogTitle) throws JSchException
    {
    	String delim = System.getProperty("file.separator");
    	String directoryPath = "." + delim;
    	File file = null;
    	JFileChooser fc = null;
		fc = new JFileChooser(directoryPath);
    	fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    	fc.setMultiSelectionEnabled(false);
    	fc.setSelectedFile(new File("StoneEdgeKey"));
        fc.setDialogTitle(dialogTitle);
        int returnVal = 0;
        returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) 
        {
            file = fc.getSelectedFile();
        } 
        if (file == null) throw new JSchException("Invalid Key file");
        return file;
 	}
	private  JMenuBar makeMenu()
	{
		JMenuBar menuBar = new JMenuBar();
		String menuText[] = {"Connection","File", "Help"};
        String itemText[][] =
        {
        	{"Connect","Disconnect","Exit"},
    		{"Transfer Images"},
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
                item.addActionListener(new MenuBarActionListeners(menuText[i] + "." +itemText[i][j]));
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
	JMenu findMenu(JMenuBar menuBar, String menuName)
	{
        MenuElement[] menus =  menuBar.getSubElements();
        for (int ii = 0; ii < menus.length; ++ii)
        {
        	JMenu menu = (JMenu) menus[ii].getComponent();
        	if (menu.getText().equals(menuName)) return menu;
        }
        return null;
	}
	JMenuItem findMenuItem(JMenu menu, String menuItemName)
	{
        if (menu.getItemCount() > 0)
        {
	        for (int ii = 0; ii < menu.getItemCount(); ++ii)
	        {
	        	JMenuItem menuItem = menu.getItem(ii);
	        	if (menuItem.getText().equals(menuItemName)) return menuItem;
	        }
        }
        return null;
	}
	protected void messageDialog(String string)
	{
		JOptionPane.showMessageDialog(this, string);
	}
	protected void quitProgram()
	{
		disconnect();
		System.gc();
//		dispose();
		System.exit(0);
	}
	
	protected void transferImages()  
	{
		if (sftpImages != null) 
		{
			if (!sftpImages.frameClosed) 
			{
				sftpImages.setState(JFrame.NORMAL);
				return;
			}
		}
		try 
		{
			sftpImages = new SftpImages(session, imageDirName, this);
		} catch (SftpImagesException e) 
		{
			e.messageDialog(this);
		} 		
	}
	protected void openHelp()
	{
		if (helpFrame != null) 
		{
			if (!helpFrame.frameClosed) 
			{
				helpFrame.setState(JFrame.NORMAL);
				return;
			}
		}
		try {helpFrame = new HelpFrame(this);} 
		catch (IOException e) {messageDialog("Can't open help file: " + e.getMessage());}
		
	}
	private class MenuBarActionListeners implements ActionListener
	{
		String actionString = "";
		
		MenuBarActionListeners(String actionString)
		{
			this.actionString = actionString;
		}
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			if (actionString.equals("Help.Help"))
			{
				openHelp();
			}
			if (actionString.equals("Help.About"))
			{
				messageDialog("StoneEdge Tunnel " + version + "\n" + "Last Updated " + versionDate);
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
			if (actionString.equals("Connection.Connect"))
			{
				connect();
			}
			if (actionString.equals("Connection.Disconnect"))
			{
				disconnect();
			}
			if (actionString.equals("Connection.Exit"))
			{
				quitProgram();
			}
			if (actionString.equals("File.Transfer Images"))
			{
				transferImages();
			}
		}
		
	}
	public static void main(String[] arg) throws JSchException, IOException 
	{
        String codeURL = "https://drive.google.com/file/d/0B3Hieedgs_7FZ3ZpZGxEVHZ4THM/edit?usp=sharing";  
        String downloadURL = "https://drive.google.com/file/d/0B3Hieedgs_7FZ3ZpZGxEVHZ4THM/edit?usp=sharing";  
        CodeUpdateInformer codeUpdateInformer = new CodeUpdateInformer(codeURL, downloadURL, null);
        if (!codeUpdateInformer.isNewCodeDownloaded())
        {
			try 
			{
				new StoneEdgeTunnel();
			}  catch (Exception e) 
			{
				new WaitFrame("StoneEdgeTunnel", e.getMessage(), null);
			}
        }
	}

 }
