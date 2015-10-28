package com.hiibox.houseshelter.activity;

import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.hiibox.houseshelter.MyApplication;
import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.net.SpliteUtil;
import com.hiibox.houseshelter.util.FileUtil;
import com.hiibox.houseshelter.util.IBreakUtil;
import com.hiibox.houseshelter.util.MessageUtil;
import com.hiibox.houseshelter.util.PreferenceUtil;
import com.hiibox.houseshelter.view.GestureDialog;
import com.hiibox.houseshelter.view.NineGridsView;
import com.hiibox.houseshelter.view.NineGridsView.OnCompleteListener;
import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.zg.camera.Monitor;
import com.zg.camera.MyCamera;
import com.zgan.youbao.R;

/**
 * 云眼界面
 * 
 * @author Administrator
 * 
 */

@SuppressLint({ "HandlerLeak", "Wakelock" })
public class CloudEyesActivity extends ShaerlocActivity implements
		IRegisterIOTCListener {

	public Monitor monitor;
	public static MyCamera camera;
	Button voice_mothed;
	Button listener_mothed;
	private boolean mIsListening = true;
	private WakeLock wakeLock;
	private String deviceCode = null;
	private Bitmap bmp = null;
	private boolean gestureToggle = false; // 安全手势开关
	private String enter = null;
	public GestureDialog gestureDialog = null;

	ProgressDialog msgDialog;
	public boolean isCloudEyesActivityRun = true;
	public static boolean CloudEyesActivityIsRun =false;
	public int flag = 0;
	// 云相册
	@ViewInject(id = R.id.cloud_photo_album_iv, click = "onClick")
	ImageView cloudPhotoAlbumIV;
	@ViewInject(id = R.id.orientation_iv, click = "onClick")
	ImageView orientationIV;
	@ViewInject(id = R.id.screenshot_iv, click = "onClick")
	ImageView screenshotIV;
	@ViewInject(id = R.id.voice_mute_iv, click = "onClick")
	CheckBox cb;
	@ViewInject(id = R.id.settings_iv, click = "onClick")
	ImageView settingsIV;
	@ViewInject(id = R.id.turn_off_iv, click = "onClick")
	ImageView turn_off_iv;

	@SuppressLint("HandlerLeak")
	private Handler Monitorhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {
				msgDialog.dismiss();

				if (isCloudEyesActivityRun) {
					isCloudEyesActivityRun = false;

					if (mIsListening) {
						mIsListening = true;
						camera.startListening(0);
					} else {
						camera.startSpeaking(0);
					}
				}

			}
		}
	};

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (null == msg.obj) {
				return;
			}
			Frame[] frame = (Frame[]) msg.obj;
			if (null == frame[0]) {
				return;
			}
			if (null == frame[1]) {
				return;
			}
			int subCmd = frame[0].subCmd;

			if (msg.what == 0) {
				if (subCmd == 18) {
					int status = Integer.valueOf(frame[1].strData);
					if (status == 0) {
						
					} else if (status == 1) {
						MessageUtil.alertMessage(mContext,
								R.string.no_rights_to_upload_picture);
					} else if (status == 2) {
						MessageUtil.alertMessage(mContext, R.string.low_memory);
					} else if (status == 3) {
						MessageUtil.alertMessage(mContext,
								R.string.mistake_type);
					}
				} else if (subCmd == 19) {
					if (SpliteUtil.getRuquestStatus(frame[1].strData)) {
						MessageUtil.alertMessage(mContext,
								R.string.photo_upload_to_cloud_album);
						return;
					} else {
						MessageUtil.alertMessage(mContext,
								R.string.upload_picture_failed);
					}
					if (SpliteUtil.getResult(frame[1].strData).equals("1")) {
						sendRequest(19);
					}
				} else if (subCmd == 80) {
					if (SpliteUtil.getRuquestStatus(frame[1].strData)) {
						deviceCode = SpliteUtil.getResult(frame[1].strData);
						MyApplication.deviceCode = deviceCode;
						getDataByVideo();
					} else {
						MessageUtil.alertMessage(mContext, R.string.no_device);
					}
				}
			} else if (msg.what == -1) {
				MessageUtil.alertMessage(mContext, R.string.network_timeout);
			}

		}
	};

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (monitor != null) {
			monitor.deattachCamera();
		}

		if (camera != null) {
			camera.stopShow(0);
			camera.stopListening(0);
			camera.stopSpeaking(0);
		}

	}

	@Override
	protected void onRestart() {
		super.onRestart();

		if (monitor != null) {
			monitor.attachCamera(camera, 0, Monitorhandler);
		}

		if (camera != null) {
			camera.startShow(0, true);

			if (mIsListening) {
				mIsListening = true;
				camera.startListening(0);
			} else {
				camera.startSpeaking(0);
			}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cloud_eyes_layout);
		CloudEyesActivityIsRun=true;
		setall();

		msgDialog = new ProgressDialog(this);
		msgDialog.setCancelable(false);

		enter = (String) getLastNonConfigurationInstance();
		if (TextUtils.isEmpty(enter)) {
			enter = getIntent().getStringExtra("enter");
		}
		gestureToggle = PreferenceUtil.getInstance(mContext).getBoolean(
				"gestureToggle", false);

		if (gestureToggle) {

			if (!TextUtils.isEmpty(enter)
					&& enter.equals("1")
					&& this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

				loadAdsDialog();

				enter = "2";
				MyApplication.isFirstTimeEntry = false;
			}

		} else {
			getDataByVideo();
		}

		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub

				if (arg1) {
					cb.setBackgroundResource(R.drawable.voice_off_icon);
					if (null != camera) {

						mIsListening = true;
						camera.stopSpeaking(0);
						camera.startListening(0);
					}
				} else {
					cb.setBackgroundResource(R.drawable.mute_on_icon);
					if (null != camera) {

						mIsListening = false;
						camera.stopListening(0);
						camera.startSpeaking(0);

					}
				}
			}
		});

	}

	private void loadAdsDialog() {
		if (isFinalActivity) {

		}

		final String currPassword = PreferenceUtil.getInstance(mContext)
				.getString("gestureTracks", null);
		View splashView = getLayoutInflater().inflate(
				R.layout.activity_gesture_setting_layout, null);
		final NineGridsView ninePoints = (NineGridsView) splashView
				.findViewById(R.id.lock_screen_view);
		TextView promptTV = (TextView) splashView.findViewById(R.id.prompt_tv);
		ninePoints.setIsUnlock(true);
		promptTV.setText(R.string.draw_current_safe_gesture);
		gestureDialog = new GestureDialog(this, splashView);
		gestureDialog.show();

		ninePoints.setOnCompleteListener(new OnCompleteListener() {
			@Override
			public void onComplete(String password) {
				if (currPassword.equals(password)) {
					ninePoints.clearUnlockInfo();
					ninePoints.clearPassword();
					password = null;
					gestureDialog.dismiss();
					gestureToggle = false;
					getDataByVideo();
				} else { // 解锁失败
					MessageUtil.alertMessage(mContext, R.string.unlock_failure);

				}
				ninePoints.clearUnlockInfo();
				ninePoints.clearPassword();
				password = null;
			}
		});
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		enter = "2";
		return enter;
	}

	/**
	 * 长亮
	 */
	private void setall() {
		// TODO Auto-generated method stub
		PowerManager manager = ((PowerManager) getSystemService(POWER_SERVICE));
		wakeLock = manager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
				| PowerManager.ON_AFTER_RELEASE, "ATAAW");
		wakeLock.acquire();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cloud_photo_album_iv:
			startActivity(new Intent(mContext, CloudPhotoAlbumActivity.class));
			//MianActivity.getScreenManager().exitActivity(mActivity);
			break;
		case R.id.screenshot_iv:
			toShowMsg();
			toSetMsgText("截图正在保存在本地相册..");
			
			if(null!=camera){
				bmp = camera.Snapshot(0);
				
				if(bmp!=null){
					//检查SD是否存在
					if (FileUtil.isSdCardMounted()) {
						 if(IBreakUtil.toSavePic(bmp,mContext)){
							 MessageUtil.alertMessage(mContext,getString(R.string.save_picure_path));
						 }else{
							 MessageUtil.alertMessage(mContext,R.string.save_picture_failed);
						 }
					}else{
						MessageUtil.alertMessage(mContext,R.string.sd_card_unmounted);
					}		
					
					bmp.recycle();
					bmp = null;
				}
			}
			
			break;
		case R.id.orientation_iv:

			if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 竖屏
				turn_off_iv.setVisibility(View.VISIBLE);
			} else {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 横屏
				turn_off_iv.setVisibility(View.GONE);
			}

			break;
		case R.id.settings_iv:
			startActivityForResult(new Intent(mContext,
					CloudEyesSettingsActivity.class), 0x111);
			break;
		case R.id.turn_off_iv:
			quit();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 0x101 && resultCode == RESULT_OK) {
			boolean unlockSuccess = data
					.getBooleanExtra("unlockSuccess", false);

			if (!unlockSuccess) {
				MianActivity.getScreenManager().exitActivity(mActivity);
				return;
			}
		} else if (requestCode == 0x111) {
		}
	}

	/**
	 * 连接监视器
	 */
	private void getDataByVideo() {
		// TODO Auto-generated method stub

		if (camera != null) {
			camera.unregisterIOTCListener(this);

			// Stop all actions of camera and then disconnect.
			camera.stopListening(0);
			camera.stopSpeaking(0);
			camera.stopShow(0);
			camera.stop(0);
			camera.disconnect();
			camera = null;

			Camera.uninit();
		}

		toShowMsg();

		camera = new MyCamera("Camera", MyApplication.deviceCode,
				MyApplication.authCode);

		camera.registerIOTCListener(this);

		monitor = (Monitor) this.findViewById(R.id.monitor);

		monitor.attachCamera(camera, 0, Monitorhandler);

		Camera.init(); // initialize IOTCAPIs
		camera.connect(camera.getUID());

		camera.start(0, "admin", "admin");

		if (!camera.isSessionConnected()) {

			camera.connect(camera.getUID());
			camera.start(0, "admin", "admin");
			camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ,
					SMsgAVIoctrlGetSupportStreamReq.parseContent());
			camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
			camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq
							.parseContent());

		}
		camera.startShow(0, true);

	}

	public void onDestroy() {
		super.onDestroy();

		// Uninit IOTCAPIs finally.
		if (monitor != null) {
			monitor.deattachCamera();

		}

		// Unregister this camera.
		if (camera != null) {
			camera.unregisterIOTCListener(this);

			// Stop all actions of camera and then disconnect.
			camera.stopListening(0);
			camera.stopSpeaking(0);
			camera.stopShow(0);
			camera.stop(0);
			camera.disconnect();
			camera = null;
		}
		Camera.uninit();
		CloudEyesActivityIsRun=false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK:

			quit();

			break;
		}

		return super.onKeyDown(keyCode, event);
	}

	private void quit() {
		// TODO Auto-generated method stub

		if (bmp != null && !bmp.isRecycled()) {
			bmp.recycle();
			bmp = null;
		}

		if (handler != null) {
			handler.removeCallbacks(null);
			handler = null;
		}

		if (cameraHandler != null) {
			cameraHandler.removeCallbacks(null);
			cameraHandler = null;
		}

		MianActivity.getScreenManager().exitActivity(mActivity);

	}

	@Override
	public void receiveChannelInfo(Camera arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveFrameData(Camera arg0, int arg1, Bitmap arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveFrameInfo(Camera arg0, int arg1, long arg2, int arg3,
			int arg4, int arg5, int arg6) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveIOCtrlData(Camera arg0, int arg1, int arg2, byte[] arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveSessionInfo(Camera mcamera, int resultCode) {
		if (camera == mcamera) {
			Bundle bundle = new Bundle();
			Message msg = cameraHandler.obtainMessage();
			msg.what = resultCode;
			msg.setData(bundle);
			cameraHandler.sendMessage(msg);
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler cameraHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Camera.CONNECTION_STATE_CONNECTING:
				toSetMsgText("正在连接视频，请稍后...");

				break;
			case Camera.CONNECTION_STATE_CONNECTED:
				toSetMsgText("视频连接成功");

				break;
			case Camera.CONNECTION_STATE_DISCONNECTED:
				toCloseMsg();
				MessageUtil
						.alertMessage(mContext, R.string.connstus_disconnect);

				break;
			case Camera.CONNECTION_STATE_UNKNOWN_DEVICE:
				toCloseMsg();
				MessageUtil.alertMessage(mContext,
						R.string.connstus_unknown_device);

				break;
			case Camera.CONNECTION_STATE_TIMEOUT:
				toCloseMsg();
				if (camera != null) {

					camera.stopSpeaking(0);
					camera.stopListening(0);
					camera.stopShow(0);
					camera.stop(0);
					camera.disconnect();
					camera.connect(camera.getUID());
					camera.start(0, "admin", "admin");
					camera.startShow(0, true);

					camera.sendIOCtrl(
							Camera.DEFAULT_AV_CHANNEL,
							AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ,
							AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq
									.parseContent());
					camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
							AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
							AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq
									.parseContent());
					camera.sendIOCtrl(
							Camera.DEFAULT_AV_CHANNEL,
							AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ,
							AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq
									.parseContent());

					if (mIsListening)
						camera.startListening(0);
				}
				MessageUtil.alertMessage(mContext, R.string.connstus_timeout);

				break;
			case Camera.CONNECTION_STATE_CONNECT_FAILED:
				toCloseMsg();
				MessageUtil.alertMessage(mContext,
						R.string.connstus_connection_failed);

				break;
			case Camera.CONNECTION_STATE_WRONG_PASSWORD:
				toCloseMsg();
				MessageUtil.alertMessage(mContext,
						R.string.connstus_wrong_password);

				break;
			default:
				break;
			}
		}
	};

	/**
	 * 发送请求
	 * 
	 * @param subCmd
	 */
	private void sendRequest(int subCmd) {

	}

	private void toShowMsg() {
		msgDialog.show();

	}

	private void toSetMsgText(String strMsg) {

		msgDialog.setMessage(strMsg);
	}

	private void toCloseMsg() {
		msgDialog.dismiss();

	}
	
}
