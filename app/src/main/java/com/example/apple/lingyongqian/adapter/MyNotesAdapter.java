package com.example.apple.lingyongqian.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.dao.Notes;

import java.util.List;


/**
 * Created by Administrator on 2016/6/30.
 */
public class MyNotesAdapter extends BaseAdapter {
    private List<Notes> list;
    private Context context;

    public MyNotesAdapter(Context context, List<Notes> list) {
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
        Notes notes = list.get(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.list_note_item,null);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.notes_img);
        TextView note_content = (TextView) convertView.findViewById(R.id.note_content);
        int type = (int)(Math.random()*(10));
        if (type==1){
            imageView.setImageResource(R.mipmap.point_1);
        }else if (type==2){
            imageView.setImageResource(R.mipmap.point_3);
        }else if (type==3){
            imageView.setImageResource(R.mipmap.point_4);
        }else if (type==4){
            imageView.setImageResource(R.mipmap.point_1);
        }else if (type==5){
            imageView.setImageResource(R.mipmap.point_2);
        }else if (type==6){
            imageView.setImageResource(R.mipmap.point_3);
        }else {
            imageView.setImageResource(R.mipmap.point_2);
        }
        note_content.setText(notes.getN_textcontent());
        return convertView;
    }
}
