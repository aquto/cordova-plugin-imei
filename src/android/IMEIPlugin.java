package com.plugin.IMEI;

import org.apache.cordova.LOG;
import org.apache.cordova.PermissionHelper;
import org.json.JSONArray;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.provider.Settings.Secure;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONException;

public class IMEIPlugin extends CordovaPlugin {


    String TAG = "IMEIPlugin";
    CallbackContext context;

    String [] permissions = { Manifest.permission.READ_PHONE_STATE };

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        LOG.d(TAG, "We are entering execute");
        context = callbackContext;

        PluginResult.Status status = PluginResult.Status.OK;
        String result = "Permission Denied!";

        if (action.equals("get")) {

            if(hasPermisssion())
            {
                result = getUniqueID();
                PluginResult r = new PluginResult(PluginResult.Status.OK,result);
                context.sendPluginResult(r);
                return true;
            }
            else {
                PermissionHelper.requestPermissions(this, 0, permissions);
            }
            return true;


        }
        else {
            status = PluginResult.Status.INVALID_ACTION;
        }
        callbackContext.sendPluginResult(new PluginResult(status, result));


        return false;
    }


    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) throws JSONException
    {
        PluginResult result;
        //This is important if we're using Cordova without using Cordova, but we have the imei plugin installed
        if(context != null) {
            for (int r : grantResults) {
                if (r == PackageManager.PERMISSION_DENIED) {
                    LOG.d(TAG, "Permission Denied!");
                    result = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION,"Permission Denied!");
                    context.sendPluginResult(result);
                    return;
                }

            }
            result = new PluginResult(PluginResult.Status.OK,getUniqueID());
            context.sendPluginResult(result);
        }
    }

    public boolean hasPermisssion() {
        for(String p : permissions)
        {
            if(!PermissionHelper.hasPermission(this, p))
            {
                return false;
            }
        }
        return true;
    }

    /*
     * We override this so that we can access the permissions variable, which no longer exists in
     * the parent class, since we can't initialize it reliably in the constructor!
     */

    public void requestPermissions(int requestCode)
    {
        PermissionHelper.requestPermissions(this, requestCode, permissions);
    }

	private String getUniqueID(){
		String myAndroidDeviceId = "";
		TelephonyManager mTelephony = (TelephonyManager) this.cordova.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
		if (mTelephony.getDeviceId() != null){
			myAndroidDeviceId = mTelephony.getDeviceId();
		}else{
			Context context=this.cordova.getActivity().getApplicationContext();
			 myAndroidDeviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		}
		return myAndroidDeviceId;
	}

}
