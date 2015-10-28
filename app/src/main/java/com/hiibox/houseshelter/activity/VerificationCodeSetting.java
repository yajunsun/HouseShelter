
package com.hiibox.houseshelter.activity;

import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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
import com.zgan.jtws.ZganJTWSService;
import com.zgan.jtws.ZganJTWSServiceTools;
import com.zgan.login.ZganLoginService;
import com.zgan.youbao.R;

@SuppressLint("HandlerLeak")
public class VerificationCodeSetting extends ShaerlocActivity {

    @ViewInject(id = R.id.root_layout) LinearLayout rootLayout;
    @ViewInject(id = R.id.back_iv, click = "onClick") ImageView backIV;
    @ViewInject(id = R.id.old_password_et) EditText oldPswET;
    @ViewInject(id = R.id.new_password_et) EditText newPswET;
    @ViewInject(id = R.id.confirm_new_password_et) EditText confirmPswET;
    @ViewInject(id = R.id.progress_bar) ProgressBar progressBar;
    @ViewInject(id = R.id.phone_code)	EditText phonecodeET;
  	@ViewInject(id = R.id.rest_btn,click = "onClick")	Button restcodeET;
  	@ViewInject(id = R.id.save_btn,click = "onClick")	Button btn_save;
  	
  	String time=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificationcode_setting_layout);
    }
    
    public void onClick(View v) {
        if (v == backIV) {
            MianActivity.getScreenManager().exitActivity(mActivity);
        }else if (v==restcodeET) {
        	Intent intent=new Intent(VerificationCodeSetting.this,ResetVerifictaicionCode.class);
        	startActivity(intent);
        	MianActivity.getScreenManager().exitActivity(mActivity);
		} else if (v == btn_save) {
            String oldPsw = oldPswET.getText().toString();
            String newPsw = newPswET.getText().toString();
            
            if (TextUtils.isEmpty(oldPsw)){
                MessageUtil.alertMessage(mContext, R.string.device_err_revisepwd_old);
                return;
            }
            
            if (TextUtils.isEmpty(newPsw)) {
                MessageUtil.alertMessage(mContext, R.string.device_err_revisepwd);
                return;
            }
            
            String confirmNewPsw = confirmPswET.getText().toString();
            if (TextUtils.isEmpty(confirmNewPsw)) {
                MessageUtil.alertMessage(mContext, R.string.device_err_revisepwd_confinrm);
                return;
            }
            
            if (!newPsw.equals(confirmNewPsw)) {
                MessageUtil.alertMessage(mContext, R.string.device_err_revisepwd_confinrm_1);
                newPswET.setText("");
                confirmPswET.setText("");
                return;
            }
            
            if(confirmNewPsw.length()>20){
                MessageUtil.alertMessage(mContext, R.string.device_err_revisepwd_len);
                newPswET.setText("");
                confirmPswET.setText("");
                return;
            }

            modifyPassword(oldPsw, newPsw);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
    	progressBar.setVisibility(View.INVISIBLE);
    	return super.onKeyDown(keyCode, event);
    }
    private void modifyPassword(String oldPsw,String newPsw) {
    	if (!ZganJTWSServiceTools.isConnect) {
			MessageUtil.alertMessage(mContext, R.string.sys_network_error);
			return;
		}
		progressBar.setVisibility(View.VISIBLE);
		
		ZganJTWSService.toGetServerData(92,new String[]{ZganLoginService.getUserName(),oldPsw,newPsw}, handler);
	}
    
    @SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			progressBar.setVisibility(View.GONE);
			
			if(msg.what==1){
				Frame frame = (Frame) msg.obj;
				
				if (frame.subCmd == 92) {					
					String src=frame.strData;
					
					if(src!=null && !src.equals("") && src.equals("0")){
						MessageUtil.alertMessage(mContext, R.string.operate_success);
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
