package com.example.apple.lingyongqian.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.activity.MyApp;
import com.example.apple.lingyongqian.dao.AllInfo;
import com.example.apple.lingyongqian.dao.AllInfoDao;
import com.example.apple.lingyongqian.dao.DaoSession;
import com.example.apple.lingyongqian.dao.Diarys;
import com.example.apple.lingyongqian.dao.DiarysDao;
import com.example.apple.lingyongqian.dao.User;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class AddDiaryActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView date;
    private EditText diary_content;
    private ImageView back,share,del,save;

    private Intent intent;
    private MyApp myApp;
    private User user;
    private AllInfoDao allInfoDao;
    private AllInfo allInfo;
    private DaoSession daoSession;
    private DiarysDao diarysDao;
    private Diarys diarys = null;
    private boolean is_haveDiary = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary);
        intent = getIntent();
        Bundle bundle = intent.getBundleExtra("diary");
        if (bundle!=null){
            diarys = bundle.getParcelable("diary_item");
            is_haveDiary = true;
        }
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
        daoSession = myApp.getDaoSession(this);
        diarysDao = daoSession.getDiarysDao();
        allInfoDao = daoSession.getAllInfoDao();

        date = (TextView) findViewById(R.id.date);
        diary_content = (EditText) findViewById(R.id.diary_content);
        back = (ImageView) findViewById(R.id.back);
        share = (ImageView) findViewById(R.id.share);
        del = (ImageView) findViewById(R.id.del);
        save = (ImageView) findViewById(R.id.save);

        if (diarys!=null){
            date.setText("日记:"+diarys.getD_add_date());
            diary_content.setText(diarys.getD_textcontent());
        }else {
            date.setText("添加今天日记");
        }

        save.setOnClickListener(this);
        back.setOnClickListener(this);
        share.setOnClickListener(this);
        del.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.save:
                String content1 = diary_content.getText().toString();
                if ("".equals(content1)){
                    toast("日记没有内容");
                    return;
                }
                if (is_haveDiary){
                    diarys.setD_textcontent(content1);
                    diarysDao.update(diarys);
                }else {
                    diarys = new Diarys();
                    diarys.setD_add_date(MyApp.today_date);
                    diarys.setD_feeling(1);
                    diarys.setD_img_url("");
                    diarys.setD_pid(user.getU_id());
                    diarys.setD_textcontent(content1);
                    diarys.setD_voice_url("");
                    diarysDao.insert(diarys);

                    AllInfo info = getInfo(MyApp.today_date);
                    if (info!=null){
                        info.setA_diarys(1);
                        allInfoDao.update(info);
                    }else {
                        info = new AllInfo();
                        info.setA_add_date(MyApp.today_date);
                        info.setA_bills(0);
                        info.setA_cards(0);
                        info.setA_diarys(1);
                        info.setA_notes(0);
                        info.setA_feeling(1);
                        info.setA_pid(user.getU_id());
                        allInfoDao.insert(info);
                    }
                }
                InputMethodManager imm1
                        = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm1.hideSoftInputFromWindow(diary_content.getWindowToken(), 0);
                toast("保存成功");
                break;
            case R.id.share:
                toast("分享");
                break;
            case R.id.del:
                if (diarys != null) {
                    //delDialog();
                } else {
                    toast("没有日记内容");
                }
                break;
            case R.id.back:

                finish();
                break;
        }
    }



    private AllInfo getInfo(String date){
        QueryBuilder builder = allInfoDao.queryBuilder();
        builder.where(AllInfoDao.Properties.A_add_date.eq(date),AllInfoDao.Properties.A_pid.eq(user.getU_id())).build();
        List list = builder.list();
        if (list.size()==0){
            return null;
        }
        return (AllInfo) list.get(0);
    }

    private void toast(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }
}
