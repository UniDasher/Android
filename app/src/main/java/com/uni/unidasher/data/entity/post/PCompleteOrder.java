package com.uni.unidasher.data.entity.post;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/7/2.
 */
public class PCompleteOrder {
    @SerializedName("mid")
    String orderId;
    @SerializedName("evalServer")
    int senderEvaluation;
    @SerializedName("evalShop")
    int shopEvaluation;

    public PCompleteOrder(String orderId, int senderEvaluation, int shopEvaluation) {
        this.orderId = orderId;
        this.senderEvaluation = senderEvaluation;
        this.shopEvaluation = shopEvaluation;
    }
}
