package com.uni.unidasher.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/7/14.
 */
public class EarnRecordInfo {
   /*
   wid	送餐人编号	String
    mid	订单号	String
    totalMoney	订单总额	Float
    carriageMoney	运费	Float
    type	订单的类型	Int		1餐厅
    2超市
    createDate	创建时间	String*/

    @SerializedName("wid")
    String senderId;

    @SerializedName("mid")
    String orderNum;

    @SerializedName("totalMoney")
    float totalMoney;

    @SerializedName("carriageMoney")
    float serviceMoney;

    @SerializedName("type")
    int type;

    @SerializedName("createDate")
    String createDate;

    public String getCreateDate() {
        return createDate;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public String getSenderId() {
        return senderId;
    }

    public float getServiceMoney() {
        return serviceMoney;
    }

    public float getTotalMoney() {
        return totalMoney;
    }

    public int getType() {
        return type;
    }
}
