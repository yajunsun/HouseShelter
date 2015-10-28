package com.hiibox.houseshelter.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;


import com.zgan.youbao.R;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

    
  
  
  
  
  
public class TencentWeiboUtil {

	private static Activity mActivity;

	private static Context mContext;

	private static TencentWeiboUtil tencentWeiboUtil;

	       
	private static TencentWeiboAPI weiboAPI;

	       
	private static TencentTO tencentTO;

	private WeiboListener listener;

	private Tencent mTencent;

	private Handler mHandler = new Handler();
	
                            
                               

	public TencentWeiboUtil() {
		tencentTO = new TencentTO();
                                                                   
                                  
                                                  
                                                 
            
	}

	public static TencentWeiboUtil getInstance(Activity activity) {
		mActivity = activity;
		mContext = activity.getApplicationContext();
		if (tencentWeiboUtil == null) {
			tencentWeiboUtil = new TencentWeiboUtil();
		}
		return tencentWeiboUtil;
	}

	    
  
  
	public void webAuthOnResult() {

		if (listener != null) {
			listener.onResult();
		}
	}

	    
  
  
	public void authOnResult(int requestCode, int resultCode, Intent data) {

		mTencent.onActivityResult(requestCode, resultCode, data);
	}

	    
  
  
  
  
  
	public boolean isAuth() {

		String accessToken = PreferenceUtil.getInstance(mContext).getString(
				ShareUtil.PREF_TX_ACCESS_TOKEN, "");
		if (TextUtils.isEmpty(accessToken)) {        
			return false;
		} else {
			long expiresTime = Long.parseLong(PreferenceUtil.getInstance(
					mContext).getString(ShareUtil.PREF_TX_EXPIRES_TIME, "0"));

			if (expiresTime - System.currentTimeMillis() > 0) {           
				String openId = PreferenceUtil.getInstance(mContext).getString(ShareUtil.PREF_TX_OPEN_ID, "");
				String clientIp = PreferenceUtil.getInstance(mContext).getString(ShareUtil.PREF_TX_CLIENT_IP, "");
				tencentTO.setAccessToken(accessToken);
				tencentTO.setOpenId(openId);
				tencentTO.setAppkey(ShareUtil.QQ_API_ID);
				tencentTO.setClientIp(clientIp);
				return true;
			} else {        
				return false;
			}
		}
	}

	    
  
  
  
  
  
  
	public void auth(WeiboListener l) {

		mTencent = Tencent.createInstance(ShareUtil.QQ_API_ID, mContext);
		IUiListener iuilistener = new BaseUiListener();
		mTencent.login(mActivity, ShareUtil.QQ_SCOPE, iuilistener);
		if (l == null) {
			listener = new WeiboListener();
		} else {
			listener = l;
		}
		             
		                                                                     
		                                                           
	}
	
	    
  
  
  
  
  
                                                         
                                                                            
                                                                
                                                                      
                           
                                              
                   
                            
            
                       
                                                                               
                                                                     
        


	private class BaseUiListener implements IUiListener {

                                        
        
        public BaseUiListener() {
            super();
        }
                                                 
                       
                                    
            
        
		@Override
		public void onComplete(JSONObject response) {
			doComplete(response);
		}

		protected void doComplete(JSONObject values) {
                                             

			String accessToken;
			try {
				accessToken = values.getString("access_token");
				String expiresIn = values.getString("expires_in");
				String openID = values.getString("openid");
				String clientIp = getClientIp();
				PreferenceUtil.getInstance(mContext).saveString(
						ShareUtil.PREF_TX_ACCESS_TOKEN, accessToken);
				PreferenceUtil.getInstance(mContext).saveString(
						ShareUtil.PREF_TX_EXPIRES_TIME,
						String.valueOf(System.currentTimeMillis()
								+ Integer.parseInt(expiresIn)));

				PreferenceUtil.getInstance(mContext).saveString(ShareUtil.PREF_TX_OPEN_ID, openID);
				tencentTO.setAccessToken(accessToken);
				tencentTO.setAppkey(ShareUtil.QQ_API_ID);
				tencentTO.setClientIp(clientIp);
				tencentTO.setOpenId(openID);

				getUserInfo(listener);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			                          
		}

		@Override
		public void onError(UiError e) {

		}

		@Override
		public void onCancel() {

		}
	}

	    
  
  
  
  
	public static String getClientIp() {
		try {
			for (Enumeration<NetworkInterface> mEnumeration = NetworkInterface
					.getNetworkInterfaces(); mEnumeration.hasMoreElements();) {
				NetworkInterface intf = mEnumeration.nextElement();
				for (Enumeration<InetAddress> enumIPAddr = intf
						.getInetAddresses(); enumIPAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIPAddr.nextElement();
					            
					if (!inetAddress.isLoopbackAddress()) {
						              
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {

		}
		return null;
	}

	    
  
  
  
  
	private void getUserInfo(final WeiboListener l) {
		weiboAPI = new TencentWeiboAPI(tencentTO);
		weiboAPI.getUserInfo(new AjaxCallBack<String>() {
			  




  
			  




  

            @Override
			public void onSuccess(String json) {
				super.onSuccess(json);

				try {
					JSONObject object = new JSONObject(json);
					String name = object.optString("nickname");                 

					                                 
					PreferenceUtil.getInstance(mContext).saveString(
							ShareUtil.PREF_TX_USER_NAME, name);
					if (l != null) {
						l.onResult();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	    
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
	public void addWeibo(final String content, final String longitude,
			final String latitude, final int syncflag, final int compatibleflag) {

		weiboAPI = new TencentWeiboAPI(tencentTO);
		weiboAPI.addWeibo(content, longitude, latitude, syncflag,
				compatibleflag, new AjaxCallBack<String>() {
					  

  

					@Override
					public void onSuccess(String t) {
						super.onSuccess(t);

						showResult(mContext.getString(R.string.weibosdk_send_sucess));
					}
				});

	}

	    
  
  
  
  
  
  
  
  
	public void addPicWeibo(final String content, final String picPath,
			final String longitude, final String latitude, final int syncflag,
			final int compatibleflag) {

		weiboAPI = new TencentWeiboAPI(tencentTO);
		weiboAPI.addPicWeibo(content, picPath, longitude, latitude, syncflag,
				compatibleflag, new AjaxCallBack<String>() {
					@SuppressWarnings("unused")
                    public void onFailure(Throwable t, String strMsg) {

						showResult(mContext.getString(R.string.weibosdk_send_failed)+strMsg);
					};

					public void onSuccess(String t) {

						showResult(mContext.getString(R.string.weibosdk_send_sucess));
					}
				});
	}
	
	    
  
  
  
  
  
  
  
  
	  
















  
	
	private void showResult(final String msg) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				MessageUtil.alertMessage(mContext, msg);
			}
		});
	}

	    
  
  
  
  
	public void logout(WeiboListener l) {
		PreferenceUtil.getInstance(mContext).remove(
				ShareUtil.PREF_TX_ACCESS_TOKEN);
		l.onResult();
	}

}
