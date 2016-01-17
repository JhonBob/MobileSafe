package com.bob.mobilesafe.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bob.mobilesafe.R;
import com.bob.mobilesafe.utils.SmsUtils;
import com.bob.mobilesafe.utils.UIUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;


//功能：高级工具
public class AtoolsActivity extends Activity implements View.OnClickListener{

    //进度提示
    public ProgressDialog pd;
    //号码归属地查询
    private TextView numberAddressQuery;
    //短信备份
    private TextView smsBack;
    //短信还原
    private TextView smsRestore;
    //应用程序锁
    private TextView open_app_lock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);

        numberAddressQuery=(TextView)findViewById(R.id.numberAddressQuery);
        numberAddressQuery.setOnClickListener(this);
        smsBack=(TextView)findViewById(R.id.smsBack);
        smsBack.setOnClickListener(this);
        smsRestore=(TextView)findViewById(R.id.smsRestore);
        smsRestore.setOnClickListener(this);
        open_app_lock=(TextView)findViewById(R.id.open_app_lock);
        open_app_lock.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.numberAddressQuery:
                addressQuery();
                break;
            case R.id.smsBack:
                smsBackup();
                break;
            case R.id.smsRestore:
                smsRestore();
                break;
            case R.id.open_app_lock:
                openAppLock();
                break;
        }
    }


    //号码归属地查询
    public void addressQuery(){
        Intent intent = new Intent(AtoolsActivity.this, NumberAddressQueryActivity.class);
        startActivity(intent);
    }

    //短信备份
    public void smsBackup(){
        pd=new ProgressDialog(AtoolsActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setTitle("提醒");
        pd.setMessage("备份中，你等着");
        pd.show();
        new Thread(){
            @Override
            public void run() {
                try{
                    boolean result= SmsUtils.smsBackup(getApplicationContext(), new SmsUtils.BackupCallBack() {
                        @Override
                        public void beforeSmsBackup(int size) {
                            pd.setMax(size);
                        }

                        @Override
                        public void onSmsBackup(int progress) {
                            pd.setProgress(progress);
                        }
                    });
                    if (result){
                        UIUtils.showToast(AtoolsActivity.this,"备份成功");
                    }
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                    UIUtils.showToast(AtoolsActivity.this, "文件生成失败");
                }catch (IllegalStateException e){
                    e.printStackTrace();
                    UIUtils.showToast(AtoolsActivity.this, "SD卡空间不足");
                }catch (IOException e){
                    e.printStackTrace();
                    UIUtils.showToast(AtoolsActivity.this, "读写错误");
                }finally {
                    pd.dismiss();
                }
            }
        }.start();
    }

    //短信还原
    public void smsRestore(){
        pd=new ProgressDialog(AtoolsActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setTitle("提醒");
        pd.setMessage("还原中，你等着");
        pd.show();
        new Thread(){
            @Override
            public void run() {
                try{
                    boolean result= SmsUtils.restoreSms(getApplicationContext(), new SmsUtils.RestoreSmsCallBack() {
                        @Override
                        public void beforeSmsRestore(int size) {
                            pd.setMax(size);
                        }

                        @Override
                        public void onSmsRestore(int progress) {
                            pd.setProgress(progress);
                        }
                    });
                    if (result){
                        UIUtils.showToast(AtoolsActivity.this,"还原成功");
                    }
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                    UIUtils.showToast(AtoolsActivity.this, "文件读取失败");
                }catch (IllegalStateException e){
                    e.printStackTrace();
                    UIUtils.showToast(AtoolsActivity.this, "SD卡错误");
                }catch (IOException e){
                    e.printStackTrace();
                    UIUtils.showToast(AtoolsActivity.this, "读写错误");
                }catch (XmlPullParserException e){
                    e.printStackTrace();
                }
                finally {
                    pd.dismiss();
                }
            }
        }.start();
    }

    //程序锁
    public void openAppLock(){
        Intent intent=new Intent(AtoolsActivity.this,AppLockActivity.class);
        startActivity(intent);
    }
}
