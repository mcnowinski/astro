package se.esss.litterbox.stoneedgeiv;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Date;

import javax.imageio.ImageIO;

import org.json.simple.JSONObject;

import se.esss.litterbox.icecube.ioc.IceCubePeriodicPollIoc;

public class DomeCamIoc extends IceCubePeriodicPollIoc
{
	public DomeCamIoc(String clientId, String mqttBrokerInfoFilePath) throws Exception 
	{
		super(clientId, mqttBrokerInfoFilePath);
	}
	@SuppressWarnings("unchecked")
	@Override
	public byte[] getDataFromGizmo() 
	{
		byte[] imageInByte = null;
		try
		{
			int scaledWidth = 800;
			int scaledHeight = 450;
			BufferedImage inputImage = null;
			URL url = new URL("http://axis:80/jpg/image.jpg");
			inputImage = ImageIO.read(url);
	        // creates output image
	        BufferedImage outputImage = new BufferedImage(scaledWidth,
	                scaledHeight, inputImage.getType());
	 
	        // scales the input image to the output image
	        Graphics2D g2d = outputImage.createGraphics();
	        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
	        g2d.dispose();

			// convert BufferedImage to byte array
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(outputImage, "jpg", baos);
			baos.flush();
			imageInByte = baos.toByteArray();
			baos.close();
			Thread.sleep(200);
			String dateString = new Date().toString();	
			boolean retained = true;
			JSONObject outputData = new JSONObject();
			outputData.put("date", dateString);
			publishMessage("domeCam/image/date", outputData.toJSONString().getBytes(), 0, retained);
		}
		catch (Exception e)
		{System.out.println("Error: " + e.getMessage());}
		return imageInByte;
	}
	@Override
	public void handleBrokerMqttMessage(String topic, byte[] message) 
	{
	}
	public static void main(String[] args) throws Exception 
	{
		int periodicPoll = Integer.parseInt(args[0]);
		DomeCamIoc ioc = new DomeCamIoc("domeCamIoc", "stoneEdgeMqttBroker.dat");
		ioc.setPeriodicPollPeriodmillis(periodicPoll);
		ioc.startIoc("domeCam/set", "domeCam/image/jpg");
	}
}
