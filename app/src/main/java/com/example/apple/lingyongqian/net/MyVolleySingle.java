package com.example.apple.lingyongqian.net;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Administrator on 2016/5/6.
 单例模式
 */
public class MyVolleySingle {
    //私有化属性
    private static MyVolleySingle singleQueue;
    private RequestQueue requestQueue;
    private static Context context;
    private MyVolleySingle (Context context){
        this.context = context;
        requestQueue = getRequestQueue();
    }
    private RequestQueue getRequestQueue(){
        if (requestQueue==null){
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }
    //synchronized,防止并发，保证线程安全
    public static synchronized MyVolleySingle getSingleQueue(Context context){
        if (singleQueue==null){
            singleQueue = new MyVolleySingle(context);
        }
        return singleQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        //防止回收而出现空指针异常
        getRequestQueue().add(req);
    }

}