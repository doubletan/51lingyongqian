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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.dao.AllCardInfo;
import com.example.apple.lingyongqian.dao.AllCardInfoDao;
import com.example.apple.lingyongqian.dao.AllInfo;
import com.example.apple.lingyongqian.dao.AllInfoDao;
import com.example.apple.lingyongqian.dao.CompanyCards;
import com.example.apple.lingyongqian.dao.CompanyCardsDao;
import com.example.apple.lingyongqian.dao.DaoSession;
import com.example.apple.lingyongqian.dao.PersonCards;
import com.example.apple.lingyongqian.dao.PersonCardsDao;
import com.example.apple.lingyongqian.dao.PlaceCards;
import com.example.apple.lingyongqian.dao.PlaceCardsDao;
import com.example.apple.lingyongqian.dao.User;
import com.example.apple.lingyongqian.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class AddCardActivity extends AppCompatActivity implements View.OnClickListener {

    private View person_liner,place_liner,company_liner;//三大卡片
    private View add_person,add_place,add_company,save_card;
    private int a = 0;//添加卡片，默认为0-即添加人物为默认画面，1-地点，2-公司
    private int b= 0;
    private String date = "";
    private Toolbar toolbar;
    private TextView card_type;//toolbar标题
    private RadioButton boy,gril;
    private PersonCards personCards = new PersonCards();
    private CompanyCards companyCards = new CompanyCards();
    private PlaceCards placeCards = new PlaceCards();
    private MyApp myApp;
    private User user;
    private DaoSession daoSession;
    private AllInfoDao allInfoDao;
    private AllCardInfo allCardInfo;
    private AllCardInfoDao allCardInfoDao;
    private PersonCardsDao personCardsDao;
    private CompanyCardsDao companyCardsDao;
    private PlaceCardsDao placeCardsDao;
    private EditText
            person_name,person_address,person_tell,person_email,person_describ,
            company_name,company_address,company_tell,company_email,company_describ,
            place_name,place_address,place_describ;
    private String
            u_name,u_sex,u_address,u_tell,u_email,u_describ,
            c_name,c_address,c_tell,c_email,c_describ,
            p_name,p_address,p_describ;

    //fragment跳转来的数据
    private Intent intent;
    private boolean isFragmentAdd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        myApp = MyApp.getApp();
        myApp.addToList(this);
        intent = getIntent();
        intent.getStringExtra("fragmentAddCard");
        if (!(intent.getStringExtra("fragmentAddCard")==null)){
            date = intent.getStringExtra("fragmentAddCard");
            isFragmentAdd = true;
        }else {
            date = myApp.today_date;
        }
        user = myApp.user1;
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("---", "AddCardActivity onStart");
        StatusBarUtil.setColor(this, Color.parseColor("#607d8b"), 0);
    }

    private void initView(){
        daoSession = myApp.getDaoSession(this);
        allInfoDao = daoSession.getAllInfoDao();
        personCardsDao = daoSession.getPersonCardsDao();
        companyCardsDao = daoSession.getCompanyCardsDao();
        placeCardsDao = daoSession.getPlaceCardsDao();
        allCardInfoDao = daoSession.getAllCardInfoDao();


        toolbar = (Toolbar) findViewById(R.id.add_toolbar);
        setToolbarDate();

        person_liner = findViewById(R.id.person);
        place_liner = findViewById(R.id.place);
        company_liner = findViewById(R.id.company);

        add_person = findViewById(R.id.add_person_card);
        add_place = findViewById(R.id.add_place_card);
        add_company = findViewById(R.id.add_company_card);
        save_card = findViewById(R.id.save_card);

        add_person.setOnClickListener(this);
        add_place.setOnClickListener(this);
        add_company.setOnClickListener(this);
        save_card.setOnClickListener(this);

        person_name = (EditText) findViewById(R.id.person_name);
        boy = (RadioButton) findViewById(R.id.boy);
        gril = (RadioButton) findViewById(R.id.gril);
        person_address = (EditText) findViewById(R.id.person_address);
        person_tell = (EditText) findViewById(R.id.person_tell);
        person_email = (EditText) findViewById(R.id.person_email);
        person_describ = (EditText) findViewById(R.id.person_describ);

        company_name = (EditText) findViewById(R.id.company_name);
        company_address = (EditText) findViewById(R.id.company_address);
        company_tell = (EditText) findViewById(R.id.company_tell);
        company_email = (EditText) findViewById(R.id.company_email);
        company_describ = (EditText) findViewById(R.id.company_describ);

        place_name = (EditText) findViewById(R.id.place_name);
        place_address = (EditText) findViewById(R.id.place_address);
        place_describ = (EditText) findViewById(R.id.place_describ);
    }
    //toolbar
    private void setToolbarDate(){
        card_type = (TextView) findViewById(R.id.card_type);
        card_type.setText("添加人");
        TextView textView = (TextView) findViewById(R.id.date);
        textView.setText(date);
        //toolbar回退
        ImageView imageView = (ImageView) findViewById(R.id.back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.save_card){
            getCardContent();
        }else {
            setVisible(id);
        }
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
    //获得编辑的数据并写入数据库
    private void getCardContent(){
        //添加卡片，默认为0-即添加人物为默认画面，1-地点，2-公司
        if (a==0){
            allCardInfo = new AllCardInfo();
            u_name = person_name.getText().toString().trim();
            if ("".equals(u_name)){
                toast("姓名未填写");
                return;
            }
            List<PersonCards> list = loadOnePersonByDate(u_name,date);
            if (list!=null && list.size()!=0){
                toast("改人物已存在");
                return;
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

            personCards.setP_pid(user.getU_id());
            personCards.setP_sex(u_sex);
            personCards.setP_tell(u_tell);
            personCards.setP_describ(u_describ);
            personCards.setP_name(u_name);
            personCards.setP_email(u_email);
            personCards.setP_adress(u_address);
            personCards.setP_add_date(date);
            if (b==0){
                personCardsDao.insert(personCards);
                allCardInfo.setAc_type(2);
                allCardInfo.setAc_pid(user.getU_id());
                allCardInfo.setAc_card_name(u_name);
                allCardInfo.setAc_add_date(date);
                allCardInfoDao.insert(allCardInfo);
            }else {
                personCardsDao.update(personCards);
            }
            b=1;
        }else if (a==1){
            allCardInfo = new AllCardInfo();
            p_name = place_name.getText().toString().trim();
            if ("".equals(p_name)){
                toast("地名未填写");
                return;
            }
            List<PlaceCards> list = loadOnePlaceByDate(p_name,date);
            if (list!=null && list.size()!=0){
                toast("该地名已存在");
                return;
            }
            p_address = place_address.getText().toString();
            p_describ = place_describ.getText().toString();

            placeCards.setS_name(p_name);
            placeCards.setS_address(p_address);
            placeCards.setS_describ(p_describ);
            placeCards.setS_add_date(date);
            placeCards.setS_pid(user.getU_id());
            if (b==0){
                placeCardsDao.insert(placeCards);
                allCardInfo.setAc_type(1);
                allCardInfo.setAc_pid(user.getU_id());
                allCardInfo.setAc_card_name(p_name);
                allCardInfo.setAc_add_date(date);
                allCardInfoDao.insert(allCardInfo);
            }else {
                placeCardsDao.update(placeCards);
            }
            b=1;
        }else {
            allCardInfo = new AllCardInfo();
            c_name = company_name.getText().toString().trim();
            if ("".equals(c_name)){
                toast("公司名称未填写");
                return;
            }
            List<CompanyCards> list = loadOneCompanyByDate(c_name,date);
            if (list!=null && list.size()!=0){
                toast("公司已存在");
                return;
            }
            c_address = company_address.getText().toString();
            c_tell = company_tell.getText().toString();
            c_email = company_email.getText().toString();
            c_describ = company_describ.getText().toString();

            companyCards.setC_pid(user.getU_id());
            companyCards.setC_name(c_name);
            companyCards.setC_address(c_address);
            companyCards.setC_tell(c_tell);
            companyCards.setC_email(c_email);
            companyCards.setC_describ(c_describ);
            companyCards.setC_add_date(date);
            if (b==0){
                companyCardsDao.insert(companyCards);
                allCardInfo.setAc_type(3);
                allCardInfo.setAc_pid(user.getU_id());
                allCardInfo.setAc_card_name(c_name);
                allCardInfo.setAc_add_date(date);
                allCardInfoDao.insert(allCardInfo);
            }else {
                companyCardsDao.update(companyCards);
            }
            b=1;
        }
        AllInfo info = checkTodayDate(date,allInfoDao);
        if (info!=null){
            Log.i("---","info不为空:"+info.getA_add_date());
            int ishave_cards = info.getA_cards();
            if (ishave_cards==0){
                info.setA_cards(1);
                allInfoDao.update(info);
            }
        }else {
            info = new AllInfo();
            info.setA_notes(0);
            info.setA_add_date(date);
            info.setA_pid(user.getU_id());
            info.setA_bills(0);
            info.setA_cards(1);
            info.setA_diarys(0);
            info.setA_feeling(1);
            allInfoDao.insert(info);
        }
        toast("添加成功");
        if(isFragmentAdd){
            setResult(a);
        }
        finish();
    }
    //输出方法
    private void toast(String str){
        Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
    }
    //显示指定id的卡片
    private void setVisible(int id){
        b=0;
        if (id==R.id.add_person_card){
            a=0;
            card_type.setText("添加人");
            place_liner.setVisibility(View.GONE);
            company_liner.setVisibility(View.GONE);
            person_liner.setVisibility(View.VISIBLE);
        }else if (id==R.id.add_place_card){
            a=1;
            card_type.setText("添加地点");
            person_liner.setVisibility(View.GONE);
            company_liner.setVisibility(View.GONE);
            place_liner.setVisibility(View.VISIBLE);
        }else {
            a=2;
            card_type.setText("添加公司");
            place_liner.setVisibility(View.GONE);
            person_liner.setVisibility(View.GONE);
            company_liner.setVisibility(View.VISIBLE);
        }
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
    //根据name,date读取单条place卡片
    private List<PlaceCards> loadOnePlaceByDate(String name, String date){
        List<PlaceCards> list = new ArrayList<>();
        QueryBuilder builder = placeCardsDao.queryBuilder();
        builder.where(PlaceCardsDao.Properties.S_name.eq(name),PlaceCardsDao.Properties.S_add_date.eq(date),PlaceCardsDao.Properties.S_pid.eq(user.getU_id())).build();
        list = builder.list();
        return list;
    }
    //根据name,date读取单条person卡片
    private List<PersonCards> loadOnePersonByDate(String name, String date){
        Log.i("---","greendao查询时间："+date);
        List<PersonCards> list = new ArrayList<>();
        QueryBuilder builder = personCardsDao.queryBuilder();
        builder.where(PersonCardsDao.Properties.P_name.eq(name),PersonCardsDao.Properties.P_add_date.eq(date),PersonCardsDao.Properties.P_pid.eq(user.getU_id())).build();
        list = builder.list();
        Log.i("---", builder.toString());
        return list;
    }
}
