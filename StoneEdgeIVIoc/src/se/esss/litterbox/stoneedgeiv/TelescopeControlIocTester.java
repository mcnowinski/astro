package se.esss.litterbox.stoneedgeiv;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.json.simple.JSONObject;

import se.esss.litterbox.icecube.simplemqtt.SimpleMqttClient;


public class TelescopeControlIocTester  extends SimpleMqttClient
{
	public TelescopeControlIocTester(String clientIdBase, String mqttBrokerInfoFilePath) throws Exception 
	{
		super(clientIdBase, mqttBrokerInfoFilePath, false);
	}
	@Override
	public void connectionLost(Throwable arg0) {}
	@Override
	public void newMessage(String topic, byte[] message) 
	{
		try
		{
			if (topic.equals("domeCam/image/date"))
			{
//				JSONParser parser = new JSONParser();		
//				JSONObject jsonData = (JSONObject) parser.parse(new String(message));
//				String info = (String) jsonData.get("date");
//				System.out.println("Received image on: " + new String(info));
			}
			if (topic.equals("domeCam/image/jpg"))
			{
				InputStream in = new ByteArrayInputStream(message);
				BufferedImage bImageFromConvert = ImageIO.read(in);
				File newFile = new File("domeImageSnatch.jpg");
		
				ImageIO.write(bImageFromConvert, "jpg", newFile);
				
			}
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.getMessage()); 
			return;
		}
	}
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception 
	{
		TelescopeControlIocTester ioCtester = new TelescopeControlIocTester("TelescopeControlIocTester", "stoneEdgeMqttBroker.dat");

		boolean retained = true;
		JSONObject outputData = new JSONObject();

		outputData.put("all", "off");
		outputData.put("debug", "false");
		ioCtester.publishMessage("tel/set/tx/lamps", outputData.toJSONString().getBytes(), 0, retained);
		ioCtester.subscribe("domeCam/image/#", 0);
		
	}

}
