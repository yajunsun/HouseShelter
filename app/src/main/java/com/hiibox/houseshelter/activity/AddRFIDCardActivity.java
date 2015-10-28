package com.hiibox.houseshelter.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.adapter.DropDownBoxAdapter;
import com.hiibox.houseshelter.core.GlobalUtil;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.util.DateUtil;
import com.hiibox.houseshelter.util.IBreakUtil;
import com.hiibox.houseshelter.util.LocationUtil;
import com.hiibox.houseshelter.util.MessageUtil;
import com.hiibox.houseshelter.util.PreferenceUtil;
import com.zgan.jtws.ZganJTWSService;
import com.zgan.jtws.ZganJTWSServiceTools;
import com.zgan.login.ZganLoginService;
import com.zgan.youbao.R;

/**
 * @Description 添加RFID卡
 * @Author wangjiang
 * @Create Date 2013-10-31 下午3:20:45
 * @Modified By
 * @Modified Date
 * @Modified Description
 */

public class AddRFIDCardActivity extends ShaerlocActivity {

	@ViewInject(id = R.id.back_iv, click = "onClick")
	ImageView backIV;
	// @ViewInject(id = R.id.upload_head_portrait_iv, click = "onClick")
	// ImageView addPicIV;
	@ViewInject(id = R.id.submit_tv, click = "onClick")
	TextView submitTV;
	@ViewInject(id = R.id.rfid_card_et)
	EditText choseCardET;
	@ViewInject(id = R.id.name_et)
	EditText nameET;

	private DropDownBoxAdapter adapter = null;
	private List<String> rfidCardList = null;
	private View cardView = null;
	private ListView cardNumberLV = null;
	private Bitmap bitmap = null;
	private ProgressDialog dialog = null;
	private String rfidCard = null;
	private String fieldString = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rfidCard = getIntent().getStringExtra("rfidCard");

		setContentView(R.layout.activity_add_rfid_card_layout);

		choseCardET.setText(rfidCard);
		adapter = new DropDownBoxAdapter(mContext);
		rfidCardList = new ArrayList<String>();
		for (int i = 0; i < 5; i++) {
			rfidCardList.add("FD990234X" + i);
		}
		adapter.setList(rfidCardList);
		cardView = getLayoutInflater().inflate(
				R.layout.popupwindow_drop_down_layout, null);
		cardNumberLV = (ListView) cardView.findViewById(R.id.popup_lv);
		cardNumberLV.setAdapter(adapter);

		dialog = new ProgressDialog(this);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setMessage(getString(R.string.registering));
	}

	public void onClick(View v) {
		int vid = v.getId();
		switch (vid) {
		case R.id.back_iv:
			MianActivity.getScreenManager().exitActivity(mActivity);
			break;

		case R.id.submit_tv:
			String nickname = nameET.getText().toString();
			if (TextUtils.isEmpty(nickname)) {
				setFocusable(nameET, R.string.hint_input_nickname);
				return;
			}
			dialog.setMessage(getString(R.string.registering));
			dialog.show();
			if (null == bitmap) {
				prepare(rfidCard, nickname, "0".getBytes());
			} else {
				byte[] picFile = IBreakUtil.bmpToByteArray(bitmap, false);
				prepare(rfidCard, nickname, picFile);
			}
			break;

		default:
			break;
		}
	}

	private void prepare(final String cardNum, final String nickname,
			final byte[] pic) {
		if (ZganJTWSServiceTools.isConnect) {
			registerRfidCard(cardNum, nickname, fieldString);
			Log.e("发送数据时间", System.currentTimeMillis() + "");

		} else {
			MessageUtil.alertMessage(mContext, R.string.sys_network_error);
		}

	}

	private void UploadBmp(String path) {
		if (LocationUtil.checkNetWork(mContext).endsWith(
				GlobalUtil.NETWORK_NONE)) {
			MessageUtil.alertMessage(mContext, R.string.sys_network_error);
			// startActivity(new Intent("android.settings.WIRELESS_SETTINGS"));
			// HomepageActivity.WLAN_FLAG=0;
			return;
		}

		Bitmap bmap = BitmapFactory.decodeFile(path);
		String phone = PreferenceUtil.getInstance(getApplicationContext())
				.getString("phone", null);
		byte[] picFile = IBreakUtil.bmpToByteArray(bmap, false);
		int fileSize = picFile.length;
		String time = DateUtil.getcurrentDay();
		int type = 1;// 上传头像

		dialog.setMessage(getString(R.string.upload_ing));
		dialog.show();

		upload(phone, fileSize, time, type);
	}

	private void upload(String phone, int fileSize, String time, int type) {
		// // TODO Auto-generated method stub
		// MyApplication.fileClient.saveFileRequest(phone, fileSize, time, type,
		// new CommandListener() {
		//
		// @Override
		// public void onTimeout(Frame src, Frame f) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public int onReceive(Frame src, Frame f) {
		// // TODO Auto-generated method stub
		// if (f.strData.equals("0")) {
		// uploadFile();
		// }
		// if (f.strData.equals("1")) {
		//
		// }
		// if (f.strData.equals("2")) {
		// }
		// if (f.strData.equals("3")) {
		//
		// }
		//
		// return 0;
		// }
		// });
	}

	protected void uploadFile() {
		// TODO Auto-generated method stub
		if (LocationUtil.checkNetWork(mContext).endsWith(
				GlobalUtil.NETWORK_NONE)) {
			MessageUtil.alertMessage(mContext, R.string.sys_network_error);
			// startActivity(new Intent("android.settings.WIRELESS_SETTINGS"));
			// HomepageActivity.WLAN_FLAG=0;
			return;
		}
		/*
		 * if (HomepageActivity.WLAN_FLAG==0) { return; }
		 */

		byte[] picFile = IBreakUtil.bmpToByteArray(bitmap, false);
		int fileSize = picFile.length;
		// MyApplication.fileClient.uploadFile(fileSize, picFile,
		// new CommandListener() {
		//
		// @Override
		// public void onTimeout(Frame src, Frame f) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public int onReceive(Frame src, Frame f) {
		// // TODO Auto-generated method stub
		// String data[] = f.strData.split("\t");
		//
		// if (data[0].equals("0")) {
		//
		// if (data[1].equals("0")) {
		// fieldString = data[2];
		// handler.sendEmptyMessage(4);
		//
		// }
		// if (data[1].equals("1")) {
		// //
		// handler.sendEmptyMessage(5);
		//
		// }
		//
		// }
		// if (data[0].equals("1")) {
		// handler.sendEmptyMessage(6);
		//
		// }
		//
		// return 0;
		// }
		// });
	}

	private void registerRfidCard(String cardNum, String nickname, String fied) {

		ZganJTWSService.toGetServerData(05, new String[] { "1",
				ZganLoginService.getUserName(), cardNum, nickname, fied },
				handler);

		// MyApplication.mainClient.regCard2(1, MyApplication.phone, cardNum,
		// nickname, fied, new CommandListener() {
		// @Override
		// public void onTimeout(Frame src, Frame f) {
		// dialog.dismiss();
		//
		// handler.sendEmptyMessage(0);
		// }
		//
		// @SuppressWarnings("unused")
		// @Override
		// public int onReceive(Frame src, Frame f) {
		// dialog.dismiss();
		// if (null != f) {
		//
		// if (f.strData.equals("0")) {
		// handler.sendEmptyMessage(3);
		// } else {
		// handler.sendEmptyMessage(2);
		// }
		//
		//
		// } else {
		// handler.sendEmptyMessage(1);
		// }
		// return 0;
		// }
		// });
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int what = msg.what;
			dialog.dismiss();

			if (what == 1) {
				Frame frame = (Frame) msg.obj;

				if (frame.subCmd == 05) {
					if (!TextUtils.isEmpty(frame.strData)) {
						String[] aryRfidData = frame.strData.split("\t");

						if (aryRfidData[0].equals("0")) {
							MessageUtil.alertMessage(mContext,
									R.string.register_success);
							Intent data = new Intent();
							data.putExtra("isAdd", true);
							setResult(RESULT_OK, data);
							MianActivity.getScreenManager().exitActivity(
									mActivity);
						} else {
							MessageUtil.alertMessage(mContext,
									R.string.register_failed);
						}
					}
				}
			}

			// if (null != dialog && dialog.isShowing()) {
			//
			//
			// }
			// if (what == 0) {
			// MessageUtil.alertMessage(mContext, R.string.network_timeout);
			// } else if (what == 1) {
			// MessageUtil.alertMessage(mContext,
			// R.string.network_not_response);
			// } else if (what == 2) {
			// MessageUtil.alertMessage(mContext, R.string.register_failed);
			// } else if (what == 3) {
			// MessageUtil.alertMessage(mContext, R.string.register_success);
			// Intent data = new Intent();
			// data.putExtra("isAdd", true);
			// setResult(RESULT_OK, data);
			// MianActivity.getScreenManager().exitActivity(mActivity);
			// } else if (what == 4) {
			// MessageUtil.alertMessage(mContext, R.string.upload_sucess);
			// } else if (what == 5) {
			// uploadFile();
			// } else if (what == 6) {
			// MessageUtil.alertMessage(mContext, R.string.upload_erre);
			//
			// }
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0x201 && resultCode == RESULT_OK) {
			String path = data.getStringExtra("picturePath");

			if (!TextUtils.isEmpty(path)) {
				File file = new File(path);

				if (file.exists()) {
					// String dta[]=path.split(".");
					bitmap = BitmapFactory.decodeFile(path);

					// addPicIV.setImageBitmap(bitmap);

					UploadBmp(path);
					// handler.sendEmptyMessage(4);

				} else {
					MessageUtil.alertMessage(mContext,
							R.string.the_picture_is_not_exist);
				}
			}
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
