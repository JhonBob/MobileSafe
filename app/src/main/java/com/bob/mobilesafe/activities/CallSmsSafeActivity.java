package com.bob.mobilesafe.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bob.mobilesafe.R;
import com.bob.mobilesafe.dao.BlackNumberDao;
import com.bob.mobilesafe.domain.BlackNumberInfo;
import com.bob.mobilesafe.utils.UIUtils;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;


//功能：高级工具——黑名单
public class CallSmsSafeActivity extends Activity {
    //黑名单列表
    private ListView lv_callsms_safe;
    //黑名单对象集合
    private List<BlackNumberInfo> infos=new ArrayList<>();
    //当前用户还未添加黑名单时的提示语
    private LinearLayout ll_addblacknumber_tip;
    //加载进度
    private LinearLayout ll_loading;
    //黑名单数据库
    private BlackNumberDao daos;

    //开始加载位置
    private int startIndex=0;
    //一次加载20条
    private int maxCount=20;

    //列表适配器
    private CallSafeAdapter adapter;
    //总条数
    private int TotalCount;
    //黑名单添加按钮
    private Button addBnumber;


    //消息分发处理
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            ll_loading.setVisibility(View.INVISIBLE);
            switch (msg.what){
                case 1:
                    addBlack();
                    break;
                case 2:
                    if(infos.size()==0){
                        ll_addblacknumber_tip.setVisibility(View.VISIBLE);
                    }else {
                        if(adapter==null){
                            adapter=new CallSafeAdapter();
                            lv_callsms_safe.setAdapter(adapter);
                        }else {
                            //刷新数据
                            adapter.notifyDataSetChanged();
                        }
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_sms_safe);
        //初始化UI
        initUI();
        //填充数据
        fillData();
    }


    //列表数据适配器
    private class CallSafeAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return infos.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            if(convertView==null){
                view=View.inflate(getApplicationContext(),R.layout.item_callsms,null);
                holder=new ViewHolder();
                holder.tv_phone=(TextView)view.findViewById(R.id.tv_item_phone);
                holder.tv_mode=(TextView)view.findViewById(R.id.tv_item_mode);
                holder.iv_delete=(ImageView)view.findViewById(R.id.iv_delete);
                //存放引用
                view.setTag(holder);
            }else {
                view=convertView;
                holder=(ViewHolder)view.getTag();
            }

            final BlackNumberInfo info = infos.get(position);
            holder.tv_phone.setText(info.getNumber());
            // 1 全部拦截 2 短信拦截 3 电话拦截
            String mode = info.getMode();
            if ("1".equals(mode)) {
                holder.tv_mode.setText("全部拦截");
            } else if ("2".equals(mode)) {
                holder.tv_mode.setText("短信拦截 ");
            } else if ("3".equals(mode)) {
                holder.tv_mode.setText("电话拦截 ");
            }

            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number=info.getNumber();
                    boolean result=daos.delete(number);
                    if(result){
                        UIUtils.showToast(CallSmsSafeActivity.this,"删除成功");
                        //从界面去除
                        infos.remove(info);
                        adapter.notifyDataSetChanged();
                    }else {
                        UIUtils.showToast(CallSmsSafeActivity.this,"删除失败");
                    }
                }
            });
            return view;
        }
    }

    //初始化UI
    public void initUI(){
        lv_callsms_safe=(ListView)findViewById(R.id.lv_callsms_safe);
        ll_addblacknumber_tip=(LinearLayout)findViewById(R.id.ll_addblacknumber_tip);
        ll_loading=(LinearLayout)findViewById(R.id.ll_loading);
        ll_loading.setVisibility(View.VISIBLE);
        addBnumber=(Button)findViewById(R.id.addBlackNumber);
        addBnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg=Message.obtain();
                msg.what=1;
                handler.sendMessage(msg);
            };
        });

        lv_callsms_safe.setOnScrollListener(new AbsListView.OnScrollListener() {
            // 滚动状态发生变化调用的方法，通过对象缓存重用，一次只从数据库返回20条数据
            // OnScrollListener.SCROLL_STATE_FLING 惯性滑动
            // OnScrollListener.SCROLL_STATE_TOUCH_SCROLL 触摸滑动
            // OnScrollListener.SCROLL_STATE_IDLE 静止
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState){
                    case SCROLL_STATE_IDLE:
                        // 判断是否是最后一个条目。
                        int lastPosition = lv_callsms_safe.getLastVisiblePosition();
                        System.out.println("最后一个可见条目的位置：" + lastPosition);
                        if(lastPosition==infos.size()-1){
                            //加载更多的数据,更改加载初始位置
                            startIndex+=maxCount;
                            if (startIndex >= TotalCount) {
                                Toast.makeText(getApplicationContext(),
                                        "没有更多的数据了。", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            fillData();
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    //填充数据
    public void fillData(){
        daos=new BlackNumberDao(this);
        TotalCount=daos.getTotalNumber();
        new Thread(){
            @Override
            public void run() {
                if(infos==null){
                    infos=daos.findpart(startIndex,maxCount);
                }
                //滚动时向集合追加数据
                infos.addAll(daos.findpart(startIndex,maxCount));
                Message msg=Message.obtain();
                msg.what=2;
                handler.sendMessage(msg);
            }
        }.start();
    }

    //对象缓存与重用
    public class ViewHolder{
        TextView tv_phone;
        TextView tv_mode;
        ImageView iv_delete;
    }

    private AlertDialog dialog;
    private EditText et_black_number;
    private CheckBox cb_phone;
    private CheckBox cb_sms;
    private Button btn_ok;
    private Button btn_cancel;


    public void addBlack(){
        //创建一个Dialog供用户添加黑名单以及拦截类型
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View dialogView=View.inflate(this, R.layout.dialog_add_blacknumber, null);
        builder.setView(dialogView);

        et_black_number=(EditText)dialogView.findViewById(R.id.et_black_number);
        cb_phone=(CheckBox)dialogView.findViewById(R.id.cb_phone);
        cb_sms=(CheckBox)dialogView.findViewById(R.id.cb_sms);
        btn_cancel=(Button)dialogView.findViewById(R.id.addcancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_ok=(Button)dialogView.findViewById(R.id.addok);


        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String blacknumber = et_black_number.getText().toString().trim();
                if (TextUtils.isEmpty(blacknumber)) {

                    return;
                }

                //拦截模式
                String mode = "0";
                if (cb_phone.isChecked() && cb_sms.isChecked()) {
                    mode = "1";
                } else if (cb_phone.isChecked()) {
                    mode = "3";
                } else if (cb_sms.isChecked()) {
                    mode = "2";
                } else {
                    return;
                }

                //界面与数据库的联动
                boolean result = daos.add(blacknumber, mode);
                //刷新界面，加到集合
                if (result) {
                    BlackNumberInfo info = new BlackNumberInfo();
                    info.setMode(mode);
                    info.setNumber(blacknumber);
                    infos.add(0, info);//界面数据集合发生变化,加到第一位置
                    // 通知界面刷新。
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter = new CallSafeAdapter();
                        lv_callsms_safe.setAdapter(adapter);
                    }
                }
                dialog.dismiss();
            }
        });
        dialog=builder.show();
    }
}

