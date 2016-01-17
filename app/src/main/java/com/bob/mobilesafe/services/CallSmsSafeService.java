package com.bob.mobilesafe.services;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObservable;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.bob.mobilesafe.dao.BlackNumberDao;

import java.lang.reflect.Method;
import java.net.URI;

//功能：黑名单拦截服务
public class CallSmsSafeService extends Service{
    private static final String TAG="CallSmsSafeService";

    private InnerSmsReceiver receiver;
    private BlackNumberDao dao;
    //电话管理器
    private TelephonyManager tm;
    private MyPhoneListener listener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        dao=new BlackNumberDao(this);
        tm=(TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        listener=new MyPhoneListener();
        tm.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);


        receiver=new InnerSmsReceiver();
        IntentFilter filter=new IntentFilter("android.provider.Telephony.SMS_RECEIVE");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(receiver, filter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        receiver=null;
        tm.listen(listener,PhoneStateListener.LISTEN_NONE);
        listener=null;
        super.onDestroy();
    }

    private class InnerSmsReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "短信到来了");
            //发件人是否在黑名单里
            Object[] objects=(Object[])intent.getExtras().get("pdus");
            for(Object object:objects){
                SmsMessage smsMessage= SmsMessage.createFromPdu((byte[]) object);
                String sender=smsMessage.getOriginatingAddress();
            String mode=dao.findBlackMode(sender);
                if("1".equals(mode)||"2".equals(mode)){
                    //黑名单短信拦截
                    Log.i(TAG, "短信拦截");
                    abortBroadcast();
                }
                //智能拦截（关键字拦截）
                //中文使用分词技术
                String body=smsMessage.getMessageBody();
                if(body.contains("fapiao")){
                    Log.i(TAG, "垃圾短信拦截");
                    abortBroadcast();
                }
            }
        }
    }

    private class MyPhoneListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state){
                case TelephonyManager.CALL_STATE_IDLE://空闲
                    break;
                case TelephonyManager.CALL_STATE_RINGING://响铃
                    String mode=dao.findBlackMode(incomingNumber);
                    if ("1".equals(mode)||"3".equals(mode)){
                        Log.i("MyPhoneListener", "挂断电话");
                        //挂断后会在另外的应用生成记录
                        //监听数据库变化，删除来电显示
                        Uri uri=Uri.parse("content://call_log/calls");
                        getContentResolver().registerContentObserver(uri, true, new CallLogObserver(new Handler(), incomingNumber));
                        //用代码挂断电话
                        //反射挂断
                        endCall();
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://接通状态
                    break;
            }
        }
    }

    public void endCall(){
        try{
            //通过反射调用系统底层API，再通过AIDL获得IBinder接口，最后调用endCALL()挂断电话
            Class clazz=getClassLoader().loadClass("android.os.ServiceManager");
            Method method=clazz.getDeclaredMethod("getService", String.class);
            IBinder iBinder=(IBinder)method.invoke(null, TELEPHONY_SERVICE);
            ITelephony iTelephony=ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();
            //开通呼叫转移
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //清除呼叫记录
    public void deleteCall(String incomingnumber){
        ContentResolver contentResolver=getContentResolver();
        Uri uri=Uri.parse("content://call_log/calls");
        contentResolver.delete(uri,"number= ?",new String[]{incomingnumber});
    }

    private class CallLogObserver extends ContentObserver{
        private String incomingnumber;

        public CallLogObserver(Handler handler,String incomingnumber) {
            super(handler);
            this.incomingnumber=incomingnumber;
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.i("CallLogObserver", "呼叫记录数据库发生变化");
            getContentResolver().unregisterContentObserver(this);
            deleteCall(incomingnumber);
            super.onChange(selfChange);
        }
    }
}
