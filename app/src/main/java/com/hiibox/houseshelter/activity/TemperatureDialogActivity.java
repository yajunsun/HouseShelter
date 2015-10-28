package com.hiibox.houseshelter.activity;

import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.core.GlobalUtil;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.util.DateUtil;
import com.hiibox.houseshelter.util.MessageUtil;
import com.hiibox.houseshelter.view.TemperatureTrendView;
import com.zgan.youbao.R;

public class TemperatureDialogActivity extends ShaerlocActivity {

	@ViewInject(id = R.id.root_layout)
	LinearLayout rootLayout;
	@ViewInject(id = R.id.trend_content)
	LinearLayout trendLayout;
	@ViewInject(id = R.id.current_date_tv)
	TextView currDateTV;
	@ViewInject(id = R.id.start_time_tv)
	TextView startTimeTV;
	@ViewInject(id = R.id.end_time_tv)
	TextView endTimeTV;
	@ViewInject(id = R.id.current_temperature_tv)
	TextView currTemperatureTV;
	@ViewInject(id = R.id.close_iv, click = "onClick")
	ImageView backIV;

	private int trendLayoutHeight = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_temperature_dialog_layout);

		currDateTV.setText(DateUtil.changeDateToYmd(DateUtil.getcurrentDay()));
		endTimeTV.setText(DateUtil.getTime());
		startTimeTV.setText(DateUtil.getLastHalfAnHour());
		sendRequest();

		ViewTreeObserver hvto = trendLayout.getViewTreeObserver();
		hvto.addOnPreDrawListener(new OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				if (trendLayoutHeight == 0) {
					trendLayoutHeight = trendLayout.getMeasuredHeight();
					return false;
				}
				return true;
			}
		});

	}

	private void sendRequest() {

	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				int[] temp = (int[]) msg.obj;
				TemperatureTrendView trendView = new TemperatureTrendView(
						mContext, GlobalUtil.mScreenWidth - 100,
						trendLayoutHeight, temp);
				trendLayout.addView(trendView);
				currTemperatureTV.setText(temp[0]
						+ getString(R.string.temperature_unit));
			} else if (msg.what == 0) {
				MessageUtil.alertMessage(mContext, R.string.network_timeout);
			} else if (msg.what == 2) {
				MessageUtil.alertMessage(mContext, R.string.no_data);
			}
		}
	};

	public void onClick(View v) {
		if (v == backIV) {
			MianActivity.getScreenManager().exitActivity(mActivity);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(null);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// modified by yajunsun 20150929
			// startActivity(new Intent(mContext, HomepageActivity.class));
			MianActivity.getScreenManager().exitActivity(mActivity);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
