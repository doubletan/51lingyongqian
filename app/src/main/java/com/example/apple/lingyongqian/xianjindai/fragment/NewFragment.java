package com.example.apple.lingyongqian.xianjindai.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.activity.MyApp;
import com.example.apple.lingyongqian.utils.Constants;
import com.example.apple.lingyongqian.xianjindai.activity.WebViewActivity;
import com.example.apple.lingyongqian.xianjindai.adapter.NewProductAdapter;
import com.example.apple.lingyongqian.xianjindai.biz.BrowsingHistory;
import com.example.apple.lingyongqian.xianjindai.entity.ImagerBean;
import com.example.apple.lingyongqian.xianjindai.entity.Product;
import com.example.apple.lingyongqian.xianjindai.util.DeviceUtil;
import com.example.apple.lingyongqian.xianjindai.util.ExceptionUtil;
import com.example.apple.lingyongqian.xianjindai.util.Utill;
import com.example.apple.lingyongqian.xianjindai.view.MyProgressDialog;
import com.example.apple.lingyongqian.xianjindai.view.VerticalViewPager;
import com.example.apple.lingyongqian.xianjindai.view.pullableview.PullToRefreshLayout;
import com.example.apple.lingyongqian.xianjindai.view.pullableview.PullableScrollView;
import com.google.gson.Gson;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bingoogolapple.bgabanner.BGABanner;

/**
 *
 */
public class NewFragment extends Fragment {

    @Bind(R.id.pull_icon)
    ImageView pullIcon;
    @Bind(R.id.refreshing_icon)
    ImageView refreshingIcon;
    @Bind(R.id.state_tv)
    TextView stateTv;
    @Bind(R.id.state_iv)
    ImageView stateIv;
    @Bind(R.id.head_view)
    RelativeLayout headView;
    @Bind(R.id.new_fragment_banner)
    BGABanner newFragmentBanner;
    @Bind(R.id.new_fragment_iv)
    ImageView newFragmentIv;
    @Bind(R.id.new_fragment_lv)
    ListView newFragmentLv;
    @Bind(R.id.new_fragment_sv)
    PullableScrollView newFragmentSv;
    @Bind(R.id.pullup_icon)
    ImageView pullupIcon;
    @Bind(R.id.loading_icon)
    ImageView loadingIcon;
    @Bind(R.id.loadstate_tv)
    TextView loadstateTv;
    @Bind(R.id.loadstate_iv)
    ImageView loadstateIv;
    @Bind(R.id.loadmore_view)
    RelativeLayout loadmoreView;
    @Bind(R.id.new_refresh_view)
    PullToRefreshLayout newRefreshView;
    @Bind(R.id.new_fragment_vvp)
    VerticalViewPager newFragmentVvp;
    private View view;
    private ImagerBean imagerBean;
    private ArrayList<Product.PrdListProduct> products = new ArrayList<Product.PrdListProduct>();
    private NewProductAdapter adapter;
    private ArrayList<String> messagess = new ArrayList<String>();
    private MyVpAdapger vpAdapter;
    private Timer timer;
    private int currentIndex;
    // 消息滚动滚动
    Handler h = new Handler() {
        public void handleMessage(Message msg) {
            newFragmentVvp.setCurrentItem(currentIndex);// 设置此次要显示的pager

        };
    };
    private String[] phone = {"3", "5", "8", "7"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //加载界面
        view = View.inflate(getActivity(), R.layout.fragment_new, null);

        try {
            ButterKnife.bind(this, view);
            //设置控件
            setViews();
            //设置监听
            setListener();
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }

        ButterKnife.bind(this, view);
        return view;
    }

    private void setListener() {
        //刷新
        newRefreshView.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
                if (DeviceUtil.IsNetWork(getContext()) == false) {
                    Toast.makeText(getContext(), "网络未连接", Toast.LENGTH_SHORT).show();
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                    return;
                }
                //轮播图
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String URL = Constants.URL;
                        String nameSpace = Constants.nameSpace;
                        String method_Name = "Daohang";
                        String SOAP_ACTION = nameSpace + method_Name;
                        SoapObject rpc = new SoapObject(nameSpace, method_Name);
                        HttpTransportSE transport = new HttpTransportSE(URL);
                        transport.debug = true;
                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        envelope.bodyOut = rpc;
                        envelope.dotNet = true;
                        envelope.setOutputSoapObject(rpc);
                        try {
                            transport.call(SOAP_ACTION, envelope);
                            SoapObject object = (SoapObject) envelope.bodyIn;
                            final String result = object.getProperty("DaohangResult").toString();
                            if (!TextUtils.isEmpty(result)) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Gson gson = new Gson();
                                        imagerBean = gson.fromJson(result, ImagerBean.class);
                                        Collections.sort(imagerBean.getDaohang());
                                        setBunder();
                                    }
                                });
                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "数据获取失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "数据获取失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
                //产品
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String URL = Constants.URL;
                        String nameSpace = Constants.nameSpace;
                        String method_Name = "GetProduct";
                        String SOAP_ACTION = nameSpace + method_Name;
                        SoapObject rpc = new SoapObject(nameSpace, method_Name);
                        rpc.addProperty("sAppName", Constants.appName);
                        rpc.addProperty("sPage", "3");
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
                                    if (!TextUtils.isEmpty(str) && !str.startsWith("1,") && !str.startsWith("2")) {
                                        Gson gson = new Gson();
                                        Product product = gson.fromJson(str, Product.class);
                                        products.clear();
                                        products.addAll(product.getPrdList());
                                        adapter.notifyDataSetChanged();
                                        Utill.resetHight(newFragmentLv);
                                        //消息刷新
                                        getMessages();
                                        if (null!=timer){
                                            timer.cancel();
                                        }
                                        vpAdapter.notifyDataSetChanged();
                                        startCycle();

                                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                                    } else {
                                        Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                                    }
                                }
                            });

                        } catch (final Exception e) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ExceptionUtil.handleException(e);
                                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                                    Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                }).start();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

            }
        });
        newFragmentLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getContext(), WebViewActivity.class).putExtra("url", products.get(position).getLink()));
                new BrowsingHistory().execute(products.get(position).getUid(),"3");
            }
        });
    }


    private void setViews() {
        /**
         * 初始化banner
         */
        newFragmentBanner.setDelegate(new BGABanner.Delegate<ImageView, String>() {
            @Override
            public void onBannerItemClick(BGABanner banner, ImageView itemView, String model, int position) {
                startActivity(new Intent(getContext(), WebViewActivity.class).putExtra("url", imagerBean.getDaohang().get(position).getLink()));
            }
        });
        newFragmentBanner.setAdapter(new BGABanner.Adapter<ImageView, String>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, String model, int position) {
                Glide.with(getActivity())
                        .load(model)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .dontAnimate()
                        .into(itemView);
            }
        });

        if (null != MyApp.imagerBean) {
            imagerBean = MyApp.imagerBean;
            setBunder();
        } else {
            getImageBean();
        }

        //产品
        if (null == MyApp.newProduct) {
            getProduct("1-1");
        } else {
            products = MyApp.newProduct.getPrdList();
        }
        adapter = new NewProductAdapter(getContext(),products);
        newFragmentLv.setAdapter(adapter);
        Utill.resetHight(newFragmentLv);

        //消息
        getMessages();
        vpAdapter = new MyVpAdapger();
        newFragmentVvp.setAdapter(vpAdapter);// 配置pager页
        startCycle();
    }

    /**
     * 循环线程
     */
    private void startCycle() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (newFragmentVvp != null) {
                    if (newFragmentVvp.getCurrentItem() == messagess.size() - 1) {
                        currentIndex = 0;
                    } else {
                        currentIndex = newFragmentVvp.getCurrentItem() + 1;// 下一个页
                    }
                    h.sendEmptyMessage(0);// 在此线程中，不能操作ui主线程
                }
            }
        }, 4000, 4000);

    }

    private void getMessages() {
        if (products.size() > 0) {
            for (int i = 0; i <= 20; i++) {
                String name = products.get(new Random().nextInt(products.size() - 1)).getName();
                messagess.add(name);
            }
        }
    }


    private void getProduct(final String s) {
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
                            if (!TextUtils.isEmpty(str) && !str.startsWith("1,") && !str.startsWith("2")) {
                                Gson gson = new Gson();
                                Product product = gson.fromJson(str, Product.class);
                                products.clear();
                                products.addAll(product.getPrdList());
                                adapter.notifyDataSetChanged();
                                Utill.resetHight(newFragmentLv);
                                //消息刷新
                                getMessages();
                                if (null!=timer){
                                    timer.cancel();
                                }
                                vpAdapter.notifyDataSetChanged();
                                startCycle();
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


    /**
     * header 滚动
     */
    private void setBunder() {
        final ArrayList<String> arr = new ArrayList<>();
        final ArrayList<ImagerBean.DaohangProduct> Data = imagerBean.getDaohang();
        for (ImagerBean.DaohangProduct s : Data) {
            arr.add(Constants.piURL + s.getAdvpath());
        }
        newFragmentBanner.setData(arr, null);
    }

    //获取banner
    private void getImageBean() {

        if (DeviceUtil.IsNetWork(getContext()) == false) {
            Toast.makeText(getContext(), "网络未连接", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                String URL = Constants.URL;
                String nameSpace = Constants.nameSpace;
                String method_Name = "Daohang";
                String SOAP_ACTION = nameSpace + method_Name;
                SoapObject rpc = new SoapObject(nameSpace, method_Name);
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.debug = true;
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.bodyOut = rpc;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                try {
                    transport.call(SOAP_ACTION, envelope);
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    final String result = object.getProperty("DaohangResult").toString();
                    if (!TextUtils.isEmpty(result)) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Gson gson = new Gson();
                                imagerBean = gson.fromJson(result, ImagerBean.class);
                                Collections.sort(imagerBean.getDaohang());
                                setBunder();
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "数据获取失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "数据获取失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }


    // ViewPager的适配器
    private class MyVpAdapger extends PagerAdapter {

        @Override
        public int getCount() {
            return messagess.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.new_product_vvp_item, null);
            TextView tv1 = (TextView) view.findViewById(R.id.new_product_vvp_item_tv1);
            TextView tv2 = (TextView) view.findViewById(R.id.new_product_vvp_item_tv2);
            TextView tv3 = (TextView) view.findViewById(R.id.new_product_vvp_item_tv3);
            TextView tv4 = (TextView) view.findViewById(R.id.new_product_vvp_item_tv4);
            TextView tv5 = (TextView) view.findViewById(R.id.new_product_vvp_item_tv5);
            String phone1="1"+phone[new Random().nextInt(4)]+new Random().nextInt(10)+"****"+(new Random().nextInt(8999)+1000);
            String money=(new Random().nextInt(10)+1)*1000+"";
            tv1.setText(phone1+"在");
            tv2.setText(messagess.get(position));
            tv4.setText(money);

            // 如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
            ViewParent vp = view.getParent();
            if (vp != null) {
                ViewGroup parent = (ViewGroup) vp;
                parent.removeView(view);
            }
            container.addView(view);
            return view;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
