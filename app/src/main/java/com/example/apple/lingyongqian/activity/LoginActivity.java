package com.example.apple.lingyongqian.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
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

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private View login_form;
    private ImageView zoom_img;
    private EditText username,password;
    private Button login_bt;
    private TextView reg_user,forget_pw;
    private String name,pass;

    private Intent intent;
    private MyVolleySingle volleySingle = null;

    private MyApp myApp;
    private DaoSession daoSession;
    private UserDao userDao;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myApp = MyApp.getApp();
        myApp.addToList(this);
        daoSession = myApp.getDaoSession(this);
        userDao = daoSession.getUserDao();
        volleySingle = MyVolleySingle.getSingleQueue(this);
        initView();
        loginFormBlur();

//        QueryBuilder qb = userDao.queryBuilder();
//        qb.where(UserDao.Properties.U_id.eq(0l), UserDao.Properties.U_id.eq(2l),UserDao.Properties.U_id.eq(3l),UserDao.Properties.U_id.eq(4l));
//        qb.buildDelete().executeDeleteWithoutDetachingEntities();
//        List<User> list = userDao.loadAll();
//        for (int i=0;i<list.size();i++){
//            User u = list.get(i);
//            Log.i("---","清空后的数据库:"+u.getU_id()+" "+u.getU_age()+" "+u.getU_name()+" "+u.getU_password());
//        }
    }

    private void initView(){
        zoom_img = (ImageView) findViewById(R.id.set_top_img);
        login_form = findViewById(R.id.login_form);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login_bt = (Button) findViewById(R.id.login_bt);
        reg_user = (TextView) findViewById(R.id.reg_user);
        forget_pw = (TextView) findViewById(R.id.forget_pw);

        login_bt.setOnClickListener(this);
        reg_user.setOnClickListener(this);
        forget_pw.setOnClickListener(this);
    }

    //zoom背景模糊
    private void loginFormBlur() {
        zoom_img.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                zoom_img.getViewTreeObserver().removeOnPreDrawListener(this);
                zoom_img.buildDrawingCache();

                Bitmap bmp = zoom_img.getDrawingCache();
                blur(bmp, login_form, 8, 20);
                return true;
            }
        });
    }

    //图片模糊
    private void blur(Bitmap bkg, View view, float scaleFactor, float radius) {
        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
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
            case R.id.login_bt:
                name = username.getText().toString().trim();
                pass = password.getText().toString().trim();
                if ("".equals(name)){
                    toast("请填写用户名");
                    return;
                }else if ("".equals(pass)){
                    toast("请填写密码");
                    return;
                }
                login();
                break;
            case R.id.reg_user:
                Intent intent = new Intent(LoginActivity.this,RegistActivity.class);
                startActivityForResult(intent,1);
                break;
            case R.id.forget_pw:
                toast("忘记密码");
                break;
        }
    }

    private void login(){
        VolleyStringPost post = new VolleyStringPost(UrlManager.USER_SERVLET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Gson gson = new Gson();
                        List<User> userList = gson.fromJson(s, new TypeToken<ArrayList<User>>() {
                        }.getType());
//                        if (userList==null){
//                            toast("网络错误");
//                            return;
//                        }
                        user = userList.get(0);
                        Log.i("---",user.getU_name());
                        if (user==null){
                            toast("用户名或密码错误");
                            return;
                        }else {
                            try{
                                QueryBuilder qb = userDao.queryBuilder();
                                qb.where(UserDao.Properties.U_name.eq(name));
                                User user_db = (User) qb.orderDesc(UserDao.Properties.U_id).list().get(0);
                                user.setU_id(user_db.getU_id());
                                userDao.update(user);
                                QueryBuilder qb1 = userDao.queryBuilder();
                                qb1.where(UserDao.Properties.U_name.eq(name));
                                MyApp.user1 = (User) qb1.orderDesc(UserDao.Properties.U_id).list().get(0);
                                Log.i("---",MyApp.user1.getU_name());
                            }catch (Exception e){
                                toast("登录失败~_~");
                                return;
                            }
                            handler.sendEmptyMessage(1);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        toast("登录失败~_~");
                    }
        });
        post.putValues("username",name);
        post.putValues("password",pass);
        post.putValues("flag","2");
        volleySingle.addToRequestQueue(post);
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
//            List<User> list = userDao.loadAll();
//            for (int i=0;i<list.size();i++){
//                User u = list.get(i);
//                Log.i("---","登录后的数据库:"+u.getU_id()+" "+u.getU_age()+" "+u.getU_name()+" "+u.getU_password());
//            }
            if (what==1){
                toast("登录成功");
                //MyApp.user = user;
                writeInFile(MyApp.user1);
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                intent.putExtra("fromLogin", "1");
                startActivity(intent);
            }
        }
    };

    //将user写入文件
    private void writeInFile(User user){
        SharedPreferences sp = super.getSharedPreferences("saveLogin", Context.MODE_PRIVATE);
        //调用edit()方法，获得Editor
        SharedPreferences.Editor  editor = sp.edit();
        //添加属性名为”name”值为”Tom”的数据
        editor.putString("username", user.getU_name());
        //提交所有更新的数据
        editor.commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null){
            username.setText(data.getStringExtra("username"));
        }
    }

    private void toast(String str){
        Toast.makeText(LoginActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    long firstTime = 0;
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            long secondTime = System.currentTimeMillis();
            if (secondTime-firstTime>2000){
                Toast.makeText(this,"再按一次退出", Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
                return true;
            }else {
                finish();
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}
