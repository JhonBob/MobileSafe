package com.bob.mobilesafe.engine;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;

import com.bob.mobilesafe.domain.TrafficInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/6.
 */

//流量信息获取
public class TrafficInfoParser {

    private static final String TAG = "TrafficInfoParser";

    private PackageManager pm;
    private Context context;

    public TrafficInfoParser(Context context) {
        this.context = context;
        pm = context.getPackageManager();
    }

    public List<TrafficInfo> getTrafficInfos() {
        //获取到配置权限信息的应用程序
        List<PackageInfo> packinfos = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        //存放具有Internet权限信息的应用
        List<TrafficInfo> trafficInfos = new ArrayList<TrafficInfo>();
        for (PackageInfo packinfo : packinfos) {
            //获取该应用的所有权限信息
            String[] permissions = packinfo.requestedPermissions;
            if (permissions != null && permissions.length > 0) {
                for (String permission : permissions) {
                    //筛选出具有Internet权限的应用程序
                    if ("android.permission.INTERNET".equals(permission)) {
                        //用于封装具有Internet权限的应用程序信息
                        TrafficInfo trafficInfo = new TrafficInfo();
                        //封装应用信息
                        trafficInfo.setPackname(packinfo.packageName);
                        trafficInfo.setIcon(packinfo.applicationInfo.loadIcon(pm));
                        trafficInfo.setAppname(packinfo.applicationInfo.loadLabel(pm).toString());
                        //获取到应用的uid（user id）
                        int uid = packinfo.applicationInfo.uid;
                        //TrafficStats对象通过应用的uid来获取应用的下载、上传流量信息
                        trafficInfo.setRx(TrafficStats.getUidRxBytes(uid));
                        trafficInfo.setTx(TrafficStats.getUidTxBytes(uid));
                        trafficInfos.add(trafficInfo);
                        trafficInfo = null;
                        break;
                    }
                }
            }
        }
        return trafficInfos;
    }
}
