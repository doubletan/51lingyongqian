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
import com.example.apple.lingyongqian.dao.CompanyCards;
import com.example.apple.lingyongqian.dao.CompanyCardsDao;
import com.example.apple.lingyongqian.dao.DaoSession;
import com.example.apple.lingyongqian.dao.User;
import com.example.apple.lingyongqian.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class AllCompanyItemActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText company_name,company_address,company_tell,company_email,company_describ;
    private String c_name,c_address,c_tell,c_email,c_describ;
    private ImageView back,save;
    private TextView card_type,company1,company_info;

    private MyApp myApp;
    private User user;
    private DaoSession daoSession;
    private AllCardInfo allCardInfo;
    private AllCardInfoDao allCardInfoDao;
    private CompanyCardsDao companyCardsDao;
    private CompanyCards companyCards;
    private Intent intent;
    private String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_company_item);
        intent = getIntent();
        Bundle bundle = intent.getBundleExtra("company");
        if (bundle!=null){
            companyCards = bundle.getParcelable("company_item");
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
        allCardInfoDao = daoSession.getAllCardInfoDao();
        companyCardsDao = daoSession.getCompanyCardsDao();

        company_name = (EditText) findViewById(R.id.company_name);
        company_address = (EditText) findViewById(R.id.company_address);
        company_tell = (EditText) findViewById(R.id.company_tell);
        company_email = (EditText) findViewById(R.id.company_email);
        company_describ = (EditText) findViewById(R.id.company_describ);
        save = (ImageView) findViewById(R.id.save_card);
        back = (ImageView) findViewById(R.id.back);
        card_type = (TextView) findViewById(R.id.card_type);
        company1 = (TextView) findViewById(R.id.company1);
        company_info = (TextView) findViewById(R.id.company_info);

        name = companyCards.getC_name();
        company_name.setText(name);
        company_address.setText(companyCards.getC_address());
        company_tell.setText(companyCards.getC_tell());
        company_email.setText(companyCards.getC_email());
        company_describ.setText(companyCards.getC_describ());
        card_type.setText(companyCards.getC_name());
        company1.setText("Edit Company");
        company_info.setText("edit Company base informations");

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
        c_name = company_name.getText().toString().trim();
        if ("".equals(c_name)){
            toast("公司名称未填写");
            return;
        }
        if(!name.equals(c_name)){
            List<CompanyCards> list = loadOneCompanyByDate(c_name,companyCards.getC_add_date());
            if (list!=null && list.size()!=0){
                toast("公司已存在");
                return;
            }
        }
        c_address = company_address.getText().toString();
        c_tell = company_tell.getText().toString();
        c_email = company_email.getText().toString();
        c_describ = company_describ.getText().toString();

        companyCards.setC_name(c_name);
        companyCards.setC_address(c_address);
        companyCards.setC_tell(c_tell);
        companyCards.setC_email(c_email);
        companyCards.setC_describ(c_describ);
        companyCardsDao.update(companyCards);
        allCardInfo.setAc_card_name(c_name);
        allCardInfoDao.update(allCardInfo);
        toast("保存成功");
        setResult(5);
        finish();
    }

    //根据name,date读取单条company卡片
    private List<CompanyCards> loadOneCompanyByDate(String name, String date){
        List<CompanyCards> list = new ArrayList<>();
        QueryBuilder builder = companyCardsDao.queryBuilder();
        builder.where(CompanyCardsDao.Properties.C_name.eq(name), CompanyCardsDao.Properties.C_add_date.eq(date), CompanyCardsDao.Properties.C_pid.eq(user.getU_id()))
                .build();
        list = builder.list();
        return list;
    }
    private AllCardInfo getItem(){
        QueryBuilder builder = allCardInfoDao.queryBuilder();
        builder.where(AllCardInfoDao.Properties.Ac_card_name.eq(name), AllCardInfoDao.Properties.Ac_add_date.eq(companyCards.getC_add_date()), AllCardInfoDao.Properties.Ac_pid.eq(user.getU_id())).build();
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
