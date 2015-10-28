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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.net.SpliteUtil;
import com.hiibox.houseshelter.util.MessageUtil;
import com.zgan.jtws.ZganJTWSServiceTools;
import com.zgan.login.ZganLoginService;
import com.zgan.youbao.R;

/**
 * 添加地址  
 * @author Administrator
 *
 */
  
  
public class AddAddressActivity extends ShaerlocActivity {

    @ViewInject(id = R.id.back_iv, click = "onClick") ImageView backIV;
    @ViewInject(id = R.id.submit_tv, click = "onClick") TextView submitTV;
    @ViewInject(id = R.id.two_dimension_code_iv, click = "onClick") ImageView codeIV;
    //@ViewInject(id = R.id.address_et) EditText addressET;
    @ViewInject(id = R.id.device_code_et) EditText deviceCodeET;
    @ViewInject(id = R.id.auth_code_et) EditText authCodeET;

    private String address = null;
    private String deviceCode = null;
    private String authCode = null;
    private ProgressDialog dialog = null;
    
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dialog.dismiss();	
			
			if(msg.what==1){
				 Frame frame = (Frame) msg.obj;
				
				 if(frame!=null){
					 if (frame.mainCmd==0x01 && frame.subCmd == 2) {
						 if(!TextUtils.isEmpty(frame.strData) && frame.strData.equals("0")){
				            MessageUtil.alertMessage(mContext, R.string.add_address_success);
							Intent data = new Intent();
				            data.putExtra("address", address);
				            data.putExtra("deviceNumber", deviceCode);
				            data.putExtra("authCode", authCode);
				            setResult(RESULT_OK, data);
				            MessageUtil.alertMessage(mContext, R.string.add_address_success);
				            AddAddressActivity.this.finish();
						 }else{
							 String res = SpliteUtil.getResult(frame.strData);
							 
							 MessageUtil.alertMessage(mContext, getString(R.string.commit_failure)+res);
						 }
					 }
				 }
				 
			}else{
				MessageUtil.alertMessage(mContext, R.string.network_timeout);
			}
			
//			if (null == msg.obj) {
//            	return;
//            }
//            Frame[] frame = (Frame[]) msg.obj;
//            if (null == frame[1]) {
//            	return;
//            }
//            dialog.dismiss();
//			if (msg.what == 0) {
//				if (frame[1].subCmd == 2) {
//					if (frame[1].strData.equals("0")) {
//						Intent data = new Intent();
//			            data.putExtra("address", address);
//			            data.putExtra("deviceNumber", deviceCode);
//			            data.putExtra("authCode", authCode);
//			            setResult(RESULT_OK, data);
//			            MessageUtil.alertMessage(mContext, R.string.add_address_success);
//			            AddAddressActivity.this.finish();
//					} else {
//						String res = SpliteUtil.getResult(frame[1].strData);
//						MessageUtil.alertMessage(mContext, getString(R.string.commit_failure)+res);
//					}
//				}
//			} else if (msg.what == -1) {
//				MessageUtil.alertMessage(mContext, R.string.network_timeout);
//			}
			
			
		}
	};
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address_layout);
        
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(getString(R.string.upload_data));
    }
    
    public void onClick(View v) {
        if (v == backIV) {
            MianActivity.getScreenManager().exitActivity(mActivity);
        } else if (v == codeIV) {
            startActivityForResult(new Intent(this, ScanCodeActivity.class), 0x102);
        } else if (v == submitTV) {
            address = "新增设备";

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
            
            dialog.show(); 
            if(ZganJTWSServiceTools.isConnect){
            ZganLoginService.toGetServerData(2, 
            		new String[]{ZganLoginService.getUserName(), deviceCode, authCode, address},handler);
            }else {
            	MessageUtil.alertMessage(mContext, R.string.sys_network_error);
			}
            
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x102 && resultCode == RESULT_OK) {
            deviceCodeET.setText(data.getStringExtra("code"));
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
    
    @Override
    protected void onResume() {
        super.onResume();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    
}
