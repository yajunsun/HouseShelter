package com.hiibox.houseshelter.camera;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;


final class AutoFocusCallback implements Camera.AutoFocusCallback {

  private static final String TAG = AutoFocusCallback.class.getSimpleName();

  private static final long AUTOFOCUS_INTERVAL_MS = 1500L;

  private Handler autoFocusHandler;
  private int autoFocusMessage;

  void setHandler(Handler autoFocusHandler, int autoFocusMessage) {
    this.autoFocusHandler = autoFocusHandler;
    this.autoFocusMessage = autoFocusMessage;
  }

  public void onAutoFocus(boolean success, Camera camera) {
    if (autoFocusHandler != null) {
      Message message = autoFocusHandler.obtainMessage(autoFocusMessage, success);
                                                                       
                                            
                                                                  
      autoFocusHandler.sendMessageDelayed(message, AUTOFOCUS_INTERVAL_MS);
      autoFocusHandler = null;
    } 
  }

}
