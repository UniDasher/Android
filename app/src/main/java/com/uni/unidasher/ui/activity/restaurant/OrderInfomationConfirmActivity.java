package com.uni.unidasher.ui.activity.restaurant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.uni.unidasher.R;
import com.uni.unidasher.ui.activity.EventBusActivity;
import com.uni.unidasher.ui.dialog.TimeWheelDialog;

public class OrderInfomationConfirmActivity extends EventBusActivity {
    private TextView txtvTimeEdit,txtvConfirm;
    private TextView txtvBack,txtvNavTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_infomation_confirm);
        findViews();
        setClickListeners();
    }

    private void findViews(){
        txtvConfirm = (TextView)findViewById(R.id.txtvConfirm);
        txtvTimeEdit = (TextView)findViewById(R.id.txtvTimeEdit);
        txtvBack = (TextView)findViewById(R.id.txtvBack);
        txtvNavTitle = (TextView)findViewById(R.id.txtvNavTitle);
        txtvNavTitle.setText("信息确认");
    }

    private void setClickListeners(){
        txtvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderInfomationConfirmActivity.this,OrderPayActivity.class));
            }
        });
        txtvTimeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimeWheelDialog(OrderInfomationConfirmActivity.this, "").show();
            }
        });
        txtvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
