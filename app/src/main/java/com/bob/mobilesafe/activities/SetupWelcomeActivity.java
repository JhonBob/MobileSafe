package com.bob.mobilesafe.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bob.mobilesafe.R;
//功能：防盗保护——欢迎引导界面
public class SetupWelcomeActivity extends BaseActivity {
    private Button mNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        mNext=(Button)findViewById(R.id.next);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
    }

    @Override
    public void next() {
        startActivityAndfinishSelf(SimBindActivity.class);
        super.next();
    }
}
