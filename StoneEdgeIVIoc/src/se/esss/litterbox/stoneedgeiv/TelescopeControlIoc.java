package se.esss.litterbox.stoneedgeiv;

import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import se.esss.litterbox.icecube.ioc.IceCubePeriodicPollIoc;

public class TelescopeControlIoc extends IceCubePeriodicPollIoc
{
	public TelescopeControlIoc(String clientId, String mqttBrokerInfoFilePath) throws Exception 
	{
		super(clientId, mqttBrokerInfoFilePath);
	}
	@Override
	public byte[] getDataFromGizmo() 
	{
		return null;
	}
	@SuppressWarnings("unchecked")
	@Override
	public void handleBrokerMqttMessage(String topic, byte[] message) 
	{
		String[][] jsonData = null;
		boolean debug = false;
		try 
		{
			jsonData = getJsonArray(topic, message);
			if (getJsonValue("debug",  jsonData).toLowerCase().equals("true")) debug = true;
			String [] commandParsed = topic.substring(topic.indexOf("/set/") + 5).split("/");
			String command = commandParsed[0];
			if (commandParsed.length > 1)
			{
				for (int ii = 1; ii < commandParsed.length; ++ii)
				{
					command = command + " " + commandParsed[ii];
				}
			}
			for (int ii = 0; ii < jsonData.length; ++ii)
			{
				if (!jsonData[ii][0].equals("debug")) command = command + " " + jsonData[ii][0] + "=" + jsonData[ii][1];
			}
			System.out.println(command);
			String response = "done debug=true";
			if (!debug)
			{
				response = this.runExternalProcess(command, true, true)[0];
			}
			System.out.println(response);
			String[] responseParsed = response.split(" ");

			JSONObject outputData = new JSONObject();

			for (int ii = 0; ii < responseParsed.length; ++ii)
			{
				int equalPos = responseParsed[ii].indexOf("=");
				String key = "";
				String val = "";
				if (equalPos > 0)
				{
					key = responseParsed[ii].substring(0, equalPos);
					val = responseParsed[ii].substring(equalPos + 1);
					outputData.put(key, val);
					
//					System.out.println(key + ":" + val);
				}
			}
			publishMessage("tel/done/" + topic.substring(topic.indexOf("/set/") + 5), outputData.toJSONString().getBytes(), 0, true);
		}
		catch (Exception e) 
		{
			System.out.println("Error: " + e.getMessage()); 
			return;
		}
				
	}
	@SuppressWarnings("rawtypes")
	private String[][] getJsonArray(String topic, byte[] message) throws Exception  
	{
		JSONParser parser = new JSONParser();		
		JSONObject jsonData;
		try {jsonData = (JSONObject) parser.parse(new String(message));} 
		catch (ParseException e) {throw new Exception("Cannot JSON parse the data on " + topic);}
		int numKeys = jsonData.keySet().size();
		String[][] data = new String[numKeys][2];
		int ikey = 0;
		try {jsonData = (JSONObject) parser.parse(new String(message));} 
		catch (ParseException e) {throw new Exception("Cannot JSON parse the data on " + topic);}
		Iterator iterJsonData = jsonData.keySet().iterator();
		while (iterJsonData.hasNext())
		{
			data[ikey][0] = (String) iterJsonData.next();
			data[ikey][1] = (String) jsonData.get(data[ikey][0]);
			++ikey;
		}
		return data;
	}
	public String getJsonValue(String key, String[][] jsonData) throws Exception
	{
		for (int ii = 0; ii < jsonData.length; ++ii)
		{
			if (jsonData[ii][0].equals(key)) return jsonData[ii][1];
		}
		throw new Exception(key + " not found");
	}
	public static void main(String[] args) throws Exception 
	{
		int periodicPoll = 100;//Integer.parseInt(args[0]);
		TelescopeControlIoc ioc = new TelescopeControlIoc("TelescopeControlIoc", "stoneEdgeMqttBroker.dat");
		ioc.setPeriodicPollPeriodmillis(periodicPoll);
		ioc.startIoc("tel/set/#", "tel/done");
	}
}
