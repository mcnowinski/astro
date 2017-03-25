package se.esss.litterbox.stoneedgeivgwt.server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import se.esss.litterbox.icecube.simplemqtt.SimpleMqttClient;


public class MqttTester  extends SimpleMqttClient
{
	public MqttTester(String clientIdBase, String mqttBrokerInfoFilePath) throws Exception 
	{
		super(clientIdBase, mqttBrokerInfoFilePath, false);
	}
	@Override
	public void connectionLost(Throwable arg0) {}
	@Override
	public void newMessage(String arg1, byte[] arg2) 
	{
		System.out.println("Received topic: " + arg1 + " with message: " + new String(arg2));
		if (arg1.indexOf("/get/info") >= 0)
		{
			try
			{
				JSONParser parser = new JSONParser();		
				JSONObject jsonData = (JSONObject) parser.parse(new String(arg2));
				String key1 = (String) jsonData.get("key1");
				System.out.println("key1 = " + key1);
			}
			catch (ParseException nfe) {nfe.printStackTrace();}
		}
	}
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception 
	{
		MqttTester mqttTester = new MqttTester("MqttTesterIoc", "../itsmqtttestbroker.dat");

		boolean retained = true;
		JSONObject outputData = new JSONObject();

		outputData.put("testDevice01", "10.3");
		outputData.put("testDevice02", "52.6");
		outputData.put("testDevice03", "78.9");
		mqttTester.publishMessage("mqttTester/get/gauges", outputData.toJSONString().getBytes(), 0, retained);

		String lineChartData = "1,2,3,4,5, 10.3,9,8.5,7,8.4 ,6,7.9,12.3,9,4.2";
		mqttTester.publishMessage("mqttTester/get/line", lineChartData.getBytes(), 0, retained);

		String scatterPlotData = "1,2,3,4,5,  10.3, 9, 8.5, 7, 8.4, 2.6,1.3,5.2,3.1,5, 6,7.9,12.3,9,4.2";
		mqttTester.publishMessage("mqttTester/get/scatter", scatterPlotData.getBytes(), 0, retained);
//		ioCtester.subscribe("mqttTester/set", 0);
		
		
	}

}
