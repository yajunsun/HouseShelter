package com.hiibox.houseshelter.activity;

import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import com.hiibox.houseshelter.util.ImageOperation;
import com.hiibox.houseshelter.util.MessageUtil;
import com.hiibox.houseshelter.view.TemperatureTrendView;
import com.zgan.youbao.R;

public class TemperatureAbnormalActivity extends ShaerlocActivity {

    @ViewInject(id = R.id.root_layout) LinearLayout rootLayout;
    @ViewInject(id = R.id.trend_content) LinearLayout trendLayout;
    @ViewInject(id = R.id.current_date_tv) TextView currDateTV;
    @ViewInject(id = R.id.start_time_tv) TextView startTimeTV;
    @ViewInject(id = R.id.end_time_tv) TextView endTimeTV;
    @ViewInject(id = R.id.current_temperature_tv) TextView currTemperatureTV;
    @ViewInject(id = R.id.back_iv, click = "onClick") ImageView backIV;
    @ViewInject(id = R.id.phone_iv, click = "onClick") ImageView phoneIV;
    
    private int trendLayoutHeight = 0;
    private String emergencyContact = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_abnormal_layout);
        String time = getIntent().getStringExtra("time");             
        String[] timeStr = time.split(" ");
        currDateTV.setText(DateUtil.changeDateToYmd(time));
        endTimeTV.setText(timeStr[1].substring(0, timeStr[1].lastIndexOf(":")));
        startTimeTV.setText(DateUtil.getLastHalfAnHour(timeStr[1]));
        getContact();
        sendRequest(time);
        
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
    
    private void sendRequest(String time) {

    }
    
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                int[] temp = (int[]) msg.obj;
                TemperatureTrendView trendView = new TemperatureTrendView(mContext, GlobalUtil.mScreenWidth-100, trendLayoutHeight, temp);
                trendLayout.addView(trendView);
                currTemperatureTV.setText(temp[0]+getString(R.string.temperature_unit));
            } else if (msg.what == 0) {
                MessageUtil.alertMessage(mContext, R.string.network_timeout);
            } else if (msg.what == 2) {
                MessageUtil.alertMessage(mContext, R.string.no_data);
            }
        }
    };
    
    private void getContact() {
                                                               

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
