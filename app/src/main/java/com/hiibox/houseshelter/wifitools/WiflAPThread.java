package com.hiibox.houseshelter.wifitools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;

import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.net.FrameTools;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Handler;
import android.util.Log;

/**
  设备AP模式设备专用类
 */

public class WiflAPThread implements Runnable {

	private String AP_SSID="Hoxlox";
	private String AP_WifiPwd="12345678";
	private String Wifi_SSID="";
	private String Wifi_PWD="";
	private String AP_IP="192.168.100.100";
	private int AP_Prot=5566;
	private Handler handler;
	private Socket AP_Socket;
	private Thread AP_Thread;
	private WifiAdmin wifiAdmin;
	
	public WiflAPThread(WifiAdmin _wifiList,Handler _handler,String _Wifi_SSID,String _Wifi_PWD){
		handler=_handler;
		Wifi_SSID=_Wifi_SSID;
		Wifi_PWD=_Wifi_PWD;
		wifiAdmin=_wifiList;
		
		wifiAdmin.openWifi();
		wifiAdmin.startScan();	
		
		AP_Thread=new Thread(this);
		
		AP_Thread.start();
	}
	
	public void toRelease(){
		try {			
			if(AP_Socket!=null){
		
				AP_Socket.close();				
			}
			AP_Thread.join();
		}catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
	
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub	
		
		Log.i("WiflAPThread", "AP模式启动");
		
		if(!toCheckAPWifi(AP_SSID)){
			handler.sendEmptyMessage(4);
			toRelease();
			return;
		}
		
		if(!toWifiSetAP(AP_SSID,AP_WifiPwd)){
			handler.sendEmptyMessage(3);
			toRelease();
			return;
		}
		
		toSocket();
	}	

	private void toSocket(){
		try {
			AP_Socket=new Socket();
		
			SocketAddress socketAddress = new InetSocketAddress(AP_IP,AP_Prot);		
		
			AP_Socket.setSoTimeout(200);
			AP_Socket.connect(socketAddress);
			
			if(AP_Socket.isConnected()){
				Log.i("WiflAPThread", "Socket连接成功");				
				
				if(checkIsSetWifi()){
					
				}else{
					handler.sendEmptyMessage(5);
					toRelease();
				}				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(5);
			toRelease();
		}	
	}
	
	//检查版本是否可以使用
	private boolean checkIsSetWifi(){
		boolean flag=false;
		boolean isRun=true;
		byte[] _buff=null;		
		int intsubCmd=0x08;
		
//		Frame f = new Frame();
//		f.platform = 0xFF;
//		f.mainCmd = 0x01;
//		f.subCmd = intsubCmd;
//		
//		_buff=FrameTools.getFrameBuffData(f);
//		
//		OutputStream ops;
//		try {
//			ops = AP_Socket.getOutputStream();			
//			ops.write(_buff);
//			ops.flush();	
//			
//			InputStream sin=null;
//			byte[] resultByte=null;
//			
//			while(isRun){						
//				resultByte = new byte[1024];
//				sin=AP_Socket.getInputStream();
//				
//				sin.read(resultByte);
//				
//				//检查包头
//	            if(resultByte!=null && resultByte.length>0 &&
//	            		resultByte[0]==36 && resultByte[1]==90 
//	            		&&resultByte[2]==71 && resultByte[3]==38){
//	            	Frame resultFrame=new Frame(resultByte);
//	            	
//	            	if(resultFrame.version==0xFF && resultFrame.mainCmd==0x01 && resultFrame.subCmd==intsubCmd){
//	            		
//	            		isRun=false;
//	            		
//	            		sin.close();
//	            		ops.close();
//	            	}
//	            }
//			}
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		return flag;
	}
	
	
	
	
//	
	
	//检查Wifi是否存在
	private boolean toCheckAPWifi(String strSSID){
		boolean flag=false;
		boolean isRun=true;
		List<ScanResult> wifiList;
		int intWifiSearch=0;
		
		while(isRun){
			wifiAdmin.startScan();
			wifiList = wifiAdmin.getWifiList();
			
			if(isSSIDEquals(wifiList,strSSID)){
				
				Log.i("WiflAPThread", "找到网络"+AP_SSID);
				isRun=false;
				flag=true;
			}else{
				try{					
					if(intWifiSearch>5){
						
						isRun=false;
					}else{
						Thread.sleep(1000);
						
						intWifiSearch++;
					}
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		return flag; 
		
	}
	
	private boolean isSSIDEquals(List<ScanResult> wifiList,String str){
		boolean flag=false;
		
		if(wifiList!=null && wifiList.size()>0){
			for (int i = 0; i < wifiList.size(); i++) {
				if (str.equals(wifiList.get(i).SSID.toString())) {
					flag=true;
					break;
				}
			}
		}
		
		return flag;
	}
	
	//检查SSID是否已连接上
	private boolean toWifiSetAP(String strSSID,String strPwd){
		boolean flag=false;
		boolean isRun=true;
		int intWifiSearch=0;
		
		WifiConfiguration cofg3 = wifiAdmin.CreateWifiInfo(strSSID, strPwd, 3);	

		if (wifiAdmin.toSetNewWifi(cofg3)) {	
		
			while(isRun){				
				if(checkAPWifiEquals(strSSID)){
					isRun=false;
					flag=true;
					Log.i("WiflAPThread", "Wifi连接上"+AP_SSID);
					
				}else{
					try{					
						if(intWifiSearch>30){
							isRun=false;
						}else{
							Thread.sleep(1000);
							
							intWifiSearch++;
						}
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}				
				
			}		
		}
		
		return flag;
	}
	
	
	private boolean checkAPWifiEquals(String strSSID){
			
		WifiInfo currentWifiInfo=wifiAdmin.GetWifiInfo();
		if (currentWifiInfo==null) {
			return false;
		}
		if (currentWifiInfo.getSSID()==null) {
			return false;
		}
		
		String strMySSID=currentWifiInfo.getSSID().replace("\"", "");
		
		if (strMySSID!=null && strMySSID.equals(strSSID) && currentWifiInfo.getIpAddress()>0 ){			
			
			
			return true;
		}
			
		
		return false;
	}

}
