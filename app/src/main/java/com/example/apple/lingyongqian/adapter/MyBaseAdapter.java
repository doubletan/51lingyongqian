package com.example.apple.lingyongqian.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.apple.lingyongqian.R;

import java.util.List;

/**
 * Created by Administrator on 2016/7/7.
 */
public class MyBaseAdapter extends BaseAdapter {
    private Context context;
    private List list;

    public MyBaseAdapter(Context context, List list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.slid_menu_item,null);
        LinearLayout l = (LinearLayout) convertView.findViewById(R.id.item_content);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) l.getLayoutParams();
        layoutParams.height = (int) list.get(position)/5;
        l.setLayoutParams(layoutParams);
        ImageView imageView = (ImageView) l.findViewById(R.id.menu_img);
        TextView textView = (TextView) l.findViewById(R.id.menu_name);
        if (position==0){
            imageView.setImageResource(R.mipmap.note_normal);
            textView.setText("便签");
            l.setBackgroundResource(R.color.slid_menu1);
        }else if (position==1){
            imageView.setImageResource(R.mipmap.card_normal);
            textView.setText("卡片");
            l.setBackgroundResource(R.color.slid_menu2);
        }else if (position==2){
            imageView.setImageResource(R.mipmap.bill_normal);
            textView.setText("账单");
            l.setBackgroundResource(R.color.slid_menu3);
        }else if (position==3){
            imageView.setImageResource(R.mipmap.diary_normal);
            textView.setText("日记");
            l.setBackgroundResource(R.color.slid_menu4);
        }else if (position==4){
            imageView.setImageResource(R.mipmap.setting_normal);
            textView.setText("设置");
            l.setBackgroundResource(R.color.slid_menu5);
        }
        return convertView;
    }
}
