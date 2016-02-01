package com.uni.unidasher.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.uni.unidasher.R;
import com.uni.unidasher.ui.utils.Alert;
import com.uni.unidasher.ui.utils.Extras;

public class PhoneValidateActivity extends EventBusActivity {
    private TextView txtvCancel,txtvNext;
    private EditText etPhone,etCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_validate);
        findViews();
        setClickListeners();
    }

    private void findViews(){
        txtvCancel = (TextView)findViewById(R.id.txtvCancel);
        txtvNext = (TextView)findViewById(R.id.txtvNext);
        etPhone = (EditText)findViewById(R.id.etPhone);
        etCode = (EditText)findViewById(R.id.etCode);
    }

    private void setClickListeners(){
        txtvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txtvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etPhone.getText().toString();
                String code = etCode.getText().toString();
                if(TextUtils.isEmpty(phone)){
                    Alert.showDefaultToast(PhoneValidateActivity.this,"手机号不能为空!");
                    return;
                }
                startActivity(new Intent(PhoneValidateActivity.this,ForgotPasswordActivity.class).putExtra(Extras.Extra_Phone_Num,phone));
                finish();
            }
        });
    }
}
