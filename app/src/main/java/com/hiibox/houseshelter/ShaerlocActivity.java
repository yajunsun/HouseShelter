package com.hiibox.houseshelter;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Build.VERSION;

import android.view.Window;


import com.hiibox.houseshelter.core.MianActivity;
import com.hiibox.houseshelter.core.GlobalUtil;
import com.zgan.youbao.R;

public class ShaerlocActivity extends FinalActivity  {

	public static FinalBitmap finalBitmap = null;
	public static FinalHttp finalHttp = null;
	public Context mContext = null;
	public Activity mActivity = null;
	public boolean isFinalActivity=false;
	
	
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (Integer.parseInt(VERSION.SDK) > 14
				|| Integer.parseInt(VERSION.SDK) == 14) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork() // or
																			// .detectAll()
																			// for
																			// all
																			// detectable
																			// problems
					.penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
					.penaltyLog().penaltyDeath().build());
		}	
		
		mActivity = this;
		mContext = mActivity.getApplicationContext();
		MianActivity.getScreenManager().addActivity(mActivity);
		finalBitmap = FinalBitmap.create(mContext, GlobalUtil.IMAGE_PATH);
		finalBitmap.configLoadfailImage(R.drawable.default_load_error_picture);
		finalBitmap.configLoadingImage(R.drawable.default_loading_picture);
		finalHttp = new FinalHttp();		
		

		isFinalActivity=true;

		
	}
	
	private String getRunningActivityName(){    
        String contextString = this.toString();
        return contextString.substring(contextString.lastIndexOf(".")+1, contextString.indexOf("@"));
   }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
