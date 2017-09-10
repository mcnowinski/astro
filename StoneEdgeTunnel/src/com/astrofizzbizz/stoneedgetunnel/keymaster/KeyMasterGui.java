package com.astrofizzbizz.stoneedgetunnel.keymaster;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import se.esss.litterbox.utilities.DpmSwingUtilities;
import se.esss.litterbox.utilities.FileReaderUtilities;
import se.esss.litterbox.utilities.WaitFrame;


public class KeyMasterGui extends JFrame
{
	private static final long serialVersionUID = -6805864154932569286L;
	public static final String delim = System.getProperty("file.separator");
	public static final String newline = System.getProperty("line.separator");
	public static DecimalFormat twoPlaces = new DecimalFormat("###.##");
	
	private JMenuBar mainMenuBar;
	private JPanel mainPane;
	protected StatusPanel statusBar;
	ClassLoader loader;

	protected String version = "v2.3";
	protected String versionDate = "September 8, 2017";

    //String host = "telescope.stoneedgeobservatory.com";
    String host = "stoneedgelocal.com";
    String user = "remote";
    Session session;
    boolean tunnelConnected = false;
	String sshdir = "/home/remote/.ssh";
	ChannelSftp channelSftp = null;
    JSch jsch = new JSch();
    ArrayList<PublicKey> keyList = new ArrayList<PublicKey>();
	JScrollPane keyListScrollPane = new JScrollPane();
	JPanel keyListPanel;
	JLabel keyListImageLabel;
  	Properties properties = new Properties();

	public KeyMasterGui(String frametitle, String statusBarTitle, int numStatusLines, String imageIconSource)
	{
		super(frametitle);
		statusBar = new StatusPanel(numStatusLines, statusBarTitle);
		
		loader  = Thread.currentThread().getContextClassLoader();
		ImageIcon  logoIcon = new ImageIcon(loader.getResource(imageIconSource));
        setIconImage(logoIcon.getImage());
        try 
        {
            UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName());
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        
        mainMenuBar = makeMenu();
        setJMenuBar(mainMenuBar);
        DpmSwingUtilities.findMenuItem(DpmSwingUtilities.findMenu(mainMenuBar, "Connection"), "Disconnect").setEnabled(false);
        DpmSwingUtilities.findMenu(mainMenuBar, "Key").setEnabled(false);
        DpmSwingUtilities.findMenuItem(DpmSwingUtilities.findMenu(mainMenuBar, "Key"), "Remove Key").setEnabled(false);
        
		mainPane= new JPanel();
        setupMainPanel();
        
		mainPane.addMouseListener(new MyJFrameMouseListeners());
        getContentPane().setLayout(new BorderLayout(5,5));
		getContentPane().add(mainPane, BorderLayout.CENTER);
		getContentPane().add(statusBar.getScrollPane(), java.awt.BorderLayout.SOUTH);  
		statusBar.setText("Version " + version);
		statusBar.setText("Last Updated " + versionDate);

		pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        
        // Determine the new location of the window
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = (dim.width-w)/2;
        int y = (dim.height-h)/2;
        
        // Move the window
        this.setLocation(x, y);
		setVisible(true);
		statusBar.getScrollPane().setMinimumSize(statusBar.getScrollPane().getSize());
		statusBar.getScrollPane().setPreferredSize(statusBar.getScrollPane().getSize());
		
		try 
		{
			properties.loadFromXML(new FileInputStream(".stoneEdgeKeyMasterConfig.xml"));
		} 
		catch (InvalidPropertiesFormatException e) {} 
		catch (FileNotFoundException e) {} 
		catch (IOException e) {}

        
        addWindowListener(new java.awt.event.WindowAdapter() 
        {
            public void windowClosing(WindowEvent winEvt) 
            {
            	quitProgram();            
            }
        });

	}
	protected void setupMainPanel()
	{
    	keyListPanel = new JPanel();
    	keyListPanel.setLayout(new BoxLayout(keyListPanel, BoxLayout.Y_AXIS));
    	keyListScrollPane = new JScrollPane(keyListPanel);
    	mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
    	keyListImageLabel = new JLabel();
    	keyListImageLabel.setIcon(new ImageIcon(loader.getResource("com/astrofizzbizz/stoneedgetunnel/files/keyMaster.jpg")));
    	keyListImageLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
    	mainPane.add(keyListImageLabel);
    	mainPane.add(keyListScrollPane);

	}
	protected  JMenuBar makeMenu()
	{
		JMenuBar menuBar = new JMenuBar();
		String menuText[] = {"Connection", "Key", "Help"};
        String subMenuText[][] =
        {
        		{"Connect","Disconnect","Exit"},
        		{"Add Key", "Remove Key"},
        		{"Help", "About"}
    	};

        for (int i = 0; i < menuText.length; i++)
        {
            JMenu menu = new JMenu(menuText[i]);
            menuBar.add (menu);
            
            for (int j = 0; j < subMenuText[i].length; j++)
            {
                JMenuItem item = new JMenuItem(subMenuText[i][j]);
                menu.add (item);
                item.addActionListener(new KeyMasterActionListeners(menuText[i] + "." +subMenuText[i][j], this));
        		if (subMenuText[i][j].equals("Open")) item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        		if (subMenuText[i][j].equals("Exit")) item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
         		if (subMenuText[i][j].equals("Help")) item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
         		if (subMenuText[i][j].equals("About")) item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
            }
        }
        
        return menuBar;
    }
	protected void quitProgram()
	{
		dispose();
		System.exit(0);
	}
	private class KeyMasterActionListeners implements ActionListener
	{
		KeyMasterGui keyMasterGui;
		String actionString = "";
		String addedInfo = null;
		KeyMasterActionListeners(String actionString, KeyMasterGui keyMasterGui)
		{
			this.actionString = actionString;
			this.keyMasterGui = keyMasterGui;
		}
		KeyMasterActionListeners(String actionString, String addedInfo, KeyMasterGui keyMasterGui)
		{
			this.actionString = actionString;
			this.keyMasterGui = keyMasterGui;
			this.addedInfo = addedInfo;
		}
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			if (actionString.equals(""))
			{
			}
			if (actionString.equals("Help.About"))
			{
				DpmSwingUtilities.messageDialog("StoneEdge Key Master " + version + "\n" + "Last Updated " + versionDate, keyMasterGui);
			}
			if (actionString.equals("Connection.Connect"))
			{
				connectToStoneEdge();
			}
			if (actionString.equals("Connection.Disconnect"))
			{
				disconnect();
			}
			if (actionString.equals("Connection.Exit"))
			{
				quitProgram();
			}
			if (actionString.equals("Key.Add Key"))
			{
				addKey();
			}
			if (actionString.equals("Key.Remove Key"))
			{
				findAndRemoveKey();
			}
			if (actionString.equals("checkBoxClicked"))
			{
				checkBoxClicked(addedInfo);
			}
			
		}
		
	}
	private class MyJFrameMouseListeners implements MouseListener
	{
		@Override
		public void mouseClicked(MouseEvent arg0) {}
		@Override
		public void mouseEntered(MouseEvent arg0) {}
		@Override
		public void mouseExited(MouseEvent arg0) {}
		@Override
		public void mousePressed(MouseEvent arg0) {}
		@Override
		public void mouseReleased(MouseEvent arg0) {}
	}
	private void connectToStoneEdge() 
	{
	    
		WaitFrame waitFrame = null;
		try 
		{
		    String[] extensions = {"txt"};
			File privateKey = DpmSwingUtilities.chooseFile(
					"." + delim, "Open StoneEdge Key", "", false, extensions, this);
			if (privateKey == null) return;
			waitFrame = new WaitFrame("Wait", "Please wait. Connecting To StoneEdge", this);
	        jsch.addIdentity(privateKey.getPath());
//	        System.out.println("identity added ");

	        session = jsch.getSession(user, host, 22);
//	        System.out.println("session created.");

	        java.util.Properties config = new java.util.Properties();
	        config.put("StrictHostKeyChecking", "no");
	        session.setConfig(config);

	        session.connect();
//	        System.out.println("session connected.....");
			Channel channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp)channel;

			tunnelConnected = true;
			DpmSwingUtilities.findMenuItem(DpmSwingUtilities.findMenu(mainMenuBar, "Connection"), "Disconnect").setEnabled(true);
	        DpmSwingUtilities.findMenuItem(DpmSwingUtilities.findMenu(mainMenuBar, "Connection"), "Connect").setEnabled(false);
	        DpmSwingUtilities.findMenu(mainMenuBar, "Key").setEnabled(true);
			statusBar.setText("StoneEdge connected");

			keyListImageLabel.setVisible(false);
	        readKeyList();
		} catch (JSchException e) 
		{
	        DpmSwingUtilities.messageDialog("Could not connect tunnel to observatory\n" + e.getMessage(), this);
		}
	    finally
	    {
	    	waitFrame.dispose();
	    }

	}
 	private void disconnect()
 	{
 		if (!tunnelConnected) return;
		session.disconnect();
		statusBar.setText("StoneEdge disconnected");

		tunnelConnected = false;
		DpmSwingUtilities.findMenuItem(DpmSwingUtilities.findMenu(mainMenuBar, "Connection"), "Disconnect").setEnabled(false);
        DpmSwingUtilities.findMenuItem(DpmSwingUtilities.findMenu(mainMenuBar, "Connection"), "Connect").setEnabled(true);
        DpmSwingUtilities.findMenu(mainMenuBar, "Key").setEnabled(false);
 		if (keyListScrollPane != null) mainPane.remove(keyListScrollPane);
		keyListImageLabel.setVisible(true);
    	pack();
	    getContentPane().validate();
	    getContentPane().repaint();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        // Determine the new location of the window
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = (dim.width-w)/2;
        int y = (dim.height-h)/2;
        
        // Move the window
        this.setLocation(x, y);
 	}
 	private void readKeyList()
 	{
 		keyList = new ArrayList<PublicKey>();
 		if (keyListScrollPane != null) mainPane.remove(keyListScrollPane);
 		keyListPanel = new JPanel();
 		keyListPanel.setLayout(new BoxLayout(keyListPanel, BoxLayout.Y_AXIS));
        DpmSwingUtilities.findMenuItem(DpmSwingUtilities.findMenu(mainMenuBar, "Key"), "Remove Key").setEnabled(false);

	    try 
	    {
	    	
	    	String src = sshdir + "/" + "authorized_keys";
		    String dst = "authorized_keys";
			int mode=ChannelSftp.OVERWRITE;
	    	channelSftp.get(src, dst, null, mode);
		    String[] keyfile = FileReaderUtilities.readFile(dst);
		    File keyFileFile = new File(dst);
		    keyFileFile.delete();
		    int numKeys = Integer.parseInt(FileReaderUtilities.stripWhiteSpaces(keyfile[0].substring(keyfile[0].lastIndexOf("#") + 1)));
		    int iline = 0;
		    for (int ii = 0; ii < numKeys; ++ii)
		    {
		    	iline = iline + 1;
		    	String userName = keyfile[iline].substring(keyfile[iline].indexOf("\"") + 1, keyfile[iline].lastIndexOf("\""));
		    	iline = iline + 1;
		    	String emailAddress = FileReaderUtilities.stripWhiteSpaces(keyfile[iline].substring(keyfile[iline].lastIndexOf("#") + 1)) ;
		    	iline = iline + 1;
		    	long createDate = Long.parseLong(FileReaderUtilities.stripWhiteSpaces(keyfile[iline].substring(keyfile[iline].lastIndexOf("#") + 1))) ;
		    	iline = iline + 1;
		    	String publicKey = keyfile[iline];
		    	keyList.add(new PublicKey(userName, emailAddress, createDate, publicKey, this));
		    	System.out.println("User    = " + userName);
		    	System.out.println("Email   = " + emailAddress);
		    	System.out.println("Key age = " + twoPlaces.format(keyList.get(ii).keyAgeInDays()));
		    	System.out.println("Key     = " + publicKey);
		    	keyListPanel.add(keyList.get(ii).keyPanel);
		    }
	    	keyListScrollPane = new JScrollPane(keyListPanel);
	    	mainPane.add(keyListScrollPane);
	    	pack();
		    getContentPane().validate();
		    getContentPane().repaint();
	        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	        // Determine the new location of the window
	        int w = this.getSize().width;
	        int h = this.getSize().height;
	        int x = (dim.width-w)/2;
	        int y = (dim.height-h)/2;
	        
	        // Move the window
	        this.setLocation(x, y);
			statusBar.setText("Key List Read");

	    } 
	    catch (SftpException e) 
	    {DpmSwingUtilities.messageDialog("Could not get key file from Stone Edge!" + e.getMessage(), this);} 
	    catch (IOException e) 
	    {
		    DpmSwingUtilities.messageDialog("Could not read key file!" + e.getMessage(), this); 
		}
 	}
 	private void writeKeyList() 
 	{
	    try 
	    {
	 		int numKeys = keyList.size();
	 		if (numKeys < 1) return;
	 		String src = "authorized_keys";
		    String dst = sshdir + "/" + "authorized_keys";
	 		PrintWriter pw = new PrintWriter(src);
	 		pw.println("# " + numKeys);
	 		for (int ii = 0; ii < numKeys; ++ii)
	 		{
	 			pw.println("# " + "\"" + keyList.get(ii).getUserName()+ "\"");
	 			pw.println("# " + keyList.get(ii).getEmailAddress());
	 			pw.println("# " + keyList.get(ii).getCreateDate());
	 			pw.println(keyList.get(ii).getPublicKey());
	 		}
	 		pw.close();
	 		channelSftp.put(src, dst);
		    File keyFileFile = new File(src);
		    keyFileFile.delete();
			statusBar.setText("Key List written.");

	    }
	    catch (IOException e) 
	    {
		    DpmSwingUtilities.messageDialog("Could not write key file!" + e.getMessage(), this); 
		} catch (SftpException e) {
		    DpmSwingUtilities.messageDialog("Could send key file!" + e.getMessage(), this); 
		}
 		
 	}
	@SuppressWarnings("deprecation")
	private void addKey()
	{
		EmailAddressDialog emailAddressDialog = new EmailAddressDialog(this);
		String emailAddress =  emailAddressDialog.getEmailAddress();
		String user =  emailAddressDialog.getUserName();
		emailAddressDialog.dispose();
		if (keyExists(emailAddress))
		{
		    DpmSwingUtilities.messageDialog("Key name already exists\nPlease remove key name from list before adding this key name.\nKey not added!", this); 
		    return;
		}
		System.out.println("emailAddress = " + emailAddress);
		System.out.println("user         = " + user);
		if ((emailAddress == null) || (user == null)) return;

		WaitFrame waitFrame = null;
		try 
		{
			waitFrame = new WaitFrame("Wait", "Please wait. Adding Key", this);
		    
			KeyPair kpair = KeyPair.genKeyPair(jsch, KeyPair.RSA);
			kpair.setPassphrase("");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			kpair.writePrivateKey(bos);
			byte[] privateKeyByteArray = bos.toByteArray();
		    String[] extensions = {"txt"};
			File privateKeyFile = DpmSwingUtilities.chooseFile(
					"." + delim, "Save new key", "StoneEdgeAccessFileForNewUser.txt", true, extensions, this);
			if (privateKeyFile != null)
			{
				FileOutputStream fis = new FileOutputStream(privateKeyFile.getPath());
				fis.write(privateKeyByteArray);
				fis.close();

				bos = new ByteArrayOutputStream();
				kpair.writePublicKey(bos, emailAddress);
				String publicKey = bos.toString("UTF-8");
//				System.out.println("pub key = " + publicKey);

				kpair.dispose();
		    	keyList.add(new PublicKey(user, emailAddress, new Date().getTime(), publicKey, this));
		    	writeKeyList();
		    	readKeyList();
				statusBar.setText("Key added.");
			}
			else
			{
				statusBar.setText("Key add aborted");
			}


		} 
		catch (JSchException e) 
		{DpmSwingUtilities.messageDialog("Could not generate new key!" + e.getMessage(), this);} 
		catch (UnsupportedEncodingException e) {} catch (FileNotFoundException e) 
		{
			{DpmSwingUtilities.messageDialog("Could not generate new key!" + e.getMessage(), this);} 
		} catch (IOException e) 
		{
			{DpmSwingUtilities.messageDialog("Could not generate new key!" + e.getMessage(), this);} 
		}
	    finally
	    {
	    	waitFrame.dispose();
	    }

	}
	private int keyIndex(String emailAddress)
	{
		int keyIndex = -1;
 		int numKeys = keyList.size();
 		if (numKeys < 1) return keyIndex;
 		for (int ii = 0; ii < numKeys; ++ii)
 		{
 			if (keyList.get(ii).getEmailAddress().equals(emailAddress)) return ii;
 		}
 		return -1;
	}
	private boolean keyExists(String emailAddress)
	{
 		if (keyIndex(emailAddress) > -1) return true;
 		return false;
	}
	private void removeKey(int keyIndex)
	{
 		if (keyList.size() < 1) return;
 		
 		int option = DpmSwingUtilities.optionDialog(
 				"Remove Key", 
 				"Remove key for " + keyList.get(keyIndex).getUserName() + " (" + keyList.get(keyIndex).getEmailAddress() + ")", 
 				"Remove", "Cancel", 1, this);
 		if (option > 1) return;
		WaitFrame waitFrame = null;
		try 
		{
			waitFrame = new WaitFrame("Wait", "Please wait. Removing Key", this);
	 		keyList.remove(keyIndex);
	    	writeKeyList();
	    	readKeyList();
			statusBar.setText("Key removed.");
		}
	    finally
	    {
	    	waitFrame.dispose();
	    }
	}
	private void removeKey(String emailAddress)
	{
		int keyIndex = keyIndex(emailAddress);
		if (keyIndex < 0) return;
		removeKey(keyIndex);
	}
	private void checkBoxClicked(String emailAddress)
	{
		int checkBox = keyIndex(emailAddress);
		if (!keyList.get(checkBox).getCheckBox().isSelected())
		{
	        DpmSwingUtilities.findMenuItem(DpmSwingUtilities.findMenu(mainMenuBar, "Key"), "Remove Key").setEnabled(false);
			return;
		}
        DpmSwingUtilities.findMenuItem(DpmSwingUtilities.findMenu(mainMenuBar, "Key"), "Remove Key").setEnabled(true);
 		for (int ii = 0; ii < keyList.size(); ++ii)
 		{
 			keyList.get(ii).getCheckBox().setSelected(false);
 		}
 		keyList.get(checkBox).getCheckBox().setSelected(true);
	}
	private void findAndRemoveKey()
	{
		int checkBox = -1;
 		for (int ii = 0; ii < keyList.size(); ++ii)
 		{
 			if (keyList.get(ii).getCheckBox().isSelected()) checkBox = ii;
 		}
 		if (checkBox < 0) return;
 		removeKey(keyList.get(checkBox).getEmailAddress());
		
	}
	public class PublicKey 
	{
		String emailAddress;
		String userName;
		long createDate;
		String publicKey;
		JPanel keyPanel;
		JCheckBox checkBox;
		KeyMasterGui keyMasterGui;
		
		public PublicKey(String userName, String emailAddress, long createDate, String publicKey, KeyMasterGui keyMasterGui)
		{
			this.emailAddress = emailAddress;
			this.userName = userName;
			this.createDate = createDate;
			this.publicKey = publicKey;
			this.keyMasterGui = keyMasterGui;
			createKeyPanel();
		}
		public double keyAgeInDays()
		{
			long dtl = new Date().getTime() - createDate;
			double dtd = (double) dtl;
			dtd = dtd  / (24 * 3600 * 1000);
			return dtd;
		}
		public void createKeyPanel()
		{
			keyPanel = new JPanel();
			keyPanel.setLayout(new GridLayout(1,4));
			checkBox = new JCheckBox();
			checkBox.setSelected(false);
			JLabel userNameLabel = new JLabel(getUserName() + " ");
			JLabel emailAddressLabel = new JLabel(getEmailAddress() + " ");
			JLabel ageLabel = new JLabel(twoPlaces.format(keyAgeInDays()) + " days old");
			userNameLabel.setForeground(Color.RED);
			emailAddressLabel.setForeground(Color.BLUE);
			ageLabel.setForeground(Color.GREEN.darker());
			keyPanel.add(checkBox);
			keyPanel.add(userNameLabel);
			keyPanel.add(emailAddressLabel);
			keyPanel.add(ageLabel);
			checkBox.addActionListener(new KeyMasterActionListeners("checkBoxClicked", getEmailAddress(), keyMasterGui));

		}
		public String getEmailAddress() {return emailAddress;}
		public String getPublicKey() {return publicKey;}
		public String getUserName() {return userName;}
		public long getCreateDate() {return createDate;}
		public JCheckBox getCheckBox() {return checkBox;}
		public void setEmailAddress(String emailAddress) {this.emailAddress = emailAddress;}
		public void setPublicKey(String publicKey) {this.publicKey = publicKey;}
		public void setUserName(String userName) {this.userName = userName;}
		public void setCreateDate(long createDate) {this.createDate = createDate;}
		
	}
	public static void main(String[] args) 
	{
		new KeyMasterGui("StoneEdge Key Master", "Info", 4, "com/astrofizzbizz/stoneedgetunnel/files/key.jpg");
	}

}
