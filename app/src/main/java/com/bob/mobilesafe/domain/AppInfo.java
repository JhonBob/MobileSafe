package com.bob.mobilesafe.domain;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2016/1/3.
 */

//手机应用信息封装类
public class AppInfo {
    //应用图标
    private Drawable icon;
    //应用名
    private String name;
    //应用安装位置 ture in rom
    private boolean inRoom;
    //应用大小
    private long appSize;
    //应用类型 true 用户应用
    private boolean userApp;
    //应用包名
    private String packgeName;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isInRoom() {
        return inRoom;
    }

    public void setInRoom(boolean inRoom) {
        this.inRoom = inRoom;
    }

    public long getAppSize() {
        return appSize;
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    public String getPackgeName() {
        return packgeName;
    }

    public void setPackgeName(String packgeName) {
        this.packgeName = packgeName;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                ",name='" + name + '\'' +
                ", inRoom=" + inRoom +
                ", appSize=" + appSize +
                ", userApp=" + userApp +
                ", packgeName='" + packgeName + '\'' +
                '}';
    }

    private String apkpath;


    public String getApkpath() {
        return apkpath;
    }

    public void setApkpath(String apkpath) {
        this.apkpath = apkpath;
    }

}
