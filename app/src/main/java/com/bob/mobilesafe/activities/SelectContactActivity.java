package com.bob.mobilesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bob.mobilesafe.R;
import com.bob.mobilesafe.domain.ContactInfo;
import com.bob.mobilesafe.engine.ContactInfoParser;

import java.util.List;

//功能：防盗保护——安全号码——选择手机防盗号码
public class SelectContactActivity extends Activity {
    //联系人对象集合
    private List<ContactInfo> infos;
    //联系人列表
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);
        infos= ContactInfoParser.findAll(this);
        listView=(ListView)findViewById(R.id.lv_contacts);
        listView.setAdapter(new ContactsAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent data=new Intent();
                data.putExtra("phone",infos.get(position).getPhone());
                setResult(0, data);
                finish();
            }
        });
    }

    //联系人适配器
    private class ContactsAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return infos.size();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public ContactInfo getItem(int position) {
            return null;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if(convertView==null){
              view=View.inflate(getApplicationContext(),R.layout.item_contact,null);
            }else {
                view=convertView;
            }
            TextView tv_name=(TextView)view.findViewById(R.id.tv_item_name);
            TextView tv_phone=(TextView)view.findViewById(R.id.tv_item_phonenumber);
            tv_name.setText(infos.get(position).getName());
            tv_phone.setText(infos.get(position).getPhone());
            return view;
        }
    }
}
