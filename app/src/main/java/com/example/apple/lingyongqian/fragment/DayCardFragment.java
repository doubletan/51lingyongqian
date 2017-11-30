package com.example.apple.lingyongqian.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.activity.AddCardActivity;
import com.example.apple.lingyongqian.activity.MyApp;
import com.example.apple.lingyongqian.adapter.MyViewPager;
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
import com.example.apple.lingyongqian.utils.FullyLinearLayoutManager;
import com.example.apple.lingyongqian.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class DayCardFragment extends Fragment implements View.OnClickListener {
    private View view,no_content1,no_content2,no_content3;
    private String date = "";//条目日期
    private RecyclerView recyclerView;
    private ImageView add,back;
    private TextView toolbar_title;

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
    private CompanyCards companyCards;
    private PersonCards personCards;
    private PlaceCards placeCards;

    private Context context;
    private LayoutInflater layoutInflater;

    private List<View> viewList = new ArrayList<>();//viewpager的list
    private View companys,persons,places;//viewpager的页面
    private ViewPager viewPager;
    private TabLayout tabLayout;


    public DayCardFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutInflater = inflater;
        view = inflater.inflate(R.layout.fragment_day_card,null);
        companys = inflater.inflate(R.layout.day_card_recycleview, null);
        places = inflater.inflate(R.layout.day_card_recycleview, null);
        persons = inflater.inflate(R.layout.day_card_recycleview, null);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);

        toolbar_title = (TextView) view.findViewById(R.id.date);
        back = (ImageView) view.findViewById(R.id.back);
        add = (ImageView) view.findViewById(R.id.add_day);

        back.setOnClickListener(this);
        add.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.add_day:
                //添加该日期下的卡片
                Intent intent = new Intent(getActivity(), AddCardActivity.class);
                intent.putExtra("fragmentAddCard", date);
                startActivityForResult(intent, 1);
                break;
            case R.id.back:
                onDestroy();
                getActivity().finish();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.i("---", resultCode + "");
        if (resultCode==0){
            List<PersonCards> temp = loadPersons(date);
            //Log.i("---","PersonCards size :"+temp.size());
            have_num1.setText("有 " + temp.size() + " 张名片");
            recyclerView1.setVisibility(View.VISIBLE);
            no_content1.setVisibility(View.GONE);
            myPersonAdapter.reflesh(temp);
        }else if (resultCode==1){
            List<PlaceCards> temp = loadPlaces(date);
            have_num2.setText("有 " + temp.size() + " 个地点");
            recyclerView2.setVisibility(View.VISIBLE);
            no_content2.setVisibility(View.GONE);
            myPlaceAdapter.reflesh(temp);
        }else {
            List<CompanyCards> temp = loadCompanys(date);
            have_num3.setText("有 " + temp.size() + " 张公司名片");
            recyclerView3.setVisibility(View.VISIBLE);
            no_content3.setVisibility(View.GONE);
            myComapnyAdapter.reflesh(temp);
        }
        myViewPager.notifyDataSetChanged();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getContext();
        Bundle bundle = getArguments();
        if (bundle!=null){
            info = bundle.getParcelable("info");
            date = info.getA_add_date();
            toolbar_title.setText("卡片：" + date);
        }
        loadCards();
        showCards();
    }
    private void loadCards(){
        myApp = MyApp.getApp();
        user = MyApp.user1;
        daoSession = myApp.getDaoSession(getContext());
        companyCardsDao = daoSession.getCompanyCardsDao();
        personCardsDao = daoSession.getPersonCardsDao();
        placeCardsDao = daoSession.getPlaceCardsDao();
        cardInfodao = daoSession.getAllCardInfoDao();
        allInfoDao = daoSession.getAllInfoDao();

        companyCardsList = loadCompanys(date);
        personCardsList = loadPersons(date);
        placeCardsList = loadPlaces(date);

        //Log.i("---","卡片fragment："+companyCardsList.size()+" "+personCardsList.size()+" "+placeCardsList.size());
    }

    private MyViewPager myViewPager;
    private void showCards(){
        viewList.add(initPersonView());
        viewList.add(initCompanyView());
        viewList.add(initPlaceView());

        //Log.i("---", "viewList:" + viewList.size());

        myViewPager = new MyViewPager(viewList);
        viewPager.setAdapter(myViewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(myViewPager);

    }

    //根据时间读取公司
    private List<CompanyCards> loadCompanys(String date){
        List<CompanyCards> list = new ArrayList<>();
        QueryBuilder builder = companyCardsDao.queryBuilder();
        builder.where(CompanyCardsDao.Properties.C_add_date.eq(date),CompanyCardsDao.Properties.C_pid.eq(user.getU_id())).build();
        list = builder.orderDesc(CompanyCardsDao.Properties.C_id).list();
        return list;
    }
    //根据时间读取地点
    private List<PlaceCards> loadPlaces(String date){
        List<PlaceCards> list = new ArrayList<>();
        QueryBuilder builder = placeCardsDao.queryBuilder();
        builder.where(PlaceCardsDao.Properties.S_add_date.eq(date),PlaceCardsDao.Properties.S_pid.eq(user.getU_id())).build();
        list = builder.orderDesc(PlaceCardsDao.Properties.S_id).list();
        return list;
    }
    //根据时间读取人物
    private List<PersonCards> loadPersons(String date){
        List<PersonCards> list = new ArrayList<>();
        QueryBuilder builder = personCardsDao.queryBuilder();
        builder.where(PersonCardsDao.Properties.P_add_date.eq(date),PersonCardsDao.Properties.P_pid.eq(user.getU_id())).build();
        list = builder.orderDesc(PersonCardsDao.Properties.P_id).list();
        return list;
    }

    //初始化布局 companys,persons,places
    private MyComapnyAdapter myComapnyAdapter;
    private MyPersonAdapter myPersonAdapter;
    private MyPlaceAdapter myPlaceAdapter;
    private TextView have_num1,have_num2,have_num3;
    private RecyclerView recyclerView1,recyclerView2,recyclerView3;
    private int size;
    private View initCompanyView(){
        companys = layoutInflater.inflate(R.layout.day_card_recycleview, null);
        recyclerView3 = (RecyclerView) companys.findViewById(R.id.recyclerView);
        no_content3 = companys.findViewById(R.id.no_contents);
        have_num3 = (TextView) companys.findViewById(R.id.have_num);
        size = this.companyCardsList.size();
        checkList(size,recyclerView3,no_content3);
        have_num3.setText("有 "+size+" 张公司名片");
        myComapnyAdapter = new MyComapnyAdapter(this.companyCardsList);
        FullyLinearLayoutManager fullyLinearLayoutManager = new FullyLinearLayoutManager(getActivity());
        fullyLinearLayoutManager.setOrientation(FullyLinearLayoutManager.VERTICAL);
        recyclerView3.setLayoutManager(fullyLinearLayoutManager);
        recyclerView3.setAdapter(myComapnyAdapter);
        return companys;
    }
    private View initPlaceView(){
        places = layoutInflater.inflate(R.layout.day_card_recycleview, null);
        recyclerView2 = (RecyclerView) places.findViewById(R.id.recyclerView);
        no_content2 = places.findViewById(R.id.no_contents);
        have_num2 = (TextView) places.findViewById(R.id.have_num);
        size = this.placeCardsList.size();
        checkList(size,recyclerView2,no_content2);
        have_num2.setText("有 " + size + " 个地点");
        myPlaceAdapter = new MyPlaceAdapter(this.placeCardsList);
        FullyLinearLayoutManager fullyLinearLayoutManager = new FullyLinearLayoutManager(getActivity());
        fullyLinearLayoutManager.setOrientation(FullyLinearLayoutManager.VERTICAL);
        recyclerView2.setLayoutManager(fullyLinearLayoutManager);
        recyclerView2.setAdapter(myPlaceAdapter);
        return places;
    }
    private View initPersonView(){
        persons = layoutInflater.inflate(R.layout.day_card_recycleview, null);
        recyclerView1 = (RecyclerView) persons.findViewById(R.id.recyclerView);
        no_content1 = persons.findViewById(R.id.no_contents);
        have_num1 = (TextView) persons.findViewById(R.id.have_num);
        size = this.personCardsList.size();
        checkList(size,recyclerView1,no_content1);
        have_num1.setText("有 " + size + " 张名片");
        myPersonAdapter = new MyPersonAdapter(this.personCardsList);
        FullyLinearLayoutManager fullyLinearLayoutManager = new FullyLinearLayoutManager(getActivity());
        fullyLinearLayoutManager.setOrientation(FullyLinearLayoutManager.VERTICAL);
        recyclerView1.setLayoutManager(fullyLinearLayoutManager);
        recyclerView1.setAdapter(myPersonAdapter);
        return persons;
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
    public void onResume() {
        super.onResume();
        StatusBarUtil.setColor(getActivity(), Color.parseColor("#607d8b"), 0);
    }

    /**
     * place
     */
    class ViewHolder2 extends RecyclerView.ViewHolder{
        EditText name,address,describ;
        TextView show_all,save;
        ImageView share,del;
        ImageView head_img;
        View others;

        public ViewHolder2(View itemView) {
            super(itemView);
            name= (EditText) itemView.findViewById(R.id.name);
            address= (EditText) itemView.findViewById(R.id.address);
            describ= (EditText) itemView.findViewById(R.id.describ);
            show_all = (TextView) itemView.findViewById(R.id.show_all);
            save=(TextView)itemView.findViewById(R.id.save);
            share = (ImageView) itemView.findViewById(R.id.share);
            del = (ImageView) itemView.findViewById(R.id.del);
            head_img = (ImageView) itemView.findViewById(R.id.person_head_img);
            others = itemView.findViewById(R.id.others);
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
            View view = LayoutInflater.from(getContext()).inflate(R.layout.day_item_place,viewGroup,false);
            //根据子视图创建ViewHolder对象
            ViewHolder2 viewHolder = new ViewHolder2(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder2 viewHolder, int i) {
            final PlaceCards placeCards = this.list.get(i);
            final String name = placeCards.getS_name();
            final String name_temp = name;
            final String address = placeCards.getS_address();
            final String describ = placeCards.getS_describ();
            viewHolder.name.setText(name);
            viewHolder.address.setText(address);
            final int[] a = {0};
            viewHolder.show_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (a[0] == 0) {
                        viewHolder.others.setVisibility(View.VISIBLE);
                        viewHolder.describ.setText(describ);
                        viewHolder.show_all.setText("隐藏描述");
                        viewHolder.save.setVisibility(View.VISIBLE);
                        a[0] = 1;
                    } else {
                        viewHolder.others.setVisibility(View.GONE);
                        viewHolder.show_all.setText("描述");
                        viewHolder.save.setVisibility(View.GONE);
                        a[0] = 0;
                    }
                }
            });
            viewHolder.save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = viewHolder.name.getText().toString().trim();
                    if (!"".equals(name)) {
                        placeCards.setS_name(name);
                        placeCards.setS_address(viewHolder.address.getText().toString().trim());
                        placeCards.setS_describ(viewHolder.describ.getText().toString().trim());
                        placeCardsDao.update(placeCards);

                        QueryBuilder<AllCardInfo> qbAll
                                = cardInfodao.queryBuilder().where(AllCardInfoDao.Properties.Ac_add_date.eq(date),
                                AllCardInfoDao.Properties.Ac_card_name.eq(name_temp), AllCardInfoDao.Properties.Ac_pid.eq(user.getU_id()));
                        AllCardInfo allCardInfo = qbAll.list().get(0);
                        allCardInfo.setAc_card_name(name);
                        cardInfodao.update(allCardInfo);

                        Toast.makeText(getContext(), "修改成功", Toast.LENGTH_SHORT).show();
                        myPlaceAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "地名不能为空", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            viewHolder.del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delDialog(placeCards,placeCards.getS_add_date(),placeCards.getS_name());
                }
            });
            viewHolder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                    intent.putExtra(Intent.EXTRA_TEXT, placeCards.getS_name()+" address:"+placeCards.getS_address());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(Intent.createChooser(intent, getActivity().getTitle()));
                }
            });
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
     * person
     */
    class ViewHolder1 extends RecyclerView.ViewHolder{
        EditText person_name,tell,address,email,describ;
        TextView show_all,save;
        ImageView call_item,share,del;
        ImageView person_head_img;
        RadioButton boy,gril;
        View others;

        public ViewHolder1(View itemView) {
            super(itemView);
            person_name=(EditText)itemView.findViewById(R.id.name);
            address=(EditText)itemView.findViewById(R.id.address);
            tell=(EditText)itemView.findViewById(R.id.tell);
            email=(EditText)itemView.findViewById(R.id.email);
            describ=(EditText)itemView.findViewById(R.id.describ);
            show_all=(TextView)itemView.findViewById(R.id.show_all);
            save=(TextView)itemView.findViewById(R.id.save);
            person_head_img = (ImageView) itemView.findViewById(R.id.person_head_img);
            call_item = (ImageView) itemView.findViewById(R.id.call_item);
            share = (ImageView) itemView.findViewById(R.id.share);
            del = (ImageView) itemView.findViewById(R.id.del);
            boy = (RadioButton) itemView.findViewById(R.id.boy);
            gril = (RadioButton) itemView.findViewById(R.id.gril);
            others = itemView.findViewById(R.id.others);
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
            View view = LayoutInflater.from(getContext()).inflate(R.layout.day_item_person,viewGroup,false);
            //根据子视图创建ViewHolder对象
            ViewHolder1 viewHolder = new ViewHolder1(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder1 viewHolder, int i) {
            final PersonCards personCards = this.list.get(i);
            final String name = personCards.getP_name();
            final String name_temp = name;
            final String address = personCards.getP_adress();
            final String tell = personCards.getP_tell();
            final String email = personCards.getP_email();
            final String describ = personCards.getP_describ();
            final String sex = personCards.getP_sex();
            viewHolder.person_name.setText(name);
            viewHolder.tell.setText(tell);
            final int[] a = {0};
            viewHolder.show_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (a[0] == 0) {
                        viewHolder.others.setVisibility(View.VISIBLE);
                        if ("男".equals(sex)) {
                            viewHolder.boy.setChecked(true);
                        } else {
                            viewHolder.gril.setChecked(true);
                        }
                        viewHolder.address.setText(address);
                        viewHolder.email.setText(email);
                        viewHolder.describ.setText(describ);
                        viewHolder.show_all.setText("隐藏部分");
                        viewHolder.save.setVisibility(View.VISIBLE);
                        a[0] = 1;
                    } else {
                        viewHolder.others.setVisibility(View.GONE);
                        viewHolder.show_all.setText("显示所有");
                        viewHolder.save.setVisibility(View.GONE);
                        a[0] = 0;
                    }
                }
            });
            viewHolder.save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = viewHolder.person_name.getText().toString().trim();
                    if (!"".equals(name)){
                        personCards.setP_name(name);
                        personCards.setP_tell(viewHolder.tell.getText().toString().trim());
                        personCards.setP_adress(viewHolder.address.getText().toString().trim());
                        personCards.setP_email(viewHolder.email.getText().toString().trim());
                        personCards.setP_describ(viewHolder.describ.getText().toString().trim());
                        if (viewHolder.boy.isChecked()){
                            personCards.setP_sex("男");
                        }else {
                            personCards.setP_sex("女");
                        }
                        personCardsDao.update(personCards);

                        QueryBuilder<AllCardInfo> qbAll
                                = cardInfodao.queryBuilder().where(AllCardInfoDao.Properties.Ac_add_date.eq(date),
                                AllCardInfoDao.Properties.Ac_card_name.eq(name_temp), AllCardInfoDao.Properties.Ac_pid.eq(user.getU_id()));
                        AllCardInfo allCardInfo = qbAll.list().get(0);
                        allCardInfo.setAc_card_name(name);
                        cardInfodao.update(allCardInfo);

                        Toast.makeText(getContext(), "修改成功", Toast.LENGTH_SHORT).show();
                        myPersonAdapter.notifyDataSetChanged();
                    }else {
                        Toast.makeText(getContext(), "姓名不能为空", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            viewHolder.call_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!"".equals(tell)){
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_CALL);
                        //url:统一资源定位符
                        //uri:统一资源标示符（更广）
                        intent.setData(Uri.parse("tel:" + tell));
                        //开启系统拨号器
                        startActivity(intent);
                    }else {
                        Toast.makeText(getContext(), "没有号码", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            viewHolder.del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delDialog(personCards,personCards.getP_add_date(),personCards.getP_name());
                }
            });
            viewHolder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                    intent.putExtra(Intent.EXTRA_TEXT, personCards.getP_name()+" tell:"+personCards.getP_tell());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(Intent.createChooser(intent, getActivity().getTitle()));
                }
            });
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
     * company
     */
    class ViewHolder extends RecyclerView.ViewHolder{
        EditText name,tell,address,email,describ;
        TextView show_all,save;
        ImageView call_item,share,del;
        ImageView head_img;
        View others;

        public ViewHolder(View itemView) {
            super(itemView);
            name= (EditText) itemView.findViewById(R.id.name);
            tell= (EditText) itemView.findViewById(R.id.tell);
            address= (EditText) itemView.findViewById(R.id.address);
            email= (EditText) itemView.findViewById(R.id.email);
            describ= (EditText) itemView.findViewById(R.id.describ);
            show_all = (TextView) itemView.findViewById(R.id.show_all);
            save=(TextView)itemView.findViewById(R.id.save);
            call_item = (ImageView) itemView.findViewById(R.id.call_item);
            share = (ImageView) itemView.findViewById(R.id.share);
            del = (ImageView) itemView.findViewById(R.id.del);
            head_img = (ImageView) itemView.findViewById(R.id.person_head_img);
            others = itemView.findViewById(R.id.others);
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
            View view = LayoutInflater.from(getContext()).inflate(R.layout.day_item_company,viewGroup,false);
            //根据子视图创建ViewHolder对象
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int i) {
            final CompanyCards companyCards = this.list.get(i);
            final String name = companyCards.getC_name();
            final String name_temp = name;
            final String address = companyCards.getC_address();
            final String tell = companyCards.getC_tell();
            final String email = companyCards.getC_email();
            final String describ = companyCards.getC_describ();
            viewHolder.name.setText(name);
            viewHolder.tell.setText(tell);
            final int[] a = {0};
            viewHolder.show_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (a[0] == 0) {
                        viewHolder.others.setVisibility(View.VISIBLE);
                        viewHolder.address.setText(address);
                        viewHolder.email.setText(email);
                        viewHolder.describ.setText(describ);
                        viewHolder.show_all.setText("隐藏部分");
                        viewHolder.save.setVisibility(View.VISIBLE);
                        a[0] = 1;
                    } else {
                        viewHolder.others.setVisibility(View.GONE);
                        viewHolder.show_all.setText("显示所有");
                        viewHolder.save.setVisibility(View.GONE);
                        a[0] = 0;
                    }
                }
            });
            viewHolder.save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = viewHolder.name.getText().toString().trim();
                    if (!"".equals(name)) {
                        companyCards.setC_name(name);
                        companyCards.setC_tell(viewHolder.tell.getText().toString().trim());
                        companyCards.setC_address(viewHolder.address.getText().toString().trim());
                        companyCards.setC_email(viewHolder.email.getText().toString().trim());
                        companyCards.setC_describ(viewHolder.describ.getText().toString().trim());
                        companyCardsDao.update(companyCards);

                        QueryBuilder<AllCardInfo> qbAll
                                = cardInfodao.queryBuilder().where(AllCardInfoDao.Properties.Ac_add_date.eq(date),
                                AllCardInfoDao.Properties.Ac_card_name.eq(name_temp), AllCardInfoDao.Properties.Ac_pid.eq(user.getU_id()));
                        AllCardInfo allCardInfo = qbAll.list().get(0);
                        allCardInfo.setAc_card_name(name);
                        cardInfodao.update(allCardInfo);

                        Toast.makeText(getContext(), "修改成功", Toast.LENGTH_SHORT).show();
                        myComapnyAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "公司名不能为空", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            viewHolder.call_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!"".equals(tell)){
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_CALL);
                        //url:统一资源定位符
                        //uri:统一资源标示符（更广）
                        intent.setData(Uri.parse("tel:" + tell));
                        //开启系统拨号器
                        startActivity(intent);
                    }else {
                        Toast.makeText(getContext(), "没有号码", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            viewHolder.del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delDialog(companyCards,companyCards.getC_add_date(),companyCards.getC_name());
                }
            });
            viewHolder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                    intent.putExtra(Intent.EXTRA_TEXT, companyCards.getC_name()+" tell:"+companyCards.getC_tell());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(Intent.createChooser(intent, getActivity().getTitle()));
                }
            });
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

    //删除数据dialog
    private void delDialog(final Object object,final String date,String name){
        int color = Color.parseColor("#546e7a");
        new MaterialDialog.Builder(getContext())
                .content("删除 " + name + " 的名片？")
                .positiveText("删除")
                .titleColor(color)
                .positiveColor(color)
                .negativeText("取消")
                .negativeColor(color)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if ("POSITIVE".equals(which.toString())) {
                            delInfo(info,object, date);
                            Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                            myViewPager.notifyDataSetChanged();
                        }
                    }
                })
                .show();
    }
    //删除
    protected void delInfo(AllInfo allInfo,Object object,String date){
        Long del_id = -1l;
        if (object instanceof PersonCards){
            PersonCards personCards = (PersonCards) object;
            del_id = personCards.getP_id();
            //删除人物卡片
            QueryBuilder<PersonCards> qb1
                    = personCardsDao.queryBuilder().where(PersonCardsDao.Properties.P_id.eq(del_id), PersonCardsDao.Properties.P_pid.eq(user.getU_id()));
            qb1.buildDelete().executeDeleteWithoutDetachingEntities();
            List<PersonCards> temp = loadPersons(date);
            //Log.i("---","PersonCards size :"+temp.size());
            int size = temp.size();
            have_num1.setText("有 " + size + " 张名片");
            if (size>0){
                recyclerView1.setVisibility(View.VISIBLE);
                no_content1.setVisibility(View.GONE);
            }else {
                recyclerView1.setVisibility(View.GONE);
                no_content1.setVisibility(View.VISIBLE);
            }
            myPersonAdapter.reflesh(temp);
            //删除AllCardInfo中的记录
            QueryBuilder<AllCardInfo> qbAll
                    = cardInfodao.queryBuilder().where(AllCardInfoDao.Properties.Ac_add_date.eq(date),
                    AllCardInfoDao.Properties.Ac_card_name.eq(personCards.getP_name()),AllCardInfoDao.Properties.Ac_pid.eq(user.getU_id()));
            qbAll.buildDelete().executeDeleteWithoutDetachingEntities();
            QueryBuilder<AllCardInfo> getAll
                    = cardInfodao.queryBuilder().where(AllCardInfoDao.Properties.Ac_add_date.eq(date), AllCardInfoDao.Properties.Ac_pid.eq(user.getU_id()));
            getAll.build();
            List list = getAll.list();
            if (list.size()==0){
                allInfo.setA_cards(0);
                allInfoDao.update(allInfo);
            }
            del_id = -1l;
        }else if (object instanceof CompanyCards){
            CompanyCards companyCards = (CompanyCards) object;
            del_id = companyCards.getC_id();
            //删除人物卡片
            QueryBuilder<CompanyCards> qb1
                    = companyCardsDao.queryBuilder().where(CompanyCardsDao.Properties.C_id.eq(del_id), CompanyCardsDao.Properties.C_pid.eq(user.getU_id()));
            qb1.buildDelete().executeDeleteWithoutDetachingEntities();
            List<CompanyCards> temp = loadCompanys(date);
            //Log.i("---","PersonCards size :"+temp.size());
            int size = temp.size();
            have_num3.setText("有 " + size + " 张公司名片");
            if (size>0){
                recyclerView3.setVisibility(View.VISIBLE);
                no_content3.setVisibility(View.GONE);
            }else {
                recyclerView3.setVisibility(View.GONE);
                no_content3.setVisibility(View.VISIBLE);
            }
            myComapnyAdapter.reflesh(temp);
            //删除AllCardInfo中的记录
            QueryBuilder<AllCardInfo> qbAll
                    = cardInfodao.queryBuilder().where(AllCardInfoDao.Properties.Ac_add_date.eq(date),
                    AllCardInfoDao.Properties.Ac_card_name.eq(companyCards.getC_name()), AllCardInfoDao.Properties.Ac_pid.eq(user.getU_id()));
            qbAll.buildDelete().executeDeleteWithoutDetachingEntities();
            QueryBuilder<AllCardInfo> getAll
                    = cardInfodao.queryBuilder().where(AllCardInfoDao.Properties.Ac_add_date.eq(date), AllCardInfoDao.Properties.Ac_pid.eq(user.getU_id()));
            getAll.build();
            List list = getAll.list();
            if (list.size()==0){
                allInfo.setA_cards(0);
                allInfoDao.update(allInfo);
            }
            del_id = -1l;
        }else if (object instanceof PlaceCards){
            PlaceCards placeCards = (PlaceCards) object;
            del_id = placeCards.getS_id();
            //删除卡片
            QueryBuilder<PlaceCards> qb1
                    = placeCardsDao.queryBuilder().where(PlaceCardsDao.Properties.S_id.eq(del_id), PlaceCardsDao.Properties.S_pid.eq(user.getU_id()));
            qb1.buildDelete().executeDeleteWithoutDetachingEntities();
            List<PlaceCards> temp = loadPlaces(date);
            //Log.i("---","PersonCards size :"+temp.size());
            int size = temp.size();
            have_num2.setText("有 " + size + " 个地点");
            if (size>0){
                recyclerView2.setVisibility(View.VISIBLE);
                no_content2.setVisibility(View.GONE);
            }else {
                recyclerView2.setVisibility(View.GONE);
                no_content2.setVisibility(View.VISIBLE);
            }
            myPlaceAdapter.reflesh(temp);
            //删除AllCardInfo中的记录
            QueryBuilder<AllCardInfo> qbAll
                    = cardInfodao.queryBuilder().where(AllCardInfoDao.Properties.Ac_add_date.eq(date),
                    AllCardInfoDao.Properties.Ac_card_name.eq(placeCards.getS_name()), AllCardInfoDao.Properties.Ac_pid.eq(user.getU_id()));
            qbAll.buildDelete().executeDeleteWithoutDetachingEntities();
            QueryBuilder<AllCardInfo> getAll
                    = cardInfodao.queryBuilder().where(AllCardInfoDao.Properties.Ac_add_date.eq(date),AllCardInfoDao.Properties.Ac_pid.eq(user.getU_id()));
            getAll.build();
            List list = getAll.list();
            if (list.size()==0){
                allInfo.setA_cards(0);
                allInfoDao.update(allInfo);
            }
            del_id = -1l;
        }
    }
}
