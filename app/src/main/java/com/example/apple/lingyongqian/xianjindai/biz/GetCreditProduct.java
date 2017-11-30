package com.example.apple.lingyongqian.xianjindai.biz;

import android.content.Context;
import android.text.TextUtils;

import com.example.apple.lingyongqian.activity.MyApp;
import com.example.apple.lingyongqian.utils.Constants;
import com.example.apple.lingyongqian.xianjindai.entity.CreditProduct;
import com.example.apple.lingyongqian.xianjindai.util.ExceptionUtil;
import com.google.gson.Gson;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by tantan on 2017/7/13.
 */

public class GetCreditProduct {
    public GetCreditProduct(Context mContext) {
        this.mContext = mContext;
    }

    private Context mContext;

    public  void execute(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                String URL = Constants.URL;
                String nameSpace = Constants.nameSpace;
                String method_Name = "CreditCardList";
                String SOAP_ACTION = nameSpace + method_Name;
                SoapObject rpc = new SoapObject(nameSpace, method_Name);
                rpc.addProperty("channel", Constants.qudao);
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.debug = true;
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.bodyOut = rpc;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                try {
                    transport.call(SOAP_ACTION, envelope);
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    String str = object.getProperty("CreditCardListResult").toString();

                    if (!TextUtils.isEmpty(str) && !str.startsWith("1,")&& !str.startsWith("2,")) {
                        Gson gson = new Gson();
                        CreditProduct creditProduct = gson.fromJson(str, CreditProduct.class);
                        MyApp.creditProduct=creditProduct;
                    }
                } catch (Exception e) {
                    ExceptionUtil.handleException(e);
                }
            }
        }).start();
    }
}
