package com.bob.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.bob.mobilesafe.domain.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/3.
 */

//功能：应用程序解析引擎
public class AppInfoParser {
    public static List<AppInfo> getAppInfos(Context context){
        //包管理器
        PackageManager pm=context.getPackageManager();
        List<PackageInfo> packageInfos=pm.getInstalledPackages(0);
        List<AppInfo> appInfos=new ArrayList<>();
        for (PackageInfo info:packageInfos){
            AppInfo appInfo=new AppInfo();

            String packagename=info.packageName;
            appInfo.setPackgeName(packagename);
            Drawable icon=info.applicationInfo.loadIcon(pm);
            appInfo.setIcon(icon);
            String appname=info.applicationInfo.loadLabel(pm).toString();
            appInfo.setName(appname);
            //sourceDir源码路径，包的路径
            String path=info.applicationInfo.sourceDir;
            appInfo.setApkpath(path);
            File file=new File(path);
            long appSize=file.length();
            appInfo.setAppSize(appSize);

            //System.out.println("包名："+packagename);
            //System.out.println( "应用："+appname);
            //System.out.println( "路径："+path);
            //System.out.println( "大小："+appSize);
            //System.out.println( "----------------------");
            //if (path.startsWith("/system/")){
            //    System.out.println( "-------目录判断系统应用---------");
            //}else {
              //  System.out.println( "-------目录判断用户应用---------");
           // }

            int flags=info.applicationInfo.flags;//二进制映射
            //int 28种组合
           if((ApplicationInfo.FLAG_EXTERNAL_STORAGE&flags)!=0){
              // System.out.println( "-------外部应用---------");
               appInfo.setInRoom(false);
           }else {
               //System.out.println( "-------内存应用---------");
               appInfo.setInRoom(true);
           }

            if((ApplicationInfo.FLAG_SYSTEM&flags)!=0){
               // System.out.println( "-------FLAG判断系统应用---------");
                appInfo.setUserApp(false);
            }else {
                appInfo.setUserApp(true);
               // System.out.println( "-------FLAG判断用户应用---------");
            }
            //System.out.println( "----------------------");
            appInfos.add(appInfo);
        }
        return appInfos;
    }
}
