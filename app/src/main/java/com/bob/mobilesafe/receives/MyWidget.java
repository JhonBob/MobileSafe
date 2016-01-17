package com.bob.mobilesafe.receives;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.bob.mobilesafe.services.UpdateWidgetService;


/**
 * Implementation of App Widget functionality.
 */

//桌面控件Widget
public class MyWidget extends AppWidgetProvider {
    @Override
    public void onEnabled(Context context) {
        //开启服务 定期的更新widget
        Intent i = new Intent(context,UpdateWidgetService.class);
        context.startService(i);
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        //停止服务 不再去更新widget
        Intent i = new Intent(context,UpdateWidgetService.class);
        context.stopService(i);
        super.onDisabled(context);
    }
}


