package com.bob.mobilesafe.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bob.mobilesafe.R;
import com.bob.mobilesafe.utils.UIUtils;

//功能：防盗保护——SIM卡绑定
public class SimBindActivity extends BaseActivity implements View.OnClickListener{
    private TextView mBindUbind;
    private TelephonyManager tm;
    private ImageView iv_setup2_state;
    private Button mNext,mPrevious;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        mNext=(Button)findViewById(R.id.next);
        mPrevious=(Button)findViewById(R.id.previous);
        mBindUbind=(TextView)findViewById(R.id.bindUnbindSim);
        tm=(TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        iv_setup2_state=(ImageView)findViewById(R.id.iv_setup2_state);
        mBindUbind.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mPrevious.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bindUnbindSim:
                bindUbind();
                break;
            case R.id.next:
                next();
                break;
            case R.id.previous:
                previous();
                break;
        }
    }

    //绑定解绑SIM卡
    public void bindUbind(){
        //判断是否绑定
        String savedSim = sp.getString("sim", null);
        if (TextUtils.isEmpty(savedSim)) {
            //号码唯一标识
            String simSerial = tm.getSimSerialNumber();
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("sim", simSerial);
            editor.apply();
            UIUtils.showToast(SimBindActivity.this,"sim卡绑定成功");
            iv_setup2_state.setImageResource(R.mipmap.lock);
        }else {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("sim", null);
            editor.apply();
            UIUtils.showToast(SimBindActivity.this,"解除绑定sim卡");
            iv_setup2_state.setImageResource(R.mipmap.unlock);
        }
    }



    public void isBind(){
        String savedSim = sp.getString("sim", null);
        if (TextUtils.isEmpty(savedSim)) {
            iv_setup2_state.setImageResource(R.mipmap.lock);
        }else {
            iv_setup2_state.setImageResource(R.mipmap.unlock);
        }
    }


    @Override
    public void next() {
        String savedSim = sp.getString("sim", null);
        if (TextUtils.isEmpty(savedSim)) {
            UIUtils.showToast(SimBindActivity.this,"请先绑定sim卡");
            return;
        }else {
            startActivityAndfinishSelf(SafePhoneActivity.class);
        }
        super.next();
    }

    @Override
    public void previous() {
        startActivityAndfinishSelf(SetupWelcomeActivity.class);
        super.previous();
    }
}
