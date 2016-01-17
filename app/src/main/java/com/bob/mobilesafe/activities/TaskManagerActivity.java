package com.bob.mobilesafe.activities;


import android.app.Activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bob.mobilesafe.R;
import com.bob.mobilesafe.domain.TaskInfo;
import com.bob.mobilesafe.engine.TaskInfoParser;
import com.bob.mobilesafe.utils.SystemInfoUtils;

import java.util.ArrayList;
import java.util.List;

//功能：进程管理
public class TaskManagerActivity extends Activity implements View.OnClickListener {
    //正在运行的进程
    private TextView tv_running_prcesscount;
    //内存信息
    private TextView tv_ram_info;
    //进程列表
    private ListView lv_taskmanger;
    //所有进程信息的集合
    private List<TaskInfo>  infos;
    //运行的用户进程
    private List<TaskInfo>  userTaskInfos = new ArrayList<>();
    //运行的系统进程
    private List<TaskInfo>  systemTaskInfos = new ArrayList<>();
    //进程封装类对象
    private TaskInfo info;
    //加载进度
    private LinearLayout ll_loading;
    //进程适配
    private TaskManagerAdapter adapter;
    //选择所有进程
    private Button selectAll;
    //反选
    private Button selectOpposite;
    //杀死所有进程
    private Button killProcess;
    //进程显示设置
    private Button openSetting;
    //正在运行的进程数
    private int runningProcessCount;
    //可用内存
    private long avaliMem;
    //总内存
    private long totalMem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);

        tv_running_prcesscount=(TextView)findViewById(R.id.tv_running_prcesscount);
        tv_ram_info=(TextView)findViewById(R.id.tv_ram_info);
        avaliMem=SystemInfoUtils.getAvaliMem(this);
        totalMem= SystemInfoUtils.gettotalMem();
        tv_running_prcesscount.setText("可用/总内存："+ Formatter.
                formatFileSize(this, avaliMem)+"/"+Formatter.formatFileSize(this,totalMem));
        runningProcessCount=SystemInfoUtils.getRunningProcessCount(this);
        tv_ram_info.setText("运行的进程：" + runningProcessCount + "个");

        lv_taskmanger=(ListView)findViewById(R.id.lv_taskmanger);
        lv_taskmanger.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = lv_taskmanger.getItemAtPosition(position);
                if (obj != null && obj instanceof TaskInfo) {
                    TaskInfo info = (TaskInfo) obj;
                    if (info.getPackagename().equals(getPackageName())) {
                        // 就是我们自己。
                        return;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (info.isChecked()) {
                        holder.cb_status.setChecked(false);
                        info.setChecked(false);
                    } else {
                        holder.cb_status.setChecked(true);
                        info.setChecked(true);
                    }
                }
            }
        });


        ll_loading=(LinearLayout)findViewById(R.id.ll_loading);
        fillData();
        selectAll=(Button)findViewById(R.id.selectAll);
        selectAll.setOnClickListener(this);
        selectOpposite=(Button)findViewById(R.id.selectOpposite);
        selectOpposite.setOnClickListener(this);
        killProcess=(Button)findViewById(R.id.killProcess);
        killProcess.setOnClickListener(this);
        openSetting=(Button)findViewById(R.id.openSetting);
        openSetting.setOnClickListener(this);

    }

    //填充数据
    public void fillData(){
        ll_loading.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                infos=TaskInfoParser.getRunningTaskInfo(TaskManagerActivity.this);
                for (TaskInfo info : infos) {
                    if (info.isUsertask()) {
                        userTaskInfos.add(info);
                    } else {
                        systemTaskInfos.add(info);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ll_loading.setVisibility(View.INVISIBLE);
                        adapter = new TaskManagerAdapter();
                        lv_taskmanger.setAdapter(adapter);
                    }
                });
            }
        }.start();
    }

    //缓存复用对象
    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_size;
        CheckBox cb_status;
    }

    //进程适配
    private class TaskManagerAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
            boolean showsystem = sp.getBoolean("showsystem", true);
            if (showsystem) {
                return userTaskInfos.size() + systemTaskInfos.size() + 1 + 1;
            } else {
                return userTaskInfos.size() + 1;
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            TaskInfo info;
            if (position == 0) {
                return null;
            } else if (position == (userTaskInfos.size() + 1)) {
                return null;
            } else if (position <= userTaskInfos.size()) {
                info = userTaskInfos.get(position - 1);
            } else {
                info = systemTaskInfos.get(position - 1 - userTaskInfos.size()
                        - 1);
            }
            return info;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final TaskInfo info;
            //显示处理
            if (position == 0) {
                TextView tv = new TextView(getApplicationContext());
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setText("用户进程：" + userTaskInfos.size() + "个");
                return tv;
            } else if (position == (userTaskInfos.size() + 1)) {
                TextView tv = new TextView(getApplicationContext());
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setText("系统进程：" + systemTaskInfos.size() + "个");
                return tv;
            } else if (position <= userTaskInfos.size()) {
                info = userTaskInfos.get(position - 1);
            } else {
                info = systemTaskInfos.get(position - 1 - userTaskInfos.size()
                        - 1);
            }

            View view;
            ViewHolder holder;
            if(convertView!=null && convertView instanceof RelativeLayout){
                view=convertView;
                holder = (ViewHolder) view.getTag();
            }else {
                view=View.inflate(getApplicationContext(),R.layout.item_task_manager,null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view
                        .findViewById(R.id.iv_task_icon);
                holder.tv_name = (TextView) view
                        .findViewById(R.id.tv_task_name);
                holder.tv_size = (TextView) view
                        .findViewById(R.id.tv_task_size);
                holder.cb_status = (CheckBox) view
                        .findViewById(R.id.cb_task_status);
                view.setTag(holder);
            }

            holder.iv_icon.setImageDrawable(info.getIcon());
            holder.tv_name.setText(info.getAppname());
            holder.tv_size.setText("占用内存："
                    + Formatter.formatFileSize(getApplicationContext(),
                    info.getMemsize()));
            holder.cb_status.setChecked(info.isChecked());
            if (info.getPackagename().equals(getPackageName())) {
                // 就是我们自己。
                holder.cb_status.setVisibility(View.INVISIBLE);
            } else {
                holder.cb_status.setVisibility(View.VISIBLE);
            }

            return view;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectAll:
                selectAll();
                break;
            case R.id.selectOpposite:
                selectOpposite();
                break;
            case R.id.killProcess:
                killProcess();
                break;
            case R.id.openSetting:
                openSetting();
                break;
        }
    }

   //选择全部的item
    private void selectAll() {
        for (TaskInfo info : userTaskInfos) {
            if (info.getPackagename().equals(getPackageName())) {
                continue;
            }
            info.setChecked(true);
        }
        for (TaskInfo info : systemTaskInfos) {
            info.setChecked(true);
        }
        // 通知界面更新
        adapter.notifyDataSetChanged();
    }

    // 反选全部的item
    private void selectOpposite() {
        for (TaskInfo info : userTaskInfos) {
            if (info.getPackagename().equals(getPackageName())) {
                continue;
            }
            info.setChecked(!info.isChecked());
        }
        for (TaskInfo info : systemTaskInfos) {
            info.setChecked(!info.isChecked());
        }
        // 通知界面更新
      adapter.notifyDataSetChanged();
    }

    private void openSetting() {
        Intent intent = new Intent(TaskManagerActivity.this, TaskManagerSettingActivity.class);
        startActivity(intent);
    }

    //杀死后台进程
    public void killProcess() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int count = 0;
        long savemem = 0;
        List<TaskInfo> killedTaskInfos = new ArrayList<TaskInfo>();

        // 在遍历集合的时候 不可以修改集合的大小
        for (TaskInfo info : userTaskInfos) {
            if (info.isChecked()) {
                count++;
                savemem += info.getMemsize();
                am.killBackgroundProcesses(info.getPackagename());
                killedTaskInfos.add(info);
            }
        }

        for (TaskInfo info : systemTaskInfos) {
            if (info.isChecked()) {
                count++;
                savemem += info.getMemsize();
                am.killBackgroundProcesses(info.getPackagename());
                killedTaskInfos.add(info);
            }
        }
        for (TaskInfo info : killedTaskInfos) {
            if (info.isUsertask()) {
                userTaskInfos.remove(info);
            } else {
                systemTaskInfos.remove(info);
            }
        }
        runningProcessCount -= count;
        //重新计算可用内存
        avaliMem += savemem;
        // 更新标题
        tv_running_prcesscount.setText("运行中进程：" + runningProcessCount + "个");
        tv_ram_info
                .setText("可用/总内存："
                        + Formatter.formatFileSize(this, avaliMem)
                        + "/"
                        + Formatter.formatFileSize(this,
                        SystemInfoUtils.gettotalMem()));
        Toast.makeText(
                this,
                "杀死了" + count + "个进程,释放了"
                        + Formatter.formatFileSize(this, savemem) + "的内存", Toast.LENGTH_SHORT)
                .show();
        // 刷新界面
        adapter.notifyDataSetChanged();
    }
}
