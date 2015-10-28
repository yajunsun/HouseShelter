package com.hiibox.houseshelter.activity;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.adapter.AdvertisementAdapter;
import com.hiibox.houseshelter.core.GlobalUtil;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.net.CommandResult;
import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.net.SpliteUtil;
import com.hiibox.houseshelter.util.ImageOperation;
import com.hiibox.houseshelter.util.LocationUtil;
import com.hiibox.houseshelter.util.MessageUtil;
import com.hiibox.houseshelter.util.PreferenceUtil;
import com.hiibox.houseshelter.util.ScreenUtil;
import com.zgan.youbao.R;

public class SmartAppActivity extends ShaerlocActivity {

	@ViewInject(id = R.id.root_layout)
	LinearLayout rootLayout;
	@ViewInject(id = R.id.back_iv, click = "onClick")
	ImageView backIV;
	@ViewInject(id = R.id.advertisement_pager)
	ViewPager advertisementPager;
	@ViewInject(id = R.id.dot_layout)
	LinearLayout dotLayout;
	@ViewInject(id = R.id.advertisement_layout)
	RelativeLayout adsLayout;
	// @ViewInject(id = R.id.remote_control_btn, click = "onClick") Button
	// remoteControlBtn;
	// @ViewInject(id = R.id.service_center_btn, click = "onClick") Button
	// serviceCenterBtn;
	@ViewInject(id = R.id.remote_control_rel, click = "onClick")
	RelativeLayout remoteControlRel;
	@ViewInject(id = R.id.service_center_rel, click = "onClick")
	RelativeLayout serviceCenterRel;

	private AdvertisementAdapter adapter = null;
	private List<String> list = null;
	private ImageView[] imageViews;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		list = new ArrayList<String>();
		setContentView(R.layout.activity_smart_app_layout);
		/*
		 * if (isAppInstalled("com.zgan.yckz")) {
		 * remoteControlBtn.setBackgroundResource(R.drawable.start_btn); } if
		 * (isAppInstalled("com.zgan.community")) {
		 * serviceCenterBtn.setBackgroundResource(R.drawable.start_btn); }
		 */

		// sendRequest(83, 0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setLargeScreenParams();
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

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_iv:
			MianActivity.getScreenManager().exitActivity(mActivity);
			break;
		/*
		 * case R.id.remote_control_btn: if (isAppInstalled("com.zgan.yckz")) {
		 * Intent remoteIntent = new Intent(Intent.ACTION_MAIN);
		 * remoteIntent.setComponent(new ComponentName("com.zgan.yckz",
		 * "com.zgan.yckz.Welcome")); startActivity(remoteIntent); } else {
		 * sendRequest(86, 11); } break; case R.id.service_center_btn: if
		 * (isAppInstalled("com.zgan.community")) { Intent remoteIntent = new
		 * Intent(Intent.ACTION_MAIN); remoteIntent.setComponent(new
		 * ComponentName("com.zgan.community",
		 * "com.zgan.community.Advertising")); startActivity(remoteIntent); }
		 * else { sendRequest(86, 15); } break;
		 */

		default:
			break;
		}
	}

	private boolean isAppInstalled(String packageName) {
		PackageManager packageManager = getPackageManager();

		List<PackageInfo> mPackageInfo = packageManager.getInstalledPackages(0);
		boolean flag = false;
		if (mPackageInfo != null) {
			String tempName = null;
			for (int i = 0; i < mPackageInfo.size(); i++) {
				tempName = mPackageInfo.get(i).packageName;
				if (tempName != null && tempName.equals(packageName)) {

					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	private void setLargeScreenParams() {
		int screenHeight = ScreenUtil.getScreenHeight(mActivity);
		if (screenHeight > 854 && screenHeight <= 1280) {
			LinearLayout.LayoutParams adsParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, 550);
			adsParams.topMargin = -10;
			adsLayout.setLayoutParams(adsParams);
		}
	}

	private void sendRequest(int subCmd, int platform) {
		if (LocationUtil.checkNetWork(mContext).endsWith(
				GlobalUtil.NETWORK_NONE)) {
			MessageUtil.alertMessage(mContext, R.string.sys_network_error);
			startActivity(new Intent("android.settings.WIRELESS_SETTINGS"));
			// HomepageActivity.WLAN_FLAG=0;
			return;
		}
		/*
		 * if (HomepageActivity.WLAN_FLAG==0) { return; }
		 */
		String phone = PreferenceUtil.getInstance(getApplicationContext())
				.getString("phone", null);

	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (null == msg.obj) {
				return;
			}
			Frame[] frame = (Frame[]) msg.obj;
			if (null == frame[0]) {
				return;
			}
			int subCmd = frame[0].subCmd;
			if (msg.what == 0) {
				if (null == frame[1]) {
					return;
				}
				String ret = frame[1].strData;

				if (subCmd == 83) {
					if (ret.startsWith("1")) {
						MessageUtil.alertMessage(mContext, R.string.no_data);
						return;
					}
					CommandResult result = new CommandResult();
					result.praseAdsUrl(frame[1]);
					list = result.getList();
					if (null != list && list.size() > 0) {
						getData();
					}
				} else if (subCmd == 86) {
					if (ret.startsWith("1")) {
						MessageUtil.alertMessage(mContext,
								R.string.get_data_failure);
						return;
					}
					String url = SpliteUtil.getResult(ret);
					if (!TextUtils.isEmpty(url)) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(url));
						startActivity(intent);
					} else {
						MessageUtil.alertMessage(mContext,
								R.string.get_data_failure);
					}
				}
			} else if (msg.what == -1) {
				MessageUtil.alertMessage(mContext, R.string.network_timeout);
			}
		}
	};

	private void getData() {
		adapter = new AdvertisementAdapter(mContext, list, finalBitmap);
		advertisementPager.setAdapter(adapter);
		int len = list.size();
		if (len > 0) {
			imageViews = new ImageView[len];
			for (int i = 0; i < len; i++) {
				ImageView iv = new ImageView(mContext);
				iv.setLayoutParams(new LayoutParams(15, 15));
				iv.setPadding(0, 0, 10, 0);
				imageViews[i] = iv;
				if (i == 0) {
					iv.setBackgroundResource(R.drawable.dot_blue);
				} else {
					iv.setBackgroundResource(R.drawable.dot_white);
				}
				dotLayout.addView(imageViews[i]);
			}
		}
		advertisementPager.setOnPageChangeListener(new PageChangeOnListener());
	}

	class PageChangeOnListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			for (int i = 0; i < imageViews.length; i++) {
				if (i == position) {
					imageViews[i].setBackgroundResource(R.drawable.dot_blue);
				} else {
					imageViews[i].setBackgroundResource(R.drawable.dot_white);
				}
			}
		}

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
