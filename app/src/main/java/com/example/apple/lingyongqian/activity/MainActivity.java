package com.example.apple.lingyongqian.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.adapter.MyBaseAdapter;
import com.example.apple.lingyongqian.adapter.MyBillAdapter;
import com.example.apple.lingyongqian.adapter.MyCardAdapter;
import com.example.apple.lingyongqian.adapter.MyHistoryAdapter;
import com.example.apple.lingyongqian.adapter.MyNotesAdapter;
import com.example.apple.lingyongqian.dao.AllBillInfo;
import com.example.apple.lingyongqian.dao.AllBillInfoDao;
import com.example.apple.lingyongqian.dao.AllCardInfo;
import com.example.apple.lingyongqian.dao.AllCardInfoDao;
import com.example.apple.lingyongqian.dao.AllInfo;
import com.example.apple.lingyongqian.dao.AllInfoDao;
import com.example.apple.lingyongqian.dao.AutoUser;
import com.example.apple.lingyongqian.dao.AutoUserDao;
import com.example.apple.lingyongqian.dao.Bills;
import com.example.apple.lingyongqian.dao.BillsDao;
import com.example.apple.lingyongqian.dao.CompanyCards;
import com.example.apple.lingyongqian.dao.CompanyCardsDao;
import com.example.apple.lingyongqian.dao.DaoSession;
import com.example.apple.lingyongqian.dao.Diarys;
import com.example.apple.lingyongqian.dao.DiarysDao;
import com.example.apple.lingyongqian.dao.Notes;
import com.example.apple.lingyongqian.dao.NotesDao;
import com.example.apple.lingyongqian.dao.PersonCards;
import com.example.apple.lingyongqian.dao.PersonCardsDao;
import com.example.apple.lingyongqian.dao.PlaceCards;
import com.example.apple.lingyongqian.dao.PlaceCardsDao;
import com.example.apple.lingyongqian.dao.User;
import com.example.apple.lingyongqian.dao.UserDao;
import com.example.apple.lingyongqian.utils.BitMapUtil;
import com.example.apple.lingyongqian.utils.FastBlur;
import com.example.apple.lingyongqian.utils.FileUitlity;
import com.example.apple.lingyongqian.utils.FullyLinearLayoutManager;
import com.example.apple.lingyongqian.utils.MyRecycleView;
import com.example.apple.lingyongqian.utils.StatusBarUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nikhilpanju.recyclerviewenhanced.OnActivityTouchListener;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener,RecyclerTouchListener.RecyclerTouchListenerHelper {

    //布局
    private PullToZoomScrollViewEx scrollView;
    private ImageView zoom_img;
    private View contentView,zoomView,headView;//内容视图
    private int[] a = new int[2];//存储内容页的坐标位置，用于更新toolbar的样式
    private View toolbar;
    private SlidingMenu slidingMenu;

    private CardView today_card;
    private String today_date = "";//今日日期
    private CardView noListView;
    private ImageView sex_img;//首页性别
    private ImageView head_img;
    private TextView day_num,year,month,userName;//当前控件
    private View add_note,add_card,add_bill;//快速导航

    private MyApp myApp;
    //-----------数据库操作类 及 实体类
    private DaoSession daoSession;
    private UserDao userDao;
    private User user;
    private BillsDao billsDao;
    private Bills bills;
    private CompanyCardsDao companyCardsDao;
    private CompanyCards companyCards;
    private DiarysDao diarysDao;
    private Diarys diarys;
    private NotesDao notesDao;
    private Notes notes;
    private PersonCardsDao personCardsDao;
    private PersonCards personCards;
    private PlaceCardsDao placeCardsDao;
    private PlaceCards placeCards;
    private AllInfoDao allInfoDao;
    private AllInfo allInfo;
    private AllCardInfo allCardInfo;
    private AllCardInfoDao allCardInfoDao;
    private AllBillInfoDao allBillInfoDao;
    private AllBillInfo allBillInfo;
    private List<AllInfo> allInfoList = new ArrayList<>();
    private List<Notes> notesList = new ArrayList<>();  //当前日期下的notes
    private List<AllCardInfo> allCardInfoList = new ArrayList<>();  //当前日期下的Card
    private List<AllBillInfo> allBillInfoList = new ArrayList<>();  //当前日期下的Bill
    //今日布局
    private MyRecycleView history_recycleview;
    private RecyclerTouchListener onTouchListener;

    private GridView today_cards,today_bills;
    private ListView notes_listview,myHistoryListVew;
    private View no_list,note_lay,card_lay,bill_lay,e_slid_menu;

    private MyHistoryAdapter historyAdapter;//以往记录
    private MainAdapter mAdapter;
    private MyCardAdapter adapter;//今日卡片
    private MyBillAdapter adapter1;//今日账单
    private MyNotesAdapter adapter2;//今日便签
    private ImageView today_diary,today_all;

    private AutoUser autoUser_db;

    private boolean is_today = true;
    private int load_size;

    public MainActivity() {
    }

    public static void launch(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myApp = MyApp.getApp();
        daoSession = myApp.getDaoSession(this);
        userDao = daoSession.getUserDao();
        StatusBarUtil.setColor(this, Color.parseColor("#607d8b"), 0);
        AddAutoUser();
//        if (!isLogin()){
//            Intent intent = new Intent(this,LoginActivity.class);
//            startActivity(intent);
//            finish();
//            return;
//        }
        myApp.addToList(this);
        initView();
        toolbar = findViewById(R.id.toolbar);
    }

    //第一次启动应用，默认注册用户，为以后的后台备份做准备 ---
    //用户本地id，以后注册成功之后后台返回一个后台id，这样就避免了用户id相同，或者用户名相同而无法正常登陆的情况
    private void AddAutoUser(){
        AutoUserDao autoUserDao = daoSession.getAutoUserDao();
        if (autoUserDao.loadAll().size()!=0){
            user = userDao.loadAll().get(0);
        }else {
            autoUserDao.insertOrReplace(new AutoUser(1l));
            User userAuto = new User(1l);
            userAuto.setU_name("昵称");
            userAuto.setU_img("");
            userDao.insertOrReplace(userAuto);
            autoUser_db = autoUserDao.loadAll().get(0);
            user = userDao.loadAll().get(0);
        }
        MyApp.user1 = user;
        if ("1".equals(user.getU_issetpass())){
            Intent intent = getIntent();
            if (!"yes".equals(intent.getStringExtra("ok"))){
                Intent intent2 = new Intent(MainActivity.this,PassToActivity.class);
                startActivity(intent2);
                finish();
            }
        }
    }


    //判断是否登陆
    private boolean isLogin(){
        Intent intent = getIntent();
        if ("1".equals(intent.getStringExtra("fromLogin"))){
            Log.i("---","来自fromLogin");
            user = MyApp.user1;
            if (user==null){
                finish();
            }
            return true;
        }else {
            SharedPreferences sp =  super.getSharedPreferences("saveLogin", Context.MODE_PRIVATE);
            String name=sp.getString("username","").trim();
            Log.i("---",name);
            if (!"".equals(name)){
                MyApp.user1 = loadUserByName(name).get(0);
                user = MyApp.user1;
                if (MyApp.user1==null){
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    //初始化布局
    private View load_more;//底部加载更多
    private TextView get_more;
    private void initView(){
        loadViewForCode();
        e_slid_menu = findViewById(R.id.view);
        userName = (TextView) headView.findViewById(R.id.username);
        userName.setText(user.getU_name());
        sex_img = (ImageView) headView.findViewById(R.id.sex_img);
        head_img = (ImageView) headView.findViewById(R.id.head_img);
        today_card = (CardView) contentView.findViewById(R.id.today_card);
        //load_more = contentView.findViewById(R.id.load_more);
        //get_more = (TextView) contentView.findViewById(R.id.get_more);

        //当天日期控件
        day_num = (TextView) today_card.findViewById(R.id.day_num);
        year = (TextView) today_card.findViewById(R.id.year);
        month = (TextView) today_card.findViewById(R.id.month);
        today_all = (ImageView) today_card.findViewById(R.id.today_more);

        today_cards = (GridView) today_card.findViewById(R.id.today_cards);

        today_bills = (GridView) today_card.findViewById(R.id.today_bills);
        notes_listview = (ListView) today_card.findViewById(R.id.note_list);

        note_lay = today_card.findViewById(R.id.note_lay);
        card_lay = today_card.findViewById(R.id.card_lay);
        bill_lay = today_card.findViewById(R.id.bill_lay);

        //myHistoryListVew = (ListView) contentView.findViewById(R.id.history);
        history_recycleview = (MyRecycleView) contentView.findViewById(R.id.history);
        noListView = (CardView) contentView.findViewById(R.id.noList);
        no_list = today_card.findViewById(R.id.no_content);

        //快速导航add_note,add_card,add_bill
        add_note = contentView.findViewById(R.id.menu1);
        add_card = contentView.findViewById(R.id.menu2);
        add_bill = contentView.findViewById(R.id.menu3);

        add_note.setOnClickListener(this);
        add_card.setOnClickListener(this);
        add_bill.setOnClickListener(this);
        today_all.setOnClickListener(this);
        e_slid_menu.setOnClickListener(this);
        userName.setOnClickListener(this);
        sex_img.setOnClickListener(this);
        head_img.setOnClickListener(this);


        allInfoDao = daoSession.getAllInfoDao();
        notesDao = daoSession.getNotesDao();
        allCardInfoDao = daoSession.getAllCardInfoDao();
        allBillInfoDao = daoSession.getAllBillInfoDao();
        billsDao = daoSession.getBillsDao();

        companyCardsDao = daoSession.getCompanyCardsDao();
        placeCardsDao = daoSession.getPlaceCardsDao();
        personCardsDao = daoSession.getPersonCardsDao();

        initBarAndStatus();//状态栏和toolbar样式

        sildMenu();
        applyBlur();
        zoomApplyBlur();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("---", "onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("---", "onStart");
        user = MyApp.user1;
        String imgUrl = user.getU_img();
        userName.setText(user.getU_name());
        Log.i("---","img:"+imgUrl);
        if (!"".equals(user.getU_img())){
            //Bitmap bitmap = getLoacalBitmap(imgUrl);
            Bitmap bitmap1 = BitMapUtil.getBitmap(user.getU_img(), 100, 100);
            head_img.setImageBitmap(bitmap1);
        }
        String sex = "";
        sex = user.getU_sex();
        if ("男".equals(sex)){
            sex_img.setImageResource(R.mipmap.boy);
        }else if ("女".equals(sex)){
            sex_img.setImageResource(R.mipmap.gril);
        }
        //初始化当天数据或布局
        loadDatas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        Log.i("---", "onResume");
    }

    //倒序返回所有AllInfo
    public List<AllInfo> select() {
        return  allInfoDao.queryBuilder().where(AllInfoDao.Properties.A_pid.eq(user.getU_id()))
                .orderDesc(AllInfoDao.Properties.A_id)
                .list();
    }

    //数据库测试
    private AllInfo today_infos;
    private void loadDatas() {
        getToday_date();//获得当前日期
        //显示当天日期
        //year.setText(today_date.substring(0,4));
        month.setText(today_date.substring(5,7)+" 月");
        day_num.setText(today_date.substring(8));

        allInfoList = select();
        today_infos = checkTodayDate(today_date, allInfoDao);
        if (today_infos!=null){
            MyApp.today = today_infos;
            allInfoList.remove(today_infos);
            //读取当前日期下的数据---便签，名片，账单
            notesList = loadNoteByDate(today_date);
            allCardInfoList = loadCardByDate(today_date);
            allBillInfoList = loadBillByDate(today_date);
        }
        load_size = allInfoList.size();
        if (allInfoList!=null && load_size!=0){
            history_recycleview.setVisibility(View.VISIBLE);
            noListView.setVisibility(View.GONE);
//            historyAdapter = new MyHistoryAdapter(this,allInfoList);
//            myHistoryListVew.setAdapter(historyAdapter);
            mAdapter = new MainAdapter(this,allInfoList);
            history_recycleview.setAdapter(mAdapter);
            history_recycleview.scrollToPosition(0);
            history_recycleview.setLayoutManager(new FullyLinearLayoutManager(this));
            Log.i("---", "history_recycleview");
            onTouchListener = new RecyclerTouchListener(this, history_recycleview);
            history_recycleview.addOnItemTouchListener(onTouchListener);
            onTouchListener
                    .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                        @Override
                        public void onRowClicked(int position) {
                            Intent intent = new Intent(getBaseContext(),OneDayAllActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("info",allInfoList.get(position));
                            intent.putExtra("info_item",bundle);
                            startActivity(intent);
                        }

                        @Override
                        public void onIndependentViewClicked(int independentViewID, int position) {

                        }
                    })
                    .setSwipeOptionViews(R.id.del_item)
                    .setSwipeable(R.id.card_item, R.id.bg, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                        @Override
                        public void onSwipeOptionClicked(int viewID, int position) {
                            if (viewID == R.id.del_item) {
                                final AllInfo allInfo = allInfoList.get(position);
                                final String date = allInfo.getA_add_date();
                                delDialog(allInfo, date);
                            }
                        }
                    });
        }else {
            history_recycleview.setVisibility(View.GONE);
            noListView.setVisibility(View.VISIBLE);
        }
        showTodayDate();
    }

    //删除数据
    private void delInfo(AllInfo allInfo,String date){
        if (allInfo.getA_notes()==1){
            QueryBuilder<Notes> qb1
                    = notesDao.queryBuilder().where(NotesDao.Properties.N_add_date.eq(date), NotesDao.Properties.N_pid.eq(user.getU_id()));
            qb1.buildDelete().executeDeleteWithoutDetachingEntities();
        }else if (allInfo.getA_cards()==1){
            Log.i("---","del cards");
            QueryBuilder<AllCardInfo> qb1
                    = allCardInfoDao.queryBuilder().where(AllCardInfoDao.Properties.Ac_add_date.eq(date),NotesDao.Properties.N_pid.eq(user.getU_id()));
            qb1.buildDelete().executeDeleteWithoutDetachingEntities();
            QueryBuilder<CompanyCards> qb2
                    = companyCardsDao.queryBuilder().where(CompanyCardsDao.Properties.C_add_date.eq(date), CompanyCardsDao.Properties.C_pid.eq(user.getU_id()));
            qb2.buildDelete().executeDeleteWithoutDetachingEntities();
            QueryBuilder<PersonCards> qb3
                    = personCardsDao.queryBuilder().where(PersonCardsDao.Properties.P_add_date.eq(date), PersonCardsDao.Properties.P_pid.eq(user.getU_id()));
            qb3.buildDelete().executeDeleteWithoutDetachingEntities();
            QueryBuilder<PlaceCards> qb4
                    = placeCardsDao.queryBuilder().where(PlaceCardsDao.Properties.S_add_date.eq(date),PlaceCardsDao.Properties.S_pid.eq(user.getU_id()));
            qb4.buildDelete().executeDeleteWithoutDetachingEntities();
        }else if (allInfo.getA_bills()==1){
            QueryBuilder<AllBillInfo> qb1
                    = allBillInfoDao.queryBuilder().where(AllBillInfoDao.Properties.Al_add_date.eq(date), AllBillInfoDao.Properties.Al_pid.eq(user.getU_id()));
            qb1.buildDelete().executeDeleteWithoutDetachingEntities();
            QueryBuilder<Bills> qb
                    = billsDao.queryBuilder().where(BillsDao.Properties.B_add_date.eq(date), BillsDao.Properties.B_pid.eq(user.getU_id()));
            qb.buildDelete().executeDeleteWithoutDetachingEntities();
        }else if (allInfo.getA_diarys()==1){
            diarysDao = daoSession.getDiarysDao();
            QueryBuilder<Diarys> qb
                    = diarysDao.queryBuilder().where(DiarysDao.Properties.D_add_date.eq(date),DiarysDao.Properties.D_pid.eq(user.getU_id()));
            qb.buildDelete().executeDeleteWithoutDetachingEntities();
        }
        allInfoList.remove(allInfo);
        allInfoDao.delete(allInfo);
    }
    //删除数据dialog
    private void delDialog(final AllInfo allInfo,final String date){
        int color = Color.parseColor("#546e7a");
        new MaterialDialog.Builder(this)
                .content("删除" + date + "下的所有记录？")
                .positiveText("删除")
                .titleColor(color)
                .positiveColor(color)
                .negativeText("取消")
                .negativeColor(color)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if ("POSITIVE".equals(which.toString())) {
                            delInfo(allInfo, date);
                            toast("删除成功");
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        history_recycleview.removeOnItemTouchListener(onTouchListener);
    }

    //根据时间读取便签
    private List<Notes> loadNoteByDate(String date){
        List<Notes> list = new ArrayList<>();
        QueryBuilder builder = notesDao.queryBuilder();
        builder.where(NotesDao.Properties.N_add_date.eq(date), NotesDao.Properties.N_pid.eq(user.getU_id())).build();
        list = builder.list();
        Log.i("---", "数据库读取notes:" + list.size());
        return list;
    }
    //根据用户名读取用户
    private List<User> loadUserByName(String name){
        List<User> list = new ArrayList<>();
        QueryBuilder builder = userDao.queryBuilder();
        builder.where(UserDao.Properties.U_name.eq(name)).orderDesc(UserDao.Properties.U_id).build();
        list = builder.list();
        Log.i("---", "数据库读取user:" + list.size());
        return list;
    }
    //根据时间读取名片
    private List<AllCardInfo> loadCardByDate(String date){
        List<AllCardInfo> list = new ArrayList<>();
        QueryBuilder builder = allCardInfoDao.queryBuilder();
        builder.where(AllCardInfoDao.Properties.Ac_add_date.eq(date),AllCardInfoDao.Properties.Ac_pid.eq(user.getU_id())).build();
        list = builder.list();
        return list;
    }
    //根据时间读取账单
    private List<AllBillInfo> loadBillByDate(String date){
        List<AllBillInfo> list = new ArrayList<>();
        QueryBuilder builder = allBillInfoDao.queryBuilder();
        builder.where(AllBillInfoDao.Properties.Al_add_date.eq(date),AllBillInfoDao.Properties.Al_pid.eq(user.getU_id())).build();
        list = builder.list();
        return list;
    }

    //根据id读取单条账单
    private List<Bills> loadOneBillByDate(String name){
        List<Bills> list = new ArrayList<>();
        QueryBuilder builder = billsDao.queryBuilder();
        builder.where(BillsDao.Properties.B_name.eq(name)).build();
        list = builder.list();
        return list;
    }
    //根据id读取单条company卡片
    private List<CompanyCards> loadOneCompanyByDate(String name){
        List<CompanyCards> list = new ArrayList<>();
        QueryBuilder builder = companyCardsDao.queryBuilder();
        builder.where(CompanyCardsDao.Properties.C_name.eq(name)).build();
        list = builder.list();
        return list;
    }
    //根据name读取单条place卡片
    private List<PlaceCards> loadOnePlaceByDate(String name){
        List<PlaceCards> list = new ArrayList<>();
        QueryBuilder builder = placeCardsDao.queryBuilder();
        builder.where(PlaceCardsDao.Properties.S_name.eq(name),PlaceCardsDao.Properties.S_pid.eq(user.getU_id())).build();
        list = builder.list();
        return list;
    }
    //根据id读取单条person卡片
    private List<PersonCards> loadOnePersonByDate(String name){
        List<PersonCards> list = new ArrayList<>();
        QueryBuilder builder = personCardsDao.queryBuilder();
        builder.where(PersonCardsDao.Properties.P_name.eq(name),PersonCardsDao.Properties.P_pid.eq(user.getU_id())).build();
        list = builder.list();
        return list;
    }
    private Context context;
    //展示当天数据和布局
    private void showTodayDate(){
        context = this;
        int a = 0;
        no_list.setVisibility(View.GONE);
        note_lay.setVisibility(View.GONE);
        card_lay.setVisibility(View.GONE);
        bill_lay.setVisibility(View.GONE);

        today_cards.setVisibility(View.GONE);

        int card_size = allCardInfoList.size();
        int bill_size = allBillInfoList.size();
        if (card_size!=0){
            a++;
            card_lay.setVisibility(View.VISIBLE);
            //首页布局美化。根据条数加载不同适配器,以后优化
//            if(card_size==1){
//                today_cards_one.setVisibility(View.VISIBLE);
//                MyBaseAdapter adapter = new MyBaseAdapter();
//            }else if (card_size==2){
//                today_cards_two.setVisibility(View.VISIBLE);
//            }else {
//                today_cards.setVisibility(View.VISIBLE);
//                adapter = new MyCardAdapter(this,allCardInfoList);
//                today_cards.setAdapter(adapter);
//            }
            today_cards.setVisibility(View.VISIBLE);
            adapter = new MyCardAdapter(this,allCardInfoList);
            today_cards.setAdapter(adapter);
            today_cards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //卡片监听
                    cardItemDialog(position);
                }
            });
        }
        if (bill_size!=0){
            a++;
            bill_lay.setVisibility(View.VISIBLE);
            adapter1 = new MyBillAdapter(this,allBillInfoList);
            today_bills.setAdapter(adapter1);
            today_bills.setOnItemClickListener(this);
        }
        if (notesList.size()!=0){
            a++;
            note_lay.setVisibility(View.VISIBLE);
            Log.i("---", "notesList:" + notesList.size());
            adapter2 = new MyNotesAdapter(this,notesList);
            notes_listview.setAdapter(adapter2);
            notes_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("noteFromFragment", notesList.get(position));
                    Intent intent = new Intent(MainActivity.this,AddNoteActivity.class);
                    intent.putExtra("fragmentNote", bundle);
                    startActivityForResult(intent,3);
                }
            });
        }
        if (a==0){
            no_list.setVisibility(View.VISIBLE);
        }
    }

    //输出方法
    private void toast(String str){
        Toast.makeText(getBaseContext(),str, Toast.LENGTH_SHORT).show();
    }
    //获取今日日期
    public String getToday_date(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        today_date = format.format(curDate);
        MyApp.today_date = today_date;
        Log.i("---", "获取当前日期：" + today_date);
        return today_date;
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

    //状态栏和toolbar
    private void initBarAndStatus(){
        zoom_img = (ImageView) zoomView.findViewById(R.id.zoom_img);
        StatusBarUtil.setColor(this, Color.parseColor("#607d8b"), 0);
    }
    //初始化PullToZoomScrollViewEx
    private void loadViewForCode() {
        scrollView = (PullToZoomScrollViewEx) findViewById(R.id.scroll_view);
        headView = LayoutInflater.from(this).inflate(R.layout.profile_head_view, null, false);
        zoomView = LayoutInflater.from(this).inflate(R.layout.profile_zoom_view, null, false);
        contentView = LayoutInflater.from(this).inflate(R.layout.profile_content_view, null, false);
        scrollView.setHeaderView(headView);
        scrollView.setZoomView(zoomView);
        scrollView.setScrollContentView(contentView);
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));
        scrollView.setHeaderLayoutParams(localObject);
        //handler.sendEmptyMessage(1);
        Log.i("---", "loadView");
    }
    //toolbar的背景模糊
    private void applyBlur() {
        zoom_img.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                zoom_img.getViewTreeObserver().removeOnPreDrawListener(this);
                zoom_img.buildDrawingCache();

                Bitmap bmp = zoom_img.getDrawingCache();
                blur(bmp, toolbar);
                return true;
            }
        });
    }
    //zoom背景模糊
    private void zoomApplyBlur() {
        zoom_img.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                zoom_img.getViewTreeObserver().removeOnPreDrawListener(this);
                zoom_img.buildDrawingCache();

                Bitmap bmp = zoom_img.getDrawingCache();
                blur(bmp, headView);
                return true;
            }
        });
    }

    //图片模糊
    private void blur(Bitmap bkg, View view) {
        float scaleFactor = 6;
        float radius = 12;
        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth()/scaleFactor),
                (int) (view.getMeasuredHeight()/scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);
        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        view.setBackground(new BitmapDrawable(getResources(), overlay));
    }
    //初始化侧边栏
    private MyBaseAdapter myBaseAdapter;
    private List list;
    private void sildMenu(){
        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT);
        final View view = LayoutInflater.from(this).inflate(R.layout.sliding_menu, null);
        final ListView listView = (ListView) view.findViewById(R.id.slid_menu_list);
        slidingMenu.setMenu(view);
        slidingMenu.setShadowWidthRes(R.dimen.shadow);
        slidingMenu.setBehindOffsetRes(R.dimen.behind_offset);
        slidingMenu.setFadeDegree(0.35f);
        //获取屏幕高宽
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        final int windowsHeight = metric.heightPixels;
        slidingMenu.setBehindWidth(windowsHeight / 5);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        slidingMenu.setOnOpenListener(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {
                list = new ArrayList();
                list.add(windowsHeight);
                list.add(windowsHeight);
                list.add(windowsHeight);
                list.add(windowsHeight);
                list.add(windowsHeight);
                myBaseAdapter = new MyBaseAdapter(getBaseContext(), list);
                listView.setAdapter(myBaseAdapter);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                if (position==0){
                    intent = new Intent(MainActivity.this,AllNotesActivity.class);
                }else if (position==1){
                    intent = new Intent(MainActivity.this,AllCardActivity.class);
                }else if (position==2){
                    intent = new Intent(MainActivity.this,AllBillActivity.class);
                }else if (position==3){
                    intent = new Intent(MainActivity.this,AllDiaryActivity.class);
                }else if (position==4){
                    intent = new Intent(MainActivity.this,SettingActivity.class);
                }
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("---", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("---", "onDestroy");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.menu1:
                Intent add_note = new Intent(MainActivity.this,AddNoteActivity.class);
                startActivity(add_note);
                break;
            case R.id.menu2:
                Intent add_card = new Intent(MainActivity.this,AddCardActivity.class);
                startActivity(add_card);
                break;
            case R.id.menu3:
                Intent add_bill = new Intent(MainActivity.this,AddBillActivity.class);
                startActivity(add_bill);
                break;
            case R.id.today_more:
                AllInfo allInfo = MyApp.today;
                if (allInfo==null){
                    toast("为空，添加点内容吧");
                    return;
                }
                Intent intent = new Intent(getBaseContext(),OneDayAllActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("info",allInfo);
                intent.putExtra("info_item",bundle);
                startActivity(intent);
                break;
            case R.id.view:
                slidingMenu.toggle();
                break;
            case R.id.username:
                changeUsername();
                break;
            case R.id.sex_img:
                changeSex();
                break;
            case R.id.head_img:
                changeHeadImg();
                break;
        }
    }



    //dialog
    private MaterialDialog dialog = null;
    private View positiveAction;
    //用户名的修改dialog
    private String change_name = "";
    private EditText change_u;
    private File file;
    private void changeHeadImg(){
        dialog = new MaterialDialog.Builder(this)
                .title(" ")
                .titleColor(Color.parseColor("#546e7a"))
                .customView(R.layout.change_headimg_dialog, true)
                .positiveColor(Color.parseColor("#546e7a"))
                .negativeText(android.R.string.cancel)
                .negativeColor(Color.parseColor("#546e7a"))
                .build();
        View dialog_view = dialog.getCustomView();
        View camare = dialog_view.findViewById(R.id.camare);
        View photos = dialog_view.findViewById(R.id.photos);
        camare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //创建文件路径
                file = FileUitlity.getInstance(getApplicationContext()).makeDir("head_image");
                //定义图片路径和名称
                path = file.getParent() + File.separatorChar + System.currentTimeMillis() + ".jpg";
                //保存图片
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path)));
                //图片质量
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                startActivityForResult(intent, 1);
            }
        });
        photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                file = FileUitlity.getInstance(getApplicationContext()).makeDir("head_image");
                //调用手机相册
                allPhoto();
            }
        });
        dialog.show();
    }
    //调用手机相册
    private void allPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }
    private void changeSex(){
        dialog = new MaterialDialog.Builder(this)
                .iconRes(R.mipmap.head_img)
                .limitIconToDefaultSize()
                .title("性别")
                .titleColor(Color.parseColor("#546e7a"))
                .customView(R.layout.change_sex_dialog, true)
                .positiveText("确定")
                .positiveColor(Color.parseColor("#546e7a"))
                .negativeText(android.R.string.cancel)
                .negativeColor(Color.parseColor("#546e7a"))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (boy.isChecked()){
                            user.setU_sex("男");
                            sex_img.setImageResource(R.mipmap.boy);
                        }else{
                            user.setU_sex("女");
                            sex_img.setImageResource(R.mipmap.gril);
                        }
                        userDao.update(user);
                    }
                })
                .build();
        View dialog_view = dialog.getCustomView();
        boy = (RadioButton) dialog_view.findViewById(R.id.boy);
        gril = (RadioButton) dialog_view.findViewById(R.id.gril);
        String sex = user.getU_sex();
        if ("男".equals(sex)){
            boy.setChecked(true);
        }else {
            gril.setChecked(true);
        }
        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        dialog.show();
    }
    private void changeUsername(){
        dialog = new MaterialDialog.Builder(this)
                .iconRes(R.mipmap.head_img)
                .limitIconToDefaultSize()
                .title("昵称")
                .titleColor(Color.parseColor("#546e7a"))
                .customView(R.layout.change_username_dialog, true)
                .positiveText("确定")
                .positiveColor(Color.parseColor("#546e7a"))
                .negativeText(android.R.string.cancel)
                .negativeColor(Color.parseColor("#546e7a"))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        change_name = change_u.getText().toString().trim();
                        userName.setText(change_name);
                        user.setU_name(change_name);
                        userDao.update(user);
                    }
                })
                .build();
        View dialog_view = dialog.getCustomView();
        change_u = (EditText) dialog_view.findViewById(R.id.change_name);
        change_u.setText(user.getU_name());
        change_u.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        dialog.show();
    }
    //卡片
    private RadioButton boy,gril;
    private ImageView call_item1,call_item2;
    private EditText item_place_name,item_place_address,
            item_person_name,item_person_tell,item_person_address,
            item_company_name,item_company_address,item_company_tell,item_company_email;
    private void cardItemDialog(int position){
        final AllCardInfo this_card = allCardInfoList.get(position);
        int type = this_card.getAc_type(); //1---place  2---person  3--company
        final String[] name = {""};
        final String[] address = {""};
        if (type==1){
            final PlaceCards placeCards = loadOnePlaceByDate(this_card.getAc_card_name()).get(0);
            dialog = new MaterialDialog.Builder(this)
                    .iconRes(R.mipmap.item_place)
                    .limitIconToDefaultSize()
                    .title("地点卡片")
                    .titleColor(Color.parseColor("#546e7a"))
                    .customView(R.layout.dailog_placecard_custview, true)
                    .positiveText("确定")
                    .positiveColor(Color.parseColor("#546e7a"))
                    .negativeText(android.R.string.cancel)
                    .negativeColor(Color.parseColor("#546e7a"))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            name[0] = item_place_name.getText().toString().trim();
                            address[0] = item_place_address.getText().toString();
                            placeCards.setS_name(name[0]);
                            placeCards.setS_address(address[0]);
                            this_card.setAc_card_name(name[0]);
                            allCardInfoDao.update(this_card);
                            placeCardsDao.update(placeCards);
                            adapter.notifyDataSetChanged();
                            if (mAdapter!=null){
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    })
                    .build();
            View dialog_view = dialog.getCustomView();
            item_place_name = (EditText) dialog_view.findViewById(R.id.item_place_name);
            item_place_address = (EditText) dialog_view.findViewById(R.id.item_place_address);
            item_place_name.setText(this_card.getAc_card_name());
            item_place_address.setText(placeCards.getS_address());
            item_place_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    positiveAction.setEnabled(s.toString().trim().length() > 0);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

        }else if (type==2){
            final PersonCards personCards = loadOnePersonByDate(this_card.getAc_card_name()).get(0);
            final String[] pname = {""};
            final String[] paddress = {""};
            final String[] psex = {""};
            final String[] ptell = {""};
            dialog = new MaterialDialog.Builder(this)
                    .iconRes(R.mipmap.item_person)
                    .limitIconToDefaultSize()
                    .title("人物卡片")
                    .titleColor(Color.parseColor("#546e7a"))
                    .customView(R.layout.dailog_personcard_custview, true)
                    .positiveText("确定")
                    .positiveColor(Color.parseColor("#546e7a"))
                    .negativeText(android.R.string.cancel)
                    .negativeColor(Color.parseColor("#546e7a"))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            pname[0] = item_person_name.getText().toString().trim();
                            ptell[0] = item_person_tell.getText().toString().trim();
                            paddress[0] = item_person_address.getText().toString().trim();
                            if (boy.isChecked()){
                                psex[0] = "男";
                            }else{
                                psex[0] = "女";
                            }
                            personCards.setP_name(pname[0]);
                            personCards.setP_sex(psex[0]);
                            personCards.setP_adress(paddress[0]);
                            personCards.setP_tell(ptell[0]);
                            this_card.setAc_card_name(pname[0]);
                            personCardsDao.update(personCards);
                            allCardInfoDao.update(this_card);
                            adapter.notifyDataSetChanged();
                            if (mAdapter!=null){
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    })
                    .build();
            View dialog_view = dialog.getCustomView();
            boy = (RadioButton) dialog_view.findViewById(R.id.boy);
            gril = (RadioButton) dialog_view.findViewById(R.id.gril);
            item_person_name = (EditText) dialog_view.findViewById(R.id.item_person_name);
            item_person_tell = (EditText) dialog_view.findViewById(R.id.item_person_tell);
            item_person_address = (EditText) dialog_view.findViewById(R.id.item_person_address);
            call_item2 = (ImageView) dialog_view.findViewById(R.id.call_item);
            item_person_name.setText(personCards.getP_name());
            if ("男".equals(personCards.getP_sex())){
                boy.setChecked(true);
            }else {
                gril.setChecked(true);
            }
            item_person_tell.setText(personCards.getP_tell());
            item_person_address.setText(personCards.getP_adress());
            item_person_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    positiveAction.setEnabled(s.toString().trim().length() > 0);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            call_item2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = personCards.getP_tell();
                    if (!"".equals(phone)){
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_CALL);
                        //url:统一资源定位符
                        //uri:统一资源标示符（更广）
                        intent.setData(Uri.parse("tel:" + phone));
                        //开启系统拨号器
                        startActivity(intent);
                    }else {
                        toast("没有号码");
                    }
                }
            });
        }else {
            final CompanyCards companyCards = loadOneCompanyByDate(this_card.getAc_card_name()).get(0);
            final String[] cname = {""};
            final String[] caddress = {""};
            final String[] ctell = {""};
            final String[] cemail = {""};
            dialog = new MaterialDialog.Builder(this)
                    .iconRes(R.mipmap.item_company)
                    .limitIconToDefaultSize()
                    .title("公司卡片")
                    .titleColor(Color.parseColor("#546e7a"))
                    .customView(R.layout.dailog_companycard_custview, true)
                    .positiveText("确定")
                    .positiveColor(Color.parseColor("#546e7a"))
                    .negativeText(android.R.string.cancel)
                    .negativeColor(Color.parseColor("#546e7a"))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            cname[0] = item_company_name.getText().toString().trim();
                            caddress[0] = item_company_address.getText().toString();
                            ctell[0] = item_company_tell.getText().toString();
                            cemail[0] = item_company_email.getText().toString();
                            companyCards.setC_name(cname[0]);
                            companyCards.setC_address(caddress[0]);
                            companyCards.setC_tell(ctell[0]);
                            companyCards.setC_email(cemail[0]);
                            companyCardsDao.update(companyCards);
                            this_card.setAc_card_name(cname[0]);
                            allCardInfoDao.update(this_card);
                            adapter.notifyDataSetChanged();
                            if (mAdapter!=null){
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    })
                    .build();
            View dialog_view = dialog.getCustomView();
            item_company_name = (EditText) dialog_view.findViewById(R.id.item_company_name);
            item_company_address = (EditText) dialog_view.findViewById(R.id.item_company_address);
            item_company_tell = (EditText) dialog_view.findViewById(R.id.item_company_tell);
            item_company_email = (EditText) dialog_view.findViewById(R.id.item_company_email);
            call_item1 = (ImageView) dialog_view.findViewById(R.id.call_item);
            item_company_name.setText(companyCards.getC_name());
            item_company_address.setText(companyCards.getC_address());
            item_company_tell.setText(companyCards.getC_tell());
            item_company_email.setText(companyCards.getC_email());
            item_company_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    positiveAction.setEnabled(s.toString().trim().length() > 0);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            call_item1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = companyCards.getC_tell();
                    if (!"".equals(phone)){
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_CALL);
                        //url:统一资源定位符
                        //uri:统一资源标示符（更广）
                        intent.setData(Uri.parse("tel:" + phone));
                        //开启系统拨号器
                        startActivity(intent);
                    }else {
                        toast("没有号码");
                    }
                }
            });
        }
        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        dialog.show();
    }
    //账单
    private EditText item_bill_num,item_bill_name;
    private RadioButton in,out;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final AllBillInfo this_bill = allBillInfoList.get(position);
        final Bills bills = loadOneBillByDate(this_bill.getAl_bill_name()).get(0);
        final String[] num = {""};
        final String[] name = {""};
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .iconRes(R.mipmap.bill_small)
                .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                .title("账目卡片")
                .titleColor(Color.parseColor("#546e7a"))
                .customView(R.layout.dailog_bill_custview, true)
                .positiveText("确定")
                .positiveColor(Color.parseColor("#546e7a"))
                .negativeText(android.R.string.cancel)
                .negativeColor(Color.parseColor("#546e7a"))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        num[0] = item_bill_num.getText().toString().trim();
                        name[0] = item_bill_name.getText().toString().trim();
                        if (in.isChecked()) {
                            this_bill.setAl_bill_type(1);
                            bills.setB_type("1");
                        } else {
                            this_bill.setAl_bill_type(2);
                            bills.setB_type("2");
                        }
                        this_bill.setAl_bill_name(name[0]);
                        this_bill.setAl_bill_num(num[0]);
                        bills.setB_num(num[0]);
                        bills.setB_name(name[0]);
                        allBillInfoDao.update(this_bill);
                        billsDao.update(bills);
                        adapter1.notifyDataSetChanged();
                        if (mAdapter!=null){
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }).build();
        View dialog_view = dialog.getCustomView();
        item_bill_num = (EditText) dialog_view.findViewById(R.id.item_bill_num);
        item_bill_name = (EditText) dialog_view.findViewById(R.id.item_bill_name);
        in = (RadioButton) dialog_view.findViewById(R.id.in);
        out = (RadioButton) dialog_view.findViewById(R.id.out);

        item_bill_name.setText(this_bill.getAl_bill_name());
        item_bill_num.setText(this_bill.getAl_bill_num());
        int it_type = this_bill.getAl_bill_type();
        if (it_type==1){
            in.setChecked(true);
        }else {
            out.setChecked(true);
        }
        item_bill_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        item_bill_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        dialog.show();
    }

    private OnActivityTouchListener touchListener;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (touchListener != null) touchListener.getTouchCoordinates(ev);
        return super.dispatchTouchEvent(ev);
    }
    @Override
    public void setOnActivityTouchListener(OnActivityTouchListener listener) {
        this.touchListener = listener;
    }


    private class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
        LayoutInflater inflater;
        List<AllInfo> modelList;

        public MainAdapter(Context context, List<AllInfo> list) {
            inflater = LayoutInflater.from(context);
            modelList = list;
        }

        @Override
        public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.history_item_card, parent, false);
            return new MainViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MainViewHolder holder, int position) {
            holder.bindData(modelList.get(position));
        }

        @Override
        public int getItemCount() {
            return modelList.size();
        }

        class MainViewHolder extends RecyclerView.ViewHolder {

            TextView day, month;
            View note,card,bill,diary,no;

            public MainViewHolder(View itemView) {
                super(itemView);
                day = (TextView) itemView.findViewById(R.id.history_date);
                month = (TextView) itemView.findViewById(R.id.history_month);
                note = itemView.findViewById(R.id.history_note);
                card = itemView.findViewById(R.id.history_card);
                bill = itemView.findViewById(R.id.history_bill);
                diary = itemView.findViewById(R.id.history_diary);
                no = itemView.findViewById(R.id.no_anything);
            }

            public void bindData(AllInfo rowModel) {
                String dateStr = rowModel.getA_add_date();
                day.setText(dateStr.substring(8));
                month.setText(dateStr.substring(0, 7));
                int b = 0;
                if (rowModel.getA_notes()==1){
                    note.setVisibility(View.VISIBLE);
                    b++;
                }
                if (rowModel.getA_cards()==1){
                    card.setVisibility(View.VISIBLE);
                    b++;
                }
                if (rowModel.getA_bills()==1){
                    bill.setVisibility(View.VISIBLE);
                    b++;
                }
                if (rowModel.getA_diarys()==1){
                    diary.setVisibility(View.VISIBLE);
                    b++;
                }
                if (b==0){
                    no.setVisibility(View.VISIBLE);
                }
            }
        }
    }
    long firstTime = 0;
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            if (slidingMenu.isMenuShowing()){
                slidingMenu.toggle();
                return true;
            }
            long secondTime = System.currentTimeMillis();
            if (secondTime-firstTime>2000){
                Toast.makeText(this,"再按一次退出", Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
                return true;
            }else {
                myApp.onTerminate();
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    private String path = "";
    private String img_name = "";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //如果返回码不为-1，则表示不成功
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if (resultCode==2){
            notesList = loadNoteByDate(today_date);
            adapter2.notifyDataSetChanged();
        }
        if (requestCode == 0){
            //调用相册
            Cursor cursor = this.getContentResolver().query(data.getData(),
                    new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            cursor.moveToFirst();
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            img_name = path.substring(path.lastIndexOf(File.separatorChar) + 1);
            cursor.close();
            startPhoneZoom(Uri.fromFile(new File(path)));
        }else if (requestCode == 1){
            //相机返回结果，调用系统裁剪
            startPhoneZoom(Uri.fromFile(new File(path)));
        }else if(requestCode == 2) {
            //设置裁剪返回的位图
            Bundle bundle = data.getExtras();
            if (bundle!=null){
                Bitmap bitmap = bundle.getParcelable("data");
                if (!"".equals(img_name)){
                    saveBitmap(bitmap, img_name);
                    img_name = "";
                }
                user.setU_img(final_imgurl);
                head_img.setImageBitmap(bitmap);
                userDao.update(user);
                final_imgurl = "";
            }
        }
    }

    //调用系统裁剪
    private String final_imgurl = "";
    private void startPhoneZoom(Uri uri){
        final_imgurl = uri.getPath();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //是否可裁剪
        intent.putExtra("corp", "true");
        //高宽比
        intent.putExtra("aspectY",1);
        intent.putExtra("aspectX",1);
        //设置裁剪框高宽
        intent.putExtra("outputX",150);
        intent.putExtra("outputY", 150);
        //返回数据
        intent.putExtra("return-data", true);
        startActivityForResult(intent,2);
    }

    /** 保存bitmap */
    public void saveBitmap(Bitmap bm, String name) {
        File f = new File(file.getParent() + File.separatorChar ,name);
        Log.i("---", f.toString());
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 加载本地图片
     * @param url
     * @return
     */
    public Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
