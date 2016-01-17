package com.bob.mobilesafe.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bob.mobilesafe.R;

//功能：手机防盗（主界面）
public class LostfindActivity extends Activity {
    private static final String TAG="LostfindActivity";
    //重新设置安全信息
    private Button reEnterSetup;
    //配置
    private SharedPreferences sp;
    //安全号码
    private TextView tv_lostfind_number;
    //防盗保护状态
    private ImageView iv_lostfind_status;
    //菜单
    private RelativeLayout rl_menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lostfind);
        sp=getSharedPreferences("config",MODE_PRIVATE);
        //判断用户是否完成过设置向导
        if(isFinishSetup()){
            onStart();
            rl_menu = (RelativeLayout) findViewById(R.id.rl_menu);
            Log.i(TAG, "完成过设置向导，进入正常的界面");
            // 安全号码
            tv_lostfind_number = (TextView) findViewById(R.id.tv_lostfind_number);
            // 保护的状态
            iv_lostfind_status = (ImageView) findViewById(R.id.iv_lostfind_status);
            tv_lostfind_number.setText(sp.getString("safenumber", ""));
            boolean protecting = sp.getBoolean("protecting", false);
            if (protecting) {
                iv_lostfind_status.setImageResource(R.mipmap.lock);
            } else {
                iv_lostfind_status.setImageResource(R.mipmap.unlock);
            }
        }else{
            //进入设置向导界面
            Log.i(TAG,"进入设置向导界面");
            Intent in=new Intent(LostfindActivity.this,SetupWelcomeActivity.class);
            startActivity(in);
            //关闭当前手机防盗界面
            finish();
        }
        reEnterSetup=(Button)findViewById(R.id.reEntrySetup);
        reEnterSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LostfindActivity.this,SetupWelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean isFinishSetup(){
        return sp.getBoolean("finishsetup",false);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lost_find_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (R.id.item_change_name == menuItem.getItemId()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("请输入新的手机防盗名称");
            final EditText et = new EditText(this);
            builder.setView(et);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int arg1) {
                    String newname = et.getText().toString().trim();
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("newname", newname);
                    editor.commit();
                }
            });
            builder.show();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
