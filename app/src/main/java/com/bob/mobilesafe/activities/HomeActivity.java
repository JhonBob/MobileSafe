package com.bob.mobilesafe.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.bob.mobilesafe.R;
import com.bob.mobilesafe.adapter.MainAdapterItem;
import com.bob.mobilesafe.adapter.MainItem;
import com.bob.mobilesafe.dao.AntiVirusDao;
import com.bob.mobilesafe.ui.Rotate3dAnimation;
import com.bob.mobilesafe.utils.Md5Utils;
import com.bob.mobilesafe.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

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
    private SlidingDrawer mDrawer;
    private ImageButton imbg;
    private ListView listScan;
    private ImageView scanBg,scanIcon;
    private Button scanBtn;
    private Animation am=null;
    LinearInterpolator lin;
    private FrameLayout mContainer;
    private LinearLayout imageview2;
    private SQLiteDatabase db;
    private PackageManager pm = null;
    protected static final int STOP = 100;
    private ScrollView sv;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    if(msg.what==STOP){
//					 imageView2.removeAllViews();
//				     am. setRepeatCount ( -1 );
//				     am.setRepeatCount(Animation.INFINITE);
//				     am.setInterpolator(lin);
                        am.setRepeatCount(0);
                        scanBtn.setClickable(true);
                    }
                    break;

                default:
                    break;
            }
            String str = (String) msg.obj;
            TextView tv = new TextView(getApplicationContext());
            tv.setTextSize(20);
            tv.setText(str);
            imageview2.setOrientation(LinearLayout.VERTICAL);
            imageview2.addView(tv);
            sv.scrollBy(0, 30);
        }

    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mGv_home=(GridView)findViewById(R.id.gv);
        mGv_home.setAdapter(new HomeAdapter());
        sp=getSharedPreferences("config",MODE_PRIVATE);
        mDrawer= (SlidingDrawer) findViewById(R.id.slidingdrawer);
        imbg=(ImageButton)findViewById(R.id.handle);
        listScan= (ListView) findViewById(R.id.list_scan);
        scanIcon= (ImageView) findViewById(R.id.main_iv_scan);
        scanBg= (ImageView) findViewById(R.id.scan_bg);
        scanBtn= (Button) findViewById(R.id.scan_btn);
        imageview2= (LinearLayout) findViewById(R.id.imageview2);
        sv= (ScrollView) findViewById(R.id.sv);
        pm = this.getPackageManager();
        mContainer= (FrameLayout) findViewById(R.id.container);
        mContainer.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
        mDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                listScan.setVisibility(View.GONE);
                mContainer.setVisibility(View.GONE);
            }
        });
        mDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                listScan.setVisibility(View.VISIBLE);
                mContainer.setVisibility(View.VISIBLE);

            }
        });
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();

            }
        });
        MainAdapterItem myadapter = new MainAdapterItem(getItem(),this);
        listScan.setAdapter(myadapter);
        listScan.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int itemid = (int) listScan.getItemIdAtPosition(position);
            }

        });
        mGv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                into(position);
            }
        });
    }



    private void into(int position) {
        Intent intent;
        switch (position) {
            case 0:
                if (isSetupPwd()) {
                    //设置过密码
                    showEnterPwdDialog();
                } else {
                    //没有设置过密码
                    showSetupPwdDialog();
                }
                break;
            case 1://通讯卫士
                intent = new Intent(HomeActivity.this, CallSmsSafeActivity.class);
                startActivity(intent);
                break;
            case 2://软件管家
                intent = new Intent(HomeActivity.this, AppManagerActivity.class);
                startActivity(intent);
                break;
            case 3://进程管理
                intent = new Intent(HomeActivity.this, TaskManagerActivity.class);
                startActivity(intent);
                break;
            case 4://流量统计
                intent = new Intent(HomeActivity.this, TrafficManagerActivity.class);
                startActivity(intent);
                break;
            case 5://病毒查杀
                intent = new Intent(HomeActivity.this, AntiVirusActivity.class);
                startActivity(intent);
                break;
            case 6://缓存清理
                intent = new Intent(HomeActivity.this, CleanCacheActivity.class);
                startActivity(intent);
                break;
            case 7://高级工具
                intent = new Intent(HomeActivity.this, AtoolsActivity.class);
                startActivity(intent);
                break;
            case 8://设置中心
                intent = new Intent(HomeActivity.this, SettingCenterActivity.class);
                startActivity(intent);
                break;
        }
    }


    //扫描
    public void scan(){
        scanBg.setImageDrawable(getResources().getDrawable(R.drawable.main_circle_bg_scan));
        //设置为匀速
        lin = new LinearInterpolator();
        am = new RotateAnimation(0, +360,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f);

        // 动画开始到结束的执行时间(1000 = 1 秒)
//		     am. setDuration ( 1000 );

        // 动画重复次数(-1 表示一直重复)
        am.setRepeatCount(-1);
        am.setRepeatCount(Animation.INFINITE);
        am.setInterpolator(lin);
        // 图片配置动画
//		     progressImage. setAnimation (am);
//		     am. startNow ();
        scanIcon.startAnimation(am);
        String name = scanBtn.getText().toString();
        if ("一键体检".equals(name)) {
            applyRotation(0, 90, R.id.scan_btn);
            am.setDuration(1000);
            scanBtn.setText("返回");
            showKill();
            scanBtn.setClickable(false);
            return;
        } else {
            applyRotation(0, 90, R.id.imageview2);
            scanBtn.setText("一键体检");
            am.setRepeatCount(0);
            scanIcon.setVisibility(View.GONE);
            scanBg.setImageResource(R.drawable.main_status_baohu);
            return;
        }
    }




    private List<MainItem> getItem(){
        List<MainItem> listItem = new ArrayList<MainItem>();
        MainItem mainItem1 = new MainItem("骚扰拦截","全面拦截垃圾短信和骚扰电话",getResources().getDrawable(R.drawable.block_icon_pressed));
        listItem.add(mainItem1);
        MainItem mainItem2= new MainItem("流量监控","开启流量监控.避免流量损失",getResources().getDrawable(R.drawable.traffic_icon_pressed));
        listItem.add(mainItem2);
        MainItem mainItem3 = new MainItem("病毒查杀","查杀病毒.恶意软件",getResources().getDrawable(R.mipmap.antivrus_icon_pressed));
        listItem.add(mainItem3);
        MainItem mainItem4 = new MainItem("手机防盗","开启防盗.手机不丢失",getResources().getDrawable(R.drawable.progress_icon_pressed));
        listItem.add(mainItem4);
        return listItem;
    }



    private void applyRotation(float start, float end, final int viewId){
        final float centerX = mContainer.getWidth() / 2.0f;
        final float centerY = mContainer.getHeight() / 2.0f;
        Rotate3dAnimation rotation =
                new Rotate3dAnimation(start, end, centerX, centerY, 200.0f, true);
        rotation.setDuration(500);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation arg0) {

                mContainer.post(new Runnable() {
                    public void run() {
                        if(viewId == R.id.scan_btn){
                            listScan.setVisibility(View.GONE);
                            imageview2.setVisibility(View.VISIBLE);
                        }else if (viewId == R.id.imageview2) {
                            imageview2.setVisibility(View.GONE);
                            listScan.setVisibility(View.VISIBLE);
                        }
                        Rotate3dAnimation rotatiomAnimation = new Rotate3dAnimation(-90, 0, centerX, centerY, 200.0f, false);
                        rotatiomAnimation.setDuration(500);
                        rotatiomAnimation.setInterpolator(new DecelerateInterpolator());

                        mContainer.startAnimation(rotatiomAnimation);
                    }
                });

            }
            public void onAnimationRepeat(Animation arg0) {
            }

            public void onAnimationStart(Animation arg0) {
            }
        });
        mContainer.startAnimation(rotation);
    }



    //查杀病毒
    private void showKill() {
        new Thread(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                // 检查手机里面的每一个应用程序
                List<PackageInfo> packInfos = pm.getInstalledPackages(0);
                int virustotal = 0;
                for(PackageInfo info :packInfos){
                    try {
                        sleep(200);
                        Message msg = Message.obtain();
                        String appname = info.applicationInfo.loadLabel(pm).toString();
                        System.out.println(appname);
                        msg.obj = "正在扫描:"+appname;
                        handler.sendMessage(msg);

                        //应用路径
                        String apkpath = info.applicationInfo.sourceDir;
                        //获取这个文件的 特征码
                        String md5 = Md5Utils.getFileMd5(apkpath);
                        System.out.println(md5);
                        //检查是否是病毒
                        String result = AntiVirusDao.checkVirus(md5);

                        if(result==null){
                            msg = Message.obtain();
                            msg.obj = appname+":"+"扫描安全";
                            handler.sendMessage(msg);
                        }else {
                            msg = Message.obtain();
                            msg.obj = appname+":"+result;
                            handler.sendMessage(msg);
                            virustotal++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                Message msg = Message.obtain();
                msg.what = STOP;
                msg.obj = "扫描完毕 ,共发现" + virustotal + "个病毒";
                handler.sendMessage(msg);
            }

        }.start();
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
            tv.setTextColor(Color.WHITE);
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
