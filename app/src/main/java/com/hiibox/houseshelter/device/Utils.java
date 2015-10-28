package com.hiibox.houseshelter.device;

public class Utils {
	
	public static long htonl(long num)
	{

		return ((num >> 56) & 0xff) | ((num<<56) & 0xff) | (num >> 40) & 0xff | ((num<<40) & 0xff) |
				((num >> 24) & 0xff) | ((num<<24) & 0xff) | ((num >> 8) & 0xff) | ((num<<8) & 0xff);
	}
	public static long nthol(long num)
	{
		return htonl(num);
	}
	
	public static int htoni(int num)
	{
		return ((num >> 24) & 0xff) | ((num << 24) & 0xff000000) | ((num >> 8) & 0x0000ff00) | ((num << 8) & 0x00ff0000);  
	}
	public static int ntohi(int num)
	{
		return htoni(num);
	}

	public static short htons(short num)
	{
		return (short) (((num >> 8) & 0xff) | ((num<<8)  & 0xff00));
		
	}
	public static short ntohs(short num)
	{
		return htons(num);
	}

}
