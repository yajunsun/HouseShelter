package com.hiibox.houseshelter.activity;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.annotation.view.ViewInject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.hiibox.houseshelter.MyApplication;
import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.adapter.MembersPagerAdapter;
import com.hiibox.houseshelter.core.GlobalUtil;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.net.MembersInfoResult;
import com.hiibox.houseshelter.net.SpliteUtil;
import com.hiibox.houseshelter.util.BackToExitUtil;
import com.hiibox.houseshelter.util.DateUtil;
import com.hiibox.houseshelter.util.LocationUtil;
import com.hiibox.houseshelter.util.MessageUtil;
import com.hiibox.houseshelter.util.PreferenceUtil;
import com.hiibox.houseshelter.util.ScreenUtil;
import com.tutk.IOTC.AVAPIs;
import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.zgan.jtws.ZganJTWSService;
import com.zgan.jtws.ZganJTWSServiceTools;
import com.zgan.login.ZganLoginService;
import com.zgan.push.ZganPushService;
import com.zgan.tutk.MyCamera;
import com.zgan.youbao.R;

/**
 * 
 * @author Administrator
 * 
 */
@SuppressLint("HandlerLeak")
public class HomepageActivity extends ShaerlocActivity implements
		OnSeekBarChangeListener, OnPageChangeListener {

	@ViewInject(id = R.id.root_layout)
	RelativeLayout rootLayout;
	@ViewInject(id = R.id.top_info_layout)
	RelativeLayout topInfoLayout;

	@ViewInject(id = R.id.temperature_tv)
	TextView temperatureTV;
	@ViewInject(id = R.id.temperature_description_tv)
	TextView tempDesTV;
	@ViewInject(id = R.id.humidity_tv)
	TextView humidityTV;
	@ViewInject(id = R.id.humidity_description_tv)
	TextView humidityDesTV;
	@ViewInject(id = R.id.family_members_layout)
	ViewPager familyMembersLayout;
	@ViewInject(id = R.id.shield_iv)
	ImageView shieldIV;
	@ViewInject(id = R.id.eye_iv1)
	ImageView eye1IV;
	@ViewInject(id = R.id.eye_iv2)
	ImageView eye2IV;
	@ViewInject(id = R.id.eye_iv3)
	ImageView eye3IV;
	@ViewInject(id = R.id.cloud_eyes_iv, click = "onClick")
	ImageView cloudEyesIV;
	@ViewInject(id = R.id.imprinting_iv, click = "onClick")
	ImageView imprintingIV;
	@ViewInject(id = R.id.cloud_photo_album_layout, click = "onClick")
	RelativeLayout cloudPhotoAlbumLayout;
	@ViewInject(id = R.id.smart_app_iv, click = "onClick")
	ImageView smartAppIV;
	@ViewInject(id = R.id.self_center_iv, click = "onClick")
	ImageView selfCenterIV;
	@ViewInject(id = R.id.yjyd_iv, click = "onClick")
	ImageView yjydIV;
	@ViewInject(id = R.id.wallet_iv, click = "onClick")
	ImageView walletIV;

	/**
	 * 布防撤防按钮
	 */
	@ViewInject(id = R.id.defence_seek_bar)
	SeekBar seekBar;
	@ViewInject(id = R.id.new_pictures_tv)
	TextView newPicNumbersTv;
	@ViewInject(id = R.id.refresh_imagebtn, click = "onClick")
	ImageView refresh_btn;

	/**
	 * 绑定的当前设备
	 */
	@ViewInject(id = R.id.deviceName)
	TextView deviceNameTextView;

	private BackToExitUtil exitPrompt = null;
	private Drawable leftDrawable = null;
	private Drawable rightDrawable = null;
	private Drawable leftLightDrawable = null;
	private Drawable rightLightDrawable = null;
	private Drawable leftGrayDrawable = null;
	private Drawable rightGrayDrawable = null;
	public String WD;// 温度
	public String SD;// 湿度

	private boolean defenceClikable = false;
	public static List<MembersInfoResult> membersList = null;
	private List<List<MembersInfoResult>> membersPagerList = null;
	private MembersPagerAdapter pagerAdapter = null;
	protected boolean isInitPager = true;
	private Animation anim1, anim2, anim3;
	private Handler weatherhandler = new Handler();
	public int progress_1;
	private int FlAG = -1;
	private String MYACTION = "com.zgan.youbao.broadcast";
	private IntentFilter filter;
	ProgressDialog dialog;

	public static MyCamera myCamera;

	private LayoutParams params = new LayoutParams();

	// 设置window type

	private WindowManager mWindowManager = null;
	private View intrudeView = null;
	public static boolean intrudeViewShowing = false;
	public int number = 0;
	public int refreshnumber = 0;
	private TimeCount timeCount;
	private TimeCount refreshTimeCount;
	AlertDialog.Builder builder = null;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			if (msg.what == 1) {

				Frame frame = (Frame) msg.obj;

				String ret = frame.strData;

				switch (frame.subCmd) {
				// 服务器反馈布防请求是否成功
				case 1:
					if (ret.equals("0")) {
						seekBar.setClickable(false);
						seekBar.setProgress(100);
						seekBar.setThumb(leftGrayDrawable);
					} else {
						seekBar.setClickable(true);
						seekBar.setProgress(0);
						seekBar.setThumb(rightDrawable);
					}
					break;
				// 服务器反馈撤防请求是否成功
				case 2:
					if (ret.equals("0")) {
						seekBar.setClickable(false);
						seekBar.setProgress(0);
						seekBar.setThumb(rightGrayDrawable);
					} else {
						seekBar.setClickable(true);
						seekBar.setProgress(100);
						seekBar.setThumb(leftDrawable);
					}
					break;
				// rfid卡列表
				case 4:
					if (frame.mainCmd == 14) {

						RefreshRfid(frame.strData);
					}

					break;
				case 72:
					// 判断设备是否在线
					if (!TextUtils.isEmpty(MyApplication.statues)
							&& MyApplication.statues.equals("3")) {
						seekBar.setClickable(true);
						defenceClikable = true;
						if (SpliteUtil.getRuquestStatus(ret)) {
							if (SpliteUtil.getResult(ret).equals("1")) {
								seekBar.setProgress(0);
								seekBar.setThumb(rightDrawable);
							} else {
								seekBar.setProgress(100);
								seekBar.setThumb(leftDrawable);
							}
						} else {
							seekBar.setProgress(0);
							seekBar.setThumb(rightDrawable);
						}
					} else {
						seekBar.setClickable(false);

						seekBar.setProgress(0);
						seekBar.setThumb(rightGrayDrawable);

					}
					break;
				case 74:
					if (SpliteUtil.getRuquestStatus(ret)) {
						String[] datas = ret.split("\t");
						if (datas != null && datas.length > 1) {
							temperatureTV.setText(datas[1]);
							ZganJTWSService.toGetServerData(75,
									ZganLoginService.getUserName(), handler);
						} else {
							getWeather(); // 获取天气信息
						}
					} else { // 若果未获取设备上报信息，则从天气网获取
						getWeather(); // 获取天气信息
					}
					break;
				case 75:
					if (SpliteUtil.getRuquestStatus(ret)) {
						String[] datas = ret.split("\t");
						if (datas != null && datas.length > 1) {
							humidityTV.setText(datas[1]);
						}
					}
					break;
				case 80:
					if (SpliteUtil.getRuquestStatus(ret)) {
						MyApplication.deviceCode = (SpliteUtil.getResult(ret))
								.replace(" ", "");
						MyApplication.statues = SpliteUtil.getStatues(ret);
						MyApplication.deviceIP = SpliteUtil.getDeviceIP(ret);

						sendRequest(82); // 获取用户设备列表
						// 判断设备是否在线
						if (!TextUtils.isEmpty(MyApplication.statues)
								&& MyApplication.statues.equals("3")) {
							// 设备在线
							cloudEyesIV
									/*
									 * .setImageBitmap(getScaledBitmap(
									 * R.drawable.cloud_eyes_silver,
									 * cloudEyesIV.getWidth(),
									 * cloudEyesIV.getHeight()));
									 */
									.setBackgroundResource(R.drawable.cloud_eyes_silver);
							new Thread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									if (myCamera == null) {
										MyCamera.init();
										AVAPIs.avClientSetMaxBufSize(2000);
										myCamera = new MyCamera("admin["
												+ MyApplication.deviceCode
												+ "]",
												MyApplication.deviceCode,
												"admin", "admin");
										myCamera.connect(MyApplication.deviceCode);
										myCamera.start(
												MyCamera.DEFAULT_AV_CHANNEL,
												"admin", "admin");
										myCamera.sendIOCtrl(
												MyCamera.DEFAULT_AV_CHANNEL,
												AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
												AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq
														.parseContent());
										myCamera.sendIOCtrl(
												MyCamera.DEFAULT_AV_CHANNEL,
												AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ,
												AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq
														.parseContent());
										myCamera.sendIOCtrl(
												MyCamera.DEFAULT_AV_CHANNEL,
												AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ,
												AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq
														.parseContent());
										myCamera.sendIOCtrl(
												Camera.DEFAULT_AV_CHANNEL,
												AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ,
												AVIOCTRLDEFs.SMsgAVIoctrlTimeZone
														.parseContent());
										myCamera.LastAudioMode = 1;
									}
								}
							}).start();
						} else {
							// 设备不在线
							seekBar.setClickable(false);

							seekBar.setProgress(0);
							seekBar.setThumb(rightGrayDrawable);

							cloudEyesIV/*
										 * .setImageBitmap(getScaledBitmap(
										 * R.drawable.cloud_eyes_h,
										 * cloudEyesIV.getWidth(),
										 * cloudEyesIV.getHeight()));
										 */
							.setBackgroundResource(R.drawable.cloud_eyes_h);

							if (FlAG == -1) {
								FlAG = 0;
								Toast.makeText(getApplicationContext(),
										R.string.online_text,
										Toast.LENGTH_SHORT).show();
							}
						}

					}
					break;
				case 82:
					if (SpliteUtil.getRuquestStatus(ret)) {
						String[] data = ret.split("\t");
						if (data.length > 0) {
							if (data[0].equals("0")) {
								int deviceCount = Integer.parseInt(data[1]);
								if (deviceCount > 0) {
									for (int i = 0; i < deviceCount; i++) {
										String[] devices = data[i + 2]
												.split("\n");
										if (devices != null
												&& devices.length >= 4
												&& devices[0]
														.equals(MyApplication.deviceCode)) {
											deviceNameTextView
													.setText(devices[1]);
											break;
										}
									}
								}
							}
						}
					}
					break;
				case 86:
					if (frame.strData != null && !frame.strData.equals("")) {
						String data[] = ret.split("\t");
						if (data[0].equals("0")) {
							final int newversion = Integer.parseInt(data[1]
									.replace(".", ""));
							String oldverString = getString(R.string.app_var)
									.replaceAll("\\D+", "")
									.replaceAll("\r", "").replaceAll("\n", "")
									.replace("v", "").replace(".", "");

							int oldversion = Integer.parseInt(oldverString
									.trim());
							final String url = data[2];

							if (newversion > oldversion) {
								builder.setMessage(getString(R.string.updat_newvar));
								builder.setPositiveButton(
										"确定",
										new android.content.DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub
												Intent intent = new Intent(
														HomepageActivity.this,
														VersionUpdate.class);
												intent.putExtra("URL", url);
												intent.putExtra("Version",
														newversion);
												startActivity(intent);
											}
										});
								builder.setNegativeButton(
										"取消",
										new android.content.DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub
											}
										});
								builder.create();
								builder.show();

							} else {
								/*
								 * MessageUtil.alertMessage(mContext,
								 * R.string.var_new);
								 */

							}

						} else {
							/*
							 * MessageUtil.alertMessage(mContext,
							 * R.string.var_new_fail);
							 */

						}
					}
					break;
				}
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (ZganJTWSService.ServiceRin) {
			FlAG = -1;
			initViews();

			initAnimations();

			membersList = new ArrayList<MembersInfoResult>();

			// 启动消息服务
			if (!ZganPushService.ServiceRin) {
				Intent intent = new Intent(mContext, ZganPushService.class);
				startService(intent);

				// 定义一个接收器
				filter = new IntentFilter();
				filter.addAction(MYACTION);
				HomepageActivity.this.registerReceiver(receiver, filter);

				ZganJTWSService.toGetServerData(86,
						new String[] { ZganLoginService.getUserName()
								+ "\t4\t0" }, handler, 2);

			}

			seekBar.setClickable(false);
			seekBar.setProgress(0);
			seekBar.setThumb(rightGrayDrawable);

			initPageData();
		} else {
			finish();
		}
		timeCount = new TimeCount(10000, 1000);// 构造CountDownTimer对象\
	}

	/**
	 * 刷新rfid卡
	 * 
	 * @param strData
	 */
	class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			number = 0;
			seekBar.setEnabled(true);
			refreshnumber = 0;
			refresh_btn.setImageDrawable(getResources().getDrawable(
					R.drawable.refresh_true));
			// refresh_btn.setImageBitmap(getScaledBitmap(R.drawable.refresh_true,
			// refresh_btn.getWidth(), refresh_btn.getHeight()));
			refresh_btn.setEnabled(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示

		}

	}

	protected void RefreshRfid(String strData) {
		// TODO Auto-generated method stub
		if (null != membersPagerList) {

			membersPagerList.clear();
			if (null != pagerAdapter) {
				pagerAdapter.notifyDataSetChanged();
			}

		} else {
			membersPagerList = new ArrayList<List<MembersInfoResult>>();
		}

		if (!TextUtils.isEmpty(strData)) {
			String[] aryRfidData = strData.split("\t");
			int intBegin = 2;
			int intDataLen = 4;
			int total = 0;
			int index = 0;

			if (aryRfidData.length >= 6) {

				if (membersList == null) {
					membersList = new ArrayList<MembersInfoResult>();
				} else {
					membersList.clear();
				}

				total = Integer.parseInt(aryRfidData[0]);
				index = Integer.parseInt(aryRfidData[1]);

				while (intBegin < aryRfidData.length) {
					MembersInfoResult result = new MembersInfoResult();

					result.cardNum = aryRfidData[0 + intBegin];
					result.nickname = aryRfidData[1 + intBegin];

					if (!TextUtils.isEmpty(MyApplication.statues)
							&& MyApplication.statues.equals("3")) {
						result.status = Integer
								.parseInt(aryRfidData[2 + intBegin]);
					}

					membersList.add(result);

					intBegin = intBegin + intDataLen;
				}
				setMembersInfo();
			}
		}
	}

	public void setMembersInfo() {

		if (null != membersList && membersList.size() > 0) {
			int size = membersList.size();
			int pages = (int) Math.ceil(size / 5d);
			pages = (pages == 0) ? 1 : pages;
			int start = 0;
			int end = start + 5;
			if (pages == 1) {
				end = size;
			}

			for (int m = 0; m < pages;) {
				List<MembersInfoResult> l = new ArrayList<MembersInfoResult>();
				for (; start < end; start++) {
					l.add(membersList.get(start));

				}
				membersPagerList.add(l);
				m++;
				if (m == pages - 1) {
					start = end;
					end = size;
				} else {
					start = end;
					end = start + 5;
				}

			}
			if (null != membersPagerList && membersPagerList.size() > 0) {
				pagerAdapter.setList(membersPagerList);

				if (isInitPager) {

					familyMembersLayout.setAdapter(pagerAdapter);
					isInitPager = false;
				}

			}
		}
	}

	/**
	 * 获取天气情况
	 */
	private void getWeather() {
		// TODO Auto-generated method stub
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				// HttpGet get = new HttpGet("http://61.4.185.48:81/g/");
				if (MyApplication.deviceIP != null
						&& !MyApplication.deviceIP.equals("")) {
					HttpGet get = new HttpGet(
							"http://ip.taobao.com/service/getIpInfo.php?ip="
									+ MyApplication.deviceIP);
					HttpClient client = new DefaultHttpClient();
					HttpResponse httpResponse;
					try {
						httpResponse = client.execute(get);
						HttpEntity entity = httpResponse.getEntity();
						StatusLine line = httpResponse.getStatusLine();
						if (line.getStatusCode() == 200) {
							String dataString = EntityUtils.toString(entity);
							JSONObject dataJson = new JSONObject(dataString);
							JSONObject infoJson = new JSONObject(
									dataJson.getString("data"));
							String cityName = infoJson.getString("city")
									.replace("市", "").replace("区", "")
									.replace("县", "").replace("省", "");
							int startIndex = MyApplication.citysString
									.indexOf(cityName);
							String cityNumber = null;
							if (startIndex >= 0) {
								cityNumber = MyApplication.citysString
										.substring(
												startIndex + cityName.length()
														+ 1, startIndex
														+ cityName.length()
														+ 10);
							}
							if (cityNumber != null) {
								String path = "http://www.weather.com.cn/data/sk/"
										+ cityNumber + ".html";
								get = new HttpGet(path);
								client = new DefaultHttpClient();
								httpResponse = client.execute(get);
								entity = httpResponse.getEntity();
								line = httpResponse.getStatusLine();
								if (line.getStatusCode() == 200) {
									String weatherString = EntityUtils
											.toString(entity);
									JSONObject jsonObject = new JSONObject(
											weatherString);
									String data = jsonObject
											.getString("weatherinfo");
									JSONObject obj = new JSONObject(data);
									WD = obj.getString("temp");
									SD = obj.getString("SD");
									weatherhandler.post(r);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				super.run();
			}
		}.start();
	}

	Runnable r = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String[] wd = WD.split("/");

			int temperatureInt = Integer.parseInt((wd[0].replace("℃", "")));
			temperatureTV.setText(wd[0].replace("℃", ""));
			if (temperatureInt > 35) {
				tempDesTV.setText("热");
			} else if (temperatureInt > 26 && temperatureInt <= 35) {
				tempDesTV.setText("暖");
			} else if (temperatureInt > 17 && temperatureInt <= 26) {
				tempDesTV.setText("舒适");
			} else if (temperatureInt >= 10 && temperatureInt <= 17) {
				tempDesTV.setText("凉");
			} else {
				tempDesTV.setText("冷");
			}

			int humidityInt = Integer.parseInt(SD.replace("%", ""));
			humidityTV.setText(SD.replace("%", ""));

			if (humidityInt < 40) {
				humidityDesTV.setText("干燥");
			} else if (humidityInt < 60) {
				humidityDesTV.setText("舒适");
			} else {
				humidityDesTV.setText("潮湿");
			}
		}
	};

	private void initViews() {
		PreferenceUtil.getInstance(mContext).saveBoolean("exitApp", false);

		setContentView(R.layout.activity_homepage_layout);
		dialog = new ProgressDialog(HomepageActivity.this);
		familyMembersLayout.setOnPageChangeListener(this);
		exitPrompt = new BackToExitUtil();
		Resources res = getResources();
		membersPagerList = new ArrayList<List<MembersInfoResult>>();
		pagerAdapter = new MembersPagerAdapter(mContext, mActivity);
		builder = new AlertDialog.Builder(mActivity);

		seekBar.setOnSeekBarChangeListener(this);
		cloudEyesIV.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {

				eye1IV.setVisibility(View.VISIBLE);
				eye2IV.setVisibility(View.VISIBLE);
				eye3IV.setVisibility(View.VISIBLE);
				eye1IV.startAnimation(anim1);
				eye2IV.startAnimation(anim2);
				eye3IV.startAnimation(anim3);
				return false;
			}
		});

		setParams();

		leftDrawable = res.getDrawable(R.drawable.thumb_ttpod_left);
		rightDrawable = res.getDrawable(R.drawable.thumb_ttpod_right);
		leftLightDrawable = res.getDrawable(R.drawable.thumb_ttpod_light_left);
		rightLightDrawable = res
				.getDrawable(R.drawable.thumb_ttpod_light_right);
		leftGrayDrawable = res.getDrawable(R.drawable.thumb_ttpod_gray_left);
		rightGrayDrawable = res.getDrawable(R.drawable.thumb_ttpod_gray_right);

		mWindowManager = (WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
	}

	private void initAnimations() {
		anim1 = AnimationUtils.loadAnimation(this, R.anim.cloud_eyes_animation);
		anim1.setDuration(2000);
		anim1.setRepeatCount(3);
		anim1.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				eye1IV.startAnimation(anim1);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				eye1IV.clearAnimation();
				eye1IV.setVisibility(View.INVISIBLE);
			}
		});
		anim2 = AnimationUtils.loadAnimation(this, R.anim.cloud_eyes_animation);
		anim2.setDuration(2000);
		anim2.setRepeatCount(3);
		anim2.setStartOffset(500);
		anim2.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				eye2IV.startAnimation(anim2);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				eye2IV.clearAnimation();
				eye2IV.setVisibility(View.INVISIBLE);
			}
		});
		anim3 = AnimationUtils.loadAnimation(this, R.anim.cloud_eyes_animation);
		anim3.setDuration(2000);
		anim3.setRepeatCount(3);
		anim3.setStartOffset(1000);
		anim3.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				eye3IV.startAnimation(anim3);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				eye3IV.clearAnimation();
				eye3IV.setVisibility(View.INVISIBLE);
			}
		});
	}

	// 初始化首页数据
	private void initPageData() {

		if (!ZganLoginService.isNetworkAvailable(mContext)) {
			MessageUtil.alertMessage(mContext, R.string.sys_network_error);
			return;
		}

		// 得到当前用户绑定设备号
		ZganJTWSService.getDeviceCode(handler);

		ZganJTWSService.toGetServerData(72, ZganLoginService.getUserName(),
				handler);

		ZganJTWSService.toGetServerData(74, ZganLoginService.getUserName(),
				handler); // 获取温度数据

		ZganJTWSService.toGetServerData(4,
				new String[] { ZganLoginService.getUserName() }, handler, 2);

	}

	private void sendRequest(int subCmd) {
		if (!ZganJTWSServiceTools.isConnect) {
			MessageUtil.alertMessage(mContext, R.string.sys_network_error);
			return;
		}
		if (number > 5) {
			MessageUtil.alertMessage(mContext, R.string.sys_network_error2);
			seekBar.setEnabled(false);
			return;
		}
		switch (subCmd) {
		case 1:
			// 开启布防
			if (number == 0 && timeCount != null) {
				if (timeCount == null) {
					timeCount = new TimeCount(10000, 1000);// 构造CountDownTimer对象\
				} else {
					timeCount.start();
				}
			}
			number++;
			ZganJTWSService.toGetServerData(
					1,
					new String[] { ZganLoginService.getUserName(),
							DateUtil.getcurrentDay() }, handler);
			break;

		case 3:
			// 开启撤防
			if (number == 0 && timeCount != null) {
				if (timeCount == null) {
					timeCount = new TimeCount(10000, 1000);// 构造CountDownTimer对象\
				} else {
					timeCount.start();

				}

			}
			number++;
			ZganJTWSService.toGetServerData(
					2,
					new String[] { ZganLoginService.getUserName(),
							DateUtil.getcurrentDay() }, handler);

			break;
		case 4:
			if (null == membersList) {
				membersList = new ArrayList<MembersInfoResult>();
			}
			if (null == membersPagerList) {
				membersPagerList = new ArrayList<List<MembersInfoResult>>();
			}
			membersList.clear();
			membersPagerList.clear();
			if (null != pagerAdapter) {
				pagerAdapter.setList(membersPagerList);

			}
			ZganJTWSService
					.toGetServerData(4,
							new String[] { ZganLoginService.getUserName() },
							handler, 2);
			break;
		case 72:

			ZganJTWSService.toGetServerData(72, ZganLoginService.getUserName(),
					handler);

			break;
		case 80:
			ZganJTWSService.getDeviceCode(handler);

			break;

		// 获取用户绑定设备
		case 82:
			ZganJTWSService.toGetServerData(82, MyApplication.phone, handler);
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		FlAG = -1;

		initPageData();
		// seekBar.setEnabled(true);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(null);
		sHandler.removeCallbacks(null);
		if (null != membersList) {
			membersList.clear();
			membersList = null;
		}
		if (null != membersPagerList) {
			membersPagerList.clear();
			membersPagerList = null;
		}
		if (myCamera != null) {
			myCamera.disconnect();
		}
		MyCamera.uninit();
		MyApplication.showedAds = false;

	}

	/**
	 * 当屏幕尺寸大于854或者小于800的时候，重新设置相关控件的参数
	 */
	private void setParams() {
		int screenHeight = ScreenUtil.getScreenHeight(mActivity);

		android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) familyMembersLayout
				.getLayoutParams();
		android.widget.FrameLayout.LayoutParams eyesParams = (android.widget.FrameLayout.LayoutParams) cloudEyesIV
				.getLayoutParams();
		android.widget.FrameLayout.LayoutParams eye1Params = (android.widget.FrameLayout.LayoutParams) eye1IV
				.getLayoutParams();
		android.widget.FrameLayout.LayoutParams eye2Params = (android.widget.FrameLayout.LayoutParams) eye2IV
				.getLayoutParams();
		android.widget.FrameLayout.LayoutParams eye3Params = (android.widget.FrameLayout.LayoutParams) eye3IV
				.getLayoutParams();
		if (screenHeight > 0 && screenHeight <= 800) {
			params.bottomMargin = 20;
			familyMembersLayout.setLayoutParams(params);
		} else if (screenHeight > 854 && screenHeight <= 1280) {

			params.bottomMargin = 70;
			familyMembersLayout.setLayoutParams(params);

			eyesParams.width = 350;
			eyesParams.height = 150;
			eyesParams.topMargin = 100;
			cloudEyesIV.setLayoutParams(eyesParams);

			eye1Params.width = 350;
			eye1Params.height = 150;
			eye1Params.topMargin = 100;
			eye1IV.setLayoutParams(eye1Params);

			eye2Params.width = 350;
			eye2Params.height = 150;
			eye2Params.topMargin = 100;
			eye2IV.setLayoutParams(eye2Params);

			eye3Params.width = 350;
			eye3Params.height = 150;
			eye3Params.topMargin = 100;
			eye3IV.setLayoutParams(eye3Params);
		}
	}

	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.cloud_eyes_iv:

			if (!MyApplication.statues.equals("3")) {
				Toast.makeText(getApplicationContext(), R.string.online_text,
						Toast.LENGTH_SHORT).show();

				return;
			}

			intent.setClass(this, NewsCloudEyesActivity.class);

			break;
		case R.id.imprinting_iv:
			intent.setClass(this, ImprintingActivity.class);
			break;
		case R.id.cloud_photo_album_layout:

			if (newPicNumbersTv.getVisibility() == 0) {
				newPicNumbersTv.setVisibility(View.GONE);
			}
			intent.setClass(this, CloudPhotoAlbumActivity.class);
			break;
		case R.id.smart_app_iv:
			intent.setClass(this, SmartAppActivity.class);
			break;
		case R.id.self_center_iv:
			intent.setClass(this, SelfCenterActivity.class);
			break;
		case R.id.temperature_tv:
			intent.setClass(this, TemperatureDialogActivity.class);

			break;
		case R.id.refresh_imagebtn:
			if (refreshnumber > 1) {
				Toast.makeText(mContext, "不要频繁刷新页面...", Toast.LENGTH_SHORT)
						.show();
				refresh_btn.setImageDrawable(getResources().getDrawable(
						R.drawable.refresh_false));
				// refresh_btn.setImageBitmap(getScaledBitmap(R.drawable.refresh_false,refresh_btn.getWidth(),refresh_btn.getHeight()));
				refresh_btn.setEnabled(false);
				return;
			}
			refreshTimeCount = new TimeCount(5000, 1000);// 构造CountDownTimer对象\
			refreshTimeCount.start();
			dialog.setMessage("正在刷新数据...");
			dialog.show();
			initPageData();
			refreshnumber++;

			break;
		case R.id.yjyd_iv:
			intent.setClass(HomepageActivity.this, MainyjydActivity.class);
			break;
		case R.id.wallet_iv:
			intent.setClass(HomepageActivity.this, MainWalletActivity.class);
			break;
		default:
			break;
		}
		if (null != intent.getClass()) {
			startActivity(intent);
			timeCount.cancel();
			seekBar.setEnabled(true);
			number = 0;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 0x901 && resultCode == RESULT_OK) {
			pagerAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			pressAgainExit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressLint("ShowToast")
	private void pressAgainExit() {
		if (exitPrompt.isExit()) {
			MyApplication.isFirstTimeEntry = true;
			PreferenceUtil.getInstance(mContext).saveBoolean("exitApp", true);
			PreferenceUtil.getInstance(mContext)
					.saveBoolean("showedAds", false);

			HomepageActivity.this.unregisterReceiver(receiver);

			MianActivity.getScreenManager().exit();
			// MianActivity.getScreenManager().exitAllActivityExceptOne();

			System.exit(0);
		} else {
			Toast.makeText(this, getString(R.string.back_to_exit_app),
					Toast.LENGTH_SHORT).show();
			exitPrompt.doExitInOneSecond();
		}
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

		if (LocationUtil.checkNetWork(mContext).endsWith(
				GlobalUtil.NETWORK_NONE)) {
			MessageUtil.alertMessage(mContext, R.string.sys_network_error);

			return;
		}
		if (!MyApplication.statues.equals("3")
				&& !MyApplication.statues.equals("")
				&& MyApplication.statues != null) {
			if (seekBar.getProgress() > 50) {
				seekBar.setThumb(leftGrayDrawable);
			} else {
				seekBar.setThumb(rightGrayDrawable);
			}
			return;
		}

		if (!defenceClikable) {
			return;
		}

		progress_1 = seekBar.getProgress();

		if (seekBar.getProgress() > 50) {
			seekBar.setThumb(leftLightDrawable);
		} else {
			seekBar.setThumb(rightLightDrawable);
		}
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		int progress = seekBar.getProgress();

		seekBar.setClickable(false);
		defenceClikable = false;

		if (!TextUtils.isEmpty(MyApplication.statues)
				&& MyApplication.statues.equals("3")) {
			// 在线

			if (progress > 50) {
				seekBar.setProgress(100);
				seekBar.setThumb(leftGrayDrawable);

				if (Math.abs(progress - progress_1) > 50) {
					// 布防
					sendRequest(1);
				}

			} else {
				seekBar.setProgress(0);
				seekBar.setThumb(rightGrayDrawable);

				if (Math.abs(progress - progress_1) > 50) {
					// 撤防
					sendRequest(3);
				}
			}

		} else {
			// 不在线
			sendRequest(80);

			seekBar.setProgress(0);
			seekBar.setThumb(rightGrayDrawable);

		}

	}

	@SuppressLint("HandlerLeak")
	public Handler sHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int what = msg.what;

			switch (what) {
			case 1: // 开启布防
				if (null == seekBar) {
					return;
				}

				if (!TextUtils.isEmpty(MyApplication.statues)
						&& MyApplication.statues.equals("3")) {
					// 设备在线

				} else {
					MyApplication.statues = "3";
					cloudEyesIV// .setImageBitmap(getScaledBitmap(R.drawable.cloud_eyes_silver,
								// cloudEyesIV.getWidth(),
								// cloudEyesIV.getHeight()));
							.setBackgroundResource(R.drawable.cloud_eyes_silver);
					ZganJTWSService.toGetServerData(4,
							new String[] { ZganLoginService.getUserName() },
							handler, 2);
				}

				defenceClikable = true;
				seekBar.setClickable(true);
				seekBar.setProgress(100);
				seekBar.setThumb(leftDrawable);
				break;
			case 2: // 撤销布防

				if (null == seekBar) {
					return;
				}

				if (!TextUtils.isEmpty(MyApplication.statues)
						&& MyApplication.statues.equals("3")) {
					// 设备在线

				} else {
					MyApplication.statues = "3";
					cloudEyesIV// .setImageBitmap(getScaledBitmap(R.drawable.cloud_eyes_silver,
								// cloudEyesIV.getWidth(),
								// cloudEyesIV.getHeight()));
							.setBackgroundResource(R.drawable.cloud_eyes_silver);
					ZganJTWSService.toGetServerData(4,
							new String[] { ZganLoginService.getUserName() },
							handler, 2);
				}

				defenceClikable = true;
				seekBar.setClickable(true);
				seekBar.setProgress(0);
				seekBar.setThumb(rightDrawable);
				break;
			case 3: // 入侵
				if (!intrudeViewShowing
						&& !NewsCloudEyesActivity.CloudEyesActivityIsRun) {
					intrudeViewShowing = true;
					String strData = msg.obj.toString();
					String strTime = getTime(strData, 2);
					addIntrudeView(strTime);

					if (!TextUtils.isEmpty(MyApplication.statues)
							&& MyApplication.statues.equals("3")) {
						// 设备在线

					} else {
						MyApplication.statues = "3";
						initPageData();
					}

				}
				// alarmPageStyle();
				break;
			case 23: // 离家
				/*
				 * if (null == membersList) { return; } if (null ==
				 * membersPagerList) { return; }
				 */
				sendRequest(4);
				break;
			}
		}
	};

	/**
	 * 获取推送时间
	 * 
	 * @param recieveData
	 *            推送信息
	 * @return
	 */
	private String getTime(String recieveData, int index) {
		if (TextUtils.isEmpty(recieveData)) {
			return getApplicationContext().getResources().getString(
					R.string.just_now);
		}
		String[] str = recieveData.split("\t");
		String[] time = str[index].split(" ");
		return time[1];

	}

	private void addIntrudeView(String strTime) {
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

		// 设置悬浮窗的长宽和显示位置
		params.width = LayoutParams.WRAP_CONTENT;
		params.height = LayoutParams.WRAP_CONTENT;
		params.gravity = Gravity.CENTER;

		intrudeView = View.inflate(getApplicationContext(),
				R.layout.alarm_dialog_layout, null);
		TextView timeTV = (TextView) intrudeView
				.findViewById(R.id.alarm_time_tv);
		timeTV.setText(getString(R.string.alarm_time) + " " + strTime);
		intrudeView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(),
						ImprintingActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("queryIndex", 2);
				startActivity(intent);
				mWindowManager.removeView(intrudeView);
				intrudeViewShowing = false;
			}
		});

		mWindowManager.addView(intrudeView, params);

	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (MYACTION.equals(intent.getAction())) {

				int intTypeId = 0;

				intTypeId = intent.getIntExtra("ZganMsgTypeID", 0);
				Message msg = new Message();
				msg.what = intTypeId;
				msg.obj = intent.getStringExtra("ZganMsgData");
				sHandler.sendMessage(msg);
			}
		}

	};

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
	}

	/****** 私有方法 *******/
	/*
	 * private Bitmap getScaledBitmap(int drawid, float w, float h) {
	 * BitmapFactory.Options opts = new Options(); opts.inJustDecodeBounds =
	 * true; Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
	 * drawid, opts); // 取最大的比例，保证整个图片的长或者宽必定在该屏幕中可以显示得下 float bpw =
	 * opts.outWidth; float bph = opts.outHeight; int scale =
	 * Math.round(Math.max(bpw / w, bph / h));
	 * 
	 * if (scale > 1) { opts.inJustDecodeBounds = false; // 缩放的比例
	 * opts.inSampleSize = scale; // 内存不足时可被回收 opts.inPurgeable = true; //
	 * 设置为false,表示不仅Bitmap的属性，也要加载bitmap opts.inJustDecodeBounds = false;
	 * 
	 * bitmap = BitmapFactory.decodeResource(getResources(), drawid, opts); }
	 * else { int quality = Math.round(Math.min(w / bpw, h / bph) * 100); bitmap
	 * = compressImage(BitmapFactory.decodeResource(getResources(), drawid),
	 * quality); } return bitmap; }
	 */

	/*
	 * private Bitmap compressImage(Bitmap image, int quality) {
	 * 
	 * ByteArrayOutputStream baos = new ByteArrayOutputStream();
	 * image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//
	 * 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中 int options = 100; while
	 * (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
	 * baos.reset();// 重置baos即清空baos image.compress(Bitmap.CompressFormat.JPEG,
	 * options, baos);// 这里压缩options%，把压缩后的数据存放到baos中 options -= 10;// 每次都减少10 }
	 * ByteArrayInputStream isBm = new
	 * ByteArrayInputStream(baos.toByteArray());//
	 * 把压缩后的数据baos存放到ByteArrayInputStream中 Bitmap bitmap =
	 * BitmapFactory.decodeStream(isBm, null, null);//
	 * 把ByteArrayInputStream数据生成图片 return bitmap; }
	 */
}
