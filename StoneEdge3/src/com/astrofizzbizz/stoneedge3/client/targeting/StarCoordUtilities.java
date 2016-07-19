package com.astrofizzbizz.stoneedge3.client.targeting;

import java.util.Date;

import com.astrofizzbizz.stoneedge3.client.Se3Exception;
import com.astrofizzbizz.stoneedge3.client.Utilities;
import com.google.gwt.i18n.client.NumberFormat;


public class StarCoordUtilities 
{
	static double degToRad = Math.PI / 180.0;
	// Return local siderial time in deg
	@SuppressWarnings("deprecation")
	public static double lmstDeg(Date localDate, double observerLongitudeDeg)
	{
	    double tu;
	    double gmst;
	    double LST;
	    double ut1;
	    double dmjd;
	    long imjd;
	    int offset;

	    double longitudeRad = observerLongitudeDeg * degToRad;
	    // Get correction in minutes between local and GMT time
	    // (e.g. calculate UTC from Civil time)
	    offset = localDate.getTimezoneOffset();

	    imjd = mjd(localDate);

	    // get ut1 (really UTC) in turns
	    ut1 = (localDate.getSeconds() + (localDate.getMinutes() + offset) * 60 +
	           localDate.getHours()*3600)/86400.0;



	    // If ut rolled over, means UTC on next day
	    if(ut1 > 1.0)
	    {
	        ut1 -= 1.0;
	        imjd += 1;
	    }

	    dmjd = (double)imjd + ut1;

	    // Julian centuries from fundamental epoch J2000 to this UT
	    tu = (dmjd - 51544.5) / 36525.0;

	    // tu is in turns
	    // GMST at this UT in radians
	    gmst = ( ut1 * (2.0*Math.PI) +
	             ( 24110.54841 +
	             ( 8640184.812866 +
	             ( 0.093104 - ( 6.2e-6 * tu ) ) * tu ) * tu ) * Math.PI/43200.0 );

	    // correct for observer longitude
	    LST = gmst + longitudeRad;

	    // assure a positive answer, between 0 and 2PI
	    while(LST < 0.0) {
	        LST += 2.0*Math.PI;
	    }
	    while(LST > 2.0*Math.PI) {
	        LST -= 2.0*Math.PI;
	    }
	    LST = LST * 180.0 / Math.PI; 


	    return(LST);
	}
	@SuppressWarnings("deprecation")
	private static long mjd(Date date)
	{
		long year;
		long month;
		long day;
		long jy, jm, ja, jday;

	    // getMonth range is 0..11
	    month = 1 + date.getMonth();
	    day = date.getDate();
	    year = date.getYear() + 1900;

	    jy = year;

	    // Garbage in, garbage out ...
	    if (jy == 0)
	        return(0);

	    if (jy < 0)
	        ++jy;
	    if (month > 2) {
	        jm=month+1;
	    } else {
	        --jy;
	        jm=month+13;
	    }
	    jday = (long)(Math.floor(365.25*jy)+Math.floor(30.6001*jm)+day+1720995);

	    // switch over to gregorian calendar
	    if (day+31L*(month+12L*year) >= (15+31L*(10+12L*1582))) {
	        ja=(long)(0.01*jy);
	        jday += 2-ja+(long) (0.25*ja);
	    }
	    return(jday-2400001);

	}
	public static double[] getAltAzDeg(double raDeg, double decDeg, double longitudeDeg, double latitudeDeg, Date date)
	{
		double hourAngleDeg = StarCoordUtilities.lmstDeg(date, longitudeDeg) - raDeg;
		double sinAlt = Math.sin(decDeg * degToRad) * Math.sin(latitudeDeg * degToRad)
			+ Math.cos(decDeg * degToRad) * Math.cos(latitudeDeg * degToRad) * Math.cos(hourAngleDeg * degToRad);

		double altDeg = Math.asin(sinAlt) / degToRad;
		double cosAz = Math.sin(decDeg * degToRad) - sinAlt * Math.sin(latitudeDeg * degToRad);
		cosAz = cosAz / (Math.cos(altDeg * degToRad) * Math.cos(latitudeDeg * degToRad));
		
		double azDeg = Math.acos(cosAz)  / degToRad;
		
		if (Math.sin(hourAngleDeg * degToRad) > 0) azDeg = 360 - azDeg;
		
		double[] altAz = {altDeg, azDeg};
		return altAz;
	}
	public static double raDeg(String raString)
	{
		int firstColon = raString.indexOf(":");
		int lastColon = raString.lastIndexOf(":");

		double hours = Double.parseDouble(raString.substring(0, firstColon));
		double mins = Double.parseDouble(raString.substring(firstColon + 1, lastColon));
		double secs = Double.parseDouble(raString.substring(lastColon + 1));
		
		double raDeg = hours * 3600 + mins * 60 + secs;
		raDeg = 360.0 * raDeg / (24.0 * 3600);
		
		return raDeg;
	}
	public static String raString(double raDeg)
	{
		double toHours = raDeg * 24 / 360.0;
		int numHrs =  (int) Math.floor(toHours);
		double toMins = (toHours - ((double) numHrs)) * 60.0;
		int numMins = (int) Math.floor(toMins);
		double toSecs = (toMins - ((double) numMins)) * 60.0;

		String hourString = Integer.toString(numHrs);
		if (numHrs <  10) hourString = "0" + hourString;
		String minString = Integer.toString(numMins);
		if (numMins <  10) minString = "0" + minString;
		String secString = NumberFormat.getFormat("#.00").format(toSecs);
//		if (toSecs <  10.0) secString = "0" + secString;

		String raString = hourString + ":" + minString + ":" + secString;
		return raString;
	}
	public static double decDeg(String decString)
	{
		int firstColon = decString.indexOf(":");
		int lastColon = decString.lastIndexOf(":");
		int minusSign = decString.indexOf("-");
		if (minusSign > -1) 
		{
			minusSign = -1;
		}
		else
		{
			minusSign = 1;
		}
		double deg = Math.abs(Double.parseDouble(decString.substring(0, firstColon)));
		double mins = Double.parseDouble(decString.substring(firstColon + 1, lastColon));
		double secs = Double.parseDouble(decString.substring(lastColon + 1));
		
		double decDeg = minusSign * (deg + (mins / 60) + (secs / 3600));
		
		return decDeg;
	}
	public static String decString(double decDeg)
	{
		double decDegAbs = Math.abs(decDeg);
		String sign = "+";
		if (decDeg < 0.0) sign = "-";
		int numDegs =  (int) Math.floor(decDegAbs);
		double toMins = (decDegAbs - ((double) numDegs)) * 60.0;
		int numMins = (int) Math.floor(toMins);
		double toSecs = (toMins - ((double) numMins)) * 60.0;

		String degString = Integer.toString(numDegs);
		if (numDegs <  10) degString = "0" + degString;
		String minString = Integer.toString(numMins);
		if (numMins <  10) minString = "0" + minString;
		String secString = NumberFormat.getFormat("#.00").format(toSecs);
//		if (toSecs <  10.0) secString = "0" + secString;

		String raString = sign + degString + ":" + minString + ":" + secString;
		return raString;
	}
/*
	public static Date getTime(Date time, String timeZoneId) 
	{
		Calendar calTZ = new GregorianCalendar(TimeZone.getTimeZone(timeZoneId));
		calTZ.setTimeInMillis(time.getTime());
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, calTZ.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, calTZ.get(Calendar.MONTH));
		cal.set(Calendar.DAY_OF_MONTH, calTZ.get(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, calTZ.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, calTZ.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, calTZ.get(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, calTZ.get(Calendar.MILLISECOND));
		return cal.getTime();
	}

	public static String getTimeString(Date time, String timeZoneId)
	{
		String sd = getTime(time, timeZoneId).toString();
		String sd2 = sd.substring(0,sd.lastIndexOf(":") + 3);
		sd2 = sd2 + " " + TimeZone.getTimeZone(timeZoneId).getDisplayName(true, TimeZone.SHORT);
		return sd2;
	}
	public static String getDefaultTimeString(Date time)
	{
		return getTimeString(time,TimeZone.getDefault().getID());
	}
*/
	public static void raStringOk(String ra) throws Se3Exception
	{
		getRaHrs(ra);
		getRaMin(ra);
		getRaSec(ra);
	}
	public static String[] getRaHrsMinsSecs(String ra)
	{
		int[] index = new int[2];
		index[0] = ra.indexOf(":", 0);
		index[1] = ra.indexOf(":", index[0] + 1);
		
		String[] raHrsMinsSecs = new String[3];
		raHrsMinsSecs[0] = ra.substring(0, index[0]);
		raHrsMinsSecs[1] = ra.substring(index[0] + 1, index[1]);
		raHrsMinsSecs[2] = ra.substring(index[1] + 1);
		
		return raHrsMinsSecs;
	}
	public static String getRaHrs(String ra) throws Se3Exception
	{
		String[] raHrsMinsSecs = getRaHrsMinsSecs(ra);
		if (Utilities.isIntNumber(raHrsMinsSecs[0]))
		{
			int raInt = Integer.parseInt(raHrsMinsSecs[0]); 
			if ((0 <= raInt) && (raInt <= 23))
			{
				return raHrsMinsSecs[0];
			}
			else throw new Se3Exception("RA Hours not inside valid range");
		}
		else throw new Se3Exception("RA Hours not a number");
	}
	public static String getRaMin(String ra) throws Se3Exception
	{
		String[] raHrsMinsSecs = getRaHrsMinsSecs(ra);
		if (Utilities.isIntNumber(raHrsMinsSecs[1]))
		{
			int raInt = Integer.parseInt(raHrsMinsSecs[1]); 
			if ((0 <= raInt) && (raInt <= 59))
			{
				return raHrsMinsSecs[1];
			}
			else throw new Se3Exception("RA Minutes not inside valid range");
		}
		else throw new Se3Exception("RA Minutes not a number");
	}
	public static String getRaSec(String ra) throws Se3Exception
	{
		String[] raHrsMinsSecs = getRaHrsMinsSecs(ra);
		if (Utilities.isDoubleNumber(raHrsMinsSecs[2]))
		{
			double radouble = Double.parseDouble(raHrsMinsSecs[2]); 
			if (0 <= radouble)
			{
				return raHrsMinsSecs[2];
			}
			else throw new Se3Exception("RA Seconds not inside valid range");
		}
		else throw new Se3Exception("RA Seconds not a number");
	}
	public static void decStringOk(String dec) throws Se3Exception
	{
		getDecDeg(dec);
		getDecMin(dec);
		getDecSec(dec);
	}
	public static String[] getDecDegMinsSecs(String dec)
	{
		int[] index = new int[2];
		index[0] = dec.indexOf(":", 0);
		index[1] = dec.indexOf(":", index[0] + 1);
		
		String[] decDegMinsSecs = new String[3];
		decDegMinsSecs[0] = dec.substring(0, index[0]);
		decDegMinsSecs[1] = dec.substring(index[0] + 1, index[1]);
		decDegMinsSecs[2] = dec.substring(index[1] + 1);
		
		return decDegMinsSecs;
	}
	public static String getDecDeg(String dec) throws Se3Exception
	{
		String[] decDegMinsSecs = getDecDegMinsSecs(dec);
		if (Utilities.isIntNumber(decDegMinsSecs[0]))
		{
			int decInt = Integer.parseInt(decDegMinsSecs[0]); 
			if ((-89 <= decInt) && (decInt <= 89))
			{
				return decDegMinsSecs[0];
			}
			else throw new Se3Exception("DEC degrees not inside valid range");
		}
		else throw new Se3Exception("DEC degrees not a number");
	}
	public static String getDecMin(String dec) throws Se3Exception
	{
		String[] decDegMinsSecs = getDecDegMinsSecs(dec);
		if (Utilities.isIntNumber(decDegMinsSecs[1]))
		{
			int decInt = Integer.parseInt(decDegMinsSecs[1]); 
			if ((0 <= decInt) && (decInt <= 59))
			{
				return decDegMinsSecs[1];
			}
			else throw new Se3Exception("Dec Minutes not inside valid range");
		}
		else throw new Se3Exception("Dec Minutes not a number");
	}
	public static String getDecSec(String dec) throws Se3Exception
	{
		String[] decDegMinsSecs = getDecDegMinsSecs(dec);
		if (Utilities.isDoubleNumber(decDegMinsSecs[2]))
		{
			double decdouble = Double.parseDouble(decDegMinsSecs[2]); 
			if (0 <= decdouble)
			{
				return decDegMinsSecs[2];
			}
			else throw new Se3Exception("DEC Seconds not inside valid range");
		}
		else throw new Se3Exception("DEC Seconds not a number");
	}

}
