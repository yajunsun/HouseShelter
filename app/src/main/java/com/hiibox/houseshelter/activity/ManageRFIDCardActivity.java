package com.hiibox.houseshelter.activity;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hiibox.houseshelter.MyApplication;
import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.adapter.RFIDCardAdapter;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.net.MembersInfoResult;
import com.hiibox.houseshelter.net.SpliteUtil;
import com.hiibox.houseshelter.util.ImageOperation;
import com.hiibox.houseshelter.util.MessageUtil;
import com.zgan.jtws.ZganJTWSService;
import com.zgan.jtws.ZganJTWSServiceTools;
import com.zgan.login.ZganLoginService;
import com.zgan.youbao.R;

/**
 * @Description RFID卡维护
 * @Author FR
 * @Create Date 2013-10-31 上午9:38:51
 * @Modified By
 * @Modified Date
 * @Modified Description
 */
public class ManageRFIDCardActivity extends ShaerlocActivity {

	@ViewInject(id = R.id.root_layout)
	RelativeLayout rootLayout;
	@ViewInject(id = R.id.back_iv, click = "onClick")
	ImageView backIV;
	@ViewInject(id = R.id.manage_address_tv)
	TextView titleTV;
	@ViewInject(id = R.id.add_address_iv, click = "onClick")
	ImageView addCardIV;
	@ViewInject(id = R.id.address_list, itemClick = "onItemClick")
	ListView rfidCardLV;
	@ViewInject(id = R.id.progress_bar)
	ProgressBar progressBar;

	private RFIDCardAdapter adapter = null;
	private ProgressDialog dialog = null;
	private List<MembersInfoResult> membersList = null; // 家庭成员信息
	private TimeCount timeCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("ManageRFIDCardActivity", "onCreate");
		if (isFinalActivity) {
			setContentView(R.layout.activity_manage_address_layout);
			titleTV.setText(getString(R.string.manage_rfid_card));
			adapter = new RFIDCardAdapter(this, finalBitmap);

			membersList = new ArrayList<MembersInfoResult>();
			prepare(4);
			dialog = new ProgressDialog(this);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setMessage(getString(R.string.dialog_get_rfid_card));
			timeCount = new TimeCount(30000, 1000);//构造CountDownTimer对象\
		}
	}
	 class TimeCount extends CountDownTimer {
			public TimeCount(long millisInFuture, long countDownInterval) {
				super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
			}
			@Override
			public void onFinish() {//计时完毕时触发
				Toast.makeText(mContext, "申请卡片超时", Toast.LENGTH_LONG).show();
				if (dialog!=null &&dialog.isShowing()) {
					dialog.dismiss();
				}				
			}
			@Override
			public void onTick(long millisUntilFinished){//计时过程显示
				
			}
		}
	public void onClick(View v) {
		if (v == backIV) {
			MianActivity.getScreenManager().exitActivity(mActivity);
		} else if (v == addCardIV) {
			if (membersList!=null && membersList.size()>=5) {
				Toast.makeText(mContext, "随身保管理数量已达上限！", Toast.LENGTH_LONG).show();
				return;	
			}
			dialog.show();
			timeCount.start();
			prepare(5);
		}
	}

	private void prepare(int subCmd) {
		if (!ZganJTWSServiceTools.isConnect) {
			MessageUtil.alertMessage(mContext, R.string.sys_network_error);
			return;
		}

		if (subCmd == 5) {
			ZganJTWSService.toGetServerData(5, "0\t"+MyApplication.phone + "\t+\t+\t"
					+ "".getBytes(), handler);

		} else if (subCmd == 4) {
			if (null != membersList) {
				membersList.clear();
			}
			ZganJTWSService.toGetServerData(4, new String[]{ZganLoginService.getUserName()}, cardHandler, 2);
					
			
		}

	}

	@SuppressWarnings("unused")
	private void getRfidCard() {
		ZganJTWSService.toGetServerData(5, "0\t"+MyApplication.phone + "\t+\t+\t"
				+ "".getBytes(), handler);
		
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			Frame frame = (Frame) msg.obj;
			if (null == frame) {
				return;
			}
			int subCmd = frame.subCmd;
			
			String ret = frame.strData;
			Log.e("frame.strData;", ret);
			Log.e("frame.subCmd;",""+frame.subCmd) ;
			
			if (subCmd == 4) {						
				if (ret.startsWith("0")) {
					progressBar.setVisibility(View.INVISIBLE);
					MessageUtil.alertMessage(mContext, R.string.no_data);
					return;
				}
				MembersInfoResult member = MembersInfoResult.parse(frame);
				if (null != member) {
					int total = member.totalMembers;
					int index = member.currIndex;

					membersList.add(member);
					if (total - index == 1) {
						progressBar.setVisibility(View.GONE);
						adapter.setList(membersList);
						rfidCardLV.setAdapter(adapter);
					}
				}
			} else if (subCmd == 5 && frame.version == 1) {
				if (timeCount!=null ) {
					timeCount.cancel();
					//Log.e("timeCount.cancel();", "timeCount.cancel();");
				}		

				progressBar.setVisibility(View.INVISIBLE);

				if (null != dialog && dialog.isShowing()) {
					dialog.dismiss();

				}

				if (SpliteUtil.getRuquestStatus(ret)) {
					Intent intent = new Intent(ManageRFIDCardActivity.this,
							AddRFIDCardActivity.class);
					intent.putExtra("rfidCard", SpliteUtil.getResult(ret));
					startActivityForResult(intent, 0x107);
					dialog.dismiss();
				} else {
					MessageUtil.alertMessage(mContext,
							R.string.request_rfid_card_failed);
					dialog.dismiss();
				}
			}

		}
	};
	@SuppressLint("HandlerLeak")
	private Handler cardHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Frame f = (Frame) msg.obj;
			if (f.strData.startsWith("0")) {
				progressBar.setVisibility(View.INVISIBLE);
				MessageUtil.alertMessage(mContext, R.string.no_data);
			} else {

				String data[] = f.strData.split("\t");
				if (!(data.length < 6)) {

					int len = (data.length - 2) / 4;
					MembersInfoResult result = null;

					for (int i = 0; i < len; i++) {
						result = new MembersInfoResult();
						result.totalMembers = Integer.parseInt(data[0]);
						result.currIndex = i;
						result.cardNum = data[2 + 4 * i];
						result.nickname = data[3 + 4 * i];
						result.status = Integer.parseInt(data[4 + 4 * i]);
						result.url = data[5 + 4 * i].getBytes();

						membersList.add(result);
					}
					progressBar.setVisibility(View.INVISIBLE);
					adapter.setList(membersList);
					rfidCardLV.setAdapter(adapter);
				}
			}
		}
	};

	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0x107 && resultCode == RESULT_OK) {
			if (null != data) {
				if (data.getBooleanExtra("isAdd", false)) {
					// prepare(4);
				}
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v("ManageRFIDCardActivity", "onCreate");

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		BitmapDrawable bitmapDrawable = new BitmapDrawable(
				ImageOperation.readBitMap(mContext, R.drawable.bg_app));
		rootLayout.setBackgroundDrawable(bitmapDrawable);
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		Log.v("ManageRFIDCardActivity", "onCreate");

		prepare(4);
		super.onRestart();
	}

	@Override
	protected void onStop() {
		Log.v("ManageRFIDCardActivity", "onCreate");

		super.onStop();
		rootLayout.setBackgroundDrawable(null);
	}

	@Override
	protected void onDestroy() {
		Log.v("ManageRFIDCardActivity", "onCreate");

		super.onDestroy();
		handler.removeCallbacks(null);
		if (null != membersList) {
			membersList.clear();
			membersList = null;
		}
	}

}
