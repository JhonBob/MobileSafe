package com.bob.mobilesafe.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bob.mobilesafe.R;
import com.bob.mobilesafe.dao.ApplockDao;
import com.bob.mobilesafe.domain.AppInfo;
import com.bob.mobilesafe.engine.AppInfoParser;

import java.util.ArrayList;
import java.util.List;

//未加锁应用碎片
public class UnLockFragment extends Fragment {
    //应用状态
    private TextView tv_status;
    //应用列表
    private ListView lv_unlock;
    //未加锁应用集合
    private List<AppInfo> unlockappInfos;
    //未加锁列表适配器
    private UnlockAdapter adapter;
    //锁数据库
    private ApplockDao dao;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_un_lock, null);
        tv_status = (TextView) view.findViewById(R.id.tv_status);
        lv_unlock = (ListView) view.findViewById(R.id.lv_unlock);
       return view;
    }

    @Override
    public void onStart() {
        dao = new ApplockDao(getActivity());
        //过滤掉已经锁定的应用程序
        unlockappInfos = new ArrayList<AppInfo>();
        //获取所有的应用
        List<AppInfo> appInfos = AppInfoParser.getAppInfos(getActivity());
        for(AppInfo info: appInfos){
            if(dao.find(info.getPackgeName())){
                //已经锁定。
            }else{
                unlockappInfos.add(info);
            }
        }
        adapter = new UnlockAdapter();
        lv_unlock.setAdapter(adapter);
        super.onStart();
    }

    //复用缓存对象
    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        ImageView iv_lock;
    }


    private class UnlockAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            tv_status.setText("未加锁("+unlockappInfos.size()+")个");
            return unlockappInfos.size();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final View view;
            ViewHolder holder;
            if(convertView!=null && convertView instanceof LinearLayout){
                view=convertView;
                holder = (ViewHolder) view.getTag();
            }else {
                view=View.inflate(getActivity(),R.layout.item_unlock,null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view
                        .findViewById(R.id.iv_app_icon);
                holder.iv_lock = (ImageView) view
                        .findViewById(R.id.iv_app_lock);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
                view.setTag(holder);
            }
            holder.iv_icon.setImageDrawable(unlockappInfos.get(position).getIcon());
            holder.tv_name.setText(unlockappInfos.get(position).getName());
            //点击图标加锁应用程序
            holder.iv_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //播放一个动画效果，x 方向移动
                    TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                    ta.setDuration(300);
                    view.startAnimation(ta);
                    new Thread() {
                        public void run() {
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //程序锁信息被加入到数据库了
                                    dao.add(unlockappInfos.get(position).getPackgeName());
                                    //从集合中移出
                                    unlockappInfos.remove(position);
                                    adapter.notifyDataSetChanged();//通知界面更新
                                }
                            });
                        }
                    }.start();
                }
            });
            return view;
        }
    }
}
