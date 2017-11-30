package com.example.apple.lingyongqian.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.fragment.AboutFragment;
import com.example.apple.lingyongqian.fragment.FristFragment;
import com.example.apple.lingyongqian.utils.DoubleClickExit;
import com.umeng.analytics.MobclickAgent;


import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {
    private ViewPager mViewPager;
    private List<Fragment> fragmentList;
    private BottomNavigationView mNavigationView;
    public static void launch(Context context) {
        context.startActivity(new Intent(context, Main2Activity.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initWeb();

    }


    private void initWeb() {
        fragmentList = new ArrayList<>();
        fragmentList.add(FristFragment.newInstance());
        fragmentList.add(AboutFragment.newInstance("about"));
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mNavigationView = (BottomNavigationView) findViewById(R.id.bye_burger);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override public int getCount() {
                return fragmentList.size();
            }
        });
        mNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if(item.getTitle().equals("home")){
                            mViewPager.setCurrentItem(0);
                        }else if(item.getTitle().equals("about")){
                            mViewPager.setCurrentItem(1);
                        }
                        return false;
                    }
                });
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    @Override
    public void onBackPressed() {
        if (!DoubleClickExit.check()) {
            Toast.makeText(this,"再按一次退出",Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }
}
