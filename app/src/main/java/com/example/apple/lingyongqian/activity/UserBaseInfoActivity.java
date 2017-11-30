package com.example.apple.lingyongqian.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.dao.DaoSession;
import com.example.apple.lingyongqian.dao.User;
import com.example.apple.lingyongqian.dao.UserDao;
import com.example.apple.lingyongqian.utils.StatusBarUtil;


public class UserBaseInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private MyApp myApp;
    private DaoSession daoSession;
    private User user;
    private UserDao userDao;

    private ImageView back;
    private TextView name,card_type;
    private EditText person_name,person_tell,person_email,person_describ;
    private RadioButton boy,gril;
    private Button button;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_base_info);
        initView();
    }
    @Override
    public void onStart() {
        super.onStart();
        StatusBarUtil.setColor(this, Color.parseColor("#607d8b"), 0);
    }

    private void initView(){
        myApp = MyApp.getApp();
        user = MyApp.user1;
        myApp.addToList(this);
        daoSession = myApp.getDaoSession(this);
        userDao = daoSession.getUserDao();

        sharedPreferences = super.getSharedPreferences("says", Context.MODE_PRIVATE);

        back = (ImageView) findViewById(R.id.back);
        card_type = (TextView) findViewById(R.id.card_type);
        name = (TextView) findViewById(R.id.name);
        person_name = (EditText) findViewById(R.id.person_name);
        person_tell = (EditText) findViewById(R.id.person_tell);
        person_email = (EditText) findViewById(R.id.person_email);
        person_describ = (EditText) findViewById(R.id.person_describ);
        boy = (RadioButton) findViewById(R.id.boy);
        gril = (RadioButton) findViewById(R.id.gril);
        button = (Button) findViewById(R.id.save);

        person_name.setText(user.getU_name());
        person_tell.setText(user.getU_phone());
        person_email.setText(user.getU_email());
        person_describ.setText(getSay());
        String sex = user.getU_sex();
        if ("男".equals(sex)){
            boy.setChecked(true);
        }else {
            gril.setChecked(true);
        }
        card_type.setText("个人信息");
        name.setText(user.getU_name());

        back.setOnClickListener(this);
        button.setOnClickListener(this);
    }

    //保存签名
    private void saveSay(String str){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("person_say",str).commit();
    }
    //获得签名
    private String getSay(){
        return sharedPreferences.getString("person_say","");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.save:
                saveEdit();
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    private String u_name,u_tell,u_emial,u_describ,sex;
    private void saveEdit(){
        u_name = person_name.getText().toString().trim();
        u_tell = person_tell.getText().toString().trim();
        u_emial = person_email.getText().toString().trim();
        u_describ = person_describ.getText().toString();
        if (boy.isChecked()){
            sex = "男";
        }else {
            sex = "女";
        }

        user.setU_name(u_name);
        user.setU_sex(sex);
        user.setU_email(u_emial);
        user.setU_phone(u_tell);
        saveSay(u_describ);

        MyApp.user1 = user;
        userDao.update(user);
        name.setText(u_name);
        toast("保存成功");
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyUp(keyCode, event);
    }
    private void toast(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
