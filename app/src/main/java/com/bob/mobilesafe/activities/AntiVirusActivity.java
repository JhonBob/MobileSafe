package com.bob.mobilesafe.activities;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bob.mobilesafe.R;
import com.bob.mobilesafe.dao.AntiVirusDao;
import com.bob.mobilesafe.utils.Md5Utils;

import java.util.List;

//功能：病毒查杀
public class AntiVirusActivity extends Activity {
    //Handler任务分发标志
    protected static final int SCANNING = 1;
    protected static final int SCAN_FINISH = 2;
    protected static final int SCAN_BENGIN = 0;

    //雷达扫描控件
    private ImageView iv_scan;
    //雷达扫描进度条
    private ProgressBar progressBar1;
    //雷达正在扫描的应用
    private TextView tv_scan_status;
    //已扫描的应用
    private LinearLayout ll_container;
    //包管理器
    private PackageManager pm;
    //是否
    private boolean flag;

    //分发任务的线程
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SCAN_BENGIN:
                    tv_scan_status.setText("正在初始化8核杀毒引擎...");
                    break;
                case SCANNING:
                    ScanInfo info = (ScanInfo) msg.obj;
                    tv_scan_status.setText("正在扫描:"+info.appname);
                    TextView child = new TextView(getApplicationContext());
                    if(info.isVirus){
                        child.setTextColor(Color.RED);
                    }else{
                        child.setTextColor(Color.BLACK);
                    }
                    child.setText(info.appname+":"+info.desc);
                    ll_container.addView(child, 0);
                    break;
                case SCAN_FINISH:
                    tv_scan_status.setText("扫描完毕！");
                    iv_scan.clearAnimation();
                    Toast.makeText(getApplicationContext(), "扫描完毕。。", Toast.LENGTH_SHORT).show();
                    //停止扫描
                    flag = false;
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_virus);


        pm = getPackageManager();
        tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        ll_container = (LinearLayout) findViewById(R.id.ll_container);


        //雷达动画
        iv_scan = (ImageView) findViewById(R.id.iv_scan);
//        RotateAnimation ra = new RotateAnimation(0, 360,
//                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//                0.5f);
//        ra.setRepeatCount(Animation.INFINITE);
//        ra.setDuration(2000);
//        iv_scan.startAnimation(ra);
        iv_scan.startAnimation(AnimationUtils.loadAnimation(this,R.anim.rotate));
        //开始扫描病毒
        scanVirus();

    }

    //扫描病毒
    private void scanVirus() {
        flag = true;
        new Thread() {
            public void run() {
                //发送开始扫描消息
                Message msg = Message.obtain();
                msg.what = SCAN_BENGIN;
                handler.sendMessage(msg);

                // 检查手机里面的每一个应用程序
                List<PackageInfo> packInfos = pm.getInstalledPackages(0);
                //将应用程序大小作为进度条总长
                int max = packInfos.size();
                progressBar1.setMax(max);
                //进度更新值
                int process = 0;
                //遍历集合里的应用检查病毒特征码
                for (PackageInfo info : packInfos) {
                    if(!flag){
                        return;
                    }
                    //应用路径
                    String apkpath = info.applicationInfo.sourceDir;
                    // 检查获取这个文件的 特征码
                    String md5info = Md5Utils.getFileMd5(apkpath);
                    //查询数据库比对病毒特征码
                    String result = AntiVirusDao.checkVirus(md5info);
                    //发送开始扫描消息标志
                    msg = Message.obtain();
                    msg.what = SCANNING;

                    ScanInfo scanInfo = new ScanInfo();
                    //根据比对结果设置比对信息
                    if (result == null) {

                        scanInfo.desc = "扫描安全";
                        scanInfo.isVirus = false;
                    } else {
                        scanInfo.desc = result;
                        scanInfo.isVirus = true;
                    }
                    //发送消息对象
                    scanInfo.packname = info.packageName;
                    scanInfo.appname = info.applicationInfo.loadLabel(pm).toString();
                    msg.obj = scanInfo;
                    handler.sendMessage(msg);
                    //更新进度值
                    process++;
                    progressBar1.setProgress(process);
                    //休眠模拟
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //发送扫描完成消息
                msg = Message.obtain();
                msg.what = SCAN_FINISH;
                handler.sendMessage(msg);

            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        if(flag){
            //停止扫描
            flag = false;
        }
        super.onDestroy();
    }




    class ScanInfo {
        String appname;
        boolean isVirus;
        String desc;
        String packname;
    }

}
