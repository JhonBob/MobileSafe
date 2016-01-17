package com.bob.mobilesafe.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bob.mobilesafe.R;
import com.bob.mobilesafe.utils.Md5Utils;
import com.bob.mobilesafe.utils.UIUtils;

public class HomeActivity extends AppCompatActivity {
    private GridView mGv_home;
    private String[] names={"手机防盗","通讯卫士","软件管理","进程管理","流量统计","手机杀毒","缓存清理","高级工具","设置中心"};
    private int[] icons={R.mipmap.safe,
                         R.mipmap.callmsgsafe,
                         R.drawable.app_selector,
                         R.mipmap.taskmanager,
                         R.mipmap.netmanager,
                         R.mipmap.trojan,
                         R.mipmap.sysoptimize,
                         R.mipmap.atools,
                         R.mipmap.settings};
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mGv_home=(GridView)findViewById(R.id.gv);
        mGv_home.setAdapter(new HomeAdapter());
        sp=getSharedPreferences("config",MODE_PRIVATE);
        mGv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position){
                    case 0:
                        if(isSetupPwd()){
                            //设置过密码
                            showEnterPwdDialog();
                        }else {
                            //没有设置过密码
                            showSetupPwdDialog();
                        }
                        break;
                    case 1://通讯卫士
                        intent = new Intent(HomeActivity.this,CallSmsSafeActivity.class);
                        startActivity(intent);
                        break;
                    case 2://软件管家
                        intent = new Intent(HomeActivity.this,AppManagerActivity.class);
                        startActivity(intent);
                        break;
                    case 3://进程管理
                        intent = new Intent(HomeActivity.this,TaskManagerActivity.class);
                        startActivity(intent);
                        break;
                    case 4://流量统计
                        intent = new Intent(HomeActivity.this,TrafficManagerActivity.class);
                        startActivity(intent);
                        break;
                    case 5://病毒查杀
                        intent = new Intent(HomeActivity.this,AntiVirusActivity.class);
                        startActivity(intent);
                        break;
                    case 6://缓存清理
                        intent = new Intent(HomeActivity.this,CleanCacheActivity.class);
                        startActivity(intent);
                        break;
                    case 7://高级工具
                         intent = new Intent(HomeActivity.this,AtoolsActivity.class);
                        startActivity(intent);
                        break;
                    case 8://设置中心
                        intent = new Intent(HomeActivity.this,SettingCenterActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private EditText et_pwd;
    private EditText et_pwd_confirm;
    private Button bt_ok;
    private Button bt_cancle;
    //享元模式
    private AlertDialog dialog;
    private EditText et_enter_pwd;

    private void showEnterPwdDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=View.inflate(this,R.layout.dialog_enter_pwd,null);
        builder.setView(view);
        et_enter_pwd=(EditText)view.findViewById(R.id.et_enter_pwd);
        bt_ok=(Button)view.findViewById(R.id.bt_ok);
        bt_cancle=(Button)view.findViewById(R.id.bt_cancel);
        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enterpwd=et_enter_pwd.getText().toString().trim();
                if (TextUtils.isEmpty(enterpwd)){
                    UIUtils.showToast(HomeActivity.this,"密码不能为空");
                    return;
                }
                String savepwd=sp.getString("password", "");
                if(Md5Utils.encode(enterpwd).equals(savepwd)){
                   //密码一致进入手机防盗界面
                    UIUtils.showToast(HomeActivity.this, "进入手机防盗界面");
                    Intent intent=new Intent(HomeActivity.this,LostfindActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                }else {
                   //密码错误请检查密码
                    UIUtils.showToast(HomeActivity.this,"密码错误请检查密码");
                }
            }
        });
        dialog=builder.show();
    }

    private void showSetupPwdDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=View.inflate(this,R.layout.dialog_setup_pwd,null);
        builder.setView(view);
        et_pwd=(EditText)view.findViewById(R.id.et_pwd);
        et_pwd_confirm=(EditText)view.findViewById(R.id.et_pwd_confirm);
        bt_ok=(Button)view.findViewById(R.id.bt_ok);
        bt_cancle=(Button)view.findViewById(R.id.bt_cancel);
        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = et_pwd.getText().toString().trim();
                String pwd_confirm = et_pwd_confirm.getText().toString().trim();
                if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwd_confirm)) {
                    UIUtils.showToast(HomeActivity.this, "密码不能为空");
                    return;
                }
                if (!(pwd.equals(pwd_confirm))) {
                    UIUtils.showToast(HomeActivity.this, "两次密码不一致");
                }
                //进行MD5加密存储保证数据安全（只进行了一次加密）
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("password", Md5Utils.encode(pwd));
                editor.apply();
                dialog.dismiss();
            }
        });
        dialog=builder.show();
    }

    //判断用户是否设置过密码
    private boolean isSetupPwd(){
        String password=sp.getString("password",null);
        if(TextUtils.isEmpty(password)){
            return false;
        }else {
            return true;
        }
    }

    private class HomeAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           View mView;
            if(convertView==null){
                mView=View.inflate(getApplicationContext(),R.layout.item_home_grid,null);
            }else {
                mView=convertView;
            }

            ImageView iv=(ImageView)mView.findViewById(R.id.iv_home_icon);
            TextView tv=(TextView)mView.findViewById(R.id.tv_home_name);
            tv.setText(names[position]);
            if(position==0){
                String newname = getSharedPreferences("config", MODE_PRIVATE).getString("newname", "");
                if(!TextUtils.isEmpty(newname)){
                    //如果用户设置了新的名称 应该显示新的名称
                    tv.setText(newname);
                }
            }
            iv.setImageResource(icons[position]);
            return mView;
        }
    }
}
