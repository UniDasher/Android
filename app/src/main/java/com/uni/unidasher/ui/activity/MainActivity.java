package com.uni.unidasher.ui.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMMessage;
import com.uni.unidasher.R;
import com.uni.unidasher.chat.applib.controller.HXSDKHelper;
import com.uni.unidasher.ui.fragment.AccountFragment;
import com.uni.unidasher.ui.fragment.ChatFragment;
import com.uni.unidasher.ui.fragment.NearByFragment;
import com.uni.unidasher.ui.fragment.OrderFragment;

public class MainActivity extends EventBusActivity implements EMEventListener {
    private FragmentTabHost mTabHost;
    private Class fragments[] = {NearByFragment.class,OrderFragment.class,ChatFragment.class,AccountFragment.class};
    private String tabText[] = {"附近","订单","消息","我的"};
    private LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layoutInflater = LayoutInflater.from(this);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        initTab();
    }

    private void initTab(){
        int length = fragments.length;
        for(int i = 0;i<length;i++){
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(tabText[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragments[i], null);
        }
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index){
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);
        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(tabText[index]);

        return view;
    }


    public void setFragment(Fragment fragment){
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left, R.animator.slide_out_right, R.animator.slide_in_right);
//        transaction.replace(R.id.content_frame, fragment, fragment.getClass().getSimpleName());
//        transaction.addToBackStack(fragment.getClass().getSimpleName()).commit();
    }

    /**
     * 监听事件
     */
    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage: //普通消息
            {
                EMMessage message = (EMMessage) event.getData();

                //提示新消息
                HXSDKHelper.getInstance().getNotifier().onNewMsg(message);

//                refreshUI();
                break;
            }

            case EventOfflineMessage:
            {
//                refreshUI();
                break;
            }

            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
