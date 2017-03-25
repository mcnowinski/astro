package se.esss.litterbox.stoneedgeivgwt.client.mqttdata;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import se.esss.litterbox.stoneedgeivgwt.client.EntryPointApp;

public abstract class MqttData 
{
	public static final int JSONDATA = 1;
	public static final int BYTEDATA = 2;
	private String[][] jsonData;
	private byte[] message;
	private int dataType = 0;
	private String topic = null;
	private EntryPointApp entryPointApp;
	
	public String[][] getJsonData() {return jsonData;}
	public byte[] getMessage() {return message;}
	public int getDataType() {return dataType;}
	public String getTopic() {return topic;}
	public EntryPointApp getEntryPointApp() {return entryPointApp;}

	public MqttData(String topic, int dataType, int timerPeriodMillis, EntryPointApp entryPointApp)
	{
		this.topic = topic;
		this.dataType = dataType;
		this.entryPointApp = entryPointApp;
		if (dataType == JSONDATA)
		{
			JsonDataTimer jdt = new JsonDataTimer(this);
			jdt.scheduleRepeating(timerPeriodMillis);
		}
		if (dataType == BYTEDATA)
		{
			ByteDataTimer bdt = new ByteDataTimer(this);
			bdt.scheduleRepeating(timerPeriodMillis);
		}
	}
	void processData()
	{
		doSomethingWithData();
	}
	public String getJsonValue(String key) throws Exception
	{
		for (int ii = 0; ii < jsonData.length; ++ii)
		{
			if (jsonData[ii][0].equals(key)) return jsonData[ii][1];
		}
		throw new Exception(key + " not found");
	}
	public abstract void doSomethingWithData();
	private static class GetJsonDataAsyncCallback implements AsyncCallback<String[][]>
	{
		MqttData mqttData;
		GetJsonDataAsyncCallback(MqttData mqttData)
		{
			this.mqttData = mqttData;
		}
		@Override
		public void onFailure(Throwable caught) 
		{
			GWT.log("Error on GetJsonDataAsyncCallback: " +  mqttData.topic);
		}
		@Override
		public void onSuccess(String[][] result) 
		{
			mqttData.jsonData = result;
			mqttData.processData();
//			for (int ii = 0; ii < result.length; ++ii)
//				mqttData.entryPointApp.getSetupApp().getStatusTextArea().addStatus(mqttData.topic + " " + result[ii][0] + " " +  result[ii][1]);
		}
	}
	private static class GetByteDataAsyncCallback implements AsyncCallback<byte[]>
	{
		MqttData mqttData;
		GetByteDataAsyncCallback(MqttData mqttData)
		{
			this.mqttData = mqttData;
		}
		@Override
		public void onFailure(Throwable caught) 
		{
			GWT.log("Error on GetByteDataAsyncCallback: " +  mqttData.topic);
		}
		@Override
		public void onSuccess(byte[] result) 
		{
			mqttData.message = result;
			mqttData.processData();
		}
	}
	private static class JsonDataTimer extends Timer
	{
		MqttData mqttData;
		JsonDataTimer(MqttData mqttData)
		{
			this.mqttData = mqttData;
		}
		@Override
		public void run() 
		{
			mqttData.entryPointApp.getSetup().getMqttService().getJsonArray(mqttData.topic, new GetJsonDataAsyncCallback(mqttData));
		}
		
	}
	private static class ByteDataTimer extends Timer
	{
		MqttData mqttData;
		ByteDataTimer(MqttData mqttData)
		{
			this.mqttData = mqttData;
		}
		@Override
		public void run() 
		{
			mqttData.entryPointApp.getSetup().getMqttService().getMessage(mqttData.topic, new GetByteDataAsyncCallback(mqttData));
		}
		
	}
	
}
