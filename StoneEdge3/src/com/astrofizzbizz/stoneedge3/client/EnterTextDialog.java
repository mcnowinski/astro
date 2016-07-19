package com.astrofizzbizz.stoneedge3.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EnterTextDialog extends DialogBox
{
	private Button enterButton = new Button("Enter");
	private Button cancelButton = new Button("Cancel");
	private Label enterLabel = new Label();
	private Label unitsLabel = new Label();
	private TextBox editTextBox = new TextBox();
	EnterTextDialogInterface enterTextDialogInterface = null;

	
	public EnterTextDialog()
	{
		super();
		setText("Enter");
		enterButton.addClickHandler(new ClickHandler() 
		{
			public void onClick(ClickEvent event) 
			{
				hide();
				enterTextDialogInterface.enterTextDialogChoice(editTextBox.getText(), true);
			}
		});
		cancelButton.addClickHandler(new ClickHandler() 
		{
			public void onClick(ClickEvent event) 
			{
				hide();
				enterTextDialogInterface.enterTextDialogChoice(editTextBox.getText(), false);
			}
		});
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		HorizontalPanel editPanel = new HorizontalPanel();

		editPanel.add(enterLabel);
		editPanel.add(editTextBox);
		editPanel.add(unitsLabel);
		dialogVPanel.add(editPanel);
		
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.add(enterButton);
		buttonPanel.add(cancelButton);
		dialogVPanel.add(buttonPanel);

		setWidget(dialogVPanel);
	}
	public void setOption(String title, String enterLabelText, String enterTextBoxText, String unitsLabelText, EnterTextDialogInterface enterTextDialogInterface, int ix, int iy)
	{
		setText(title);
		enterLabel.setText(enterLabelText);
		editTextBox.setText(enterTextBoxText);
		unitsLabel.setText(unitsLabelText);
		this.enterTextDialogInterface = enterTextDialogInterface;
		setPopupPosition(ix, iy);
//        center();
        show();
	}

	public interface EnterTextDialogInterface 
	{
		void enterTextDialogChoice(String enterText, boolean useText);

	}
}
