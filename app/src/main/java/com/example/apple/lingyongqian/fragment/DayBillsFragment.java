package com.example.apple.lingyongqian.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.example.apple.lingyongqian.activity.AddBillActivity;
import com.example.apple.lingyongqian.activity.MyApp;
import com.example.apple.lingyongqian.adapter.MyViewPager;
import com.example.apple.lingyongqian.dao.AllBillInfo;
import com.example.apple.lingyongqian.dao.AllBillInfoDao;
import com.example.apple.lingyongqian.dao.AllInfo;
import com.example.apple.lingyongqian.dao.AllInfoDao;
import com.example.apple.lingyongqian.dao.Bills;
import com.example.apple.lingyongqian.dao.BillsDao;
import com.example.apple.lingyongqian.dao.DaoSession;
import com.example.apple.lingyongqian.dao.User;
import com.example.apple.lingyongqian.utils.AutofitTextView;
import com.example.apple.lingyongqian.utils.FullyLinearLayoutManager;
import com.example.apple.lingyongqian.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;


public class DayBillsFragment extends Fragment implements View.OnClickListener {

    private List<View> viewList = new ArrayList<>();//viewpager的list
    private View in,out;//viewpager的页面
    private TextView inTotal,outTotal,inNum,outNum;//统计
    private ViewPager viewpager;
    private View view,no_content1,no_content2;
    private String date = "";//条目日期
    private RecyclerView recyclerView,recyclerView1,recyclerView2;
    private ImageView add,back;
    private TextView toolbar_title;
    private TabLayout tabLayout;

    private MyApp myApp;
    private User user;
    private AllInfo info;
    private AllInfoDao allInfoDao;
    private DaoSession daoSession;
    private BillsDao billsDao;
    private AllBillInfoDao allBillInfoDao;
    private List<Bills> inList = new ArrayList<>();
    private List<Bills> outList = new ArrayList<>();
    private int total = 0;//总的交易次数
    private List<AllBillInfo> allBillInfoList = new ArrayList<>();

    private final String IN = "1";
    private final String OUT = "2";

    public DayBillsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_day_bills,null);
        inNum = (TextView) view.findViewById(R.id.in);
        outNum = (TextView) view.findViewById(R.id.out);
        in = inflater.inflate(R.layout.day_bill_recycleview, null);
        out = inflater.inflate(R.layout.day_bill_recycleview, null);
        viewpager = (ViewPager) view.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);

        toolbar_title = (TextView) view.findViewById(R.id.date);
        back = (ImageView) view.findViewById(R.id.back);
        add = (ImageView) view.findViewById(R.id.add_day);

        back.setOnClickListener(this);
        add.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        StatusBarUtil.setColor(getActivity(), Color.parseColor("#607d8b"), 0);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.add_day:
                //添加该日期下的账单
                Intent intent = new Intent(getActivity(), AddBillActivity.class);
                intent.putExtra("fragmentAddBill", date);
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
        if (resultCode==0){
            inList = loadBills(date, IN);//进账
            outList = loadBills(date, OUT);//支出
            int size = inList.size();
            inTotal.setText("当日共 " + size + " 笔收入");
            checkList(size, recyclerView1, no_content1);
            myInBillAdapter.refresh(inList);
            nums(inList,outList);
        }else {
            inList = loadBills(date, IN);//进账
            outList = loadBills(date, OUT);//出账
            int size = outList.size();
            outTotal.setText("当日共 "+size+" 笔支出");
            checkList(size, recyclerView2, no_content2);
            myOutBillAdapter.refresh(outList);
            nums(inList, outList);
        }
        myViewPager.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle!=null){
            info = bundle.getParcelable("info");
            date = info.getA_add_date();
            toolbar_title.setText("账单：" + date);
        }
        loadBills();
        showBills();
        nums(inList,outList);
    }

    private void loadBills() {
        myApp = MyApp.getApp();
        user = MyApp.user1;
        daoSession = myApp.getDaoSession(getContext());
        allBillInfoDao = daoSession.getAllBillInfoDao();
        billsDao = daoSession.getBillsDao();
        allInfoDao = daoSession.getAllInfoDao();

        inList = loadBills(date, IN);//进账
        outList = loadBills(date, OUT);//出账


    }

    //计算收入与支出
    private void nums(List<Bills> inList,List<Bills> outList){
        float in = 0;
        float out = 0;

        for (int i = 0;i<inList.size();i++){
            Bills bills = inList.get(i);
            String num = bills.getB_num().trim();
            in+=(int)Float.parseFloat(num);
        }
        for (int i = 0;i<outList.size();i++){
            Bills bills = outList.get(i);
            String num = bills.getB_num().trim();
            out+=(int)Float.parseFloat(num);
        }
        inNum.setText("收入(均取整): "+in+" ￥");
        outNum.setText("支出: "+out+" ￥");
    }

    private MyViewPager myViewPager;
    private void showBills() {
        viewList.add(initInView());
        viewList.add(initOutView());

        Log.i("---", "viewList:" + viewList.size());

        myViewPager = new MyViewPager(viewList);
        viewpager.setAdapter(myViewPager);
        tabLayout.setupWithViewPager(viewpager);
        tabLayout.setTabsFromPagerAdapter(myViewPager);
    }

    //根据时间和类型读取账单
    private List<Bills> loadBills(String date,String type){
        List<Bills> list = new ArrayList<>();
        QueryBuilder builder = billsDao.queryBuilder();
        builder.where(BillsDao.Properties.B_add_date.eq(date),BillsDao.Properties.B_type.eq(type),BillsDao.Properties.B_pid.eq(user.getU_id())).build();
        list = builder.orderDesc(BillsDao.Properties.B_id).list();
        return list;
    }

    private MyInBillAdapter myInBillAdapter;
    private MyOutBillAdapter myOutBillAdapter;

    private View initInView(){
        recyclerView1 = (RecyclerView) in.findViewById(R.id.recyclerView);
        no_content1 = in.findViewById(R.id.no_contents);
        inTotal = (TextView) in.findViewById(R.id.have_num);
        inTotal.setText("当日共 "+this.inList.size()+" 笔收入");
        checkList(this.inList.size(), recyclerView1,no_content1);
        myInBillAdapter = new MyInBillAdapter(this.inList);
        FullyLinearLayoutManager fullyLinearLayoutManager = new FullyLinearLayoutManager(getActivity());
        fullyLinearLayoutManager.setOrientation(FullyLinearLayoutManager.VERTICAL);
        recyclerView1.setLayoutManager(fullyLinearLayoutManager);
        recyclerView1.setAdapter(myInBillAdapter);
        return in;
    }
    private View initOutView(){
        recyclerView2 = (RecyclerView) out.findViewById(R.id.recyclerView);
        no_content2 = out.findViewById(R.id.no_contents);
        outTotal = (TextView) out.findViewById(R.id.have_num);
        outTotal.setText("当日共 "+this.outList.size()+" 笔支出");
        checkList(this.outList.size(), recyclerView2,no_content2);
        myOutBillAdapter = new MyOutBillAdapter(this.outList);
        FullyLinearLayoutManager fullyLinearLayoutManager = new FullyLinearLayoutManager(getActivity());
        fullyLinearLayoutManager.setOrientation(FullyLinearLayoutManager.VERTICAL);
        recyclerView2.setLayoutManager(fullyLinearLayoutManager);
        recyclerView2.setAdapter(myOutBillAdapter);
        return out;
    }
    //判断list是否为空
    private void checkList(int size,RecyclerView recyclerView,View no){
        if (size==0){
            recyclerView.setVisibility(View.GONE);
            no.setVisibility(View.VISIBLE);
        }else {
            recyclerView.setVisibility(View.VISIBLE);
            no.setVisibility(View.GONE);
        }
    }

    /**
     * out
     */
    class ViewHolder extends RecyclerView.ViewHolder{
        AutofitTextView inNum,fromOrTo;
        TextView describ;
        ImageView del,edit;

        public ViewHolder(View itemView) {
            super(itemView);
            inNum = (AutofitTextView) itemView.findViewById(R.id.inNum);
            fromOrTo= (AutofitTextView) itemView.findViewById(R.id.fromOrTo);
            describ = (TextView) itemView.findViewById(R.id.describ);
            del = (ImageView) itemView.findViewById(R.id.del);
            edit = (ImageView) itemView.findViewById(R.id.edit);

        }
    }
    class MyOutBillAdapter extends  RecyclerView.Adapter<ViewHolder>{

        private List<Bills> list;

        public MyOutBillAdapter(List<Bills> list) {
            if (list==null){
                throw new IllegalArgumentException("list must not be null");
            }
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            //加载item子视图生成View对象
            View view = LayoutInflater.from(getContext()).inflate(R.layout.day_bill_item,viewGroup,false);
            //根据子视图创建ViewHolder对象
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int i) {
            final Bills bills = this.list.get(i);
            viewHolder.inNum.setText(bills.getB_num());
            viewHolder.fromOrTo.setText("出处 : "+bills.getB_name());
            String describ = bills.getB_describ();
            if ("".equals(describ)){
                describ = "暂无描述";
            }
            viewHolder.describ.setText("其它描述 : "+describ);
            viewHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog(bills);
                }
            });
            viewHolder.del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delDialog(bills);
                }
            });
        }

        @Override
        public int getItemCount() {
            return this.list.size();
        }

        public void refresh(List<Bills> list){
            this.list.clear();
            this.list.addAll(list);
            this.notifyDataSetChanged();
        }

        View positiveAction;
        EditText item_bill_num = null;
        EditText item_bill_name = null;
        EditText item_bill_describ = null;
        RadioButton in,out;

        private void dialog(final Bills this_bill){
            final String[] name = {""};
            final String[] num = {""};
            final String[] describ = {""};
            MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                    .iconRes(R.mipmap.bill_small)
                    .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                    .title("账目卡片")
                    .titleColor(Color.parseColor("#546e7a"))
                    .customView(R.layout.dailog_bill_edit, true)
                    .positiveText("确定")
                    .positiveColor(Color.parseColor("#546e7a"))
                    .negativeText(android.R.string.cancel)
                    .negativeColor(Color.parseColor("#546e7a"))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            name[0] = item_bill_name.getText().toString().trim();
                            num[0] = item_bill_num.getText().toString().trim();
                            describ[0] = item_bill_describ.getText().toString();
                            int a = 0;
                            if (in.isChecked()){
                                a = 1;
                                this_bill.setB_type("1");
                                QueryBuilder<AllBillInfo> qbAll
                                        = allBillInfoDao.queryBuilder().where(AllBillInfoDao.Properties.Al_add_date.eq(this_bill.getB_add_date()),
                                        AllBillInfoDao.Properties.Al_bill_name.eq(this_bill.getB_name()));
                                AllBillInfo allBillInfo = qbAll.build().list().get(0);
                                allBillInfo.setAl_bill_name(name[0]);
                                allBillInfo.setAl_bill_num(num[0]);
                                allBillInfo.setAl_bill_type(1);
                                allBillInfoDao.update(allBillInfo);
                            }else {
                                this_bill.setB_type("2");
                            }
                            this_bill.setB_name(name[0]);
                            this_bill.setB_num(num[0]);
                            this_bill.setB_describ(describ[0]);
                            billsDao.update(this_bill);
                            outList = loadBills(date, OUT);//支出
                            int out_size = outList.size();
                            outTotal.setText("当日共 "+out_size+" 笔支出");
                            checkList(outList.size(),recyclerView2,no_content2);
                            myOutBillAdapter.refresh(outList);
                            if (a==1){
                                inList = loadBills(date, IN);//进账
                                int size = inList.size();
                                inTotal.setText("当日共 "+size+" 笔收入");
                                checkList(size,recyclerView1,no_content1);
                                myInBillAdapter.refresh(inList);
                                a=0;
                            }
                            nums(inList,outList);
                            myViewPager.notifyDataSetChanged();
                        }
                    }).build();
            View dialog_view = dialog.getCustomView();
            positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
            item_bill_num = (EditText) dialog_view.findViewById(R.id.item_bill_num);
            item_bill_name = (EditText) dialog_view.findViewById(R.id.item_bill_name);
            item_bill_describ = (EditText) dialog_view.findViewById(R.id.item_bill_describ);
            in = (RadioButton) dialog_view.findViewById(R.id.in);
            out = (RadioButton) dialog_view.findViewById(R.id.out);

            item_bill_name.setText(this_bill.getB_name());
            item_bill_num.setText(this_bill.getB_num());
            item_bill_describ.setText(this_bill.getB_describ());
            String it_type = this_bill.getB_type();
            if ("1".equals(it_type)){
                in.setChecked(true);
            }else {
                out.setChecked(true);
            }
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
            dialog.show();
        }
    }


    /**
     * in
     */
    class ViewHolder2 extends RecyclerView.ViewHolder{
        AutofitTextView inNum,fromOrTo;
        TextView describ;
        ImageView del,edit;

        public ViewHolder2(View itemView) {
            super(itemView);
            inNum = (AutofitTextView) itemView.findViewById(R.id.inNum);
            fromOrTo= (AutofitTextView) itemView.findViewById(R.id.fromOrTo);
            describ = (TextView) itemView.findViewById(R.id.describ);
            del = (ImageView) itemView.findViewById(R.id.del);
            edit = (ImageView) itemView.findViewById(R.id.edit);

        }
    }
    class MyInBillAdapter extends  RecyclerView.Adapter<ViewHolder2>{

        private List<Bills> list;

        public MyInBillAdapter(List<Bills> list) {
            if (list==null){
                throw new IllegalArgumentException("list must not be null");
            }
            this.list = list;
        }

        @Override
        public ViewHolder2 onCreateViewHolder(ViewGroup viewGroup, int i) {
            //加载item子视图生成View对象
            View view = LayoutInflater.from(getContext()).inflate(R.layout.day_bill_item,viewGroup,false);
            //根据子视图创建ViewHolder对象
            ViewHolder2 viewHolder = new ViewHolder2(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder2 viewHolder, int i) {
            final Bills bills = this.list.get(i);
            viewHolder.inNum.setText(bills.getB_num());
            viewHolder.fromOrTo.setText("来源 : "+bills.getB_name());
            String describ = bills.getB_describ();
            if ("".equals(describ)){
                describ = "暂无描述";
            }
            viewHolder.describ.setText("其它描述 : "+describ);
            viewHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog(bills);
                }
            });
            viewHolder.del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delDialog(bills);
                }
            });
        }

        @Override
        public int getItemCount() {
            return this.list.size();
        }
        public void refresh(List<Bills> list){
            this.list.clear();
            this.list.addAll(list);
            this.notifyDataSetChanged();
        }

        View positiveAction;
        EditText item_bill_num = null;
        EditText item_bill_name = null;
        EditText item_bill_describ = null;
        RadioButton in,out;

        private void dialog(final Bills this_bill){
            final String[] name = {""};
            final String[] num = {""};
            final String[] describ = {""};
            MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                    .iconRes(R.mipmap.bill_small)
                    .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                    .title("账目卡片")
                    .titleColor(Color.parseColor("#546e7a"))
                    .customView(R.layout.dailog_bill_edit, true)
                    .positiveText("确定")
                    .positiveColor(Color.parseColor("#546e7a"))
                    .negativeText(android.R.string.cancel)
                    .negativeColor(Color.parseColor("#546e7a"))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            name[0] = item_bill_name.getText().toString().trim();
                            num[0] = item_bill_num.getText().toString().trim();
                            describ[0] = item_bill_describ.getText().toString();
                            int a = 0;
                            if (in.isChecked()){
                                this_bill.setB_type("1");
                                QueryBuilder<AllBillInfo> qbAll
                                        = allBillInfoDao.queryBuilder().where(AllBillInfoDao.Properties.Al_add_date.eq(this_bill.getB_add_date()),
                                        AllBillInfoDao.Properties.Al_bill_name.eq(this_bill.getB_name()));
                                AllBillInfo allBillInfo = qbAll.build().list().get(0);
                                allBillInfo.setAl_bill_name(name[0]);
                                allBillInfo.setAl_bill_num(num[0]);
                                allBillInfo.setAl_bill_type(1);
                                allBillInfoDao.update(allBillInfo);
                            }else {
                                this_bill.setB_type("2");
                                QueryBuilder<AllBillInfo> qbAll
                                        = allBillInfoDao.queryBuilder().where(AllBillInfoDao.Properties.Al_add_date.eq(this_bill.getB_add_date()),
                                        AllBillInfoDao.Properties.Al_bill_name.eq(this_bill.getB_name()));
                                AllBillInfo allBillInfo = qbAll.build().list().get(0);
                                allBillInfo.setAl_bill_name(name[0]);
                                allBillInfo.setAl_bill_num(num[0]);
                                allBillInfo.setAl_bill_type(2);
                                allBillInfoDao.update(allBillInfo);
                                a = 1;
                            }
                            this_bill.setB_name(name[0]);
                            this_bill.setB_num(num[0]);
                            this_bill.setB_describ(describ[0]);
                            billsDao.update(this_bill);
                            inList = loadBills(date, IN);//进账
                            int in_size = inList.size();
                            inTotal.setText("当日共 "+in_size+" 笔收入");
                            checkList(in_size,recyclerView1,no_content1);
                            myInBillAdapter.refresh(inList);
                            if (a==1){
                                outList = loadBills(date, OUT);
                                int size = outList.size();
                                outTotal.setText("当日共 "+size+" 笔支出");
                                checkList(size,recyclerView2,no_content2);
                                myOutBillAdapter.refresh(outList);
                                a = 0;
                            }
                            nums(inList,outList);
                            myViewPager.notifyDataSetChanged();
                        }
                    }).build();
            View dialog_view = dialog.getCustomView();
            positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
            item_bill_num = (EditText) dialog_view.findViewById(R.id.item_bill_num);
            item_bill_name = (EditText) dialog_view.findViewById(R.id.item_bill_name);
            item_bill_describ = (EditText) dialog_view.findViewById(R.id.item_bill_describ);
            in = (RadioButton) dialog_view.findViewById(R.id.in);
            out = (RadioButton) dialog_view.findViewById(R.id.out);

            item_bill_name.setText(this_bill.getB_name());
            item_bill_num.setText(this_bill.getB_num());
            item_bill_describ.setText(this_bill.getB_describ());
            String it_type = this_bill.getB_type();
            if ("1".equals(it_type)){
                in.setChecked(true);
            }else {
                out.setChecked(true);
            }
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
            dialog.show();
        }
    }
    //删除数据dialog
    private void delDialog(final Bills bills){
        int color = Color.parseColor("#546e7a");
        new MaterialDialog.Builder(getContext())
                .content("删除 " + bills.getB_name() + " 的账单？")
                .positiveText("删除")
                .titleColor(color)
                .positiveColor(color)
                .negativeText("取消")
                .negativeColor(color)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if ("POSITIVE".equals(which.toString())) {
                            delInfo(info,bills, bills.getB_add_date());
                            Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                            myViewPager.notifyDataSetChanged();
                        }
                    }
                })
                .show();
    }
    //删除账单
    protected void delInfo(AllInfo allInfo,Bills bills,String date) {
        Long del_id = bills.getB_id();
        String type = bills.getB_type();
        QueryBuilder<Bills> qb1
                = billsDao.queryBuilder().where(BillsDao.Properties.B_id.eq(del_id), BillsDao.Properties.B_pid.eq(user.getU_id()));
        qb1.buildDelete().executeDeleteWithoutDetachingEntities();
        List<Bills> in_temp = loadBills(date, type);
        int size = in_temp.size();
        if ("1".equals(type)){
            nums(in_temp, outList);
            inTotal.setText("当日共 "+size+" 笔收入");
            if (size > 0) {
                recyclerView1.setVisibility(View.VISIBLE);
                no_content1.setVisibility(View.GONE);
                myInBillAdapter.refresh(in_temp);
            } else {
                recyclerView1.setVisibility(View.GONE);
                no_content1.setVisibility(View.VISIBLE);
            }
        } else {
            inList = loadBills(date,IN);
            nums(inList, in_temp);
            outTotal.setText("当日共 "+size+" 笔支出");
            if (size > 0) {
                recyclerView2.setVisibility(View.VISIBLE);
                no_content2.setVisibility(View.GONE);
                myOutBillAdapter.refresh(in_temp);
            } else {
                recyclerView2.setVisibility(View.GONE);
                no_content2.setVisibility(View.VISIBLE);
            }
        }
        //删除AllBillInfo中的记录
        QueryBuilder<AllBillInfo> qbAll
                = allBillInfoDao.queryBuilder().where(AllBillInfoDao.Properties.Al_add_date.eq(date),
                AllBillInfoDao.Properties.Al_bill_name.eq(bills.getB_name()), AllBillInfoDao.Properties.Al_pid.eq(user.getU_id()));
        qbAll.buildDelete().executeDeleteWithoutDetachingEntities();
        QueryBuilder<AllBillInfo> getAll
                = allBillInfoDao.queryBuilder().where(AllBillInfoDao.Properties.Al_add_date.eq(date),AllBillInfoDao.Properties.Al_pid.eq(user.getU_id()));
        getAll.build();
        List list = getAll.list();
        if (list.size() == 0) {
            allInfo.setA_bills(0);
            allInfoDao.update(allInfo);
        }
    }
}
