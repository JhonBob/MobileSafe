package com.bob.mobilesafe.receives;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/1/4.
 */

//功能：进程管理-杀死当前正在运行的进程
public class KillAllRecevier extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningAppProcessInfo info: am.getRunningAppProcesses()){
            am.killBackgroundProcesses(info.processName);
        }
        Toast.makeText(context, "清理完毕", Toast.LENGTH_SHORT).show();
    }

}