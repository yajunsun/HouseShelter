package com.hiibox.houseshelter.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.net.TheErrorCode;
import com.hiibox.houseshelter.util.ImageOperation;
import com.hiibox.houseshelter.util.LocationUtil;
import com.hiibox.houseshelter.util.MessageUtil;
import com.zgan.file.ZganFileService;
import com.zgan.jtws.ZganJTWSService;
import com.zgan.login.ZganLoginService;
import com.zgan.youbao.R;

public class RegisterActivity extends ShaerlocActivity {

	@ViewInject(id = R.id.root_layout)
	LinearLayout rootLayout;
	@ViewInject(id = R.id.two_dimension_code_iv, click = "onClick")
	ImageView codeIV;
	@ViewInject(id = R.id.cancel_register_iv, click = "onClick")
	ImageView cancelIV;
	@ViewInject(id = R.id.commit_iv, click = "onClick")
	ImageView commitIV;
	@ViewInject(id = R.id.device_code_et)
	EditText deviceCodeET;
	@ViewInject(id = R.id.phone_number_et)
	EditText phoneET;
	@ViewInject(id = R.id.password_et)
	EditText passwordET;
	@ViewInject(id = R.id.nickname_et)
	EditText nicknameET;
	@ViewInject(id = R.id.confirm_password_et)
	EditText confirmPasswordET;
	@ViewInject(id = R.id.address_et)
	EditText addressET;
	@ViewInject(id = R.id.auth_code_et)
	EditText authCodeET;
	@ViewInject(id = R.id.phone_code)
	EditText phonecodeET;
	@ViewInject(id = R.id.sms_code,click = "onClick")
	Button smscodeET;
	
	private TimeCount timeCount;
	private String deviceCode = "";
	private String authCode = "";
	private String smscode="";
	private String phone="";
	private String password="";
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_layout);
        dialog=new ProgressDialog(mActivity);
        dialog.setCancelable(false);
		timeCount = new TimeCount(60000, 1000);//构造CountDownTimer对象
	}

	public void onClick(View v) {
		if (v == codeIV) {
			startActivityForResult(new Intent(this, ScanCodeActivity.class),
					0x101);
		} else if (v == cancelIV) {
			MianActivity.getScreenManager().exitActivity(mActivity);
		}else if (v==smscodeET) {
			//发送短信较验码
			String phone =phoneET.getText().toString().trim();
			
			if (!TextUtils.isEmpty(phone)) {				
				timeCount.start();		
				
				ZganLoginService.toGetServerData(8, new String[]{phone}, handler);
			}else{
				MessageUtil.alertMessage(mContext, R.string.prompt_input_phone);
			}
			
		} else if (v == commitIV) {
			toUserRegister();	
		}
	}
	
	//用户注册
	private void toUserRegister(){
		phone=phoneET.getText().toString().trim();
		
		if (TextUtils.isEmpty(phone)) {
			MessageUtil.alertMessage(mContext,
					R.string.hint_input_phone_number);
			return;
		}
		if (!isMobileNum(phone)) {
			MessageUtil.alertMessage(mContext,
					R.string.prompt_input_correct_phone);
			return;
		}
		password = passwordET.getText().toString();
		if (TextUtils.isEmpty(password)) {
			setFocusable(passwordET, R.string.hint_input_password);
			return;
		}
		String confirmNewPsw = confirmPasswordET.getText().toString();
		if (TextUtils.isEmpty(confirmNewPsw)) {
			setFocusable(confirmPasswordET,
					R.string.hint_input_confirm_password);
			return;
		}
		
		if(password.length()>20){
			setFocusable(passwordET,R.string.hint_input_passwordlen);
			return;
		}
		
		if (!password.equals(confirmNewPsw)) {
			setFocusable(passwordET,
					R.string.prompt_input_diffrient_password);
			MessageUtil.alertMessage(mContext,
					R.string.prompt_input_diffrient_password);
			confirmPasswordET.setText("");
			return;
		}

		String nickname = nicknameET.getText().toString().trim();
		if (TextUtils.isEmpty(nickname)) {
			setFocusable(nicknameET, R.string.hint_input_nickname);
			return;
		}

		String address = addressET.getText().toString().trim();
		if (TextUtils.isEmpty(address)) {
			setFocusable(addressET, R.string.hint_input_address);
			return;
		}
		deviceCode = deviceCodeET.getText().toString().trim();
		if (TextUtils.isEmpty(deviceCode)) {
			setFocusable(deviceCodeET, R.string.hint_input_device_code);
			return;
		}
		authCode = authCodeET.getText().toString().trim();
		if (TextUtils.isEmpty(authCode)) {
			setFocusable(authCodeET, R.string.hint_input_auth_code);
			return;
		}
		smscode = phonecodeET.getText().toString().trim();
		if (TextUtils.isEmpty(smscode)) {
			setFocusable(phonecodeET, R.string.hint_input_sms_code);
			return;
		}

		dialog.setMessage("正在注册中，请稍候...");		
		dialog.show();
		
		ZganLoginService.toGetServerData(3, new String[]{phone, password, deviceCode,
				authCode, address, nickname, smscode}, handler);

	}
	
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
		}
		@Override
		public void onFinish() {//计时完毕时触发
			smscodeET.setText("重新发送");
			smscodeET.setClickable(true);
			
			
		}
		@Override
		public void onTick(long millisUntilFinished){//计时过程显示
			smscodeET.setClickable(false);
			smscodeET.setText(millisUntilFinished /1000+"秒");
		}
	}
	@SuppressWarnings("unused")
	private String transformCode(String src) {
		String des = null;
		try {
			des = URLEncoder.encode(src, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return des;
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if(msg.what==1){
				Frame frame=(Frame)msg.obj;
				if(frame!=null){
					//获取短信
					if(frame.mainCmd==0x01 && frame.subCmd==8){
						dialog.dismiss();
						
						if(!TextUtils.isEmpty(frame.strData) && frame.strData.equals("0")){
							
							MessageUtil.alertMessage(mContext, R.string.sms_success);
						}else{
							MessageUtil.alertMessage(mContext, R.string.network_not_response);
						}

					}else if(frame.mainCmd==0x01 && frame.subCmd==3){
						Log.e("frame.strData", ""+frame.strData);
						//注册成功						
						if(!TextUtils.isEmpty(frame.strData) && frame.strData.equals("0")){
							dialog.setMessage("注册成功，正登录柚保...");							
							String imei = LocationUtil.getDrivenToken(mContext,phone);
							
							try {
								Thread.sleep(1000);
								
								ZganLoginService.toUserLogin(phone, password, imei, handler);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
							
						}else{
							dialog.dismiss();
							MessageUtil.alertMessage(mContext,getString(R.string.register_failed)+TheErrorCode.compare(frame.strData));
						}
						
					}else if(frame.mainCmd==0x01 && frame.subCmd==4){
						//自动登录
						if(!TextUtils.isEmpty(frame.strData) && frame.strData.equals("0")){
							ZganJTWSService.toStartJTWSService(RegisterActivity.this);
							ZganFileService.toStartFileService(RegisterActivity.this);
							
							startActivity(new Intent(RegisterActivity.this,HomepageActivity.class));
							MianActivity.getScreenManager().exitAllActivityExceptOne();
							
							dialog.dismiss();
						}else{
							dialog.dismiss();
							MessageUtil.alertMessage(mContext,getString(R.string.register_failed));
							
						}
					}
				}				
				
			}else{
				dialog.dismiss();
				MessageUtil.alertMessage(mContext,getString(R.string.network_not_response));
			}
		}
	};


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0x101 && resultCode == RESULT_OK) {
			deviceCodeET.setText(data.getStringExtra("code"));
		}
	}

	public void setFocusable(EditText editText, int msg) {
		MessageUtil.alertMessage(mContext, msg);
		editText.setText("");
		editText.setFocusableInTouchMode(true);
		editText.setFocusable(true);
		editText.requestFocus();
		Editable editable = editText.getText();
		Selection.setSelection(editable, 0);
	}

	private boolean isMobileNum(String mobiles) {
		if (mobiles.startsWith("1")&& mobiles.length()==11) {
			return true;
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		BitmapDrawable bitmapDrawable = new BitmapDrawable(
				ImageOperation.readBitMap(mContext, R.drawable.bg_app));
		rootLayout.setBackgroundDrawable(bitmapDrawable);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	MianActivity.getScreenManager().exitActivity(mActivity);
            return true; 
        }
        return super.onKeyDown(keyCode, event);
    }
	
	
	@Override
	protected void onStop() {
		super.onStop();
		rootLayout.setBackgroundDrawable(null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(null);
	}
	


}
