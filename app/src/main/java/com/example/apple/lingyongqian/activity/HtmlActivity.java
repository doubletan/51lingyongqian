package com.example.apple.lingyongqian.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.xianjindai.util.PopupWindowUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class HtmlActivity extends AppCompatActivity {


    @Bind(R.id.webView)
    WebView webView;
    @Bind(R.id.bar)
    ProgressBar bar;

    private ValueCallback mFilePathCallback;
    private final int REQUEST_CODE_TAKE_PICETURE = 11;
    private final int REQUEST_CODE_PICK_PHOTO = 12;
    private final int REQUEST_CODE_CAMERA_PERMISSION = 13;
    private final int REQUEST_CODE_READ_EXTERNAL_PERMISSION = 14;
    private File vFile;
    private Uri origUri;

    public static void launch(Context context) {
        context.startActivity(new Intent(context, HtmlActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html);
        ButterKnife.bind(this);
        CheckInternet();
    }

    private void CheckInternet() {
            ConnectivityManager con= (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
            boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
            boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
            if(wifi|internet){
                //执行相关操作
                getDate();

            }else{
                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("网络异常，请检查网络")
                     .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                         @Override
                         public void onClick(SweetAlertDialog sweetAlertDialog) {
                                finish();
                         }
                     })
                        .show();


            }
    }

    private void getDate() {
        String html = getIntent().getStringExtra("html");
        if(html!=null){
            webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            webView.setWebChromeClient(new WebChromeClient());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setAppCachePath(getCacheDir().getPath());
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.getSettings().setAppCacheEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setSupportZoom(false);
            webView.loadUrl(html);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return false;
                }

                /**
                 * h5打开相机或相册
                 */
                // Andorid 4.1+
                public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
                    try {
                        showMyDialog();
                        mFilePathCallback = uploadFile;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // Andorid 3.0 +
                public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType) {
                    try {
                        showMyDialog();
                        mFilePathCallback = uploadFile;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // Android 3.0
                public void openFileChooser(ValueCallback<Uri> uploadFile) {
                    try {
                        showMyDialog();
                        mFilePathCallback = uploadFile;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            webView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if (keyEvent.getAction() == keyEvent.ACTION_DOWN) {

                        if (i == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                            webView.goBack();
                            return true;
                        }
                    }
                    return false;
                }
            });
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    // TODO 自动生成的方法存根

                    if (newProgress == 100) {
                        bar.setVisibility(View.GONE);//加载完网页进度条消失
                    } else {
                        bar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                        bar.setProgress(newProgress);//设置进度值
                    }
                }
            });
        }
        }


    private void showMyDialog() {
        View rootView=getLayoutInflater().inflate(R.layout.activity_main,null);
        PopupWindowUtil.showPopupWindow(this,rootView,"相机","文件","取消",
                new PopupWindowUtil.onPupupWindowOnClickListener() {
                    @Override
                    public void onFirstButtonClick() {
                        int flag1 = ActivityCompat.checkSelfPermission(HtmlActivity.this, Manifest.permission.CAMERA);
                        int flag2 = ActivityCompat.checkSelfPermission(HtmlActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (PackageManager.PERMISSION_GRANTED != flag1||PackageManager.PERMISSION_GRANTED != flag2) {
                            ActivityCompat.requestPermissions(HtmlActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_CAMERA_PERMISSION);
                            cancelFilePathCallback();
                        }else {
                            takeForPicture();
                        }
                    }

                    @Override
                    public void onSecondButtonClick() {
                        int flag2 = ActivityCompat.checkSelfPermission(HtmlActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (PackageManager.PERMISSION_GRANTED != flag2) {
                            ActivityCompat.requestPermissions(HtmlActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_PERMISSION);
                            cancelFilePathCallback();
                        }else {
                            takeForPhoto();
                        }
                    }

                    @Override
                    public void onCancleButtonClick() {
                        cancelFilePathCallback();
                    }
                });
    }

    /**
     * 调用相册
     */
    private void takeForPhoto() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),REQUEST_CODE_PICK_PHOTO);
        } else {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),REQUEST_CODE_PICK_PHOTO);
        }
    }


    /**
     * 调用相机
     */
    private void takeForPicture() {

        try {
            String storageState = Environment.getExternalStorageState();
            if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                vFile = new File(Environment.getExternalStorageDirectory().getPath()
                        + "/xianjindai/");//图片位置
                if (!vFile.exists()) {
                    vFile.mkdirs();
                }
            } else {
                Toast.makeText(this, "未挂载sdcard", Toast.LENGTH_LONG).show();
                return;
            }

            String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new
                    Date()) + ".jpg";

            Uri uri = Uri.fromFile(new File(vFile, fileName));

            //拍照所存路径
            origUri = uri;

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (Build.VERSION.SDK_INT > 23) {//7.0及以上
//            Uri contentUri = getUriForFile(HtmlActivity.this, "com.xinhe.crame", picturefile);
//            grantUriPermission(getPackageName(),contentUri,Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
//        } else {//7.0以下
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picturefile));
//        }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, origUri);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICETURE);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void cancelFilePathCallback() {
        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(null);
            mFilePathCallback = null;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webView.destroy();
    }


}
