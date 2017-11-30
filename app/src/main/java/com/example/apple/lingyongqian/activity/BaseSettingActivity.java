package com.example.apple.lingyongqian.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.dao.DaoSession;
import com.example.apple.lingyongqian.dao.User;
import com.example.apple.lingyongqian.dao.UserDao;
import com.example.apple.lingyongqian.utils.StatusBarUtil;


public class BaseSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private MyApp myApp;
    private User user;
    private DaoSession daoSession;
    private UserDao userDao;
    private EditText pass,again,old_pass,again_old;
    private Button save,cancel_pass;
    private SharedPreferences sharedPreferences;
    private ImageView back;

    private String set_pass,set_again,old_pwd,again_old_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_setting);
        myApp = MyApp.getApp();
        user = MyApp.user1;
        myApp.addToList(this);
        daoSession = myApp.getDaoSession(this);
        userDao = daoSession.getUserDao();
        sharedPreferences = super.getSharedPreferences("says", Context.MODE_PRIVATE);
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        StatusBarUtil.setColor(this, Color.parseColor("#607d8b"), 0);
    }

    private void initView(){
        pass = (EditText) findViewById(R.id.pass);
        again = (EditText) findViewById(R.id.again);
        save = (Button) findViewById(R.id.save);
        back = (ImageView) findViewById(R.id.back);

        old_pass = (EditText) findViewById(R.id.old_pass);
        again_old = (EditText) findViewById(R.id.again_old);
        cancel_pass = (Button) findViewById(R.id.cancel_pass);

        save.setOnClickListener(this);
        cancel_pass.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.save:
                set_pass = pass.getText().toString().trim();
                set_again = again.getText().toString().trim();
                if ("".equals(set_pass)){
                    toast("密码不能为空");
                    return;
                }else if ("".equals(set_again)){
                    toast("请确认密码");
                    return;
                }else if (!set_pass.equals(set_again)){
                    toast("两次密码不正确");
                    return;
                }else {
                    user.setU_issetpass("1");
                    userDao.update(user);
                    MyApp.user1 = user;
                    savePass(set_pass);
                    toast("设置成功");
                    finish();
                }
                break;
            case R.id.cancel_pass:
                old_pwd = old_pass.getText().toString().trim();
                again_old_pass = again_old.getText().toString().trim();
                if ("".equals(old_pwd)){
                    toast("原始密码不能为空");
                    return;
                }else if ("".equals(again_old_pass)){
                    toast("请确认原始密码");
                    return;
                }else if (!old_pwd.equals(again_old_pass)){
                    toast("两次密码不正确");
                    return;
                }else {
                    user.setU_issetpass("0");
                    userDao.update(user);
                    MyApp.user1 = user;
                    savePass("");
                    toast("已取消访问密码");
                    finish();
                }
                break;
            case R.id.back:
                finish();
                break;
        }
    }
    //保存密码
    private void savePass(String str){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("person_pass",str).commit();
    }

    private void toast(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
