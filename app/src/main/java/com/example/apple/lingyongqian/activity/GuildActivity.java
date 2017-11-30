package com.example.apple.lingyongqian.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.apple.lingyongqian.utils.SPUtils;
import com.example.apple.lingyongqian.utils.SharedPreferencesUtil;
import com.example.apple.lingyongqian.xianjindai.activity.XianJinDai;
import com.example.apple.lingyongqian.xianjindai.biz.GetCreditProduct;
import com.example.apple.lingyongqian.xianjindai.biz.GetImageBean;
import com.example.apple.lingyongqian.xianjindai.biz.GetNewProduct;
import com.example.apple.lingyongqian.xianjindai.biz.GetProduct;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;

public class GuildActivity extends AppCompatActivity {
    private SwitchHandler mHandler = new SwitchHandler(this);

    private static  final String URL="http://www.shoujijiekuan.com/tantan/app105.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean url1 = SPUtils.contains(this, "url");
        if(!url1){
            setUrl();
        }else {
            TextNet();
                mHandler.sendEmptyMessageDelayed(2, 1000);
        }
    }
    private void setUrl() {

        StringRequest request=new StringRequest(URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //执行相关操作
                new GetProduct(GuildActivity.this).execute();
                new GetNewProduct(GuildActivity.this).execute();
                new GetImageBean(GuildActivity.this).execute();
                new GetCreditProduct(GuildActivity.this).execute();
                SPUtils.put(GuildActivity.this,"url",URL);
                setWelcome();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mHandler.sendEmptyMessageDelayed(1, 1500);
            }
        });
        MyApp.getVolleyRequestQueue().add(request);
    }

    private static class SwitchHandler extends Handler {
        private WeakReference<GuildActivity> mWeakReference;

        SwitchHandler(GuildActivity activity) {
            mWeakReference = new WeakReference<GuildActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            GuildActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what){
                    case 1:
                        MainActivity.launch(activity);
                        activity.finish();
                        break;
                    case 2:
                        Intent intent = new Intent(activity, XianJinDai.class);
                        activity.startActivity(intent);
                        activity.finish();
                        break;
                    case 4:
                        Intent intent1 = new Intent(activity, GuideActivity.class);
                        activity.startActivity(intent1);
                        activity.finish();
                        break;

                }
                activity.finish();
            }
        }
    }
    private void setWelcome(){
        boolean isFirstOpen = SharedPreferencesUtil.getBoolean(GuildActivity.this, SharedPreferencesUtil.FIRST_OPEN, true);
        if (isFirstOpen) {
            mHandler.sendEmptyMessageDelayed(4, 1500);
            return;
        }else {
            mHandler.sendEmptyMessageDelayed(2, 1500);
        }
    }

    private void TextNet() {
        ConnectivityManager con= (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        if(wifi|internet){
            //执行相关操作
            new GetProduct(this).execute();
            new GetNewProduct(this).execute();
            new GetImageBean(this).execute();
            new GetCreditProduct(this).execute();
        }else{
            Toast.makeText(this,"亲，网络连接失败咯！", Toast.LENGTH_LONG).show();
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
