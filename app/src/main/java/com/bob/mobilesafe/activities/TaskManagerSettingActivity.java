package com.bob.mobilesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.bob.mobilesafe.R;
import com.bob.mobilesafe.services.AutoKillService;
import com.bob.mobilesafe.utils.SystemInfoUtils;

//功能：进程分类显示设置
public class TaskManagerSettingActivity extends Activity {
    //显示系统进程
    private CheckBox cb_show_system;
    //配置
    private SharedPreferences sp;
    //轮询自动杀死进程
    private CheckBox cb_lock_autokill;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager_setting);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
        cb_show_system.setChecked(sp.getBoolean("showsystem", false));
        cb_show_system.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("showsystem", isChecked);
                editor.commit();
            }
        });

        final Intent intent = new Intent(this,AutoKillService.class);
        cb_lock_autokill = (CheckBox) findViewById(R.id.cb_lock_autokill);
        cb_lock_autokill.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(isChecked){
                   startService(intent);
               }else{
                   stopService(intent);
               }
           }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(SystemInfoUtils.isServiceRunning(this, "com.bob.mobilesafe.services.AutoKillService")){
            cb_lock_autokill .setChecked(true);
        }else{
            cb_lock_autokill .setChecked(false);
        }
    }
}
