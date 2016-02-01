package com.uni.unidasher.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.uni.unidasher.BuildConfig;
import com.uni.unidasher.R;
import com.uni.unidasher.data.DataProvider;
import com.uni.unidasher.data.api.request.ForgotPassword;
import com.uni.unidasher.data.rest.RESTManager;
import com.uni.unidasher.ui.dialog.AlertPrompt;
import com.uni.unidasher.ui.utils.Alert;
import com.uni.unidasher.ui.utils.Extras;
import com.uni.unidasher.ui.utils.HandlerHelper;

public class ForgotPasswordActivity extends EventBusActivity {
    private TextView txtvCancel,txtvSave;
    private EditText etPwd,etRePwd;
    private DataProvider mDataProvider;
    private String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mDataProvider = DataProvider.getInstance(this);
        phone = this.getIntent().getStringExtra(Extras.Extra_Phone_Num);
        findViews();
        setClickListeners();
    }

    private void findViews(){
        txtvCancel = (TextView)findViewById(R.id.txtvCancel);
        txtvSave = (TextView)findViewById(R.id.txtvSave);
        etPwd = (EditText)findViewById(R.id.etPwd);
        etRePwd = (EditText)findViewById(R.id.etRePwd);
    }

    private void setClickListeners(){
        txtvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txtvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPwd = etPwd.getText().toString();
                String rePwd = etRePwd.getText().toString();
                if(newPwd.length()<6){
                    Alert.showDefaultToast(ForgotPasswordActivity.this,"密码不能小于六位!");
                    return;
                }else if(!newPwd.equals(rePwd)){
                    Alert.showDefaultToast(ForgotPasswordActivity.this,"两次密码输入不同!");
                    return;
                }
                mDataProvider.forgotPassowrd(new ForgotPassword(phone, newPwd, "888888"), new RESTManager.OnObjectDownloadedListener() {
                    @Override
                    public void onObjectDownloaded(final boolean success, String resultStr, final String tipStr) {
                        if (BuildConfig.DEBUG) {
                            Log.i("forgotPassowrd", success + "---->" + resultStr);
                        }
                        HandlerHelper.post(new HandlerHelper.onRun() {
                            @Override
                            public void run() {
                                if (success) {
                                    Alert.showTip(ForgotPasswordActivity.this, "提示", "密码重置成功!", false, new AlertPrompt.OnAlertClickListener() {
                                        @Override
                                        public void onClick() {
                                            finish();
                                        }
                                    });
                                }else{
                                    String errorMsg = "密码重置失败!";
                                    if(!TextUtils.isEmpty(tipStr)){
                                        errorMsg = tipStr;
                                    }
                                    Alert.showDefaultToast(ForgotPasswordActivity.this,errorMsg);
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}
