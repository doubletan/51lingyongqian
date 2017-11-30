package com.example.apple.lingyongqian.xianjindai.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.activity.Login2Activity;
import com.example.apple.lingyongqian.activity.MyApp;
import com.example.apple.lingyongqian.utils.SPUtils;
import com.example.apple.lingyongqian.xianjindai.entity.UpdataInfo;
import com.example.apple.lingyongqian.xianjindai.fragment.MainFragment;
import com.example.apple.lingyongqian.xianjindai.fragment.MeFragment;
import com.example.apple.lingyongqian.xianjindai.fragment.NewFragment;
import com.example.apple.lingyongqian.xianjindai.util.ExceptionUtil;
import com.umeng.analytics.MobclickAgent;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class XianJinDai extends AppCompatActivity {

    // 版本更新
    private final String TAG = this.getClass().getName();
    private final int UPDATA_NONEED = 10;
    private final int UPDATA_CLIENT = 11;
    private final int GET_UNDATAINFO_ERROR = 12;
    private final int DOWN_ERROR = 13;
    private UpdataInfo info;
    private int localVersion;

    @Bind(R.id.main_activity_fragment_container)
    LinearLayout mainActivityFragmentContainer;
    @Bind(R.id.main_activity_buttom_btn1)
    Button mainActivityButtomBtn1;
    @Bind(R.id.main_activity_buttom_rl1)
    RelativeLayout mainActivityButtomRl1;
    @Bind(R.id.main_activity_buttom_btn2)
    Button mainActivityButtomBtn2;
    @Bind(R.id.main_activity_buttom_rl2)
    RelativeLayout mainActivityButtomRl2;
    @Bind(R.id.main_activity_buttom_btn3)
    Button mainActivityButtomBtn3;
    @Bind(R.id.main_activity_buttom_rl3)
    RelativeLayout mainActivityButtomRl3;
    @Bind(R.id.main_activity_buttom_ll)
    LinearLayout mainActivityButtomLl;
    @Bind(R.id.activity_main)
    LinearLayout activityMain;


    private Button[] btnArray = new Button[3];
    private MainFragment main;
    private NewFragment New;
    private MeFragment me;
    private Fragment[] fragments;
    private long mLastBackTime;
    private int selectedIndex;
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xianjindai);
        ButterKnife.bind(this);
        try {
            MyApp.getApp().addToList(this);
            //设置控件
            setViews();
            //设置监听
            setListener();
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }

    private void setListener() {

    }

    private void setViews() {

        if (!SPUtils.contains(this, "userId")) {
            new Handler().postDelayed(new Runnable(){
                public void run() {
                    login();
                }
            }, 500);
        } else {
            MyApp.userId = (String) SPUtils.get(this, "userId", "");
        }

        btnArray[0] = (Button) findViewById(R.id.main_activity_buttom_btn1);
        btnArray[1] = (Button) findViewById(R.id.main_activity_buttom_btn2);
        btnArray[2] = (Button) findViewById(R.id.main_activity_buttom_btn3);
        btnArray[0].setSelected(true);

        main = new MainFragment();
        New = new NewFragment();
        me = new MeFragment();
        fragments = new Fragment[]{main, New, me};
        // 一开始，显示第一个fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.main_activity_fragment_container, main);
        transaction.show(main);
        transaction.commit();
    }


    @OnClick({R.id.main_activity_buttom_rl1, R.id.main_activity_buttom_rl2, R.id.main_activity_buttom_rl3})
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.main_activity_buttom_rl1:
                    selectedIndex = 0;
                    break;
                case R.id.main_activity_buttom_rl2:
                    selectedIndex = 1;
                    break;
                case R.id.main_activity_buttom_rl3:
                    selectedIndex = 2;
                    break;
            }
            // 判断单击是不是当前的
            if (selectedIndex != currentIndex) {
                // 不是当前的
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                // 当前hide
                transaction.hide(fragments[currentIndex]);
                // show你选中

                if (!fragments[selectedIndex].isAdded()) {
                    // 以前没添加过
                    transaction.add(R.id.main_activity_fragment_container, fragments[selectedIndex]);
                }
                // 事务
                transaction.show(fragments[selectedIndex]);
                transaction.commit();

                btnArray[currentIndex].setSelected(false);
                btnArray[selectedIndex].setSelected(true);
                currentIndex = selectedIndex;
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }

    public void login() {
        Intent intent = new Intent(this, Login2Activity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.login_in, R.anim.login_out);
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
        // finish while click back key 2 times during 1s.
        if ((System.currentTimeMillis() - mLastBackTime) < 2000) {
            finish();
            MobclickAgent.onKillProcess(this);
            System.exit(0);
        } else {
            mLastBackTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
        }
    }

}
