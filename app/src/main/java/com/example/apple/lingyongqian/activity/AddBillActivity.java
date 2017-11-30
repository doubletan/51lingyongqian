package com.example.apple.lingyongqian.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.dao.AllBillInfo;
import com.example.apple.lingyongqian.dao.AllBillInfoDao;
import com.example.apple.lingyongqian.dao.AllInfo;
import com.example.apple.lingyongqian.dao.AllInfoDao;
import com.example.apple.lingyongqian.dao.Bills;
import com.example.apple.lingyongqian.dao.BillsDao;
import com.example.apple.lingyongqian.dao.DaoSession;
import com.example.apple.lingyongqian.dao.User;
import com.example.apple.lingyongqian.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class AddBillActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView toolbar_name,date;//toolbar标题和日期
    private View add_card,minus_card;//账单卡
    private ImageView add_img,minus_img,save_img;//底部图片按钮
    private int a = 0;//添加卡片，默认为0-即添加入账账单为默认画面，1-出账
    private String today_date = "";
    private int b = 0;

    private MyApp myApp;
    private User user;
    private DaoSession daoSession;
    private AllInfoDao allInfoDao;
    private AllBillInfo allBillInfo;
    private AllBillInfoDao allBillInfoDao;
    private Bills bills = new Bills();
    private BillsDao billsDao;

    private Intent intent;
    private boolean isFragmentAdd;

    private EditText
            add_num,money_from,in_describ,
            plus_num,money_out,out_describ;
    private String
            addnum,moneyfrom,indescrib,
            plusnum,moneyout,outdescrib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bill);
        myApp = MyApp.getApp();
        myApp.addToList(this);
        user = MyApp.user1;
        intent = getIntent();
        if (!(intent.getStringExtra("fragmentAddBill")==null)){
            Log.i("---","fragmentbill add");
            today_date = intent.getStringExtra("fragmentAddBill");
            isFragmentAdd = true;
        }else {
            today_date = myApp.today_date;
        }
        initView();
    }
    private void initView(){
        daoSession = myApp.getDaoSession(this);
        allInfoDao = daoSession.getAllInfoDao();
        allBillInfoDao = daoSession.getAllBillInfoDao();
        billsDao = daoSession.getBillsDao();

        toolbar = (Toolbar) findViewById(R.id.add_toolbar);
        toolbar_name = (TextView) findViewById(R.id.card_type);
        date = (TextView) findViewById(R.id.date);
        toolbar_name.setText("入账");
        date.setText(today_date);
        //toolbar回退
        ImageView imageView = (ImageView) findViewById(R.id.back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        add_card = findViewById(R.id.add);
        minus_card = findViewById(R.id.minus);

        add_img = (ImageView) findViewById(R.id.add_bill);
        minus_img = (ImageView) findViewById(R.id.minus_bill);
        save_img = (ImageView) findViewById(R.id.save_bill_card);

        add_num = (EditText) findViewById(R.id.add_num);
        money_from = (EditText) findViewById(R.id.money_from);
        in_describ = (EditText) findViewById(R.id.in_describ);
        plus_num = (EditText) findViewById(R.id.plus_num);
        money_out = (EditText) findViewById(R.id.money_out);
        out_describ = (EditText) findViewById(R.id.out_describ);

        add_img.setOnClickListener(this);
        minus_img.setOnClickListener(this);
        save_img.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        StatusBarUtil.setColor(this, Color.parseColor("#607d8b"), 0);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.save_bill_card){
            getBillContent();
        }else {
            setVisible(id);
        }
    }

    //获取数据并保存
    private void getBillContent(){
        if (a==0){
            allBillInfo = new AllBillInfo();
            addnum = add_num.getText().toString().trim();
            moneyfrom = money_from.getText().toString().trim();
            if ("".equals(addnum)){
                toast("入账数目未填写");
                return;
            }
            if ("".equals(moneyfrom)){
                toast("来源未填写");
                return;
            }
            List<Bills> billsList = loadOneBillByDate(moneyfrom,today_date);
            if (billsList!=null && billsList.size()!=0){
                toast("已存在 '"+moneyfrom+"' 该账目");
                return;
            }
            indescrib = in_describ.getText().toString();

            bills.setB_add_date(today_date);
            bills.setB_type("1");
            bills.setB_name(moneyfrom);
            bills.setB_num(addnum);
            bills.setB_pid(user.getU_id());
            bills.setB_describ(indescrib);
            if (b==0){
                billsDao.insert(bills);
                allBillInfo.setAl_pid(user.getU_id());
                allBillInfo.setAl_add_date(today_date);
                allBillInfo.setAl_bill_name(moneyfrom);
                allBillInfo.setAl_bill_num(addnum);
                allBillInfo.setAl_bill_type(1);
                allBillInfoDao.insert(allBillInfo);
            }else {
                billsDao.update(bills);
            }
            b=1;
        }else {
            allBillInfo = new AllBillInfo();
            plusnum = plus_num.getText().toString().trim();
            moneyout = money_out.getText().toString().trim();
            if ("".equals(plusnum)){
                toast("出账数目未填写");
                return;
            }
            if ("".equals(moneyout)){
                toast("出处未填写");
                return;
            }
            List<Bills> billsList = loadOneBillByDate(moneyout,today_date);
            if (billsList!=null && billsList.size()!=0){
                toast("已存在 '"+moneyout+"' 该账目");
                return;
            }
            outdescrib = out_describ.getText().toString();

            bills.setB_add_date(today_date);
            bills.setB_type("2");
            bills.setB_name(moneyout);
            bills.setB_num(plusnum);
            bills.setB_pid(user.getU_id());
            bills.setB_describ(outdescrib);

            if (b==0){
                billsDao.insert(bills);
                allBillInfo.setAl_pid(user.getU_id());
                allBillInfo.setAl_add_date(today_date);
                allBillInfo.setAl_bill_name(moneyout);
                allBillInfo.setAl_bill_num(plusnum);
                allBillInfo.setAl_bill_type(2);
                allBillInfoDao.insert(allBillInfo);
            }else {
                billsDao.update(bills);
            }
            b=1;
        }
        AllInfo info = checkTodayDate(today_date, allInfoDao);
        if (info!=null){
            Log.i("---", "info不为空:" + info.getA_add_date());
            int ishave_cards = info.getA_bills();
            if (ishave_cards==0){
                info.setA_bills(1);
                allInfoDao.update(info);
            }
        }else {
            info = new AllInfo();
            info.setA_notes(0);
            info.setA_add_date(today_date);
            info.setA_pid(user.getU_id());
            info.setA_bills(1);
            info.setA_cards(0);
            info.setA_diarys(0);
            info.setA_feeling(1);
            allInfoDao.insert(info);
        }
        if (isFragmentAdd){
            setResult(a);
        }
        toast("添加成功");
        finish();
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

    //输出方法
    private void toast(String str){
        Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
    }

    //显示指定id的卡片
    private void setVisible(int id){
        b = 0;
        if (id==R.id.add_bill){
            a=0;
            toolbar_name.setText("入账");
            minus_card.setVisibility(View.GONE);
            add_card.setVisibility(View.VISIBLE);
        }else if (id == R.id.minus_bill){
            a=1;
            toolbar_name.setText("出账");
            add_card.setVisibility(View.GONE);
            minus_card.setVisibility(View.VISIBLE);
        }
    }

    //根据name读取单条账单
    private List<Bills> loadOneBillByDate(String name, String date){
        List<Bills> list = new ArrayList<>();
        QueryBuilder builder = billsDao.queryBuilder();
        builder.where(BillsDao.Properties.B_name.eq(name),BillsDao.Properties.B_add_date.eq(date),BillsDao.Properties.B_pid.eq(user.getU_id())).build();
        list = builder.list();
        return list;
    }
}
