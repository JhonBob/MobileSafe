package com.bob.mobilesafe.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bob.mobilesafe.R;
import com.bob.mobilesafe.services.RocketService;
import com.bob.mobilesafe.services.CallSmsSafeService;
import com.bob.mobilesafe.services.ShowLocationService;
import com.bob.mobilesafe.services.WatchDogService;
import com.bob.mobilesafe.ui.SettingView;
import com.bob.mobilesafe.utils.SystemInfoUtils;

//功能：设置中心
public class SettingCenterActivity extends Activity implements View.OnClickListener{
    //来电显示
    private static final String[] items ={"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
    //设置
    private SharedPreferences sp;
    //自定义控件
    private SettingView sv_autoupdate;
    //黑名单
    private SettingView sv_blacknumber;
    private Intent callSmsSafeIntent;
    //归属地显示
    private SettingView sv_showaddress;
    private Intent showAddressIntent;

    private TextView tv_title_style;
    private RelativeLayout changeBgStyle;

    //看门狗设置
    private SettingView sv_watchdog;
    private Intent watchDogIntent;

    //桌面小火箭
    private SettingView rocket;
    private Intent rocketIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_center);
        //显示的是默认的风格
        tv_title_style = (TextView) findViewById(R.id.tv_title_style);
        changeBgStyle=(RelativeLayout)findViewById(R.id.changeBgStyle);
        changeBgStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBgStyle();
            }
        });
        sp = getSharedPreferences("config", MODE_PRIVATE);
        int which = sp.getInt("which", 0);
        tv_title_style.setText(items[which]);
        //自动更新
        sv_autoupdate = (SettingView) findViewById(R.id.sv_autoupdate);
        sv_autoupdate.setChecked(sp.getBoolean("autoupdate", false));
        sv_autoupdate.setOnClickListener(this);
        //黑名单
        sv_blacknumber = (SettingView) findViewById(R.id.sv_blacknumber);
        sv_blacknumber.setOnClickListener(this);
        //归属地
        sv_showaddress = (SettingView) findViewById(R.id.sv_showaddress);
        sv_showaddress.setOnClickListener(this);

        //程序监视看门狗
        sv_watchdog = (SettingView) findViewById(R.id.sv_watchdog);
        sv_watchdog.setOnClickListener(this);

        //桌面小火箭
        rocket=(SettingView)findViewById(R.id.rocket);
        rocket.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sv_autoupdate:
                autoUpdate();
                break;
            case R.id.sv_blacknumber:
                blackNumber();
                break;
            case R.id.sv_showaddress:
                showAddress();
                break;
            case R.id.sv_watchdog:
                watchdog();
                break;
            case R.id.rocket:
                rocket();
                break;
        }
    }

    @Override
    protected void onStart() {
        //黑名单
        boolean running = SystemInfoUtils.isServiceRunning(this, "com.bob.mobilesafe.services.CallSmsSafeService");
        if(running){
            sv_blacknumber.setChecked(true);
        }else{
            sv_blacknumber.setChecked(false);
        }

        //定位跟踪
        boolean run = SystemInfoUtils.isServiceRunning(this, "com.bob.mobilesafe.services.ShowLocationService");
        if(run){
            sv_showaddress.setChecked(true);
        }else{
            sv_showaddress.setChecked(false);
        }

        //看门狗锁定应用
        boolean rundog = SystemInfoUtils.isServiceRunning(this, "com.bob.mobilesafe.services.WatchDogService");
        if(rundog){
            sv_watchdog.setChecked(true);
        }else{
            sv_watchdog.setChecked(false);
        }

        //小火箭
        boolean runrocket=SystemInfoUtils.isServiceRunning(this,"com.bob.mobilesafe.services.RocketService");
        if(runrocket){
            rocket.setChecked(true);
        }else {
            rocket.setChecked(false);
        }

        super.onStart();
    }



    //自动更新
    public void autoUpdate(){
        System.out.println("hahah ，被点击了。");
        SharedPreferences.Editor editor = sp.edit();
        //判断勾选的状态。
        if (sv_autoupdate.isChecked()) {
            sv_autoupdate.setChecked(false);
            editor.putBoolean("autoupdate", false);
        } else {
            sv_autoupdate.setChecked(true);
            editor.putBoolean("autoupdate", true);
        }
        editor.apply();
    }

    //黑名单拦截
    public  void blackNumber(){
        final Intent CallSmsService=new Intent(SettingCenterActivity.this, CallSmsSafeService.class);
        if(sv_blacknumber.isChecked()){
            sv_blacknumber.setChecked(false);
            stopService(CallSmsService);
        }else {
            sv_blacknumber.setChecked(true);
            startService(CallSmsService);
        }
    }

    //定位服务
    public void showAddress(){
        showAddressIntent = new Intent(SettingCenterActivity.this,ShowLocationService.class);
        if(sv_showaddress.isChecked()){
            sv_showaddress.setChecked(false);
            //停止定位服务
            stopService(showAddressIntent);
        }else{
            sv_showaddress.setChecked(true);
            //开启定位服务
            startService(showAddressIntent);
        }
    }

    //看门狗用于程序锁
    public void watchdog(){
        watchDogIntent = new Intent(SettingCenterActivity.this,WatchDogService.class);
        if(sv_watchdog.isChecked()){
            sv_watchdog.setChecked(false);
            //停止服务
            stopService(watchDogIntent);
        }else{
            sv_watchdog.setChecked(true);
            //开启服务
            startService(watchDogIntent);
        }
    }


    //桌面小火箭
    public void rocket(){
        rocketIntent=new Intent(SettingCenterActivity.this, RocketService.class);
        if(rocket.isChecked()){
            rocket.setChecked(false);
            //停止服务
            stopService(rocketIntent);
        }else{
            rocket.setChecked(true);
            //开启服务
            startService(rocketIntent);
        }
    }


    //修改归属地显示的背景风格
    public void changeBgStyle(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.main_icon_36);
        builder.setSingleChoiceItems(items, sp.getInt("which", 0), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("which", which);
                editor.commit();
                tv_title_style.setText(items[which]);
                dialog.dismiss();
            }
        });
        builder.setTitle("归属地提示框风格");
        builder.show();
    }

}
