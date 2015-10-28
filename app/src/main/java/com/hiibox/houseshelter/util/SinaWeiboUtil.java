package com.hiibox.houseshelter.util;

import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;


import com.zgan.youbao.R;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;
import com.weibo.sdk.android.sso.SsoHandler;

    
  
  
  
  
  
public class SinaWeiboUtil {

	private static Context mContext;
	private static Activity mActivity;
	private static SinaWeiboUtil mInstantce;

	private static Weibo mWeibo;

	       
	private static Oauth2AccessToken mAccessToken;

	       
	private static SsoHandler mSsoHandler;

	private WeiboListener listener;
	
                            
                               

	public SinaWeiboUtil() {
		mWeibo = Weibo.getInstance(ShareUtil.SINA_WEIBO_API_ID,
				ShareUtil.WEIBO_REDICT_URL);
                                                                   
                                  
                                                  
                                                 
            
	}

	public static SinaWeiboUtil getInstance(Activity activity) {
		mActivity = activity;
		mContext = activity.getApplicationContext();
		if (mInstantce == null) {
			mInstantce = new SinaWeiboUtil();
		}
		return mInstantce;
	}

	    
  
  
  
	@SuppressLint("SimpleDateFormat")
	public boolean isAuth() {
		String token = PreferenceUtil.getInstance(mContext).getString(
				ShareUtil.PREF_SINA_ACCESS_TOKEN, "");
		long expiresTime = PreferenceUtil.getInstance(mContext).getLong(
				ShareUtil.PREF_SINA_EXPIRES_TIME, 0);
		mAccessToken = new Oauth2AccessToken();
		mAccessToken.setToken(token);
		mAccessToken.setExpiresTime(expiresTime);


		if (mAccessToken.isSessionValid()) {            
			return true;
		} else {

			return false;
		}
	}

	    
  
  
  
  
	public void auth(WeiboListener l) {

		         
		mSsoHandler = new SsoHandler(mActivity, mWeibo);
		mSsoHandler.authorize(new AuthDialogListener());
		if(l!=null){
			listener = l;
		}else{
			listener = new WeiboListener();
		}

		             
		                                                         
	}
	
	    
  
  
  
  
	class AuthDialogListener implements WeiboAuthListener {

        public AuthDialogListener() {
            super();
        }
        
		@Override
		public void onCancel() {
			MessageUtil.alertMessage(mContext, R.string.user_auth_cancel);
		}

		@Override
		public void onComplete(Bundle values) {
			String token = values.getString(ShareUtil.SINA_ACCESS_TOKEN);
			String uid = values.getString(ShareUtil.SINA_UID);
			String userName = values.getString(ShareUtil.SINA_USER_NAME);
			String expiresIn = values.getString(ShareUtil.SINA_EXPIRES_IN);                                      
			String remindIn = values.getString(ShareUtil.SINA_REMIND_IN);

			mAccessToken = new Oauth2AccessToken(token, expiresIn);
			if (mAccessToken.isSessionValid()) {
                                              
                                                        
                    
			    
				PreferenceUtil.getInstance(mContext).saveString(
						ShareUtil.PREF_SINA_ACCESS_TOKEN, token);
				PreferenceUtil.getInstance(mContext).saveString(
						ShareUtil.PREF_SINA_UID, uid);
				PreferenceUtil.getInstance(mContext).saveLong(
						ShareUtil.PREF_SINA_EXPIRES_TIME,
						mAccessToken.getExpiresTime());             
				PreferenceUtil.getInstance(mContext).saveString(
						ShareUtil.PREF_SINA_REMIND_IN, remindIn);
				if (TextUtils.isEmpty(userName)) {
					show(Long.parseLong(uid));
				} else {
					PreferenceUtil.getInstance(mContext).saveString(
							ShareUtil.PREF_SINA_USER_NAME, userName);
					if (listener != null) {
						listener.onResult();
					}
				}
			}
		}

		@Override
		public void onError(WeiboDialogError e) {

			MessageUtil.alertMessage(mContext, mContext.getString(R.string.weibosdk_send_failed) + e.getMessage());
		}

		@Override
		public void onWeiboException(WeiboException e) {

			MessageUtil.alertMessage(mContext, mContext.getString(R.string.weibosdk_send_failed) + e.getMessage());
		}
	}

	    
  
  
  
  
  
  
	public void authCallBack(int requestCode, int resultCode, Intent data) {
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	    
  
  
  
  
	public void show(long uid) {
		SinaWeiboAPI api = new SinaWeiboAPI(mAccessToken);
		api.show(uid, new RequestListener() {

			@Override
			public void onIOException(IOException e) {

				MessageUtil.alertMessage(mContext, mContext.getString(R.string.user_auth_fail) + e.getMessage());
			}

			@Override
			public void onError(WeiboException e) {
	
				MessageUtil.alertMessage(mContext, mContext.getString(R.string.user_auth_fail) + e.getMessage());
			}

			@Override
			public void onComplete(String json) {
				JSONObject object;
				try {
					object = new JSONObject(json);
					String userName = object.optString(ShareUtil.SINA_NAME);
		
					PreferenceUtil.getInstance(mContext).saveString(
							ShareUtil.PREF_SINA_USER_NAME, userName);
					if (listener != null) {
						listener.onResult();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	    
  
  
  
  
  
  
  
  
  
  
	public void update(String content, String lat, String lon) {

		SinaWeiboAPI api = new SinaWeiboAPI(mAccessToken);
		api.update(content, lat, lon, new RequestListener() {

			@Override
			public void onIOException(IOException e) {

				Looper.prepare();
				MessageUtil.alertMessage(mContext, mContext.getString(R.string.user_auth_fail) + e.getMessage());
				Looper.loop();
			}

			@Override
			public void onError(WeiboException e) {

				Looper.prepare();
				if (e.getStatusCode() == 400) {                 
					MessageUtil.alertMessage(mContext, mContext.getString(R.string.weibosdk_error_repeat_content)
							+ e.getMessage());
				} else {
					MessageUtil.alertMessage(mContext,
							mContext.getString(R.string.user_auth_fail)+ e.getMessage());
				}
				Looper.loop();
			}

			@Override
			public void onComplete(String str) {

				Looper.prepare();
				MessageUtil.alertMessage(mContext, mContext.getString(R.string.weibosdk_send_sucess));
				Looper.loop();
			}
		});
	}
	
	    
  
  
  
  
  
  
  
  
  
  
	public void upload(String content, String picUrl, String lat, String lon) {

	    SinaWeiboAPI api = new SinaWeiboAPI(mAccessToken);
	    api.upload(content, picUrl, lat, lon, new RequestListener() {
	        
	        @Override
	        public void onIOException(IOException e) {

	            MessageUtil.alertMessage(mContext, mContext.getString(R.string.user_auth_fail) + e.getMessage());
	        }
	        
	        @Override
	        public void onError(WeiboException e) {

	            Looper.prepare();
	            if (e.getStatusCode() == 400) {                 
	                MessageUtil.alertMessage(mContext, mContext.getString(R.string.weibosdk_error_repeat_content)
	                    + e.getMessage());
	            } else {
	                MessageUtil.alertMessage(mContext,
	                    mContext.getString(R.string.user_auth_fail)+ e.getMessage());
	            }
	            Looper.loop();
	        }
	        
	        @Override
	        public void onComplete(String str) {

	            Looper.prepare();
	            MessageUtil.alertMessage(mContext, mContext.getString(R.string.weibosdk_send_sucess));
	            Looper.loop();
	        }
	    });
	}
	
	    
  
  
  
  
  
  
  
  
  
  
	public void uploadUrlText(String content, String picUrl, String lat, String lon) {

	    SinaWeiboAPI api = new SinaWeiboAPI(mAccessToken);
	    api.uploadUrlText(content, picUrl, lat, lon, new RequestListener() {
	        
	        @Override
	        public void onIOException(IOException e) {
	 
	            MessageUtil.alertMessage(mContext, mContext.getString(R.string.user_auth_fail) + e.getMessage());
	        }
	        
	        @Override
	        public void onError(WeiboException e) {

	            Looper.prepare();
	            if (e.getStatusCode() == 400) {                 
	                MessageUtil.alertMessage(mContext, mContext.getString(R.string.weibosdk_error_repeat_content)
	                    + e.getMessage());
	            } else {
	                MessageUtil.alertMessage(mContext,
	                    mContext.getString(R.string.user_auth_fail)+ e.getMessage());
	            }
	            Looper.loop();
	        }
	        
	        @Override
	        public void onComplete(String str) {

	            Looper.prepare();
	            MessageUtil.alertMessage(mContext, mContext.getString(R.string.weibosdk_send_sucess));
	            Looper.loop();
	        }
	    });
	}

	    
  
  
  
  
	public void logout(WeiboListener l) {
		PreferenceUtil.getInstance(mContext).remove(
				ShareUtil.PREF_SINA_ACCESS_TOKEN);
		l.onResult();
	}


}
