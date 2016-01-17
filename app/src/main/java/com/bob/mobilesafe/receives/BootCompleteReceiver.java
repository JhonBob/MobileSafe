package com.bob.mobilesafe.receives;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by Administrator on 2015/12/31.
 */

//功能：手机防盗检测SIM卡的广播接收器
public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String TAG="BootCompleteReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"手机启动完毕");
        SharedPreferences sp=context.getSharedPreferences("config",Context.MODE_PRIVATE);
        boolean protecting=sp.getBoolean("protecting",false);
        if(protecting){
            String bindsim=sp.getString("sim",null);
            TelephonyManager tm=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            String realsim=tm.getSimSerialNumber();
            if (bindsim.equals(realsim)){
                Log.i(TAG,"SIM卡未发生变化，还是你的手机");
            }else {
                Log.i(TAG,"SIM卡发生变化");
                String savednumber=sp.getString("safenumber",null);
                SmsManager smsManager=SmsManager.getDefault();
                smsManager.sendTextMessage(savednumber,null,"SIM CHANGED!", null,null);
            }
        }else {
            Log.i(TAG,"手机防盗未开启");
        }

    }
}
