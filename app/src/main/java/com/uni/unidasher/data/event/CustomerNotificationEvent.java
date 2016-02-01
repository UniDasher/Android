package com.uni.unidasher.data.event;

import com.easemob.chat.EMMessage;

/**
 * Created by Administrator on 2015/7/27.
 */
public class CustomerNotificationEvent {
    String msg;

    public CustomerNotificationEvent(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
