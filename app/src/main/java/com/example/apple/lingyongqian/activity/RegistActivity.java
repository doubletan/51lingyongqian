package com.example.apple.lingyongqian.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.dao.DaoSession;
import com.example.apple.lingyongqian.dao.User;
import com.example.apple.lingyongqian.dao.UserDao;
import com.example.apple.lingyongqian.net.MyVolleySingle;
import com.example.apple.lingyongqian.net.UrlManager;
import com.example.apple.lingyongqian.net.VolleyStringPost;
import com.example.apple.lingyongqian.utils.FastBlur;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class RegistActivity extends AppCompatActivity implements View.OnClickListener {
    private View reg_form;
    private ImageView zoom_img;
    private EditText username,password,check_pwd;
    private Button reg_bt;
    private TextView back_login;
    private String name,pass,check_pwds;
    private Intent intent;
    private MyVolleySingle volleySingle = null;

    private MyApp myApp;
    private DaoSession daoSession;
    private UserDao userDao;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        intent = getIntent();
        myApp = MyApp.getApp();
        myApp.addToList(this);
        daoSession = myApp.getDaoSession(this);
        userDao = daoSession.getUserDao();
        volleySingle = MyVolleySingle.getSingleQueue(this);
        initView();
        loginFormBlur();
    }

    private void initView(){
        zoom_img = (ImageView) findViewById(R.id.set_top_img);
        reg_form = findViewById(R.id.reg_form);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        check_pwd = (EditText) findViewById(R.id.check_password);
        reg_bt = (Button) findViewById(R.id.reg_bt);
        back_login = (TextView) findViewById(R.id.back_login);

        reg_bt.setOnClickListener(this);
        back_login.setOnClickListener(this);
    }
    //zoom背景模糊
    private void loginFormBlur() {
        zoom_img.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                zoom_img.getViewTreeObserver().removeOnPreDrawListener(this);
                zoom_img.buildDrawingCache();

                Bitmap bmp = zoom_img.getDrawingCache();
                blur(bmp, reg_form, 8, 20);
                return true;
            }
        });
    }

    //图片模糊
    private void blur(Bitmap bkg, View view, float scaleFactor, float radius) {
        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth()/scaleFactor),
                (int) (view.getMeasuredHeight()/scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);
        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        view.setBackground(new BitmapDrawable(getResources(), overlay));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.reg_bt:
                name = username.getText().toString().trim();
                pass = password.getText().toString().trim();
                check_pwds = check_pwd.getText().toString().trim();
                if ("".equals(name)){
                    toast("用户名为空");
                    return;
                }else if ("".equals(pass)) {
                    toast("密码为空");
                    return;
                }else if ("".equals(check_pwds)){
                    toast("请确认密码");
                    return;
                }else if (!pass.equals(check_pwds)){
                    toast("两次密码不对");
                    return;
                }
                //注册
                reg(name, pass);
                break;
            case R.id.back_login:
                finish();
                break;
        }
    }
    //注册
    private void reg(String username, final String pass){
        VolleyStringPost post = new VolleyStringPost(UrlManager.USER_SERVLET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Gson gson = new Gson();
                        List<User> userList = gson.fromJson(s, new TypeToken<ArrayList<User>>() {
                        }.getType());
                        User user = userList.get(0);
                        if (user==null){
                            toast("用户名已存在");
                            return;
                        }else {
                            try{
                                userDao.insert(user);
                            }catch (Exception e){
                                toast("注册失败~_~");
                            }
                            handler.sendEmptyMessage(1);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        toast("网络错误");
                    }
        });
        try {
            post.putValues("username", URLDecoder.decode(username, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        post.putValues("password",pass);
        post.putValues("flag","1");
        volleySingle.addToRequestQueue(post);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            toast("注册成功");
            if (what==1){
                intent.putExtra("username",name);
                intent.putExtra("pwd", pass);
                setResult(2, intent);
                //这里可用广播结束当前activity
            }
        }
    };

    private void toast(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
