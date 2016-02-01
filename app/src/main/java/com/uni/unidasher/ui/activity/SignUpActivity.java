package com.uni.unidasher.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.google.common.eventbus.Subscribe;
import com.uni.unidasher.DasherApplication;
import com.uni.unidasher.R;
import com.uni.unidasher.data.DataProvider;
import com.uni.unidasher.data.entity.LoginInfo;
import com.uni.unidasher.data.entity.RegisterInfo;
import com.uni.unidasher.data.event.SignInEvent;
import com.uni.unidasher.data.event.SignUpEvent;
import com.uni.unidasher.data.event.SignUpLoginInEvent;
import com.uni.unidasher.data.status.SignInState;
import com.uni.unidasher.ui.utils.Alert;
import com.uni.unidasher.ui.utils.CountDownButtonHelper;
import com.uni.unidasher.ui.utils.HandlerHelper;

public class SignUpActivity extends EventBusActivity {
    private TextView btnValidate;
    private Button btnRegister;
    private EditText editPhoneNum,editPwd,editValidationPwd,editUserName;
    private ProgressDialog progressDialog;
    private String phoneNum,pwd;
    private DataProvider mDataProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mDataProvider = DataProvider.getInstance(this);
        findViews();
        setClicksListener();
    }

    private void findViews(){
        editUserName = (EditText)findViewById(R.id.editUserName);
        editPhoneNum = (EditText)findViewById(R.id.editPhoneNum);
        editPwd = (EditText)findViewById(R.id.editPwd);
        editValidationPwd = (EditText)findViewById(R.id.editValidationPwd);
        btnValidate = (TextView)findViewById(R.id.btnValidate);
        btnRegister =(Button)findViewById(R.id.btnRegister);
    }

    private void setClicksListener(){
        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CountDownButtonHelper(btnValidate,btnValidate.getText().toString(),30,1).start();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phoneNum = editPhoneNum.getText().toString();
                pwd = editPwd.getText().toString();
                String userName = editUserName.getText().toString();

                if (TextUtils.isEmpty(userName)) {
                    Alert.showDefaultToast(SignUpActivity.this, "用户名不能为空!");
                    return;
                }else if(TextUtils.isEmpty(phoneNum)){
                    Alert.showDefaultToast(SignUpActivity.this, "手机号不能为空!");
                    return;
                }else if(pwd.length()<6){
                    Alert.showDefaultToast(SignUpActivity.this, "密码不能小于六位!");
                    return;
                }
                progressDialog = ProgressDialog.show(SignUpActivity.this, "", "Please wait...", true, true);
                RegisterInfo registerInfo = new RegisterInfo(userName, phoneNum, pwd, "888888");
                mDataProvider.signUp(registerInfo);
            }
        });
    }

    public void loginIn(){
        progressDialog = ProgressDialog.show(SignUpActivity.this,"","login...",true,true);
        mDataProvider.signIn(new LoginInfo(phoneNum,pwd),false);
    }

    @Subscribe
    public void onSignUpEvent(final SignUpEvent signUpEvent){
        if(signUpEvent!=null){
            if(signUpEvent.isSuccess()){
                final String userId = signUpEvent.getUserId();
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        loginIn();
                    }
                });
            }else{
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        String errorMsg = "注册失败!";
                        if(!TextUtils.isEmpty(signUpEvent.getErrorInfo())){
                            errorMsg = signUpEvent.getErrorInfo();
                        }
                        Alert.showDefaultToast(SignUpActivity.this, errorMsg);
                    }
                });

            }
        }
    }

    public void loginHx(){
        EMChatManager.getInstance().login(mDataProvider.getUserId(), "dasherhjb", new EMCallBack() {
            @Override
            public void onSuccess() {
                try {
                    // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                    EMChatManager.getInstance().loadAllConversations();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                EMChatManager.getInstance().updateCurrentUserNick(mDataProvider.getUserName());
                // 进入主页面
                progressDialog.dismiss();
                startActivity(new Intent(SignUpActivity.this, UploadPhotoActivity.class));
                LoginActivity.instance.finish();
                finish();
            }
            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int code, final String message) {
                HandlerHelper.post(new HandlerHelper.onRun() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Alert.showDefaultToast(SignUpActivity.this, "登录失败,请重新登录!");
                    }
                });
            }
        });
    }

    @Subscribe
    public void OnSignInEvent(final SignUpLoginInEvent signInEvent){
        if(signInEvent.getSignInState() == SignInState.LOGGED){
            loginHx();
        }else{
            HandlerHelper.post(new HandlerHelper.onRun() {
                @Override
                public void run() {
                    String errorMsg = "登录失败,请重新登录!";
                    if(!TextUtils.isEmpty(signInEvent.getErrorDescription())){
                        errorMsg = signInEvent.getErrorDescription();
                    }
                    Alert.showDefaultToast(SignUpActivity.this, errorMsg);
//                    Alert.showTip(SignUpActivity.this,"提示","登录失败",true,null);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        finish();
    }
}
