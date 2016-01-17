package com.bob.mobilesafe.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bob.mobilesafe.R;
import com.bob.mobilesafe.utils.UIUtils;

//功能：防盗保护——安全号码
public class SafePhoneActivity extends BaseActivity implements View.OnClickListener {

    private Button mNext,mPrevious,mselect_contact;
    private EditText mPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        mNext=(Button)findViewById(R.id.next);
        mPrevious=(Button)findViewById(R.id.previous);
        mNext.setOnClickListener(this);
        mselect_contact=(Button)findViewById(R.id.select_concat);
        mPhone=(EditText)findViewById(R.id.et_satup_phone);
        mPhone.setText(sp.getString("safenumber", null));
        mselect_contact.setOnClickListener(this);
        mPrevious.setOnClickListener(this);
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
            case R.id.select_concat:
                select_contact();
                break;
        }
    }

    @Override
    public void next() {
        String phone=mPhone.getText().toString().trim();
        if(TextUtils.isEmpty(phone)){
            UIUtils.showToast(SafePhoneActivity.this,"请先设置安全号码");
        }
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("safenumber", phone);
        editor.apply();
        startActivityAndfinishSelf(OpenProtectActivity.class);
        super.next();
    }

    @Override
    public void previous() {
        startActivityAndfinishSelf(SimBindActivity.class);
        super.previous();
    }

    //选择要设置的安全号码
    public void select_contact(){
        Intent intent = new Intent(SafePhoneActivity.this, SelectContactActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null){
            String phone=data.getStringExtra("phone");
            mPhone.setText(phone);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
