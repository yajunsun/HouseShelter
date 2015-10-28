package com.hiibox.houseshelter.activity;

import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.util.MessageUtil;
import com.zgan.jtws.ZganJTWSService;
import com.zgan.jtws.ZganJTWSServiceTools;
import com.zgan.login.ZganLoginService;
import com.zgan.youbao.R;

/**
 * 意见反馈
 * 
 * @author Administrator
 * 
 */

public class AdviceFeedbackActivity extends ShaerlocActivity {

	@ViewInject(id = R.id.back_iv, click = "onClick")
	ImageView backIV;
	@ViewInject(id = R.id.send_feedback_tv, click = "onClick")
	TextView sendTV;
	@ViewInject(id = R.id.advice_et)
	EditText adviceET;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advice_feedback_layout);
	}

	public void onClick(View v) {
		if (v == backIV) {
			MianActivity.getScreenManager().exitActivity(mActivity);
		} else if (v == sendTV) {
			String advice = adviceET.getText().toString().trim();
			if (TextUtils.isEmpty(advice)) {
				setFocusable(adviceET, R.string.prompt_input_feedback_msg);
				return;
			}
			sendRequest(advice);
		}
	}

	private void sendRequest(String advice) {
		if (ZganJTWSServiceTools.isConnect) {

			MessageUtil.alertMessage(mContext, R.string.upload_feedback_info);

			ZganJTWSService.toGetServerData(81, ZganLoginService.getUserName() + "\t"
					+ advice, handler);
		} else {
			MessageUtil.alertMessage(mContext, R.string.sys_network_error);

		}

	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Frame frame = (Frame) msg.obj;

			if (msg.what == 1) {
				if (frame.strData != null && !frame.strData.equals("")) {
					if (frame.strData.equals("0")) {
						MessageUtil.alertMessage(mContext,
								R.string.feed_back_success);
						MianActivity.getScreenManager().exitActivity(mActivity);
					} else {
						MessageUtil.alertMessage(mContext,
								R.string.operate_failed);

					}
				}else{
					MessageUtil.alertMessage(mContext,
							R.string.network_not_response);
				}
				
			}
		}
	};

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
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(null);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

}
