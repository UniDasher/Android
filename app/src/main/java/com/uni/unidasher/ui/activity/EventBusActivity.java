package com.uni.unidasher.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import com.easemob.chat.EMMessage;
import com.google.common.eventbus.Subscribe;
import com.uni.unidasher.R;
import com.uni.unidasher.data.DataProvider;
import com.uni.unidasher.data.event.CustomerNotificationEvent;
import com.uni.unidasher.ui.dialog.TopNotificationDialogFragment;


/**
 * Created by Administrator on 2015/5/18.
 */
public abstract class EventBusActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        DataProvider.getEventBus().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataProvider.getEventBus().unregister(this);
    }


    public void onLeftActionBarClick(){}

    public void onRightActionBarClick(){}

    @Subscribe
    public void OnCustomerNotificationEvent(CustomerNotificationEvent customerNotificationEvent){
        String msg = customerNotificationEvent.getMsg();
        if(msg!=null){
            TopNotificationDialogFragment notificationDialog =
                    TopNotificationDialogFragment.newInstance(
                            msg, 4000l);
            notificationDialog.show(getFragmentManager(), "chat_notification_dialog");
        }
    }

}
