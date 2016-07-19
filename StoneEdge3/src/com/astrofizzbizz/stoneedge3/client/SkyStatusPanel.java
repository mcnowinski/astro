package com.astrofizzbizz.stoneedge3.client;

import com.astrofizzbizz.stoneedge3.shared.ObsCommandReturnInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SkyStatusPanel extends Se3TabLayoutScrollPanel
{
	private String skyStatusCommand = "tx slit; tx temps; tx taux; tx mets; sun; moon; who";
	private String[] skyStatusDebugResponse 
		= {
			"done slit slit=closed",
			"done temps amb=12.2 pri=12.8 sec=11.0",
			"done taux ovolts=2.944 irvolts=0.106 cloud=0.64 rain=0 dew=2.96",
			"done mets oat=9.8 windspeed=0 winddir=154 humidity=84 bar=1019.3 sun=0 rainfall=0.57",
			"15:38:54.53 -19:29:25.3 2013.883 sun alt=-45.9",
			"04:57:38.55 +18:48:39.4 2013.883 moon alt=30.1 phase=0.90 lunation=1124",
			"remote   pts/0        2013-11-18 20:03 (c83-251-175-168.bredband.comhem.se)",
			"remote   pts/1        2013-11-18 20:32 (c83-251-175-168.bredband.comhem.se)",
		};
	private Label slitLabel = new Label("");
	private Label tempsLabel = new Label("");
	private Label tempsAuxLabel = new Label("");
	private Label metsLabel = new Label("");
	private Label sunLabel = new Label("");
	private Label moonLabel = new Label("");
	private Label[] whoLabel = new Label[5];
	Image darkSkyimage;
	Grid whoGrid;
	int version = 1;

	public SkyStatusPanel(StoneEdge3 stoneEdge3)
	{
		super(stoneEdge3);
		VerticalPanel vp1 = new VerticalPanel();
		Grid statusGrid = new Grid(7, 2);
		statusGrid.setBorderWidth(1);
		
		statusGrid.setWidget(0, 0, new Label("Slit Status:"));
		statusGrid.setWidget(1, 0, new Label("Temps: "));
		statusGrid.setWidget(2, 0, new Label("Temps Aux: "));
		statusGrid.setWidget(3, 0, new Label("Mets: "));
		statusGrid.setWidget(4, 0, new Label("Sun: "));
		statusGrid.setWidget(5, 0, new Label("Moon: "));
		statusGrid.setWidget(6, 0, new Label("Who: "));
		statusGrid.setWidget(0, 1, slitLabel);
		statusGrid.setWidget(1, 1, tempsLabel);
		statusGrid.setWidget(2, 1, tempsAuxLabel);
		statusGrid.setWidget(3, 1, metsLabel);
		statusGrid.setWidget(4, 1, sunLabel);
		statusGrid.setWidget(5, 1, moonLabel);
		
		whoGrid = new Grid(5, 1);
		
		for (int ii = 0; ii < 5; ++ii)
		{
			whoLabel[ii] = new Label("");
			whoGrid.setWidget(ii, 0, whoLabel[ii]);
			whoLabel[ii].setVisible(false);
		}
		statusGrid.setWidget(6, 1, whoGrid);
		vp1.add(statusGrid);
		darkSkyimage = new Image("http://cleardarksky.com/csk/getcsk.php?id=SmnCA");
		vp1.add(darkSkyimage);
		add(vp1);

	}
	public void update()
	{
//		darkSkyimage = new Image("http://cleardarksky.com/csk/getcsk.php?id=SmnCA");
		getStoneEdge3().getStatusTextArea().addStatus("Getting Sky Status...");
		clearWho();
		getStoneEdge3().getObsCommandBuffer().add(skyStatusCommand, true, getStoneEdge3().isDebug(), skyStatusDebugResponse,
				new skyStatusAsyncCallback());
		darkSkyimage = new Image("http://cleardarksky.com/csk/getcsk.php?id=SmnCA");

	}
	private void setTxtToAllLabels(String text)
	{
		slitLabel.setText(text);
		tempsLabel.setText(text);
		tempsAuxLabel.setText(text);
		metsLabel.setText(text);
		sunLabel.setText(text);
		moonLabel.setText(text);
		for (int ii = 11; ii < 5; ++ii)
		{
			whoLabel[ii].setText(text);
			whoLabel[ii].setVisible(false);
		}
	}
	private void clearWho()
	{
		for (int ii = 11; ii < 5; ++ii)
		{
			whoLabel[ii].setText("");
			whoLabel[ii].setVisible(false);
		}
	}
	class skyStatusAsyncCallback implements AsyncCallback<ObsCommandReturnInfo>
	{
		@Override
		public void onFailure(Throwable caught) 
		{
			getStoneEdge3().getObsCommandBuffer().setCommandComplete();
			getStoneEdge3().getStatusTextArea().addStatus("Error: Failed to get sky status");
			setTxtToAllLabels("Error");
		}
		@Override
		public void onSuccess(ObsCommandReturnInfo info) 
		{
			getStoneEdge3().getObsCommandBuffer().setCommandComplete();
			if (!skyStatusCommand.equals(info.getCommand()))
			{
				getStoneEdge3().getStatusTextArea().addStatus("Error: sky status command and callback do not match.");
				setTxtToAllLabels("Error");
				return;
			}
			if (info.getResponse() == null)
			{
				getStoneEdge3().getStatusTextArea().addStatus("Error: No Sky Status response");
				setTxtToAllLabels("Error");
				return;
			}
			if (info.getResponse().length < 6)
			{
				getStoneEdge3().getStatusTextArea().addStatus("Error: Bad Sky Status response");
				setTxtToAllLabels("Error");
				return;
			}
			int iindex = info.getResponse()[0].indexOf("done slit");
			if (iindex >= 0)
			{
				slitLabel.setText(info.getResponse()[0].substring(10));
			}
			else
			{
				slitLabel.setText("Error");
			}
			iindex = info.getResponse()[1].indexOf("done temps");
			if (iindex >= 0)
			{
				tempsLabel.setText(info.getResponse()[1].substring(11));
			}
			else
			{
				tempsLabel.setText("Error");
			}
			iindex = info.getResponse()[2].indexOf("done taux");
			if (iindex >= 0)
			{
				tempsAuxLabel.setText(info.getResponse()[2].substring(10));
			}
			else
			{
				tempsAuxLabel.setText("Error");
			}
			iindex = info.getResponse()[3].indexOf("done mets");
			if (iindex >= 0)
			{
				metsLabel.setText(info.getResponse()[3].substring(10));
			}
			else
			{
				metsLabel.setText("Error");
			}
			sunLabel.setText(info.getResponse()[4]);
			moonLabel.setText(info.getResponse()[5]);
			int responseLength = info.getResponse().length;
			if (responseLength > 6)
			{
				for (int ii = 6; ii < responseLength; ++ii)
				{
					whoLabel[ii - 6].setText(info.getResponse()[ii]);
					whoLabel[ii - 6].setVisible(true);
				}
			}
			else
			{
				whoGrid.setVisible(false);
			}
			getStoneEdge3().getStatusTextArea().addStatus("Sky Status complete.");

		}
		
	}
	

}
