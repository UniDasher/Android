package com.uni.unidasher.data.entity.post;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/7/1.
 */
public class PCompainInfo {
/*    uid	用户编号
    mid	投诉点单编号
    wid	送餐人编号
    type	订单类型
    content	投诉内容描述*/

    @SerializedName("mid")
    String orderId;

    @SerializedName("uid")
    String userId;

    @SerializedName("wid")
    String sendId;

    /*
    1商家订单
    2超市订单
    */
    @SerializedName("type")
    int type;

    @SerializedName("content")
    String content;

    public PCompainInfo(String orderId, String userId, String sendId, int type, String content) {
        this.orderId = orderId;
        this.userId = userId;
        this.sendId = sendId;
        this.type = type;
        this.content = content;
    }
}
