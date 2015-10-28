package com.hiibox.houseshelter.wifitools;

import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

public class WifiAdmin {
	// 锟斤拷锟斤拷WifiManager锟斤拷锟斤拷
	public WifiManager mWifiManager;
	// 锟斤拷锟斤拷WifiInfo锟斤拷锟斤拷
	private WifiInfo mWifiInfo;
	// 扫锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷斜锟?ScanResult锟斤拷要锟斤拷锟斤拷锟斤拷锟斤拷锟窖撅拷锟斤拷锟斤拷锟侥斤拷锟斤拷悖拷锟斤拷锟斤拷锟斤拷锟斤拷牡锟街凤拷锟斤拷锟斤拷锟斤拷锟斤拷锟狡ｏ拷锟斤拷锟斤拷锟街わ拷锟狡碉拷剩锟斤拷藕锟角匡拷鹊锟斤拷锟较?
	private List<ScanResult> mWifiList;
	// 锟斤拷锟斤拷锟斤拷锟斤拷锟叫憋拷
	private List<WifiConfiguration> mWifiConfiguration;
	// 锟斤拷锟斤拷一锟斤拷WifiLock
	WifiLock mWifiLock;
	
	private Context context;
	private WifiInfo nowWifiInfo;
	// 锟斤拷锟斤拷锟斤拷
	public WifiAdmin(Context context) {
		// 取锟斤拷WifiManager锟斤拷锟斤拷
		this.context=context;
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);				
	}
	
	// 锟斤拷WIFI
	public void openWifi() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
			System.out.println("wifi锟津开成癸拷!");
		}
		// if (mWifiManager.disconnect()) {
		// mWifiManager.setWifiEnabled(true);
		// System.out.println("wifi锟津开成癸拷!!");
		// }
	}

	// 锟截憋拷WIFI
	public void closeWifi() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	// 锟斤拷榈鼻癢IFI状态
	public int checkState() {
		return mWifiManager.getWifiState();
	}

	// 锟斤拷WifiLock
	public void acquireWifiLock() {
		mWifiLock.acquire();
	}

	// 锟斤拷锟斤拷WifiLock
	public void releaseWifiLock() {
		// 锟叫讹拷时锟斤拷锟斤拷
		if (mWifiLock.isHeld()) {
			mWifiLock.acquire();
		}
	}

	// 锟斤拷锟斤拷一锟斤拷WifiLock
	public void creatWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("Test");
	}

	// 锟矫碉拷锟斤拷锟矫好碉拷锟斤拷锟斤拷
	public List<WifiConfiguration> getConfiguration() {
		return mWifiConfiguration;
	}
	public  WifiInfo  getNowConnetWifi(){
		nowWifiInfo=mWifiManager.getConnectionInfo();
		return nowWifiInfo;
	}

	// 指锟斤拷锟斤拷锟矫好碉拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟?
	public void connectConfiguration(int index) {
		// 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷煤玫锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
		if (index > mWifiConfiguration.size()) {
			System.out.println("锟斤拷锟斤拷失锟斤拷!");
			return;
		}
		// 锟斤拷锟斤拷锟斤拷锟矫好碉拷指锟斤拷ID锟斤拷锟斤拷锟斤拷
		mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
				true);
		System.out.println(index + "锟斤拷锟接成癸拷!");
	}

	public void startScan() {
		mWifiManager.startScan();
		// 锟矫碉拷扫锟斤拷锟斤拷
		if (mWifiList!=null) {
			mWifiList.clear();
		}
		mWifiList = mWifiManager.getScanResults();
		// 锟矫碉拷锟斤拷锟矫好碉拷锟斤拷锟斤拷锟斤拷锟斤拷
		mWifiConfiguration = mWifiManager.getConfiguredNetworks();
	}

	// 锟矫碉拷锟斤拷锟斤拷锟叫憋拷
	public List<ScanResult> getWifiList() {
	    return mWifiList;
	}
	
	//锟介看扫锟斤拷锟斤拷
    public StringBuilder LookUpScan()
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++)
        {
            stringBuilder.append("Index_"+new Integer(i + 1).toString() + ":");
            //锟斤拷ScanResult锟斤拷息转锟斤拷锟斤拷一锟斤拷锟街凤拷锟?
            //锟斤拷锟叫把帮拷锟斤拷锟斤拷BSSID锟斤拷SSID锟斤拷capabilities锟斤拷frequency锟斤拷level
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("\n");
        }
        return stringBuilder;
    }
    
    //锟矫碉拷MAC锟斤拷址
    public String GetMacAddress()
    {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }
    
    //锟矫碉拷锟斤拷锟斤拷锟斤拷BSSID
    public String GetBSSID()
    {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }
    
    //锟矫碉拷IP锟斤拷址
    public int GetIPAddress()
    {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }
    
    //锟矫碉拷锟斤拷锟接碉拷ID
    public int GetNetworkId()
    {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }
    
    //锟矫碉拷WifiInfo锟斤拷锟斤拷锟斤拷锟斤拷息锟斤拷
    public WifiInfo GetWifiInfo()
    {
    	// 取锟斤拷WifiInfo锟斤拷锟斤拷
    	return mWifiManager.getConnectionInfo();
        //return (WifiInfo) ((mWifiInfo == null) ? "NULL" : mWifiInfo);
    }
    
    //锟斤拷锟揭伙拷锟斤拷锟斤拷绮拷锟斤拷锟?
    public boolean AddNetwork(WifiConfiguration wcg)
    {
        int wcgID = mWifiManager.addNetwork(wcg);
        return mWifiManager.enableNetwork(wcgID, true);
    }
    
    public boolean toSetNewWifi(WifiConfiguration wcg){
    	
    	boolean flag=false;
    	
    	//断开当前Wifi
    	if(mWifiManager.disconnect()){
    		int wcgID = mWifiManager.addNetwork(wcg);
    		flag=mWifiManager.enableNetwork(wcgID, true);
    	} 
    	
    	return flag;
    }
    
    //锟较匡拷指锟斤拷ID锟斤拷锟斤拷锟斤拷
    public void DisconnectWifi(int netId)
    {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }
    
    

    // 1没锟斤拷锟斤拷锟斤拷2锟斤拷wep锟斤拷锟斤拷3锟斤拷wpa锟斤拷锟斤拷
    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type)  
    {  
          WifiConfiguration config = new WifiConfiguration();    
          config.allowedAuthAlgorithms.clear();  
          config.allowedGroupCiphers.clear();  
          config.allowedKeyManagement.clear();  
          config.allowedPairwiseCiphers.clear();  
          config.allowedProtocols.clear();  
          config.SSID = "\"" + SSID + "\"";    
           
          WifiConfiguration tempConfig = this.IsExsits(SSID);            
          if(tempConfig != null) {   
              mWifiManager.removeNetwork(tempConfig.networkId);   
          } 
          
          if(Type == 1) //WIFICIPHER_NOPASS 
          {  
        	  	config.hiddenSSID=true;
               //config.wepKeys[0] = "";  
               config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);  
               //config.wepTxKeyIndex = 0;  
          }  
          if(Type == 2) //WIFICIPHER_WEP 
          {  
              config.hiddenSSID = true; 
              config.wepKeys[0]= "\""+Password+"\"";  
              config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);  
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);  
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);  
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);  
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);  
              config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);  
              config.wepTxKeyIndex = 0;  
          }  
          if(Type == 3) //WIFICIPHER_WPA 
          {  
          config.preSharedKey = "\""+Password+"\"";  
          config.hiddenSSID = true;    
          config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);    
          config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);                          
          config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);                          
          config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);                     
          //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);   
          config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP); 
          config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP); 
          config.status = WifiConfiguration.Status.ENABLED;    
          } 
           return config;  
    }  
     
    public WifiConfiguration IsExsits(String SSID)   
    {   
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();   
           for (WifiConfiguration existingConfig : existingConfigs)    
           {   
             if (existingConfig.SSID!=null &&  existingConfig.SSID.equals("\""+SSID+"\""))   
             {   
                 return existingConfig;   
             }   
           }   
        return null;    
    }  
    
    public boolean isWifiConnect() {   
    	ConnectivityManager mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo mWifi = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
    	return mWifi.isConnected();  
    }
    
    public boolean IsWifiEnabled()
    {
    	return mWifiManager.isWifiEnabled();
    }

	public WifiManager getmWifiManager() {
		return mWifiManager;
	}

	public void setmWifiManager(WifiManager mWifiManager) {
		this.mWifiManager = mWifiManager;
	}
    
}
