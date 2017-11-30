package com.example.apple.lingyongqian.fragment;


import android.app.Activity;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.utils.SPUtils;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class FristFragment extends Fragment {


    @Bind(R.id.mWebView)
    WebView mWebView;


    public static FristFragment newInstance() {
        Bundle args = new Bundle();
        FristFragment fragment = new FristFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public FristFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frist, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        String url = (String) SPUtils.get(getActivity(), "url", "");
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setSaveFormData(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAppCachePath(getActivity().getCacheDir().getPath());
        TextNet();
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return true;
            }
        });
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction()==keyEvent.ACTION_DOWN){
                    if (i == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
                        mWebView.goBack();
                        return true;
                    }
                }
                return false;
            }
        });
    }
    private void TextNet() {
        ConnectivityManager con=(ConnectivityManager)getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
        boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        if(wifi|internet){
            //执行相关操作
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        }else{
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            Toast.makeText(getActivity(),
                    "亲，网络连接失败咯！", Toast.LENGTH_LONG)
                    .show();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mWebView.removeAllViews();
        mWebView.destroy();
        ButterKnife.unbind(this);
    }
}
