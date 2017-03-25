package se.esss.litterbox.stoneedgeivgwt.client.gskel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

public abstract class GskelSettingButtonGrid extends Grid
{
	Button changeButton  = new Button("Change");
	Button setButton  = new Button("Set");
	Button cancelButton  = new Button("Cancel");
	boolean settingsPermitted = false;
	boolean settingsEnabled = false;
	public boolean isSettingsEnabled() {return settingsEnabled;}
	public GskelSettingButtonGrid(boolean settingsPermitted)
	{
		super(1, 3);
		this.settingsPermitted = settingsPermitted;
		HTMLTable.CellFormatter formatter = getCellFormatter();
		setWidth("100%");
		setWidget(0, 0, setButton);
		setWidget(0, 1, changeButton);
		setWidget(0, 2, cancelButton);
		setButton.setWidth("5.0em");
		changeButton.setWidth("5.0em");
		cancelButton.setWidth("5.0em");
		formatter.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
		formatter.setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);	
		formatter.setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
		formatter.setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_MIDDLE);	
		formatter.setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_RIGHT);
		formatter.setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_MIDDLE);
		setButton.addClickHandler(new ActionButtonClickHandler(setButton));
		changeButton.addClickHandler(new ActionButtonClickHandler(changeButton));
		cancelButton.addClickHandler(new ActionButtonClickHandler(cancelButton));
		setButtonState(false);
	}
	private void setButtonState(boolean enabled)
	{
		setButton.setVisible(enabled);
		changeButton.setVisible(!enabled);
		cancelButton.setVisible(enabled);
		enableSettingsInput(enabled);
		settingsEnabled = enabled;
	}
	public abstract void enableSettingsInput(boolean enabled);
	public abstract void doSettings();
	class ActionButtonClickHandler implements ClickHandler
	{
		Button button;
		ActionButtonClickHandler(Button button)
		{
			this.button = button;
		}
		@Override
		public void onClick(ClickEvent event) 
		{
			if (button.equals(changeButton))
			{
				if (settingsPermitted) setButtonState(true);
			}
			if (button.equals(cancelButton))
			{
				setButtonState(false);
			}
			if (button.equals(setButton))
			{
				doSettings();
				setButtonState(false);
			}
			
		}
		
	}
}
