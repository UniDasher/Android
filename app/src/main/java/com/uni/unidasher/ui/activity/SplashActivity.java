package com.uni.unidasher.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.easemob.chat.EMChatManager;
import com.uni.unidasher.R;
import com.uni.unidasher.data.DataProvider;
import com.uni.unidasher.ui.utils.HandlerHelper;

public class SplashActivity extends EventBusActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        boolean isLogin = DataProvider.getInstance(this).isUserLogged();
//        if(isLogin){
//            EMChatManager.getInstance().loadAllConversations();
//        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(2000);
                    HandlerHelper.post(new HandlerHelper.onRun() {
                        @Override
                        public void run() {
                            nextPage();
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void nextPage(){
        boolean isLogin = DataProvider.getInstance(this).isUserLogged();
        if(!isLogin){
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }else{
            EMChatManager.getInstance().loadAllConversations();
            startActivity(new Intent(this,TabActivity.class));
            finish();
        }
    }

}
