package com.example.apple.lingyongqian.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.utils.CaptchaTimeCount;
import com.example.apple.lingyongqian.utils.Constants;
import com.example.apple.lingyongqian.utils.SPUtils;
import com.example.apple.lingyongqian.utils.ToastUtils;
import com.example.apple.lingyongqian.xianjindai.util.CheckUtil;
import com.example.apple.lingyongqian.xianjindai.util.DeviceUtil;
import com.example.apple.lingyongqian.xianjindai.util.ExceptionUtil;
import com.example.apple.lingyongqian.xianjindai.view.MyProgressDialog;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Login2Activity extends AppCompatActivity {

    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.et_code)
    EditText etCode;
    @Bind(R.id.bt_getCode)
    Button btGetCode;
    @Bind(R.id.layout_code)
    RelativeLayout layoutCode;

    private CaptchaTimeCount captchaTimeCount;
    private String MessageCode = null;
    private String phone;
    private String code;
    private String getCode;
    private String savePhone;
    private TimeCount time;


    public static void launch(Context context) {
        context.startActivity(new Intent(context, Login2Activity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        ButterKnife.bind(this);
        // 构造CountDownTimer对象
        time = new TimeCount(60000, 1000);
    }

    @OnClick({R.id.bt_getCode, R.id.bt_Login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_getCode:
                getCodeMessage();
                break;
            case R.id.bt_Login:
                if (TextUtils.isEmpty(getCode)){
                    getCodeMessage();
                }else {
                    setLogin();
                }
                break;
        }
    }

    /**
     * 获取短信验证码
     */
    private void getCodeMessage() {
        if (DeviceUtil.IsNetWork(this) == false) {
            Toast.makeText(this, "网络未连接", Toast.LENGTH_SHORT).show();
            return;
        }
        phone = etPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_LONG).show();
            return;
        }
        if (!CheckUtil.isMobile(phone)) {
            Toast.makeText(this, "手机号输入错误", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject js1 = new JSONObject();
        final JSONObject js2 = new JSONObject();
        try {
            js1.put("username", phone);
            js1.put("password", "");
            js1.put("channel", Constants.channel);
            js1.put("qudao", Constants.channel1);
            js2.put("Register", js1);
        } catch (JSONException e) {
            ExceptionUtil.handleException(e);
        }

        final MyProgressDialog dialog = new MyProgressDialog(this, "登录中...", R.style.CustomDialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String URL = Constants.URL;
                String nameSpace = Constants.nameSpace;
                String method_Name = "QuickLgnMsg";
                String SOAP_ACTION = nameSpace + method_Name;
                SoapObject rpc = new SoapObject(nameSpace, method_Name);
                rpc.addProperty("strJson", js2.toString());
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.debug = true;
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.bodyOut = rpc;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                try {
                    transport.call(SOAP_ACTION, envelope);
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    String result = object.getProperty("QuickLgnMsgResult").toString();
                    if (!TextUtils.isEmpty(result) && result.startsWith("0,")) {
                        getCode = result.substring(2);
                        savePhone = phone;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                layoutCode.setVisibility(View.VISIBLE);
                                // 开始计时
                                time.start();
                                Toast.makeText(Login2Activity.this, "验证码发送成功", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        });
                    } else if (!TextUtils.isEmpty(result) && result.startsWith("1,")) {
                        MyApp.userId = result.substring(2);
                        SPUtils.put(Login2Activity.this, "userId", MyApp.userId);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                                overridePendingTransition(R.anim.login_in, R.anim.login_out);
                                Toast.makeText(Login2Activity.this, "登录成功", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Login2Activity.this, "登录失败", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        });
                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Login2Activity.this, "登录失败", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });
                }
            }
        }).start();
    }

    // 定义一个倒计时的内部类
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            btGetCode.setText("重新发送");
            btGetCode.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            btGetCode.setClickable(false);
            btGetCode.setText(millisUntilFinished / 1000 + "秒");
        }
    }

    /**
     * 登陆
     */

    private void setLogin() {
        if (DeviceUtil.IsNetWork(this) == false) {
            Toast.makeText(this, "网络未连接", Toast.LENGTH_SHORT).show();
            return;
        }

        phone = etPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_LONG).show();
            return;
        }
        if (!CheckUtil.isMobile(phone)) {
            Toast.makeText(this, "手机号输入错误", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(getCode)) {
            Toast.makeText(this, "请获取手机验证码", Toast.LENGTH_LONG).show();
            return;
        }

        code = etCode.getText().toString();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_LONG).show();
            return;
        }
        if (!code.equals(getCode)) {
            Toast.makeText(this, "验证码输入错误", Toast.LENGTH_LONG).show();
            return;
        }
        if (!phone.equals(savePhone)) {
            Toast.makeText(this, "手机号与验证码不匹配", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject js1 = new JSONObject();
        final JSONObject js2 = new JSONObject();
        try {
            js1.put("username", phone);
            js1.put("password", "");
            js1.put("channel", Constants.channel);
            js1.put("qudao", Constants.channel1);
            js2.put("Register", js1);
        } catch (JSONException e) {
            ExceptionUtil.handleException(e);
        }

        final MyProgressDialog dialog = new MyProgressDialog(this, "登录中...", R.style.CustomDialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String URL = Constants.URL;
                String nameSpace = Constants.nameSpace;
                String method_Name = "QuickLgn";
                String SOAP_ACTION = nameSpace + method_Name;
                SoapObject rpc = new SoapObject(nameSpace, method_Name);
                rpc.addProperty("strJson", js2.toString());
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.debug = true;
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.bodyOut = rpc;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                try {
                    transport.call(SOAP_ACTION, envelope);
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    String result = object.getProperty("QuickLgnResult").toString();
                    if (!TextUtils.isEmpty(result) && result.startsWith("0,")) {
                        MyApp.userId = result.substring(2);
                        SPUtils.put(Login2Activity.this, "userId", MyApp.userId);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Login2Activity.this, "登录成功", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                                finish();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Login2Activity.this, "登录失败", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        });
                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Login2Activity.this, "登录失败", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });
                }
            }
        }).start();

    }

    private long mLastBackTime = 0;

    @Override
    public void onBackPressed() {
        // finish while click back key 2 times during 1s.
        if ((System.currentTimeMillis() - mLastBackTime) < 1000) {
            finish();
            MyApp.getApp().onTerminate();
        } else {
            mLastBackTime = System.currentTimeMillis();
            ToastUtils.showToast(this, "再点一次退出程序");
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
