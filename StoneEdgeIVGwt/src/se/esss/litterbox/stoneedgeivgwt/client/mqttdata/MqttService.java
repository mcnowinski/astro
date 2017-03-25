package se.esss.litterbox.stoneedgeivgwt.client.mqttdata;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("mqtt")
public interface MqttService  extends RemoteService
{
	String[][] getJsonArray(String topic) throws Exception;
	String publishJsonArray(String topic, String[][] jsonArray,  boolean settingsEnabled) throws Exception;
	byte[] getMessage(String topic) throws Exception;
	String publishMessage(String topic, byte[] message, boolean settingsEnabled) throws Exception;
}
