package com.hiibox.houseshelter.wifitools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.zgan.youbao.R;
import com.hiibox.houseshelter.ShaerlocActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;

public class WifiListGet extends ShaerlocActivity {
	ListView listView;
	private WifiListAdapter adapter;
	private Context context;
	// private EditText name;
	private EditText password;
	private ImageView connect;
	private WifiAdmin wifiAdmin;
	private Spinner wifispinner;
	private List<ScanResult> wifiList;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// 判断0或10
			if (msg.what == 10) {
				/*
				 * adapter = new WifiListAdapter(list);
				 * listView.setAdapter(adapter); dialog.dismiss();
				 */
			}

		}

	};
	String SSID;
	String WKTYPE;
	// String ENABLE;
	String WIFIKEY;
	ProgressDialog dialog;

	WebView view;
	// List<WIFI_Info> list1 = new ArrayList<WIFI_Info>();
	List<String> list_details;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.line_wifilist);
		context = WifiListGet.this;

		dialog = new ProgressDialog(WifiListGet.this);
		wifiAdmin = new WifiAdmin(WifiListGet.this);

		dialog.setMessage(getResources().getString(R.string.aptxt_wifisearch));
		dialog.show();

		// name = (EditText) findViewById(R.id.wifiNameEditText);
		password = (EditText) findViewById(R.id.wifiPsdEditText);

		connect = (ImageView) findViewById(R.id.connectButton);
		wifispinner.setOnItemSelectedListener(l);
		
		connect.setOnClickListener(clickListener);
		GetWifiList1();

	}

	OnItemSelectedListener l = new OnItemSelectedListener() {

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

	private void GetWifiList1() {
		// TODO Auto-generated method stub
		getCurrentConnectedWifiInfo();
	}

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.connectButton:
				if (password.getText().toString() != null
						&& !"".equals(password.getText().toString())) {
					dialog.setMessage(getResources().getString(
							R.string.aptxt_wifiljw));
					dialog.show();
					connectTargetWifi("IPCAM-AP", null);
				} else {
					Toast.makeText(WifiListGet.this, "请输入密码",
							Toast.LENGTH_SHORT).show();
				}

				break;

			}
		}
	};

	private void getCurrentConnectedWifiInfo() {
		// android:text="Zgan_JTWS"
		// android:text="Zg123456"
		wifiAdmin.openWifi();// 打开wifi
		if (!wifiAdmin.getmWifiManager().isWifiEnabled()) {
			if (wifiAdmin.getmWifiManager().setWifiEnabled(true)) {

				// WifiInfo wifiInfo=wifiAdmin.GetWifiInfo();//获取链接的wifi信息
				// name.setText(wifiInfo.getSSID());
				// WifiConfiguration
				// wifiConfiguration=wifiAdmin.IsExsits(wifiInfo.getSSID());
				// String
				// pass=wifiConfiguration.preSharedKey==null?wifiConfiguration.wepKeys[0]:wifiConfiguration.preSharedKey;
				// password.setText(pass);

			} else {
				Toast.makeText(WifiListGet.this, R.string.aptxt_wifierr,
						Toast.LENGTH_LONG).show();
				return;
			}
		}
		
		if (wifiList!=null) {
			wifiList.clear();
		}
		
		wifiAdmin.startScan();		
		wifiList = wifiAdmin.getWifiList();
		adapter = new WifiListAdapter(wifiList);
		wifispinner.setAdapter(adapter);
		dialog.dismiss();
		SpinnerSet();
	}

	private void SpinnerSet() {
		// TODO Auto-generated method stub
		int flag;
		String str=wifiAdmin.getNowConnetWifi().getSSID();


		try {
			flag=isIntEquals(wifiList, str);

			if (flag!=-1) {
				wifispinner.setSelection(flag);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	protected void SetWifi(final String sSID2, final String wIFIKEY2) {
		// TODO Auto-generated method stub
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String str3 = sendGet("http://192.168.234.1/cgi-bin/getwifiattr.cgi?cmd=getwifiattr");
	
				String[] sub = str3.split("var linkstatus=");

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (sub.length > 1) {
					String sub2 = (sub[1].replace("\"", "")).substring(0, 1);
					
					if (sub2.equals("0")) {
						Looper.prepare();
						Toast.makeText(getApplicationContext(), "家庭卫士重启成功",
								Toast.LENGTH_LONG).show();
						connectTargetWifi2(sSID2, wIFIKEY2);
						dialog.dismiss();
						finish();
					}
				} else {
	
					SetWifi(SSID, WIFIKEY);
				}
				super.run();

			}
		}.start();
	}

	protected void setWIFIList() {
		// TODO Auto-generated method stub
		if (!wifiAdmin.getmWifiManager().isWifiEnabled()) {
			if (wifiAdmin.getmWifiManager().setWifiEnabled(true)) {

				// SSID=wifispinner.getSelectedItem().toString();
				WIFIKEY = password.getText().toString();


				// connectTargetWifi(na, pa);//����ָ��wifi
				if (SSID != null && SSID != null) {
					dialog.setMessage(getResources().getString(
							R.string.aptxt_wifiljw));
					// dialog.show();
					SetWifi(SSID, WIFIKEY);
				} else {
					Toast.makeText(WifiListGet.this, "密码不能为空",
							Toast.LENGTH_LONG).show();
				}

			} else {
				Toast.makeText(WifiListGet.this, "wifi开启失败，请手动开启",
						Toast.LENGTH_LONG).show();
			}
		} else {


			WIFIKEY = password.getText().toString();

			if (SSID != null && WIFIKEY != null) {
				dialog.setMessage(getResources().getString(
						R.string.aptxt_wifiop));

				SetWifi(SSID, WIFIKEY);
			} else {
				Toast.makeText(WifiListGet.this, "wifi密码不能为空",
						Toast.LENGTH_LONG).show();
			}

		}
	}
	/**
	 * 判断该wifi名称是否存在
	 * @param list
	 * @param str
	 * @return true or false
	 * @throws Exception
	 */
	public static boolean isItEquals(List<ScanResult> list, String str)
			throws Exception {
		for (int i = 0; i < list.size(); i++) {
			if (str.equals(list.get(i).SSID.toString())) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 判断该wifi名称是否存在
	 * @param list
	 * @param str
	 * @return i
	 * @throws Exception
	 */
	public static int  isIntEquals(List<ScanResult> list, String str)
			throws Exception {
		for (int i = 0; i < list.size(); i++) {
			if (str.equals(list.get(i).SSID.toString())) {
				return i;
			}
		}
		return -1;
	}
	private void connectTargetWifi(String na, String pa) {

		// builder.append("初始时的wifi信息"+wifiAdmin.GetWifiInfo()+"\n");
		wifiAdmin.openWifi();
		wifiAdmin.startScan();
		try {
			wifiAdmin.startScan();
			wifiList = wifiAdmin.getWifiList();
			if (isItEquals(wifiList, na) == false) {
				Toast.makeText(getApplicationContext(), "家庭卫士AP模式未启动",
						Toast.LENGTH_SHORT).show();
				dialog.dismiss();
				return;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WifiConfiguration cofg1 = wifiAdmin.CreateWifiInfo(na, "", 1);
		// Log.e("cofg1",""+cofg1);
		if (wifiAdmin.AddNetwork(cofg1)) {
			// registerSetWifiChangedBoradCast();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					/**
					 * str="COMPLETED" 则链接该wifi
					 */
					String str = wifiAdmin.GetWifiInfo().getSupplicantState()
							.toString();
					if (str.equals("COMPLETED")) {
						// builder.append("链接成功后的wifi信息"+str+"\n");
						Toast.makeText(getApplicationContext(), "家庭卫士链接成功",
								Toast.LENGTH_SHORT).show();
						dialog.setMessage(getResources().getString(
								R.string.aptxt_wifiop));
						dialog.show();
						setWIFIList();

					} else {
						// builder.append("链接失败后的wifi信息"+str+"\n");
						Toast.makeText(getApplicationContext(), "正在重新链接家庭卫士",
								Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						connectTargetWifi("IPCAM-AP", null);
					}
					// showInfo.setText(builder.toString());
				}
			}, 5000);
		} else {
			// builder.append("链接出错！请手动链接");
			Toast.makeText(getApplicationContext(), na+R.string.aptxt_aperr,
					Toast.LENGTH_SHORT).show();
			dialog.dismiss();

			// showInfo.setText(builder.toString());
		}

	}

	private void connectTargetWifi2(String na, String pa) {

		// builder.append("初始时的wifi信息"+wifiAdmin.GetWifiInfo()+"\n");
		wifiAdmin.openWifi();
		wifiAdmin.startScan();

		WifiConfiguration cofg3 = wifiAdmin.CreateWifiInfo(na, pa, 3);
		WifiConfiguration cofg2 = wifiAdmin.CreateWifiInfo(na, pa, 2);
		WifiConfiguration cofg1 = wifiAdmin.CreateWifiInfo(na, "", 1);
		if (wifiAdmin.AddNetwork(cofg3) || wifiAdmin.AddNetwork(cofg2)
				|| wifiAdmin.AddNetwork(cofg1)) {
			// registerSetWifiChangedBoradCast();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					/**
					 * str="COMPLETED" 则链接该wifi
					 */
					String str = wifiAdmin.GetWifiInfo().getSupplicantState()
							.toString();
					if (str.equals("COMPLETED")) {
						// builder.append("链接成功后的wifi信息"+str+"\n");
						Toast.makeText(getApplicationContext(),
								R.string.aptxt_apok, Toast.LENGTH_SHORT).show();

						dialog.dismiss();
						finish();
					} else {
						// builder.append("链接失败后的wifi信息"+str+"\n");
						Toast.makeText(getApplicationContext(),
								R.string.aptxt_aperr, Toast.LENGTH_SHORT)
								.show();
						dialog.dismiss();
						finish();

					}
					// showInfo.setText(builder.toString());
				}
			}, 3000);
		} else {
			// builder.append("链接出错！请手动链接");

			Toast.makeText(getApplicationContext(), R.string.aptxt_aperr,
					Toast.LENGTH_SHORT).show();

			dialog.dismiss();
			finish();
			// showInfo.setText(builder.toString());
		}

	}

	/**
	 * 链接IPCAM后进行wifi列表获取
	 */
	/*
	 * private void GetWifiList() { // TODO Auto-generated method stub
	 * 
	 * // TODO Auto-generated method stub new Thread() {
	 * 
	 * @Override public void run() { // TODO Auto-generated method stub String
	 * str = sendGet("http://192.168.234.1/cgi-bin/listwifiap.cgi"); String[]
	 * data = str.split("var ssid"); for (int i = 0; i < data.length; i++) {
	 * Log.i("WifiListGet string ", data[i]); String[] idata =
	 * data[i].split(";"); list_details = new ArrayList<String>();
	 * 
	 * for (int j = 0; j < idata.length; j++) { String SubDev = idata[j];
	 * Log.i("�豸��Ϣ" + j, SubDev); list_details.add(SubDev); } if
	 * (list_details.size() == 3) { WIFI_Info info = new WIFI_Info();
	 * info.setSSID(list_details.get(0)); info.setSingle(list_details.get(1));
	 * info.setSecret(list_details.get(2)); Log.i("list_details.get(0)", "" +
	 * list_details.get(0)); Log.i("list_details.get(1)", "" +
	 * list_details.get(1)); Log.i("list_details.get(2)", "" +
	 * list_details.get(2));
	 * 
	 * list.add(info); }
	 * 
	 * } Log.i("list.toString", list.toString()); Log.i("WifiListGet string ",
	 * str); handler.sendEmptyMessage(10); super.run();
	 * 
	 * } }.start(); }
	 */

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

	/*
	 * private LayoutInflater inflater;// �õ�һ��LayoutInfalter�����������벼��
	 * private List<WIFI_Info> dataList;
	 *//**
	 * ���캯��
	 * 
	 * @param context
	 * @param i
	 */
	/*
	 * public WifiListAdapter(List<WIFI_Info> list) { inflater =
	 * LayoutInflater.from(context); dataList = list; }
	 * 
	 * @Override public int getCount() { // TODO Auto-generated method stub
	 * return dataList.size(); }
	 * 
	 * @Override public Object getItem(int position) { // TODO Auto-generated
	 * method stub return dataList.get(position); }
	 * 
	 * @Override public long getItemId(int position) { // TODO Auto-generated
	 * method stub return position; }
	 * 
	 * @Override public View getView(int position, View convertView, ViewGroup
	 * parent) { ViewHolder holder; if (convertView == null) {
	 * 
	 * convertView = inflater.inflate( R.layout.community_political_item, null);
	 * holder = new ViewHolder();
	 *//** �õ������ؼ��Ķ��� */
	/*
	 * holder.title = (TextView) convertView .findViewById(R.id.itemTitle);
	 * 
	 * convertView.setTag(holder);// ��ViewHolder���� } else { holder =
	 * (ViewHolder) convertView.getTag();// ȡ��ViewHolder���� }
	 *//** ����TextView��ʾ������ ��Title����¼� */
	/*
	 * final WIFI_Info news = dataList.get(position); final String[] str =
	 * news.getSSID().split("="); final String[] str_signle =
	 * news.getSingle().split("=");
	 * 
	 * holder.title.setText(str[1].replace("\"", "")); // ȥ����� );
	 * holder.title.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { name.setText(str[1].replace("\"",
	 * "")); SSID = str[1].replace("\"", ""); WKTYPE =
	 * str_signle[1].replace("\"", ""); } });
	 * 
	 * return convertView; }
	 *//**
	 * ��ſؼ�
	 * */
	/*
	 * public final class ViewHolder { public TextView title; }
	 */

	public static String sendGet(String str) {
		String reStr = "";
		BufferedReader in = null;
		try {
			URL url = new URL(str);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setConnectTimeout(10000);
			conn.setRequestMethod("GET");
			// conn.setRequestProperty("accept", "*/*");

			conn.connect();
			Map<String, List<String>> map = conn.getHeaderFields();
			try {
				for (String key : map.keySet()) {
					System.out.println(key + "--->" + map.get(key));
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			String line;
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			while ((line = in.readLine()) != null) {
				System.out.print(line);
				reStr += line;
			}

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		return reStr;
		// TODO Auto-generated method stub

	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//wifiAdmin.DisconnetmWifiManager();
		super.onDestroy();
	}
}
