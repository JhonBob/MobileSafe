package com.bob.mobilesafe.activities;

import android.app.Activity;

import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.bob.mobilesafe.R;

public class BackgroundActivity extends Activity {

    private ImageView iv_top;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);
        iv_top=(ImageView)findViewById(R.id.iv_top);
        AlphaAnimation aa=new AlphaAnimation(0.0f,1.0f);
        aa.setDuration(1000);
        iv_top.startAnimation(aa);
        new Thread(){
            @Override
            public void run() {
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                finish();
            }
        }.start();
    }
}
