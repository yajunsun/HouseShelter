package com.zgan.login;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.text.TextUtils;

import com.hiibox.houseshelter.net.Frame;

public class ZganLoginService {

	private static boolean ServiceRin=false;
    private static Thread _threadListen;
    private static Thread _threadMain;
	private static ZganLoginService_Listen ztl;
	public static String UserName="";
	public static String UPwd="";
	public static String UIMIE="";
	public static int Tag=0;

	//正式115.29.147.12  测试60.172.246.193
	private final static String ZGAN_LOGIN_DOMAINNAME = "cloudlogin.zgantech.com";
	private final static String ZGAN_LOGIN_IP = "115.29.147.12";
	public final static int ZGAN_LOGIN_PORT=21000;
	public static String LoginService_IP="";
	
    public final static int PLATFORM_APP = 0x04;
    public final static int VERSION_1 = 0x01;
    public final static int VERSION_2 = 0x02;
    public final static int MAIN_CMD=0x01;   
    
    public final static String ZGAN_DBNAME="ZGANDB";
    public final static String ZGAN_USERNAME="ZGAN_USERNAME";
    public final static String ZGAN_USERPWD="ZGAN_USERPWD";
    public final static String ZGAN_USERIMEI="ZGAN_USERIMEI";
    public final static String ZGAN_JTWSSERVER="ZGAN_JTWSSERVER";
    public final static String ZGAN_FILESERVER="ZGAN_FILESERVER";
    public final static String ZGAN_PUSHSERVER="ZGAN_PUSHSERVER";
    
    public static Context _zgan_context;
    private static SharedPreferences ZganInfo;
    public static int LoginServerState=0;  //0:登录用户,1:获取IP列表,2:其它方法
    
	public ZganLoginService(){
		
	}
	
	/**
	 * 用户登录
	 * */
	public static void toUserLogin(String strUName,String strPwd,String strImei,Handler _handler){
		ZganLoginService.LoginServerState=0;
		
		Frame f = createFrame();
		f.subCmd = 1;
		f.strData = strUName+"\t"+strPwd+"\t"+strImei+"\t\t0";
		f._handler=_handler;
		f.version=2;
		
		toGetData(f);
		
		UserName=strUName;
		UPwd=strPwd;
		UIMIE=strImei;
		
		ztl.toConnectServer();
	}
	
	/**
	 * 用户自动登录
	 * */
	public static boolean toAutoUserLogin(Handler _handler){
		String strUserName=getUserName();
		String strPwd=toGetDB(ZGAN_USERPWD);
		String strImei=toGetDB(ZGAN_USERIMEI);
		
		if(!TextUtils.isEmpty(strUserName) && !TextUtils.isEmpty(strPwd)){
			toUserLogin(strUserName,strPwd,strImei,_handler);
			return true;
		}
		
		return false;
	}
	
	/**
	 * 用户注销登录
	 * */
	public static void toUserQuit(Handler _handler){
		String strUserName=getUserName();
		
		Frame f = createFrame();
		f.subCmd = 5;
		f.strData = strUserName;
		f._handler=_handler;
		
		ZganLoginService.LoginServerState=2;
		
		ztl.toConnectServer();
		
		toGetData(f);
	}
	
	/**
	 * 得到服务器列表
	 * */
	public static void toGetServerList(String strUserName,Handler _handler){
		Frame f = createFrame();
		f.subCmd = 4;
		f.strData = strUserName;
		f._handler=_handler;
		f.version=2;
		
		ZganLoginService.LoginServerState=2;
		
		toGetData(f);
	}
	
	/**
	 * 得到用户名
	 * */
	public static String getUserName(){
		String strUserName="";
		
		strUserName=UserName;
		
		if(TextUtils.isEmpty(strUserName)){
			strUserName=toGetDB(ZGAN_USERNAME);
		}
		
		return strUserName;
	}
	
	
	/**
	 * 设置用户名
	 * */
	public static void toSetZganDB(String strKey,String strUserName){
		toSaveDB(strKey,strUserName);
	}
	
	public static void toClearZganDB(){
		//toSaveDB(ZGAN_USERNAME,null);
		toSaveDB(ZGAN_USERPWD,null);
		toSaveDB(ZGAN_USERIMEI,null);
		toSaveDB(ZGAN_JTWSSERVER,null);
		toSaveDB(ZGAN_PUSHSERVER,null);
		toSaveDB(ZGAN_FILESERVER,null);
	}
	
	/**
	 * 得到服务器IP
	 * */
	public static String toGetServerIP(String strKey){
		String strIP=toGetDB(strKey);
		
		if(!TextUtils.isEmpty(strIP)){
			strIP=strIP.split(":")[0];
		}
		
		return strIP;
	}
	
	/**
	 * 得到服务器端口
	 * */
	public static int toGetServerPort(String strKey){
		String strIP=toGetDB(strKey);
		int intPort=0;
		
		if(!TextUtils.isEmpty(strIP)){
			strIP=strIP.split(":")[1];
			
			intPort=Integer.parseInt(strIP);
		}
		
		return intPort;
	}
	
	/**
	 * 获取服务器数据(通用)
	 * */
	public static void toGetServerData(int subcmd,String strData,Handler _handler) {
		Frame f = createFrame();
		f.subCmd = subcmd;
		f.strData = strData;
		f._handler=_handler;
		
		ztl.toConnectServer();
		
		ZganLoginService.LoginServerState=2;
		
		toGetData(f);
	}
	
	/**
	 * 获取服务器数据(通用)
	 * */
	public static void toGetServerData(int subcmd,String[] aryParam,Handler _handler) {
		Frame f = createFrame();
		f.subCmd = subcmd;
		f.strData = getParam(aryParam);
		f._handler=_handler;

		ZganLoginService.LoginServerState=2;
		
		ztl.toConnectServer();
		
		toGetData(f);
	}
	
	/**
	 * 获取服务器数据(通用)
	 * */
	public static void toGetServerData(int subcmd,String[] aryParam,Handler _handler,int intVar) {
		Frame f = createFrame();
		f.subCmd = subcmd;
		f.strData = getParam(aryParam);
		f._handler=_handler;
		f.version=intVar;

		ZganLoginService.LoginServerState=2;
		
		ztl.toConnectServer();
		
		toGetData(f);
	}
	
	/**
	 * 获取服务器数据(通用)
	 * */
	public static void toGetServerData(int subcmd,String[] aryParam,Handler _handler,int intVar,int mainCmd) {
		Frame f = createFrame();
		f.mainCmd= (byte)mainCmd;
		f.subCmd = subcmd;
		f.strData = getParam(aryParam);
		f._handler=_handler;
		f.version=intVar;
		
		ZganLoginService.LoginServerState=2;
		
		ztl.toConnectServer();
		
		toGetData(f);
	}
	
	private static String getParam(String[] aryParam){
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
		f.platform = PLATFORM_APP;
		f.mainCmd = MAIN_CMD;
		f.version = VERSION_1;
		return f;
	}
	
	public static void toGetData(Frame f){
		ZganLoginServiceTools.toGetFunction(f);
	}
	
	//启动登录服务线程
	public static void toStartLoginService(Context context){
		if(!ServiceRin){			
	
			LoginService_IP=toGetHostIP();
			
			_zgan_context=context;
			
			ZganInfo=context.getSharedPreferences(ZGAN_DBNAME,Context.MODE_PRIVATE);
			
			//启动监听线程
			ztl=new ZganLoginService_Listen(context);
			_threadListen=new Thread(ztl);
			_threadListen.start();
			
			//启动主线程
			ZganLoginService_Main zm=new ZganLoginService_Main(ZganLoginServiceTools.PushQueue_Receive,
					ZganLoginServiceTools.PushQueue_Function);
			_threadMain=new Thread(zm);					
			_threadMain.start();				
			
			ServiceRin=true;
		}
	}
	
	private static boolean toSaveDB(String strKey,String strValue){
		Editor editor=ZganInfo.edit();
		
		editor.putString(strKey, strValue);
		return editor.commit();
	}
	
	public static String toGetDB(String strKey){
		if (ZganInfo.getString(strKey, null) != null) {
			return ZganInfo.getString(strKey, null);
		} else {
			return null;
		}
	}
	
	//解析登录服务器IP
	private static String toGetHostIP() {
		InetAddress x;
		String strIP = ZGAN_LOGIN_IP;

		try {
			x = java.net.InetAddress.getByName(ZGAN_LOGIN_DOMAINNAME);
			strIP = x.getHostAddress();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block

		}

		return strIP;
	}

	public static boolean isNetworkAvailable(Context context) {     
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
}
