package se.esss.litterbox.stoneedgeivgwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import se.esss.litterbox.stoneedgeivgwt.client.EntryPointAppService;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class EntryPointAppServiceImpl extends RemoteServiceServlet implements EntryPointAppService 
{
	@Override
	public String[] gskelServerTest(String name) throws Exception 
	{
		System.out.println(name);
		try {Thread.sleep(3000);} catch (InterruptedException e) {}
		String[] answer = {"high", "low"};
		return answer;
	}

	@Override
	public String[] checkIpAddress() throws Exception 
	{
		try
		{
			String userName = getThreadLocalRequest().getUserPrincipal().getName();
			boolean userOkay = getThreadLocalRequest().isUserInRole("webAppSettingsPermitted");
			String[] returnData = new String[2];
			returnData[0] = userName;
			returnData[1] = Boolean.toString(userOkay);
			return returnData;
		}
		catch (Exception e)
		{
			String[] returnData = new String[2];
			returnData[0] = "Backdoor";
			returnData[1] = Boolean.toString(true);
			return returnData;
		}
	}

}
