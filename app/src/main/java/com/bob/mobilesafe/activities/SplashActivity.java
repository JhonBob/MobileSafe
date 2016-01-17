package com.bob.mobilesafe.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.SharedPreferences;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import android.widget.TextView;


import com.bob.mobilesafe.R;
import com.bob.mobilesafe.dao.AntiVirusDao;
import com.bob.mobilesafe.utils.StreamUtils;
import com.bob.mobilesafe.utils.UIUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

//功能：应用启动引导
public class SplashActivity extends Activity {
    //Handler事件分发处理标志
    protected static final int SHOW_UPDATE_DIALOG=0;
    private static final int LOAD_MAINUI=1;

    private TextView mSplashversion;
    private TextView mDownLoadInfo;
    //服务器新资源描述
    private static String desc;
    //服务器资源下载路径
    private static String loadurl;
    //包管理器
    private PackageManager mPackageManager;
    //客户端版本号
    private int clientVersionCode;
    //消息处理
    private final MyHandler handler=new MyHandler(this);
    //消息处理内部类
    private final class MyHandler extends Handler {
        //持有Activity的弱引用
        private final WeakReference<SplashActivity> mActivity;
        //构造方法
        public MyHandler(SplashActivity activity) {
            mActivity = new WeakReference<>(activity);
        }
        //消息处理
        @Override
        public void handleMessage(Message msg) {
            SplashActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case LOAD_MAINUI:
                        LoadMainUI();
                        break;
                    case SHOW_UPDATE_DIALOG:
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        //流氓行径
                        //builder.setCancelable(false);
                       // builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                          //  @Override
                           // public void onCancel(DialogInterface dialog) {
                          //      LoadMainUI();
                          //  }
                       // });
                        builder.setTitle("跟新提醒");
                        builder.setMessage(desc);
                        builder.setPositiveButton("马上更新", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.out.println("下载" + loadurl);
                                DownLoad(loadurl);
                            }
                        });
                        builder.setNegativeButton("下次更新", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LoadMainUI();
                            }
                        });
                        builder.show();
                        break;
                }
            }
            }
        }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mSplashversion=(TextView)findViewById(R.id.tv_splash_version);
        mDownLoadInfo=(TextView)findViewById(R.id.tv_info);
        mPackageManager=getPackageManager();
        //拷贝资产目录下的数据库文件
        copyDB("address.db");
        //把病毒数据库拷贝到  系统目录
        copyDB("antivirus.db");
        //桌面快捷图标
       // createShortCut();
        //更新病毒数据库
        //updateVirusDB();

        try {
            PackageInfo packageInfo=mPackageManager.getPackageInfo("com.bob.mobilesafe", 0);
            //String versionName=packageInfo.versionName;
            clientVersionCode=packageInfo.versionCode;
            mSplashversion.setText(getResources().getString(R.string.version)+clientVersionCode);
            // 判断是否需要检查更新版本
            SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
            boolean autoupdate = sp.getBoolean("autoupdate", false);
            if (autoupdate) {
                //检查版本
                checkversion();
            }else{
                //自动更新被关闭。
                new Thread(){
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        LoadMainUI();
                    };
                }.start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 更新病毒数据库的资源文件。
     */
   // private void updateVirusDB() {
        //连接服务器获取更新信息。
       // new Thread(){
           // public void run() {
               // if(AntiVirusDao.isDBExit()){
                  //  String version = AntiVirusDao.getDBVersionNum();
                    //http://localhost:8080/web/UpdateInfoServlet?version=1720
                  //  HttpUtils uitls = new HttpUtils();
                  //  String url = "http://192.168.56.1:8080/web/UpdateInfoServlet?version="+version;
                    //uitls.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>(){
                      //  @Override
                      //  public void onFailure(HttpException arg0, String arg1) {

                    //    }
                     //   @Override
                     //   public void onSuccess(ResponseInfo<String> info) {
                     //       try {
                     //           System.out.println("需要更新数据库"+info.result);
                     //           JSONObject obj = new JSONObject(info.result);
                      //          String md5 = obj.getString("md5");
                    //            String desc = obj.getString("desc");
                    //            System.out.println(md5);
                    //            System.out.println(desc);
                   //             AntiVirusDao.add(desc, md5);
                    //            AntiVirusDao.updateDBVersion(76564);
                    //        } catch (JSONException e) {
                    //            e.printStackTrace();
                   //         }
                   //     }
                   // });
              //  }
           // };
        //}.start();
   // }


    //检查版本错误处理
    private void checkversion(){
        new Thread(){
            public void run(){
                Message msg=Message.obtain();
                long startTime=System.currentTimeMillis();
                try {
                    URL url = new URL(getResources().getString(R.string.serverurl));
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setConnectTimeout(2000);
                    int code=urlConnection.getResponseCode();
                    if(code==200){
                        InputStream in=urlConnection.getInputStream();
                        String json= StreamUtils.readStreamUtils(in);
                        if(TextUtils.isEmpty(json)){
                            //服务器获取失败
                            msg.what=LOAD_MAINUI;
                            UIUtils.showToast(SplashActivity.this, "错误2016，获取JSON失败，请联系客服");
                        }else{
                            JSONObject jsonObject=new JSONObject(json);
                             loadurl=jsonObject.getString("downloadurl");
                            int serverVersionCode=jsonObject.getInt("version");
                            desc=jsonObject.getString("desc");
                            if(clientVersionCode==serverVersionCode){
                                //进入主界面
                                msg.what=LOAD_MAINUI;
                            }else {
                                //弹出更新对话框
                                msg.what=SHOW_UPDATE_DIALOG;
                            }
                            System.out.println(loadurl);
                            System.out.println(serverVersionCode);
                            System.out.println(desc);
                        }
                    }else {
                        msg.what=LOAD_MAINUI;
                        UIUtils.showToast(SplashActivity.this,"错误2015,服务器状态码错误，请联系客服");
                    }

                } catch (MalformedURLException exception) {
                    exception.printStackTrace();
                    msg.what=LOAD_MAINUI;
                    UIUtils.showToast(SplashActivity.this, "错误2011，url路径错误，请联系客服");
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                    msg.what=LOAD_MAINUI;
                    UIUtils.showToast(SplashActivity.this, "错误2012，服务器地址找不到，请联系客服");
                }catch (IOException ioe){
                    ioe.printStackTrace();
                    msg.what=LOAD_MAINUI;
                    UIUtils.showToast(SplashActivity.this, "错误2013，网络错误，请联系客服");
                } catch (JSONException e) {
                    e.printStackTrace();
                    msg.what = LOAD_MAINUI;
                    UIUtils.showToast(SplashActivity.this, "错误2016，json解析错误，请联系客服");
                } finally {
                    long endTime = System.currentTimeMillis();
                    long dtime = endTime - startTime;
                    if (dtime > 2000) {
                        handler.sendMessage(msg);
                    } else {
                        try {
                            Thread.sleep(2000 - dtime);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        handler.sendMessage(msg);
                    }
                }
            }
        }.start();
    }

    public void LoadMainUI() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    //多线程的下载器
    public void DownLoad(String downloadurl) {
        // 多线程断点下载。
        HttpUtils http = new HttpUtils();
        http.download(downloadurl, "/mnt/sdcard/mobilesafe.apk", true, new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> info) {
                UIUtils.showToast(SplashActivity.this, "文件保存成功：路径" + info.result.getAbsolutePath());
                System.out.println("安装/mnt/sdcard/mobilesafe.apk");

                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "mobilesafe.apk")),
                        "application/vnd.android.package-archive");
                startActivityForResult(intent, 0);
            }

            @Override
            public void onFailure(HttpException arg0, String msg) {
                UIUtils.showToast(SplashActivity.this, "下载失败：" + msg);
                LoadMainUI();
            }

            @Override
            public void onLoading(long total, long current,
                                  boolean isUploading) {
                mDownLoadInfo.setText("下载进度：" + current + "/" + total);
                super.onLoading(total, current, isUploading);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LoadMainUI();
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 创建应用程序的快捷图标
     */
    private void createShortCut() {
        Intent intent  = new Intent();
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        //干什么事，叫什么名，长什么样
        Intent shortcutIntent = new Intent();
        intent.putExtra("duplicate", false);//只允许一个快捷图标
        shortcutIntent.setAction("ooo.aaa.bbb");
        shortcutIntent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "黑马快捷");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.app));
        sendBroadcast(intent);
    }




    //拷贝数据库文件
    private void copyDB(final String dbname) {
        new Thread(){
            public void run() {
                try {
                    File file = new File(getFilesDir(),dbname);
                    if(file.exists() && file.length() > 0){
                        Log.i("SplashActivity", "数据库是存在的。无需拷贝");
                        return ;
                    }
                    InputStream is = getAssets().open(dbname);
                    FileOutputStream fos  = openFileOutput(dbname, MODE_PRIVATE);
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while((len = is.read(buffer))!=-1){
                        fos.write(buffer, 0, len);
                    }
                    is.close();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
