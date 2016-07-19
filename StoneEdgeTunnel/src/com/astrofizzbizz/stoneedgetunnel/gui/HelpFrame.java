package com.astrofizzbizz.stoneedgetunnel.gui;


import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

public class HelpFrame extends JFrame
{
	private static final long serialVersionUID = 5339573481632276378L;
	private JEditorPane htmlPane;
    private URL helpURL;
	boolean frameClosed = false;

	public HelpFrame(JFrame parentFrame) throws IOException
	{
		super("Help");
        setIconImage(parentFrame.getIconImage());
		initialize();
        pack();
        setVisible(true);
        setLocationRelativeTo(parentFrame);
	}
	private void initialize() throws IOException
	{
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
            }
        });

        //Create the HTML viewing pane.
        htmlPane = new JEditorPane();
        htmlPane.setEditable(false);
        JScrollPane htmlView = new JScrollPane(htmlPane);
        Dimension minimumSize = new Dimension(800, 600);
        htmlView.setPreferredSize(minimumSize);
        add(htmlView);
        
 		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		helpURL = loader.getResource("com/astrofizzbizz/stoneedgetunnel/files/help.htm");
        htmlPane.setPage(helpURL);
	}
	protected void messageDialog(String string)
	{
		JOptionPane.showMessageDialog(this, string);
	}

}
