package com.zgan.jtws;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.net.FrameTools;
import com.zgan.push.ZganSocketClient;

public class ZganJTWSService_Listen implements Runnable{
	private int ServerPort=0;
	private String ServerIP="";
	private String UName=""; 
    private Context _context;
    private int ServerState=0;
    private ZganSocketClient zsc;
	
	public ZganJTWSService_Listen(Context context,String _ip,int _prot,String strUName){
		_context=context;
		ServerPort=_prot;
		ServerIP=_ip;
		UName=strUName;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub

		zsc=new ZganSocketClient(ServerIP,ServerPort,
				ZganJTWSServiceTools.PushQueue_Send,ZganJTWSServiceTools.PushQueue_Receive);
		zsc.toStartClient();
		zsc.toStartPing(4,FrameTools.Frame_MainCmd_Ping);
		zsc.ThreadName="ZganJTWSService";
		
		while(true){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			boolean isNet=isNetworkAvailable(_context);	
			
			if(ServerState==1){
				
				//判断是否登录成功
				if(!ZganJTWSServiceTools.isLoginOK && ZganJTWSServiceTools.PushQueue_Receive.size()>0){
					byte[] resultByte=null;
					resultByte=ZganJTWSServiceTools.PushQueue_Receive.poll();
					
					Frame f=new Frame(resultByte);
					
					if(f.mainCmd==0x0e && f.subCmd==21 && f.strData!=null && f.strData.equals("0")){
						
						ZganJTWSServiceTools.isLoginOK=true;
						
						Log.i("ZganJTWSService_Listen","登录家庭卫士服务器成功");
					}
				}
				
				if(!isNet){					
					ServerState=2;
				}
				
				if(zsc.client.isClosed()){
					ServerState=2;
				}
				
				if(!zsc.isRun){
					ServerState=2;
				}
				
			}else if(ServerState==0){
				
				if(isNet){
					Log.i("ZganJTWSService_Listen", "重新连接");

					ServerState=3;
					
					if(zsc.toConnectServer()){						
						ServerState=1;
						
						ZganJTWSServiceTools.isConnect=true;
						
						LoginMsgServer(UName);
					}else{
						ServerState=0;
					}

				}
				
			}else if(ServerState==2){
				//网络断开

				ZganJTWSServiceTools.isConnect=false;
				zsc.toConnectDisconnect();
				ServerState=0;
			}
		}	
	}
	
	 public boolean isNetworkAvailable(Context context) {     
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

	
	//登录消息服务器
    public void LoginMsgServer(String user) {
        Frame f = new Frame();
        f.platform = 4;
        f.mainCmd = 0x0e;
        f.subCmd = 21;
        f.strData = user;
        
        ZganJTWSServiceTools.isLoginOK=false;
        
        UName=user;

        ZganJTWSServiceTools.toSendMsg(f);
    }


}
