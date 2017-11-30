package com.example.apple.lingyongqian.xianjindai.util;


import com.example.apple.lingyongqian.activity.MyApp;

/**
 * 异常统一处理
 *
 * @author tarena
 *
 */
public class ExceptionUtil {
	public static void handleException(Exception e) {
		if (MyApp.isRelease) {
			return;
		} else {
			e.printStackTrace();
		}
	}

}
