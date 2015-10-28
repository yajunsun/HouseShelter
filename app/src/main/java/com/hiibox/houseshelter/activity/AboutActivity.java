package com.hiibox.houseshelter.activity;

import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hiibox.houseshelter.ShaerlocActivity;
import com.zgan.jtws.ZganJTWSService;
import com.zgan.jtws.ZganJTWSServiceTools;
import com.zgan.login.ZganLoginService;
import com.zgan.youbao.R;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.core.GlobalUtil;
import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.util.LocationUtil;
import com.hiibox.houseshelter.util.MessageUtil;

/**
 * 关于界面
 * 
 * @author Administrator
 * 
 */
public class AboutActivity extends ShaerlocActivity {

	@ViewInject(id = R.id.root_layout)
	RelativeLayout rootLayout;
	@ViewInject(id = R.id.progress_bar)
	ProgressBar progressBar;
	@ViewInject(id = R.id.back_iv, click = "onClick")
	ImageView backIV;
	// about_shebei
	TextView textView;

	private ProgressDialog dialog = null;
	AlertDialog.Builder builder = null;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				Frame frame = (Frame) msg.obj;

				if (frame.subCmd == 96) {
					String ret = frame.strData;
					if (frame.strData != null
							&& !frame.strData.equals("")) {
						String data[] = ret.split("\t");
						if (data[0].equals("0")) {

							textView.setText("设备版本：" + data[1]);
						} else {
							textView.setText(getString(R.string.about_shebeivartxt));
						}
					}

				}
			}
		}

	};

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		builder = new AlertDialog.Builder(mActivity);

		dialog = new ProgressDialog(mActivity);
		dialog.setMessage(getString(R.string.dialog_message_loading_data));
		dialog.setCancelable(true);

		setContentView(R.layout.activity_about_layout);
		textView = (TextView) findViewById(R.id.about_shebei);

		if(ZganJTWSServiceTools.isConnect){
			sendRequest(96, null, null);
		}else{
			MessageUtil.alertMessage(mContext, R.string.sys_network_error);
		}
		

	}

	public void onClick(View v) {
		MianActivity.getScreenManager().exitActivity(mActivity);
	}

	private void sendRequest(final int subCmd, String nickname,
			String emergencyTel) {
		if (LocationUtil.checkNetWork(mContext).endsWith(
				GlobalUtil.NETWORK_NONE)) {
			MessageUtil.alertMessage(mContext, R.string.sys_network_error);

			return;
		}

		
		if (subCmd == 96) {
			ZganJTWSService.toGetServerData(96, ZganLoginService.getUserName(), handler);

		}	

	}
}
