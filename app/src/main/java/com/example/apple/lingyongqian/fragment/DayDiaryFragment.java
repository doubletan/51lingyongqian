package com.example.apple.lingyongqian.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.activity.MyApp;
import com.example.apple.lingyongqian.dao.AllInfo;
import com.example.apple.lingyongqian.dao.AllInfoDao;
import com.example.apple.lingyongqian.dao.DaoSession;
import com.example.apple.lingyongqian.dao.Diarys;
import com.example.apple.lingyongqian.dao.DiarysDao;
import com.example.apple.lingyongqian.dao.User;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class DayDiaryFragment extends Fragment implements View.OnClickListener {

    private View view,show_contents;
    private TextView date;
    private EditText diary_content;
    private ImageView back,share,del,save;

    private Bundle bundle;
    private String dateString;

    private MyApp myApp;
    private User user;
    private AllInfoDao allInfoDao;
    private AllInfo info;
    private DaoSession daoSession;
    private DiarysDao diarysDao;
    private Diarys diary;
    private List<Diarys> diarysList = new ArrayList<>();

    private boolean is_haveDiary = false;

    public DayDiaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_day_diary, null);
        show_contents = view.findViewById(R.id.show_contents);
        date = (TextView) view.findViewById(R.id.date);
        back = (ImageView) view .findViewById(R.id.back);
        share = (ImageView) view .findViewById(R.id.share);
        del = (ImageView) view .findViewById(R.id.del);
        save = (ImageView) view .findViewById(R.id.save);

        bundle = getArguments();


        save.setOnClickListener(this);
        back.setOnClickListener(this);
        share.setOnClickListener(this);
        del.setOnClickListener(this);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (bundle!=null){
            info = bundle.getParcelable("info");
            dateString = info.getA_add_date();
            date.setText("日记: "+dateString);
        }
        myApp = MyApp.getApp();
        user = MyApp.user1;
        daoSession = myApp.getDaoSession(getContext());
        diarysDao = daoSession.getDiarysDao();
        allInfoDao = daoSession.getAllInfoDao();
        loadDiaryByDate(dateString);
        showDiary();
    }

    //读取日记
    private void loadDiaryByDate(String date){
        QueryBuilder builder = diarysDao.queryBuilder();
        builder.where(DiarysDao.Properties.D_add_date.eq(date),DiarysDao.Properties.D_pid.eq(user.getU_id())).build();
        diarysList = builder.list();
    }

    //展示日记
    private void showDiary(){
        show_contents.setVisibility(View.VISIBLE);
        diary_content = (EditText) show_contents.findViewById(R.id.diary_content);
        if (diarysList.size()!=0){
            diary = diarysList.get(0);
            diary_content.setText(diary.getD_textcontent());
            is_haveDiary = true;
        }else {
            diary_content.setHint("编辑日记");
            diary_content.setHintTextColor(Color.parseColor("#546e7a"));
            is_haveDiary = false;
        }

    }

    private int saveimg = 0;
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.save:
                saveimg = 1;
                String content1 = diary_content.getText().toString();
                if ("".equals(content1)){
                    Toast.makeText(getContext(), "日记没有内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (is_haveDiary){
                    diary.setD_textcontent(content1);
                    diarysDao.update(diary);
                }else {
                    diary = new Diarys();
                    diary.setD_add_date(dateString);
                    diary.setD_feeling(1);
                    diary.setD_img_url("");
                    diary.setD_pid(user.getU_id());
                    diary.setD_textcontent(content1);

                    loadDiaryByDate(dateString);
                    if (diarysList.size()!=0){
                        Diarys diarys = diarysList.get(0);
                        diarys.setD_textcontent(content1);
                        diarysDao.update(diarys);
                    }else {
                        diarysDao.insert(diary);
                        info.setA_diarys(1);
                        allInfoDao.update(info);
                    }
//                    diary.setD_voice_url("");
//                    diarysDao.insert(diary);
//                    info.setA_diarys(1);
//                    allInfoDao.update(info);
                }
                InputMethodManager imm1
                        = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm1.hideSoftInputFromWindow(diary_content.getWindowToken(), 0);
                Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
                saveimg=0;
                break;
            case R.id.share:
                if (diary!=null){
                    Intent intent=new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                    intent.putExtra(Intent.EXTRA_TEXT, diary.getD_textcontent());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(Intent.createChooser(intent, getActivity().getTitle()));
                }else {
                    Toast.makeText(getContext(), "请保存后分享", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.del:
                if (diary != null) {
                    delDialog();
                } else {
                    Toast.makeText(getContext(), "未保存", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.back:
                onDestroy();
                getActivity().finish();
                break;
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        String content1 = diary_content.getText().toString();
        if (saveimg==0){
            if ("".equals(content1)){
                Toast.makeText(getContext(), "日记内容为空，未保存", Toast.LENGTH_SHORT).show();
                return;
            }
            if (is_haveDiary){
                diary.setD_textcontent(content1);
                diarysDao.update(diary);
            }else {
                diary = new Diarys();
                diary.setD_add_date(dateString);
                diary.setD_feeling(1);
                diary.setD_img_url("");
                diary.setD_pid(user.getU_id());
                diary.setD_textcontent(content1);
                diary.setD_voice_url("");

                loadDiaryByDate(dateString);
                if (diarysList.size()!=0){
                    Diarys diarys = diarysList.get(0);
                    diarys.setD_textcontent(content1);
                    diarysDao.update(diarys);
                }else {
                    diarysDao.insert(diary);
                    info.setA_diarys(1);
                    allInfoDao.update(info);
                }
            }
        }
    }

    //删除数据dialog
    private void delDialog(){
        int color = Color.parseColor("#546e7a");
        new MaterialDialog.Builder(getContext())
                .content("删除日记？")
                .positiveText("删除")
                .titleColor(color)
                .positiveColor(color)
                .negativeText("取消")
                .negativeColor(color)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if ("POSITIVE".equals(which.toString())) {
                            diarysDao.delete(diary);
                            if (info.getA_notes() == 0 && info.getA_cards() == 0 && info.getA_bills() == 0) {
                                allInfoDao.delete(info);
                            } else {
                                info.setA_diarys(0);
                                allInfoDao.update(info);
                            }
                            diary_content.setText("");
                            diary_content.setHint("编辑日记");
                            Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }




}
