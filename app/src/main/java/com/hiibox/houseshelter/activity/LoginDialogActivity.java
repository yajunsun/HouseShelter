package com.hiibox.houseshelter.activity;

import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.MyApplication;
import com.zgan.file.ZganFileService;
import com.zgan.jtws.ZganJTWSService;
import com.zgan.login.ZganLoginService;
import com.zgan.youbao.R;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.util.LocationUtil;
import com.hiibox.houseshelter.util.MessageUtil;
import com.hiibox.houseshelter.util.PreferenceUtil;

public class LoginDialogActivity extends ShaerlocActivity {

    public  String phone = null;
    public  String password = null;
	private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            
//            if(msg.what==1){
//				ZganJTWSService.toStartJTWSService(LoginDialogActivity.this, phone);
//				ZganFileService.toStartFileService(LoginDialogActivity.this, phone);
//				startActivity(new Intent(LoginDialogActivity.this, HomepageActivity.class));
//				MianActivity.getScreenManager().exitAllActivityExceptOne();
//            }else{
//            	MessageUtil.alertMessage(mContext, R.string.receive_server_info_failed);
//            	MianActivity.getScreenManager().exitActivity(mActivity);
//            }
            
            
//            switch (msg.what) {
//                case 2:
//                    MyApplication.phone = phone;
//                    MyApplication.password = password;
//
//                    PreferenceUtil.getInstance(mContext).saveString("phone", phone);
//                    PreferenceUtil.getInstance(mContext).saveString("password", password);
//
//                    MyApplication.initTcpManager();
//

//

//                    break;
//                case 3:
//
//                    MessageUtil.alertMessage(mContext, R.string.receive_server_info_failed);
//                    MianActivity.getScreenManager().exitActivity(mActivity);
//                    break;
//                case 4:
//                    MessageUtil.alertMessage(mContext, R.string.network_error);
//                    MianActivity.getScreenManager().exitActivity(mActivity);
//                    break;
//                case 5:
//                    MessageUtil.alertMessage(mContext, R.string.network_not_response);
//                    MianActivity.getScreenManager().exitActivity(mActivity);
//                    break;
//                case 6:
//                    MessageUtil.alertMessage(mContext, R.string.network_timeout);
//                    MianActivity.getScreenManager().exitActivity(mActivity);
//                    break;
//	            case 1:	            	
//					MessageUtil.alertMessage(mContext, getString(R.string.login_failed)+msg.obj);
//                    MianActivity.getScreenManager().exitActivity(mActivity);
//                    break;
//            }
        }
    };
    public boolean onKeyDown(int keyCode, KeyEvent event) {        
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_dialog_layout);
        phone = LoginActivity.phone;
        password = LoginActivity.password;

        String imei = LocationUtil.getDrivenToken(mContext,phone);
        
       // ZganLoginService.toUserLogin(phone, password, imei, handler);
        
//        MyApplication.initTcpManager();
//        try {
//            MyApplication.tcpManager.login(phone, password, imei, handler);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        

    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(null);
    }
}
