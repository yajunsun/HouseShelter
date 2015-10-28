package com.hiibox.houseshelter.view;

import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.util.ImageOperation;
import com.hiibox.houseshelter.util.MessageUtil;
import com.zgan.jtws.ZganJTWSService;
import com.zgan.login.ZganLoginService;
import com.zgan.youbao.R;

public class ChangeRFidNameDialog extends ShaerlocActivity {
	@ViewInject(id = R.id.root_layout)	LinearLayout rootLayout;	
	@ViewInject(id = R.id.new_rfid_name)	EditText editText;
	@ViewInject(id = R.id.back_iv, click = "onClick") ImageView backIV;
	@ViewInject(id = R.id.save_btn, click = "onClick") Button confirmTV;
    @ViewInject(id = R.id.progress_bar) ProgressBar progressBar;

	String str = null;

	@Override
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_rfid_name);
		if (getIntent().getExtras().getString("RFID").toString() != null
				&& !getIntent().getExtras().getString("RFID").toString()
						.equals("")) {
			str = getIntent().getExtras().getString("RFID").toString();
			Log.e("str", str);
		}

	}

	public void onClick(View v) {
		if (v == backIV) {
			MianActivity.getScreenManager().exitActivity(mActivity);
		} else if (v == confirmTV) {
			String new_name = editText.getText().toString();
			if (TextUtils.isEmpty(new_name)) {
				Toast.makeText(mContext, "新昵称不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			progressBar.setVisibility(View.VISIBLE);

			sendOrder(str, new_name);

		}
	}

	private void sendOrder(final String cardNum, final String name) {
		ZganJTWSService.toGetServerData(99,new String[]{ZganLoginService.getUserName(),cardNum,name}, handler);
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int what = msg.what;
			progressBar.setVisibility(View.INVISIBLE);
			
			if(what==1){
				Frame frame = (Frame) msg.obj;
				
				if (frame.subCmd == 99) {					
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

	@Override
	protected void onResume() {
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		BitmapDrawable bitmapDrawable = new BitmapDrawable(
				ImageOperation.readBitMap(mContext, R.drawable.bg_app));
		rootLayout.setBackgroundDrawable(bitmapDrawable);
	}
}
