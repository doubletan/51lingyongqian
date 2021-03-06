package com.example.apple.lingyongqian.xianjindai.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.activity.MyApp;
import com.example.apple.lingyongqian.utils.Constants;
import com.example.apple.lingyongqian.xianjindai.activity.WebViewActivity;
import com.example.apple.lingyongqian.xianjindai.adapter.ProductAdapter;
import com.example.apple.lingyongqian.xianjindai.biz.BrowsingHistory;
import com.example.apple.lingyongqian.xianjindai.entity.Product;
import com.example.apple.lingyongqian.xianjindai.util.DeviceUtil;
import com.example.apple.lingyongqian.xianjindai.util.ExceptionUtil;
import com.example.apple.lingyongqian.xianjindai.view.MyProgressDialog;
import com.google.gson.Gson;
import com.mancj.slideup.SlideUp;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class MainFragment extends Fragment {


    @Bind(R.id.main_fragment_tv1)
    TextView mainFragmentTv1;
    @Bind(R.id.main_fragment_iv1)
    ImageView mainFragmentIv1;
    @Bind(R.id.main_fragment_rl1)
    RelativeLayout mainFragmentRl1;
    @Bind(R.id.main_fragment_tv2)
    TextView mainFragmentTv2;
    @Bind(R.id.main_fragment_rl2)
    RelativeLayout mainFragmentRl2;
    @Bind(R.id.main_fragment_rv)
    RecyclerView mainFragmentRv;
    @Bind(R.id.product_choose_pop_btn1)
    Button productChoosePopBtn1;
    @Bind(R.id.product_choose_pop_btn2)
    Button productChoosePopBtn2;
    @Bind(R.id.product_choose_pop_btn3)
    Button productChoosePopBtn3;
    @Bind(R.id.product_choose_pop_btn4)
    Button productChoosePopBtn4;
    @Bind(R.id.product_choose_pop)
    LinearLayout productChoosePop;
    @Bind(R.id.product_choose_pop_rl)
    RelativeLayout productChoosePopRl;
    private View view;
    private RotateAnimation reverseAnimation;
    private int reverseCount = 0;
    private SlideUp slideUp;
    private int buttonPosition;
    private ArrayList<Product.PrdListProduct> products = new ArrayList<Product.PrdListProduct>();
    private ProductAdapter adapter;
    private ArrayList<Product.PrdListProduct> products1 = new ArrayList<Product.PrdListProduct>();
    private ArrayList<Product.PrdListProduct> products2 = new ArrayList<Product.PrdListProduct>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            //加载界面
            view = View.inflate(getActivity(), R.layout.main_fragment, null);
            ButterKnife.bind(this, view);
            //初始化控件
            initView();
            //设置数据
            setViews();
            //监听
            setListener();
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }

        ButterKnife.bind(this, view);
        return view;
    }

    private void setListener() {
        mainFragmentRv.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent=new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("url",products.get(position).getLink());
                startActivity(intent);
                if ("0".equals(buttonPosition)){
                    new BrowsingHistory().execute(products.get(position).getUid(),"");
                }else {
                    new BrowsingHistory().execute(products.get(position).getUid(),"");
                }
            }
        });
    }

    private void setViews() {
        //创建默认的线性LayoutManager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mainFragmentRv.setLayoutManager(mLayoutManager);
        if (null == MyApp.product) {
            getProduct1("31");
        } else {
            products1 = MyApp.product.getPrdList();
            products.addAll(products1);
        }
        adapter = new ProductAdapter(products);
        mainFragmentRv.setAdapter(adapter);

    }

    private void getProduct1(final String s) {
        if (DeviceUtil.IsNetWork(getContext()) == false) {
            Toast.makeText(getContext(), "网络未连接", Toast.LENGTH_SHORT).show();
            return;
        }
        final MyProgressDialog dialog = new MyProgressDialog(getContext(), "获取中...", R.style.CustomDialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String URL = Constants.URL;
                String nameSpace = Constants.nameSpace;
                String method_Name = "GetProduct";
                String SOAP_ACTION = nameSpace + method_Name;
                SoapObject rpc = new SoapObject(nameSpace, method_Name);
                rpc.addProperty("sAppName", Constants.appName);
                rpc.addProperty("sPage", s);
                rpc.addProperty("channel", "3");
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.debug = true;
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.bodyOut = rpc;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                try {
                    transport.call(SOAP_ACTION, envelope);
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    final String str = object.getProperty("GetProductResult").toString();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(str) && !str.startsWith("1")&& !str.startsWith("2")) {
                                products.clear();
                                Gson gson = new Gson();
                                Product product = gson.fromJson(str, Product.class);
                                products1 = product.getPrdList();
                                products.addAll(products1);
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    });

                } catch (final Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ExceptionUtil.handleException(e);
                            dialog.dismiss();
                            Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();
    }

    private void getProduct2() {
        if (DeviceUtil.IsNetWork(getContext()) == false) {
            Toast.makeText(getContext(), "网络未连接", Toast.LENGTH_SHORT).show();
            return;
        }
        final MyProgressDialog dialog = new MyProgressDialog(getContext(), "获取中...", R.style.CustomDialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String URL = Constants.URL;
                String nameSpace = Constants.nameSpace;
                String method_Name = "GetProduct";
                String SOAP_ACTION = nameSpace + method_Name;
                SoapObject rpc = new SoapObject(nameSpace, method_Name);
                rpc.addProperty("sAppName", Constants.appName);
                rpc.addProperty("sPage", "32");
                rpc.addProperty("channel", "3");
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.debug = true;
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.bodyOut = rpc;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                try {
                    transport.call(SOAP_ACTION, envelope);
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    final String str = object.getProperty("GetProductResult").toString();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(str) && !str.startsWith("1")&& !str.startsWith("2")) {
                                products.clear();
                                Gson gson = new Gson();
                                Product product = gson.fromJson(str, Product.class);
                                products2 = product.getPrdList();
                                products.addAll(products2);
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    });

                } catch (Exception e) {
                    ExceptionUtil.handleException(e);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    });

                }
            }
        }).start();
    }

    private void initView() {
        //筛选的第一个按钮默认选中
        productChoosePopBtn1.setSelected(true);
        // 翻转动画
        reverseAnimation = (RotateAnimation) AnimationUtils.loadAnimation(getContext(), R.anim.reverse_anim);
        LinearInterpolator lir = new LinearInterpolator();
        reverseAnimation.setInterpolator(lir);
        //选择pop
        slideUp = new SlideUp.Builder(productChoosePop)
                .withStartGravity(Gravity.TOP)
                .withLoggingEnabled(true)
                .withGesturesEnabled(false)
                .withStartState(SlideUp.State.HIDDEN)
                .build();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.main_fragment_rl1, R.id.product_choose_pop_rl, R.id.main_fragment_rl2, R.id.product_choose_pop_btn1, R.id.product_choose_pop_btn2, R.id.product_choose_pop_btn3, R.id.product_choose_pop_btn4})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_fragment_rl1:
                if (buttonPosition == 1) {
                    mainFragmentTv1.setTextColor(getResources().getColor(R.color.text_color_huang));
                    mainFragmentTv2.setTextColor(getResources().getColor(R.color.text_color_32));
                    mainFragmentIv1.setImageResource(R.mipmap.main_fragment1);
                    buttonPosition = 0;
                    //切换数据
                    products.clear();
                    products.addAll(products1);
                    adapter.notifyDataSetChanged();
                }
                if (reverseCount == 0) {
                    mainFragmentIv1.startAnimation(reverseAnimation);
                    reverseCount = 1;
                    slideUp.show();
                } else {
                    mainFragmentIv1.clearAnimation();
                    reverseCount = 0;
                    slideUp.hide();
                }
                break;
            case R.id.main_fragment_rl2:
                if (buttonPosition == 0) {
                    mainFragmentTv1.setTextColor(getResources().getColor(R.color.text_color_32));
                    mainFragmentTv2.setTextColor(getResources().getColor(R.color.text_color_huang));
                    mainFragmentIv1.setImageResource(R.mipmap.main_fragment2);
                    mainFragmentIv1.clearAnimation();
                    if (slideUp.isVisible()) {
                        slideUp.hide();
                    }
                    buttonPosition = 1;
                    reverseCount = 0;
                    //获取数据
                    getProduct2();
                }
                break;
            case R.id.product_choose_pop_btn1:
                productChoosePopBtn1.setSelected(true);
                productChoosePopBtn2.setSelected(false);
                productChoosePopBtn3.setSelected(false);
                productChoosePopBtn4.setSelected(false);
                //获取数据
                getProduct1("31");
                finishFilterBox();
                break;
            case R.id.product_choose_pop_btn2:
                productChoosePopBtn1.setSelected(false);
                productChoosePopBtn2.setSelected(true);
                productChoosePopBtn3.setSelected(false);
                productChoosePopBtn4.setSelected(false);
                //获取数据
                getProduct1("32");
                finishFilterBox();
                break;
            case R.id.product_choose_pop_btn3:
                productChoosePopBtn1.setSelected(false);
                productChoosePopBtn2.setSelected(false);
                productChoosePopBtn3.setSelected(true);
                productChoosePopBtn4.setSelected(false);
                //获取数据
                getProduct1("33");
                finishFilterBox();
                break;
            case R.id.product_choose_pop_btn4:
                productChoosePopBtn1.setSelected(false);
                productChoosePopBtn2.setSelected(false);
                productChoosePopBtn3.setSelected(false);
                productChoosePopBtn4.setSelected(true);
                //获取数据
                getProduct1("34");
                finishFilterBox();
                break;
            case R.id.product_choose_pop_rl:
                finishFilterBox();
                break;
        }
    }

    //筛选框收起
    private void finishFilterBox() {
        //筛选框消失
        if (slideUp.isVisible()){
            slideUp.hide();
        }
        //翻转次数归零
        reverseCount = 0;
        //箭头停止动画
        mainFragmentIv1.clearAnimation();
    }

    //自定义pop
    private void showPopupWindow(View view) {
//        // 一个自定义的布局，作为显示的内容
//        View contentView = LayoutInflater.from(getContext()).inflate(
//                R.layout.product_choose_pop, null);
//        // 设置按钮的点击事件
//        ListView lv = (ListView) contentView.findViewById(R.id.product_choose_pop_lv);
//        String[] a = { "默认", "金额从高到低", "利率从低到高", "期限从高到低"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.product_choose_pop_item, a);
//        lv.setAdapter(adapter);
//
//        popupWindow = new PopupWindow(contentView,
//                ListView.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        // TODO: 2016/5/17 设置动画
//        popupWindow.setAnimationStyle(R.style.popup_window_anim);
//        popupWindow.setTouchable(true);
//        popupWindow.setOutsideTouchable(true);
//
//
//        // 设置好参数之后再show
//        popupWindow.showAsDropDown(view);

    }

}
