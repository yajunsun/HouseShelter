package com.hiibox.houseshelter.activity;

import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.SuppressLint;
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
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.util.ImageOperation;
import com.hiibox.houseshelter.util.MessageUtil;
import com.hiibox.houseshelter.util.PreferenceUtil;
import com.zgan.jtws.ZganJTWSService;
import com.zgan.jtws.ZganJTWSServiceTools;
import com.zgan.login.ZganLoginService;
import com.zgan.youbao.R;

public class ResetVerifictaicionCode extends ShaerlocActivity {

	@ViewInject(id = R.id.root_layout)
	LinearLayout rootLayout;
	@ViewInject(id = R.id.back_iv, click = "onClick")
	ImageView backIV;

	@ViewInject(id = R.id.new_password_et)
	EditText newPswET;
	@ViewInject(id = R.id.confirm_new_password_et)
	EditText confirmPswET;
	@ViewInject(id = R.id.progress_bar)
	ProgressBar progressBar;
	@ViewInject(id = R.id.phone_code)
	EditText phonecodeET;
	@ViewInject(id = R.id.sms_code, click = "onClick")
	Button smscodeET;


	@ViewInject(id = R.id.save_btn, click = "onClick")	Button confirmTV;
	
	String time = null;
	private TimeCount timeCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verificationcode_setting_layout2);
		timeCount = new TimeCount(60000, 1000);// 构造CountDownTimer对象

	}

	public void onClick(View v) {
		if (v == backIV) {
			Intent intent=new Intent(ResetVerifictaicionCode.this,VerificationCodeSetting.class);
        	startActivity(intent);
			
			MianActivity.getScreenManager().exitActivity(mActivity);
			
			
		} else if (v == smscodeET) {
			timeCount.start();
			String phone = PreferenceUtil.getInstance(getApplicationContext())
					.getString("phone", null);
			GetCode(phone);

		} else if (v == confirmTV) {


			String newPsw = newPswET.getText().toString();
			if (TextUtils.isEmpty(newPsw)) {
				MessageUtil.alertMessage(mContext,R.string.device_err_revisepwd);
				return;
			}
			
			String confirmNewPsw = confirmPswET.getText().toString();
			if (TextUtils.isEmpty(confirmNewPsw)) {
				MessageUtil.alertMessage(mContext,R.string.device_err_revisepwd_confinrm);
				return;
			}
			
			if (!newPsw.equals(confirmNewPsw)) {
				MessageUtil.alertMessage(mContext,R.string.device_err_revisepwd_confinrm_1);
				newPswET.setText("");
				confirmPswET.setText("");
				return;
			}		
			
			if(newPsw.length()>20){
				MessageUtil.alertMessage(mContext,R.string.device_err_revisepwd_len);
				return;
			}
			
			String code = phonecodeET.getText().toString();			
			if (TextUtils.isEmpty(code)) {
				MessageUtil.alertMessage(mContext,R.string.device_err_revisepwd_code);
				return;
			}
			
			int subid = 0;
			if (time != null) {
				subid = 95;
			} else {
				subid = 0;
			}
			modifyPassword(newPsw, code, subid);
		}
	}

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			smscodeET.setText("重新发送");
			smscodeET.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			smscodeET.setClickable(false);
			smscodeET.setText(millisUntilFinished / 1000 + "秒");
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		progressBar.setVisibility(View.INVISIBLE);
		return super.onKeyDown(keyCode, event);
	}

	private void GetCode(String phone) {
		if (!ZganJTWSServiceTools.isConnect) {
			MessageUtil.alertMessage(mContext, R.string.sys_network_error);
			return;
		}
	
		ZganJTWSService.toGetServerData(94, ZganLoginService.getUserName(), handler);
	}

	private void modifyPassword(final String newPsw,String code, int submid) {
		if (!ZganJTWSServiceTools.isConnect) {
			MessageUtil.alertMessage(mContext, R.string.sys_network_error);
			return;
		}
		ZganJTWSService.toGetServerData(95, new String[]{code, time, newPsw}, handler);

	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			progressBar.setVisibility(View.GONE);
			
			if(msg.what==1){
				Frame frame=(Frame)msg.obj;
				
				//获取短信
				if(frame.subCmd==94){
					if (!TextUtils.isEmpty(frame.strData)){
						String[] aryRfidData = frame.strData.split("\t");
						
						if(aryRfidData.length==2 && aryRfidData[0].equals("0")){
							MessageUtil.alertMessage(mContext, R.string.sms_success);
							
							time = aryRfidData[1];
						}						
					}					
				}else if(frame.subCmd==95){
					//重置设备验证码
					
					if(frame.strData!=null && frame.strData.equals("0")){						
						MessageUtil.alertMessage(mContext, R.string.ret_success);
						MianActivity.getScreenManager().exitActivity(mActivity);
					}else{
						MessageUtil.alertMessage(mContext, R.string.operate_failed);
					}
				}				
			}

		}
	};

	public void setFocusable(EditText editText, int msg) {
		editText.setText("");
		editText.setFocusableInTouchMode(true);
		editText.setFocusable(true);
		editText.requestFocus();
		Editable editable = editText.getText();
		Selection.setSelection(editable, 0);
		MessageUtil.alertMessage(mContext, msg);
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
