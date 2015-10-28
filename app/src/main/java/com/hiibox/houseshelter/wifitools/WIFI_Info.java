package com.hiibox.houseshelter.wifitools;

public class WIFI_Info {
	String SSID;
	String Single;
	String Secret;

	public String getSSID() {
		return SSID;
	}

	public void setSSID(String sSID) {
		SSID = sSID;
	}

	public String getSingle() {
		return Single;
	}

	public void setSingle(String single) {
		Single = single;
	}

	public String getSecret() {
		return Secret;
	}

	public void setSecret(String secret) {
		Secret = secret;
	}

	@Override
	public String toString() {
		return "WIFI_Info [SSID=" + SSID + ", Single=" + Single + ", Secret="
				+ Secret + "]";
	}
}
