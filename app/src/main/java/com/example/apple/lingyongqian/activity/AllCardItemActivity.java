package com.example.apple.lingyongqian.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.dao.AllCardInfo;
import com.example.apple.lingyongqian.dao.AllCardInfoDao;
import com.example.apple.lingyongqian.dao.DaoSession;
import com.example.apple.lingyongqian.dao.PersonCards;
import com.example.apple.lingyongqian.dao.PersonCardsDao;
import com.example.apple.lingyongqian.dao.User;
import com.example.apple.lingyongqian.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class AllCardItemActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView back,save;
    private TextView card_type,person1,person_info;
    private RadioButton boy,gril;
    private EditText
            person_name,person_address,person_tell,person_email,person_describ;
    private String
            u_name,u_sex,u_address,u_tell,u_email,u_describ;
    private Intent intent;
    private DaoSession daoSession;
    private PersonCards personCards;
    private PersonCardsDao personCardsDao;
    private AllCardInfo allCardInfo;
    private AllCardInfoDao allCardInfoDao;
    private MyApp myApp;
    private User user;
    private String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_card_item);
        intent = getIntent();
        Bundle bundle = intent.getBundleExtra("person");
        if (bundle!=null){
            personCards = bundle.getParcelable("person_item");
        }
        initView();
    }

    private void initView(){

        myApp = MyApp.getApp();
        user = MyApp.user1;
        daoSession = myApp.getDaoSession(this);
        personCardsDao = daoSession.getPersonCardsDao();
        allCardInfoDao = daoSession.getAllCardInfoDao();

        back = (ImageView) findViewById(R.id.back);
        card_type = (TextView) findViewById(R.id.card_type);

        person_name = (EditText) findViewById(R.id.person_name);
        boy = (RadioButton) findViewById(R.id.boy);
        gril = (RadioButton) findViewById(R.id.gril);
        person_address = (EditText) findViewById(R.id.person_address);
        person_tell = (EditText) findViewById(R.id.person_tell);
        person_email = (EditText) findViewById(R.id.person_email);
        person_describ = (EditText) findViewById(R.id.person_describ);
        save = (ImageView) findViewById(R.id.save_card);
        person1 = (TextView) findViewById(R.id.person1);
        person_info = (TextView) findViewById(R.id.person_info);

        name = personCards.getP_name();
        person_name.setText(name);
        String sex = personCards.getP_sex();
        if ("男".equals(sex)){
            boy.setChecked(true);
        }else {
            gril.setChecked(true);
        }
        person_address.setText(personCards.getP_adress());
        person_tell.setText(personCards.getP_tell());
        person_email.setText(personCards.getP_email());
        person_describ.setText(personCards.getP_describ());
        card_type.setText(personCards.getP_name());
        person1.setText("Edit Person");
        person_info.setText("edit person base informations");

        save.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        StatusBarUtil.setColor(this, Color.parseColor("#607d8b"), 0);
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

    //获得编辑的数据并写入数据库
    private void getCardContent(){
        allCardInfo = getItem();
        u_name = person_name.getText().toString().trim();
        if ("".equals(u_name)){
            toast("姓名未填写");
            return;
        }
        if (!name.equals(u_name)){
            List<PersonCards> list = loadOnePersonByDate(u_name,personCards.getP_add_date());
            if (list!=null && list.size()!=0){
                toast("改人物已存在");
                return;
            }
        }
        if (boy.isChecked()){
            u_sex = "男";
        }else {
            u_sex = "女";
        }
        u_address = person_address.getText().toString();
        u_tell = person_tell.getText().toString();
        u_email = person_email.getText().toString();
        u_describ = person_describ.getText().toString();

        personCards.setP_sex(u_sex);
        personCards.setP_tell(u_tell);
        personCards.setP_describ(u_describ);
        personCards.setP_name(u_name);
        personCards.setP_email(u_email);
        personCards.setP_adress(u_address);
        personCardsDao.update(personCards);
        allCardInfo.setAc_card_name(u_name);
        allCardInfoDao.update(allCardInfo);
        toast("保存成功");
        setResult(4);
        finish();
    }
    //根据name,date读取单条person卡片
    private List<PersonCards> loadOnePersonByDate(String name, String date){
        Log.i("---", "greendao查询时间：" + date);
        List<PersonCards> list = new ArrayList<>();
        QueryBuilder builder = personCardsDao.queryBuilder();
        builder.where(PersonCardsDao.Properties.P_name.eq(name),PersonCardsDao.Properties.P_add_date.eq(date),PersonCardsDao.Properties.P_pid.eq(user.getU_id())).build();
        list = builder.list();
        Log.i("---", builder.toString());
        return list;
    }

    private AllCardInfo getItem(){
        QueryBuilder builder = allCardInfoDao.queryBuilder();
        builder.where(AllCardInfoDao.Properties.Ac_card_name.eq(name), AllCardInfoDao.Properties.Ac_add_date.eq(personCards.getP_add_date()), AllCardInfoDao.Properties.Ac_pid.eq(user.getU_id())).build();
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
        Toast.makeText(this,str, Toast.LENGTH_SHORT).show();
    }
}
