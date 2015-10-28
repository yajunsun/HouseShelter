package com.hiibox.houseshelter.activity;

import com.hiibox.houseshelter.MyApplication;
import com.tutk.IOTC.AVAPIs;
import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.zgan.tutk.MyCamera;
import com.zgan.youbao.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class testhomeactivity extends Activity {

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (myCamera.isSessionConnected())
			myCamera.disconnect();
		MyCamera.uninit();
	}

	public static MyCamera myCamera;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 MyApplication.deviceCode="WBPXTJRR22YL9NBXA7CJ";
		MyCamera.init();
			AVAPIs.avClientSetMaxBufSize(2000);
			myCamera = new MyCamera("admin[" + MyApplication.deviceCode + "]",
					MyApplication.deviceCode, "admin", "admin");
			myCamera.connect(MyApplication.deviceCode);
			myCamera.start(MyCamera.DEFAULT_AV_CHANNEL, "admin", "admin");
			myCamera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
			myCamera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq.parseContent());
			myCamera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq
							.parseContent());
			myCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());
			myCamera.LastAudioMode = 1;
			setContentView(R.layout.testhomeactivity);
	}

	public void Onclick(View v) {
		if (v.getId() == R.id.btngo) {
			Intent intent=new Intent();
			intent.setClass(this, NewsCloudEyesActivity.class);
			startActivity(intent);
		}
	}
}
