package com.example.apple.lingyongqian.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.dao.AllInfo;
import com.example.apple.lingyongqian.dao.AllInfoDao;
import com.example.apple.lingyongqian.dao.DaoSession;
import com.example.apple.lingyongqian.dao.Diarys;
import com.example.apple.lingyongqian.dao.DiarysDao;
import com.example.apple.lingyongqian.dao.User;
import com.example.apple.lingyongqian.utils.AddDiaryActivity;
import com.example.apple.lingyongqian.utils.FullyLinearLayoutManager;
import com.example.apple.lingyongqian.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class AllDiaryActivity extends AppCompatActivity implements View.OnClickListener {

    private MyApp myApp;
    private User user;
    private DaoSession daoSession;
    private DiarysDao diarysDao;
    private AllInfoDao allInfoDao;
    private AllInfo allInfo;
    private List<Diarys> diarysList = new ArrayList<>();
    private TextView toolbar_title;
    private ImageView add,back;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_diary);
        myApp = MyApp.getApp();
        myApp.addToList(this);
        context = this;
        user = MyApp.user1;
        initView();
    }

    private String final_diarys_date;
    private void initView(){

        recyclerView = (RecyclerView) findViewById(R.id.all_diary_recycleView);

        toolbar_title = (TextView) findViewById(R.id.date);
        back = (ImageView) findViewById(R.id.back);
        add = (ImageView) findViewById(R.id.add_day);

        daoSession = myApp.getDaoSession(this);
        diarysDao = daoSession.getDiarysDao();
        allInfoDao = daoSession.getAllInfoDao();
        loadDiary();
        if (diarysList.size()!=0){
            final_diarys_date = diarysList.get(0).getD_add_date();
        }
        toolbar_title.setText("所有日记");

        back.setOnClickListener(this);
        add.setOnClickListener(this);

    }

    private AllInfo getInfo(String date){
        QueryBuilder builder = allInfoDao.queryBuilder();
        builder.where(AllInfoDao.Properties.A_add_date.eq(date),AllInfoDao.Properties.A_pid.eq(user.getU_id())).build();
        List list = builder.list();
        if (list.size()==0){
            return null;
        }
        return (AllInfo) list.get(0);
    }

    @Override
    public void onStart() {
        super.onStart();
        StatusBarUtil.setColor(this, Color.parseColor("#607d8b"), 0);
        showDiary();
        if (MyApp.today_date.equals(final_diarys_date)){
            add.setVisibility(View.GONE);
        }else {
            add.setVisibility(View.VISIBLE);
            add.setOnClickListener(this);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        StatusBarUtil.setColor(this, Color.parseColor("#607d8b"), 0);
        loadDiary();
        showAdd();
        myAdapter.refresh(diarysList);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.add_day:
                Intent intent = new Intent(AllDiaryActivity.this, AddDiaryActivity.class);
                startActivity(intent);
                break;
            case R.id.back:
                finish();
                break;
        }
    }



    private void loadDiary(){
        QueryBuilder builder = diarysDao.queryBuilder();
        //builder.limit(10);
        diarysList = builder.where(DiarysDao.Properties.D_pid.eq(user.getU_id())).orderDesc(DiarysDao.Properties.D_add_date).list();
    }
    private void showDiary(){
        myAdapter = new MyAdapter(diarysList);
        FullyLinearLayoutManager fullyLinearLayoutManager = new FullyLinearLayoutManager(this);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(fullyLinearLayoutManager);
    }

    /**
     * 自定义ViewHolder，需要继承RecyclerView.ViewHolder。
     * RecyclerView强制使用该模式
     */
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView diary_date,content;
        ImageView date_dot,share,del;
        TextView date_line;

        public ViewHolder(View itemView) {
            super(itemView);
            diary_date=(TextView)itemView.findViewById(R.id.diary_date);
            content=(TextView)itemView.findViewById(R.id.content);
            date_dot= (ImageView) itemView.findViewById(R.id.date_dot);
            date_line = (TextView) itemView.findViewById(R.id.date_line);
            share= (ImageView) itemView.findViewById(R.id.share);
            del= (ImageView) itemView.findViewById(R.id.del);
        }
    }
    class MyAdapter extends  RecyclerView.Adapter<ViewHolder>{

        private List<Diarys> list;

        public MyAdapter(List<Diarys> list) {
            if (list==null){
                throw new IllegalArgumentException("list must not be null");
            }
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            //加载item子视图生成View对象
            View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.all_diary_item,viewGroup,false);
            //根据子视图创建ViewHolder对象
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {
            final Diarys diarys = this.list.get(i);
            viewHolder.diary_date.setText(diarys.getD_add_date());
            viewHolder.content.setText(diarys.getD_textcontent());
            //随机更换时间轴的样式
            int type = (int)(1+ Math.random()*(5));
            if (type==1){
                viewHolder.date_dot.setImageResource(R.mipmap.base_dot);
                viewHolder.date_line.setBackgroundResource(R.color.slid_menu1);
            }else if (type==2){
                viewHolder.date_dot.setImageResource(R.mipmap.green_dot);
                viewHolder.date_line.setBackgroundResource(R.color.green_color);
            }else if (type==3){
                viewHolder.date_dot.setImageResource(R.mipmap.blue_dot);
                viewHolder.date_line.setBackgroundResource(R.color.blue_color);
            }else if (type==4){
                viewHolder.date_dot.setImageResource(R.mipmap.pink_dot);
                viewHolder.date_line.setBackgroundResource(R.color.pink_color);
            }else {
                viewHolder.date_dot.setImageResource(R.mipmap.orange_dot);
                viewHolder.date_line.setBackgroundResource(R.color.orange_color);
            }
            viewHolder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                    intent.putExtra(Intent.EXTRA_TEXT, diarys.getD_textcontent());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(Intent.createChooser(intent, getTitle()));
                }
            });
            viewHolder.del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delDiary(diarys);
                }
            });
            viewHolder.content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AllDiaryActivity.this,AddDiaryActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("diary_item",diarys);
                    intent.putExtra("diary",bundle);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void refresh(List<Diarys> diarys){
            this.list.clear();
            this.list.addAll(diarys);
            this.notifyDataSetChanged();
        }
    }

    private void delDiary(final Diarys diarys){
        int color = Color.parseColor("#546e7a");
        new MaterialDialog.Builder(context)
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
                            try {
                                diarysDao.delete(diarys);
                                allInfo = getInfo(diarys.getD_add_date());
                                if (allInfo.getA_notes() == 0 && allInfo.getA_cards() == 0 && allInfo.getA_bills() == 0) {
                                    allInfoDao.delete(allInfo);
                                } else {
                                    allInfo.setA_diarys(0);
                                    allInfoDao.update(allInfo);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            toast("删除成功");
                            loadDiary();
                            myAdapter.refresh(diarysList);
                            showAdd();
                        }
                    }
                })
                .show();
    }

    private void showAdd(){
        if (diarysList.size()!=0){
            final_diarys_date = diarysList.get(0).getD_add_date();
            if (MyApp.today_date.equals(final_diarys_date)){
                add.setVisibility(View.GONE);
            }else {
                add.setVisibility(View.VISIBLE);
                add.setOnClickListener(this);
            }
        }else {
            add.setVisibility(View.VISIBLE);
        }

    }

    private void toast(String str){
        Toast.makeText(this,str, Toast.LENGTH_SHORT).show();
    }
}
