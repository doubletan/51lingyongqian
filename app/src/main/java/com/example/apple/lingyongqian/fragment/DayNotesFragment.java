package com.example.apple.lingyongqian.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.example.apple.lingyongqian.activity.AddNoteActivity;
import com.example.apple.lingyongqian.activity.MyApp;
import com.example.apple.lingyongqian.dao.AllInfo;
import com.example.apple.lingyongqian.dao.AllInfoDao;
import com.example.apple.lingyongqian.dao.DaoSession;
import com.example.apple.lingyongqian.dao.Notes;
import com.example.apple.lingyongqian.dao.NotesDao;
import com.example.apple.lingyongqian.dao.User;
import com.example.apple.lingyongqian.utils.ExStaggeredGridLayoutManager;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class DayNotesFragment extends Fragment implements View.OnClickListener {


    private View view,no_content;
    private ImageView add_img;
    private String date = "";//条目日期
    private RecyclerView recyclerView;

    private MyApp myApp;
    private User user;
    private DaoSession daoSession;
    private NotesDao notesDao;
    private Notes notes;
    private List<Notes> list = new ArrayList<>();
    private MyAdapter adapter;
    private TextView toolbar_title,num;
    private ImageView add,back;

    private Bundle bundle;
    private RecyclerTouchListener onTouchListener;
    private AllInfoDao allInfoDao;
    private AllInfo info;

    public DayNotesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.day_each_layout,null);
        toolbar_title = (TextView) view.findViewById(R.id.date);
        back = (ImageView) view.findViewById(R.id.back);
        add = (ImageView) view.findViewById(R.id.add_day);
        num = (TextView) view.findViewById(R.id.num);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        no_content = view.findViewById(R.id.no_contents);

        bundle = getArguments();

        back.setOnClickListener(this);
        add.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("---","onActivityCreated");
        if (bundle!=null){
            info = bundle.getParcelable("info");
            date = info.getA_add_date();
            Log.i("---",""+info.getA_diarys()+info.getA_bills()+info.getA_cards()+info.getA_notes());
        }
        loadNotes();
        showNotes();
        checkList();
        adapter = new MyAdapter(list);
        ExStaggeredGridLayoutManager sgm = new ExStaggeredGridLayoutManager(2,ExStaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(sgm);
        //为RecyclerView设置适配器
        recyclerView.setAdapter(adapter);
        onTouchListener = new RecyclerTouchListener(getActivity(), recyclerView);
        recyclerView.addOnItemTouchListener(onTouchListener);
        onTouchListener
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("noteFromFragment", list.get(position));
                        Intent intent = new Intent(getActivity(), AddNoteActivity.class);
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
                        if (viewID == R.id.del_item){
                            delDialog(list.get(position));
                        }else if (viewID == R.id.share_item){
                            Notes notes = list.get(position);
                            Intent intent=new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                            intent.putExtra(Intent.EXTRA_TEXT, notes.getN_textcontent());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(Intent.createChooser(intent, getActivity().getTitle()));
                        }
                    }
                });
    }

    //private删除便签
    private void delNotes(Notes notes, final String date){
        QueryBuilder<Notes> qb
                = notesDao.queryBuilder().where(NotesDao.Properties.N_id.eq(notes.getN_id()), NotesDao.Properties.N_pid.eq(user.getU_id()));
        qb.buildDelete().executeDeleteWithoutDetachingEntities();
        list.remove(notes);
        if (list.size()==0){
            info.setA_notes(0);
            allInfoDao.update(info);
            recyclerView.setVisibility(View.GONE);
            no_content.setVisibility(View.VISIBLE);
        }
        num.setText("共 "+list.size()+" 条便签");
    }
    //删除数据dialog
    private void delDialog(final Notes notes){
        int color = Color.parseColor("#546e7a");
        new MaterialDialog.Builder(getContext())
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
                            Toast.makeText(getContext(),"删除成功",Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                        }
                    }
                })
                .show();
    }

    private void loadNotes(){
        myApp = MyApp.getApp();
        user = MyApp.user1;
        daoSession = myApp.getDaoSession(getContext());
        allInfoDao = daoSession.getAllInfoDao();
        notesDao = daoSession.getNotesDao();
        list = loadNoteByDate(date);
        num.setText("共 "+list.size()+" 条便签");
    }
    //判断list是否为空
    private void checkList(){
        if (list.size()==0){
            recyclerView.setVisibility(View.GONE);
            no_content.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            no_content.setVisibility(View.GONE);
        }
    }
    //show notes
    private void showNotes(){
        toolbar_title.setText("便签：" + date);

    }



    //根据时间读取便签
    private List<Notes> loadNoteByDate(String date){
        List<Notes> list = new ArrayList<>();
        QueryBuilder builder = notesDao.queryBuilder();
        builder.where(NotesDao.Properties.N_add_date.eq(date),NotesDao.Properties.N_pid.eq(user.getU_id())).build();
        list = builder.orderDesc(NotesDao.Properties.N_id).list();
        Log.i("---","list size: "+list.size());
        return list;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.add_day:
                //添加该日期下的便签
                Intent intent = new Intent(getActivity(), AddNoteActivity.class);
                intent.putExtra("fragmentAddNote", date);
                startActivityForResult(intent, 2);
                break;
            case R.id.back:

                onDestroy();
                getActivity().finish();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("---", "恢复fragment");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("---", resultCode + "");
        List temp = loadNoteByDate(date);
      //  list.clear();
       // list.addAll(temp);
        if (temp.size()==0){
            recyclerView.setVisibility(View.GONE);
            no_content.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            no_content.setVisibility(View.GONE);
        }
        num.setText("共 "+temp.size()+" 条便签");
        adapter.reflesh(temp);
    }

    /**
     * 自定义ViewHolder，需要继承RecyclerView.ViewHolder。
     * RecyclerView强制使用该模式
     */
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView date,content;

        public ViewHolder(View itemView) {
            super(itemView);
            date=(TextView)itemView.findViewById(R.id.item_addtime);
            content=(TextView)itemView.findViewById(R.id.item_content);
        }
    }
    class MyAdapter extends  RecyclerView.Adapter<ViewHolder>{

        private List<Notes> list;

        public MyAdapter(List<Notes> list) {
            if (list==null){
                throw new IllegalArgumentException("list must not be null");
            }
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            //加载item子视图生成View对象
            View view = LayoutInflater.from(getContext()).inflate(R.layout.day_notes_recycler_item,viewGroup,false);
            //根据子视图创建ViewHolder对象
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {
            Notes notes = this.list.get(i);
            viewHolder.date.setText("No." + (i + 1));
            viewHolder.content.setText(notes.getN_textcontent());
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
}
