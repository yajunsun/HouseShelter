package com.hiibox.houseshelter.activity;

import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hiibox.houseshelter.MyApplication;
import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.core.GlobalUtil;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.util.BackToExitUtil;
import com.hiibox.houseshelter.util.LocationUtil;
import com.hiibox.houseshelter.util.MessageUtil;
import com.hiibox.houseshelter.util.PreferenceUtil;
import com.hiibox.houseshelter.util.ScreenUtil;
import com.hiibox.houseshelter.wifitools.NewWifiListGet;
import com.zgan.file.ZganFileService;
import com.zgan.jtws.ZganJTWSService;
import com.zgan.login.ZganLoginService;
import com.zgan.youbao.R;

public class LoginActivity extends ShaerlocActivity {

	@ViewInject(id = R.id.root_layout)
	RelativeLayout rootLayout;
	@ViewInject(id = R.id.login_iv, click = "onClick")
	ImageView loginIV;
	@ViewInject(id = R.id.register_iv, click = "onClick")
	ImageView registerIV;
	@ViewInject(id = R.id.dial_to_find_password_tv, click = "onClick")
	TextView dialIV;
	@ViewInject(id = R.id.network_setting_tv, click = "onClick")
	TextView settingsIV;
	@ViewInject(id = R.id.phone_number_et)
	EditText phoneET;
	@ViewInject(id = R.id.password_et)
	EditText passwordET;
	@ViewInject(id = R.id.login_register_layout)
	LinearLayout loginRegisterLayout;
	@ViewInject(id = R.id.phone_layout)
	LinearLayout phoneLayout;
	@ViewInject(id = R.id.password_layout)
	LinearLayout passwordLayout;

	private BackToExitUtil exitPrompt = null;
	public static String phone = "";
	public static String password = "";
	private ProgressDialog loginDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login_layout);
		exitPrompt = new BackToExitUtil();
		loginDialog = new ProgressDialog(this);
		loginDialog.setCancelable(false);
		loginDialog.setCanceledOnTouchOutside(false);
		phoneET.setText(ZganLoginService.getUserName());
	}

	@Override
	protected void onResume() {
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setLargeScreenParams();

	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(null);
	}

	private void setLargeScreenParams() {
		int screenHeight = ScreenUtil.getScreenHeight(mActivity);
		if (screenHeight > 854 && screenHeight <= 1280) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, 90);
			params.topMargin = 50;
			loginRegisterLayout.setLayoutParams(params);

			LinearLayout.LayoutParams phoneParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, 90);
			phoneLayout.setLayoutParams(phoneParams);

			LinearLayout.LayoutParams passwordParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, 90);
			passwordParams.topMargin = 30;
			passwordLayout.setLayoutParams(passwordParams);
		}
	}

	@SuppressWarnings("static-access")
	public void onClick(View v) {
		int vid = v.getId();
		Intent intent = new Intent();
		switch (vid) {
		case R.id.login_iv:
			toUserLogin();
			return;
		case R.id.register_iv:
			intent.setClass(this, RegisterActivity.class);
			break;
		case R.id.dial_to_find_password_tv:
			intent.setClass(this, RetrievePassword.class);
			break;
		case R.id.network_setting_tv:
			intent.setClass(this, NewWifiListGet.class);
			break;
		default:
			break;
		}
		startActivity(intent);
	}
	/**
	 * 判断输入的是否为手机号
	 * @param mobile
	 * @return
	 */
	public static boolean isMoblieNub(String mobile){
		if (mobile.startsWith("1")&& mobile.length()==11) {
			return true;
		}
		return false;
		
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
			MyApplication.showedAds = false;
			PreferenceUtil.getInstance(mContext)
					.saveBoolean("showedAds", false);
			
			MianActivity.getScreenManager().exit();
			
			System.exit(0);
		} else {
			Toast.makeText(this, getString(R.string.back_to_exit_app),
					Toast.LENGTH_SHORT).show();
			exitPrompt.doExitInOneSecond();
		}
	}

	public void setFocusable(EditText editText, int msg) {
		editText.setText("");
		editText.setFocusableInTouchMode(true);
		editText.setFocusable(true);
		editText.requestFocus();
		Editable editable = editText.getText();
		Selection.setSelection(editable, 0);
		MessageUtil.alertMessage(mContext, msg);
	}
	
	private void toUserLogin(){
		if (LocationUtil.checkNetWork(mContext).endsWith(
				GlobalUtil.NETWORK_NONE)) {
			MessageUtil.alertMessage(mContext, R.string.sys_network_error1);
			
			return;
		}

		phone = phoneET.getText().toString();
		if (TextUtils.isEmpty(phone)) {
			setFocusable(phoneET, R.string.prompt_input_phone);
			return;
		}
		if (!isMoblieNub(phone)) {
			setFocusable(phoneET, R.string.prompt_input_true_phone);
			return;
		}
		password = passwordET.getText().toString();
		if (TextUtils.isEmpty(password)) {
			setFocusable(passwordET, R.string.hint_input_password);
			return;
		}
		
		if(password.length()>20){
			setFocusable(passwordET, R.string.hint_input_passwordlen);
			return;
		}
		
		loginDialog.setMessage(getResources().getString(R.string.logining));
		loginDialog.setCancelable(false);
		loginDialog.show();
		
		String imei = LocationUtil.getDrivenToken(mContext,phone);
		
		ZganLoginService.toUserLogin(phone, password, imei, handler);
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
            if(msg.what==1){
            	Frame frame=(Frame)msg.obj;
            	
            	if(frame.mainCmd==0x01 && frame.subCmd==4){            	
	            	
					ZganJTWSService.toStartJTWSService(mContext);
					ZganFileService.toStartFileService(mContext);
					
					startActivity(new Intent(mContext, HomepageActivity.class));
					//update by YQ (修改登录时黑屏)
					//MianActivity.getScreenManager().exitAllActivityExceptOne();
					
					loginDialog.dismiss();
				}
            }else{
            	
            	loginDialog.dismiss();
            	
            	Frame frame=(Frame)msg.obj;
            	String strMsg=getString(R.string.receive_server_info_failed);
            	
            	if(frame!=null && !TextUtils.isEmpty(frame.strData)){
            		String[] aryData=frame.strData.split("\t");
            		
            		if(aryData.length==2){
            			strMsg=aryData[1];
            		}           		
            	}
            	
            	MessageUtil.alertMessage(mContext,strMsg);
            }
		}
	};

}
