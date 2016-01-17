package com.bob.mobilesafe.activities;

import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bob.mobilesafe.R;
import com.bob.mobilesafe.dao.NumberAddressDao;

//功能：高级工具——号码归属地查询
public class NumberAddressQueryActivity extends AppCompatActivity {
    //确认查询按钮
    private Button query;
    //号码输入
    private EditText et_phone_number;
    //归属地显示
    private TextView tv_address_info;
    //震动
    private Vibrator vibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_address_query);
        //得到系统的震动服务
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        query=(Button)findViewById(R.id.query);
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               query();
            }
        });
        tv_address_info = (TextView) findViewById(R.id.tv_address_info);
        et_phone_number = (EditText) findViewById(R.id.et_phone_number);
        et_phone_number.addTextChangedListener(new TextWatcher() {
            //当文本变化的时候调用的方法
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            //当文本变化之前调用的方法
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }
            //当文本变化之后调用的方法
            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                String location = NumberAddressDao.getLocation(text);
                tv_address_info.setText("归属地："+location);
            }
        });
    }



    public void query(){
        String phone = et_phone_number.getText().toString().trim();
        if(TextUtils.isEmpty(phone)){
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            et_phone_number.startAnimation(shake);
            vibrator.vibrate(new long[]{200,100,300,100,300,100}, 2);
            Toast.makeText(this, "号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        String location = NumberAddressDao.getLocation(phone);
        tv_address_info.setText("归属地："+location);
    }
}
