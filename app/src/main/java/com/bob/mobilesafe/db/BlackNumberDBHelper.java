package com.bob.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/1/1.
 */

//功能：黑名单数据库管理类
public class BlackNumberDBHelper extends SQLiteOpenHelper {
    public BlackNumberDBHelper(Context context){
        super(context,"callsafe.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blackinfo (_id integer primary key autoincrement,number varchar(2),mode varchar(2))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
