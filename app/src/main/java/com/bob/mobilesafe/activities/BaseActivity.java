package com.bob.mobilesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import com.bob.mobilesafe.R;

//功能：引导基类，抽取公用类
public  class BaseActivity extends Activity {
    //配置文件对象
    public SharedPreferences sp;
    //手势对象
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        sp=getSharedPreferences("config",MODE_PRIVATE);
        //手势识别器
        mGestureDetector=new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(Math.abs(velocityX)<100){
                    Toast.makeText(getApplicationContext(),"无动作,滑动太慢",Toast.LENGTH_SHORT).show();
                    return true;
                }
                if(e2.getRawX()-e1.getRawX()>100){
                    //上一界面
                    previous();
                    return true;
                }
                if(e1.getRawX()-e2.getRawX()>100){
                    //下一界面
                    next();
                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    public  void next(){
        overridePendingTransition(R.anim.next_in,R.anim.next_out);
    }

    public  void previous(){
        overridePendingTransition(R.anim.pre_in,R.anim.pre_out);
    }




    public void startActivityAndfinishSelf(Class<?> cls){
        Intent intent=new Intent(this,cls);
        startActivity(intent);
        finish();
    }

    //识别事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //分析手势事件
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
