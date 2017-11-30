package com.example.apple.lingyongqian.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.dao.AllCardInfo;

import java.util.List;

/**
 * Created by Administrator on 2016/6/30.
 * grildView 的适配器
 */
public class MyCardAdapter extends BaseAdapter {
    private List<AllCardInfo> list;
    private Context context;

    public MyCardAdapter(Context context, List list) {
        this.list = list;
        this.context = context;
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
        AllCardInfo info = list.get(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.grild_card_item,null);
//        if (position==list.size()-1){
//            convertView.findViewById(R.id.crad_content).setVisibility(View.GONE);
//            View add_img =   convertView.findViewById(R.id.add_card);
//            add_img.setVisibility(View.VISIBLE);
//            add_img.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(context,"添加名片",Toast.LENGTH_SHORT).show();
//                }
//            });
//        }else {
            TextView crad_name = (TextView) convertView.findViewById(R.id.crad_name);
            ImageView card_img = (ImageView) convertView.findViewById(R.id.card_type);
            crad_name.setText(info.getAc_card_name());
            int card_type = info.getAc_type();
            //1---place  2---person  3--company
            if (card_type==1){
                card_img.setImageResource(R.mipmap.item_place);
            }else if (card_type==2){
                card_img.setImageResource(R.mipmap.item_person);
            }else if (card_type==3){
                card_img.setImageResource(R.mipmap.item_company);
            }
       // }
        return convertView;
    }
}
