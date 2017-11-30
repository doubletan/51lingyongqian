package com.example.apple.lingyongqian.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.apple.lingyongqian.dao.AllInfo;
import com.example.apple.lingyongqian.dao.DaoMaster;
import com.example.apple.lingyongqian.dao.DaoSession;
import com.example.apple.lingyongqian.dao.User;
import com.example.apple.lingyongqian.xianjindai.entity.CreditProduct;
import com.example.apple.lingyongqian.xianjindai.entity.ImagerBean;
import com.example.apple.lingyongqian.xianjindai.entity.Product;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/6/28.
 */
public class MyApp extends Application {
    public static RequestQueue requestQueue;
    /**
     * release=true 软件发布 false:开发中
     */
    public static boolean isRelease = false;
    //    用户ID
    public static String userId;
    //    产品
    public static Product product;
    //    新产品
    public static Product newProduct;
    //    信用产品
    public static CreditProduct creditProduct;
    //    轮播图
    public static ImagerBean imagerBean;

    public static RequestQueue getVolleyRequestQueue(){
        return requestQueue;
    }
    //dao
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    public static User user1;
    public static AllInfo today;
    public static String today_date = "";

    private static MyApp instance;

    public static SharedPreferences sp;



    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        requestQueue= Volley.newRequestQueue(getApplicationContext());
        sp = super.getSharedPreferences("eSetting", Context.MODE_PRIVATE);//只能被本应用访问
    }

    public static MyApp getApp(){
        return instance;
    }

    public DaoMaster getDaoMaster(Context context){
        if (daoMaster == null){
            DaoMaster.OpenHelper helper =
                    new DaoMaster.DevOpenHelper(context,"easywork.db",null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    public DaoSession getDaoSession(Context context){
        if (daoSession == null){
            if (daoMaster == null){
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    List<Activity> myActivity = new ArrayList<>();
    public void addToList(Activity activity){
        myActivity.add(activity);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        for (Activity activity : myActivity){
            activity.finish();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
