package com.hiibox.houseshelter.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.R.string;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.hiibox.houseshelter.MyApplication;
import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.core.GlobalUtil;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.util.MessageUtil;
import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IOTCAPIs;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.Monitor;
import com.tutk.IOTC.St_SInfo;
import com.zgan.tutk.MyCamera;
import com.zgan.youbao.R;

public class NewsCloudEyesActivity extends ShaerlocActivity implements
		ViewSwitcher.ViewFactory, IRegisterIOTCListener {

	private static final int BUILD_VERSION_CODES_ICE_CREAM_SANDWICH = 14;
	private static final int STS_CHANGE_CHANNEL_STREAMINFO = 99;
	private static final int STS_SNAPSHOT_SCANED = 98;
	// private static final String FILE_TYPE = "image/*";

	private static final int REQUEST_CODE_ALBUM = 99;

	private static final int OPT_MENU_ITEM_ALBUM = Menu.FIRST;
	private static final int OPT_MENU_ITEM_SNAPSHOT = Menu.FIRST + 1;
	// private static final int OPT_MENU_ITEM_SUBSTREAM = Menu.FIRST + 2;
	private static final int OPT_MENU_ITEM_AUDIOCTRL = Menu.FIRST + 2;
	private static final int OPT_MENU_ITEM_AUDIO_IN = Menu.FIRST + 3;
	private static final int OPT_MENU_ITEM_AUDIO_OUT = Menu.FIRST + 4;

	// private TouchedMonitor monitor = null;
	private Monitor monitor = null;
	private MyCamera mCamera = null;
	private String mDevUID;
	private int mVideoWidth;
	private int mVideoHeight;
	private int mSelectedChannel;

	private boolean mIsListening = false;
	private boolean mIsSpeaking = false;

	private ImageView btn_Quit;
	private ImageView btn_SavePhoto;
	private ImageView btn_goAlbum;
	private ImageView btn_goSetup;
	private ImageView btn_Change;
	private CheckBox btn_Speak;
	private ProgressDialog msgDialog;
	public static boolean CloudEyesActivityIsRun = false;

	// //////
	private int mVideoFPS;
	private long mVideoBPS;
	private int mOnlineNm;
	private int mFrameCount;
	private int mIncompleteFrameCount;
	private TextView txtConnectionSlash;
	private TextView txtResolutionSlash;
	private TextView txtShowFPS;
	private TextView txtFPSSlash;
	private TextView txtShowBPS;
	private TextView txtOnlineNumberSlash;
	private TextView txtShowFrameRatio;
	private TextView txtFrameCountSlash;
	private TextView txtRecvFrmPreSec;
	private TextView txtRecvFrmSlash;
	private TextView txtDispFrmPreSeco;

	private TextView txtConnectionMode;
	private TextView txtResolution;
	private TextView txtFrameRate;
	private TextView txtBitRate;
	private TextView txtOnlineNumber;
	private TextView txtFrameCount;
	private TextView txtIncompleteFrameCount;
	private TextView txtPerformance;
	private long waitingtime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.live_view_portrait);
		// 保持屏幕常亮
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		CloudEyesActivityIsRun = true;
		initView();
		mDevUID = MyApplication.deviceCode;
		// 等待HomepageActivity.myCamera初始化成功
		toShowMsg();
		toSetMsgText("视频连接中");

		new Thread(new Runnable() {

			@Override
			public void run() { // TODO Auto-generated method stub
				while (HomepageActivity.myCamera == null) {

					try {
						// 当等待时间大于5秒时退出当前监控界面
						if (waitingtime > 5000) {
							handler.post(new Runnable() {
								public void run() {
									MessageUtil.alertMessage(mContext,
											R.string.connstus_timeout);
									MianActivity.getScreenManager()
											.exitActivity(mActivity);

								}
							});
						}
						Thread.sleep(500);
						waitingtime += 500;
					} catch (InterruptedException e) {
						// TODOAuto-generated catch block
						e.printStackTrace();
					}
				}

				handler.post(new Runnable() {

					@Override
					public void run() { // TODO Auto-generated method stub
						mSelectedChannel = 0;
						mCamera = HomepageActivity.myCamera;
						if (!mCamera.isSessionConnected()) {
							toStartVideo();
						}
						mCamera.registerIOTCListener(NewsCloudEyesActivity.this);
						mCamera.startShow(mSelectedChannel, true);
						if (monitor != null)
							monitor.deattachCamera();

						monitor = null;
						monitor = (Monitor) findViewById(R.id.monitor);
						monitor.setMaxZoom(3.0f);
						monitor.mEnableDither = mCamera.mEnableDither;
						monitor.attachCamera(mCamera, mSelectedChannel);

						toStartListening();
						//toCloseMsg();
					}
				});

			}
		}).start();

		/*
		 * while (HomepageActivity.myCamera == null) { try { Thread.sleep(500);
		 * } catch (InterruptedException e) { // TODO Auto-generated catch block
		 * // e.printStackTrace(); Log.v("suntest", e.getMessage());
		 * MianActivity.getScreenManager().exitActivity(mActivity); }
		 * 
		 * } mSelectedChannel = 0; mCamera = HomepageActivity.myCamera; if
		 * (!mCamera.isSessionConnected()) { toStartVideo(); }
		 * mCamera.registerIOTCListener(this);
		 * mCamera.startShow(mSelectedChannel, true); if (monitor != null)
		 * monitor.deattachCamera();
		 * 
		 * monitor = null; monitor = (Monitor) findViewById(R.id.monitor);
		 * monitor.setMaxZoom(3.0f); monitor.mEnableDither =
		 * mCamera.mEnableDither; monitor.attachCamera(mCamera,
		 * mSelectedChannel);
		 * 
		 * toStartListening();
		 */
	}

	private void initView() {
		// /

		txtConnectionSlash = (TextView) findViewById(R.id.txtConnectionSlash);
		txtResolutionSlash = (TextView) findViewById(R.id.txtResolutionSlash);
		txtShowFPS = (TextView) findViewById(R.id.txtShowFPS);
		txtFPSSlash = (TextView) findViewById(R.id.txtFPSSlash);
		txtShowBPS = (TextView) findViewById(R.id.txtShowBPS);

		txtOnlineNumberSlash = (TextView) findViewById(R.id.txtOnlineNumberSlash);
		txtShowFrameRatio = (TextView) findViewById(R.id.txtShowFrameRatio);
		txtFrameCountSlash = (TextView) findViewById(R.id.txtFrameCountSlash);

		txtDispFrmPreSeco = (TextView) findViewById(R.id.txtDispFrmPreSeco);
		txtRecvFrmSlash = (TextView) findViewById(R.id.txtRecvFrmSlash);
		txtRecvFrmPreSec = (TextView) findViewById(R.id.txtRecvFrmPreSec);
		txtPerformance = (TextView) findViewById(R.id.txtPerformance);

		txtConnectionMode = (TextView) findViewById(R.id.txtConnectionMode);
		txtResolution = (TextView) findViewById(R.id.txtResolution);
		txtFrameRate = (TextView) findViewById(R.id.txtFrameRate);
		txtBitRate = (TextView) findViewById(R.id.txtBitRate);
		txtOnlineNumber = (TextView) findViewById(R.id.txtOnlineNumber);
		txtFrameCount = (TextView) findViewById(R.id.txtFrameCount);
		txtIncompleteFrameCount = (TextView) findViewById(R.id.txtIncompleteFrameCount);

		btn_Quit = (ImageView) findViewById(R.id.turn_off_iv);
		btn_SavePhoto = (ImageView) findViewById(R.id.screenshot_iv);
		btn_goAlbum = (ImageView) findViewById(R.id.cloud_photo_album_iv);
		btn_goSetup = (ImageView) findViewById(R.id.settings_iv);
		btn_Change = (ImageView) findViewById(R.id.orientation_iv);
		btn_Speak = (CheckBox) findViewById(R.id.voice_mute_iv);

		msgDialog = new ProgressDialog(this);
		// modified by yajunsun 20150917
		msgDialog.setCancelable(false);

		if (btn_Quit != null) {
			// 退出
			btn_Quit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					toQuitVideo();
				}
			});
		}

		// 保存照片
		btn_SavePhoto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				toSavePhoto();
			}
		});

		// 跳转云相册
		btn_goAlbum.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(mContext,
						CloudPhotoAlbumActivity.class));
			}
		});

		// 跳转云眼设置
		btn_goSetup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(mContext,
						CloudEyesSettingsActivity.class));
			}
		});

		// 横\竖屏切换
		btn_Change.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (NewsCloudEyesActivity.this.getResources()
						.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 竖屏

				} else {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 横屏
				}

			}
		});

		// 监听\通话切换
		btn_Speak.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub

				if (arg1) {
					btn_Speak.setBackgroundResource(R.drawable.voice_off_icon);

					toStartListening();

				} else {
					btn_Speak.setBackgroundResource(R.drawable.mute_on_icon);

					toStartSpeaking();
				}
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
		// FlurryAgent.onStartSession(this, "Q1SDXDZQ21BQMVUVJ16W");
	}

	@Override
	protected void onStop() {
		super.onStop();
		// FlurryAgent.onEndSession(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mCamera != null) {
			mCamera.stopSpeaking(mSelectedChannel);
			mCamera.stopListening(mSelectedChannel);
			mCamera.stopShow(mSelectedChannel);
		}

		if (monitor != null)
			monitor.deattachCamera();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (monitor != null) {
			monitor.mEnableDither = mCamera.mEnableDither;
			monitor.attachCamera(mCamera, mSelectedChannel);
		}

		if (mCamera != null) {

			mCamera.startShow(mSelectedChannel, true);

			toSetSpeaking();
		}
	}

	// @Override
	// public void onConfigurationChanged(Configuration newConfig) {
	// super.onConfigurationChanged(newConfig);
	//
	// Configuration cfg = getResources().getConfiguration();
	//
	// if (cfg.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	//
	// setupViewInLandscapeLayout();
	//
	// } else if (cfg.orientation == Configuration.ORIENTATION_PORTRAIT) {
	//
	// setupViewInPortraitLayout();
	// }
	// }

	// 启动视频
	private void toStartVideo() {

		if (mCamera != null) {
			mCamera.connect(mDevUID);
			mCamera.start(Camera.DEFAULT_AV_CHANNEL, "admin", "admin");

			mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq.parseContent());
			mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
			mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq
							.parseContent());

			mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());
			mCamera.LastAudioMode = 1;
		}
	}

	// 退出视频
	private void toQuitVideo() {

		toShowMsg();
		toSetMsgText("正在关闭视频");

		if (monitor != null) {
			monitor.deattachCamera();
		}

		if (mCamera != null) {
			mCamera.unregisterIOTCListener(this);
			mCamera.stopSpeaking(mSelectedChannel);
			mCamera.stopListening(mSelectedChannel);
			mCamera.stopShow(mSelectedChannel);
			// mCamera.disconnect();
		}

		if (handler != null) {
			handler.removeCallbacks(null);
			handler = null;
		}
		// 在此处不关闭实例，只做停止显示的处理 modified by yajunsun 20150916
		// MyCamera.uninit();
		CloudEyesActivityIsRun = false;

		// NewsCloudEyesActivity.this.finish();
		toCloseMsg();
		MianActivity.getScreenManager().exitActivity(mActivity);
	}

	// 开启监听
	private void toStartListening() {
		mCamera.stopSpeaking(mSelectedChannel);
		mCamera.startListening(mSelectedChannel);

		mIsListening = true;
		mIsSpeaking = false;
	}

	// 开启通话
	private void toStartSpeaking() {
		mCamera.stopListening(mSelectedChannel);
		mCamera.startSpeaking(mSelectedChannel);

		mIsListening = false;
		mIsSpeaking = true;
	}

	// 判断通话状态
	private void toSetSpeaking() {
		if (mIsListening)
			toStartListening();
		if (mIsSpeaking)
			toStartSpeaking();
	}

	// 保存照片
	private void toSavePhoto() {

		if (mCamera != null && mCamera.isChannelConnected(mSelectedChannel)) {

			if (isSDCardValid()) {

				File rootFolder = new File(GlobalUtil.CAMERA_PATH);

				if (!rootFolder.exists()) {
					try {
						rootFolder.mkdir();
					} catch (SecurityException se) {
						// super.onOptionsItemSelected(item);
					}
				}

				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");// 设置日期格式

				String file = rootFolder.getPath() + "/Zgan_"
						+ df.format(new Date()) + ".jpg";
				Bitmap frame = mCamera != null ? mCamera
						.Snapshot(mSelectedChannel) : null;

				if (frame != null && saveImage(file, frame)) {

					MessageUtil.alertMessage(mContext,
							R.string.save_picure_path);
				} else {
					MessageUtil.alertMessage(mContext,
							R.string.save_picture_failed);
				}
			} else {
				MessageUtil.alertMessage(mContext, R.string.sd_card_unmounted);
			}
		}

	}

	// 横屏
	private void setupViewInLandscapeLayout() {

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		setContentView(R.layout.live_view_landscape);

		initView();

		if (monitor != null)
			monitor.deattachCamera();

		txtConnectionMode = null;
		txtResolution = null;
		txtFrameRate = null;
		txtBitRate = null;
		txtOnlineNumber = null;
		txtFrameCount = null;
		txtIncompleteFrameCount = null;
		txtRecvFrmPreSec = null;
		txtDispFrmPreSeco = null;
		txtPerformance = null;
		monitor = null;
		monitor = (Monitor) findViewById(R.id.monitor);
		monitor.setMaxZoom(3.0f);
		monitor.mEnableDither = mCamera.mEnableDither;
		monitor.attachCamera(mCamera, mSelectedChannel);

		if (mIsListening) {
			btn_Speak.setBackgroundResource(R.drawable.voice_off_icon);

			toStartListening();

		} else {
			btn_Speak.setBackgroundResource(R.drawable.mute_on_icon);

			toStartSpeaking();
		}
	}

	// 竖屏
	private void setupViewInPortraitLayout() {

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		setContentView(R.layout.live_view_portrait);

		initView();

		if (monitor != null)
			monitor.deattachCamera();
		txtConnectionSlash = (TextView) findViewById(R.id.txtConnectionSlash);
		txtResolutionSlash = (TextView) findViewById(R.id.txtResolutionSlash);
		txtShowFPS = (TextView) findViewById(R.id.txtShowFPS);
		txtFPSSlash = (TextView) findViewById(R.id.txtFPSSlash);
		txtShowBPS = (TextView) findViewById(R.id.txtShowBPS);

		txtOnlineNumberSlash = (TextView) findViewById(R.id.txtOnlineNumberSlash);
		txtShowFrameRatio = (TextView) findViewById(R.id.txtShowFrameRatio);
		txtFrameCountSlash = (TextView) findViewById(R.id.txtFrameCountSlash);

		txtDispFrmPreSeco = (TextView) findViewById(R.id.txtDispFrmPreSeco);
		txtRecvFrmSlash = (TextView) findViewById(R.id.txtRecvFrmSlash);
		txtRecvFrmPreSec = (TextView) findViewById(R.id.txtRecvFrmPreSec);
		txtPerformance = (TextView) findViewById(R.id.txtPerformance);

		txtConnectionMode = (TextView) findViewById(R.id.txtConnectionMode);
		txtResolution = (TextView) findViewById(R.id.txtResolution);
		txtFrameRate = (TextView) findViewById(R.id.txtFrameRate);
		txtBitRate = (TextView) findViewById(R.id.txtBitRate);
		txtOnlineNumber = (TextView) findViewById(R.id.txtOnlineNumber);
		txtFrameCount = (TextView) findViewById(R.id.txtFrameCount);
		txtIncompleteFrameCount = (TextView) findViewById(R.id.txtIncompleteFrameCount);
		monitor = null;
		monitor = (Monitor) findViewById(R.id.monitor);
		monitor.setMaxZoom(3.0f);
		monitor.mEnableDither = mCamera.mEnableDither;
		monitor.attachCamera(mCamera, mSelectedChannel);

		txtConnectionSlash.setText("");
		txtResolutionSlash.setText("");
		txtShowFPS.setText("");
		txtFPSSlash.setText("");
		txtShowBPS.setText("");
		txtOnlineNumberSlash.setText("");
		txtShowFrameRatio.setText("");
		txtFrameCountSlash.setText("");
		txtRecvFrmSlash.setText("");
		txtPerformance
				.setText(getPerformance((int) (((float) mCamera
						.getDispFrmPreSec() / (float) mCamera
						.getRecvFrmPreSec()) * 100)));

		txtConnectionMode.setVisibility(View.GONE);
		txtFrameRate.setVisibility(View.GONE);
		txtBitRate.setVisibility(View.GONE);
		txtFrameCount.setVisibility(View.GONE);
		txtIncompleteFrameCount.setVisibility(View.GONE);
		txtRecvFrmPreSec.setVisibility(View.GONE);
		txtDispFrmPreSeco.setVisibility(View.GONE);
		if (mIsListening) {
			btn_Speak.setBackgroundResource(R.drawable.voice_off_icon);

			toStartListening();

		} else {
			btn_Speak.setBackgroundResource(R.drawable.mute_on_icon);

			toStartSpeaking();
		}

	}

	// 刷新相册
	private void scanPhotos(String filePath, Context context) {
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri uri = Uri.fromFile(new File(filePath));
		intent.setData(uri);
		context.sendBroadcast(intent);
	}

	private boolean saveImage(String fileName, Bitmap frame) {

		if (fileName == null || fileName.length() <= 0)
			return false;

		boolean bErr = false;
		FileOutputStream fos = null;

		try {

			fos = new FileOutputStream(fileName, false);
			frame.compress(Bitmap.CompressFormat.JPEG, 90, fos);

			scanPhotos(fileName, NewsCloudEyesActivity.this);

			fos.flush();
			fos.close();

		} catch (Exception e) {

			bErr = true;
			;

		} finally {

			if (bErr) {

				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return false;
			}
		}
		return true;
	}

	private static boolean isSDCardValid() {

		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK:

			toQuitVideo();

			break;
		}

		return super.onKeyDown(keyCode, event);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			// Bundle bundle = msg.getData();
			int avChannel = msg.getData().getInt("avChannel");
			// modified by yajunsun 20150917
			// St_SInfo stSInfo = new St_SInfo();
			// IOTCAPIs.IOTC_Session_Check(mCamera.getMSID(), stSInfo);

			switch (msg.what) {

			case STS_CHANGE_CHANNEL_STREAMINFO:

				// if (txtResolution != null)
				// txtResolution.setText(String.valueOf(mVideoWidth) + "x"
				// + String.valueOf(mVideoHeight));
				// // if (txtConnectionMode!=null)
				// // txtConnectionMode.setText(getSessionMode(mCamera != null ?
				// // mCamera.getSessionMode() : -1) + " C: " +
				// // IOTCAPIs.IOTC_Get_Nat_Type() + ", D: " + stSInfo.NatType +
				// // ",R" + mCamera.getbResend() );
				// if (txtFrameRate != null)
				// txtFrameRate.setText(String.valueOf(mVideoFPS));
				//
				// if (txtBitRate != null)
				// txtBitRate.setText(String.valueOf(mVideoBPS) + "Kbps");
				//
				// if (txtOnlineNumber != null)
				// txtOnlineNumber.setText(String.valueOf(mOnlineNm));
				//
				// if (txtFrameCount != null)
				// txtFrameCount.setText(String.valueOf(mFrameCount));
				//
				// if (txtIncompleteFrameCount != null)
				// txtIncompleteFrameCount.setText(String
				// .valueOf(mIncompleteFrameCount));
				//
				// if (txtRecvFrmPreSec != null)
				// txtRecvFrmPreSec.setText(String.valueOf(mCamera
				// .getRecvFrmPreSec()));
				//
				// if (txtDispFrmPreSeco != null)
				// txtDispFrmPreSeco.setText(String.valueOf(mCamera
				// .getDispFrmPreSec()));
				//
				// if (txtPerformance != null)
				// txtPerformance
				// .setText(getPerformance((int) (((float) mCamera
				// .getDispFrmPreSec() / (float) mCamera
				// .getRecvFrmPreSec()) * 100)));

				break;
			case STS_SNAPSHOT_SCANED:

				// Toast.makeText(LiveViewActivity.this,
				// getText(R.string.tips_snapshot_ok),
				// Toast.LENGTH_SHORT).show();

				break;

			case Camera.CONNECTION_STATE_CONNECTING:

				if (!mCamera.isSessionConnected()
						|| !mCamera.isChannelConnected(mSelectedChannel)) {
					toSetMsgText("正在连接视频，请稍后...");
				}

				break;

			case Camera.CONNECTION_STATE_CONNECTED:

				if (mCamera.isSessionConnected()
						&& avChannel == mSelectedChannel
						&& mCamera.isChannelConnected(mSelectedChannel)) {
					toSetMsgText("视频连接成功");
					toCloseMsg();
				}

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

				MessageUtil.alertMessage(mContext, R.string.connstus_timeout);

				// 当连接成功后又出现连接超时的时候直接关闭 ACTIVITY，否者将会循环重新连接导致无法操作
				MianActivity.getScreenManager().exitActivity(mActivity);
				/*
				 * if (mCamera != null) {
				 * 
				 * toShowMsg(); toSetMsgText("正在连接视频，请稍后...");
				 * 
				 * toStartVideo(); toSetSpeaking(); }
				 */

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

			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_RESP:

				// LiveViewActivity.this.invalidateOptionsMenu();

				break;
			}

			super.handleMessage(msg);
		}
	};

	@Override
	public void receiveChannelInfo(final Camera camera, int avChannel,
			int resultCode) {
		// TODO Auto-generated method stub
		if (mCamera == camera && avChannel == mSelectedChannel) {
			Bundle bundle = new Bundle();
			bundle.putInt("avChannel", avChannel);

			Message msg = handler.obtainMessage();
			msg.what = resultCode;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

	@Override
	public void receiveFrameData(final Camera camera, int avChannel, Bitmap bmp) {
		// TODO Auto-generated method stub

		if (mCamera == camera && avChannel == mSelectedChannel) {
			if (bmp.getWidth() != mVideoWidth
					|| bmp.getHeight() != mVideoHeight) {
				mVideoWidth = bmp.getWidth();
				mVideoHeight = bmp.getHeight();
				handler.sendEmptyMessage(Camera.CONNECTION_STATE_CONNECTED);
			}
		}
	}

	@Override
	public void receiveFrameInfo(final Camera camera, int avChannel,
			long bitRate, int frameRate, int onlineNm, int frameCount,
			int incompleteFrameCount) {
		// TODO Auto-generated method stub
		if (mCamera == camera && avChannel == mSelectedChannel) {
			mVideoFPS = frameRate;
			mVideoBPS = bitRate;
			mOnlineNm = onlineNm;
			mFrameCount = frameCount;
			mIncompleteFrameCount = incompleteFrameCount;
			/*
			 * Bundle bundle = new Bundle(); bundle.putInt("avChannel",
			 * avChannel);
			 */
			Message msg = handler.obtainMessage();
			msg.what = STS_CHANGE_CHANNEL_STREAMINFO;
			// msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

	@Override
	public void receiveIOCtrlData(final Camera camera, int avChannel,
			int avIOCtrlMsgType, byte[] data) {
		// TODO Auto-generated method stub
		if (mCamera == camera) {
			Bundle bundle = new Bundle();
			bundle.putInt("avChannel", avChannel);
			bundle.putByteArray("data", data);
			Message msg = handler.obtainMessage();
			msg.what = avIOCtrlMsgType;
			handler.sendMessage(msg);
		}
	}

	@Override
	public void receiveSessionInfo(final Camera camera, int resultCode) {
		// TODO Auto-generated method stub
		if (mCamera == camera) {
			// Bundle bundle = new Bundle();
			Message msg = handler.obtainMessage();
			msg.what = resultCode;
			// msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

	@Override
	public View makeView() {
		// TODO Auto-generated method stub
		return null;
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

	private String getPerformance(int mode) {

		String result = "";
		if (mode < 30)
			result = "差";
		else if (mode < 60)
			result = "一般";
		else
			result = "佳";

		return result;
	}

	private String getSessionMode(int mode) {

		String result = "";
		if (mode == 0)
			result = getText(R.string.connmode_p2p).toString();
		else if (mode == 1)
			result = getText(R.string.connmode_relay).toString();
		else if (mode == 2)
			result = getText(R.string.connmode_lan).toString();
		else
			result = getText(R.string.connmode_none).toString();

		return result;
	}

}
