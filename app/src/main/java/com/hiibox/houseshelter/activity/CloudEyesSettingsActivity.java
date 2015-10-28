package com.hiibox.houseshelter.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hiibox.houseshelter.MyApplication;
import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.adapter.DropDownBoxAdapter;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.util.DateUtil;
import com.hiibox.houseshelter.util.ImageOperation;
import com.hiibox.houseshelter.util.MessageUtil;
import com.hiibox.houseshelter.util.PreferenceUtil;
import com.zgan.jtws.ZganJTWSService;
import com.zgan.jtws.ZganJTWSServiceTools;
import com.zgan.login.ZganLoginService;
import com.zgan.youbao.R;

public class CloudEyesSettingsActivity extends ShaerlocActivity {

	@ViewInject(id = R.id.root_layout)
	LinearLayout rootLayout;
	@ViewInject(id = R.id.back_iv, click = "onClick")
	ImageView backIV;
	// @ViewInject(id = R.id.clear_cache_iv, click = "onClick") ImageView
	// clearCacheIV;

	@ViewInject(id = R.id.video_quality_drop_down_box_et, click = "onClick")
	EditText videoQualityET;
	@ViewInject(id = R.id.capture_drop_down_box_et, click = "onClick")
	EditText captureNumET;
	@ViewInject(id = R.id.timing_drop_down_box_et, click = "onClick")
	EditText timingET;
	@ViewInject(id = R.id.gesture_toggle_btn_layout, click = "onClick")
	LinearLayout gestureToggleLayout;
	@ViewInject(id = R.id.gesture_toggle_btn_on_iv)
	ImageView gestureToggleOnIV;
	@ViewInject(id = R.id.gesture_toggle_btn_off_iv)
	ImageView gestureToggleOffIV;
	@ViewInject(id = R.id.gesture_toggle_btn_prompt_tv)
	TextView gestureTogglePromptTV;
	@ViewInject(id = R.id.capture_toggle_btn_layout, click = "onClick")
	LinearLayout captureToggleLayout;
	@ViewInject(id = R.id.capture_toggle_btn_on_iv)
	ImageView captureToggleOnIV;
	@ViewInject(id = R.id.capture_toggle_btn_off_iv)
	ImageView captureToggleOffIV;
	@ViewInject(id = R.id.capture_toggle_btn_prompt_tv)
	TextView captureTogglePromptTV;

	private DropDownBoxAdapter adapter = null;
	private String[] captureNumbersArr = null;
	private String[] captureNumbersArr2 = null;
	private List<String> captureNumbersList = null;
	private List<String> captureNumbersList2 = null;

	private View cardView = null;
	private ListView cardNumberLV = null;
	private View cardView2 = null;
	private ListView cardNumberLV2 = null;

	private TimePickerDialog picker = null;
	private boolean gestureToggle = false;
	private boolean captureToggle = false;
	private String gestureTracks = null;
	private int picNum = 0;
	private int picNum2 = 0;
	private String time = null;
	private ProgressDialog loginDialog = null;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (null == msg.obj) {
				return;
			}

			loginDialog.dismiss();

			Frame frame = (Frame) msg.obj;

			int subCmd = frame.subCmd;
			if (msg.what == 1) {

				String ret = frame.strData;

				switch (subCmd) {
				case 7: // 抓拍数量设置!!!!!
					loginDialog.dismiss();

					if (!TextUtils.isEmpty(frame.strData) && ret.equals("0")) {
						captureNumET.setText(captureNumbersList.get(picNum));
						PreferenceUtil.getInstance(mContext).saveInt(
								"captureNumbers", picNum);
						MessageUtil.alertMessage(mContext,
								R.string.setting_success);
					} else {
						MessageUtil.alertMessage(mContext,
								R.string.setting_failed);
					}

					break;
				case 12: // 定时抓拍时间设置
					loginDialog.dismiss();
					if (!TextUtils.isEmpty(frame.strData) && ret.equals("0")) {
						timingET.setText(time);
						PreferenceUtil.getInstance(mContext).saveString(
								"captureTime", time);
						MessageUtil.alertMessage(mContext,
								R.string.setting_success);
					} else {
						MessageUtil.alertMessage(mContext,
								R.string.setting_failed);
					}

					break;
				case 88:
					if (!TextUtils.isEmpty(frame.strData)) {
						String[] aryRfidData = frame.strData.split("\t");

						if (aryRfidData.length == 2
								&& aryRfidData[0].equals("0")) {

							String strCount = aryRfidData[1];

							if (!TextUtils.isEmpty(strCount)
									&& strCount.equals("1")) {
								captureNumET.setText(captureNumbersList.get(0));
							} else if (!TextUtils.isEmpty(strCount)
									&& strCount.equals("2")) {
								captureNumET.setText(captureNumbersList.get(1));
							} else if (!TextUtils.isEmpty(strCount)
									&& strCount.equals("3")) {
								captureNumET.setText(captureNumbersList.get(2));
							} else {
								captureNumET
										.setText(getString(R.string.spaceholder));
							}
						}
					}

					break;
				case 89:
					if (!TextUtils.isEmpty(frame.strData)) {
						String[] aryRfidData = frame.strData.split("\t");

						if (aryRfidData.length == 2
								&& aryRfidData[0].equals("0")) {
							timingET.setText(aryRfidData[1]);
						}
					}

					break;
				case 90:
					if (!TextUtils.isEmpty(frame.strData)) {
						String[] aryRfidData = frame.strData.split("\t");

						if (aryRfidData.length == 3
								&& aryRfidData[0].equals("0")) {
							// 设置时间格式
							String strTime = "--:--";

							if (!TextUtils.isEmpty(aryRfidData[1])
									&& !aryRfidData[1].equals("null")) {
								strTime = aryRfidData[1].substring(0, 5);
							}

							timingET.setText(strTime);

							if (aryRfidData[2].equals("0")) {
								captureToggle = true;
								captureToggleOnIV.setVisibility(View.VISIBLE);
								captureToggleOffIV
										.setVisibility(View.INVISIBLE);
								captureToggleLayout
										.setBackgroundResource(R.drawable.bg_toggle_btn_on);
								captureTogglePromptTV
										.setText(getString(R.string.turn_on));
							} else {
								captureToggle = false;
								captureToggleOnIV.setVisibility(View.INVISIBLE);
								captureToggleOffIV.setVisibility(View.VISIBLE);
								captureToggleLayout
										.setBackgroundResource(R.drawable.bg_toggle_btn_off);
								captureTogglePromptTV
										.setText(getString(R.string.turn_off));
							}
						}
					}

					break;
				case 24:
					if (!TextUtils.isEmpty(frame.strData) && ret.equals("0")) {
						videoQualityET
								.setText(captureNumbersList2.get(picNum2));
						PreferenceUtil.getInstance(mContext).saveInt(
								"captureNumbers2", picNum2);
						MessageUtil.alertMessage(mContext,
								R.string.setting_success);
					} else {
						MessageUtil.alertMessage(mContext,
								R.string.setting_failed);
					}
					break;
				case 26:
					if (!TextUtils.isEmpty(frame.strData)) {
						String[] aryRfidData = frame.strData.split("\t");

						if (aryRfidData.length == 2
								&& aryRfidData[0].equals("0")) {
							if (aryRfidData[1].equals("1")) {
								videoQualityET.setText(captureNumbersList2
										.get(0));
							}
							if (aryRfidData[1].equals("3")) {
								videoQualityET.setText(captureNumbersList2
										.get(1));
							}
							//modified by yajunsun 20150916当前视频质量显示
							else {
								videoQualityET.setText(captureNumbersList2.get(
										(Integer.parseInt(aryRfidData[1])-1)/2));
							}
						}

					} else {
						MessageUtil.alertMessage(mContext,
								R.string.setting_failed);
					}
					break;
				}
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cloud_eyes_settings_layout);
		gestureToggle = PreferenceUtil.getInstance(mContext).getBoolean(
				"gestureToggle", false);
		captureToggle = PreferenceUtil.getInstance(mContext).getBoolean(
				"captureToggle", false);
		picNum = PreferenceUtil.getInstance(mContext).getInt("captureNumbers",
				2);
		picNum2 = PreferenceUtil.getInstance(mContext).getInt(
				"captureNumbers2", 2);

		// if (gestureToggle) { // 手势已开启
		// gestureToggleOnIV.setVisibility(View.VISIBLE);
		// gestureToggleOffIV.setVisibility(View.INVISIBLE);
		// gestureToggleLayout
		// .setBackgroundResource(R.drawable.bg_toggle_btn_on);
		// gestureTogglePromptTV.setText(getString(R.string.turn_on));
		// } else { // 手势已关闭
		// gestureToggleOnIV.setVisibility(View.INVISIBLE);
		// gestureToggleOffIV.setVisibility(View.VISIBLE);
		// gestureToggleLayout
		// .setBackgroundResource(R.drawable.bg_toggle_btn_off);
		// gestureTogglePromptTV.setText(getString(R.string.turn_off));
		// }

		initAdapter();

		loginDialog = new ProgressDialog(this);
		loginDialog.setCancelable(true);
		loginDialog.setCanceledOnTouchOutside(true);

		sendRequest(88, null, 0);
		sendRequest(90, null, 0);
		sendRequest(26, null, 0);

	}

	@Override
	protected void onResume() {
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		BitmapDrawable bitmapDrawable = new BitmapDrawable(
				ImageOperation.readBitMap(mContext, R.drawable.bg_app));
		rootLayout.setBackgroundDrawable(bitmapDrawable);
		gestureTracks = PreferenceUtil.getInstance(mContext).getString(
				"gestureTracks", null);

	}

	@Override
	protected void onStop() {
		super.onStop();
		rootLayout.setBackgroundDrawable(null);
	}

	private void initAdapter() {
		captureNumbersArr = getResources().getStringArray(
				R.array.capture_picture_numbers_array);
		captureNumbersList = new ArrayList<String>();
		for (int i = 0; i < captureNumbersArr.length; i++) {
			captureNumbersList.add(captureNumbersArr[i]);
		}

		adapter = new DropDownBoxAdapter(mContext);
		adapter.setList(captureNumbersList);
		cardView = getLayoutInflater().inflate(
				R.layout.popupwindow_drop_down_layout, null);
		cardNumberLV = (ListView) cardView.findViewById(R.id.popup_lv);
		cardNumberLV.setAdapter(adapter);

		captureNumbersArr2 = getResources().getStringArray(
				R.array.capture_picture_numbers_array2);
		captureNumbersList2 = new ArrayList<String>();
		for (int i = 0; i < captureNumbersArr2.length; i++) {
			captureNumbersList2.add(captureNumbersArr2[i]);
		}

		adapter = new DropDownBoxAdapter(mContext);
		adapter.setList(captureNumbersList2);
		cardView2 = getLayoutInflater().inflate(
				R.layout.popupwindow_drop_down_layout, null);
		cardNumberLV2 = (ListView) cardView2.findViewById(R.id.popup_lv);
		cardNumberLV2.setAdapter(adapter);

	}

	public void onClick(View v) {
		int vid = v.getId();

		switch (vid) {

		case R.id.back_iv:
			MianActivity.getScreenManager().exitActivity(mActivity);
			break;

		case R.id.video_quality_drop_down_box_et:
			if (!TextUtils.isEmpty(MyApplication.statues)
					&& MyApplication.statues.equals("3")) {
				// 设备在线
				Log.e("设备在线", "设备在线");

				showPopupWindow2();

			} else {
				Toast.makeText(getApplicationContext(), R.string.online_text,
						Toast.LENGTH_SHORT).show();

			}

			break;
		case R.id.capture_drop_down_box_et:
			if (!TextUtils.isEmpty(MyApplication.statues)
					&& MyApplication.statues.equals("3")) {
				// 设备在线
				Log.e("设备在线", "设备在线");

			} else {
				Toast.makeText(getApplicationContext(), R.string.online_text,
						Toast.LENGTH_SHORT).show();
				return;
			}
			showPopupWindow();
			break;
		case R.id.timing_drop_down_box_et:
			if (!TextUtils.isEmpty(MyApplication.statues)
					&& MyApplication.statues.equals("3")) {
				// 设备在线
				Log.e("设备在线", "设备在线");

			} else {
				Toast.makeText(getApplicationContext(), R.string.online_text,
						Toast.LENGTH_SHORT).show();
				return;
			}
			showTimePicker();
			break;
		case R.id.gesture_toggle_btn_layout:
			if (!TextUtils.isEmpty(MyApplication.statues)
					&& MyApplication.statues.equals("3")) {
				// 设备在线
				Log.e("设备在线", "设备在线");

			} else {
				Toast.makeText(getApplicationContext(), R.string.online_text,
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (gestureToggle) { // 安全手势 开-->关
				startActivityForResult(new Intent(
						CloudEyesSettingsActivity.this,
						CloseGestureActivity.class), 0x502);
			} else { // 安全手势 关-->开
				if (TextUtils.isEmpty(gestureTracks)) {
					startActivityForResult(new Intent(
							CloudEyesSettingsActivity.this,
							GestureSettingActivity.class), 0x501);
				} else {
					openGesture();
				}
			}
			break;
		case R.id.capture_toggle_btn_layout:
			if (!TextUtils.isEmpty(MyApplication.statues)
					&& MyApplication.statues.equals("3")) {
				// 设备在线
				Log.e("设备在线", "设备在线");

			} else {
				Toast.makeText(getApplicationContext(), R.string.online_text,
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (captureToggle) { // 抓拍开关 开-->关
				sendRequest(12, time, 1);
				captureToggle = false;
				captureToggleOnIV.setVisibility(View.INVISIBLE);
				captureToggleOffIV.setVisibility(View.VISIBLE);
				captureToggleLayout
						.setBackgroundResource(R.drawable.bg_toggle_btn_off);
				captureTogglePromptTV.setText(getString(R.string.turn_off));
			} else { // 抓拍开关 关-->开
				sendRequest(12, time, 0);
				captureToggle = true;
				captureToggleOnIV.setVisibility(View.VISIBLE);
				captureToggleOffIV.setVisibility(View.INVISIBLE);
				captureToggleLayout
						.setBackgroundResource(R.drawable.bg_toggle_btn_on);
				captureTogglePromptTV.setText(getString(R.string.turn_on));
			}
			break;
		default:
			break;
		}
	}

	private void openGesture() {
		gestureToggle = true;
		gestureToggleOnIV.setVisibility(View.VISIBLE);
		gestureToggleOffIV.setVisibility(View.INVISIBLE);
		gestureToggleLayout.setBackgroundResource(R.drawable.bg_toggle_btn_on);
		gestureTogglePromptTV.setText(getString(R.string.turn_on));
		PreferenceUtil.getInstance(mContext).saveBoolean("gestureToggle",
				gestureToggle);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0x501 && resultCode == RESULT_OK) {
			boolean gestureSuccess = data.getBooleanExtra("unlockSuccess",
					false);
			if (gestureSuccess) {
				openGesture();
			}
		} else if (requestCode == 0x502 && resultCode == RESULT_OK) {
			if (null == data) {
				return;
			}
			boolean allowCloseGesture = data.getBooleanExtra(
					"allowCloseGesture", false);
			if (allowCloseGesture) {
				gestureToggle = false;
				gestureToggleOnIV.setVisibility(View.INVISIBLE);
				gestureToggleOffIV.setVisibility(View.VISIBLE);
				gestureToggleLayout
						.setBackgroundResource(R.drawable.bg_toggle_btn_off);
				gestureTogglePromptTV.setText(getString(R.string.turn_off));
				PreferenceUtil.getInstance(mContext).saveBoolean(
						"gestureToggle", gestureToggle);
			}
		}
	}

	private void showPopupWindow() {
		final PopupWindow popupWindow = new PopupWindow(cardView,
				captureNumET.getWidth(), LayoutParams.WRAP_CONTENT, true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.getBackground().setAlpha(128);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.showAsDropDown(captureNumET);

		cardNumberLV
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View v,
							int position, long id) {

						picNum = position;
						// flag=position;

						sendRequest(7, String.valueOf(picNum + 1), 0);
						popupWindow.dismiss();
					}
				});
	}

	private void showPopupWindow2() {
		final PopupWindow popupWindow = new PopupWindow(cardView2,
				videoQualityET.getWidth(), LayoutParams.WRAP_CONTENT, true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.getBackground().setAlpha(128);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.showAsDropDown(videoQualityET);

		cardNumberLV2
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View v,
							int position, long id) {
						int type = 0;
						picNum2 = position;
						if (position != 0) {
							type = position + position;
						}
						// time=DateUtil.getTime();
						sendRequest(24, time, (type + 1));
						popupWindow.dismiss();
					}
				});
	}

	private void showTimePicker() {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		picker = new TimePickerDialog(this, new TimeSetListener(), hour,
				minute, true);
		picker.show();
	}

	@Override
	protected void onDestroy() {
		if (handler != null) {
			handler.removeCallbacks(null);
			handler = null;
		}

		super.onDestroy();
	}

	class TimeSetListener implements OnTimeSetListener {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			time = hourOfDay + ":" + minute + ":" + "00";

			if (captureToggle) {
				sendRequest(12, time, 0);
			} else {
				sendRequest(12, time, 1);
			}
		}
	}

	private void sendRequest(int queryCode, String time, int type) {
		if (!ZganJTWSServiceTools.isConnect) {
			MessageUtil.alertMessage(mContext, R.string.sys_network_error);
			return;
		}

		switch (queryCode) {
		case 12:
			ZganJTWSService.toGetServerData(12,
					new String[] { ZganLoginService.getUserName(), time,
							Integer.toString(type) }, handler);
			break;
		case 88:
			ZganJTWSService.toGetServerData(88, MyApplication.deviceCode,
					handler);
			break;
		case 89:
			ZganJTWSService.toGetServerData(89, MyApplication.deviceCode,
					handler);

			break;
		case 7:
			ZganJTWSService.toGetServerData(7,
					new String[] { ZganLoginService.getUserName(), time,
							DateUtil.getcurrentDay() }, handler);

			break;
		case 90:
			ZganJTWSService.toGetServerData(90, MyApplication.deviceCode,
					handler);

			break;
		case 24:
			ZganJTWSService.toGetServerData(
					24,
					new String[] { ZganLoginService.getUserName(),
							Integer.toString(type), time }, handler, 1, 0x0E);
			break;
		case 26:
			ZganJTWSService.toGetServerData(26,
					new String[] { ZganLoginService.getUserName() }, handler,
					1, 0x0E);
			break;

		}

	}

}
