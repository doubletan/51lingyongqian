package com.example.apple.lingyongqian.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.adapter.MyViewPager;
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
import com.example.apple.lingyongqian.utils.FullyLinearLayoutManager;
import com.example.apple.lingyongqian.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class AllCardActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView add,back;
    private TextView toolbar_title;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private List<View> viewList = new ArrayList<>();//viewpager的list
    private LayoutInflater inflater;
    private View companys,persons,places;//viewpager的页面

    private MyApp myApp;
    private User user;
    private DaoSession daoSession;
    private CompanyCardsDao companyCardsDao;
    private PersonCardsDao personCardsDao;
    private PlaceCardsDao placeCardsDao;
    private AllInfoDao allInfoDao;
    private AllInfo info;
    private AllCardInfoDao cardInfodao;
    private List<CompanyCards> companyCardsList;
    private List<PlaceCards> placeCardsList;
    private List<PersonCards> personCardsList;

    private Context context;
    private String date = "";//日期

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_card);
        StatusBarUtil.setColor(this, Color.parseColor("#607d8b"), 0);
        user = MyApp.user1;
        initView();
    }
    @Override
    public void onStart() {
        super.onStart();
        StatusBarUtil.setColor(this, Color.parseColor("#607d8b"), 0);
    }

    private void initView(){

        myApp = MyApp.getApp();
        myApp.addToList(this);
        context = this;
        date = MyApp.today_date;
        daoSession = myApp.getDaoSession(this);
        personCardsDao = daoSession.getPersonCardsDao();
        companyCardsDao = daoSession.getCompanyCardsDao();
        placeCardsDao = daoSession.getPlaceCardsDao();

        inflater = LayoutInflater.from(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        toolbar_title = (TextView) findViewById(R.id.date);
        back = (ImageView) findViewById(R.id.back);
        add = (ImageView) findViewById(R.id.add_day);

        toolbar_title.setText("所有卡片");

        back.setOnClickListener(this);
        add.setOnClickListener(this);

        showCards();
    }

    //判断list是否为空
    private void checkList(int size,RecyclerView recyclerView,View no_content){
        if (size==0){
            recyclerView.setVisibility(View.GONE);
            no_content.setVisibility(View.VISIBLE);
        }else {
            recyclerView.setVisibility(View.VISIBLE);
            no_content.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.add_day:
                //添加该日期下的卡片
                Intent intent = new Intent(AllCardActivity.this, AddCardActivity.class);
                intent.putExtra("fragmentAddCard", date);
                startActivityForResult(intent, 1);
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==0){
            loadPersons();
            //Log.i("---","PersonCards size :"+temp.size());
            have_num1.setText("有 " + this.personCardsList.size() + " 个联系人");
            recyclerView1.setVisibility(View.VISIBLE);
            no_content1.setVisibility(View.GONE);
            myPersonAdapter.reflesh(this.personCardsList);
        }else if (resultCode==1){
            loadPlaces();
            have_num2.setText("有 " + this.placeCardsList.size() + " 个地点");
            recyclerView2.setVisibility(View.VISIBLE);
            no_content2.setVisibility(View.GONE);
            myPlaceAdapter.reflesh(this.placeCardsList);
        }else if (resultCode==4){
            loadPersons();
            //Log.i("---","PersonCards size :"+temp.size());
            have_num1.setText("有 " + this.personCardsList.size() + " 个联系人");
            recyclerView1.setVisibility(View.VISIBLE);
            no_content1.setVisibility(View.GONE);
            myPersonAdapter.reflesh(this.personCardsList);
        }else if (resultCode==5){
            loadCompanys();
            have_num3.setText("有 " + this.companyCardsList.size() + " 个公司");
            recyclerView3.setVisibility(View.VISIBLE);
            no_content3.setVisibility(View.GONE);
            myComapnyAdapter.reflesh(this.companyCardsList);
        }else if (resultCode==6){
            loadPlaces();
            have_num2.setText("有 " + this.placeCardsList.size() + " 个地点");
            recyclerView2.setVisibility(View.VISIBLE);
            no_content2.setVisibility(View.GONE);
            myPlaceAdapter.reflesh(this.placeCardsList);
        }else {
            loadCompanys();
            have_num3.setText("有 " + this.companyCardsList.size() + " 个公司");
            recyclerView3.setVisibility(View.VISIBLE);
            no_content3.setVisibility(View.GONE);
            myComapnyAdapter.reflesh(this.companyCardsList);
        }
        myViewPager.notifyDataSetChanged();
    }

    private MyViewPager myViewPager;
    private void showCards(){
        loadPersons();
        loadCompanys();
        loadPlaces();

        viewList.add(loadPersonView());
        viewList.add(loadCompanyView());
        viewList.add(loadPlaceView());

        //Log.i("---", "viewList:" + viewList.size());

        myViewPager = new MyViewPager(viewList);
        viewPager.setAdapter(myViewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(myViewPager);

    }

    //根据时间读取公司
    private void loadCompanys() {
        QueryBuilder builder = companyCardsDao.queryBuilder();
        companyCardsList = builder.where(CompanyCardsDao.Properties.C_pid.eq(user.getU_id())).orderDesc(CompanyCardsDao.Properties.C_id).list();
    }
    //根据时间读取地点
    private void loadPlaces() {
        QueryBuilder builder = placeCardsDao.queryBuilder();
        placeCardsList = builder.where(PlaceCardsDao.Properties.S_pid.eq(user.getU_id())).orderDesc(PlaceCardsDao.Properties.S_id).list();
    }
    //根据时间读取人物
    private void loadPersons() {
        QueryBuilder builder = personCardsDao.queryBuilder();
        personCardsList = builder.where(PersonCardsDao.Properties.P_pid.eq(user.getU_id())).orderDesc(PersonCardsDao.Properties.P_id).list();
    }

    //初始化布局 companys,persons,places
    private MyComapnyAdapter myComapnyAdapter;
    private MyPersonAdapter myPersonAdapter;
    private MyPlaceAdapter myPlaceAdapter;
    private RecyclerView recyclerView1,recyclerView2,recyclerView3;
    private View view,no_content1,no_content2,no_content3;
    private TextView have_num1,have_num2,have_num3;
    private int size;
    private View loadPersonView(){
        persons = inflater.inflate(R.layout.all_card_layout, null);
        recyclerView1 = (RecyclerView) persons.findViewById(R.id.recyclerView);
        have_num1 = (TextView) persons.findViewById(R.id.have_num);
        no_content1 = persons.findViewById(R.id.no_contents);
        size = this.personCardsList.size();
        checkList(size, recyclerView1, no_content1);
        have_num1.setText("有 " + size + " 个联系人");
        myPersonAdapter = new MyPersonAdapter(this.personCardsList);
        FullyLinearLayoutManager fullyLinearLayoutManager = new FullyLinearLayoutManager(this);
        fullyLinearLayoutManager.setOrientation(FullyLinearLayoutManager.VERTICAL);
        recyclerView1.setLayoutManager(fullyLinearLayoutManager);
        recyclerView1.setAdapter(myPersonAdapter);
        return persons;
    }

    private View loadPlaceView(){
        places = inflater.inflate(R.layout.all_card_layout, null);
        recyclerView2 = (RecyclerView) places.findViewById(R.id.recyclerView);
        have_num2 = (TextView) places.findViewById(R.id.have_num);
        no_content2 = places.findViewById(R.id.no_contents);
        size = this.placeCardsList.size();
        checkList(size, recyclerView2,no_content2);
        have_num2.setText("有 " + size + " 个地点");
        myPlaceAdapter = new MyPlaceAdapter(this.placeCardsList);
        FullyLinearLayoutManager fullyLinearLayoutManager = new FullyLinearLayoutManager(this);
        fullyLinearLayoutManager.setOrientation(FullyLinearLayoutManager.VERTICAL);
        recyclerView2.setLayoutManager(fullyLinearLayoutManager);
        recyclerView2.setAdapter(myPlaceAdapter);
        return places;
    }

    private View loadCompanyView(){
        companys = inflater.inflate(R.layout.all_card_layout, null);
        recyclerView3 = (RecyclerView) companys.findViewById(R.id.recyclerView);
        have_num3 = (TextView) companys.findViewById(R.id.have_num);
        no_content3 = companys.findViewById(R.id.no_contents);
        size = this.companyCardsList.size();
        checkList(size,recyclerView3,no_content3);
        have_num3.setText("有 "+size+" 个公司");
        myComapnyAdapter = new MyComapnyAdapter(this.companyCardsList);
        FullyLinearLayoutManager fullyLinearLayoutManager = new FullyLinearLayoutManager(this);
        fullyLinearLayoutManager.setOrientation(FullyLinearLayoutManager.VERTICAL);
        recyclerView3.setLayoutManager(fullyLinearLayoutManager);
        recyclerView3.setAdapter(myComapnyAdapter);
        return companys;
    }

    private int[] bg_color = {
            R.drawable.all_card_item_bg5,R.drawable.all_card_item_bg1,
            R.drawable.all_card_item_bg2,R.drawable.all_card_item_bg3,R.drawable.all_card_item_bg4};
    /**
     * person
     */
    class ViewHolder1 extends RecyclerView.ViewHolder{
        TextView item_name,item_tell,simple_name;
        ImageView more;
        View take_con;

        public ViewHolder1(View itemView) {
            super(itemView);
            item_name= (TextView) itemView.findViewById(R.id.item_name);
            item_tell= (TextView) itemView.findViewById(R.id.item_tell);
            simple_name= (TextView) itemView.findViewById(R.id.simple_name);
            more = (ImageView) itemView.findViewById(R.id.more);
            take_con = itemView.findViewById(R.id.take_con);
        }
    }
    class MyPersonAdapter extends  RecyclerView.Adapter<ViewHolder1>{

        private List<PersonCards> list;

        public MyPersonAdapter(List<PersonCards> list) {
            if (list==null){
                throw new IllegalArgumentException("list must not be null");
            }
            this.list = list;
        }

        @Override
        public ViewHolder1 onCreateViewHolder(ViewGroup viewGroup, int i) {
            //加载item子视图生成View对象
            View view = inflater.inflate(R.layout.all_card_item, viewGroup, false);
            //根据子视图创建ViewHolder对象
            ViewHolder1 viewHolder = new ViewHolder1(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder1 viewHolder, int i) {
            final PersonCards personCards = this.list.get(i);
            String name = personCards.getP_name();
            String tell = personCards.getP_tell();
            viewHolder.item_name.setText(name);
            viewHolder.item_tell.setText(tell);
            String temp = name.substring(0, 1);
            if ("".equals(temp)){
                viewHolder.simple_name.setText("A");
            }else {
                viewHolder.simple_name.setText(temp);
            }
            //随机更换时间轴的样式
            int type = (int)(1+ Math.random()*(4-1+1));
            viewHolder.simple_name.setBackgroundResource(bg_color[type]);
            viewHolder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AllCardActivity.this,AllCardItemActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("person_item",personCards);
                    intent.putExtra("person",bundle);
                    startActivityForResult(intent, 5);
                }
            });
            viewHolder.take_con.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tell = personCards.getP_tell();
                    if ("".equals(tell)){
                        tell = "没有联系方式";
                    }
                    try{
                        showCallbacks(tell,personCards.getP_name());
                    }catch (Exception e){
                        Log.i("---", "dialog异常"+e.getMessage());
                    }
                }
            });
        }
        public void showCallbacks(final String tell, String name) {
            new MaterialDialog.Builder(context)
                    .title(name)
                    .content(tell)
                    .positiveText("立即拨打")
                    .positiveColor(Color.parseColor("#546e7a"))
                    .negativeText("发送短信")
                    .negativeColor(Color.parseColor("#546e7a"))
                    .neutralText("取消")
                    .neutralColor(Color.parseColor("#546e7a"))
                    .onAny(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if ("POSITIVE".equals(which.name())) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_CALL);
                                //url:统一资源定位符
                                //uri:统一资源标示符（更广）
                                intent.setData(Uri.parse("tel:" + tell));
                                //开启系统拨号器
                                startActivity(intent);
                            } else if ("NEGATIVE".equals(which.name())) {
                                Uri uri= Uri.parse("smsto:"+tell);
                                Intent it=new Intent(Intent.ACTION_SENDTO,uri);
                                it.putExtra("sms_body","");
                                //it.setType("vnd.android-dir/mns-sms");
                                startActivity(it);
                            } else {

                            }
                        }
                    })
                    .show();
        }
        @Override
        public int getItemCount() {
            return this.list.size();
        }

        public void reflesh(List<PersonCards> personCardsList){
            this.list.clear();
            this.list.addAll(personCardsList);
            this.notifyDataSetChanged();
        }
    }
    /**
     * place
     */
    class ViewHolder2 extends RecyclerView.ViewHolder{
        TextView item_name,item_tell,simple_name;
        ImageView more;
        View take_con;

        public ViewHolder2(View itemView) {
            super(itemView);
            item_name= (TextView) itemView.findViewById(R.id.item_name);
            item_tell= (TextView) itemView.findViewById(R.id.item_tell);
            simple_name= (TextView) itemView.findViewById(R.id.simple_name);
            more = (ImageView) itemView.findViewById(R.id.more);
            take_con = itemView.findViewById(R.id.take_con);
        }
    }
    class MyPlaceAdapter extends  RecyclerView.Adapter<ViewHolder2>{

        private List<PlaceCards> list;

        public MyPlaceAdapter(List<PlaceCards> list) {
            if (list==null){
                throw new IllegalArgumentException("list must not be null");
            }
            this.list = list;
        }

        @Override
        public ViewHolder2 onCreateViewHolder(ViewGroup viewGroup, int i) {
            //加载item子视图生成View对象
            View view = inflater.inflate(R.layout.all_card_item, viewGroup, false);
            //根据子视图创建ViewHolder对象
            ViewHolder2 viewHolder = new ViewHolder2(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder2 viewHolder, int i) {
            final PlaceCards placeCards = this.list.get(i);
            String name = placeCards.getS_name();
            String address = placeCards.getS_address();
            viewHolder.item_name.setText(name);
            viewHolder.item_tell.setText(address);
            String temp = name.substring(0, 1);
            if ("".equals(temp)){
                viewHolder.simple_name.setText("Auto");
            }else {
                viewHolder.simple_name.setText(temp);
            }
            //随机更换时间轴的样式
            int type = (int)(1+ Math.random()*(4-1+1));
            viewHolder.simple_name.setBackgroundResource(bg_color[type]);
            viewHolder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AllCardActivity.this, AllPlaceItemActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("place_item", placeCards);
                    intent.putExtra("place", bundle);
                    startActivityForResult(intent, 7);
                }
            });
            viewHolder.take_con.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String address = placeCards.getS_address();
                    if ("".equals(address)){
                        address = "没有详细地址";
                    }
                    try{
                        showCallbacks(address,placeCards.getS_name());
                    }catch (Exception e){
                        Log.i("---", "dialog异常"+e.getMessage());
                    }
                }
            });
        }
        public void showCallbacks(final String tell, String name) {
            new MaterialDialog.Builder(context)
                    .title(name)
                    .content(tell)
                    .positiveText("确定")
                    .positiveColor(Color.parseColor("#546e7a"))
                    .onAny(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if ("POSITIVE".equals(which.name())) {
                                dialog.dismiss();
                            }
                        }
                    })
                    .show();
        }
        @Override
        public int getItemCount() {
            return this.list.size();
        }
        public void reflesh(List<PlaceCards> placeCardsList){
            this.list.clear();
            this.list.addAll(placeCardsList);
            this.notifyDataSetChanged();
        }
    }
    /**
     * company
     */
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView item_name,item_tell,simple_name;
        ImageView more;
        View take_con;
        public ViewHolder(View itemView) {
            super(itemView);
            item_name= (TextView) itemView.findViewById(R.id.item_name);
            item_tell= (TextView) itemView.findViewById(R.id.item_tell);
            simple_name= (TextView) itemView.findViewById(R.id.simple_name);
            more = (ImageView) itemView.findViewById(R.id.more);
            take_con = itemView.findViewById(R.id.take_con);

        }
    }
    class MyComapnyAdapter extends  RecyclerView.Adapter<ViewHolder>{

        private List<CompanyCards> list;

        public MyComapnyAdapter(List<CompanyCards> list) {
            if (list==null){
                throw new IllegalArgumentException("list must not be null");
            }
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            //加载item子视图生成View对象
            View view = inflater.inflate(R.layout.all_card_item, viewGroup, false);
            //根据子视图创建ViewHolder对象
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int i) {
            final CompanyCards companyCards = this.list.get(i);
            String name = companyCards.getC_name();
            String tell = companyCards.getC_tell();
            viewHolder.item_name.setText(name);
            viewHolder.item_tell.setText(tell);
            String temp = name.substring(0, 1);
            if ("".equals(temp)){
                viewHolder.simple_name.setText("Auto");
            }else {
                viewHolder.simple_name.setText(temp);
            }
            //随机更换时间轴的样式
            int type = (int)(1+ Math.random()*(4-1+1));
            viewHolder.simple_name.setBackgroundResource(bg_color[type]);
            viewHolder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AllCardActivity.this, AllCompanyItemActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("company_item", companyCards);
                    intent.putExtra("company", bundle);
                    startActivityForResult(intent, 6);
                }
            });
            viewHolder.take_con.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tell = companyCards.getC_tell();
                    if ("".equals(tell)){
                        tell = "没有联系方式";
                    }
                    try{
                        showCallbacks(tell,companyCards.getC_name());
                    }catch (Exception e){
                        Log.i("---", "dialog异常"+e.getMessage());
                    }
                }
            });
        }
        public void showCallbacks(final String tell, String name) {
            new MaterialDialog.Builder(context)
                    .title(name)
                    .content(tell)
                    .positiveText("立即拨打")
                    .positiveColor(Color.parseColor("#546e7a"))
                    .neutralText("取消")
                    .neutralColor(Color.parseColor("#546e7a"))
                    .onAny(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if ("POSITIVE".equals(which.name())) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_CALL);
                                //url:统一资源定位符
                                //uri:统一资源标示符（更广）
                                intent.setData(Uri.parse("tel:" + tell));
                                //开启系统拨号器
                                startActivity(intent);
                            } else {
                                dialog.dismiss();
                            }
                        }
                    })
                    .show();
        }

        @Override
        public int getItemCount() {
            return this.list.size();
        }
        public void reflesh(List<CompanyCards> companyCardsList){
            this.list.clear();
            this.list.addAll(companyCardsList);
            this.notifyDataSetChanged();
        }
    }

    private void toast(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
