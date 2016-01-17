package com.bob.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by Administrator on 2016/1/2.
 */

//功能：获取手机信息
public class SystemInfoUtils {



    public static boolean isServiceRunning(Context context,String className){
        //活动管理器，进程管理器
        ActivityManager am=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo>infos=am.getRunningServices(200);
        for(ActivityManager.RunningServiceInfo info:infos){
            String serviceClassName=info.service.getClassName();
            if(className.equals(serviceClassName)){
                return true;
            }
        }
        return  false;
    }

    //获取手机的总内存
    public static long gettotalMem(){
        try {
            FileInputStream fis=new FileInputStream(new File("/proc/meminfo"));
            BufferedReader br=new BufferedReader(new InputStreamReader(fis));
            String totalInfo=br.readLine();
            StringBuffer sb=new StringBuffer();
            for (char c:totalInfo.toCharArray()){
                if (c>='0' && c<='9'){
                    sb.append(c);
                }
            }
            long bytesize=Long.parseLong(sb.toString())*1024;
            return bytesize;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }

    //获取手机可用空间
    public static long getAvaliMem(Context context){
        ActivityManager am=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo=new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        long avaliMem=outInfo.availMem;
        return avaliMem;
    }

    //正在运行的进程
    public static int getRunningProcessCount(Context context){
        ActivityManager am=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos=am.getRunningAppProcesses();
        int count=runningAppProcessInfos.size();
        return count;
    }
}
