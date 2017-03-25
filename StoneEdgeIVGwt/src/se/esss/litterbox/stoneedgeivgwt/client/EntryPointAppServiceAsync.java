package se.esss.litterbox.stoneedgeivgwt.client;


import com.google.gwt.user.client.rpc.AsyncCallback;

public interface EntryPointAppServiceAsync 
{
	void checkIpAddress(AsyncCallback<String[]> callback);
	void gskelServerTest(String name, AsyncCallback<String[]> callback);
}
