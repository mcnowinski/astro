package com.astrofizzbizz.stoneedge3.client.targeting;

import com.astrofizzbizz.stoneedge3.client.Se3Exception;
import com.astrofizzbizz.stoneedge3.client.StoneEdge3;
import com.astrofizzbizz.stoneedge3.client.Utilities;
import com.astrofizzbizz.stoneedge3.shared.AstroTarget;
import com.astrofizzbizz.stoneedge3.shared.ObsCommandReturnInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;


public class AstroTargetPanel extends CaptionPanel
{
	private AstroTarget astroTarget = new AstroTarget();
	StoneEdge3 stoneEdge3;
	
	private String checkVisibleCommand = null;
//	private String[] checkVisibleLimitDebugResponse = {"alt limit alt=-42.8"};
	private String[] checkVisibleOkDebugResponse = {"01:03:30 00:00:00 2000.000000 testTarg"};
	
	private String slewCommand = "";
	private String[] slewOKDebugResponse = {"done point move=47.630 dist=0.0005"};
//	private String[] slewErrorDebugResponse = {"ERROR point axis=Az fatal following error"};
	
	private TextBox targetNameTextBox = new TextBox();
	private final Button editTargetButton = new Button("Edit");
	private final Button doneEditTargetButton = new Button("Done Edit");
	private Button slewButton = new Button("Slew");
	private IntegerBox raHrsIntegerBox = new IntegerBox();
	private IntegerBox decDegIntegerBox = new IntegerBox();
	private IntegerBox raMinIntegerBox = new IntegerBox();
	private IntegerBox decMinIntegerBox = new IntegerBox();
	private DoubleBox raSecDoubleBox = new DoubleBox();
	private DoubleBox decSecDoubleBox = new DoubleBox();
	private Label visibleStatusLabel = new Label("Hi There");
		
	public AstroTarget getAstroTarget() {return astroTarget;}
	public void setAstroTarget(AstroTarget astroTarget) {this.astroTarget = astroTarget;}

	public AstroTargetPanel(StoneEdge3 stoneEdge3)  
	{
		super("Target");
		this.stoneEdge3 = stoneEdge3;
		VerticalPanel verticalPanel1 = new VerticalPanel();
		HorizontalPanel horizontalPanel1 = new HorizontalPanel();
		HorizontalPanel horizontalPanel2 = new HorizontalPanel();
		horizontalPanel2.setSpacing(5);
		horizontalPanel2.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		targetNameTextBox.setSize("8em", "1em");
		Label label = new Label("Name");
		horizontalPanel1.setCellVerticalAlignment(label, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel1.add(label);
		horizontalPanel1.add(targetNameTextBox);
		horizontalPanel1.add(editTargetButton);
		editTargetButton.addClickHandler(new EditButtonClickHandler());
		horizontalPanel1.add(doneEditTargetButton);
		doneEditTargetButton.setVisible(false);
		doneEditTargetButton.addClickHandler(new DoneEditButtonClickHandler());
		slewButton.setVisible(false);
		slewButton.addClickHandler(new SlewButtonClickHandler());
		verticalPanel1.add(horizontalPanel1);
		verticalPanel1.setCellVerticalAlignment(horizontalPanel1, HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel1.add(raDecGrid());
		horizontalPanel2.add(visibleStatusLabel);
		horizontalPanel2.add(slewButton);
		verticalPanel1.add(horizontalPanel2);
		visibleStatusLabel.setVisible(false);

		setEnabledTargetEditBox(false);
		
		setContentWidget(verticalPanel1);

	}
	private Grid raDecGrid()
	{
		raHrsIntegerBox.setSize("2em", "1em");
		decDegIntegerBox.setSize("2em", "1em");
		raMinIntegerBox.setSize("2em", "1em");
		decMinIntegerBox.setSize("2em", "1em");
		raSecDoubleBox.setSize("4em", "1em");
		decSecDoubleBox.setSize("4em", "1em");
		Grid raDecGrid = new Grid(2, 7);
		raDecGrid.setWidget(0, 0, new Label("R.A."));
		raDecGrid.setWidget(1, 0, new Label("Dec"));
		raDecGrid.setWidget(0, 1, raHrsIntegerBox);
		raDecGrid.setWidget(1, 1, decDegIntegerBox);
		raDecGrid.setWidget(0, 2, new Label("Hours"));
		raDecGrid.setWidget(1, 2, new Label("Degs."));
		raDecGrid.setWidget(0, 3, raMinIntegerBox);
		raDecGrid.setWidget(1, 3, decMinIntegerBox);
		raDecGrid.setWidget(0, 4, new Label("Min."));
		raDecGrid.setWidget(1, 4, new Label("Min."));
		raDecGrid.setWidget(0, 5, raSecDoubleBox);
		raDecGrid.setWidget(1, 5, decSecDoubleBox);
		raDecGrid.setWidget(0, 6, new Label("Sec."));
		raDecGrid.setWidget(1, 6, new Label("Sec."));
		
		return raDecGrid;
	}
	private void setEnabledTargetEditBox(boolean enable)
	{
		targetNameTextBox.setEnabled(enable);
		raHrsIntegerBox.setEnabled(enable);
		raMinIntegerBox.setEnabled(enable);
		raSecDoubleBox.setEnabled(enable);
		decDegIntegerBox.setEnabled(enable);
		decMinIntegerBox.setEnabled(enable);
		decSecDoubleBox.setEnabled(enable);
	}
//TODO show slew button only when dome is open and lock owned by me.
	public void update() 
	{
		astroTarget.setAltitudeAzimuthAirmass(stoneEdge3.getObsLongitude(), stoneEdge3.getObsLatitude(), stoneEdge3.getTelescopeStatusPanel().getUpdateDate());
		astroTarget.setVisible(stoneEdge3.getAltLimit());
//		visibleStatusLabel.setVisible(astroTarget.isVisible());
		if (astroTarget.isVisible())
		{
			if (stoneEdge3.getLockPanel().isLockedOwnByMe())
			{
				checkVisibleCommand = "echo \"" + astroTarget.getRa() + " " + astroTarget.getDec() + " " 
							+ astroTarget.getEpoch() + " " + astroTarget.getName() + "\" | visible";
				String[] debugResponse = checkVisibleOkDebugResponse;
				stoneEdge3.getObsCommandBuffer().add(checkVisibleCommand, true, stoneEdge3.isDebug(), debugResponse,
						new checkVisibleAsyncCallback());
			}
		}
		if ( astroTarget.isVisible()) visibleStatusLabel.setText("Target: " + targetNameTextBox.getText() + " is Visible");
		if (!astroTarget.isVisible()) visibleStatusLabel.setText("Target: " + targetNameTextBox.getText() +" is NOT Visible");
		stoneEdge3.getSe3TabLayoutPanel().getTargetingPanel().getSkyc().setTargetRaString(astroTarget.getRa());
		stoneEdge3.getSe3TabLayoutPanel().getTargetingPanel().getSkyc().setTargetDecString(astroTarget.getDec());
		stoneEdge3.getSe3TabLayoutPanel().getTargetingPanel().getSkyc().updateChart(stoneEdge3.getTelescopeStatusPanel().getUpdateDate());
		updateSlewButton();
	}
	private void updateSlewButton()
	{
		if (stoneEdge3.getLockPanel().isLockedOwnByMe() && astroTarget.isVisible() && stoneEdge3.getTelescopeStatusPanel().isSlitOpen()) 
		{
			slewButton.setVisible(true);
		}
		else
		{
			slewButton.setVisible(false);
		}
	}
	public void setTargetInfo()
	{
		try 
		{
			raHrsIntegerBox.setValue(Integer.parseInt(StarCoordUtilities.getRaHrs(astroTarget.getRa())));
			raMinIntegerBox.setValue(Integer.parseInt(StarCoordUtilities.getRaMin(astroTarget.getRa())));
			raSecDoubleBox.setValue(Double.parseDouble(StarCoordUtilities.getRaSec(astroTarget.getRa())));
			decDegIntegerBox.setValue(Integer.parseInt(StarCoordUtilities.getDecDeg(astroTarget.getDec())));
			decMinIntegerBox.setValue(Integer.parseInt(StarCoordUtilities.getDecMin(astroTarget.getDec())));
			decSecDoubleBox.setValue(Double.parseDouble(StarCoordUtilities.getDecSec(astroTarget.getDec())));
			targetNameTextBox.setValue(astroTarget.getName());
			update();
		} 
		catch (NumberFormatException e) 
		{
			stoneEdge3.getStatusTextArea().addStatus("Bad RA value");
		} 
		catch (Se3Exception e) 
		{
			stoneEdge3.getStatusTextArea().addStatus("Bad RA value");
		}
		try 
		{
			decDegIntegerBox.setValue(Integer.parseInt(StarCoordUtilities.getDecDeg(astroTarget.getDec())));
			decMinIntegerBox.setValue(Integer.parseInt(StarCoordUtilities.getDecMin(astroTarget.getDec())));
			decSecDoubleBox.setValue(Double.parseDouble(StarCoordUtilities.getDecSec(astroTarget.getDec())));
		} 
		catch (NumberFormatException e) 
		{
			stoneEdge3.getStatusTextArea().addStatus("Bad Dec value");
		} 
		catch (Se3Exception e) 
		{
			stoneEdge3.getStatusTextArea().addStatus("Bad Dec value");
		}
		targetNameTextBox.setValue(astroTarget.getName());
		visibleStatusLabel.setVisible(true);
		update();

	}
	public void setTargetInfo(String targetName, String raString, String decString)
	{
		try 
		{
			targetNameTextBox.setText(targetName);
			astroTarget.setName(targetName);
			astroTarget.setRa(raString);
			raHrsIntegerBox.setValue(Integer.parseInt(StarCoordUtilities.getRaHrs(astroTarget.getRa())));
			raMinIntegerBox.setValue(Integer.parseInt(StarCoordUtilities.getRaMin(astroTarget.getRa())));
			raSecDoubleBox.setValue(Double.parseDouble(StarCoordUtilities.getRaSec(astroTarget.getRa())));
			astroTarget.setDec(decString);
			decDegIntegerBox.setValue(Integer.parseInt(StarCoordUtilities.getDecDeg(astroTarget.getDec())));
			decMinIntegerBox.setValue(Integer.parseInt(StarCoordUtilities.getDecMin(astroTarget.getDec())));
			decSecDoubleBox.setValue(Double.parseDouble(StarCoordUtilities.getDecSec(astroTarget.getDec())));
			readTargetInfo();
		} catch (Exception e) 
		{
			stoneEdge3.getStatusTextArea().addStatus(e.getMessage());
		} 
		
	}
	private void readTargetInfo()
	{
		visibleStatusLabel.setVisible(false);
		String raString 
			= raHrsIntegerBox.getValue().toString() 
			+ ":" + raMinIntegerBox.getValue().toString()
			+ ":" + raSecDoubleBox.getValue().toString();
		String decString 
			= decDegIntegerBox.getValue().toString() 
			+ ":" + decMinIntegerBox.getValue().toString()
			+ ":" + decSecDoubleBox.getValue().toString();
		try 
		{
			StarCoordUtilities.raStringOk(raString);
			astroTarget.setRa(raString);
		} catch (Se3Exception e) 
		{
			stoneEdge3.getStatusTextArea().addStatus("Bad RA value");
			raHrsIntegerBox.setText("");
			raMinIntegerBox.setText("");
			raSecDoubleBox.setText("");
			raString = null;
			astroTarget.setRa(raString);
		}
		try 
		{
			StarCoordUtilities.decStringOk(decString);
			astroTarget.setDec(decString);
		} catch (Se3Exception e) 
		{
			stoneEdge3.getStatusTextArea().addStatus("Bad DEC value");
			decDegIntegerBox.setText("");
			decMinIntegerBox.setText("");
			decSecDoubleBox.setText("");
			decString = null;
			astroTarget.setDec(decString);
		}
		String name = Utilities.stripWhiteSpaces(targetNameTextBox.getText());
		if (name.length() < 1) name = "newTarget";
		targetNameTextBox.setText(name);
		astroTarget.setName(name);
		astroTarget.setAlias(name);
		visibleStatusLabel.setVisible(true);
		update();
	}
	private class EditButtonClickHandler implements ClickHandler
	{
		@Override
		public void onClick(ClickEvent event) 
		{
			slewButton.setVisible(false);
			editTargetButton.setVisible(false);
			doneEditTargetButton.setVisible(true);
			setEnabledTargetEditBox(true);
		}
	}
	private class DoneEditButtonClickHandler implements ClickHandler
	{
		@Override
		public void onClick(ClickEvent event) 
		{
			editTargetButton.setVisible(true);
			doneEditTargetButton.setVisible(false);
			setEnabledTargetEditBox(false);
			readTargetInfo();
		}
	}
	private class SlewButtonClickHandler implements ClickHandler
	{
		@Override
		public void onClick(ClickEvent event) 
		{
			stoneEdge3.getStatusTextArea().addStatus("Slewing...");
			slewButton.setVisible(false);
			slewButton.setEnabled(false);
			slewCommand = "tx point ra=" + astroTarget.getRa() + " dec=" + astroTarget.getDec() + " equinox=" + astroTarget.getEpoch();
			stoneEdge3.getObsCommandBuffer().add(slewCommand, true, stoneEdge3.isDebug(), slewOKDebugResponse,
					new slewAsyncCallback());
		}
	}
	class checkVisibleAsyncCallback implements AsyncCallback<ObsCommandReturnInfo>
	{
		@Override
		public void onFailure(Throwable caught) 
		{
			astroTarget.setVisible(false);
			visibleStatusLabel.setText(targetNameTextBox.getText() + "is NOT Visible");
			updateSlewButton();
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			stoneEdge3.getStatusTextArea().addStatus("Error: Server error on checkVisible command.");
		}
		@Override
		public void onSuccess(ObsCommandReturnInfo info) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			if (!checkVisibleCommand.equals(info.getCommand()))
			{
				astroTarget.setVisible(false);
				visibleStatusLabel.setText("Target: " + targetNameTextBox.getText() + " is NOT Visible");
				stoneEdge3.getStatusTextArea().addStatus("Error: checkVisible command and callback do not match.");
				return;
			}
			int ilimitIndex = info.getResponse()[0].indexOf("limit");
			if (ilimitIndex < 0)
			{
				astroTarget.setVisible(true);
				visibleStatusLabel.setText("Target: " + targetNameTextBox.getText() + " is Visible");
			}
			else
			{
				stoneEdge3.getStatusTextArea().addStatus(astroTarget.getName() + " is at limit " + info.getResponse()[0].substring(ilimitIndex + 6));			
				astroTarget.setVisible(false);
				visibleStatusLabel.setText("Target: " + targetNameTextBox.getText() + " is NOT Visible");
			}
			updateSlewButton();
		}
	}
	class slewAsyncCallback implements AsyncCallback<ObsCommandReturnInfo>
	{
		@Override
		public void onFailure(Throwable caught) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			stoneEdge3.getStatusTextArea().addStatus("Error: Server error on slew command.");
			slewButton.setEnabled(true);
		}
		@Override
		public void onSuccess(ObsCommandReturnInfo info) 
		{
			stoneEdge3.getObsCommandBuffer().setCommandComplete();
			slewButton.setEnabled(true);
			if (!slewCommand.equals(info.getCommand()))
			{
				stoneEdge3.getStatusTextArea().addStatus("Error: slew command and callback do not match.");
				return;
			}
			int ilimitIndex = info.getResponse()[0].indexOf("done point");
			if (ilimitIndex < 0)
			{
				stoneEdge3.getStatusTextArea().addStatus("Slew Error: " + info.getResponse()[0]);			
			}
			else
			{
				slewButton.setVisible(true);
				stoneEdge3.getStatusTextArea().addStatus("...Done slewing");
				stoneEdge3.getSe3TabLayoutPanel().getTargetingPanel().getCurrentTargetLabel().setText(astroTarget.getName());
				if (stoneEdge3.isDebug())
				{
					stoneEdge3.getTelescopeStatusPanel().getStatusDebugResponse()[4] 
							= "done where ra=" + astroTarget.getRa() + " dec=" + astroTarget.getDec() + " equinox=2013.434 ha=0.003 secz=1.00 alt=90.0 az=260.7 slewing=0";
				}
				boolean overideLock = false;
				stoneEdge3.getTelescopeStatusPanel().refreshTelescopeStatus(overideLock);
			}
		}
	}

}
