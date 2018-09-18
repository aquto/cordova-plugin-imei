package com.plugin.IMEI;


import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;


import android.Manifest;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.content.pm.PackageManager;


public class IMEIPlugin extends CordovaPlugin {

      public static final int REQ_CODE = 0;
      public static final int PERMISSION_DENIED_ERROR = 20;
      public static final String READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;

      private CallbackContext callbackContext;        // The callback context from which we were invoked.
      private JSONArray executeArgs;
      private String action;


      protected void getPermission(int requestCode) {
        cordova.requestPermission(this, requestCode, READ_PHONE_STATE);
      }

 @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    this.callbackContext = callbackContext;
    this.executeArgs = args;

    this.action = action;


        if (cordova.hasPermission(READ_PHONE_STATE)) {
          getIMEI(executeArgs);
        } else {
          getPermission(REQ_CODE);
        }        


    

    return true;
  }


  public void onRequestPermissionResult(int requestCode, String[] permissions,
                                        int[] grantResults) throws JSONException {
        for (int r : grantResults) {
          if (r == PackageManager.PERMISSION_DENIED) {
            this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, PERMISSION_DENIED_ERROR));
            return;
          }
        }
        switch (requestCode) {
          case REQ_CODE:
            getIMEI(executeArgs);
            break;
        }
  }


   private void getIMEI(JSONArray args) throws JSONException {

        PluginResult.Status status = PluginResult.Status.OK;
        String result = "";

        if (this.action.equals("get")) {
            TelephonyManager telephonyManager = (TelephonyManager)this.cordova.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            result = telephonyManager.getDeviceId();
        }
        else {
            status = PluginResult.Status.INVALID_ACTION;
        }
        callbackContext.sendPluginResult(new PluginResult(status, result));


   }    

}
