package com.astrofizzbizz.stoneedge3.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class OptionDialog extends DialogBox
{
	private TextArea informationtext = new TextArea();
	private double defaultWidthEM = 25.0;
	private double defaultHeightEM = 15.0;
	private Button choice1button = new Button("Choice 1");
	private Button choice2button = new Button("Choice 2");
	OptionDialogInterface optionDialogInterface = null;

	public OptionDialog()
	{
		super();
		setText("Choice");
		choice1button.addClickHandler(new ClickHandler() 
		{
			public void onClick(ClickEvent event) 
			{
				hide();
				optionDialogInterface.optionDialogChoice(choice1button.getText());
			}
		});
		choice2button.addClickHandler(new ClickHandler() 
		{
			public void onClick(ClickEvent event) 
			{
				hide();
				optionDialogInterface.optionDialogChoice(choice2button.getText());
			}
		});
		VerticalPanel dialogVPanel = new VerticalPanel();
		setInformationTextSize(defaultWidthEM, defaultHeightEM);
		dialogVPanel.add(informationtext);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);

		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.add(choice1button);
		buttonPanel.add(choice2button);
		dialogVPanel.add(buttonPanel);

		setWidget(dialogVPanel);
	}
	private void setInformationTextSize(double widthEM, double heightEM)
	{
		informationtext.setSize(Double.toString(widthEM) + "em", Double.toString(heightEM) + "em");
	}
	public void setOption(String title, String message, String choice1ButtonText, String choice2ButtonText, OptionDialogInterface optionDialogInterface)
	{
		setText(title);
		informationtext.setText(message);
		choice1button.setText(choice1ButtonText);
		choice2button.setText(choice2ButtonText);
		this.optionDialogInterface = optionDialogInterface;
        center();
        show();
	}
	public void setOption(String title, String message, String choice1ButtonText, String choice2ButtonText,  OptionDialogInterface optionDialogInterface, double widthEM, double heightEM)
	{
		setInformationTextSize(widthEM, heightEM);
		setOption(title, message, choice1ButtonText, choice2ButtonText, optionDialogInterface);
	}
	public interface OptionDialogInterface 
	{
		void optionDialogChoice(String choiceButtonText);

	}

}
