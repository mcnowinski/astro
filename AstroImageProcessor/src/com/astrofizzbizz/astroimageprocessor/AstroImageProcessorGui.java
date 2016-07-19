package com.astrofizzbizz.astroimageprocessor;


import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.astrofizzbizz.pixie.PixieImage;
import com.astrofizzbizz.pixie.PixieImageException;
import com.astrofizzbizz.pixie.PixieImageRGBPlotterNoSwing;
import com.astrofizzbizz.pixie.SingleSimpleImageProcess;

import static javax.swing.ScrollPaneConstants.*;

public class AstroImageProcessorGui  extends JFrame
{
	private static final long serialVersionUID = -1433615005454840487L;
	protected String version = "v3.4";
	protected String versionDate = "February 20,2014";
	private final static int RECOMMENDED_HEAP = 768;
	JLabel statusBar = new JLabel();
	JLabel imageIconLabel = new JLabel();
	JLabel zoomedImageIconLabel = new JLabel();
	JPanel settingsAndImagePanel;
	JPanel imagePanel;
 	JPanel settingsPanel;
 	JPanel scaleTypePanel;
	JPanel alignPanel;
	JPanel rgbPanel;
	JPanel invertAndColorPanel;
	JSlider[] rgbSlider = new  JSlider[3];
	JLabel[] rgbLevelLabel = new  JLabel[3];
	JPanel minMaxPanel;
	JSlider[] minMaxSlider = new  JSlider[2];
	JLabel[] minMaxLevelLabel = new  JLabel[2];
	JRadioButton invertImageButton;
	JRadioButton colorSpectrumButton;
	boolean invertImage = false;
	boolean colorSpectrum = false;
	boolean initializingPanels = true;
	double[] imageScale = new double[2];
	double[] imageOffset = new double[2];
	int ialignLeftSum = 0;
	int ialignUpSum = 0;

 	SingleSimpleImageProcess ssip = null;
 	SingleSimpleImageProcess ssipAlignStart = null;
	SingleSimpleImageProcess ssipRef = null;
	SingleSimpleImageProcess[] ssipRgbOrig = {null, null, null};
	SingleSimpleImageProcess[] ssipRgbMod = {null, null, null};
 	String ssipFitsFileName = null;
 	String subtractFitsFileName = "";
 	String normalizeFitsFileName = "";
 	String alignFitsFileName = "";
	PixieImage[] pixieImageDisplay = new PixieImage[3];
	boolean disableMenu = false;
	boolean startupScreenDisplayed = true;
	ImageIcon startupDisplay = null;
	String[] fitsExtensions = {"fits", "fts", "fit"};
	Dimension mouseClick = null;
	Dimension settingsPanelMinimumSize = null;
	AstroImageProcessorHelpFrame helpFrame = null;


	DecimalFormat tp = new DecimalFormat("##.##");
 	
	private String lastDirectoryPath = null;
 	
 	int scaleType = 3;
	
	public AstroImageProcessorGui(String jFrameTitle)
	{
		super(jFrameTitle);
        try 
        {
            UIManager.setLookAndFeel(
            UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {}
        
		setupDisplay();
		getContentPane().add(settingsAndImagePanel, BorderLayout.CENTER);
		getContentPane().add(statusBar, java.awt.BorderLayout.SOUTH);  
        pack();
        setVisible(true);
        toFront();
        repaint();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(getExtendedState()|JFrame.MAXIMIZED_BOTH);
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		String iconLoc = "com/astrofizzbizz/astroimageprocessor/files/StoneEdgeTelescopeLogo.jpg";
		String startupImageLoc = "com/astrofizzbizz/astroimageprocessor/files/StoneEdgeImages.jpg";
		URL resourceURL = loader.getResource(startupImageLoc);
//		startupDisplay = new ImageIcon(resourceURL);

//        displayStartUpScreen(startupDisplay);
		resourceURL = loader.getResource(iconLoc);
        setIconImage(new ImageIcon(resourceURL).getImage());
        this.addComponentListener(new AstroImageProcessorActionListeners("frameResized", this));
        this.setMinimumSize(new Dimension(600,400));
		int heapSizeMegs = (int) (Runtime.getRuntime().maxMemory()/1024)/1024;
		if (heapSizeMegs < RECOMMENDED_HEAP)
		{
			String message = "Max Memory size = " + heapSizeMegs + " MB\n";
			message = message + "Recommend size = " + RECOMMENDED_HEAP + " MB\n";
			message = message + "Might have trouble aligning or making RGB plots for large images";
			messageDialog(message);
		}
	}
	private void setupDisplay()
	{
        setJMenuBar(addMenu());
        	 	
	 	settingsPanel = new JPanel();
	 	settingsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Settings"),BorderFactory.createEmptyBorder(5,5,5,5)));
	 	settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.PAGE_AXIS));
	 	setupScaleTypePanel();
	 	setupInvertAndColorPanel();
	 	setupAlignPanel();
	 	setupRgbPanel();
	 	setupMinMaxSliderPanel();
	 	settingsPanel.add(scaleTypePanel);
	 	settingsPanel.add(invertAndColorPanel);
	 	settingsPanel.add(minMaxPanel);
	 	settingsPanel.add(rgbPanel);
	 	settingsPanel.add(alignPanel);
	 	
	 	JPanel zoomedImagePanel = new JPanel();
	 	zoomedImagePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Zoom"),BorderFactory.createEmptyBorder(5,5,5,5)));
	 	zoomedImagePanel.setLayout(new FlowLayout());
	 	zoomedImagePanel.add(zoomedImageIconLabel);
	 	settingsPanel.add(zoomedImagePanel);
	 	
	 	settingsPanelMinimumSize = new Dimension(settingsPanel.getMinimumSize());
	 	settingsPanel.setMaximumSize(new Dimension(settingsPanelMinimumSize.width, 1500));
	 	
	 	
	 	alignPanel.setVisible(false);
	 	rgbPanel.setVisible(false);
	
		imageIconLabel = new JLabel();
		imageIconLabel.addMouseListener(new AstroImageProcessorActionListeners("mouseClickedOnImage", this));
		imagePanel = new JPanel();
		imagePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(""),BorderFactory.createEmptyBorder(5,5,5,5)));
		imagePanel.add(imageIconLabel);
		
        settingsAndImagePanel = new JPanel();
        settingsAndImagePanel.setLayout(new BoxLayout(settingsAndImagePanel, BoxLayout.X_AXIS));
        settingsAndImagePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(""),BorderFactory.createEmptyBorder(5,5,5,5)));

	 	JScrollPane settingsScrollPane  = new JScrollPane(settingsPanel);
	 	settingsScrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
	 	settingsScrollPane.setMaximumSize(new Dimension(settingsPanelMinimumSize.width + 10, 1500));
        settingsAndImagePanel.add(settingsScrollPane);
		settingsAndImagePanel.add(imagePanel);
		statusBar.setText("Version " + version + " - " + versionDate);
		initializingPanels = false;
	}
	private void setupScaleTypePanel()
	{
		scaleTypePanel = new JPanel();
		JRadioButton[] scaleTypeButton = new JRadioButton[4];
		scaleTypePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Transfer Function"),BorderFactory.createEmptyBorder(5,5,5,5)));
		scaleTypePanel.setLayout(new FlowLayout());
		scaleTypeButton[0] = new JRadioButton("Linear");
		scaleTypeButton[1] = new JRadioButton("Sqrt");
		scaleTypeButton[2] = new JRadioButton("Log");
		scaleTypeButton[3] = new JRadioButton("Asinh");
		scaleTypeButton[0].addActionListener(new AstroImageProcessorActionListeners("linearScale", this));
		scaleTypeButton[1].addActionListener(new AstroImageProcessorActionListeners("sqrtScale", this));
		scaleTypeButton[2].addActionListener(new AstroImageProcessorActionListeners("logScale", this));
		scaleTypeButton[3].addActionListener(new AstroImageProcessorActionListeners("asinhScale", this));
	 	ButtonGroup graphicsTypeButtonGroup = new ButtonGroup();
	 	graphicsTypeButtonGroup.add(scaleTypeButton[0]);
	 	graphicsTypeButtonGroup.add(scaleTypeButton[1]);
	 	graphicsTypeButtonGroup.add(scaleTypeButton[2]);
	 	graphicsTypeButtonGroup.add(scaleTypeButton[3]);
	 	scaleTypePanel.add(scaleTypeButton[0]);
	 	scaleTypePanel.add(scaleTypeButton[1]);
	 	scaleTypePanel.add(scaleTypeButton[2]);
	 	scaleTypePanel.add(scaleTypeButton[3]);
	 	if (scaleType == 0)
	 		scaleTypeButton[0].setSelected(true);
	 	if (scaleType == 3)
	 		scaleTypeButton[1].setSelected(true);
	 	if (scaleType == 1)
	 		scaleTypeButton[2].setSelected(true);
	 	if (scaleType == 4)
	 		scaleTypeButton[3].setSelected(true);


		return;
	}
	private void setupInvertAndColorPanel()
	{
		invertImage = false;
		colorSpectrum = false;
	 	invertAndColorPanel = new JPanel();
	 	invertAndColorPanel.setLayout(new BoxLayout(invertAndColorPanel, BoxLayout.X_AXIS));
	 	invertAndColorPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(""),BorderFactory.createEmptyBorder(5,5,5,5)));
		invertImageButton = new JRadioButton("Invert Image");
		invertImageButton.setSelected(false);
		invertImageButton.addActionListener(new AstroImageProcessorActionListeners("invertImage", this));
		invertAndColorPanel.add(invertImageButton);

		colorSpectrumButton = new JRadioButton("Color Spectrum");
		colorSpectrumButton.setSelected(false);
		colorSpectrumButton.addActionListener(new AstroImageProcessorActionListeners("colorSpectrum", this));
		invertAndColorPanel.add(colorSpectrumButton);
	}
	private void setupAlignPanel()
	{
		alignPanel = new JPanel();
		alignPanel.setLayout(new BoxLayout(alignPanel, BoxLayout.Y_AXIS));
		alignPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Align"),BorderFactory.createEmptyBorder(5,5,5,5)));

		JPanel[] buttonPanel = new JPanel[3];
		for (int ii = 0; ii < 3; ++ii)
		{
			buttonPanel[ii] = new JPanel();
			buttonPanel[ii].setLayout(new FlowLayout());
		}
		JButton moveUpButton = new JButton(" Up ");
		moveUpButton.addActionListener(new AstroImageProcessorActionListeners("MoveUpButton", this));
		buttonPanel[0].add(moveUpButton);
		
		JButton moveDownButton = new JButton("Down");
		moveDownButton.addActionListener(new AstroImageProcessorActionListeners("MoveDownButton", this));
		buttonPanel[2].add(moveDownButton);
		
		JButton moveLeftButton = new JButton("Left");
		moveLeftButton.addActionListener(new AstroImageProcessorActionListeners("MoveLeftButton", this));
		buttonPanel[1].add(moveLeftButton);
		
		JButton moveRightButton = new JButton("Right");
		moveRightButton.addActionListener(new AstroImageProcessorActionListeners("MoveRightButton", this));
		buttonPanel[1].add(moveRightButton);
		
		JPanel allButtonPanel = new JPanel();
		allButtonPanel.setLayout(new BoxLayout(allButtonPanel, BoxLayout.Y_AXIS));
		allButtonPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(""),BorderFactory.createEmptyBorder(5,5,5,5)));
		allButtonPanel.add(buttonPanel[0]);
		allButtonPanel.add(buttonPanel[1]);
		allButtonPanel.add(buttonPanel[2]);		
		alignPanel.add(allButtonPanel);
		
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new AstroImageProcessorActionListeners("SaveAlignButton", this));

		JButton closeButton = new JButton("Close Align");
		closeButton.addActionListener(new AstroImageProcessorActionListeners("CloseAlignButton", this));

		JPanel saveQuitPanel = new JPanel();
		saveQuitPanel.setLayout(new FlowLayout());
		saveQuitPanel.add(saveButton);
		saveQuitPanel.add(closeButton);
		alignPanel.add(saveQuitPanel);
		
	}
	void setupRgbPanel()
	{
		JPanel rgbSliderPanel = new JPanel();
		rgbSliderPanel.setLayout(new GridLayout(1,3));
		AstroImageProcessorActionListeners[] rgbSliderListen = new AstroImageProcessorActionListeners[3];
		JPanel[] singleRgbSliderPanel  = new JPanel[3];
		JLabel[] colorTitle = {new JLabel("Red"), new JLabel("Green"), new JLabel("Blue")};
		for (int ii = 0; ii < 3; ++ii)
		{
			singleRgbSliderPanel[ii] = new JPanel();
			singleRgbSliderPanel[ii].setLayout(new BoxLayout(singleRgbSliderPanel[ii], BoxLayout.Y_AXIS));
			rgbSlider[ii] = new JSlider(JSlider.VERTICAL, -100, 100, 0);
			rgbSliderListen[ii] = new AstroImageProcessorActionListeners("RGB " + ii + " Slider Changed", this);
			rgbSlider[ii].addChangeListener(rgbSliderListen[ii]);
			rgbSlider[ii].setMinimumSize(new Dimension(20,100));
			
//			Turn on labels at major tick marks.
			rgbSlider[ii].setMajorTickSpacing(100);
			rgbSlider[ii].setMinorTickSpacing(100);
			rgbSlider[ii].setPaintTicks(true);
			rgbSlider[ii].setPaintLabels(false);
			rgbSlider[ii].setSnapToTicks(false);
			rgbLevelLabel[ii] = new JLabel("1.0");
//			rgbSlider[ii].setSize(rgbSlider[ii].getSize().width, 1000);
			
			singleRgbSliderPanel[ii].add(rgbSlider[ii]);
			singleRgbSliderPanel[ii].add(colorTitle[ii]);
			singleRgbSliderPanel[ii].add(rgbLevelLabel[ii]);
			
			rgbSliderPanel.add(singleRgbSliderPanel[ii]);

		}
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new AstroImageProcessorActionListeners("SaveRgbButton", this));

		JButton closeButton = new JButton("Close RGB");
		closeButton.addActionListener(new AstroImageProcessorActionListeners("CloseRgbButton", this));

		JPanel saveQuitPanel = new JPanel();
		saveQuitPanel.setLayout(new FlowLayout());
		saveQuitPanel.add(saveButton);
		saveQuitPanel.add(closeButton);

		rgbPanel = new JPanel();
		rgbPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("RGB Colors"),BorderFactory.createEmptyBorder(5,5,5,5)));
		rgbPanel.setLayout(new BoxLayout(rgbPanel, BoxLayout.Y_AXIS));
//		rgbPanel.setLayout(new GridLayout(2,1));
		rgbPanel.add(rgbSliderPanel);
		rgbPanel.add(saveQuitPanel);
	}
	private void setupMinMaxSliderPanel()
	{
		JPanel minMaxSliderPanel = new JPanel();
		minMaxSliderPanel.setLayout(new GridLayout(2,1));
		AstroImageProcessorActionListeners[] minMaxSliderListen = new AstroImageProcessorActionListeners[2];
		JPanel[] singleMinMaxSliderPanel  = new JPanel[2];
		JLabel[] minMaxTitle = {new JLabel("Min"), new JLabel("Max"), new JLabel("Blue")};
		for (int ii = 0; ii < 2; ++ii)
		{
			singleMinMaxSliderPanel[ii] = new JPanel();
			singleMinMaxSliderPanel[ii].setLayout(new BoxLayout(singleMinMaxSliderPanel[ii], BoxLayout.X_AXIS));
			minMaxSlider[ii] = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
			minMaxSliderListen[ii] = new AstroImageProcessorActionListeners("minMax " + ii + " Slider Changed", this);
			minMaxSlider[ii].addChangeListener(minMaxSliderListen[ii]);

//			Turn on labels at major tick marks.
			minMaxSlider[ii].setMajorTickSpacing(100);
			minMaxSlider[ii].setMinorTickSpacing(25);
			minMaxSlider[ii].setPaintTicks(true);
			minMaxSlider[ii].setPaintLabels(true);
			minMaxSlider[ii].setSnapToTicks(false);
			minMaxLevelLabel[ii] = new JLabel("0.0");

			
			singleMinMaxSliderPanel[ii].add(minMaxTitle[ii]);			
			singleMinMaxSliderPanel[ii].add(minMaxSlider[ii]);
			singleMinMaxSliderPanel[ii].add(minMaxLevelLabel[ii]);
			minMaxSliderPanel.add(singleMinMaxSliderPanel[ii]);

		}
		minMaxSlider[0].setValue(0);
		minMaxSlider[1].setValue(100);
		minMaxLevelLabel[1].setText("100.0");
		minMaxPanel = new JPanel();
		minMaxPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Scale"),BorderFactory.createEmptyBorder(5,5,5,5)));
		minMaxPanel.setLayout(new GridLayout(1,1));
		minMaxPanel.add(minMaxSliderPanel);
	}
	private  JMenuBar addMenu()
    {
        JMenuBar menuBar = new JMenuBar();
        
        String menuText[] = {"File", "Clean", "Action",  "RGB Plot", "Help"};
        String itemText[][] = {
        		{"Open", "Save as FITS", "Save as PNG", "Exit"},
        		{"Remove Hot Spots"},
        		{"Add", "Subtract", "Divide", "Normalize", "Align"},
        		{"Create RGB Plot"},
        		{"Help", "YouTube Video", "About"}
        };

        for (int i = 0; i < menuText.length; i++)
        {
            JMenu menu = new JMenu(menuText[i]);
            menuBar.add (menu);
            
            for (int j = 0; j < itemText[i].length; j++)
            {
                JMenuItem item = new JMenuItem(itemText[i][j]);
                menu.add (item);
                item.addActionListener(new AstroImageProcessorActionListeners( menuText[i] + "." +itemText[i][j], this));
        		if (itemText[i][j].equals("Open"))
        		{
        			item.setAccelerator(KeyStroke.getKeyStroke(
        			        KeyEvent.VK_O, ActionEvent.CTRL_MASK));

        		}
        		if (itemText[i][j].equals("Save as FITS"))
        		{
        			item.setAccelerator(KeyStroke.getKeyStroke(
        			        KeyEvent.VK_S, ActionEvent.CTRL_MASK));

        		}
        		if (itemText[i][j].equals("Exit"))
        		{
        			item.setAccelerator(KeyStroke.getKeyStroke(
        			        KeyEvent.VK_Q, ActionEvent.CTRL_MASK));

        		}
        		if (itemText[i][j].equals("Add"))
        		{
        			item.setAccelerator(KeyStroke.getKeyStroke(
        			        KeyEvent.VK_P, ActionEvent.CTRL_MASK));

        		}
        		if (itemText[i][j].equals("Subtract"))
        		{
        			item.setAccelerator(KeyStroke.getKeyStroke(
        			        KeyEvent.VK_M, ActionEvent.CTRL_MASK));

        		}
        		if (itemText[i][j].equals("Divide"))
        		{
        			item.setAccelerator(KeyStroke.getKeyStroke(
        			        KeyEvent.VK_D, ActionEvent.CTRL_MASK));

        		}
        		if (itemText[i][j].equals("Remove Hot Spots"))
        		{
        			item.setAccelerator(KeyStroke.getKeyStroke(
        			        KeyEvent.VK_H, ActionEvent.CTRL_MASK));

        		}
        		if (itemText[i][j].equals("Normalize"))
        		{
        			item.setAccelerator(KeyStroke.getKeyStroke(
        			        KeyEvent.VK_N, ActionEvent.CTRL_MASK));

        		}
        		if (itemText[i][j].equals("Align"))
        		{
        			item.setAccelerator(KeyStroke.getKeyStroke(
        			        KeyEvent.VK_A, ActionEvent.CTRL_MASK));

        		}
           }
        }
        
        return menuBar;
    }
	public void openFitsFile() 
	{
		File fitsFile = chooseFile(lastDirectoryPath, "Open Fits File", "", false, fitsExtensions);
		if (fitsFile == null ) return;
		lastDirectoryPath = fitsFile.getPath();
		try {
			ssip = new SingleSimpleImageProcess(fitsFile.getPath());
			ssipFitsFileName = fitsFile.getName();
			pixieImageDisplay = ssip.createRGBPixieImage();
			mouseClick = new Dimension(pixieImageDisplay[0].getColCount() / 2, pixieImageDisplay[0].getRowCount() / 2);
		} catch (PixieImageException e) 
		{
			messageDialog("Weird fits format.\nCan't open File\nSorry!");
			return;
		}
		displayFitsFile();
		statusBar.setText("Opened " + ssipFitsFileName);

	}
	String removeExtension(String fileName)
	{
		String stripName = fileName.substring(0, fileName.lastIndexOf("."));
		return stripName;
	}
	FileNameExtensionFilter makeFileNameExtensionFilter(String[] extensions)
	{
		int numExtensions = 0;
		for (int ii = 0; ii < extensions.length; ++ii) if (extensions[ii] != null) numExtensions = numExtensions + 1;
		String[] nne = new String[numExtensions];
		int iext = 0;
	   	String extensionDesc = "";
		for (int ii = 0; ii < extensions.length; ++ii) 
		{
			if (extensions[ii] != null)
			{
				nne[iext] = extensions[ii];
				if (iext == 0 ) extensionDesc = extensionDesc + "*." + extensions[ii];
				if (iext >  0 ) extensionDesc = extensionDesc + ", *." + extensions[ii];
				iext = iext + 1;
			}
		}
		if (numExtensions == 1) return new FileNameExtensionFilter(extensionDesc, nne[0]);
		if (numExtensions == 2) return new FileNameExtensionFilter(extensionDesc, nne[0], nne[1]);
		if (numExtensions == 3) return new FileNameExtensionFilter(extensionDesc, nne[0], nne[1], nne[2]);
		if (numExtensions >= 4) return new FileNameExtensionFilter(extensionDesc, nne[0], nne[1], nne[2], nne[3]);
		return null;
	}
    public File chooseFile(String directoryPath, String dialogTitle, String selectedFileName, boolean saveDialog, String[] extensions) 
    {
    	File file = null;
    	JFileChooser fc = null;
    	if (directoryPath != null)
    	{
    		fc = new JFileChooser(directoryPath);
    	}
    	else
    	{
    		fc = new JFileChooser();
    	}
    	fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    	fc.setMultiSelectionEnabled(false);
    	fc.setSelectedFile(new File(selectedFileName));
        fc.addChoosableFileFilter(makeFileNameExtensionFilter(extensions));
        fc.setDialogTitle(dialogTitle);
        int returnVal = 0;
        if (saveDialog)
        {
            returnVal = fc.showSaveDialog(this);
        }
        else
        {
            returnVal = fc.showOpenDialog(this);
        }
        if (returnVal == JFileChooser.APPROVE_OPTION) 
        {
            file = fc.getSelectedFile();
        } 
        return file;
 	}
	void saveFitsFile()
	{
		if (ssip == null) 
		{
			messageDialog("Nothing to save");
			return;
		}
		File fitsFile = chooseFile(lastDirectoryPath, "Save Fits File", ssipFitsFileName, true, fitsExtensions);
		if (fitsFile == null ) return;
     	lastDirectoryPath = fitsFile.getPath();
     	try {
			ssip.writeOutFitsFile(fitsFile.getPath());
		} catch (PixieImageException e) {
			messageDialog("Can't Save File\nSorry!");
			return;
		}
	}
	void savePngFile(String suggestedName)
	{
		String[] pngFileExtension = {"png"};
		File pngFile = chooseFile(lastDirectoryPath, "Save PNG File", suggestedName, true, pngFileExtension);
		if (pngFile == null ) return;
     	lastDirectoryPath = pngFile.getPath();
		PixieImageRGBPlotterNoSwing plotter = null;
		try {
			plotter = SingleSimpleImageProcess.threeColor(pixieImageDisplay[0], pixieImageDisplay[1], pixieImageDisplay[2], 
					scaleType, (double) minMaxSlider[0].getValue(), (double) minMaxSlider[1].getValue());
		} catch (PixieImageException e) {
			messageDialog("Can't Save File\nSorry!");
		}
		plotter.toPNGFile(pngFile);
	}
	void saveFitsToPng()
	{
		if (ssip == null) 
		{
			messageDialog("Open a file first!");
			return;
		}
		String suggestedName = removeExtension(ssipFitsFileName) + ".png";
		savePngFile(suggestedName);
	}
	void displayStartUpScreen(ImageIcon imageIcon)
	{
/*		startupScreenDisplayed = true;
		int iheightH = this.getHeight() - 150;
		int iwidthH = iheightH * imageIcon.getIconWidth();
		iwidthH  = iwidthH / imageIcon.getIconHeight();
		
		int iwidthW = this.getWidth() - settingsPanel.getWidth() - 50;
		int iheightW = iwidthW * imageIcon.getIconHeight();
		iheightW  = iheightW / imageIcon.getIconWidth();
		
		int iwidth = iwidthH;
		int iheight = iheightH;
		if((iwidthW < iwidthH) || (iheightW < iheightH))
		{
			iwidth = iwidthW;
			iheight = iheightW;
		}

		BufferedImage bi = new BufferedImage(iwidth,iheight,BufferedImage.TYPE_INT_ARGB);
	 	Graphics2D graphics2D = bi.createGraphics();
	    graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	    			RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    graphics2D.drawImage(imageIcon.getImage(), 0, 0, iwidth, iheight, null);
	 	imageIconLabel.setIcon(new ImageIcon(bi));
*/
	}
	void displayFitsFile()
	{
		if ((ssip == null) && (ssipRgbMod == null)) return;
		startupScreenDisplayed = false;
		PixieImageRGBPlotterNoSwing plotter = null;
		try {
			plotter = SingleSimpleImageProcess.threeColor(pixieImageDisplay[0], pixieImageDisplay[1], pixieImageDisplay[2], 
					scaleType, (double) minMaxSlider[0].getValue(), (double) minMaxSlider[1].getValue());
		} catch (PixieImageException e) {
			e.printStackTrace();
		}
		plotter.setColorSpectrum(colorSpectrum);
		plotter.setInvertImage(invertImage);
		BufferedImage biOrig =  plotter.makeBufferedImage();

		int iheightH = this.getHeight() - 150;
		int iwidthH = iheightH * biOrig.getWidth();
		iwidthH  = iwidthH / biOrig.getHeight();
		
		int iwidthW = this.getWidth() - settingsPanel.getWidth() - 50;
		int iheightW = iwidthW * biOrig.getHeight();
		iheightW  = iheightW / biOrig.getWidth();
		
		int imageWidth = iwidthH;
		int imageHeight = iheightH;
		if((iwidthW < iwidthH) || (iheightW < iheightH))
		{
			imageWidth = iwidthW;
			imageHeight = iheightW;
		}
		int rows = pixieImageDisplay[0].getRowCount();
		int cols = pixieImageDisplay[0].getColCount();
		imageScale[0] =  ((double) cols ) / ((double) imageWidth);
		imageScale[1] = -((double) rows ) / ((double) imageHeight);
		imageOffset[0] = 0;
		imageOffset[1] = -(double) imageHeight;

		BufferedImage bi = new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_INT_ARGB);
	 	Graphics2D graphics2D = bi.createGraphics();
	    graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	    			RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    graphics2D.drawImage(plotter.makeBufferedImage(), 0, 0, imageWidth, imageHeight, null);
	 	imageIconLabel.setIcon(new ImageIcon(bi));
	 	makeZoomedImage(10.0, settingsPanelMinimumSize.width, plotter);
	}
	void subtractFitsFile()
	{
		if (ssip == null) 
		{
			messageDialog("Open a file first!");
			return;
		}
		File fitsFile = chooseFile(lastDirectoryPath, "Open Fits File To Subtract", subtractFitsFileName, false, fitsExtensions);
		if (fitsFile == null ) return;
		lastDirectoryPath = fitsFile.getPath();
		try {
			ssip.subtractImage(fitsFile.getPath());
			ssipFitsFileName = removeExtension(ssipFitsFileName) + "_sub.fits";
			subtractFitsFileName = fitsFile.getName();
		} catch (PixieImageException e) {
			messageDialog("Weird fits format.\nCan't open File\nSorry!");
			return; 
		}
		displayFitsFile();
		statusBar.setText("Fits File Suggested Name " + ssipFitsFileName);

	}
	void addFitsFile()
	{
		if (ssip == null) 
		{
			messageDialog("Open a file first!");
			return;
		}
		File fitsFile = chooseFile(lastDirectoryPath, "Open Fits File To Add", "", false, fitsExtensions);
		if (fitsFile == null ) return;
		lastDirectoryPath = fitsFile.getPath();
		try {
			ssip.addImage(fitsFile.getPath());
			ssipFitsFileName = removeExtension(ssipFitsFileName) + "_add.fits";
		} catch (PixieImageException e) {
			messageDialog("Weird fits format.\nCan't open File\nSorry!");
			return; 
		}
		displayFitsFile();
		statusBar.setText("Fits File Suggested Name " + ssipFitsFileName);
	}
	void divideFitsFile()
	{
		if (ssip == null) 
		{
			messageDialog("Open a file first!");
			return;
		}
		File fitsFile = chooseFile(lastDirectoryPath, "Open Fits File To Divide", "", false, fitsExtensions);
		if (fitsFile == null ) return;
		lastDirectoryPath = fitsFile.getPath();
		try {
			ssip.divideImage(fitsFile.getPath());
			ssipFitsFileName = removeExtension(ssipFitsFileName) + "_div.fits";
		} catch (PixieImageException e) {
			messageDialog("Weird fits format.\nCan't open File\nSorry!");
			return; 
		}
		displayFitsFile();
		statusBar.setText("Fits File Suggested Name " + ssipFitsFileName);
	}
	void removeMean()
	{
		if (ssip == null) 
		{
			messageDialog("Open a file first!");
			return;
		}
		ssip.subtractMean();
		ssipFitsFileName = removeExtension(ssipFitsFileName) + "_meanR.fits";
		displayFitsFile();
		statusBar.setText("Fits File Suggested Name " + ssipFitsFileName);
	}
	void removeHotspots()
	{
		if (ssip == null) 
		{
			messageDialog("Open a file first!");
			return;
		}
		ssip.removeHotSpots();
		ssipFitsFileName = removeExtension(ssipFitsFileName) + "_hspotR.fits";
		displayFitsFile();
		statusBar.setText("Fits File Suggested Name " + ssipFitsFileName);
	}
	void normalize()
	{
		if (ssip == null) 
		{
			messageDialog("Open a file first!");
			return;
		}
		File fitsFile = chooseFile(lastDirectoryPath, "Open Fits File To Normalize To", normalizeFitsFileName, false, fitsExtensions);
		if (fitsFile == null ) return;
		lastDirectoryPath = fitsFile.getPath();
		try {
			ssip.normalize(fitsFile.getPath());
			ssipFitsFileName = removeExtension(ssipFitsFileName) + "_norm.fits";
			normalizeFitsFileName = fitsFile.getName();
		} catch (PixieImageException e) {
			messageDialog("Weird fits format.\nCan't open File\nSorry!");
			return; 
		}
		displayFitsFile();
		statusBar.setText("Fits File Suggested Name " + ssipFitsFileName);

	}
	void align() 
	{
		if (ssip == null) 
		{
			messageDialog("Open a file first!");
			return;
		}
		File fitsFile = chooseFile(lastDirectoryPath, "Open Fits File To Align To", alignFitsFileName, false, fitsExtensions);
		if (fitsFile == null ) return;
		lastDirectoryPath = fitsFile.getPath();
		try {
			ssipRef = new SingleSimpleImageProcess(fitsFile.getPath());
			pixieImageDisplay[0] = ssip.getPixieImage();
			pixieImageDisplay[1] = ssipRef.getPixieImage();
			pixieImageDisplay[2] = ssipRef.getPixieImage();
			ssipFitsFileName = removeExtension(ssipFitsFileName) + "_align.fits";
			alignFitsFileName = fitsFile.getName();
		} catch (PixieImageException e) {
			messageDialog("Weird fits format.\nCan't open File\nSorry!");
			return; 
		}
		ialignLeftSum = 0;
		ialignUpSum = 0;
		try {
			ssipAlignStart = new SingleSimpleImageProcess(ssip);
		} catch (PixieImageException e) {
			e.printStackTrace();
		}
	 	alignPanel.setVisible(true);
		disableMenu = true;
		displayFitsFile();
		statusBar.setText("Fits File Suggested Name " + ssipFitsFileName);
	}
	void plotAlignedImages(int ileft, int iup)
	{
		ialignLeftSum = ialignLeftSum + ileft;
		ialignUpSum = ialignUpSum + iup;
		try {
			ssip = new SingleSimpleImageProcess(ssipAlignStart);
		} catch (PixieImageException e) {
			e.printStackTrace();
		}
		ssip.shiftImage(ialignLeftSum, ialignUpSum);
		pixieImageDisplay[0] = ssip.getPixieImage();
		pixieImageDisplay[1] = ssipRef.getPixieImage();
		pixieImageDisplay[2] = ssipRef.getPixieImage();
		displayFitsFile();
	}
	void saveAlignedImages(boolean saveImages)
	{
		if (saveImages) saveFitsFile();
	 	alignPanel.setVisible(false);
		disableMenu = false;
		pixieImageDisplay[0] = ssip.getPixieImage();
		pixieImageDisplay[1] = ssip.getPixieImage();
		pixieImageDisplay[2] = ssip.getPixieImage();
		displayFitsFile();

	}
	void rgbPlot()
	{
		colorSpectrum = false;
		invertImage = false;
		colorSpectrumButton.setSelected(false);
		invertImageButton.setSelected(false);
		String[] directions = {"Open Fits Red File", "Open Fits Green File", "Open Fits Blue File"};
		for (int ii = 0; ii < 3; ++ii)
		{
			File fitsFile = chooseFile(lastDirectoryPath, directions[ii], "", false, fitsExtensions);
			if (fitsFile == null ) return;
			lastDirectoryPath = fitsFile.getPath();
			try {
				ssipRgbOrig[ii] = new SingleSimpleImageProcess(fitsFile.getPath());
				ssip = ssipRgbOrig[ii];
			} catch (PixieImageException e1) {
				messageDialog("Weird fits format.\nCan't open File\nSorry!");
				return;
			}
			pixieImageDisplay[0] = ssipRgbOrig[ii].getPixieImage();
			pixieImageDisplay[1] = ssipRgbOrig[ii].getPixieImage();
			pixieImageDisplay[2] = ssipRgbOrig[ii].getPixieImage();
			mouseClick = new Dimension(pixieImageDisplay[0].getColCount() / 2, pixieImageDisplay[0].getRowCount() / 2);
			displayFitsFile();
		}
		for (int ii = 0; ii < 3; ++ii)
		{
			pixieImageDisplay[ii] = ssipRgbOrig[ii].getPixieImage();
			rgbSlider[ii].setValue(0);
			rgbLevelLabel[ii].setText("1.0");
		}
		rgbPanel.setVisible(true);
		disableMenu = true;
		displayFitsFile();
		
	}
	protected void rgbSliderChanged(int islider) 
	{
		if (rgbSlider[islider].getValueIsAdjusting()) return;
		for (int ii = 0; ii < 3; ++ii)
		{
			double sliderValue = (double) rgbSlider[ii].getValue();
			sliderValue = Math.pow(10.0, sliderValue / 100.0);
			rgbLevelLabel[ii].setText(tp.format(sliderValue));
			try {
				ssipRgbMod[ii] = new SingleSimpleImageProcess(ssipRgbOrig[ii]);
				ssipRgbMod[ii].gainAdjust(sliderValue);
			} catch (PixieImageException e) {
				e.printStackTrace();
				return;
			}
			pixieImageDisplay[ii] = ssipRgbMod[ii].getPixieImage();
		}
		displayFitsFile();
	}
	protected void minMaxSliderChanged(int islider) 
	{
		if (initializingPanels) return;
		if (minMaxSlider[islider].getValueIsAdjusting()) return;
		int[] sliderValue = new int[2];
		for (int ii = 0; ii < 2; ++ii)
		{
			sliderValue[ii] = minMaxSlider[ii].getValue();
		}
		if (islider == 0)
		{
			if (sliderValue[0] > sliderValue[1]) sliderValue[0] = sliderValue[1];
			minMaxSlider[0].setValue(sliderValue[0]);
			minMaxLevelLabel[0].setText(tp.format(sliderValue[0]));
		}
		if (islider == 1)
		{
			if (sliderValue[0] > sliderValue[1]) sliderValue[1] = sliderValue[0];
			minMaxSlider[1].setValue(sliderValue[1]);
			minMaxLevelLabel[1].setText(tp.format(sliderValue[1]));
		}
		displayFitsFile();
	
	
	}
	void saveRgb(boolean saveImages)
	{
		if (saveImages) savePngFile("");
		rgbPanel.setVisible(false);
		disableMenu = false;
	 	ssip = null;

	}
	void windowResized()
	{
		if (startupScreenDisplayed) 
		{
			displayStartUpScreen(startupDisplay);
			return;
		}
		displayFitsFile();
	}
	protected void mouseClickedOnBWImage(MouseEvent e)
	{
		if ((ssip == null) && (ssipRgbMod == null)) return;
		int ix = e.getX();
		int iy = e.getY();
		
		double x = (((double) ix) + imageOffset[0]) * imageScale[0];
		double y = (((double) iy) + imageOffset[1]) * imageScale[1];
		
		ix = (int) x;
		iy = (int) y;
		int ired = (int) pixieImageDisplay[0].getPix()[ix][iy];
		int igreen = (int) pixieImageDisplay[1].getPix()[ix][iy];
		int iblue = (int) pixieImageDisplay[2].getPix()[ix][iy];
		mouseClick.setSize(ix, iy);
		
		String message = "Pixel: X = " + ix + ", Y = " + iy + "; Red = " + ired + ", Green = " + igreen + ", Blue = " + iblue;
		statusBar.setText(message);
		displayFitsFile();
	}
	protected void makeZoomedImage(double zoom, int displayWidth, PixieImageRGBPlotterNoSwing origPlotter)
	{
		PixieImage[] zoomPixieImageDisplay  
			= SingleSimpleImageProcess.makeZoomedPixieImage(pixieImageDisplay, zoom, mouseClick.height, mouseClick.width);
		PixieImageRGBPlotterNoSwing plotter = null;
		try {
			plotter = SingleSimpleImageProcess.threeColor(zoomPixieImageDisplay[0], zoomPixieImageDisplay[1], zoomPixieImageDisplay[2], 
					scaleType, (double) minMaxSlider[0].getValue(), (double) minMaxSlider[1].getValue());
		} catch (PixieImageException e) {
			e.printStackTrace();
		}
		plotter.setAutoScale(false);
		plotter.setPixelValueLimits(origPlotter.getMinPixelValue(), origPlotter.getMaxPixelValue());
		plotter.setColorSpectrum(colorSpectrum);
		plotter.setInvertImage(invertImage);
		BufferedImage biOrig =  plotter.makeBufferedImage();
		int displayHeight = displayWidth * biOrig.getHeight();
		displayHeight  = displayHeight / biOrig.getWidth();

		BufferedImage bi = new BufferedImage(displayWidth,displayHeight,BufferedImage.TYPE_INT_ARGB);
	 	Graphics2D graphics2D = bi.createGraphics();
	    graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	    			RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    graphics2D.drawImage(plotter.makeBufferedImage(), 0, 0, displayWidth, displayHeight, null);
	    zoomedImageIconLabel.setIcon(new ImageIcon(bi));
	}
	void flipInvertImageButton()
	{
		if (invertImage)
		{
			invertImageButton.setSelected(false);
			invertImage = false;
		}
		else
		{
			invertImageButton.setSelected(true);
			invertImage = true;
		}
		if (!startupScreenDisplayed) displayFitsFile();

	}
	void flipColorSpectrumButton()
	{
		if (colorSpectrum)
		{
			colorSpectrumButton.setSelected(false);
			colorSpectrum = false;
		}
		else
		{
			colorSpectrumButton.setSelected(true);
			colorSpectrum = true;
		}
		if (!startupScreenDisplayed) displayFitsFile();

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
		try {helpFrame = new AstroImageProcessorHelpFrame(this);} 
		catch (IOException e) {messageDialog("Can't open help file: " + e.getMessage());}
		
	}
	protected void openYouTubeVideo()
	{
		try 
		{
			URL imageLinkUrl = new URL("http://www.youtube.com/watch?v=dE_-yH0GfJg");
	    	Desktop.getDesktop().browse(imageLinkUrl.toURI());
		} 
		catch (MalformedURLException e) {} 
		catch (IOException e) {} 
		catch (URISyntaxException e) {}
	}
	protected void messageDialog(String string)
	{
		JOptionPane.showMessageDialog(this, string);
	}
	public static void main(String[] args) throws Exception 
	{
		if (args.length > 0 ) 
		{
			System.out.println(args[0]);
		}
		new AstroImageProcessorGui("StoneEdge Astro Image Processor");
	}

}
