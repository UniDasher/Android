package com.uni.unidasher.data.event;

import com.uni.unidasher.data.entity.OrderInfo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/6/30.
 */
public class ReShopOrderListEvent {
    ArrayList<OrderInfo> orderInfos;

    public ReShopOrderListEvent(ArrayList<OrderInfo> orderInfos) {
        this.orderInfos = orderInfos;
    }

    public ArrayList<OrderInfo> getOrderInfos() {
        return orderInfos;
    }
}
