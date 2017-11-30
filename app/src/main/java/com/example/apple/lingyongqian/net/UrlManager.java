package com.example.apple.lingyongqian.net;

/**
 * Created by Administrator on 2016/7/13.
 */
public class UrlManager {
    public static final String BASE_URL = "http://192.168.191.1:8080/EasyWork";

    //flag = 1--注册,2--登陆,3--修改user信息/servlet/DiaryServlet
    public static final String USER_SERVLET = BASE_URL+"/servlet/UserServlet";

    //日记的缓存与加载
    public static final String DIARY_SERVLET = BASE_URL+"/servlet/DiaryServlet";

}
