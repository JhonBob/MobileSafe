package com.bob.mobilesafe.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.bob.mobilesafe.R;
import com.bob.mobilesafe.fragments.LockedFragment;
import com.bob.mobilesafe.fragments.UnLockFragment;

//功能：应用锁活动，高级工具的第四个功能
public class AppLockActivity extends FragmentActivity implements View.OnClickListener{
    //锁控件
    private TextView tv_unlock;
    private TextView tv_locked;
    //碎片管理者
    private FragmentManager fm;
    //未加锁的碎片
    private UnLockFragment unlockFragment;
    //已加锁的碎片
    private LockedFragment lockedFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);

        tv_unlock=(TextView)findViewById(R.id.tv_unlock);
        tv_unlock.setOnClickListener(this);
        tv_locked=(TextView)findViewById(R.id.tv_locked);
        tv_locked.setOnClickListener(this);


        fm=getSupportFragmentManager();
        unlockFragment = new UnLockFragment();
        lockedFragment = new LockedFragment();
        //开启界面变化的事务
        FragmentTransaction ft  = fm.beginTransaction();
        ft.replace(R.id.fl_container, unlockFragment);
        ft.commit();
    }

    @Override
    public void onClick(View v) {
       FragmentTransaction ft=fm.beginTransaction();
        switch (v.getId()){
            case R.id.tv_unlock:
                tv_locked.setBackgroundResource(R.mipmap.tab_right_default);
                tv_unlock.setBackgroundResource(R.mipmap.tab_left_pressed);
                System.out.println("替换为未加锁碎片");
                ft.replace(R.id.fl_container,unlockFragment);
                break;
            case R.id.tv_locked:
                tv_locked.setBackgroundResource(R.mipmap.tab_right_pressed);
                tv_unlock.setBackgroundResource(R.mipmap.tab_left_default);
                System.out.println("替换为加锁碎片");
                ft.replace(R.id.fl_container,lockedFragment);
                break;
        }
        ft.commit();

    }
}
