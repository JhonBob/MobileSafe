package com.bob.mobilesafe.utils;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/1/2.
 */

//功能：高级工具---短信备份还原
public class SmsUtils {
    //定义一个接口作为回掉
    public interface BackupCallBack{
         void beforeSmsBackup(int size);
         void onSmsBackup(int progress);
    }
    public static boolean smsBackup(Context context,BackupCallBack callback)throws  IllegalStateException, IOException {
        XmlSerializer serializer = Xml.newSerializer();
        File sdDir = Environment.getExternalStorageDirectory();
        long freesize = sdDir.getFreeSpace();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                && freesize > 1024l * 1024l) {
            File file = new File(Environment.getExternalStorageDirectory(), "backup.xml");
            FileOutputStream os = new FileOutputStream(file);
            serializer.setOutput(os, "utf-8");
            serializer.startDocument("utf-8",true);
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse("content://sms/");
            Cursor cursor = resolver.query(uri, new String[]{"address", "body", "type", "date"}, null, null, null);
            int size = cursor.getCount();
            callback.beforeSmsBackup(size);
            int progress=0;
            serializer.startTag(null,"smss");
            serializer.attribute(null,"size",String.valueOf(size));
            while (cursor.moveToNext()) {
                serializer.startTag(null, "sms");

                serializer.startTag(null, "address");
                serializer.text(cursor.getString(0));
                serializer.endTag(null, "address");

                serializer.startTag(null, "body");
                //可能会有乱码问题需要处理，如果出现乱码会导致备份失败
                try {
                    String bodyencpyt = Crypto.encrypt("123", cursor.getString(1));
                    serializer.text(bodyencpyt);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    serializer.text("短信读取失败");
                }
                serializer.endTag(null, "body");

                serializer.startTag(null, "type");
                serializer.text(cursor.getString(2));
                serializer.endTag(null, "type");

                serializer.startTag(null, "date");
                serializer.text(cursor.getString(3));
                serializer.endTag(null, "date");

                serializer.endTag(null, "sms");
                try {
                    Thread.sleep(600);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                progress++;
                callback.onSmsBackup(progress);
            }
            cursor.close();
            serializer.endTag(null,"smss");
            serializer.endDocument();
            os.flush();
            os.close();
            return true;
        } else {
            throw new IllegalStateException("sd卡空间不足");
        }
    }

    public interface RestoreSmsCallBack{
         void beforeSmsRestore(int size);
         void onSmsRestore(int progress);
    }

    /**
     *
     * @param context
     * @return
     */
    public static boolean restoreSms(Context context,RestoreSmsCallBack callback)throws  IllegalStateException,
            IOException,XmlPullParserException{
        //判断 是否备份文件存在 读取sd卡的 文件
        //解析xml文件。
        //1. 创建pull解析器
        //2.初始化pull解析器，设置编码 inputstream
        //3.解析xml文件 while(文档末尾）
        //{
        //读取属性 size 总个数据. 调用接口的方法 beforeSmsRestore
        //每读取到一条短信 就把这个短信 body（解密） address date type获取出来
        //利用内容提供者  resolver.insert(Uri.parse("content://sms/"),contentValue);
        //每还原条 count++ 调用onSmsRestore(count);
        //}
        XmlPullParser xp = Xml.newPullParser();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory().toString(), "backup.xml");
            InputStream is = new FileInputStream(file);
            xp.setInput(is, "utf-8");
            int types=xp.getEventType();
            int size=xp.getColumnNumber();
            callback.beforeSmsRestore(size);
            int count=0;
            while(types != XmlPullParser.END_DOCUMENT){
                ContentValues values=new ContentValues();
                switch (types) {
                    case XmlPullParser.START_TAG:
                        //				获取当前节点的名字
                        if("smss".equals(xp.getName())){
                         System.out.println("当前结点：smss");
                        }
                        else if("sms".equals(xp.getName())){
                            System.out.println("当前结点：sms");
                        }
                        else if("address".equals(xp.getName())){
                            String address = xp.nextText();
                            values.put("address",address);
                        }
                        else if("body".equals(xp.getName())){
                            //获取当前节点的下一个节点的文本
                            String body = xp.nextText();
                            try {
                                String bodydecpyt = Crypto.decrypt("123", body);
                                //把文本保存至对象
                                values.put("body",bodydecpyt);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                        else if("type".equals(xp.getName())){
                            String type = xp.nextText();
                            values.put("type",type);
                        }
                        else if("date".equals(xp.getName())){
                            String date = xp.nextText();
                            values.put("date",date);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if("sms".equals(xp.getName())){
                            count++;
                            System.out.println(count);
                            callback.onSmsRestore(count);
                            ContentResolver resolver = context.getContentResolver();
                            Uri uri = Uri.parse("content://sms/");
                            resolver.insert(uri, values);
                        }
                        break;
                }
                //把指针移动到下一个节点，并且返回该节点的事件类型
                types = xp.next();
            }
            is.close();
            return true;
        } else {
            throw new IllegalStateException("sd卡有问题");
        }
    }
}

