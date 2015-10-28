package com.zgan.ap;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.net.FrameTools;
import com.hiibox.houseshelter.wifitools.WifiAdmin;
import com.zgan.push.ZganSocketClient;

public class ZganAPService implements Runnable{
	
	private String AP_SSID="Hoxlox";
	private String AP_WifiPwd="12345678";
	private String Wifi_SSID="";
	private String Wifi_PWD="";
	private String AP_IP="192.168.100.100";
	private int AP_Prot=5566;
	private Handler handler;
	private ZganSocketClient zsc;
	private Thread AP_Thread;
	private WifiAdmin wifiAdmin;	
	private int ServerState=0;
	private Context _context;
	
	//发送消息队列
	public static Queue<byte[]> PushQueue_Send = new LinkedList<byte[]>();
	
	//接收消息队列
	public static Queue<byte[]> PushQueue_Receive = new LinkedList<byte[]>();
	
	//任务队列
	public static Queue<Frame> PushQueue_Function = new LinkedList<Frame>();
	
	//发送消息
	public void toSendMsg(Frame f){
		byte[] Buff=null;
		Buff=FrameTools.getFrameBuffData(f);
		
		if(Buff!=null){
			PushQueue_Send.offer(Buff);
		}
	}
	
	public static void toGetFunction(Frame f){
		PushQueue_Function.offer(f);
	}
	
	/**
	 * 获取服务器数据(通用)
	 * */
	public void toGetServerData(int subcmd,String[] aryParam,Handler _handler) {
		Frame f = createFrame();
		f.subCmd = subcmd;
		f.strData = getParam(aryParam);
		f._handler=_handler;

		toSendMsg(f);
	}
	
	private String getParam(String[] aryParam){
		String strParam="";
		
		if(aryParam!=null){
			for (String oneRow : aryParam) {
				strParam+="\t"+oneRow;
			}
			
			if(strParam!=null && !strParam.equals("")){
				strParam=strParam.substring(1);
			}
		}
		
		return strParam;
	}
	
	/**
	 * 创建数据包
	 * */
	public static Frame createFrame() {
		Frame f = new Frame();
		f.platform = 0xff;
		f.mainCmd = 1;
		f.version = 1;
		return f;
	}
	
	public ZganAPService(){

	}

	public void toStartAP(WifiAdmin _wifiAdmin,Handler _handler,String _Wifi_SSID,String _Wifi_PWD,Context c){
		handler=_handler;
		Wifi_SSID=_Wifi_SSID;
		Wifi_PWD=_Wifi_PWD;
		wifiAdmin=_wifiAdmin;
		_context=c;
		
		AP_Thread=new Thread(this);		
		AP_Thread.start();
	}
	
	public void toCloseAP(){
		zsc.toCloseClient();
		AP_Thread.interrupt();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		zsc=new ZganSocketClient(AP_IP,AP_Prot,PushQueue_Send,PushQueue_Receive);
		zsc.ZganReceiveTime=500;
		zsc.toStartClient();
		zsc.ThreadName="ZganAPService";
		int intRunCount=0;
		
		while(true){
		
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Log.i("ZganAPService", "ServerState["+ServerState+"]");
			
			if(ServerState==0){
				//检查Hoxlox是否存在
				
				if(intRunCount>50){
					intRunCount=0;
					handler.sendEmptyMessage(4);					
					toCloseAP();					
					return ;
				}
				
				if(toCheckAPWifi(AP_SSID)){
					Log.i("ZganAPService", "找到网络"+AP_SSID);
					
					WifiConfiguration cofg3 = wifiAdmin.CreateWifiInfo(AP_SSID, AP_WifiPwd, 3);	
					
					if (wifiAdmin.toSetNewWifi(cofg3)) {	
						ServerState=1;
						intRunCount=0;
					}
					
				}else{
					intRunCount++;
				}
				
			}else if(ServerState==1){
				//检查是否连接上AP	
				if(intRunCount>300){
					intRunCount=0;
					handler.sendEmptyMessage(3);					
					toCloseAP();					
					return ;
				}
				
				if(isNetworkAvailable(_context)){
					Log.i("ZganAPService", "连接上["+AP_SSID+"]");
					
					ServerState=2;
				}else{
					intRunCount++;
				}
				
			}else if(ServerState==2){
				//连接AP			
				
				if(zsc.toConnectServer()){					
					toGetServerData(8,new String[]{Wifi_SSID,Wifi_PWD},handler);
					ServerState=3;
				}

			}else if(ServerState==3 && PushQueue_Receive!=null && PushQueue_Receive.size()>0){
				//设置AP
				byte[] resultByte=null;
				resultByte=PushQueue_Receive.poll();
				
				Frame f=new Frame(resultByte);
				
				if(f!=null && !TextUtils.isEmpty(f.strData)){
					String[] aryData=f.strData.split("\t");
					if(aryData.length==3 && aryData[2].equals("0")){
						Log.i("ZganAPService", "设置["+AP_SSID+"]成功");
						ServerState=4;
					}else{
						handler.sendEmptyMessage(0);
					}
				}else{
					handler.sendEmptyMessage(0);
				}
				
			}else if(ServerState==4){
				//还原Wifi
				
				Log.i("ZganAPService", "还原wifi");
				ServerState=5;
				handler.sendEmptyMessage(7);
				intRunCount=0;
			}else if(ServerState==5){
				//检查wifi是否可用
				
				if(intRunCount>300){
					intRunCount=0;
					handler.sendEmptyMessage(2);					
					toCloseAP();					
					return ;
				}
				
				if(isNetworkAvailable(_context)){
					Log.i("ZganAPService", "还原wifi成功");
					handler.sendEmptyMessage(2);
					toCloseAP();
					ServerState=999;
				}else{
					intRunCount++;
				}
				
			}
		}
		
	}
	
	//检查Wifi是否存在
	private boolean toCheckAPWifi(String strSSID){
		boolean flag=false;
		List<ScanResult> wifiList;
		
		wifiAdmin.startScan();
		wifiList = wifiAdmin.getWifiList();
		
		if(isSSIDEquals(wifiList,strSSID)){
			flag=true;
		}
		
		return flag; 
		
	}
	
	//检查Wifi是否可以用
	private boolean isNetworkAvailable(Context context) {     
	        ConnectivityManager cm = (ConnectivityManager) context     
	                .getSystemService(Context.CONNECTIVITY_SERVICE);     
	        if (cm == null)   
	        {     
	  
	        }   
	        else   
	        {  
	            //如果仅仅是用来判断网络连接则可以使用 cm.getActiveNetworkInfo().isAvailable();  
	            NetworkInfo[] info = cm.getAllNetworkInfo();     
	            if (info != null)  
	            {     
	                for (int i = 0; i < info.length; i++)  
	                {     
	                    if (info[i].getState() == NetworkInfo.State.CONNECTED)   
	                    {     
	                        return true;     
	                    }     
	                }     
	            }     
	        }     
	        
	        return false;     
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
}
