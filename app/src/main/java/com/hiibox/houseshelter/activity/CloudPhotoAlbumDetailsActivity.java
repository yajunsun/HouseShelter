package com.hiibox.houseshelter.activity;

import java.util.ArrayList;

import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.adapter.AdvertisementAdapter;
import com.hiibox.houseshelter.core.GlobalUtil;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.util.MessageUtil;
import com.zgan.file.ZganFileService;
import com.zgan.file.ZganFileServiceTools;
import com.zgan.youbao.R;

public class CloudPhotoAlbumDetailsActivity extends ShaerlocActivity implements
		OnPageChangeListener {

	@ViewInject(id = R.id.cloud_pictures_pager)
	ViewPager pager;
	@ViewInject(id = R.id.pic_date_tv)
	TextView dateTV;
	@ViewInject(id = R.id.back_iv, click = "onClick")
	ImageView backIV;
	@ViewInject(id = R.id.pre_iv, click = "onClick")
	ImageView preIV;
	@ViewInject(id = R.id.after_iv, click = "onClick")
	ImageView afterIV;
	// 分享按钮
	@ViewInject(id = R.id.delete_iv, click = "onClick")
	ImageView deleteIV;
	@ViewInject(id = R.id.progress_bar)
	ProgressBar progressBar;

	private ArrayList<String> picUrlList = null;
	private ArrayList<String> picIdList = null;
	private ArrayList<String> picTimeList = null;

	private int currPagerIndex = 0;
	private int picLen = 0;
	private AdvertisementAdapter adapter = null;
	private boolean isDelete = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cloud_photo_album_details_layout);
		setLargeScreenParams();
		Intent intent = getIntent();
		picUrlList = intent.getStringArrayListExtra("picUrlList");
		picIdList = intent.getStringArrayListExtra("picIdList");
		String picDate = intent.getStringExtra("picDate");
		picTimeList = intent.getStringArrayListExtra("picTime");
		picLen = picUrlList.size();
		dateTV.setText(picTimeList.get(currPagerIndex));
		adapter = new AdvertisementAdapter(mContext, picUrlList, finalBitmap);
		pager.setAdapter(adapter);
		pager.setOnPageChangeListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isDelete) {
				Intent intent = new Intent();
				intent.putExtra("isDelete", isDelete);
				this.setResult(RESULT_OK, intent);
			}
			MianActivity.getScreenManager().exitActivity(mActivity);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onClick(View v) {
		int vId = v.getId();
		switch (vId) {
		case R.id.back_iv:
			if (isDelete) {
				Intent intent = new Intent();
				intent.putExtra("isDelete", isDelete);
				this.setResult(RESULT_OK, intent);
			}
			MianActivity.getScreenManager().exitActivity(mActivity);
			break;
		case R.id.pre_iv:
			currPagerIndex--;
			if (currPagerIndex == -1) {
				MessageUtil.alertMessage(mContext, R.string.the_first_picture);
				currPagerIndex = 0;
				return;
			}
			pager.setCurrentItem(currPagerIndex);
			dateTV.setText(picTimeList.get(currPagerIndex));

			break;
		case R.id.after_iv:
			currPagerIndex++;
			if (currPagerIndex >= picLen) {
				MessageUtil.alertMessage(mContext, R.string.the_last_picture);
				currPagerIndex = picLen - 1;
				return;
			}
			
			pager.setCurrentItem(currPagerIndex);
			dateTV.setText(picTimeList.get(currPagerIndex));

			break;

		case R.id.delete_iv:
			new AlertDialog.Builder(this)
					.setTitle(R.string.dialog_delete_photo)
					.setCancelable(true)
					.setPositiveButton(R.string.positive,
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (null != picIdList
											&& picIdList.size() > 0) {
										deleteIV.setEnabled(false);
										deletePhotos(
												picIdList.get(currPagerIndex),
												currPagerIndex);
									}
									dialog.dismiss();
								}
							})
					.setNegativeButton(R.string.negative,
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).create().show();
			break;
		default:
			break;
		}
	}

	private void deletePhotos(String fileId, final int position) {
		
		if(ZganFileServiceTools.isConnect){
			
			ZganFileService.toGetServerData(41,new String[]{ZganFileService.UserName,fileId}, handler);
			
		}else{
			MessageUtil.alertMessage(mContext, R.string.sys_network_error);
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			progressBar.setVisibility(View.GONE);
			
			if(msg.what==1){
				Frame frame=(Frame)msg.obj;
				
				//删除文件
				if(frame.subCmd==41){
					if(!TextUtils.isEmpty(frame.strData) && frame.strData.equals("0")){
						isDelete = true;

						picIdList.remove(currPagerIndex);
						picUrlList.remove(currPagerIndex);
						picTimeList.remove(currPagerIndex);
						adapter.setList(picUrlList);
						adapter.notifyDataSetChanged();
						
						
						if (null != picTimeList && picTimeList.size() != 0) {
							dateTV.setText(picTimeList.get(currPagerIndex));
						}
						if (null == picTimeList && picTimeList.size() == 0) {
							dateTV.setText("");
						}
						if (null != picUrlList && picUrlList.size() == 0) {
							
						}
						
						deleteIV.setEnabled(true);
						
						MessageUtil.alertMessage(mContext, R.string.operate_success);
						
					}
				}
					
			}
		}
			

	};

	private void setLargeScreenParams() {
		if (GlobalUtil.mScreenHeight > 854 && GlobalUtil.mScreenHeight <= 1280) {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT, 450);
			params.addRule(RelativeLayout.CENTER_IN_PARENT);
			pager.setLayoutParams(params);
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {

		currPagerIndex = position;
		dateTV.setText(picTimeList.get(currPagerIndex));

	}

	@Override
	protected void onResume() {
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(null);
	}
}
