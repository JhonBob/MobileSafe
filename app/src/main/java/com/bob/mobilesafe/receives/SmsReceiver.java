package com.bob.mobilesafe.receives;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.util.Log;

import com.bob.mobilesafe.R;
import com.bob.mobilesafe.services.LocationService;


/**
 * Created by Administrator on 2015/12/31.
 */

//功能：手机防盗——远程控制
public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG="SmsReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "短信到来了");
        Object[] objectses=(Object[])intent.getExtras().get("pdus");
        //获取超级管理员
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        for(Object object:objectses){
            SmsMessage smsMessage= SmsMessage.createFromPdu((byte[])object);
            String sender=smsMessage.getOriginatingAddress();
            String body=smsMessage.getMessageBody();
            if("#*location*#".equals(body)){
                Log.i(TAG, "返回用户的位置信息");
                //放在服务里实现
                Intent service=new Intent(context, LocationService.class);
                context.startService(service);
                abortBroadcast();
            }else  if("#*alarm*#".equals(body)){
                Log.i(TAG, "播放报警音乐");
                MediaPlayer mediaPlayer=MediaPlayer.create(context, R.raw.ylzs);
                mediaPlayer.setVolume(1.0f,1.0f);
                mediaPlayer.start();
                abortBroadcast();
            }else  if("#*wipedata*#".equals(body)){
                Log.i(TAG, "远程清除数据");
                dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                abortBroadcast();
            }else  if("#*lockscreen*#".equals(body)){
                Log.i(TAG, "远程锁屏");
                dpm.resetPassword("123", 0);
                dpm.lockNow();
                abortBroadcast();
            }
        }
    }
}
