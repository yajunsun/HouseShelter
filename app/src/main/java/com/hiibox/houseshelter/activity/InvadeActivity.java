package com.hiibox.houseshelter.activity;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.adapter.InvadeAdapter;
import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.net.Frame;
import com.hiibox.houseshelter.net.InvadePhotoResult;
import com.hiibox.houseshelter.net.InvadePhotoResult.PhotoInfo;
import com.hiibox.houseshelter.util.ImageOperation;
import com.hiibox.houseshelter.util.MessageUtil;
import com.hiibox.houseshelter.util.PreferenceUtil;
import com.hiibox.houseshelter.view.PullToRefreshView;
import com.hiibox.houseshelter.view.PullToRefreshView.OnFooterRefreshListener;
import com.hiibox.houseshelter.view.PullToRefreshView.OnHeaderRefreshListener;
import com.zgan.file.ZganFileService;
import com.zgan.file.ZganFileServiceTools;
import com.zgan.jtws.ZganJTWSService;
import com.zgan.login.ZganLoginService;
import com.zgan.youbao.R;

public class InvadeActivity extends ShaerlocActivity implements OnFooterRefreshListener, OnHeaderRefreshListener {

    @ViewInject(id = R.id.root_layout) RelativeLayout rootLayout;
    @ViewInject(id = R.id.back_iv, click = "onClick") ImageView backIV;
    @ViewInject(id = R.id.phone_iv, click = "onClick") ImageView phoneIV;

    @ViewInject(id = R.id.pull_to_refresh_view) PullToRefreshView refreshView;
    @ViewInject(id = R.id.invade_lv) ListView invadeLV;
    @ViewInject(id = R.id.progress_bar) ProgressBar progressBar;
    
    private List<PhotoInfo> list = null;
    private InvadeAdapter adapter = null;
    private boolean buzzerSwitch = false;
    private ProgressDialog dialog = null;
    private String emergencyContact = null;
                                                           
    private InvadePhotoResult albumResult = null;
    private String filedId = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        buzzerSwitch = PreferenceUtil.getInstance(mContext).getBoolean("buzzerSwitch", false);
        filedId = getIntent().getStringExtra("filedId");
       setContentView(R.layout.activity_invade_layout);
        refreshView.setHeadRefresh(false);
        refreshView.setFooterRefresh(false);
        refreshView.setOnFooterRefreshListener(this);
        refreshView.setOnHeaderRefreshListener(this);
        
        list = new ArrayList<InvadePhotoResult.PhotoInfo>();
        albumResult = new InvadePhotoResult();
                                                        
        queryPhotos();
        
        adapter = new InvadeAdapter(mActivity, mContext, finalBitmap);
        
        dialog = new ProgressDialog(this);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        
        getContact();
        invadeLV.setOnItemClickListener(listener);
        
        
    }
    OnItemClickListener listener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position,
				long arg3) {
			// TODO Auto-generated method stub
			PhotoInfo info=list.get(position);
			Intent intent=new Intent(InvadeActivity.this,BigPic.class);
			if (info.getUrl()!=null) {
				intent.putExtra("url", info.getUrl());	
				startActivity(intent);
			}
			
		}
	};
    
    private void queryPhotos() {
    	
    	if(ZganFileServiceTools.isConnect){
    		ZganFileService.toGetServerData(39, new String[]{ZganFileService.UserName,filedId,"6"}, handler);
    	}else{
    		MessageUtil.alertMessage(mContext, R.string.sys_network_error);
    	}
 
    }
    
    @SuppressLint("HandlerLeak")
    private Handler picHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (null != msg) {
                Frame[] f = (Frame[]) msg.obj;
                if (null == f) {
                    return;
                }
                if (null == f[1]) {
                    return;
                }

                if (f[1].subCmd == 39) {
                    int rc = albumResult.prasePhotoAlbum(f[1]);
                    if (rc == 0) {
                        list = albumResult.getList();
                        if (list.size() > 0) {
                            adapter.setList(list);
                            invadeLV.setAdapter(adapter);
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        }
    };
    
    private void getContact() {
    	emergencyContact = PreferenceUtil.getInstance(mContext).getString("emergencyContact", null);
    	
    	ZganJTWSService.toGetServerData(79,new String[]{ZganLoginService.getUserName()}, handler);
    }
    
    @SuppressWarnings("static-access")
    public void onClick(View v) {
        if (v == backIV) {
            MianActivity.getScreenManager().exitActivity(mActivity);
        } else if (v == phoneIV) {
            Intent intent = new Intent();
            intent.setAction(intent.ACTION_DIAL);
            if (!TextUtils.isEmpty(emergencyContact)) {
                intent.setData(Uri.parse("tel:"+emergencyContact));
            } else {
                intent.setData(Uri.parse("tel:"+110));
            }
            startActivity(intent);
        } 
    }
    
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int what = msg.what;
			dialog.dismiss();
			
			if(what==1){
				Frame frame = (Frame) msg.obj;				
				//联系电话
				if (frame.mainCmd==0x0e && frame.subCmd == 79) {					
					if (!TextUtils.isEmpty(frame.strData)){
						String[] aryRfidData = frame.strData.split("\t");
						
						if(aryRfidData.length==2 && aryRfidData[0].equals("0")){
                            emergencyContact =aryRfidData[1];
                            
                            if(!TextUtils.isEmpty(emergencyContact)){                            
                            	PreferenceUtil.getInstance(mContext).saveString("emergencyContact", emergencyContact.trim());
                            }
						}
					}
				}else if(frame.mainCmd==0x0f && frame.subCmd == 39){
					//文件列表
					
					int rc = albumResult.prasePhotoAlbum(frame);
                    if (rc == 0) {
                        list = albumResult.getList();
                        if (list.size() > 0) {
                            adapter.setList(list);
                            invadeLV.setAdapter(adapter);
                        }
                        progressBar.setVisibility(View.GONE);
                    }
				}
			}
		}
    };
    
    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        adapter.notifyDataSetChanged();
        refreshView.onFooterRefreshComplete();
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        refreshView.onHeaderRefreshComplete();   
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
    	picHandler.removeCallbacks(null);
    }
    
}
