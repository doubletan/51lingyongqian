package com.example.apple.lingyongqian.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.dao.AllCardInfo;
import com.example.apple.lingyongqian.dao.AllCardInfoDao;
import com.example.apple.lingyongqian.dao.AllInfoDao;
import com.example.apple.lingyongqian.dao.DaoSession;
import com.example.apple.lingyongqian.dao.PlaceCards;
import com.example.apple.lingyongqian.dao.PlaceCardsDao;
import com.example.apple.lingyongqian.dao.User;
import com.example.apple.lingyongqian.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class AllPlaceItemActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView back,save;
    private TextView card_type,place1,place_info;

    private EditText place_name,place_address,place_describ;
    private String p_name,p_address,p_describ;

    private MyApp myApp;
    private User user;
    private DaoSession daoSession;
    private AllInfoDao allInfoDao;
    private AllCardInfo allCardInfo;
    private AllCardInfoDao allCardInfoDao;
    private PlaceCardsDao placeCardsDao;
    private PlaceCards placeCards;

    private Intent intent;
    private String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_place_item);
        intent = getIntent();
        Bundle bundle = intent.getBundleExtra("place");
        if (bundle!=null){
            placeCards = bundle.getParcelable("place_item");
        }
        initView();
    }
    @Override
    protected void onStart() {
        super.onStart();
        StatusBarUtil.setColor(this, Color.parseColor("#607d8b"), 0);
    }

    private void initView(){

        myApp = MyApp.getApp();
        user = MyApp.user1;
        daoSession = myApp.getDaoSession(this);
        placeCardsDao = daoSession.getPlaceCardsDao();
        allCardInfoDao = daoSession.getAllCardInfoDao();

        back = (ImageView) findViewById(R.id.back);
        card_type = (TextView) findViewById(R.id.card_type);
        save = (ImageView) findViewById(R.id.save_card);
        back = (ImageView) findViewById(R.id.back);
        place1 = (TextView) findViewById(R.id.place1);
        place_info = (TextView) findViewById(R.id.place_info);

        place_name = (EditText) findViewById(R.id.place_name);
        place_address = (EditText) findViewById(R.id.place_address);
        place_describ = (EditText) findViewById(R.id.place_describ);
        name = placeCards.getS_name();
        card_type.setText(name);
        place_name.setText(name);
        place_address.setText(placeCards.getS_address());
        place_describ.setText(placeCards.getS_describ());
        place1.setText("Edit Place");
        place_info.setText("edit place base informations");

        save.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.save_card:
                getCardContent();
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    private void getCardContent() {
        allCardInfo = getItem();
        p_name = place_name.getText().toString().trim();
        if ("".equals(p_name)){
            toast("地名未填写");
            return;
        }
        if (!name.equals(p_name)){
            List<PlaceCards> list = loadOnePlaceByDate(p_name,placeCards.getS_add_date());
            if (list!=null && list.size()!=0){
                toast("该地名已存在");
                return;
            }
        }
        p_address = place_address.getText().toString();
        p_describ = place_describ.getText().toString();

        placeCards.setS_name(p_name);
        placeCards.setS_address(p_address);
        placeCards.setS_describ(p_describ);
        placeCardsDao.update(placeCards);
        allCardInfo.setAc_card_name(p_name);
        allCardInfoDao.update(allCardInfo);
        toast("保存成功");
        setResult(6);
        finish();
    }
    //根据name,date读取单条place卡片
    private List<PlaceCards> loadOnePlaceByDate(String name, String date){
        List<PlaceCards> list = new ArrayList<>();
        QueryBuilder builder = placeCardsDao.queryBuilder();
        builder.where(PlaceCardsDao.Properties.S_name.eq(name),PlaceCardsDao.Properties.S_add_date.eq(date),PlaceCardsDao.Properties.S_pid.eq(user.getU_id())).build();
        list = builder.list();
        return list;
    }
    private AllCardInfo getItem(){
        QueryBuilder builder = allCardInfoDao.queryBuilder();
        builder.where(AllCardInfoDao.Properties.Ac_card_name.eq(name), AllCardInfoDao.Properties.Ac_add_date.eq(placeCards.getS_add_date()), AllCardInfoDao.Properties.Ac_pid.eq(user.getU_id())).build();
        return (AllCardInfo) builder.list().get(0);
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
