package com.astrofizzbizz.stoneedge3.shared;

import java.io.Serializable;
import java.util.Date;

import com.astrofizzbizz.stoneedge3.client.targeting.StarCoordUtilities;

@SuppressWarnings("serial")
public class AstroTarget implements Serializable
{
//	alias="H9105" dec="+42:05:32" epoch="2000.0" link="" magn="6.01" name="Focus00RA" ra="00:04:36.7" type="star"
	private String alias = null;
	private String dec = null;
	private String epoch = "2000.0";
	private String link = null;
	private Double magn = null;
	private String name = null;
	private String ra = null;
	private String type = null;
	private boolean visible = false;
	private double altitude = -90.0;
	private double azimuth = -90.0;
	private double airmass = -1.0;
	
	
	public String getAlias() {return alias;}
	public String getDec() {return dec;}
	public String getEpoch() {return epoch;}
	public String getLink() {return link;}
	public Double getMagn() {return magn;}
	public String getName() {return name;}
	public String getRa() {return ra;}
	public String getType() {return type;}
	public boolean isVisible() {return visible;}
	public double getAltitude() {return altitude;}
	public double getAzimuth() {return azimuth;}
	public double getAirmass() {return airmass;}

	public void setAlias(String alias) {this.alias = alias;}
	public void setDec(String dec) {this.dec = dec;}
	public void setEpoch(String epoch) {this.epoch = epoch;}
	public void setLink(String link) {this.link = link;}
	public void setMagn(Double magn) {this.magn = new Double(magn);}
	public void setName(String name) {this.name = name;}
	public void setRa(String ra) {this.ra = ra;}
	public void setType(String type) {this.type = type;}
	public void setVisible(boolean visible) {this.visible = visible;}
	
	public AstroTarget()
	{
		
	}
	public AstroTarget(AstroTarget astroTarget)
	{
		this();
		setAlias(astroTarget.getAlias());
		setDec(astroTarget.getDec());
		setEpoch(astroTarget.getEpoch());
		setLink(astroTarget.getLink());
		setMagn(astroTarget.getMagn());
		setName(astroTarget.getName());
		setRa(astroTarget.getRa());
		setType(astroTarget.getType());
	}

	public void setAltitudeAzimuthAirmass(double longitude, double latitude, Date date)
	{
		if ((ra == null) || (dec == null) )
		{
			altitude = -90.0;
			azimuth = -90.0;
			airmass =-1.0;
			return;
		}
		double[] altAz = StarCoordUtilities.getAltAzDeg(StarCoordUtilities.raDeg(ra), StarCoordUtilities.decDeg(dec), 
				longitude, latitude, date);
		altitude = altAz[0];
		azimuth = altAz[1];
		airmass = 90.0 - altitude;
		airmass = 1.0 / Math.cos(airmass * Math.PI / 180.0);
		return;
	}
	public void setVisible(double altitudeLimit) 
	{
		visible = false;
		if (altitude > altitudeLimit) visible = true;
	}
}
