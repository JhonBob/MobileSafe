package com.bob.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

import com.bob.mobilesafe.R;
import com.bob.mobilesafe.domain.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/4.
 */

//功能：应用进程信息解析引擎
public class TaskInfoParser {
    //获取正在运行的所有信息
    public static List<TaskInfo>getRunningTaskInfo(Context context){
        ActivityManager am=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm=context.getPackageManager();

        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos=am.getRunningAppProcesses();
        List<TaskInfo> taskInfos=new ArrayList<>();

        for (ActivityManager.RunningAppProcessInfo processInfo:runningAppProcessInfos){
            TaskInfo taskInfo=new TaskInfo();

            String packagename=processInfo.processName;
            taskInfo.setPackagename(packagename);

            MemoryInfo[] memoryInfos=am.getProcessMemoryInfo(new int[]{processInfo.pid});
            long memsize=memoryInfos[0].getTotalPrivateDirty()*1024;
            taskInfo.setMemsize(memsize);

            try {
                PackageInfo packageInfo=pm.getPackageInfo(packagename,0);

                Drawable icon=packageInfo.applicationInfo.loadIcon(pm);
                taskInfo.setIcon(icon);

                String appname=packageInfo.applicationInfo.loadLabel(pm).toString();
                taskInfo.setAppname(appname);

                if((ApplicationInfo.FLAG_SYSTEM&packageInfo.applicationInfo.flags)!=0){
                    //系统
                    taskInfo.setUsertask(false);
                }else {
                    //用户
                    taskInfo.setUsertask(true);
                }

            }catch (PackageManager.NameNotFoundException e){
                e.printStackTrace();
                taskInfo.setAppname(packagename);
                taskInfo.setIcon(context.getResources().getDrawable(R.mipmap.ic_default));
            }
            taskInfos.add(taskInfo);
        }
        return taskInfos;
    }
}
