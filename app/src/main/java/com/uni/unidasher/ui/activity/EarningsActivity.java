package com.uni.unidasher.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.uni.unidasher.BuildConfig;
import com.uni.unidasher.R;
import com.uni.unidasher.data.DataProvider;
import com.uni.unidasher.data.entity.EarnInfo;
import com.uni.unidasher.data.rest.RESTManager;
import com.uni.unidasher.data.utils.Constants;
import com.uni.unidasher.ui.fragment.EventBusFragment;
import com.uni.unidasher.ui.utils.HandlerHelper;
import com.uni.unidasher.ui.views.chart.SmoothLineChartEquallySpaced;

public class EarningsActivity extends EventBusActivity {
    private TextView txtvBack,txtvNavTitle;
    private ImageView imgvDate;
    private SmoothLineChartEquallySpaced chartView;
    private DataProvider mDataProvider;
    private TextView txtvWeekEarn,txtvServiceEarn,txtvTotalEarn,txtvNoPay,txtvLastDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earnings);
        mDataProvider = DataProvider.getInstance(this);
        findViews();
        setClickListeners();
        retrieveEarmInfo();
    }
    private void findViews(){
        txtvBack = (TextView)findViewById(R.id.txtvBack);
        txtvNavTitle = (TextView)findViewById(R.id.txtvNavTitle);
        txtvNavTitle.setText("收益记录");
        imgvDate = (ImageView)findViewById(R.id.imgvDate);
        txtvWeekEarn = (TextView)findViewById(R.id.txtvWeekEarn);
        txtvServiceEarn = (TextView)findViewById(R.id.txtvServiceEarn);
        txtvTotalEarn = (TextView)findViewById(R.id.txtvTotalEarn);
        txtvNoPay = (TextView)findViewById(R.id.txtvNoPay);
        txtvLastDate = (TextView)findViewById(R.id.txtvLastDate);

        chartView = (SmoothLineChartEquallySpaced)findViewById(R.id.chartView);
//        chartView.setData(new float[]{
//                0,
//                0,
//                0,
//                0,
//                0,
//                35,
//                0
//        });
    }
    private void setClickListeners(){
        txtvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();;
            }
        });
        imgvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EarningsActivity.this, EarningsDateRecordActivity.class));
            }
        });
    }
    private void setViews(EarnInfo earnInfo){
        if(earnInfo == null){
            return;
        }
        txtvWeekEarn.setText(""+ earnInfo.getWeekEarn());
        txtvLastDate.setText(""+earnInfo.getLastDate().substring(0,10));
        txtvNoPay.setText(""+earnInfo.getBalance());
        txtvServiceEarn.setText(""+earnInfo.getTotalServiceEarn());
        txtvTotalEarn.setText("" + earnInfo.getTotalMoney());
        chartView.setData(earnInfo.getWeekEarnInfoList());


    }

    private void retrieveEarmInfo(){
        mDataProvider.retrieveEarn(new RESTManager.OnObjectDownloadedListener() {
            @Override
            public void onObjectDownloaded(boolean success, String resultStr, String tipStr) {
                if(BuildConfig.DEBUG){
                    Log.i("retrieveEarn",success+"--->"+resultStr);
                }
                if(success){
                    final EarnInfo earnInfo = Constants.GSON_RECEIVED.fromJson(resultStr,EarnInfo.class);
                    HandlerHelper.post(new HandlerHelper.onRun() {
                        @Override
                        public void run() {
                            setViews(earnInfo);
                        }
                    });
                }
            }
        });
    }
}
