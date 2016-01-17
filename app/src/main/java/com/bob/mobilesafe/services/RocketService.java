package com.bob.mobilesafe.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bob.mobilesafe.R;
import com.bob.mobilesafe.activities.BackgroundActivity;

//功能：小火箭服务
public class RocketService extends Service {
    //窗口管理器
    private WindowManager wm;
    //小火箭图标
    private ImageView iv;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            mParams.y -=5;
            wm.updateViewLayout(iv, mParams);
        };
    };

    private WindowManager.LayoutParams mParams;
    @Override
    public void onCreate() {
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        iv = new ImageView(this);
        iv.setBackgroundResource(R.drawable.rocket);
        AnimationDrawable rocketAnimation = (AnimationDrawable) iv.getBackground();
        rocketAnimation.start();
        mParams = new WindowManager.LayoutParams();
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        // 修改完左上角对其
        mParams.gravity = Gravity.LEFT + Gravity.TOP;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                // | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE 自定义的土司需要用户触摸
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        mParams.format = PixelFormat.TRANSLUCENT;
        // mParams.type = WindowManager.LayoutParams.TYPE_TOAST; 土司窗体天生不响应触摸事件
        mParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        wm.addView(iv, mParams);

        iv.setOnTouchListener(new View.OnTouchListener() {
            int startX;
            int startY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int newX = (int) event.getRawX();
                        int newY = (int) event.getRawY();
                        int dx = newX - startX;
                        int dy = newY - startY;
                        mParams.x +=dx;
                        mParams.y +=dy;
                        wm.updateViewLayout(iv, mParams);
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        int lastx = (int) event.getRawX();
                        int lasty = (int) event.getRawY();
                        if(lastx>100&&lastx<500&&lasty>330){
                            Toast.makeText(getApplicationContext(), "发射火箭", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RocketService.this,BackgroundActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            new Thread(){
                                public void run() {
                                    for(int i=0;i<900;i+=5){
                                        handler.sendEmptyMessage(0);
                                        try {
                                            Thread.sleep(5);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }.start();
                        }
                        break;
                }
                return true;
            }
        });

        super.onCreate();
    }





    @Override
    public void onDestroy() {
        wm.removeView(iv);
        iv = null;
        super.onDestroy();
    }
}
