package se.esss.litterbox.stoneedgeivgwt.client.mqttdata;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MqttServiceAsync 
{
	void getJsonArray(String topic, AsyncCallback<String[][]> callback);
	void getMessage(String topic, AsyncCallback<byte[]> callback);
	void publishMessage(String topic, byte[] message, boolean settingsEnabled, AsyncCallback<String> callback);
	void publishJsonArray(String topic, String[][] jsonArray, boolean settingsEnabled, AsyncCallback<String> callback);
}
