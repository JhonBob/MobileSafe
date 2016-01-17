package com.bob.mobilesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bob.mobilesafe.R;

//功能：高级工具——程序锁——密码输入界面
public class EntpwdActivity extends Activity {

    //密码输入框
    private EditText et_password;
    //密码确认按钮
    private Button click;
    //应用程序包名
    private String packname;
    //已加锁的应用图标
    private ImageView iv_lock_appicon;
    //应用程序名
    private TextView tv_lock_appname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entpwd);

        et_password = (EditText) findViewById(R.id.et_password);
        click=(Button)findViewById(R.id.click);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = et_password.getText().toString().trim();
                if("123".equals(pwd)){
                    //通知看门狗 ，这个是熟人， 停止保护。
                    //发送自定义的广播消息。
                    Intent intent = new Intent();
                    intent.setAction("com.bob.mobilesafe.stopprotect");
                    intent.putExtra("packname", packname);
                    sendBroadcast(intent);
                    finish();//关闭输入密码的界面。
                }else{
                    Toast.makeText(EntpwdActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
                    Animation aa = AnimationUtils.loadAnimation(EntpwdActivity.this, R.anim.shake);
                    et_password.startAnimation(aa);
                }
            }
        });

        packname = getIntent().getStringExtra("packname");
        tv_lock_appname = (TextView) findViewById(R.id.tv_lock_appname);
        iv_lock_appicon = (ImageView) findViewById(R.id.iv_lock_appicon);
        PackageManager pm = getPackageManager();

        try {
            iv_lock_appicon.setImageDrawable(pm.getApplicationInfo(packname, 0).loadIcon(pm));
            tv_lock_appname.setText(pm.getApplicationInfo(packname, 0).loadLabel(pm).toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        //回桌面。
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN" );
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
        finish();//关闭掉输入密码的界面。
    }
}
