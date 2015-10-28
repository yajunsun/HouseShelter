package com.hiibox.houseshelter.activity;

import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.util.ImageOperation;
import com.hiibox.houseshelter.util.MessageUtil;
import com.zgan.login.ZganLoginService;
import com.zgan.youbao.R;


public class RetrievePassword extends ShaerlocActivity {

    @ViewInject(id = R.id.root_layout) LinearLayout rootLayout;
    @ViewInject(id = R.id.back_iv, click = "onClick") ImageView backIV;
    @ViewInject(id = R.id.save_btn, click = "onClick") Button confirmTV;
    @ViewInject(id = R.id.new_password_et) EditText newPswET;
    @ViewInject(id = R.id.confirm_new_password_et) EditText confirmPswET;
    @ViewInject(id = R.id.phone_code)	EditText phonecodeET;
	@ViewInject(id = R.id.sms_code,click = "onClick")	Button smscodeET;
	@ViewInject(id = R.id.user_phone)	EditText userphone;
	ProgressDialog dialog;
	private TimeCount timeCount;
	String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_setting_layout1);
        dialog=new ProgressDialog(mActivity);
        dialog.setCancelable(false);
        timeCount = new TimeCount(60000, 1000);//构造CountDownTimer对象\
        
        userphone.setText(ZganLoginService.getUserName());
    }
    
    public void onClick(View v) {
        if (v == backIV) {
            MianActivity.getScreenManager().exitActivity(mActivity);
        }else if (v==smscodeET) {
			phone =userphone.getText().toString().trim();			
			if (!TextUtils.isEmpty(phone)) {
				/*dialog.setMessage("发送短信验证中...");
				dialog.show();*/
				
				ZganLoginService.toGetServerData(8, new String[]{phone}, handler);
			}else{
				MessageUtil.alertMessage(mContext, R.string.prompt_input_phone);
			}
			
		}else if (v == confirmTV) {
            int submid=0;

            String phone =userphone.getText().toString().trim();
    		if (TextUtils.isEmpty(phone)) {
				 MessageUtil.alertMessage(mContext, R.string.prompt_input_phone);
            	 return;
			}
    		
            String newPsw = newPswET.getText().toString();
            if (TextUtils.isEmpty(newPsw)) {
                MessageUtil.alertMessage(mContext, R.string.prompt_input_new_password);
                return;
            }
            
            String confirmNewPsw = confirmPswET.getText().toString();
            if (TextUtils.isEmpty(confirmNewPsw)) {
                MessageUtil.alertMessage(mContext, R.string.prompt_input_new_password_again);
                return;
            }
            
            if (!newPsw.equals(confirmNewPsw)) {
                MessageUtil.alertMessage(mContext, R.string.prompt_input_diffrient_password);
                newPswET.setText("");
                confirmPswET.setText("");
                return;
            }
            
            if(newPsw.length()>20){
                MessageUtil.alertMessage(mContext, R.string.prompt_input_new_password_len);
                return;
            }
            
            String codeString=phonecodeET.getText().toString();

            if (TextUtils.isEmpty(codeString)) {
				Toast.makeText(getApplicationContext(), "校验码不能为空", Toast.LENGTH_LONG).show();
            	 return;
			}
    		
            submid=7;
            
            modifyPassword(newPsw,codeString,submid,phone);
        }
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
    private void modifyPassword(String newPsw, String codeString,int submid,String phone) {
    	
		dialog.setMessage("重置登录密码中...");
		dialog.show();
		ZganLoginService.toGetServerData(7, new String[]{codeString,phone,newPsw}, handler);
    	
	}
    
    @SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dialog.dismiss();
			
			if(msg.what==1){
				Frame frame=(Frame)msg.obj;
				
				if(frame!=null){
					if(frame.mainCmd==0x01 && frame.subCmd==7){
						if(!TextUtils.isEmpty(frame.strData) && frame.strData.equals("0")){							
							MessageUtil.alertMessage(mContext, R.string.ret_success);
							RetrievePassword.this.finish();						
						}else{
							MessageUtil.alertMessage(mContext, R.string.network_not_response);
						}

					}else if(frame.mainCmd==0x01 && frame.subCmd==8){
						if(!TextUtils.isEmpty(frame.strData) && frame.strData.equals("0")){
							timeCount.start();
							MessageUtil.alertMessage(mContext, R.string.sms_success);
						}else{
							MessageUtil.alertMessage(mContext, R.string.network_not_response);
						}
					}
				}
				
				
			}else{
				MessageUtil.alertMessage(mContext,getString(R.string.network_not_response) );
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
