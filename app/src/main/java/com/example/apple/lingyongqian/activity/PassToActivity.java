package com.example.apple.lingyongqian.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.utils.FastBlur;


public class PassToActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText password;
    private Button login_bt;
    private String pass;
    private View login_form;
    private ImageView zoom_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_to);
        initView();
        loginFormBlur();
    }

    private void initView(){
        password = (EditText) findViewById(R.id.password);
        login_bt = (Button) findViewById(R.id.login_bt);
        login_bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        pass = password.getText().toString().trim();
        SharedPreferences sharedPreferences = super.getSharedPreferences("says", Context.MODE_PRIVATE);
        String p = sharedPreferences.getString("person_pass", "");
        if (p.equals(pass)){
            Intent intent = new Intent(PassToActivity.this,MainActivity.class);
            intent.putExtra("ok","yes");
            startActivity(intent);
            finish();
        }else {
            toast("密码错误");
            return;
        }
    }
    //zoom背景模糊
    private void loginFormBlur() {
        zoom_img = (ImageView) findViewById(R.id.set_top_img);
        login_form = findViewById(R.id.login_form);
        zoom_img.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                zoom_img.getViewTreeObserver().removeOnPreDrawListener(this);
                zoom_img.buildDrawingCache();

                Bitmap bmp = zoom_img.getDrawingCache();
                blur(bmp, login_form, 8, 20);
                return true;
            }
        });
    }

    //图片模糊
    private void blur(Bitmap bkg, View view, float scaleFactor, float radius) {
        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);
        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        view.setBackground(new BitmapDrawable(getResources(), overlay));
    }
    private void toast(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
