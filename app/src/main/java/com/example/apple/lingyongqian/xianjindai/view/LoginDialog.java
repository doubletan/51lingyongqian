package com.example.apple.lingyongqian.xianjindai.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.example.apple.lingyongqian.R;


/**
 * Created by tantan on 2017/7/13.
 */

public class LoginDialog extends Dialog{
    public LoginDialog(Context context, int theme) {
        super(context, theme);
    }

    public LoginDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_tips);
    }
}
