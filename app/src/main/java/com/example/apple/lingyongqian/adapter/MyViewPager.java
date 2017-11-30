package com.example.apple.lingyongqian.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2016/7/5.
 */
public class MyViewPager extends PagerAdapter {

    private List<View> viewList;
    private int mChildCount = 0;

    public MyViewPager(List<View> viewList) {
        this.viewList = viewList;
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewList.get(position));//添加页卡
        return viewList.get(position);
    }
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object)   {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));//删除页卡
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String charSequence = "";
        if (viewList.size()==2){
            if (position==0){
                charSequence = "收入";
            }else {
                charSequence = "支出";
            }
        }else {
            if (position==0){
                charSequence = "名片";
            }else if (position==1){
                charSequence = "公司";
            }else {
                charSequence = "地点";
            }
        }

        return charSequence;//页卡标题
    }



}
