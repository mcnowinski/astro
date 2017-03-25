package se.esss.litterbox.stoneedgeivgwt.client.callbacks;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import se.esss.litterbox.stoneedgeivgwt.client.EntryPointApp;

public class CheckIpAddresslAsyncCallback implements AsyncCallback<String[]>
{
	EntryPointApp entryPointApp;
	public CheckIpAddresslAsyncCallback(EntryPointApp entryPointApp)
	{
		this.entryPointApp =  entryPointApp;
	}

	@Override
	public void onFailure(Throwable caught) 
	{
		GWT.log("Error in getting IP Address of client. Error: " + caught.getMessage());
	}

	@Override
	public void onSuccess(String[] result) 
	{
		boolean ipOkay = Boolean.parseBoolean(result[1]);
		if (!ipOkay)
		{
			entryPointApp.getSetup().getMessageDialog().setImageUrl("images/warning.jpg");
			entryPointApp.getSetup().getMessageDialog().setMessage("Warning", result[0] + ": You can look but not touch", true);
			WaitTimer waitTimer = new WaitTimer();
			waitTimer.scheduleRepeating(200);
			waitTimer.run();
		}
		else
		{
			entryPointApp.getSetup().setSettingsPermitted(true);
			entryPointApp.initializeTabs();
		}
		
	}
	private class WaitTimer extends Timer
	{
		@Override
		public void run() 
		{
			if (!entryPointApp.getSetup().getMessageDialog().isShowing())
			{
				entryPointApp.getSetup().setSettingsPermitted(false);
				cancel();
			}

		}
		
	}
	
}
