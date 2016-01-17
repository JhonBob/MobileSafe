package com.bob.mobilesafe.activities;

import android.app.Activity;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bob.mobilesafe.R;
import com.bob.mobilesafe.utils.UIUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

//功能：缓存清理
public class CleanCacheActivity extends Activity {
    //Handler任务分发标志
    protected static final  int SCANNING=1;
    protected static final int FINISH=2;
    //包管理器
    private PackageManager pm;
    //缓存清理按钮
    private Button cleanAll;
    //缓存扫描进度条
    private LinearLayout ll_loading;
    //当前扫描的应用状态
    private TextView tv_scan_status;
    //显示存在缓存垃圾的应用
    private LinearLayout ll_container;
    //扫描到的应用程序集合
    private List<CacheInfo> cacheInfos;

    //分发任务的线程
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SCANNING:
                    PackageInfo info=(PackageInfo)msg.obj;
                    tv_scan_status.setText("扫描："+info.applicationInfo.loadLabel(pm).toString());
                    break;
                case FINISH:
                    ll_loading.setVisibility(View.INVISIBLE);
                    if (cacheInfos.size()==0){
                        UIUtils.showToast(CleanCacheActivity.this,"您的手机十分干净");
                    }else {
                        UIUtils.showToast(CleanCacheActivity.this,"您的手机是垃圾堆，赶紧清理");
                        for (final CacheInfo cacheInfo:cacheInfos){
                            View view=View.inflate(getApplicationContext(),R.layout.item_cache_info,null);
                            ImageView iv_icon=(ImageView)view.findViewById(R.id.iv_app_icon);
                            TextView tv_name=(TextView)view.findViewById(R.id.tv_app_name);
                            TextView tv_cache=(TextView)view.findViewById(R.id.tv_cache_size);
                            iv_icon.setImageDrawable(cacheInfo.icon);
                            tv_name.setText(cacheInfo.appname);
                            tv_cache.setText(Formatter.formatFileSize(getApplicationContext(), cacheInfo.cachesize));
                            ll_container.addView(view);
                        }
                    }
                    break;
            }
        }
    };


    //Aidl接口（缓存观察者）
    class ClearCacheObserver extends IPackageDataObserver.Stub{
        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
            System.out.println(succeeded);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cache);
        cleanAll=(Button)findViewById(R.id.cleanAll);
        cleanAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearAll();
            }
        });
        ll_loading=(LinearLayout)findViewById(R.id.ll_loading);
        tv_scan_status=(TextView)findViewById(R.id.tv_scan_status);
        ll_container=(LinearLayout)findViewById(R.id.ll_container);
        new Thread(){
            @Override
            public void run() {
                cacheInfos=new ArrayList<CleanCacheActivity.CacheInfo>();
                //遍历手机里的所有应用程序
                pm=getPackageManager();
                List<PackageInfo> infos=pm.getInstalledPackages(0);
                for (PackageInfo info:infos){
                    //获取每个应用的缓存大小
                    getCacheSize(info);

                    try{
                        Thread.sleep(50);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }

                    Message msg=Message.obtain();
                    msg.what=SCANNING;
                    msg.obj=info;
                    handler.sendMessage(msg);
                }

                Message msg=Message.obtain();
                msg.what=FINISH;
                handler.sendMessage(msg);
            }
        }.start();
    }

    class CacheInfo {
        String packname;
        String appname;
        long cachesize;
        Drawable icon;
    }

    //获取包对应的应用缓存大小

    public void getCacheSize(PackageInfo info){
        try{
            Method method=PackageManager.class.getDeclaredMethod(
                    "getPackageSizeInfo",
                    String.class,
                    IPackageStatsObserver.class
            );
            method.invoke(pm,info.packageName,new MyPackObserver(info));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //AIDL接口（缓存观察者：将存在缓存的应用对象加到List<CachInfo>中）
    public class  MyPackObserver extends IPackageStatsObserver.Stub{
        private PackageInfo info;
        //构造函数
        public MyPackObserver(PackageInfo info) {
            this.info = info;
        }

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            long cachesize=pStats.cacheSize;
            if(cachesize>0){
                System.out.println("应用程序缓存："+info.applicationInfo.loadLabel(pm)
                +"------"
                + Formatter.formatFileSize(getApplicationContext(),cachesize));

                CacheInfo cacheInfo=new CacheInfo();
                //缓存大小
                cacheInfo.cachesize=cachesize;
                //程序包名
                cacheInfo.packname=info.packageName;
                //应用程序名称
                cacheInfo.appname=info.applicationInfo.loadLabel(pm).toString();
                //应用图标
                cacheInfo.icon=info.applicationInfo.loadIcon(pm);
                //加到集合中
                cacheInfos.add(cacheInfo);
            }
        }
    }

    public void ClearAll() {
        //清除全部 缓存 利用Android系统的一个漏洞
            Method[] methods=PackageManager.class.getMethods();


            for (Method method:methods){
                System.out.println(method.getName());
                if ("freeStorageAndNotify".equals(method.getName())){
                    try{
                        method.invoke(pm,Integer.MAX_VALUE,new ClearCacheObserver());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return;
                }
            }

            Toast.makeText(CleanCacheActivity.this,"清理完毕",Toast.LENGTH_SHORT).show();
    }
}
