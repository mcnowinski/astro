package se.esss.litterbox.stoneedgeivgwt.client.gskel;

import com.google.gwt.user.client.Timer;

public abstract class GskelLoadWaiter extends Timer
{
	private int itask = -1;
	public int getItask() {return itask;}
	public GskelLoadWaiter(int loopTimeMillis, int itask)
	{
		this.itask = itask;
		scheduleRepeating(loopTimeMillis);
	}
	public abstract boolean isLoaded();
	public abstract void taskAfterLoad();
	@Override
	public void run() 
	{
		if (!isLoaded()) return;
		cancel();
		taskAfterLoad();
	}

}
