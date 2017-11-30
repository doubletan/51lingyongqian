package com.example.apple.lingyongqian.net;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/5.
 */
public class VolleyStringPost extends StringRequest {
    private Map map;
    public VolleyStringPost(String url,
                            Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        //Method.POST是post请求
        //这里系统提供了两个构造，另一个构造中有int method参数，0：get,1:post,默认是get
        //这里选择无int method的构造，但在super()中传入了Method.POST（系统值，值为1），也就规定了该方法就是post方式
        //这样的好处，不用再传入int method参数
        super(Method.POST,url, listener, errorListener);
        map = new HashMap();
    }


    public void putValues(String key, String values){
        map.put(key,values);
    }

    //post方式必须重写该方法
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
