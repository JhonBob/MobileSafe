package com.bob.mobilesafe.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;


import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.stericson.RootTools.RootTools;

import com.bob.mobilesafe.R;
import com.bob.mobilesafe.domain.AppInfo;
import com.bob.mobilesafe.engine.AppInfoParser;

//功能：软件管理
public class AppManagerActivity extends AppCompatActivity implements OnClickListener {

    public static final String TAG = "AppManagerActivity";
    //软件列表
    private ListView appmanager;
    //Rom的大小
    private TextView tv_rom;
    //SD卡的大小
    private TextView tv_sd;
    //应用程序的大小
    private TextView tv_appsize_lable;
    //环形进度控件
    private LinearLayout ll_load;
    //所有的应用程序对象集合
    private List<AppInfo> infos;
    //用户应用程序对象集合
    private List<AppInfo> userAppInfos;
    //系统应用程序大小
    private List<AppInfo> systemAppInfos;


    //弹出窗体
    private PopupWindow popupwindow;

    //弹出窗体：启动
    private LinearLayout ll_start;
    //弹出窗体：分享
    private LinearLayout ll_share;
    //弹出窗体：卸载
    private LinearLayout ll_uninstall;
    //弹出窗体：设置
    private LinearLayout ll_setting;


    //被点击的条目对应的，appinfo对象
    private AppInfo clickedAppInfo;
    //卸载应用程序广播接收器
    private UninstallReceiver receiver;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ll_load.setVisibility(View.INVISIBLE);
            appmanager.setAdapter(new AppManagerAdapter());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        appmanager = (ListView) findViewById(R.id.app_mamager);
        appmanager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                 dissmisspop();
                if (userAppInfos != null && systemAppInfos != null) {
                    //越过系统文本位置
                    if (firstVisibleItem >= (userAppInfos.size() + 1)) {
                        tv_appsize_lable.setText("系统程序：" + systemAppInfos.size() + "个");
                    } else {//未越过系统应用文本位置
                        tv_appsize_lable.setText("用户程序：" + userAppInfos.size() + "个");
                    }
                }
            }
        });

        //条目点击是弹出popwindow控件
        appmanager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj=appmanager.getItemAtPosition(position);
                if(obj!=null && obj instanceof AppInfo){
                    //取得对象
                    clickedAppInfo = (AppInfo) obj;
                    //获得弹窗对象
                    View contentView=View.inflate(getApplicationContext(),R.layout.popup_item,null);

                    ll_uninstall = (LinearLayout) contentView
                            .findViewById(R.id.ll_uninstall);
                    ll_start = (LinearLayout) contentView
                            .findViewById(R.id.ll_start);
                    ll_share = (LinearLayout) contentView
                            .findViewById(R.id.ll_share);
                    ll_setting = (LinearLayout) contentView
                            .findViewById(R.id.ll_setting);


                    ll_share.setOnClickListener(AppManagerActivity.this);
                    ll_start.setOnClickListener(AppManagerActivity.this);
                    ll_uninstall.setOnClickListener(AppManagerActivity.this);
                    ll_setting.setOnClickListener(AppManagerActivity.this);


                    dissmisspop();
                    //弹出窗体
                    popupwindow=new PopupWindow(contentView,-2,-2);
                    popupwindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    int location[]=new int[2];
                    view.getLocationInWindow(location);
                    popupwindow.showAtLocation(parent, Gravity.LEFT+Gravity.TOP,60,location[1]);
                    //动画播放前提，窗体要有背景资源
                    ScaleAnimation sa=new ScaleAnimation(0.5f,1.0f,0.5f,1.0f,
                            Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0.5f);
                    sa.setDuration(300);
                    AlphaAnimation aa=new AlphaAnimation(0.5f,1.0f);
                    aa.setDuration(300);
                    AnimationSet set=new AnimationSet(false);
                    set.addAnimation(aa);
                    set.addAnimation(sa);
                    contentView.startAnimation(set);
                }
            }
        });



        tv_rom = (TextView) findViewById(R.id.tv_avail_rom);
        tv_sd = (TextView) findViewById(R.id.tv_avail_sd);
        tv_appsize_lable = (TextView) findViewById(R.id.tv_appsize_lable);
        ll_load=(LinearLayout)findViewById(R.id.ll_load);


        long avail_sd = Environment.getExternalStorageDirectory().getFreeSpace();
        long avail_rom = Environment.getDataDirectory().getFreeSpace();
        String str_tv_rom = Formatter.formatFileSize(this, avail_rom);
        String str_tv_sd = Formatter.formatFileSize(this, avail_sd);
        tv_rom.setText("内部剩余：" + str_tv_rom);
        tv_sd.setText("SD卡剩余：" + str_tv_sd);

        //向列表填充数据
        fiiDate();

        //动态注册卸载广播接收器
        receiver = new UninstallReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(receiver, filter);

    }
        public void fiiDate(){
            //开始填充数据时显示进度条
            ll_load.setVisibility(View.VISIBLE);
            //子线程处理耗时的操作
            new Thread(){
                @Override
                public void run() {
                    //使用AppInfoParser引擎解析获得所有的应用信息
                    infos = AppInfoParser.getAppInfos(AppManagerActivity.this);
                    //初始化集合对象
                    userAppInfos=new ArrayList<>();
                    systemAppInfos=new ArrayList<>();
                    //遍历infos集合将用户应用与系统应用分区持有
                    for(AppInfo info:infos){
                        if(info.isUserApp()){
                            //用户
                            userAppInfos.add(info);
                        }else {
                            //系统
                            systemAppInfos.add(info);
                        }
                    }
                    handler.sendEmptyMessage(0);
                }
            }.start();
    }

    private class AppManagerAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            //+2显示两个标签
            return userAppInfos.size()+systemAppInfos.size()+2;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {


            //标签位置处理
            if (position == 0) {
                // 第0个位置显示的应该是 用户程序的个数的标签。
                return null;
            } else if (position == (userAppInfos.size() + 1)) {
                //系统标签的位置
                return null;
            }


            AppInfo appInfo;
            if (position < (userAppInfos.size() + 1)) {
                // 用户程序列表position的值相对集合的位置多了一
                appInfo = userAppInfos.get(position - 1);// 多了一个textview的标签 ，
                // 位置需要-1
            } else {
                // 系统程序列表相对Position的值相对集合多了两个标签加用户集合长度
                int location = position - 1 - userAppInfos.size() - 1;
                appInfo = systemAppInfos.get(location);
            }
            return appInfo;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            AppInfo appInfo;

            //两个标签的特殊位置
            if(position==0){
                TextView tv=new TextView(getApplicationContext());
                tv.setBackgroundColor(Color.BLUE);
                tv.setTextColor(Color.WHITE);
                tv.setText("用户程序："+userAppInfos.size()+"个");
                return tv;
            }else if(position==userAppInfos.size()+1){
                TextView tv=new TextView(getApplicationContext());
                tv.setBackgroundColor(Color.BLUE);
                tv.setTextColor(Color.WHITE);
                tv.setText("系统程序："+systemAppInfos.size()+"个");
                return tv;
            }

            View view;
            ViewHolder holder;


            //区分用户应用和系统应用
            if(position<(userAppInfos.size()+1)){
                //用户
                appInfo=userAppInfos.get(position-1);
            }else {
                //系统
                int location=position-userAppInfos.size()-2;
                appInfo=systemAppInfos.get(location);
            }

            //列表优化
            //convertView instanceof LinearLayout 复用缓存对象的类型是否正确
            if(convertView!=null && convertView instanceof LinearLayout){
                view=convertView;
                holder = (ViewHolder) view.getTag();

            }else {
                view=View.inflate(AppManagerActivity.this,R.layout.item_app_manager,null);
                holder = new ViewHolder();
                holder.iv_app_icon = (ImageView) view
                        .findViewById(R.id.iv_app_icon);
                holder.tv_app_name = (TextView) view
                        .findViewById(R.id.tv_app_name);
                holder.tv_app_size = (TextView) view
                        .findViewById(R.id.tv_app_size);
                holder.tv_app_location = (TextView) view
                        .findViewById(R.id.tv_app_location);
                view.setTag(holder);
            }

            holder.iv_app_icon.setImageDrawable(appInfo.getIcon());
            holder.tv_app_name.setText(appInfo.getName());
            holder.tv_app_size.setText(Formatter.formatFileSize(
                    getApplicationContext(), appInfo.getAppSize()));
            if (appInfo.isInRoom()) {
                holder.tv_app_location.setText("手机内存");
            } else {
                holder.tv_app_location.setText("外部存储");
            }



            return view;
        }
    }

    //使用内部类ViewHolder复用缓存对象，提高效率，优化界面
    static class ViewHolder {
        ImageView iv_app_icon;
        TextView tv_app_name;
        TextView tv_app_size;
        TextView tv_app_location;
    }

    //取消窗体
    public void dissmisspop(){
        if(popupwindow!=null&&popupwindow.isShowing()){
            popupwindow.dismiss();
            popupwindow=null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_share:
                Log.i(TAG, "分享：");
                shareApplication();
                break;
            case R.id.ll_uninstall:
                Log.i(TAG, "卸载：");
                uninstallApplication();
                break;
            case R.id.ll_start:
                Log.i(TAG, "开启：");
                startApplication();
                break;
            case R.id.ll_setting:
               Log.i(TAG, "设置：");
                viewAppDetail();
               break;
        }
        dissmisspop();
    }

    //应用详情
    private void viewAppDetail() {
        Intent intent = new Intent();
        //调用系统设置界面
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        // dat=package:com.itheima.mobileguard
        //对应要打开的应用
        intent.setData(Uri.parse("package:" + clickedAppInfo.getPackgeName()));
        startActivity(intent);
    }

     //分享应用
    private void shareApplication() {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,
                "推荐您使用一款软件，名称叫：" + clickedAppInfo.getName()
                        + "下载路径：https://play.google.com/store/apps/details?id="
                        + clickedAppInfo.getPackgeName());
        startActivity(intent);
    }


     //开启应用程序
    private void startApplication() {
        // 打开这个应用程序的入口activity。
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(clickedAppInfo
                .getPackgeName());
        if (intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "该应用没有启动界面", Toast.LENGTH_SHORT).show();
        }
    }

    //卸载应用程序
    private void uninstallApplication() {
        if (clickedAppInfo.isUserApp()) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + clickedAppInfo.getPackgeName()));
            startActivity(intent);
        }else{
            //系统应用 ，root权限 利用linux命令删除文件。
            if(!RootTools.isRootAvailable()){
                Toast.makeText(this, "卸载系统应用，必须要root权限", Toast.LENGTH_SHORT).show();
                return ;
            }
            try {
                if(!RootTools.isAccessGiven()){
                    Toast.makeText(this, "请授权黑马小护卫root权限", Toast.LENGTH_SHORT).show();
                    return ;
                }
                //系统挂载为可读可写
                RootTools.sendShell("mount -o remount ,rw /system", 3000);
                //强力删除
                RootTools.sendShell("rm -r "+clickedAppInfo.getApkpath(), 30000);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    //卸载应用的广播接收器
    private class UninstallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String info = intent.getData().toString();
            System.out.println(info);
            fiiDate();
        }
    }

    @Override
    protected void onDestroy() {
        dissmisspop();
        unregisterReceiver(receiver);
        receiver = null;
        super.onDestroy();
    }
}
