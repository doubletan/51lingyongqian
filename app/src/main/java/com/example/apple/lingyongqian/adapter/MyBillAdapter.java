package com.example.apple.lingyongqian.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.dao.AllBillInfo;

import java.util.List;


/**
 * Created by Administrator on 2016/6/30.
 */
public class MyBillAdapter extends BaseAdapter {
    private List<AllBillInfo> list;
    private Context context;

    public MyBillAdapter(Context context, List<AllBillInfo> list) {
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
        AllBillInfo info = list.get(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.grild_bill_item,null);
//        if (position==list.size()-1){
//            convertView.findViewById(R.id.bill_content).setVisibility(View.GONE);
//            View add_img =   convertView.findViewById(R.id.add_bill);
//            add_img.setVisibility(View.VISIBLE);
//            add_img.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(context, "添加账单", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }else {
            TextView name = (TextView) convertView.findViewById(R.id.bill_name);
            TextView num = (TextView) convertView.findViewById(R.id.bill_num);
            TextView type = (TextView) convertView.findViewById(R.id.bill_type);
            name.setText(info.getAl_bill_name());
            num.setText(info.getAl_bill_num());
            int bill_type = info.getAl_bill_type();
            if (bill_type==1){
                type.setText("+");
            }else if (bill_type==2){
                type.setText("-");
            }
        //}
        return convertView;
    }
}
