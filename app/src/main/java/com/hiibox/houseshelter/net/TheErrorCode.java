package com.hiibox.houseshelter.net;


/**
 * 错误代码
 * 
 * @author fr
 * 
 */
public class TheErrorCode {
	public static String ERR_NOT_MAIN_FUN = "无此主功能号";// 3
	public static String ERR_NOT_SUB_FUN = "无此子功能号";// 4
	public static String ERR_DISABLE_LOGIN = "禁止登陆";// 5
	public static String ERR_RECORD_NOT_EXIST = "数据记录不存在";// 6
	public static String ERR_DATA_FORMAT = "数据格式错误";// 7
	public static String ERR_USER_NOT_EXIST = "用户不存在";// 8
	public static String ERR_DEVICE_NOT_EXIST = "设备不存在";// 9
	public static String ERR_NOT_ME = "平台代码错误";// A
	public static String ERR_INVALIDATE_PLATFORM = "平台代码错误";// B
	public static String ERR_DEVICE_OLD_CRC = "老的ICRC码错误";// C
	public static String ERR_DB_INSERT = "数据库插入数据	错误";// D
	public static String ERR_DB_MAC_NOT_EXIST = "MAC地址不存在";// E
	public static String ERR_FILE_CREATE_FALSE = "文件创建失败";// G
	public static String ERR_FILE_NO_FREEDISK = "磁盘空间不足";// H
	public static String ERR_PARAMS = "参数错误：如错误的序号";// I
	public static String ERR_DISABLE_DEL_DEV = "绑定设备不能删除";// J
	public static String ERR_RFID_EXISTS = " RFID已经存在不允许重新注册";// K
	public static String ERR_RFID_BIND = "RFID不是该设备的";// L
	public static String ERR_DEVICE_OFFLINE = "设备不在线";// M
	public static String ERR_MSG_CODE = "短信校验码错误";// N

	public static String ERR_DOT_KNOW = "未知错误";

	public static String compare(String data) {
		if (data.equals("3")) {
			data = ERR_NOT_MAIN_FUN;
		}
		if (data.equals("4")) {
			data = ERR_NOT_SUB_FUN;
		}
		if (data.equals("5")) {
			data = ERR_DISABLE_LOGIN;
		}
		if (data.equals("6")) {
			data = ERR_RECORD_NOT_EXIST;
		}
		if (data.equals("7")) {
			data = ERR_DATA_FORMAT;
		}
		if (data.equals("8")) {
			data = ERR_USER_NOT_EXIST;
		}
		if (data.equals("9")) {
			data = ERR_DEVICE_NOT_EXIST;
		}
		if (data.equals("A")) {
			data = ERR_NOT_ME;
		}
		if (data.equals("B")) {
			data = ERR_INVALIDATE_PLATFORM;
		}
		if (data.equals("C")) {
			data = ERR_DEVICE_OLD_CRC;
		}
		if (data.equals("D")) {
			data = ERR_DB_INSERT;
		}
		if (data.equals("E")) {
			data = ERR_DB_MAC_NOT_EXIST;
		}
		if (data.equals("G")) {
			data = ERR_FILE_CREATE_FALSE;
		}
		if (data.equals("H")) {
			data = ERR_FILE_NO_FREEDISK;
		}
		if (data.equals("I")) {
			data = ERR_PARAMS;
		}
		if (data.equals("J")) {
			data = ERR_DISABLE_DEL_DEV;
		}
		if (data.equals("K")) {
			data = ERR_RFID_EXISTS;
		}
		if (data.equals("L")) {
			data = ERR_RFID_BIND;
		}
		if (data.equals("M")) {
			data = ERR_DEVICE_OFFLINE;
		}
		if (data.equals("N")) {
			data = ERR_MSG_CODE;
		} else {
			String[] dataString = data.split("\t");
			if (dataString.length > 1) {
				data = dataString[1];
			} else {
				data = ERR_DOT_KNOW;

			}
		}
		return data;
	}

}
