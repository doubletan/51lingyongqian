package com.example.apple.lingyongqian.listener;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerViewItemListener implements RecyclerView.OnItemTouchListener {

    //定义Item单击监听器
    public interface OnItemClickListener{
        public void OnItemClick(View item, int adapterPosition);
    }
    //定义手势
    private GestureDetector mGestureDetector;
    private OnItemClickListener itemClickListener;

    public RecyclerViewItemListener(Context mContext, OnItemClickListener itemClickListener) {
        //初始化自定义单击监听器
        this.itemClickListener = itemClickListener;
        //初始手势
        this.mGestureDetector = new GestureDetector(mContext,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                //必须返回true
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        //根据点击位置计算对应的Item子视图
        View cView = rv.findChildViewUnder(e.getX(),e.getY());
        if(cView!=null && itemClickListener!=null && mGestureDetector.onTouchEvent(e)){
            //根据子视图放回子视图在RecyclerView adapter中的位置
            int position =rv.getChildAdapterPosition(cView);
            itemClickListener.OnItemClick(cView,position);
            return true;
        }
        return false;
    }
    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }
    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
