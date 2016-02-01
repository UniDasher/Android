package com.uni.unidasher.data.entity.post;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/7/3.
 */
public class POrderStatus {
    @SerializedName("mid")
    String orderId;
    @SerializedName("status")
    int status;

    public POrderStatus(String orderId, int status) {
        this.orderId = orderId;
        this.status = status;
    }
}
