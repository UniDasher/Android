package com.uni.unidasher.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.google.common.eventbus.Subscribe;
import com.uni.unidasher.R;
import com.uni.unidasher.data.DataProvider;
import com.uni.unidasher.data.entity.LoginInfo;
import com.uni.unidasher.data.event.SignInEvent;
import com.uni.unidasher.data.status.SignInState;
import com.uni.unidasher.ui.utils.Alert;
import com.uni.unidasher.ui.utils.HandlerHelper;

import org.w3c.dom.Text;


public class LoginActivity extends EventBusActivity {

    private EditText editAccount,editPwd;
    private Button btnLogin;
    private Button btnSignUp;
    private TextView txtvForgetPwd;
    private DataProvider mDataProvider;
    private ProgressDialog progressDialog;
    public static LoginActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        instance = this;
        mDataProvider = DataProvider.getInstance(this);
        findViews();
        setClickListeners();
    }

    private void findViews(){
        editAccount = (EditText)findViewById(R.id.editAccount);
        editPwd = (EditText)findViewById(R.id.editPwd);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);
        txtvForgetPwd = (TextView)findViewById(R.id.txtvForgetPwd);
        txtvForgetPwd.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    private void setClickListeners(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String phone = editAccount.getText().toString();
            String pwd = editPwd.getText().toString();
            if(!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd)){
                progressDialog = ProgressDialog.show(LoginActivity.this, "", "Please wait...", true, true);
                mDataProvider.signIn(new LoginInfo(phone,pwd),true);
            }else{
                Alert.showDefaultToast(LoginActivity.this,"手机号或密码不能为空!");
            }
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                finish();
            }
        });
        txtvForgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,PhoneValidateActivity.class));
            }
        });
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
                if(progressDialog!=null){
                    progressDialog.dismiss();
                }
                startActivity(new Intent(LoginActivity.this, TabActivity.class));
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
                        Alert.showDefaultToast(LoginActivity.this,"登录失败,请重新登录!");
//                        Alert.showTip(LoginActivity.this, "提示", "登录失败\n"+message,true,null);
                    }
                });
            }
        });
    }

    @Subscribe
    public void OnSignInEvent(final SignInEvent signInEvent){
        if(signInEvent.getSignInState() == SignInState.LOGGED){
            loginHx();
        }else{
            HandlerHelper.post(new HandlerHelper.onRun() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    String errorMsg = "登录失败,请重新登录!";
                    if(!TextUtils.isEmpty(signInEvent.getErrorDescription())){
                        errorMsg = signInEvent.getErrorDescription();
                    }
                    Alert.showDefaultToast(LoginActivity.this,errorMsg);
//                    Alert.showTip(LoginActivity.this, "提示", "登录失败",true,null);
                }
            });
        }
    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.i("onPause", "----------------------->onPause");
//        Toast.makeText(this,"onPause",Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.i("onStop", "----------------------->onStop");
//        Toast.makeText(this,"onStop",Toast.LENGTH_LONG).show();
//    }
}
