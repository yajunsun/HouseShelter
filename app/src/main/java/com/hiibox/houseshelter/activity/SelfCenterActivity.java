package com.hiibox.houseshelter.activity;

import java.util.List;

import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hiibox.houseshelter.MyApplication;
import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.net.SpliteUtil;
import com.hiibox.houseshelter.util.MessageUtil;
import com.hiibox.houseshelter.util.PreferenceUtil;
import com.hiibox.houseshelter.wifitools.NewWifiListGet;
import com.zgan.jtws.ZganJTWSService;
import com.zgan.jtws.ZganJTWSServiceTools;
import com.zgan.login.ZganLoginService;
import com.zgan.push.ZganPushService;
import com.zgan.youbao.R;

public class SelfCenterActivity extends ShaerlocActivity {

	@ViewInject(id = R.id.root_layout)
	LinearLayout rootLayout;
	@ViewInject(id = R.id.back_iv, click = "onClick")
	ImageView backIV;
	@ViewInject(id = R.id.modify_nickname_iv, click = "onClick")
	ImageView nicknameIV;
	@ViewInject(id = R.id.modify_emergency_contacts_iv, click = "onClick")
	ImageView contactsIV;
	@ViewInject(id = R.id.exit_account_iv, click = "onClick")
	ImageView exitIV;
	@ViewInject(id = R.id.verification_code_setting, click = "onClick")
	TextView verification_code_setting;
	@ViewInject(id = R.id.wifi_setting_tv, click = "onClick")
	TextView wifi_setting_tv;
	@ViewInject(id = R.id.phone_tv)
	TextView phoneTV;
	@ViewInject(id = R.id.nickname_et)
	EditText nicknameET;
	@ViewInject(id = R.id.emergency_contacts_et)
	EditText contactsET;
	@ViewInject(id = R.id.password_setting_tv, click = "onClick")
	TextView passwordSettingTV;
	@ViewInject(id = R.id.new_or_maintain_address_tv, click = "onClick")
	TextView addressTV;
	@ViewInject(id = R.id.maintain_rfid_card_tv, click = "onClick")
	TextView rfidCardTV;
	@ViewInject(id = R.id.camera_setting_tv, click = "onClick")
	TextView cameraSettingTV;
	@ViewInject(id = R.id.advice_feedback_tv, click = "onClick")
	TextView feedbackTV;
	@ViewInject(id = R.id.about_tv, click = "onClick")
	TextView aboutTV;
	@ViewInject(id = R.id.gesture_toggle_btn_layout, click = "onClick")
	LinearLayout alarmToggleLayout;
	@ViewInject(id = R.id.gesture_toggle_btn_prompt_tv)
	TextView alarmToggleTV;
	@ViewInject(id = R.id.gesture_toggle_btn_on_iv)
	ImageView alarmToggleOnIV;
	@ViewInject(id = R.id.gesture_toggle_btn_off_iv)
	ImageView alarmToggleOffIV;
	/*
	 * @ViewInject(id = R.id.home_toggle_btn_layout, click = "onClick")
	 * LinearLayout homeToggleLayout;
	 * 
	 * @ViewInject(id = R.id.home_toggle_btn_prompt_tv) TextView homeToggleTV;
	 * 
	 * @ViewInject(id = R.id.home_toggle_btn_on_iv) ImageView homeToggleOnIV;
	 * 
	 * @ViewInject(id = R.id.home_toggle_btn_off_iv) ImageView homeToggleOffIV;
	 * 
	 * @ViewInject(id = R.id.defence_toggle_btn_layout, click = "onClick")
	 * LinearLayout defenceToggleLayout;
	 * 
	 * @ViewInject(id = R.id.defence_toggle_btn_prompt_tv) TextView
	 * defenceToggleTV;
	 * 
	 * @ViewInject(id = R.id.defence_toggle_btn_on_iv) ImageView
	 * defenceToggleOnIV;
	 * 
	 * @ViewInject(id = R.id.defence_toggle_btn_off_iv) ImageView
	 * defenceToggleOffIV;
	 */

	@ViewInject(id = R.id.voice_toggle_btn_layout, click = "onClick")
	LinearLayout voiceToggleLayout;
	@ViewInject(id = R.id.voice_toggle_btn_prompt_tv)
	TextView voiceToggleTV;
	@ViewInject(id = R.id.voice_toggle_btn_on_iv)
	ImageView voiceToggleOnIV;
	@ViewInject(id = R.id.voice_toggle_btn_off_iv)
	ImageView voiceToggleOffIV;
	@ViewInject(id = R.id.version_update, click = "onClick")
	TextView versionUpTV;

	private String nickname = null;
	private String contacts = null;
	private boolean modifyNickname = false;
	private boolean modifyContacts = false;
	private boolean alarmToggle = true;
	/*
	 * private boolean homeToggle = true; private boolean defenceToggle = true;
	 */
	private boolean voiceToggle = true;
	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_self_center_layout);
		dialog = new ProgressDialog(SelfCenterActivity.this);
		phoneTV.setText(ZganLoginService.getUserName());

		alarmToggle = PreferenceUtil.getInstance(mContext).getBoolean(
				"alarmMessage", true);
		/*
		 * homeToggle = PreferenceUtil.getInstance(mContext).getBoolean(
		 * "goHomeMessage", true); defenceToggle =
		 * PreferenceUtil.getInstance(mContext).getBoolean( "defenceMessage",
		 * true);
		 */
		if (!alarmToggle) {
			closeAlarmToggle();
		}
		/*
		 * if (!homeToggle) { closeHomeToggle(); } if (!defenceToggle) {
		 * closeDefenceToggle(); }
		 */

		sendRequest(77, null, null);
		sendRequest(98, null, null);

	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			//
			//
			// if (msg.what == 2){
			//
			//
			//
			// Toast.makeText(getApplicationContext(), "注销成功！",
			// Toast.LENGTH_SHORT).show();
			//
			// Logout();
			//
			// }
			// if (msg.what == 3){
			// dialog.dismiss();
			//
			// Toast.makeText(getApplicationContext(), "注销失败！",
			// Toast.LENGTH_SHORT).show();
			//
			// }

			if (msg.what == 1) {
				Frame frame = (Frame) msg.obj;

				int subCmd = frame.subCmd;
				String ret = frame.strData;

				if (frame.mainCmd == 0x0e) {
					switch (subCmd) {
					case 73:
						if (SpliteUtil.getRuquestStatus(ret)) {
							MyApplication.userLevel = Integer
									.valueOf(SpliteUtil.getResult(ret));

						}

						break;
					case 77:

						if (ret.startsWith("1")) {
							return;
						}
						if (!ret.contains("\t")) {
							return;
						}
						String[] result = ret.split("\t");
						if (result[0].equals("0")) {

							if (result.length == 2) {
								nicknameET.setText(result[1].trim());
								nickname = result[1].trim();
							} else if (result.length == 3) {
								nicknameET.setText(result[1].trim());
								contactsET.setText(result[2].trim());
								nickname = result[1].trim();
								contacts = result[2].trim();
								PreferenceUtil.getInstance(mContext)
										.saveString("emergencyContact",
												contacts);

							}
						}
						break;
					case 78:

						if (ret.equals("0")) {
							MessageUtil.alertMessage(mContext,
									R.string.modify_msg_successful);
							PreferenceUtil.getInstance(mContext).saveString(
									"emergencyContact", contacts);
						} else {
							MessageUtil.alertMessage(mContext,
									R.string.modify_msg_failure);
						}
						break;
					case 79:

						break;
					case 97:

						if (ret.equals("0")) {
							if (voiceToggle) {
								voiceToggle = false;

								closeVoiceToggle();
							} else {
								voiceToggle = true;

								openVoiceToggle();
							}
						} else {
							MessageUtil.alertMessage(mContext,
									R.string.modify_voice_failure);
						}
						break;
					case 98:

						String data[] = ret.split("\t");
						if (data[0].equals("0")) {
							if (data[1].equals("0")) {
								voiceToggle = false;

								closeVoiceToggle();
							} else {
								voiceToggle = true;

								openVoiceToggle();
							}
						} else {
							Toast.makeText(mContext, "获取声音开关状态失败",
									Toast.LENGTH_SHORT).show();
						}

						break;
					default:
						break;
					}
				} else if (frame.mainCmd == 0x01 && subCmd == 5) {
					dialog.dismiss();

					if (!TextUtils.isEmpty(frame.strData)
							&& frame.strData.equals("0")) {
						Logout();
					}
				}
			}
		}

	};

	private void sendRequest(final int subCmd, String nickname,
			String emergencyTel) {
		if (!ZganJTWSServiceTools.isConnect) {
			MessageUtil.alertMessage(mContext, R.string.sys_network_error);
			return;
		}
		switch (subCmd) {
		case 73:

			ZganJTWSService.toGetServerData(73, ZganLoginService.getUserName(),
					handler);

			break;
		case 77:
			ZganJTWSService.toGetServerData(77, ZganLoginService.getUserName(),
					handler);

			break;
		case 78:
			ZganJTWSService.toGetServerData(78,
					new String[] { ZganLoginService.getUserName(), nickname,
							emergencyTel }, handler);

			break;
		case 79:
			ZganJTWSService.toGetServerData(79, ZganLoginService.getUserName(),
					handler);

			break;
		case 97:
			ZganJTWSService.toGetServerData(97,
					new String[] { ZganLoginService.getUserName(), nickname },
					handler);

			break;
		case 98:
			ZganJTWSService.toGetServerData(98, ZganLoginService.getUserName(),
					handler);
			break;
		default:
			break;
		}
	}

	public void onClick(View v) {
		int vId = v.getId();
		switch (vId) {
		case R.id.back_iv:
			MianActivity.getScreenManager().exitActivity(mActivity);
			break;
		case R.id.modify_nickname_iv:
			if (!modifyNickname) {
				nicknameET.setEnabled(true);
				nicknameET.requestFocus();
				nicknameET.setSelection(nicknameET.getText().toString()
						.length());
				InputMethodManager imm = (InputMethodManager) nicknameET
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
				nicknameIV.setBackgroundResource(R.drawable.confirm_iv);
				modifyNickname = true;
			} else {
				nicknameET.setEnabled(false);
				nicknameIV.setBackgroundResource(R.drawable.pencel);
				modifyNickname = false;
				String _nickname = nicknameET.getText().toString();
				String _contacts = contactsET.getText().toString();
				if (nickname.equals(_nickname)) {
					break;
				}
				sendRequest(78, _nickname, _contacts);
			}
			break;
		case R.id.modify_emergency_contacts_iv:
			if (!modifyContacts) {
				contactsET.setEnabled(true);
				contactsET.requestFocus();
				contactsET.setSelection(contactsET.getText().toString().trim()
						.length());
				InputMethodManager imm = (InputMethodManager) contactsET
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
				contactsIV.setBackgroundResource(R.drawable.confirm_iv);
				modifyContacts = true;
			} else {
				contactsET.setEnabled(false);
				contactsIV.setBackgroundResource(R.drawable.pencel);
				modifyContacts = false;
				String _nickname = nicknameET.getText().toString();
				String _contacts = contactsET.getText().toString();
				contacts = _contacts;
				sendRequest(78, _nickname, _contacts);

			}
			break;

		case R.id.password_setting_tv:
			startActivity(new Intent(this, PasswordSettingActivity.class));
			break;
		case R.id.wifi_setting_tv:
			startActivity(new Intent(this, NewWifiListGet.class));
			break;
		case R.id.new_or_maintain_address_tv:
			startActivity(new Intent(this, ManageAddressActivity.class));
			break;
		case R.id.maintain_rfid_card_tv:
			startActivity(new Intent(this, ManageRFIDCardActivity.class));
			break;
		case R.id.camera_setting_tv:
			startActivity(new Intent(this, CloudEyesSettingsActivity.class));
			break;
		case R.id.advice_feedback_tv:
			startActivity(new Intent(this, AdviceFeedbackActivity.class));
			break;
		case R.id.about_tv:
			startActivity(new Intent(mContext, AboutActivity.class));
			break;
		case R.id.gesture_toggle_btn_layout:
			if (alarmToggle) {
				alarmToggle = false;
				closeAlarmToggle();
				stopService(new Intent(mContext, ZganPushService.class));
			} else {
				alarmToggle = true;
				openAlarmToggle();
				startService(new Intent(mContext, ZganPushService.class));

			}
			PreferenceUtil.getInstance(mContext).saveBoolean("alarmMessage",
					alarmToggle);
			break;
		/*
		 * case R.id.home_toggle_btn_layout: if (homeToggle) { homeToggle =
		 * false; closeHomeToggle(); } else { homeToggle = true;
		 * openHomeToggle(); }
		 * PreferenceUtil.getInstance(mContext).saveBoolean("goHomeMessage",
		 * homeToggle); break; case R.id.defence_toggle_btn_layout: if
		 * (defenceToggle) { defenceToggle = false; closeDefenceToggle(); } else
		 * { defenceToggle = true; openDefenceToggle(); }
		 * PreferenceUtil.getInstance(mContext).saveBoolean("defenceMessage",
		 * defenceToggle); break;
		 */
		case R.id.voice_toggle_btn_layout:
			if (voiceToggle) {
				sendRequest(97, "0", null);
			} else {
				sendRequest(97, "1", null);
			}

			break;

		case R.id.exit_account_iv:
			dialog.setMessage("正在注销您的账号...");
			dialog.setCancelable(false);
			dialog.show();
			ZganLoginService.toUserQuit(handler);

			// try {
			// MyApplication.tcpManager.logout(MyApplication.phone,
			// MyApplication.password, handler);
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			break;
		case R.id.verification_code_setting:
			startActivity(new Intent(this, VerificationCodeSetting.class));
			break;
		case R.id.version_update:
			startActivity(new Intent(this, VersionUpdate.class));
			break;
		default:
			break;
		}
	}

	private void Logout() {
		// TODO Auto-generated method stub

		PreferenceUtil.getInstance(mContext).saveBoolean("exitApp", true);
		PreferenceUtil.getInstance(mContext).clear();
		PreferenceUtil.getInstance(mContext).destroy();

		ZganLoginService.toClearZganDB();

		stopService(new Intent(mContext, ZganPushService.class));

		setContentView(R.layout.nullxml);
		finish();
		android.os.Process.killProcess(android.os.Process.myPid());

		System.exit(0);
	}

	/**
	 * 判断该服务是否正在运行
	 * 
	 * @param cContext
	 * @param className
	 * @return
	 */

	public static boolean isServiceRunning(Context cContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) cContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	/*
	 * private void openDefenceToggle() {
	 * defenceToggleOnIV.setVisibility(View.VISIBLE);
	 * defenceToggleOffIV.setVisibility(View.INVISIBLE);
	 * defenceToggleLayout.setBackgroundResource(R.drawable.bg_toggle_btn_on);
	 * defenceToggleTV.setText(getString(R.string.turn_on)); }
	 * 
	 * private void closeDefenceToggle() {
	 * defenceToggleOnIV.setVisibility(View.INVISIBLE);
	 * defenceToggleOffIV.setVisibility(View.VISIBLE);
	 * defenceToggleLayout.setBackgroundResource(R.drawable.bg_toggle_btn_off);
	 * defenceToggleTV.setText(getString(R.string.turn_off)); }
	 */
	private void openVoiceToggle() {
		voiceToggleOnIV.setVisibility(View.VISIBLE);
		voiceToggleOffIV.setVisibility(View.INVISIBLE);
		voiceToggleLayout.setBackgroundResource(R.drawable.bg_toggle_btn_on);
		voiceToggleTV.setText(getString(R.string.turn_on));
	}

	private void closeVoiceToggle() {
		voiceToggleOnIV.setVisibility(View.INVISIBLE);
		voiceToggleOffIV.setVisibility(View.VISIBLE);
		voiceToggleLayout.setBackgroundResource(R.drawable.bg_toggle_btn_off);
		voiceToggleTV.setText(getString(R.string.turn_off));
	}

	/*
	 * private void openHomeToggle() {
	 * homeToggleOnIV.setVisibility(View.VISIBLE);
	 * homeToggleOffIV.setVisibility(View.INVISIBLE);
	 * homeToggleLayout.setBackgroundResource(R.drawable.bg_toggle_btn_on);
	 * homeToggleTV.setText(getString(R.string.turn_on)); }
	 * 
	 * private void closeHomeToggle() {
	 * homeToggleOnIV.setVisibility(View.INVISIBLE);
	 * homeToggleOffIV.setVisibility(View.VISIBLE);
	 * homeToggleLayout.setBackgroundResource(R.drawable.bg_toggle_btn_off);
	 * homeToggleTV.setText(getString(R.string.turn_off)); }
	 */

	private void openAlarmToggle() {
		alarmToggleOnIV.setVisibility(View.VISIBLE);
		alarmToggleOffIV.setVisibility(View.INVISIBLE);
		alarmToggleLayout.setBackgroundResource(R.drawable.bg_toggle_btn_on);
		alarmToggleTV.setText(getString(R.string.turn_on));
	}

	private void closeAlarmToggle() {
		alarmToggleOnIV.setVisibility(View.INVISIBLE);
		alarmToggleOffIV.setVisibility(View.VISIBLE);
		alarmToggleLayout.setBackgroundResource(R.drawable.bg_toggle_btn_off);
		alarmToggleTV.setText(getString(R.string.turn_off));
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		handler.removeCallbacks(null);
		MyApplication.Tag = 0;
		super.onDestroy();
	}

	@SuppressWarnings("unused")
	private void hideSoftInput(EditText et) {
		try {
			Class<EditText> cls = EditText.class;
			java.lang.reflect.Method setSoftInputShownOnFocus = cls.getMethod(
					"setSoftInputShownOnFocus", boolean.class);
			setSoftInputShownOnFocus.setAccessible(true);
			setSoftInputShownOnFocus.invoke(et, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// modified by yajunsun 20150929
			MianActivity.getScreenManager().exitActivity(mActivity);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
