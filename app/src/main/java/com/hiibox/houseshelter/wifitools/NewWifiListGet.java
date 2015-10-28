package com.hiibox.houseshelter.wifitools;

import java.util.List;
import java.util.TimerTask;

import com.zgan.ap.ZganAPService;
import com.zgan.youbao.R;
import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.device.DeviceList;
import com.hiibox.houseshelter.device.DeviceLocalInfo;
import com.hiibox.houseshelter.device.LocalService;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NewWifiListGet extends ShaerlocActivity {
	ListView listView;
	private Context context;

	private WifiAdmin wifiAdmin;
	private List<ScanResult> wifiList;

	EditText wifiPwdEditText;
	TextView txt_SSID;

	ImageView setSetingImageView;

	public String SSID;
	public String WKTYPE;

	public String WIFIKEY;
	ProgressDialog dialog;
	WebView view;

	List<String> list_details;

	LocalService localService;
	DeviceList deviceList;
	DeviceLocalInfo deviceLocalInfo;
	WifiInfo wi;
	private ZganAPService zas;
	
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// 判断0或10
			
			if (msg.what == 0  ) {
				dialog.dismiss();
				setSetingImageView.setImageDrawable(getResources().getDrawable(
						R.drawable.commit_wifi));
				Toast.makeText(mContext,R.string.aptxt_wifiNo, Toast.LENGTH_LONG).show();	
				
    			wifiAdmin.mWifiManager.disconnect();
    			wifiAdmin.mWifiManager.enableNetwork(wi.getNetworkId(), true); 
    			zas.toCloseAP();
			}
			
			if( msg.what == 3 || msg.what == 4){
				dialog.dismiss();
				setSetingImageView.setImageDrawable(getResources().getDrawable(
						R.drawable.commit_wifi));
				Toast.makeText(mContext,R.string.aptxt_wifiNo, Toast.LENGTH_LONG).show();	
				
    			wifiAdmin.mWifiManager.disconnect();
    			wifiAdmin.mWifiManager.enableNetwork(wi.getNetworkId(), true);  
			}
			
//			if (msg.what == 1) {
//				Log.i("NewWifiListGet", "设置AP成功");
//				ResumeWifi();			
//			}
			
			if (msg.what == 2) {
				dialog.dismiss();
				setSetingImageView.setImageDrawable(getResources().getDrawable(R.drawable.commit_wifi));	
				Toast.makeText(getApplicationContext(), R.string.aptxt_apok,
						Toast.LENGTH_LONG).show();			
			}

			if(msg.what==5){
				//兼容老版本
				Log.i("NewWifiListGet", "启动AP旧协议");
				init();
			}
			
			if(msg.what==7){
    			wifiAdmin.mWifiManager.disconnect();
    			wifiAdmin.mWifiManager.enableNetwork(wi.getNetworkId(), true);  
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.line_wifilist);

		context = NewWifiListGet.this;
		wifiAdmin = new WifiAdmin(NewWifiListGet.this);

		dialog = new ProgressDialog(NewWifiListGet.this);
		dialog.setCancelable(false);
		
		wifiPwdEditText = (EditText) findViewById(R.id.wifiPsdEditText);

		setSetingImageView = (ImageView) findViewById(R.id.connectButton);

		txt_SSID=(TextView) findViewById(R.id.txt_SSID);
		
		setSetingImageView.setOnClickListener(l);
		
		
		GetWifiList1();

	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		GetWifiList1();

		super.onResume();
	}
	
	private void init() {
		// TODO Auto-generated method stub
		
		localService = new LocalService();
		deviceList = new DeviceList();
		localService.init(deviceList, SSID, WIFIKEY, handler);
	}
	
	
	//恢复原网络
	private void ResumeWifi(){		
		
		new Thread()   
        {  
			boolean isRun=true;
			boolean isSetWifi=false;
			int intWifiCount=0;
			
            public void run()  
            {  
            	while(isRun){
            		if(localService.isSet() && isSetWifi==false){
            			localService.setBsetted();      
            			wifiAdmin.mWifiManager.disconnect();
            			isSetWifi=wifiAdmin.mWifiManager.enableNetwork(wi.getNetworkId(), true);          			
            		}else{            		
            			
            			try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
            			
            			intWifiCount++;
            			
            			WifiInfo wi1=wifiAdmin.mWifiManager.getConnectionInfo();
            			
            			if(wi1!=null && wi1.getBSSID()!=null && wi1.getBSSID().equals(wi.getBSSID())  && wi1.getIpAddress()>0 ){
            				isRun=false;
            				
            				handler.sendEmptyMessage(2);
            				
            				try {
								this.join();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
            			}
            			
            			if(intWifiCount>10){
            				
            				handler.sendEmptyMessage(2);
            				
              				try {
    								this.join();
    							} catch (InterruptedException e) {
    								// TODO Auto-generated catch block
    								e.printStackTrace();
    						}
            			}
            		}
            	}
            }  
            
        }.start();   
		
	}
	
	
	public void SetNewWifi() {

		while (localService.isSet()) {

			setSetingImageView.setImageDrawable(getResources().getDrawable(
					R.drawable.commit_wifi));
			connectTargetWifi(SSID, WIFIKEY);
			localService.setBsetted();

		}
	}

	private void GetWifiList1() {
		// TODO Auto-generated method stub
		if(CheckWifi()){
			getCurrentConnectedWifiInfo();
		}else {
			Toast.makeText(mContext, "当前未连接网络或不支持该网络，请更换为可用的网络连接！", Toast.LENGTH_SHORT).show();
		}	
	}
	/**
	 * 检测用户手机网络类型是否为wifi（1）
	 * @return
	 */
	
	private boolean CheckWifi() {
		// TODO Auto-generated method stub
		ConnectivityManager connectMgr = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = connectMgr.getActiveNetworkInfo();

		if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		} else {
			return false;
		}

	}
	
	private void getCurrentConnectedWifiInfo() {
		// TODO Auto-generated method stub
		wifiAdmin.openWifi();// 打开wifi
		if (!wifiAdmin.getmWifiManager().isWifiEnabled()) {
			if (wifiAdmin.getmWifiManager().setWifiEnabled(true)) {


			} else {
				Toast.makeText(NewWifiListGet.this, R.string.aptxt_wifierr,
						Toast.LENGTH_LONG).show();
				return;
			}
		}

		if (wifiList != null) {
			wifiList.clear();
		}
		
		wifiAdmin.startScan();
		wifiList = wifiAdmin.getWifiList();
		
		String strSSID=wifiAdmin.getNowConnetWifi().getSSID();
		
		if(strSSID!=null && !strSSID.equals("")){
			strSSID=strSSID.replace("\"", "");
		}else{
			strSSID="";
		}
		
		SSID=strSSID;
		
		txt_SSID.setText(strSSID);
		
	}

	/**
	 * 判断该wifi名称是否存在
	 * 
	 * @param list
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static int isIntEquals(List<ScanResult> list, String str)
			throws Exception {
		for (int i = 0; i < list.size(); i++) {
			if (str.equals(list.get(i).SSID.toString())) {
				return i;
			}
		}
		return -1;
	}

	OnClickListener l = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			dialog.setMessage(getResources().getString(
				R.string.aptxt_wifiljw));
			dialog.setCancelable(false);
			dialog.show();
			
			WIFIKEY = wifiPwdEditText.getText().toString();
			
			wi=wifiAdmin.mWifiManager.getConnectionInfo();			
			
			zas=new ZganAPService();
			zas.toStartAP(wifiAdmin,handler, SSID, WIFIKEY,mContext);
			
			//WiflAPThread wapt=new WiflAPThread(wifiAdmin,handler,SSID,WIFIKEY);

		}
		
	};
	TimerTask task = new TimerTask() {

		@Override
		public void run() {
			
			handler.sendEmptyMessage(3);
		}
	};

	private void connectTargetWifi(String na, String pa) {

		wifiAdmin.openWifi();
		wifiAdmin.startScan();

		WifiConfiguration cofg3 = wifiAdmin.CreateWifiInfo(na, pa, 3);
		WifiConfiguration cofg2 = wifiAdmin.CreateWifiInfo(na, pa, 2);
		WifiConfiguration cofg1 = wifiAdmin.CreateWifiInfo(na, "", 1);
		if (wifiAdmin.AddNetwork(cofg3) || wifiAdmin.AddNetwork(cofg2)
				|| wifiAdmin.AddNetwork(cofg1)) {

			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					
					dialog.dismiss();
					NewWifiListGet.this.finish();


				}
			}, 3000);
		} else {


			Toast.makeText(getApplicationContext(), R.string.aptxt_aperr,
					Toast.LENGTH_SHORT).show();

			dialog.dismiss();
			finish();

		}

	}

	OnItemSelectedListener listener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int position, long arg3) {
			// TODO Auto-generated method stub

			SSID = wifiList.get(position).SSID;
	

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	};

	public class WifiListAdapter extends BaseAdapter {

		private LayoutInflater inflater;// 得到一个LayoutInfalter对象用来导入布局
		private List<ScanResult> dataList;

		/**
		 * 构造函数
		 * 
		 * @param context
		 * @param i
		 */
		public WifiListAdapter(List<ScanResult> list) {
			inflater = LayoutInflater.from(context);
			dataList = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {

				convertView = inflater.inflate(
						R.layout.community_political_item, null);
				holder = new ViewHolder();

				/** 得到各个控件的对象 */
				holder.title = (TextView) convertView
						.findViewById(R.id.itemTitle);

				convertView.setTag(holder);// 绑定ViewHolder对象
			} else {
				holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
			}

			/** 设置TextView显示的内容 和Title点击事件 */
			final ScanResult news = dataList.get(position);
			holder.title.setText(news.SSID);
			/*
			 * holder.title.setOnClickListener(new OnClickListener() {
			 * 
			 * @Override public void onClick(View v) {
			 * //name.setText(news.SSID); } });
			 */

			return convertView;
		}

		/**
		 * 存放控件
		 * */
		public final class ViewHolder {
			public TextView title;
		}
	}
}
