package com.example.apple.lingyongqian.utils;


import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Button;

import com.example.apple.lingyongqian.R;


/**
 * 验证码倒计时
 *
 */
public class CaptchaTimeCount extends CountDownTimer {
	private Button validate_btn;
	private Context context;
	/**
	 * 验证码倒计时
	 * @param millisInFuture
	 * @param countDownInterval
	 * @param validate_btn 点击的按钮
	 * @param context
	 */
	public CaptchaTimeCount(long millisInFuture, long countDownInterval, Button validate_btn, Context context) {
	    super(millisInFuture, countDownInterval);
	    this.validate_btn=validate_btn;
	    this.context=context;
	}
	@Override//开始
	public void onTick(long millisUntilFinished) {
	    validate_btn.setClickable(false);
	    validate_btn.setText(millisUntilFinished / 1000 + "s后重发");//   改为  60秒倒计时
	    validate_btn.setTextColor(context.getResources().getColorStateList(R.color.color_ffffff)); //颜色
	    validate_btn.setBackgroundResource(R.drawable.time_button);//背景颜色
	}
	@Override
	public void onFinish() {
	    validate_btn.setClickable(true); 
	    validate_btn.setText(R.string.captcha_btn_resend);//@color/color_63b953
	    validate_btn.setTextColor(context.getResources().getColorStateList(R.color.window_background));
	    validate_btn.setBackgroundResource(R.drawable.login_code);
	}
	/**
	 * 重置
	 */
	public void reset(){
		 validate_btn.setClickable(true);
		 validate_btn.setText(R.string.validate_btn_hint);
		 validate_btn.setTextColor(context.getResources().getColorStateList(R.color.window_background));
		 validate_btn.setBackgroundResource(R.drawable.login_code);
	}
}
