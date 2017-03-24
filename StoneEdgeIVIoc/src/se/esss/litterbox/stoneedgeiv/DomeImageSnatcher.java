package se.esss.litterbox.stoneedgeiv;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;

public class DomeImageSnatcher 
{

	public static void main(String[] args) throws Exception 
	{
		int scaledWidth = 800;
		int scaledHeight = 450;
		BufferedImage inputImage = null;
//		URL url = new URL("http://localhost:8082/jpg/image.jpg");
		URL url = new URL("http://axis:80/jpg/image.jpg");
		inputImage = ImageIO.read(url);
        // creates output image
        BufferedImage outputImage = new BufferedImage(scaledWidth,
                scaledHeight, inputImage.getType());
 
        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();
        ImageIO.write(outputImage, "jpg", new File("domeImageSnatch.jpg"));
	}

}
