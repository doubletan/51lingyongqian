package com.example.apple.lingyongqian.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.dao.DaoSession;
import com.example.apple.lingyongqian.dao.Diarys;
import com.example.apple.lingyongqian.dao.DiarysDao;
import com.example.apple.lingyongqian.dao.User;
import com.example.apple.lingyongqian.net.MyVolleySingle;
import com.example.apple.lingyongqian.net.UrlManager;
import com.example.apple.lingyongqian.net.VolleyStringPost;
import com.example.apple.lingyongqian.utils.StatusBarUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class CloudActivity extends AppCompatActivity implements View.OnClickListener {

    private MyApp myApp;
    private User user;
    private ImageView back,auto_img;
    private ProgressBar pross;
    private TextView cloud_save,cloud_load,date;
    private Gson gson = null;

    private MyVolleySingle myVolleySingle;

    private List<Diarys> diarysList = new ArrayList<>();

    private DaoSession daoSession;
    private DiarysDao diarysDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud);
        myApp = MyApp.getApp();
        myApp.addToList(this);
        user = MyApp.user1;
        daoSession = myApp.getDaoSession(this);
        diarysDao = daoSession.getDiarysDao();
        myVolleySingle = MyVolleySingle.getSingleQueue(this);
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        StatusBarUtil.setColor(this, Color.parseColor("#607d8b"), 0);
        loadAllDiarys();
    }

    private void initView(){
        back = (ImageView) findViewById(R.id.back);
        auto_img = (ImageView) findViewById(R.id.auto_img);
        pross = (ProgressBar) findViewById(R.id.pross);
        cloud_save = (TextView) findViewById(R.id.cloud_save);
        cloud_load = (TextView) findViewById(R.id.cloud_load);
        date = (TextView) findViewById(R.id.card_type);

        date.setText("EasyWork Cloud");
        cloud_save.setText("云备份");
        cloud_load.setText("云加载");

        cloud_save.setOnClickListener(this);
        cloud_load.setOnClickListener(this);
        back.setOnClickListener(this);

    }


//    各种问题需要解决

    @Override
    public void onClick(View v) {
        int id = v.getId();
        gson = new Gson();
        switch (id){
            case R.id.cloud_save:
                sendLoad();
                break;
            case R.id.cloud_load:
                cloudLoad(user.getU_id());
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    int temp = 0;
    int error = 0;
    int net_error = 0;
    int num = 0;
    int size = 0;
    //循环list发送q请求
    private void sendLoad(){
        size = diarysList.size();
        for (int i=0;i<size;i++){
            Diarys diarys = diarysList.get(i);
            cloudSave(i,diarys);
        }
    }

    private void loadAllDiarys(){
        QueryBuilder builder = diarysDao.queryBuilder();
        builder.where(DiarysDao.Properties.D_pid.eq(user.getU_id()));
        diarysList = builder.orderDesc(DiarysDao.Properties.D_id).list();
    }

    //云储存
    private void cloudSave(final int i,Diarys diarys){
        num = i;
        VolleyStringPost post = new VolleyStringPost(UrlManager.DIARY_SERVLET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        List<Diarys> diaryList = gson.fromJson(s, new TypeToken<ArrayList<Diarys>>() {
                        }.getType());
                        toast("存储成功");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (i==0){
                            toast("网络错误");
                        }
                    }
        });
        post.putValues("flag","1");
        post.putValues("diary_id", String.valueOf(i));
        post.putValues("item_id", String.valueOf(diarys.getD_id()));
        post.putValues("item_pid", String.valueOf(diarys.getD_pid()));
        post.putValues("item_add_time",diarys.getD_add_date());
        post.putValues("item_content",diarys.getD_textcontent());
        post.putValues("item_img",diarys.getD_img_url());
        post.putValues("item_voice",diarys.getD_voice_url());
        post.putValues("item_feel", String.valueOf(diarys.getD_feeling()));
        myVolleySingle.addToRequestQueue(post);
    }
    //云加载
    private void cloudLoad(Long pid){
        VolleyStringPost post = new VolleyStringPost(UrlManager.DIARY_SERVLET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        List<Diarys> diaryList = gson.fromJson(s, new TypeToken<ArrayList<Diarys>>() {
                        }.getType());
                        toast("返回日记：" + diaryList.size()+"条");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        toast("网络错误");
                    }
                });
        post.putValues("flag","2");
        post.putValues("pid", String.valueOf(pid));
        myVolleySingle.addToRequestQueue(post);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if (num==size){
                if (what==1){
                    if (temp==size){
                        toast("全部存至云");
                        temp=0;
                    }
                }else if (what==2){
                    if (error>0){
                        toast(error+"条存储失败，可能是网络原因");
                        error=0;
                    }
                }else {
                    if (net_error>0){
                        toast("网络异常");
                        net_error=0;
                    }
                }
            }

        }
    };

    private void toast(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
