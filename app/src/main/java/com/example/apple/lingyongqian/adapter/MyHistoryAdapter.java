package com.example.apple.lingyongqian.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.activity.OneDayAllActivity;
import com.example.apple.lingyongqian.dao.AllInfo;

import java.util.List;


/**
 * Created by Administrator on 2016/6/30.
 */
public class MyHistoryAdapter extends BaseAdapter {
    private Context context;
    private List<AllInfo> list;

    public MyHistoryAdapter(Context context, List<AllInfo> list) {
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
        final AllInfo info = list.get(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.history_item_card,null);
        TextView month = (TextView) convertView.findViewById(R.id.history_month);
        TextView day = (TextView) convertView.findViewById(R.id.history_date);
        String date = info.getA_add_date();
        if (!"".equals(date)){
            day.setText(date.substring(8));
            month.setText(date.substring(0, 7));
        }
        if (info.getA_notes()==1){
            View note = convertView.findViewById(R.id.history_note);
            note.setVisibility(View.VISIBLE);
        }
        if (info.getA_cards()==1){
            View card = convertView.findViewById(R.id.history_card);
            card.setVisibility(View.VISIBLE);
        }
        if (info.getA_bills()==1){
            View bill = convertView.findViewById(R.id.history_bill);
            bill.setVisibility(View.VISIBLE);
        }
        if (info.getA_diarys()==1){
            View diary = convertView.findViewById(R.id.history_diary);
            diary.setVisibility(View.VISIBLE);
        }
        View card_view = convertView.findViewById(R.id.card_item);
        card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,OneDayAllActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("info",info);
                intent.putExtra("info_item",bundle);
                context.startActivity(intent);
            }
        });
        return convertView;
    }
}
