package com.bob.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2016/1/4.
 */

//功能：应用程序进程封装类
public class TaskInfo {
    private Drawable icon;
    private String appname;
    private String packagename;
    private boolean usertask;
    private long memsize;
    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }

    public boolean isUsertask() {
        return usertask;
    }

    public void setUsertask(boolean usertask) {
        this.usertask = usertask;
    }

    public long getMemsize() {
        return memsize;
    }

    public void setMemsize(long memsize) {
        this.memsize = memsize;
    }

    @Override
    public String toString() {
        return "TaskInfo{" +
                "appname='" + appname + '\'' +
                ", packagename='" + packagename + '\'' +
                ", usertask=" + usertask +
                ", memsize=" + memsize +
                '}';
    }
}
