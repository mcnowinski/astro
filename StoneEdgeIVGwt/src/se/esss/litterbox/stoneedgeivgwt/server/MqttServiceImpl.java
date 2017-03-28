package se.esss.litterbox.stoneedgeivgwt.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import se.esss.litterbox.stoneedgeivgwt.client.mqttdata.MqttService;


@SuppressWarnings("serial")
public class MqttServiceImpl extends RemoteServiceServlet implements MqttService
{
	String[] topics = {
			"domeCam/image/jpg", 
			"domeCam/image/date", 
			"tel/done/tx/slit", 
			"tel/done/tx/track", 
			"tel/done/tx/dome", 
			"tel/done/tx/lamps",
			"tel/done/tx/where",
			"tel/done/tx/lock",
			"tel/done/tx/focus",
			"tel/done/time",
			"tel/done/pfilter",
			"tel/done/tx/temps",
			"tel/done/tx/taux",
			"tel/done/tx/mets",
			"tel/done/sun"};
	byte[][] messages;
	boolean mqttClientInitialized = false;
	long[] domeImageCounterArray = new long[10];
	int domeImageCounterArrayIndex = -1;
	MqttServiceImpClient mqttClient;
	
	boolean updateDomeCamImage = true;

	public void init()
	{
		for (int ii = 0; ii < 10; ++ii) domeImageCounterArray[ii] = -1;
		if (topics.length < 1) return;
		boolean cleanSession = false;
		int subscribeQos = 0;
		messages = new byte[topics.length][];
		try 
		{
			mqttClient = new MqttServiceImpClient(this, "StoneEdgeIVWebApp", getMqttDataPath(), cleanSession);
			for (int ii = 0; ii < topics.length; ++ii)
			{	
				messages[ii] = "noData".getBytes();
				mqttClient.subscribe(topics[ii], subscribeQos);
			}
			mqttClientInitialized = true;
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	private String getMqttDataPath() throws Exception
	{
		File tmpFile = new File(getServletContext().getRealPath("./"));
		tmpFile = new File(tmpFile.getParent());
		tmpFile = new File(tmpFile.getParent());
		return tmpFile.getPath() + "/stoneEdgeMqttBroker.dat";
		
	}
	public void destroy()
	{
		if (!mqttClientInitialized) return;
		try {mqttClient.unsubscribeAll();} catch (Exception e) {mqttClient = null;}
		try {mqttClient.disconnect();} catch (Exception e) {mqttClient = null;}
	}
	public void setMessage(String topic, byte[] message)
	{
		int itopic = getTopicIndex(topic);
		if (itopic >= 0)
		{
			messages[itopic] = new byte[message.length];
			for (int ii = 0; ii < message.length; ++ii) messages[itopic][ii] = message[ii];
		}
		if (topic.indexOf(topics[0]) > -1) writeDomeCamImage(message);
	}
	private int getTopicIndex(String topic)
	{
		int itopic = -1;
		for (int ii = 0; ii < topics.length; ++ii) 
			if (topic.equals(topics[ii])) itopic = ii;
		return itopic;
		
	}
	@SuppressWarnings("rawtypes")
	@Override
	public String[][] getJsonArray(String topic) throws Exception  
	{
		if (!mqttClientInitialized) throw new Exception("Mqtt client not initialized.");

		int itopic = getTopicIndex(topic);
		if (itopic < 0) throw new Exception(topic + " not found");
		JSONParser parser = new JSONParser();		
		JSONObject jsonData;
		try {jsonData = (JSONObject) parser.parse(new String(messages[itopic]));} 
		catch (ParseException e) {throw new Exception("Cannot JSON parse the data on " + topic);}
		int numKeys = jsonData.keySet().size();
		String[][] data = new String[numKeys][2];
		int ikey = 0;
		try {jsonData = (JSONObject) parser.parse(new String(messages[itopic]));} 
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
	@SuppressWarnings("unchecked")
	@Override
	public String publishJsonArray(String topic, String[][] jsonArray,  boolean settingsEnabled) throws Exception
	{
		if (!mqttClientInitialized) throw new Exception("Mqtt client not initialized.");
		if (!settingsEnabled) throw new Exception("Settings to Mqtt Broker are not permitted");
		JSONObject outputData = new JSONObject();
		for (int ii = 0; ii < jsonArray.length; ++ii)
			outputData.put(jsonArray[ii][0], jsonArray[ii][1]);
		mqttClient.publishMessage(topic, outputData.toJSONString().getBytes(), 0, true);
		return "ok";

	}
	@Override
	public byte[] getMessage(String topic) throws Exception
	{
		if (!mqttClientInitialized) throw new Exception("Mqtt client not initialized.");
		int itopic = getTopicIndex(topic);
		if (itopic < 0) throw new Exception(topic + " not found");
		if (messages[itopic].length < 1) throw new Exception("Zero length byte array on " + topic);
		return messages[itopic];
	}
	@Override
	public String publishMessage(String topic, byte[] message, boolean settingsEnabled) throws Exception
	{
		if (!mqttClientInitialized) throw new Exception("Mqtt client not initialized.");
		if (!settingsEnabled) throw new Exception("Settings to Mqtt Broker are not permitted");
		mqttClient.publishMessage(topic, message, 0, true);
		return "ok";
	}
	private void writeDomeCamImage(byte[] imageInByte) 
	{
		if (!updateDomeCamImage) return;

		try
		{
			String[][] jsonArray =  getJsonArray("domeCam/image/date");
			long imageCounter = 0;
			for (int ii = 0; ii < jsonArray.length; ++ii)
			{
				if (jsonArray[ii][0].equals("counter")) imageCounter = Long.parseLong(jsonArray[ii][1]) ;
			}
			++domeImageCounterArrayIndex;
			if (domeImageCounterArrayIndex > 9) domeImageCounterArrayIndex = 0;
			int deleteIndex = domeImageCounterArrayIndex + 1;
			domeImageCounterArray[domeImageCounterArrayIndex] = imageCounter;
			if (deleteIndex > 9) deleteIndex = 0;
			if (domeImageCounterArray[deleteIndex] >= 0)
			{
				File deleteFile = new File(getServletContext().getRealPath("images/domeCamImage" + Long.toString(domeImageCounterArray[deleteIndex]) + ".jpg"));
				deleteFile.delete();
				
			}
			InputStream in = new ByteArrayInputStream(imageInByte);
			BufferedImage bImageFromConvert = ImageIO.read(in);
			File newFile = new File(getServletContext().getRealPath("images/domeCamImage" + Long.toString(imageCounter) + ".jpg"));
	
			ImageIO.write(bImageFromConvert, "jpg", newFile);
			
		}
		catch (Exception e) {System.out.println("Error in writeDomeCamImage: " + e.getMessage());}

	}
}
