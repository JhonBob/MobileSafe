package com.bob.mobilesafe.activities;


import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Bundle;


import com.bob.mobilesafe.R;

//功能：流量统计
public class TrafficManagerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_manager);
        //手机接收和下载的流量
        long mobileRx=TrafficStats.getMobileRxBytes();
        //手机发送和上传的流量
        long mobileTx=TrafficStats.getMobileTxBytes();
        long totalRx=TrafficStats.getTotalRxBytes();
        long totalTx=TrafficStats.getTotalTxBytes();
        //用户ID
        int uid=0;

        long i=TrafficStats.getUidRxBytes(10041);
        long e=TrafficStats.getUidTxBytes(10041);
        ///proc/uid_stat/10041/tcp_rcv  存储的就是下载的流量
        //proc/uid_stat/10041/tcp_snd 上传的流量

        ConnectivityManager cm=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        String type=cm.getActiveNetworkInfo().getTypeName();
    }
}
