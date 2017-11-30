package com.example.apple.lingyongqian.activity;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.dao.AllInfo;
import com.example.apple.lingyongqian.dao.AllInfoDao;
import com.example.apple.lingyongqian.dao.DaoSession;
import com.example.apple.lingyongqian.dao.Notes;
import com.example.apple.lingyongqian.dao.NotesDao;
import com.example.apple.lingyongqian.dao.User;
import com.example.apple.lingyongqian.utils.ExStaggeredGridLayoutManager;
import com.example.apple.lingyongqian.utils.StatusBarUtil;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class AllNotesActivity extends AppCompatActivity implements View.OnClickListener {

    private MyApp myApp;
    private User user;
    private DaoSession daoSession;
    private AllInfoDao allInfoDao;
    private AllInfo allInfo;
    private NotesDao notesDao;
    private List<Notes> notesList = new ArrayList<>();
    private TextView toolbar_title;
    private ImageView add,back;
    private TextView say;
    private RecyclerView all_notes_recycleView;
    private MyAdapter myAdapter;
    private RecyclerTouchListener onTouchListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_notes);
        myApp = MyApp.getApp();
        myApp.addToList(this);
        user = MyApp.user1;
        initView();
    }

    private void initView(){

        toolbar_title = (TextView) findViewById(R.id.date);
        back = (ImageView) findViewById(R.id.back);
        add = (ImageView) findViewById(R.id.add_day);
        say = (TextView) findViewById(R.id.say);
        all_notes_recycleView = (RecyclerView) findViewById(R.id.all_notes_recycleView);

        daoSession = myApp.getDaoSession(this);
        notesDao = daoSession.getNotesDao();
        allInfoDao = daoSession.getAllInfoDao();

        toolbar_title.setText("所有便签");

        back.setOnClickListener(this);
        add.setOnClickListener(this);

        loadNotes();
        showNotes();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadNotes();
        myAdapter.reflesh(notesList);
    }

    @Override
    public void onStart() {
        super.onStart();
        StatusBarUtil.setColor(this, Color.parseColor("#607d8b"), 0);
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.add_day:
                //添加该日期下的便签
                Intent intent = new Intent(AllNotesActivity.this, AddNoteActivity.class);
                intent.putExtra("fragmentAddNote", MyApp.today_date);
                startActivityForResult(intent, 2);
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    private void loadNotes(){
        QueryBuilder builder = notesDao.queryBuilder();
        //builder.limit(12);
        builder.where(NotesDao.Properties.N_pid.eq(user.getU_id())).orderDesc(NotesDao.Properties.N_add_date);
        notesList = builder.list();
    }

    private void showNotes(){
        myAdapter = new MyAdapter(notesList);
        ExStaggeredGridLayoutManager sgm = new ExStaggeredGridLayoutManager(2,ExStaggeredGridLayoutManager.VERTICAL);
        all_notes_recycleView.setLayoutManager(sgm);
        //为RecyclerView设置适配器
        all_notes_recycleView.setAdapter(myAdapter);
        onTouchListener = new RecyclerTouchListener(this, all_notes_recycleView);
        all_notes_recycleView.addOnItemTouchListener(onTouchListener);
        onTouchListener
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("noteFromFragment", notesList.get(position));
                        Intent intent = new Intent(AllNotesActivity.this, AddNoteActivity.class);
                        intent.putExtra("fragmentNote", bundle);
                        startActivityForResult(intent, 1);
                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {

                    }
                })
                .setSwipeOptionViews(R.id.del_item, R.id.share_item)
                .setSwipeable(R.id.card_item, R.id.bg, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        if (viewID == R.id.del_item) {
                            delDialog(notesList.get(position));
                        } else if (viewID == R.id.share_item) {
                            Notes notes = notesList.get(position);
                            Intent intent=new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                            intent.putExtra(Intent.EXTRA_TEXT, notes.getN_textcontent());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(Intent.createChooser(intent, getTitle()));
                        }
                    }
                });
    }

    private int loadByDate(String date){
        QueryBuilder<Notes> qb
                = notesDao.queryBuilder().where(NotesDao.Properties.N_add_date.eq(date));
        return qb.list().size();
    }

    //private删除便签
    private void delNotes(Notes notes, final String date){
        QueryBuilder<Notes> qb
                = notesDao.queryBuilder().where(NotesDao.Properties.N_id.eq(notes.getN_id()), NotesDao.Properties.N_pid.eq(user.getU_id()));
        qb.buildDelete().executeDeleteWithoutDetachingEntities();
        notesList.remove(notes);
        if (loadByDate(date)==0){
            QueryBuilder<AllInfo> qbInfo
                    = allInfoDao.queryBuilder().where(AllInfoDao.Properties.A_add_date.eq(date),AllInfoDao.Properties.A_pid.eq(user.getU_id()));
            allInfo = qbInfo.list().get(0);
            if (allInfo!=null){
                if (allInfo.getA_diarys() == 0 && allInfo.getA_cards() == 0 && allInfo.getA_bills() == 0) {
                    allInfoDao.delete(allInfo);
                } else {
                    allInfo.setA_notes(0);
                    allInfoDao.update(allInfo);
                }
            }
        }
    }
    //删除数据dialog
    private void delDialog(final Notes notes){
        int color = Color.parseColor("#546e7a");
        new MaterialDialog.Builder(this)
                .content("删除便签？")
                .positiveText("删除")
                .titleColor(color)
                .positiveColor(color)
                .negativeText("取消")
                .negativeColor(color)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if ("POSITIVE".equals(which.toString())) {
                            delNotes(notes, notes.getN_add_date());
                            toast("删除成功");
                            loadNotes();
                            myAdapter.reflesh(notesList);
                        }
                    }
                })
                .show();
    }

    /**
     * 自定义ViewHolder，需要继承RecyclerView.ViewHolder。
     * RecyclerView强制使用该模式
     */
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView item_num,item_content,item_date;
        LinearLayout num_bg;

        public ViewHolder(View itemView) {
            super(itemView);
            item_num=(TextView)itemView.findViewById(R.id.item_num);
            item_content=(TextView)itemView.findViewById(R.id.item_content);
            item_date=(TextView)itemView.findViewById(R.id.item_date);
            num_bg = (LinearLayout) itemView.findViewById(R.id.num_bg);
        }
    }
    class MyAdapter extends  RecyclerView.Adapter<ViewHolder>{

        private List<Notes> list;
        private int[] bg_color = {
                R.color.base_color,R.color.green_color,
                R.color.blue_color,R.color.pink_color,R.color.orange_color};
        private String top_date = "";
        private int bg_color_num = 0;//i<=bg_color.length=5

        public MyAdapter(List<Notes> list) {
            if (list==null){
                throw new IllegalArgumentException("list must not be null");
            }
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            //加载item子视图生成View对象
            View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.all_notes_item,viewGroup,false);
            //根据子视图创建ViewHolder对象
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {
            Notes notes = this.list.get(i);
            viewHolder.item_num.setText("No." + (i + 1));
            viewHolder.item_content.setText(notes.getN_textcontent());
            viewHolder.item_date.setText(notes.getN_add_date());
            String temp = "";
            if (i==0){
                top_date = notes.getN_add_date();
                viewHolder.num_bg.setBackgroundResource(bg_color[bg_color_num]);
            }else {
                temp = notes.getN_add_date();
                if (top_date.equals(temp)){
                    viewHolder.num_bg.setBackgroundResource(bg_color[bg_color_num]);
                }else {
                    top_date = temp;
                    if (bg_color_num<4){
                        bg_color_num++;
                    }else {
                        bg_color_num=0;
                    }
                    viewHolder.num_bg.setBackgroundResource(bg_color[bg_color_num]);
                }
            }


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void reflesh(List<Notes> notes){
            this.list.clear();
            this.list.addAll(notes);
            this.notifyDataSetChanged();
        }
    }

    private void toast(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
