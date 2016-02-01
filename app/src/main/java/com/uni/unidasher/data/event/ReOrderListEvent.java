package com.uni.unidasher.data.event;

import com.uni.unidasher.data.entity.OrderInfo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/6/29.
 */
public class ReOrderListEvent {
    private ArrayList<OrderInfo> orderInfos;

    public ReOrderListEvent(ArrayList<OrderInfo> orderInfos) {
        this.orderInfos = orderInfos;
    }

    public ArrayList<OrderInfo> getOrderInfos() {
        return orderInfos;
    }
}
