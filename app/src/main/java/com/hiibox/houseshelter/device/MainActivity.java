package com.hiibox.houseshelter.device;



import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	Button textView;
	String st;
	String Data;
	String content;
	Handler handler;
	ProgressDialog dialog;
	int did = 1;
	int sid = 1;
	LocalService localService;
	DeviceList deviceList;
	DeviceLocalInfo deviceLocalInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		localService = new LocalService();
		deviceList = new DeviceList();
		localService.init(deviceList);

	}

	OnClickListener l = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			localService.setDeviceParam(localService.dev_Info);
			
		}

	};

}
