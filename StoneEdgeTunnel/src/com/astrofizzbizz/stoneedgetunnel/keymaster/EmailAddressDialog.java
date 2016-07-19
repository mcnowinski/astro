package com.astrofizzbizz.stoneedgetunnel.keymaster;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import se.esss.litterbox.utilities.DpmSwingUtilities;
import se.esss.litterbox.utilities.FileReaderUtilities;


public class EmailAddressDialog extends JDialog
{
	private static final long serialVersionUID = -2153159339179283075L;

	KeyMasterGui keyMasterGui;
	private String emailAddress = null;
	private String userName = null;
	private JTextField userNameField     = new JTextField(25);
	private JTextField emailAddressField = new JTextField(25);
	private JButton sendButton = new JButton();
	private JButton cancelButton = new JButton();
	
	protected EmailAddressDialog(KeyMasterGui keyMasterGui)
	{
		super(keyMasterGui, true);
		this.keyMasterGui = keyMasterGui;
		setTitle("Enter Email Address:");

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		String iconLoc = "com/astrofizzbizz/stoneedgetunnel/files/mail.jpg";
		URL resourceURL = loader.getResource(iconLoc);
        setIconImage(new ImageIcon(resourceURL).getImage());
		
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new GridLayout(2,1));
		labelPanel.add(new JLabel("User Name "));
		labelPanel.add(new JLabel("Email Address "));
		
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new GridLayout(2,1));
		textPanel.add(userNameField);
		textPanel.add(emailAddressField);

		JPanel userInfoPanel = new JPanel();
		userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.X_AXIS));
		userInfoPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(""),BorderFactory.createEmptyBorder(5,5,5,5)));
		userInfoPanel.add(labelPanel);
		userInfoPanel.add(textPanel);
		
        sendButton.addActionListener(new EmailAddressDialogActionListeners("sendButton", this));
        cancelButton.addActionListener(new EmailAddressDialogActionListeners("cancelButton", this));
        
        sendButton.setIcon(new ImageIcon(loader.getResource("com/astrofizzbizz/stoneedgetunnel/files/ok.jpg")));
        cancelButton.setIcon(new ImageIcon(loader.getResource("com/astrofizzbizz/stoneedgetunnel/files/cancel.jpg")));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(sendButton);
		buttonPanel.add(cancelButton);
		
		JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(""),BorderFactory.createEmptyBorder(5,5,5,5)));
        mainPanel.add(userInfoPanel);
        mainPanel.add(buttonPanel);
       
		getContentPane().add(mainPanel);
		
        pack();
        setLocationRelativeTo(keyMasterGui);
        setVisible(true);
	}
	public String getEmailAddress() {return emailAddress;}
	public String getUserName() {return userName;}
	public void setEmailAddress(String emailAddress) {this.emailAddress = emailAddress;}
	public void setUserName(String userName) {this.userName = userName;}
	
	private class EmailAddressDialogActionListeners implements ActionListener
	{
		String actionString = "";
		EmailAddressDialog emailAddressDialog;
		private EmailAddressDialogActionListeners(String actionString, EmailAddressDialog emailAddressDialog)
		{
			this.actionString = actionString;
			this.emailAddressDialog = emailAddressDialog;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			if (actionString.equals("sendButton"))
			{
				if (emailAddressField.getText().lastIndexOf("@") > 0)
				{
					setEmailAddress(FileReaderUtilities.stripWhiteSpaces(emailAddressField.getText()).toLowerCase());
					if (FileReaderUtilities.stripWhiteSpaces(userNameField.getText()).length() > 2)
					{
						setUserName(userNameField.getText());
					}
					else
					{
						DpmSwingUtilities.messageDialog("Bad User Name!", emailAddressDialog);
						setEmailAddress(null);
						setUserName(null);
					}
				}
				else
				{
					DpmSwingUtilities.messageDialog("Bad Email address!", emailAddressDialog);
					setEmailAddress(null);
					setUserName(null);
				}
				emailAddressDialog.setVisible(false);
			}
			if (actionString.equals("cancelButton"))
			{
				setEmailAddress(null);
				setUserName(null);
				emailAddressDialog.setVisible(false);
			}
		}
		
	}

}
