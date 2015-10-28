package com.zgan.login;

import android.content.Context;
import android.util.Log;
import com.zgan.push.ZganSocketClient;

public class ZganLoginService_Listen implements Runnable{

    private ZganSocketClient zsc;
    public static int LoginServerState=0;
	
	public ZganLoginService_Listen(Context context){
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub

		zsc=new ZganSocketClient(ZganLoginService.LoginService_IP,ZganLoginService.ZGAN_LOGIN_PORT,
				ZganLoginServiceTools.PushQueue_Send,ZganLoginServiceTools.PushQueue_Receive);
		zsc.ZganReceiveTime=500;
		zsc.toStartClient();
		zsc.ThreadName="ZganLoginService";
		
		while(true){			
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(LoginServerState==2){
				Log.i("ZganLoginService_Listen", "连接断开");
				
				zsc.toConnectDisconnect();
				LoginServerState=0;
			}
			
//			try {
//				Thread.sleep(100);	
//				
//				if(ZganLoginServiceTools.isLoginOK && !ZganLoginServiceTools.isConnect){
//					ZganLoginServiceTools.isLoginOK=false;
//					
//					
//					
//					
//				}
//				
//			
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} 
		}
	}
	
	public void toConnectServer(){
		if(zsc.toConnectServer()){
			LoginServerState=1;
		}
	}
	
}
