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
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.dao.DaoSession;
import com.example.apple.lingyongqian.dao.User;
import com.example.apple.lingyongqian.dao.UserDao;
import com.example.apple.lingyongqian.utils.BitMapUtil;
import com.example.apple.lingyongqian.utils.FastBlur;
import com.example.apple.lingyongqian.utils.FileUitlity;
import com.example.apple.lingyongqian.utils.StatusBarUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView topImg;
    private View set_top,passWord,my_info,checkVersion;
    private View setting_top;
    private Button login_bt;
    private TextView userName,my_says;
    private ImageView headImg;

    private MyApp myApp;
    private User user;
    private DaoSession daoSession;
    private UserDao userDao;
    private SharedPreferences sharedPreferences;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        myApp = MyApp.getApp();
        myApp.addToList(this);
        user = MyApp.user1;
        sharedPreferences = super.getSharedPreferences("says", Context.MODE_PRIVATE);
        daoSession = myApp.getDaoSession(this);
        userDao = daoSession.getUserDao();
        initView();
    }
    @Override
    public void onStart() {
        super.onStart();
        StatusBarUtil.setColor(this, Color.parseColor("#607d8b"), 0);
        user = MyApp.user1;
        userName.setText(user.getU_name());
        my_says.setText(getSay());
    }
    //获得签名
    private String getSay(){
        return sharedPreferences.getString("person_say","");
    }

    private void initView(){
        setting_top = findViewById(R.id.setting_top);
        topImg = (ImageView) findViewById(R.id.set_top_img);
        passWord = findViewById(R.id.passWord);//访问密码设置
        userName = (TextView) findViewById(R.id.title);
        my_info = findViewById(R.id.my_info);
        my_says = (TextView) findViewById(R.id.my_says);
        headImg = (ImageView) findViewById(R.id.headImg);
        checkVersion = findViewById(R.id.checkVersion);

        userName.setText(user.getU_name());
        if (!"".equals(user.getU_img())){
            //Bitmap bitmap = getLoacalBitmap(user.getU_img());
            Bitmap bitmap1 = BitMapUtil.getBitmap(user.getU_img(),100,100);
            headImg.setImageBitmap(bitmap1);
        }

        passWord.setOnClickListener(this);
        my_info.setOnClickListener(this);
        headImg.setOnClickListener(this);
        my_says.setOnClickListener(this);
        userName.setOnClickListener(this);
        checkVersion.setOnClickListener(this);

        setHeight();//设置top高度
        zoomApplyBlur();//模糊背景

    }
    //设置高度
    private void setHeight(){
        //获取屏幕高宽
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int windowsHeight = metric.heightPixels;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) setting_top.getLayoutParams();
        params.height = windowsHeight/3;
        setting_top.setLayoutParams(params);
    }
    //zoom背景模糊
    private void zoomApplyBlur() {
        topImg.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                topImg.getViewTreeObserver().removeOnPreDrawListener(this);
                topImg.buildDrawingCache();

                Bitmap bmp = topImg.getDrawingCache();
                blur(bmp, setting_top);
                return true;
            }
        });
    }
    //图片模糊
    private void blur(Bitmap bkg, View view) {
        float scaleFactor = 6;
        float radius = 20;
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.passWord:
                Intent intent3 = new Intent(SettingActivity.this,BaseSettingActivity.class);
                startActivity(intent3);
                break;
            case R.id.my_info:
                Intent intent = new Intent(SettingActivity.this,UserBaseInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.title:
                Intent intent1 = new Intent(SettingActivity.this,UserBaseInfoActivity.class);
                startActivity(intent1);
                break;
            case R.id.headImg:
                changeHeadImg();
                break;
            case R.id.my_says:
                Intent intent2 = new Intent(SettingActivity.this,UserBaseInfoActivity.class);
                startActivity(intent2);
                break;
            case R.id.checkVersion:
                toast("已是最新版本");
                break;
        }
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyUp(keyCode, event);
    }
    /**
     * 加载本地图片
     * @param url
     * @return
     */
    public Bitmap getLoacalBitmap(String url) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //dialog
    private MaterialDialog dialog = null;
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
    private String path = "";
    private String img_name = "";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //如果返回码不为-1，则表示不成功
        if (resultCode != Activity.RESULT_OK){
            return;
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
                headImg.setImageBitmap(bitmap);
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
        startActivityForResult(intent, 2);
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
    private void toast(String str){
        Toast.makeText(this,str, Toast.LENGTH_SHORT).show();
    }
}
