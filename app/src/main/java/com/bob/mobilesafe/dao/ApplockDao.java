package com.bob.mobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.bob.mobilesafe.db.ApplockDBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/4.
 */
//功能：应用安全锁数据库API
public class ApplockDao {
    private ApplockDBHelper helper;
    private Context context;

    public ApplockDao(Context context) {
        helper=new ApplockDBHelper(context);
        this.context=context;
    }

    /**
     * 查询某个包名是否需要别锁定
     * @param packname
     * @return
     */
    public boolean find(String packname){
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("info", null, "packname=?", new String[]{packname}, null, null, null);
        if(cursor.moveToNext()){
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 查询全部的锁定的包名
     * @return
     */
    public List<String> findAll(){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("info", new String[]{"packname"}, null, null, null, null, null);
        List<String> packnames = new ArrayList<String>();
        while(cursor.moveToNext()){
            packnames.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return packnames;
    }

    /**
     * 添加一个包名到程序锁数据库
     * @param packname
     */
    public void add(String packname){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packname", packname);
        db.insert("info", null, values);
        db.close();
        //通知内容观察者数据变化了。
        context.getContentResolver().notifyChange(Uri.parse("content://com.bob.mobilesafe.applock"), null);
    }

    /**
     * 删除一个包名，从程序锁数据库删除
     * @param packname
     */
    public void delete(String packname){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("info", "packname=?", new String[]{packname});
        db.close();
        //通知内容观察者数据变化了。
        context.getContentResolver().notifyChange(Uri.parse("content://com.bob.mobilesafe.applock"), null);
    }
}
