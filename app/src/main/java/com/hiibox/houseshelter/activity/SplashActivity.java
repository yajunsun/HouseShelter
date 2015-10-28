package com.hiibox.houseshelter.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.core.GlobalUtil;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.util.MessageUtil;
import com.hiibox.houseshelter.util.ScreenUtil;
import com.zgan.file.ZganFileService;
import com.zgan.jtws.ZganJTWSService;
import com.zgan.login.ZganLoginService;
import com.zgan.youbao.R;

@SuppressLint("HandlerLeak")
public class SplashActivity extends ShaerlocActivity {

	private int queryIndex = 0;
	private Thread t1;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {
				Frame frame = (Frame) msg.obj;

				if (frame.mainCmd == 0x01 && frame.subCmd == 4) {
					ZganJTWSService.toStartJTWSService(SplashActivity.this);
					ZganFileService.toStartFileService(SplashActivity.this);

					if (queryIndex == 1 || queryIndex == 2) {
						Intent intent = new Intent(mContext,
								ImprintingActivity.class);
						intent.putExtra("queryIndex", queryIndex);
						startActivity(intent);
						MianActivity.getScreenManager().exitActivity(mActivity);
					} else {
						startActivity(new Intent(mContext,
								HomepageActivity.class));
						// startActivity(new
						// Intent(mContext,testhomeactivity.class));
						MianActivity.getScreenManager().exitActivity(mActivity);
					}
				}

			} else if (msg.what == 2) {

				t1.interrupt();
				startActivity(new Intent(mActivity, LoginActivity.class));
				MianActivity.getScreenManager().exitActivity(mActivity);
			} else {
				MessageUtil.alertMessage(mContext, R.string.network_error);
				startActivity(new Intent(mActivity, LoginActivity.class));
				MianActivity.getScreenManager().exitActivity(mActivity);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 启动登录服务线程
		ZganLoginService.toStartLoginService(mContext);

		queryIndex = getIntent().getIntExtra("queryIndex", 0);
		setContentView(R.layout.activity_splash_layout);

		GlobalUtil.mScreenWidth = ScreenUtil.getScreenWidth(mActivity);
		GlobalUtil.mScreenHeight = ScreenUtil.getScreenHeight(mActivity);

		// 检查网络是否正常
		if (!ZganLoginService.isNetworkAvailable(mContext)) {
			MessageUtil.alertMessage(mContext, R.string.sys_network_error1);
			handler.sendEmptyMessage(0);
		} else if (!ZganLoginService.toAutoUserLogin(handler)) {
			// 判断自动登录

			Thread_TimerToActivity tt = new Thread_TimerToActivity();
			t1 = new Thread(tt);
			t1.start();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onStop() {
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(null);
	}

	// 判断数据发送超时
	private class Thread_TimerToActivity implements Runnable {

		private boolean isRun = true;

		@Override
		public void run() {
			// TODO Auto-generated method stub

			while (isRun) {

				try {
					Thread.sleep(500);

					isRun = false;
					handler.sendEmptyMessage(2);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

}
