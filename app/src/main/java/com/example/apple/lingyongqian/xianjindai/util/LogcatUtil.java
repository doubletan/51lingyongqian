package com.example.apple.lingyongqian.xianjindai.util;


import android.util.Log;

import com.example.apple.lingyongqian.activity.MyApp;


public class LogcatUtil {

	public static void printLogcat(String log) {
		if (MyApp.isRelease) {
			return;
		} else {
			Log.i("手机借款", log);
		}
	}
}
