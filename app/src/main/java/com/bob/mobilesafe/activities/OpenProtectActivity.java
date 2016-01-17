package com.bob.mobilesafe.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bob.mobilesafe.R;
//功能：防盗保护——开启/关闭防盗
public class OpenProtectActivity extends BaseActivity implements View.OnClickListener{
    private Button mNext,mPrevious;
    private TextView tv_setup_state;
    private CheckBox mCheckProtect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        mNext=(Button)findViewById(R.id.next);
        mPrevious=(Button)findViewById(R.id.previous);
        mNext.setOnClickListener(this);
        tv_setup_state=(TextView)findViewById(R.id.tv_setup_state);
        mCheckProtect=(CheckBox)findViewById(R.id.check_protect);
        boolean protecting=sp.getBoolean("protecting", false);
        if(protecting){
            tv_setup_state.setText("防盗保护已开启");
            mCheckProtect.setChecked(true);
        }else {
            tv_setup_state.setText("防盗保护未开启");
            mCheckProtect.setChecked(false);
        }

        mCheckProtect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               onChanged(isChecked);
            }
        });
        mPrevious.setOnClickListener(this);
    }



    //状态
    public void onChanged(boolean isChecked){
        if (isChecked) {
            tv_setup_state.setText("防盗保护已开启");
        } else {
            tv_setup_state.setText("防盗保护未开启");
        }

        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("protecting", isChecked);
        editor.apply();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.next:
                next();
                break;
            case R.id.previous:
                previous();
                break;
        }
    }

    @Override
    public void next() {
        //写配置信息
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean("finishsetup",true);
        editor.apply();
        startActivityAndfinishSelf(LostfindActivity.class);
        super.next();
    }

    @Override
    public void previous() {
        startActivityAndfinishSelf(SafePhoneActivity.class);
        super.previous();
    }
}
