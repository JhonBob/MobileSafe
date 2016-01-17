package com.bob.mobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bob.mobilesafe.db.BlackNumberDBHelper;
import com.bob.mobilesafe.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/1.
 */

//功能：手机黑名单数据库API
public class BlackNumberDao {
    private BlackNumberDBHelper helper;

    public BlackNumberDao(Context context){
        helper=new BlackNumberDBHelper(context);
    }

    //号码和模式
    public boolean add(String number,String mode){
        SQLiteDatabase db=helper.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("number",number);
        value.put("mode",mode);
       long rawid= db.insert("blackinfo",null,value);
        if(rawid==-1){
            return false;
        }else {
            return true;
        }
    }

    //删除号码
    public boolean delete(String number){
        SQLiteDatabase db=helper.getWritableDatabase();
        int rawnumber=db.delete("blackinfo", "number=?", new String[]{number});
        if(rawnumber==0){
            return false;
        }else {
            return true;
        }
    }

    //修改黑名单号码和拦截模式
    public boolean updateMode(String number,String newmode){
        SQLiteDatabase db=helper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("mode",newmode);
        int rawnumber=db.update("blackinfo", values, "number=?", new String[]{number});
        if(rawnumber==0){
            return false;
        }else {
            return true;
        }
    }

    //返回拦截模式 （1.全部拦截2.短信拦截3.电话拦截）
    public String findBlackMode(String number){
        String mode="0";
        SQLiteDatabase db=helper.getReadableDatabase();
        Cursor cursor=db.query("blackinfo", new String[]{"mode"}, "number=?", new String[]{number}, null, null, null);
        if(cursor.moveToNext()){
            mode=cursor.getString(0);
        }
        cursor.close();
        db.close();
        return mode;
    }


    //查询所有的数据
    public List<BlackNumberInfo> findAll(){
        SQLiteDatabase db=helper.getReadableDatabase();
        Cursor cursor=db.query("blackinfo", new String[]{"number", "mode"}, null, null, null, null, null);
        List<BlackNumberInfo> blackNumberInfos=new ArrayList<BlackNumberInfo>();
        while(cursor.moveToNext()){
            BlackNumberInfo info=new BlackNumberInfo();
            String number=cursor.getString(0);
            String mode=cursor.getString(1);
            info.setNumber(number);
            info.setMode(mode);
            blackNumberInfos.add(info);
        }
        cursor.close();
        db.close();
        return blackNumberInfos;
    }

    //分页查询所有的数据
    public List<BlackNumberInfo> findPart(int pagenumber,int pagesize){
        SQLiteDatabase db=helper.getReadableDatabase();
        Cursor cursor=db.rawQuery("select number,mode from blackinfo limit ? offset ?",
                new String[]{String.valueOf(pagesize), String.valueOf(pagesize * pagenumber)});
        List<BlackNumberInfo> blackNumberInfos=new ArrayList<BlackNumberInfo>();
        while(cursor.moveToNext()){
            BlackNumberInfo info=new BlackNumberInfo();
            String number=cursor.getString(0);
            String mode=cursor.getString(1);
            info.setNumber(number);
            info.setMode(mode);
            blackNumberInfos.add(info);
        }
        cursor.close();
        db.close();
        return blackNumberInfos;
    }


    //分页查询所有条目数
    public int  getTotalNumber(){
        SQLiteDatabase db=helper.getReadableDatabase();
        Cursor cursor=db.rawQuery("select count(*) from blackinfo",null);
        cursor.moveToNext();
        int count=cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    //分批加载所有的数据
    public List<BlackNumberInfo> findpart(int startIndex ,int maxCount){
        SQLiteDatabase db=helper.getReadableDatabase();
        Cursor cursor=db.rawQuery("select number,mode from blackinfo order by _id desc limit ? offset ?",
                new String[]{String.valueOf(maxCount),
                        String.valueOf(startIndex)});
        List<BlackNumberInfo> blackNumberInfos=new ArrayList<BlackNumberInfo>();
        while(cursor.moveToNext()){
            BlackNumberInfo info=new BlackNumberInfo();
            String number=cursor.getString(0);
            String mode=cursor.getString(1);
            info.setNumber(number);
            info.setMode(mode);
            blackNumberInfos.add(info);
        }
        cursor.close();
        db.close();
        return blackNumberInfos;
    }
}

