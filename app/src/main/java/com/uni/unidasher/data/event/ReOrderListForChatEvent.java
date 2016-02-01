package com.uni.unidasher.data.event;

import com.uni.unidasher.data.entity.OrderInfo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/7/3.
 */
public class ReOrderListForChatEvent {
    private ArrayList<OrderInfo> orderInfos;

    public ReOrderListForChatEvent(ArrayList<OrderInfo> orderInfos) {
        this.orderInfos = orderInfos;
    }

    public ArrayList<OrderInfo> getOrderInfos() {
        return orderInfos;
    }
}
