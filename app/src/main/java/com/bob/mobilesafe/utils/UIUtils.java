package com.bob.mobilesafe.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/12/28.
 */
//功能：分装好的吐司
public class UIUtils {
    public static void showToast(final Activity context,final String msg){
        if("main".equals(Thread.currentThread().getName())){
            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
        }else {
            context.runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
