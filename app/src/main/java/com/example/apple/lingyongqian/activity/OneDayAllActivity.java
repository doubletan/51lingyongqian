package com.example.apple.lingyongqian.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.dao.AllInfo;
import com.example.apple.lingyongqian.fragment.DayBillsFragment;
import com.example.apple.lingyongqian.fragment.DayCardFragment;
import com.example.apple.lingyongqian.fragment.DayDiaryFragment;
import com.example.apple.lingyongqian.fragment.DayNotesFragment;
import com.example.apple.lingyongqian.utils.StatusBarUtil;


public class OneDayAllActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView note,card,bill,diary;
    private Intent intent;
    private AllInfo allInfo;
    private FragmentManager fragmentManager;
    private Bundle bundle;
    private MyApp myApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_all);
        myApp = MyApp.getApp();
        myApp.addToList(this);
        intent = getIntent();
        bundle = intent.getBundleExtra("info_item");
        if (bundle!=null){
            allInfo = bundle.getParcelable("info");
            Log.i("---","OneDayAllActivity:"+allInfo.getA_add_date());
        }
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (notesFragment==null){
            notesFragment = new DayNotesFragment();
            notesFragment.setArguments(bundle);
            fragmentTransaction.add(R.id.fragmentLayout, notesFragment);
        }else {
            fragmentTransaction.show(notesFragment);
        }
        fragmentTransaction.commit();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        StatusBarUtil.setColor(this, Color.parseColor("#607d8b"), 0);
    }

    private void initView(){
        note = (ImageView) findViewById(R.id.note);
        card = (ImageView) findViewById(R.id.card);
        bill = (ImageView) findViewById(R.id.bill);
        diary = (ImageView) findViewById(R.id.diary);

        note.setOnClickListener(this);
        card.setOnClickListener(this);
        bill.setOnClickListener(this);
        diary.setOnClickListener(this);

    }

    private DayDiaryFragment diaryFragment;
    private DayNotesFragment notesFragment;
    private DayCardFragment cardFragment;
    private DayBillsFragment billsFragment;


    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (diaryFragment == null && fragment instanceof DayDiaryFragment) {
            diaryFragment = (DayDiaryFragment)fragment;
        }else if (billsFragment == null && fragment instanceof DayBillsFragment) {
            billsFragment = (DayBillsFragment)fragment;
        }else if (cardFragment == null && fragment instanceof DayCardFragment) {
            cardFragment = (DayCardFragment)fragment;
        }else if (notesFragment == null && fragment instanceof DayNotesFragment){
            notesFragment = (DayNotesFragment)fragment;
        }
    }

    private void changeColor(int id){
        note.setBackgroundResource(R.color.card_auto);
        card.setBackgroundResource(R.color.card_auto);
        bill.setBackgroundResource(R.color.card_auto);
        diary.setBackgroundResource(R.color.card_auto);
        switch (id){
            case R.id.note:
                note.setBackgroundResource(R.color.card_pressed);
                break;
            case R.id.card:
                card.setBackgroundResource(R.color.card_pressed);
                break;
            case R.id.bill:
                bill.setBackgroundResource(R.color.card_pressed);
                break;
            case R.id.diary:
                diary.setBackgroundResource(R.color.card_pressed);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (notesFragment != null) {
            fragmentTransaction.hide(notesFragment);
        }

        if (cardFragment != null) {
            fragmentTransaction.hide(cardFragment);
        }

        if (billsFragment != null) {
            fragmentTransaction.hide(billsFragment);
        }

        if (diaryFragment != null) {
            fragmentTransaction.hide(diaryFragment);
        }
        switch (id){
            case R.id.note:
                if (notesFragment==null){
                    notesFragment = new DayNotesFragment();
                    notesFragment.setArguments(bundle);
                    fragmentTransaction.add(R.id.fragmentLayout, notesFragment);
                }else {
                    fragmentTransaction.show(notesFragment);
                }
                break;
            case R.id.card:
                if (cardFragment==null){
                    cardFragment = new DayCardFragment();
                    cardFragment.setArguments(bundle);
                    fragmentTransaction.add(R.id.fragmentLayout, cardFragment);
                }else {
                    fragmentTransaction.show(cardFragment);
                }
                break;
            case R.id.bill:
                if (billsFragment==null){
                    billsFragment = new DayBillsFragment();
                    billsFragment.setArguments(bundle);
                    fragmentTransaction.add(R.id.fragmentLayout, billsFragment);
                }else {
                    fragmentTransaction.show(billsFragment);
                }
                break;
            case R.id.diary:
                if (diaryFragment==null){
                    diaryFragment = new DayDiaryFragment();
                    diaryFragment.setArguments(bundle);
                    fragmentTransaction.add(R.id.fragmentLayout, diaryFragment);
                }else {
                    fragmentTransaction.show(diaryFragment);
                }
                break;
        }
        fragmentTransaction.commit();
    }
}
