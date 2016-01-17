package com.bob.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/1/4.
 */

//功能：应用加锁数据库管理类
public class ApplockDBHelper extends SQLiteOpenHelper {
    public ApplockDBHelper(Context context){
        super(context,"applock.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table info (_id integer primary key autoincrement,packname varchar(20)) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
