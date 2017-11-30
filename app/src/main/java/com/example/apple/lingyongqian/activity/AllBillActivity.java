package com.example.apple.lingyongqian.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.apple.lingyongqian.R;
import com.example.apple.lingyongqian.dao.AllBillInfo;
import com.example.apple.lingyongqian.dao.AllBillInfoDao;
import com.example.apple.lingyongqian.dao.Bills;
import com.example.apple.lingyongqian.dao.BillsDao;
import com.example.apple.lingyongqian.dao.DaoSession;
import com.example.apple.lingyongqian.dao.User;
import com.example.apple.lingyongqian.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;

public class AllBillActivity extends AppCompatActivity implements View.OnClickListener {

    private MyApp myApp;
    private User user;
    private DaoSession daoSession;
    private AllBillInfoDao allBillInfoDao;
    private AllBillInfo allBillInfo;
    private BillsDao billsDao;
    private List<Bills> billsList = new ArrayList<>();
    private List<AllBillInfo> allBillInfoList = new ArrayList<>();
    private TextView toolbar_title,say,all_in_textview,all_in_num,all_out_textview,all_out_num;
    private ImageView add,back;
    //图表
    private ColumnChartView chart;
    private ColumnChartData data;
    private PieChartView chartPie;
    private PieChartData dataPie;
    private float[][] allBills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_bill);
        myApp = MyApp.getApp();
        myApp.addToList(this);
        user = MyApp.user1;
        initView();
        loadBill();
        makeNum(billsList);
    }

    @Override
    public void onStart() {
        super.onStart();
        StatusBarUtil.setColor(this, Color.parseColor("#607d8b"), 0);
        generateNegativeStackedData();
        generatePieData();
    }

    private void initView(){
       // say = (TextView) findViewById(R.id.say);
        toolbar_title = (TextView) findViewById(R.id.date);
        back = (ImageView) findViewById(R.id.back);
        add = (ImageView) findViewById(R.id.add_day);
        add.setVisibility(View.INVISIBLE);
        chart = (ColumnChartView) findViewById(R.id.chart);
        chart.setOnValueTouchListener(new ValueTouchListener());
        chartPie = (PieChartView) findViewById(R.id.chartPie);
        chartPie.setOnValueTouchListener(new ValueTouchPieListener());

        all_in_textview = (TextView) findViewById(R.id.all_in_textview);
        all_in_num = (TextView) findViewById(R.id.all_in_num);
        all_out_textview = (TextView) findViewById(R.id.all_out_textview);
        all_out_num = (TextView) findViewById(R.id.all_out_num);

        daoSession = myApp.getDaoSession(this);
        allBillInfoDao = daoSession.getAllBillInfoDao();
        billsDao = daoSession.getBillsDao();

        toolbar_title.setText("账单统计");
        back.setOnClickListener(this);
        add.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.back:
                finish();
                break;
        }
    }

    private void loadBill(){
        QueryBuilder builder = billsDao.queryBuilder();
        billsList = builder.where(BillsDao.Properties.B_pid.eq(user.getU_id())).orderDesc(BillsDao.Properties.B_add_date).list();
    }

    //统计每天的收支
    private float[] all = new float[2];
    private void makeNum(List<Bills> list){
        allBills = new float[7][2];
        if (list.size()==0){
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        String temp_date = "";
        String fun_in = "";
        float in = 0;
        float out = 0;
        int arryLength = 0;
        int size = list.size();
        for (int i = 0;i<size;i++){
            Bills allBillInfo = list.get(i);
            Log.i("---","途经:"+allBillInfo.getB_name()+" 时间："+allBillInfo.getB_add_date()+"  金钱："+allBillInfo.getB_num());
            float temp = 0;
            try{
                temp = Float.parseFloat(allBillInfo.getB_num());
            }catch (Exception e){
                Log.i("---","非法数字");
                continue;
            }
            fun_in = allBillInfo.getB_add_date();
            int type = Integer.parseInt(allBillInfo.getB_type());
            if (i==0){
                temp_date = fun_in;
                stringBuffer.append(temp_date+" ");
                if (type==1){
                    in+=temp;
                    all[0]+=temp;
                }else {
                    out+=temp;
                    all[1]+=temp;
                }
            }else {
                if (temp_date.equals(fun_in)){
                    if (type==1){
                        in+=temp;
                        all[0]+=temp;
                    }else {
                        out+=temp;
                        all[1]+=temp;
                    }
                }else {
                    temp_date = fun_in;
                    if (arryLength<=6){
                        allBills[arryLength][0] = in;
                        allBills[arryLength][1] = -1*out;
                    }
                    in = 0;
                    out = 0;
                    if (type==1){
                        in+=temp;
                        all[0]+=temp;
                    }else {
                        out+=temp;
                        all[1]+=temp;
                    }
                    stringBuffer.append(temp_date+" ");
                    arryLength++;
                }
            }
            if (size==i+1){
                allBills[arryLength][0] = in;
                allBills[arryLength][1] = -1*out;
            }
        }
    }

    //饼
    private void generatePieData() {
        int numValues = 2;

        List<SliceValue> values = new ArrayList<SliceValue>();
        int color = 0;
        for (int i = 0; i < numValues; ++i) {
            color = ChartUtils.pickColor();
            SliceValue sliceValue = new SliceValue(all[i], color);
            values.add(sliceValue);
            if (i==0){
                all_in_textview.setBackgroundColor(color);
                all_in_num.setText(" "+all[i]+" ￥");
            }else {
                all_out_textview.setBackgroundColor(color);
                all_out_num.setText(" " + all[i] + " ￥");
            }
        }


        dataPie = new PieChartData(values);
        dataPie.setHasLabels(true);
        dataPie.setHasLabelsOnlyForSelected(false);
        dataPie.setHasLabelsOutside(true);
        dataPie.setHasCenterCircle(false);

        chartPie.setPieChartData(dataPie);
    }

    //条形图
    private void generateNegativeStackedData() {
        int numSubcolumns = 2;
        int numColumns = 7;
        Log.i("---",numColumns+"");
        // Column can have many stacked subcolumns, here I use 4 stacke subcolumn in each of 4 columns.
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        for (int i = 0; i < numColumns; ++i) {
            values = new ArrayList<SubcolumnValue>();
            for (int j = 0; j < numSubcolumns; ++j) {
                values.add(new SubcolumnValue(allBills[i][j], ChartUtils.pickColor()));
            }
            axisValues.add(new AxisValue(i).setLabel("" + (i + 1)));
            Column column = new Column(values);
            column.setHasLabels(true);
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
        }
        data = new ColumnChartData(columns);
        data.setAxisXBottom(new Axis(axisValues).setHasLines(true).setName("最近7天（正轴为收入，负轴为支出）"));
        data.setAxisYLeft(new Axis().setHasLines(true));
        // Set stacked flag.
        data.setStacked(true);
        chart.setColumnChartData(data);
    }


    private class ValueTouchListener implements ColumnChartOnValueSelectListener {

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            if (subcolumnIndex==0){
                toast("收入：" +value.getValue()+" 元");
            }else {
                toast("支出：" +value.getValue()+" 元");
            }
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub
        }
    }
    private class ValueTouchPieListener implements PieChartOnValueSelectListener {

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            if (arcIndex==0){
                toast("收入：" +value.getValue()+" 元");
            }else {
                toast("支出：" +value.getValue()+" 元");
            }
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }
    private void toast(String str) {
        Toast.makeText(this,str, Toast.LENGTH_SHORT).show();
    }
}
