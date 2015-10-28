package com.hiibox.houseshelter.activity;

import java.io.File;
import java.text.DecimalFormat;

import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zgan.jtws.ZganJTWSService;
import com.zgan.jtws.ZganJTWSServiceTools;
import com.zgan.login.ZganLoginService;
import com.zgan.youbao.R;
import com.hiibox.houseshelter.MyApplication;
import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.downtools.DownloadFileService;
import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.util.MessageUtil;

public class VersionUpdate extends ShaerlocActivity {

	@ViewInject(id = R.id.root_layout)
	RelativeLayout rootLayout;
	@ViewInject(id = R.id.back_iv, click = "onClick")
	ImageView backIV;
	TextView textView;

	LinearLayout linearLayout;
	TextView progressTextView;

	ProgressBar progressBar;
	public  int newversion;
	AlertDialog.Builder builder = null;
	Context context;
	public Boolean  FLAG=false;
	public IntentFilter filter=null;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				Frame frame = (Frame) msg.obj;
				if (null == frame) {
					return;
				}

				if (frame.subCmd == 86) {
					String ret = frame.strData;
					if (frame.strData != null && !frame.strData.equals("")) {
						String data[] = ret.split("\t");
						if (data[0].equals("0")) {
							 newversion = Integer.parseInt(data[1].replace(
									".", ""));
							String oldverString = getString(R.string.app_var)
									.replaceAll("\\D+", "")
									.replaceAll("\r", "").replaceAll("\n", "")
									.replace("v", "").replace(".", "");

							int oldversion = Integer.parseInt(oldverString
									.trim());
							final String url = data[2];

							if (newversion > oldversion) {
								builder.setMessage(getString(R.string.updat_newvar));
								builder.setPositiveButton(
										"确定",
										new android.content.DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub

												Intent intent = new Intent(
														getApplicationContext(),
														DownloadFileService.class);
												intent.putExtra("URL", url);
												intent.putExtra("Version", newversion);
												startService(intent);
												
												linearLayout.setVisibility(View.VISIBLE);
												linearLayout.setOnClickListener(listener);

												// 定义一个接收器
												 filter = new IntentFilter();
												filter.addAction(DownloadFileService.BroadAction);
												VersionUpdate.this.registerReceiver(receiver, filter);
												
												
												Toast.makeText(mContext,
														"柚保正在后台下载，请稍后...",
														Toast.LENGTH_LONG)
														.show();

												// VersionUpdate.this.finish();

											}
										});
								builder.setNegativeButton(
										"取消",
										new android.content.DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub
											}
										});
								builder.create();
								builder.show();

							} else {
								MessageUtil.alertMessage(mContext,
										R.string.var_new);

							}

						} else {
							MessageUtil.alertMessage(mContext,
									R.string.var_new_fail);

						}
					}

				}
			}
			if (msg.what == -1) {

			}
			if (msg.what == -2) {

			}
			if (msg.what == -3) {

			}
		}

	};

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		builder = new AlertDialog.Builder(mActivity);
		setContentView(R.layout.activity_version_updat);
		context=this;

		linearLayout = (LinearLayout) findViewById(R.id.downloadview);
		progressTextView = (TextView) findViewById(R.id.progressTv);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		linearLayout.setOnClickListener(listener);

		if (getIntent().getExtras() != null) {
			Log.i("VersionUpdate", ""+getIntent().getExtras().getString("URL"));
			Intent intent = new Intent(getApplicationContext(),
					DownloadFileService.class);
			intent.putExtra("URL", getIntent().getExtras().getString("URL"));
			intent.putExtra("Version", getIntent().getExtras().getInt("Version"));
			
			startService(intent);
			
			
			
			linearLayout.setVisibility(View.VISIBLE);

			// 定义一个接收器
			filter = new IntentFilter();
			filter.addAction(DownloadFileService.BroadAction);
			VersionUpdate.this.registerReceiver(receiver, filter);

			Toast.makeText(mContext, "柚保正在下载，请稍后...", Toast.LENGTH_LONG).show();

		} else {
			sendRequest(86, null, null);
		}
	}

	OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.downloadview:
				if (FLAG) {
					installApk(Environment.getExternalStorageDirectory() + "/Zgan"
							+ "/YouBao"+newversion+".apk");
				}
				

				break;

			default:
				break;
			}
		}
	};
	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (DownloadFileService.BroadAction.equals(intent.getAction())) {
				int intTypeId = 0;

				intTypeId = intent.getIntExtra("Progress", 0);

				if (intTypeId == -1) {
					// 下载失败
					
					progressTextView.setText("下载失败");
					FLAG=false;

					// handler.sendEmptyMessage(-1);

				} else if (intTypeId == 100) {
					FLAG=true;
					// 下载完成
					progressBar.setProgress(100);
					progressTextView.setText("下载完成");
					
					// handler.sendEmptyMessage(-2);

					//installApk(Environment.getExternalStorageDirectory()
							//+ "/Zgan" + "/YouBao.apk");

				} else {
					FLAG=false;

					// 下载中
					DecimalFormat format = new DecimalFormat("0.00");
					String progress = format.format(intTypeId);

					progressBar.setProgress(intTypeId);
					progressTextView.setText("已下载" + progress + "%");
					// handler.sendEmptyMessage(-3);

				}

			}
		}

	};

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_iv:
			MianActivity.getScreenManager().exitActivity(mActivity);
			break;

		default:
			break;
		}
	
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (filter!=null) {
			this.unregisterReceiver(receiver);
		}
		super.onDestroy();
	}
	protected void installApk(String filename) {
		// TODO Auto-generated method stub
		File file = new File(filename);
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW); // 浏览网页的Action(动作)
		String type = "application/vnd.android.package-archive";
		intent.setDataAndType(Uri.fromFile(file), type); // 设置数据类型
		startActivity(intent);
	}

	private void sendRequest(final int subCmd, String nickname,
			String emergencyTel) {
		if (!ZganJTWSServiceTools.isConnect) {
			MessageUtil.alertMessage(mContext, R.string.sys_network_error);
			return;
		}
		if (subCmd == 86) {
			ZganJTWSService.toGetServerData(86,
					new String[] { ZganLoginService.getUserName() + "\t4\t0" },
					handler, 2);
		}

	}
}
