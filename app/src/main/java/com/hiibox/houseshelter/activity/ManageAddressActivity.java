package com.hiibox.houseshelter.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hiibox.houseshelter.MyApplication;
import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.adapter.AddressAdapter;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.net.DeviceInfoResult;
import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.net.SpliteUtil;
import com.hiibox.houseshelter.util.ImageOperation;
import com.hiibox.houseshelter.util.MessageUtil;
import com.zgan.jtws.ZganJTWSService;
import com.zgan.jtws.ZganJTWSServiceTools;
import com.zgan.youbao.R;

public class ManageAddressActivity extends ShaerlocActivity {

	@ViewInject(id = R.id.root_layout)
	RelativeLayout rootLayout;
	@ViewInject(id = R.id.back_iv, click = "onClick")
	ImageView backIV;
	@ViewInject(id = R.id.add_address_iv, click = "onClick")
	ImageView addAddressIV;
	@ViewInject(id = R.id.address_list, itemClick = "onItemClick", itemLongClick = "onItemLongClick")
	ListView addressLV;
	@ViewInject(id = R.id.progress_bar)
	ProgressBar progressBar;

	private AddressAdapter adapter = null;
	private List<Map<String, Object>> list = null;
	private int defaultAddrId = 0; // 默认地址position
	private String deviceCode = null;
	private EditText et = null;
	private int position = -1;
	ProgressDialog dialog_1;
	
	private String defaultAddrName = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_address_layout);
		progressBar.setVisibility(View.INVISIBLE);
		list = new ArrayList<Map<String, Object>>();
		adapter = new AddressAdapter(mContext, this);
		deviceCode = MyApplication.deviceCode;

		if (TextUtils.isEmpty(MyApplication.deviceCode)) {
			sendRequest(80, null, null);
		}
		sendRequest(82, null, null);
		et = new EditText(mContext);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		params.leftMargin = 10;
		params.rightMargin = 10;
		et.setLayoutParams(params);

		dialog_1 = new ProgressDialog(ManageAddressActivity.this);
		dialog_1.setCancelable(false);
	}

	public void onClick(View v) {
		if (v == backIV) {
			MianActivity.getScreenManager().exitActivity(mActivity);
		} else if (v == addAddressIV) {
			if (list != null && list.size() >= 30) {

				Toast.makeText(mContext, "地址管理数量已达上限！", Toast.LENGTH_LONG)
						.show();
				return;
			}
			startActivityForResult(new Intent(this, AddAddressActivity.class),
					0x401);
		}
	}

	public void onItemClick(AdapterView<?> parent, View v, final int position,
			long id) {
		showPromptDialog(position);
	}

	public void onItemLongClick(AdapterView<?> parent, View v,
			final int position, long id) {
		Map<String, Object> map = list.get(position);
		final String deviceNumber = (String) map.get("deviceNumber");
		final EditText inputText = new EditText(this);
		Dialog dialog = new AlertDialog.Builder(this)
				.setView(inputText)
				.setMessage("请输入地址名")
				.setNegativeButton(R.string.negative,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface d, int wicth) {
								d.dismiss();
							}
						})
				.setPositiveButton(R.string.positive,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface d, int wicth) {
								defaultAddrId = position;
								defaultAddrName = inputText.getText().toString();
								dialog_1.setMessage("更新地址名称");
								dialog_1.show();
								sendRequest(105, deviceNumber, inputText
										.getText().toString());
							}
						}).create();
		dialog.show();
	}

	private void showPromptDialog(final int position) {
		Map<String, Object> map = list.get(position);
		final String deviceNumber = (String) map.get("deviceNumber");
		final String authCode = (String) map.get("authCode");
		Dialog dialog = new AlertDialog.Builder(this)
				.setMessage(
						getString(R.string.change_default_address)
								+ map.get("address"))
				.setPositiveButton(R.string.positive,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								defaultAddrId = position;
								dialog_1.setMessage(getResources().getString(
										R.string.change_default_addressing));
								dialog_1.show();
								sendRequest(71, deviceNumber, authCode);

							}
						})
				.setNegativeButton(R.string.negative,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();
		dialog.show();
	}

	private void sendRequest(int subCmd, final String deviceNumber,
			final String authCode) {
		if (!ZganJTWSServiceTools.isConnect) {
			MessageUtil.alertMessage(mContext, R.string.sys_network_error);
			return;
		}

		if (subCmd == 71) {
			ZganJTWSService.toGetServerData(71, MyApplication.phone + "\t"
					+ deviceNumber + "\t" + authCode, handler);

		} else if (subCmd == 82) {
			ZganJTWSService.toGetServerData(82, MyApplication.phone, handler);

		} else if (subCmd == 80) {
			ZganJTWSService.toGetServerData(80, MyApplication.phone, handler);

		} else if (subCmd == 87) {
			ZganJTWSService.toGetServerData(87, MyApplication.phone + "\t"
					+ deviceNumber + "\t" + authCode, handler);

		} else if (subCmd == 105) {
			ZganJTWSService.toGetServerData(105, MyApplication.phone + "\t"
					+ deviceNumber + "\t" + authCode, handler);
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {

				Frame frame = (Frame) msg.obj;

				int subCmd = frame.subCmd;

				String ret = frame.strData;

				if (subCmd == 71) {
					if (ret.equals("0")) {
						for (int i = 0; i < list.size(); i++) {
							Map<String, Object> m = list.get(i);
							if (i == defaultAddrId) {
								MyApplication.deviceCode = (String) m
										.get("deviceNumber");
								m.put("defaultAddr", true);
								list.set(i, m);
							} else {
								m.put("defaultAddr", false);
								list.set(i, m);
							}
						}
						adapter.setList(list);

						dialog_1.dismiss();
						MessageUtil.alertMessage(mContext,
								R.string.change_device_OK);

					} else {
						dialog_1.dismiss();
						MessageUtil.alertMessage(mContext,
								R.string.change_device_failed);
					}

				} else if (subCmd == 82) {
					if (ret.startsWith("1")) {
						MessageUtil.alertMessage(mContext, R.string.no_data);
						return;
					} else if (SpliteUtil.getRuquestStatus(ret)) {
						DeviceInfoResult infoResult = new DeviceInfoResult(
								deviceCode);
						infoResult.praseDeviceInfo(frame);
						list = infoResult.getDeviceList();
						if (null != list && list.size() > 0) {
							adapter.setList(list);
							addressLV.setAdapter(adapter);
						}
					}
				} else if (subCmd == 80) {
					if (SpliteUtil.getRuquestStatus(ret)) {
						MyApplication.deviceCode = SpliteUtil.getResult(ret);
						deviceCode = MyApplication.deviceCode;
					}
				} else if (subCmd == 87) {
					if (ret.equals("0")) {
						if (null != list && list.size() > 0) {
							if (position >= 0) {
								list.remove(position);
								adapter.setList(list);
								MessageUtil.alertMessage(mContext,
										R.string.success_delete_device);
							}
						}
					} else {
						MessageUtil.alertMessage(mContext,
								R.string.failed_delete_device);
					}
				} else if (subCmd == 105) {
					if (ret.equals("0")) {
						list.get(defaultAddrId).put("address", defaultAddrName);
						adapter.setList(list);
						MessageUtil.alertMessage(mContext, "更新成功");
					} else {
						MessageUtil.alertMessage(mContext, "更新失败");
					}
					dialog_1.dismiss();
				}
			} else {

			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0x401 && resultCode == RESULT_OK) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("address", data.getStringExtra("address"));
			map.put("deviceNumber", data.getStringExtra("deviceNumber"));
			map.put("authCode", data.getStringExtra("authCode"));
			map.put("defaultAddr", data.getBooleanExtra("defaultAddr", false));
			list.add(map);
			adapter.setList(list);
		} else if (requestCode == 0x104 && resultCode == RESULT_OK) {
			if (null != data) {
				String deviceNumber = data.getStringExtra("deviceNumber");
				String authCode = data.getStringExtra("authCode");
				position = data.getIntExtra("position", -1);

				if (!TextUtils.isEmpty(deviceNumber)
						&& !TextUtils.isEmpty(authCode)) {
					sendRequest(87, deviceNumber, authCode);
				}
			}
		} else if (requestCode == 0x105 && resultCode == RESULT_OK) {
			if (null != data) {
				String deviceNumber = data.getStringExtra("deviceNumber");
				String authCode = data.getStringExtra("authCode");
				position = data.getIntExtra("position", -1);

				if (!TextUtils.isEmpty(deviceNumber)
						&& !TextUtils.isEmpty(authCode)) {
					defaultAddrId = position;
					sendRequest(71, deviceNumber, authCode);
				}
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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

}
