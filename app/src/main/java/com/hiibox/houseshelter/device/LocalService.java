//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : See51
//  @ File Name : LocalService.java
//  @ Date : 2012-5-30
//  @ Author : Eric Guo <gjl@my51c.com>
//
//

package com.hiibox.houseshelter.device;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import android.os.Handler;
import android.util.Log;
/**
 * LocalService 局域网通信类. <br>
 ***** &nbsp;&nbsp;该类实现了与摄像头局域网通信的基本功能，它拥有自己的线程，通过init方法初始化后该线程一直运行，<br>
 ***** 直至release被调用. <br>
 ***** &nbsp;&nbsp;该类拥有一个设备列表的引用，在初始化的时候传入， 在整个运行过程中一直动态维护该列表。
 */

public class LocalService implements Runnable {
	private DeviceList localDevList;
	public DatagramChannel datagramChannel;
	private Selector selector;
	private boolean bInited;
	private boolean bSetted;
	private boolean bStop;
	private Thread runThread;
	private Handler handler;
	public DeviceLocalInfo dev_Info;
	private int flag=0;
	final int sndPort = 8628;
	final int rcvPort = 8629;
	public String sSIDString;
	public String wifikeyString;
	final static public int M51GET = 0x56;
	final static public int RESPONDM51GET = 0x57;
	final static public int M51SET = 0x87;
	final static public int RIGHTM51SET = 0x89;
	final static public int WRONGM51SET = 0x93;
	public static final String CMD_GET_FLAG = "HdvsGet";
	public static final String CMD_SET_INFO_FLAG = "HdvsSetInfo";
	public static final String CMD_SET_SUCCESS_FLAG = "hdvsset success";
	/***
	 * //new by marshal #define CMD_GET_FLAG "HdvsGet" #define CMD_SET_INFO_FLAG
	 * "HdvsSetInfo" #define CMD_SET_SUCCESS_FLAG "hdvsset success"
	 */
	private OnSetDeviceListener mSetDeviceListener;

	public interface OnSetDeviceListener {
		public void onSetDeviceSucess(DeviceLocalInfo devInfo);

		public void onSetDeviceFailed(DeviceLocalInfo devInfo);
	}

	public void setListener(OnSetDeviceListener l) {
		mSetDeviceListener = l;
	}

	private String listeningID;

	public void setListenID(String id) {
		listeningID = id;
	}

	/**
	 * 初始化方法. 初始化局域网通信服务，启动线程 <br>
	 * 
	 * @param localList
	 *            - 设备列表，搜索到的设备将被保存在该列表中，不能为null <br>
	 * @return 无
	 */
	public void init(DeviceList localList) {
		this.localDevList = localList;
		bInited = false;
		runThread = new Thread(this);
		runThread.start();
	}
	public void init(DeviceList localList,String SSID,String WIFIKEY,Handler shandler) {
		this.localDevList = localList;
		bInited = false;
		bSetted=false;
		runThread = new Thread(this);
		sSIDString=SSID;
		wifikeyString=WIFIKEY;
		handler=shandler;
		runThread.start();
	}

	/**
	 * 判断初始化成功方法. 判断初始化是否成功. <br>
	 * 
	 * @param 无
	 * <br>
	 * @return 无
	 */
	public Boolean isInit() {
		return bInited;
	}
	public Boolean isSet(){
		return bSetted;
	}
	public void setBsetted(){
		bSetted=false;				
	}

	/**
	 * release 方法.
	 * <p>
	 * 停止局域网通信服务, 停止运行的线程，关闭网络接口<br>
	 * 
	 * @param 无
	 * @return 无
	 */
	public void release() {
		bStop = true;
		try {
			runThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * search 方法.
	 * <p>
	 * 主动搜索本地局域网的摄像头设备, 将已经搜索到的设备列表清空，重新搜索局域网内的设备
	 * </p>
	 * <br>
	 * 
	 * @param 无
	 * @return 无
	 */
	public void search() {
		DeviceLocalInfo searchPack = new DeviceLocalInfo();
		searchPack.setCmd(M51GET);
		searchPack.setPacketFlag(CMD_GET_FLAG);
		sendPackage(searchPack);
		flag=0;
	}

	/**
	 * 设置设备参数. 设置设备参数, 设置搜索到的设备的参数，包括网络设置、音视频参数设置等
	 * 
	 * @param info
	 *            设备信息，将设备原有信息修改需要修改的部分后传入
	 * 
	 *            <b>*咱未实现*</b>
	 */
	public void setDeviceParam(DeviceLocalInfo info) {
		// DeviceLocalInfo pack = (DeviceLocalInfo) info.clone();
		setListenID(info.getCamSerial());
		info.setCmd(M51SET);
		info.setPacketFlag(CMD_SET_INFO_FLAG);
		info.setWiFiSSID(info.getWiFiSSID());
		info.setWiFiPwd(info.getWiFiPwd());
	//	info.setWiFiPwd("123");

		sendPackage(info);
	}
	/**
	 * 传入用户信息以及设置wifi名称和wifi密码
	 * @param info
	 * @param WiFiSSID
	 * @param WiFiPwd
	 * @param value 1开启，0关闭 //是否开启WiFi，不开启wifi情况下，wifi处于AP模式

	 */
	public void setDeviceParam(DeviceLocalInfo info,String WiFiSSID,String WiFiPwd,int value) {
		// DeviceLocalInfo pack = (DeviceLocalInfo) info.clone();
		setListenID(info.getCamSerial());
		
		info.setCmd(M51SET);
		info.setPacketFlag(CMD_SET_INFO_FLAG);
		info.setWiFiSSID(WiFiSSID);
		info.setWiFiPwd(WiFiPwd);
		info.setEnableWiFi(value);
		Log.e("setDeviceParam", "setDeviceParam"+"wifissid"+WiFiSSID+"wifikey"+WiFiPwd);
		
		sendPackage(info);
		bSetted=true;
	}

	public void rebootDevice(DeviceLocalInfo info) {
		// if (info==null) {
		// info = new DeviceLocalInfo();
		// }
		setListenID(info.getCamSerial());
		info.setCmd(M51SET);
		info.setbDeviceRest((byte) 2); // 1,恢复出厂值（并解绑）; 2 重启设备
		info.setPacketFlag(CMD_SET_INFO_FLAG);
		sendPackage(info);
	}

	public void setDefaultPara(DeviceLocalInfo info) {
		setListenID(info.getCamSerial());
		info.setCmd(M51SET);
		info.setbDeviceRest((byte) 1); // 1,恢复出厂值（并解绑）; 2 重启设备
		info.setPacketFlag(CMD_SET_INFO_FLAG);
		sendPackage(info);
		
		Log.d("LocalService", "setDefaultPara: " + info.getCamSerial());
	}

	private void OnRecievePackate(ByteBuffer buf, SocketAddress rAddr) {
		Log.d("LocalService", "Received " + buf.limit() + " bytes From "
				+ rAddr.toString());
		dev_Info = new DeviceLocalInfo(buf);
		if (dev_Info.getCmd() == RESPONDM51GET) {
			localDevList.onReceivedMessage(dev_Info, rAddr);
			Log.d("LocalService", "find deivce" + dev_Info.getDeviceName()
					+ " sn: " + dev_Info.getCamSerial());
			Log.d("LocalService", "find deivce" + dev_Info.getUserName()
					+ " sn: " + dev_Info.getPwd());
			Log.d("LocalService", "find deivce" + dev_Info.getWiFiSSID()
					+ " sn: " + dev_Info.getWiFiPwd());	
			if (flag==0) {
				//handler.sendEmptyMessage(1);
				Log.e("LocalService", "设置网络");
				setDeviceParam(dev_Info, sSIDString, wifikeyString, 1);
				flag=1;	
				
			}
			
			if (dev_Info.getWiFiSSID().equals(sSIDString) && dev_Info.getWiFiPwd().equals(wifikeyString)) {
				Log.e("LocalService", "设置成功");	 
			   
				bSetted=true;	
			
				handler.sendEmptyMessage(1);
			}
			
			
		} else if (dev_Info.getCmd() == RIGHTM51SET
				&& dev_Info.getCamSerial().equals(listeningID)) {
			Log.d("LocalService",
					"RIGHTM51SET  from " + dev_Info.getCamSerial());
			Log.d("LocalService", "listeningID " + listeningID);
			if (mSetDeviceListener != null) {
				mSetDeviceListener.onSetDeviceSucess(dev_Info);
			}
		} else if (dev_Info.getCmd() == WRONGM51SET
				&& dev_Info.getCamSerial().equals(listeningID)) {
			Log.d("LocalService",
					"WRONGM51SET  from " + dev_Info.getCamSerial());
			Log.d("LocalService", "listeningID " + listeningID);
			if (mSetDeviceListener != null) {
				mSetDeviceListener.onSetDeviceFailed(dev_Info);
			}
		}
	}

	@Override
	public void run() {

		Log.d("LocalThread", "I am running");
		bStop = false;
		ByteBuffer byteBuf = ByteBuffer.allocate(1088);
		try {
			datagramChannel = DatagramChannel.open();
			datagramChannel.socket().setReuseAddress(true);
			datagramChannel.socket().bind(new InetSocketAddress(rcvPort));
			Log.d("LocalService", "socket sndbufsize = "
					+ datagramChannel.socket().getSendBufferSize());
			Log.d("LocalService", "socket rcv = "
					+ datagramChannel.socket().getReceiveBufferSize());
			datagramChannel.configureBlocking(false);
			datagramChannel.socket().setBroadcast(true);

			selector = Selector.open();
			datagramChannel.register(selector, SelectionKey.OP_READ);

			bInited = true;
			localDevList.clear();
			search(); // 启动即搜索

			while (!bStop) {
				if (selector.select(200) > 0) {
					Log.d("LocalThread", "selector.select(100) > 0");
					for (SelectionKey sk : selector.selectedKeys()) {
						Log.d("LocalThread", "sk:selector.selectedKeys()");
						// 删除正在处理的selectionkey
						selector.selectedKeys().remove(sk);
						// 如果该selectionkey对应的channel中有可读的数据
						if (sk.isReadable()) {
							DatagramChannel sc = (DatagramChannel) sk.channel();
							byteBuf.clear();
							SocketAddress sa = sc.receive(byteBuf);
							byteBuf.flip();
							// 处理收到的消息
							Log.d("LocalThread",
									"byteBuf.limit() = " + byteBuf.limit());
							if (byteBuf.limit() != 1088) {
								continue;
							}
							OnRecievePackate(byteBuf, sa);

							// 为下一次读取作准备
							sk.interestOps(SelectionKey.OP_READ);
						}
					}
				}
			}
		} catch (IOException ioe) {
			bInited = false;
			ioe.printStackTrace();
			Log.d("LocalThread", ioe.getMessage());
		}

		Log.d("LocalThread", "I am stop");
	}

	private boolean sendPackage(DeviceLocalInfo devPackge) {
		if (!bInited || datagramChannel == null) {
			return false;
		}
		final ByteBuffer bf = devPackge.toByteBuffer();
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					datagramChannel.send(bf, new InetSocketAddress(
							"255.255.255.255", sndPort));
					Log.e("datagramChannel.send(","datagramChannel.send(");
				} catch (IOException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(3);
				}
			}
		});

		thread.start();
		return true;
	}
	
}
