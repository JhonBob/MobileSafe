package com.bob.mobilesafe.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bob.mobilesafe.R;
import com.bob.mobilesafe.dao.BlackNumberDao;
import com.bob.mobilesafe.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;
//功能：高级工具——黑名单
public class CallSmsSafe2Activity extends Activity {
    private ListView lv_callsms_safe;
    private List<BlackNumberInfo> infos=new ArrayList<>();
    private LinearLayout ll_addblacknumber_tip,ll_loading;
    private BlackNumberDao daos;

    private EditText et_pagenumber;
    private TextView tv_page_info;

    private static final int PageSize=20;
    private int currentPageNumber=0;
    private int TotalPage=0;

    private Button pre;
    private Button next;
    private Button jump;


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            ll_loading.setVisibility(View.INVISIBLE);
            if(infos.size()==0){
                ll_addblacknumber_tip.setVisibility(View.VISIBLE);
            }else {
                lv_callsms_safe.setAdapter(new CallSafeAdapter());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callsms_safe2);
        initUI();
        fillData();
    }


    private class CallSafeAdapter extends BaseAdapter{
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
                //存放引用
                view.setTag(holder);
            }else {
                view=convertView;
                holder=(ViewHolder)view.getTag();
            }

             BlackNumberInfo info = infos.get(position);
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
            return view;
        }
    }

    //初始化UI
    public void initUI(){
        lv_callsms_safe=(ListView)findViewById(R.id.lv_callsms_safe);
        ll_addblacknumber_tip=(LinearLayout)findViewById(R.id.ll_addblacknumber_tip);
        ll_loading=(LinearLayout)findViewById(R.id.ll_loading);
        ll_loading.setVisibility(View.VISIBLE);
        et_pagenumber=(EditText)findViewById(R.id.et_page_number);
        tv_page_info=(TextView)findViewById(R.id.tv_page_info);
        pre=(Button)findViewById(R.id.prePage);
        next=(Button)findViewById(R.id.nextPage);
        jump=(Button)findViewById(R.id.jump);
        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepage();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextpage();
            }
        });
        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jump();
            }
        });

    }

    //填充数据
    public void fillData(){
        daos=new BlackNumberDao(this);
        //每一页的数据
        TotalPage=daos.getTotalNumber()/PageSize;
        tv_page_info.setText(currentPageNumber+"/"+TotalPage);
        new Thread(){
            @Override
            public void run() {
                infos=daos.findPart(currentPageNumber,PageSize);
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    public class ViewHolder{
        TextView tv_phone;
        TextView tv_mode;
    }

    public void prepage(){
        if(currentPageNumber<=0){
            return;
        }
       currentPageNumber--;
        fillData();
    }
    public void nextpage(){
        if(currentPageNumber>TotalPage-1){
        return;
    }
        currentPageNumber++;
        fillData();
    }
    public void jump(){
        String page=et_pagenumber.getText().toString().trim();
        if(TextUtils.isEmpty(page)){
            return;
        }else {
            int number=Integer.parseInt(page);
            if(number>=0 && number<=TotalPage){
                currentPageNumber=number;
                fillData();
            }
        }

    }
}
