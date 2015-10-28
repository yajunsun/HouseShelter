package com.hiibox.houseshelter.net;

import android.text.TextUtils;

public class SpliteUtil {
	
	public static boolean getRuquestStatus(String data) {
		if (TextUtils.isEmpty(data)) {
			return false;
		}
		if (!data.contains("\t")) {
			return false;
		}
		String[] datas = data.split("\t");
		return datas[0].equals("0");
	}
	
	public static String getResult(String data) {
		if (TextUtils.isEmpty(data)) {
			return "";
		}
		if (!data.contains("\t")) {
			return "";
		}
		String[] datas = data.split("\t");
		if (datas.length < 2) {
			return "";
		}
		return datas[1];
	}
	
	public static String getStatues(String data) {
		if (TextUtils.isEmpty(data)) {
			return "";
		}
		if (!data.contains("\t")) {
			return "";
		}
		
		
		String[] datas = data.split("\t");
		if (datas.length < 3) {
			return "";
		}
		return datas[2];
	}
	
	/*
	 * 获取设备的IP地址
	 */
	public static String getDeviceIP(String data){
		if(TextUtils.isEmpty(data)){
			return "";
		}
		if(!data.contains("\t")){
			return "";
		}
		
		String[] datas = data.split("\t");
		if(datas.length < 5){
			return "";
		}
		
		return datas[4];
	}
}
