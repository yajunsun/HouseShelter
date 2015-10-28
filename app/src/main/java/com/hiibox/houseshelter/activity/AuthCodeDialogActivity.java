package com.hiibox.houseshelter.activity;

import net.tsz.afinal.annotation.view.ViewInject;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.util.MessageUtil;
import com.zgan.youbao.R;

public class AuthCodeDialogActivity extends ShaerlocActivity {

	@ViewInject(id = R.id.confirm_iv, click = "onClick") Button confirmBtn;
	@ViewInject(id = R.id.cancel_iv, click = "onClick") Button cancelBtn;
	@ViewInject(id = R.id.auth_code_et) EditText authCodeET;
	
	private String deviceNumber = null;
	private int position = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth_code_dialog_layout);
		deviceNumber = getIntent().getStringExtra("deviceNumber");
		position = getIntent().getIntExtra("position", -1);
	}
	
	public void onClick(View v) {
		if (v == confirmBtn) {
			String authCode = authCodeET.getText().toString().trim();
			if (TextUtils.isEmpty(authCode)) {
				setFocusable(authCodeET, R.string.hint_input_auth_code);
                return;
			}
			Intent intent = new Intent();
			intent.putExtra("deviceNumber", deviceNumber);
			intent.putExtra("authCode", authCode);
			intent.putExtra("position", position);
			setResult(RESULT_OK, intent);
			MianActivity.getScreenManager().exitActivity(mActivity);
		} else if (v == cancelBtn) {
			MianActivity.getScreenManager().exitActivity(mActivity);
		}
	}
	
	public void setFocusable(EditText editText, int msg) {
        MessageUtil.alertMessage(mContext, msg);
        editText.setText("");
        editText.setFocusableInTouchMode(true);
        editText.setFocusable(true);
        editText.requestFocus();
        Editable editable = editText.getText();
        Selection.setSelection(editable, 0);
    }
}
