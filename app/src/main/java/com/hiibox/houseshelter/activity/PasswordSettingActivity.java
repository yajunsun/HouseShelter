package com.hiibox.houseshelter.activity;

import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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
import android.widget.ProgressBar;

import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.util.ImageOperation;
import com.hiibox.houseshelter.util.MessageUtil;
import com.zgan.jtws.ZganJTWSServiceTools;
import com.zgan.login.ZganLoginService;
import com.zgan.youbao.R;

  
@SuppressLint("HandlerLeak")
public class PasswordSettingActivity extends ShaerlocActivity {

    @ViewInject(id = R.id.root_layout) LinearLayout rootLayout;
    @ViewInject(id = R.id.back_iv, click = "onClick") ImageView backIV;
    @ViewInject(id = R.id.save_btn, click = "onClick") Button confirmTV;
    @ViewInject(id = R.id.old_password_et) EditText oldPswET;
    @ViewInject(id = R.id.new_password_et) EditText newPswET;
    @ViewInject(id = R.id.confirm_new_password_et) EditText confirmPswET;
    @ViewInject(id = R.id.progress_bar) ProgressBar progressBar;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_setting_layout);

    }
    
    public void onClick(View v) {
        if (v == backIV) {
            MianActivity.getScreenManager().exitActivity(mActivity);
        }else if (v == confirmTV) {
            String oldPsw = oldPswET.getText().toString();
 
            int submid=0;

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

            modifyPassword(oldPsw, newPsw,"");
        }
    }

    private void modifyPassword(String oldPsw, final String newPsw, String codeString) {
    	if(ZganJTWSServiceTools.isConnect){
    		progressBar.setVisibility(View.VISIBLE);
    		
    		ZganLoginService.toGetServerData(9, new String[]{ZganLoginService.getUserName(),oldPsw,newPsw}, handler);

    	}else{
    		MessageUtil.alertMessage(mContext, R.string.sys_network_error);
    	}
	}
    
    @SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			progressBar.setVisibility(View.GONE);
			
			if(msg.what==1){
				Frame frame=(Frame)msg.obj;
				
				if(frame!=null && !TextUtils.isEmpty(frame.strData) && frame.strData.equals("0")){
					MessageUtil.alertMessage(mContext, R.string.operate_success);
					MianActivity.getScreenManager().exitActivity(mActivity);
				}else{
					MessageUtil.alertMessage(mContext,getString(R.string.change_failed));
				}
				
			}else{
				MessageUtil.alertMessage(mContext, R.string.network_not_response);
			}
			
//			if (msg.what == 0) {
//				MessageUtil.alertMessage(mContext, R.string.network_timeout);
//			} else if (msg.what == 1) {
//				MessageUtil.alertMessage(mContext, R.string.network_not_response);
//			} else if (msg.what == 2) {
//				MessageUtil.alertMessage(mContext, R.string.receive_mistake_info);
//			} else if (msg.what == 3) {
//				MessageUtil.alertMessage(mContext, R.string.operate_success);
//				MianActivity.getScreenManager().exitActivity(mActivity);
//			} else if (msg.what == 5) {
//				progressBar.setVisibility(View.INVISIBLE);
//				String reason = (String) msg.obj;
//				reason=TheErrorCode.compare(reason);
//				MessageUtil.alertMessage(mContext,
//						getString(R.string.change_failed));
//			}
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
