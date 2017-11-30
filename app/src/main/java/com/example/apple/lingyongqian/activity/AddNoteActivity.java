package com.example.apple.lingyongqian.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.dao.AllInfo;
import com.example.apple.lingyongqian.dao.AllInfoDao;
import com.example.apple.lingyongqian.dao.DaoSession;
import com.example.apple.lingyongqian.dao.Notes;
import com.example.apple.lingyongqian.dao.NotesDao;
import com.example.apple.lingyongqian.dao.User;
import com.example.apple.lingyongqian.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class AddNoteActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView toolbar_name,date;//toolbar标题和日期
    private EditText edit_text;//便签
    private String content,add_date,is_done;//内容，添加时间，是否完成
    private String[] img_url,voice_url;//添加的图片，语言   --以后完善
    private int is_alert;//是否添加闹钟   --以后完善
    private ImageView save_img;//保存键
    private int input_num = 0;//是否多次保存

    private MyApp myApp;
    private DaoSession daoSession;
    private AllInfoDao allInfoDao;
    private NotesDao notesDao;

    private User user;
    private Notes notes = new Notes();

    //fragment跳转来的数据
    private Intent intent;
    private Bundle bundle;
    private Notes noteFromFragment;
    private boolean isFromFragment = false;
    private boolean isFragmentAdd = false;
    private String fragmentaddDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        myApp = MyApp.getApp();
        myApp.addToList(this);
        user = MyApp.user1;
        StatusBarUtil.setColor(this, Color.parseColor("#607d8b"), 0);
        //fragment跳转来的数据
        intent = getIntent();
        if (intent==null){
            Log.i("---","intent为空");
        }
        //是否是fragment的添加
        //fragmentaddDate = intent.getStringExtra("fragmentAddNote");
        //是否是fragment的修改
        bundle = intent.getBundleExtra("fragmentNote");
        if (bundle!=null){
            noteFromFragment = bundle.getParcelable("noteFromFragment");
            if (noteFromFragment!=null){
                isFromFragment = true;
                add_date = noteFromFragment.getN_add_date();
            }
        }else if (!(intent.getStringExtra("fragmentAddNote")==null)){
            fragmentaddDate = intent.getStringExtra("fragmentAddNote");
            Log.i("---",fragmentaddDate);
            add_date = fragmentaddDate;
            isFragmentAdd = true;
            Log.i("---",""+isFragmentAdd);
        }else {
            add_date = MyApp.today_date;//日期
        }

        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        StatusBarUtil.setColor(this, Color.parseColor("#607d8b"), 0);
    }

    private void initView(){
        daoSession = myApp.getDaoSession(this);
        allInfoDao = daoSession.getAllInfoDao();
        notesDao = daoSession.getNotesDao();


        toolbar = (Toolbar) findViewById(R.id.add_toolbar);
        toolbar_name = (TextView) findViewById(R.id.card_type);
        date = (TextView) findViewById(R.id.date);
        edit_text = (EditText) findViewById(R.id.edit_text);
        if (isFromFragment){
            toolbar_name.setText("便签");
            date.setText(add_date);
            InputMethodManager imm
                    = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edit_text.getWindowToken(), 0);
            edit_text.setText(noteFromFragment.getN_textcontent());
        }else if (isFragmentAdd){
            toolbar_name.setText("添加便签");
            date.setText(fragmentaddDate);
        }else {
            toolbar_name.setText("添加便签");
            date.setText(MyApp.today_date);
        }

        //toolbar回退
        ImageView imageView = (ImageView) findViewById(R.id.back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeBack = 1;
                if (isFromFragment){
                    getNoteContent();
                    saveFragmentNote();
                    InputMethodManager imm
                            = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edit_text.getWindowToken(), 0);
                    setResult(2);
                    finish();
                }else if (isFragmentAdd){
                    getNoteContent();
                    saveFragmentNote();
                    setResult(3);
                    finish();
                }else {
                    getNoteContent();
                    saveNote();
                    finish();
                }
            }
        });

        save_img = (ImageView) findViewById(R.id.save_card);
        save_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNoteContent();
                if (isFromFragment){
                    saveFragmentNote();
                    InputMethodManager imm
                            = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edit_text.getWindowToken(), 0);
                    toast("保存");
                    setResult(2);
                    finish();
                }else if (isFragmentAdd){
                    saveFragmentNote();
                    InputMethodManager imm
                            = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edit_text.getWindowToken(), 0);
                    toast("保存");
                    setResult(3);
                    finish();
                }else {
                    saveNote();
                }

            }
        });
    }

    //获得编辑的便签信息
    private void getNoteContent(){
        is_done = "0";//默认未完成
        content = edit_text.getText().toString();//内容
    }

    //输出方法
    private void toast(String str){
        Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
    }

    //保存fragment的便签
    private void saveFragmentNote(){
        if ("".equals(content)){
            return;
        }
        if (isFromFragment){
            noteFromFragment.setN_textcontent(content);
            notesDao.update(noteFromFragment);
        }else if (isFragmentAdd){
            AllInfo info = checkTodayDate(add_date,allInfoDao);
            if (info==null){
                info = new AllInfo();
                info.setA_pid(user.getU_id());
                info.setA_notes(1);
                info.setA_diarys(0);
                info.setA_cards(0);
                info.setA_bills(0);
                info.setA_feeling(1);
                info.setA_add_date(add_date);
                allInfoDao.insert(info);
            }else {
                info.setA_notes(1);
                allInfoDao.update(info);
            }
            notes.setN_pid(user.getU_id());
            notes.setN_textcontent(content);
            notes.setN_add_date(add_date);
            notes.setN_isdone("0");
            notes.setN_img_url("");
            notes.setN_voice_url("");
            long rowid = 0;
            if (input_num==0){
                rowid = notesDao.insert(notes);
            }else {
                //notes.setN_id(rowid);
                notesDao.update(notes);
            }
            input_num = 1;
        }

    }


    //保存便签,写入数据库
    private void saveNote(){
        if ("".equals(content)){
            if (homeBack==0){
                toast("给便签加点内容吧");
            }
            return;
        }
        AllInfo info = checkTodayDate(add_date,allInfoDao);
        if (info!=null){
            Log.i("---","info不为空:"+info.getA_add_date());
            int ishave_note = info.getA_notes();
            if (ishave_note==0){
                Log.i("---","今日便签不为空:"+info.getA_add_date());
                info.setA_notes(1);
                allInfoDao.update(info);
            }
        }else {
            info = new AllInfo();
            info.setA_notes(1);
            info.setA_add_date(add_date);
            info.setA_pid(user.getU_id());
            info.setA_bills(0);
            info.setA_cards(0);
            info.setA_diarys(0);
            info.setA_feeling(1);
            allInfoDao.insert(info);
        }

        notes.setN_pid(user.getU_id());
        notes.setN_textcontent(content);
        notes.setN_add_date(add_date);
        notes.setN_isdone("0");
        notes.setN_img_url("");
        notes.setN_voice_url("");
        long rowid = 0;
        if (input_num==0){
            rowid = notesDao.insert(notes);
        }else {
            //notes.setN_id(rowid);
            notesDao.update(notes);
        }

        input_num = 1;

        initSnackBar();
    }

    private int homeBack = 0;
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            homeBack = 1;
            if (isFromFragment){
                getNoteContent();
                saveFragmentNote();
                InputMethodManager imm
                        = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edit_text.getWindowToken(), 0);
                setResult(2);
            }else if (isFragmentAdd){
                getNoteContent();
                saveFragmentNote();
                saveFragmentNote();
                setResult(3);
            }else {
                getNoteContent();
                saveNote();
            }
            finish();
        }
        return super.onKeyUp(keyCode, event);
    }

    //查询数据库是否存在当前日期
    public AllInfo checkTodayDate(String today_date, AllInfoDao infoDao){
        List<AllInfo> today_info = new ArrayList<>();
        QueryBuilder builder = infoDao.queryBuilder();
        builder.where(AllInfoDao.Properties.A_add_date.eq(today_date),AllInfoDao.Properties.A_pid.eq(user.getU_id())).build();
        today_info = builder.list();
        AllInfo info = null;
        if (today_info!=null && today_info.size()!=0){
            info = today_info.get(0);
        }
        return info;
    }

    private void initSnackBar(){
        InputMethodManager imm
                = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit_text.getWindowToken(), 0);
        if (homeBack==1){
            toast("自动保存");
        }else {
            toast("保存成功");
        }
//        //调用make方法创建SnackBar的实例
//        final Snackbar snackbar = Snackbar.make(save_img, "便签保存成功", Snackbar.LENGTH_LONG);
//        //为SnackBar设置Action
//        snackbar.setAction("返回首页", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                snackbar.dismiss();
//                finish();
//            }
//        });
//        //显示SanckBar
//        snackbar.show();
    }

}
