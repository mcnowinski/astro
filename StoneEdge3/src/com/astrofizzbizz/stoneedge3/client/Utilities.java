package com.astrofizzbizz.stoneedge3.client;


public class Utilities
{
	public static String stripWhiteSpaces1(String whitey)
	{
		int numChar = 0;
		for (int ii = 0; ii < whitey.length(); ++ii)
		{
			if (whitey.charAt(ii) != ' ') numChar = numChar + 1;
		}
		if (numChar == 0) return "";
		char[] slimJimArray = new char[numChar];
		int iChar = 0;
		for (int ii = 0; ii < whitey.length(); ++ii)
		{
			if (whitey.charAt(ii) != ' ') 
			{
				slimJimArray[iChar] = whitey.charAt(ii);
				iChar = iChar + 1;
			}
		}
		return new String(slimJimArray);
	}
	public static String stripWhiteSpaces(String whitey)
	{
		return whitey.replaceAll(" ", "");

	}
	public static String stripSpecialCharacters(String special)
	{
		return special.replaceAll("[^a-zA-Z0-9]", "");

	}
	public static String removeDecimalPlacesInString(String pointy)
	{
		if (pointy == null) return null;
		if (pointy.length() < 1) return "";
		return pointy.replace('.', 'p');
	}
	public static boolean isIntNumber(String intString) 
	{
		boolean isInt = true;
		try
		{
			Integer.parseInt(intString);
		}catch (NumberFormatException nfe)
		{
			isInt = false;
		}
		return isInt;
	}
	public static boolean isDoubleNumber(String doubleString) 
	{
		boolean isDouble = true;
		try
		{
			Double.parseDouble(doubleString);
		}catch (NumberFormatException nfe)
		{
			isDouble = false;
		}
		return isDouble;
	}
	public static void main(String[] args) 
	{
	}

}
