package com.uni.unidasher.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/7/14.
 */
public class OutInfo {
/*
    uid	用户编号	String
    userName	姓名	String
    oldBalance	账户余额	Float
    type	收支的类型	Int		1.	用户收入
    2.	用户结算
    settlePrice	收支的金额	Float
    curBalance	账户当前余额	Float
    settleNumberType	账号类型	String		填“订单”“支付宝”“XX银行”，表示SettleNumber的类型
    settleNumber	账号	String		填“订单”“支付宝”“XX银行”等的账号
    settleDesc	收支的说明	String
    */

    @SerializedName("uid")
    String userId;

    @SerializedName("userName")
    String userName;

    @SerializedName("oldBalance")
    float oldBalance;

    @SerializedName("type")
    int type;

    @SerializedName("settlePrice")
    float settlePrice;

    @SerializedName("curBalance")
    float curBalance;

    @SerializedName("settleNumberType")
    String settleNumberType;

    @SerializedName("settleNumber")
    String settleNumber;

    @SerializedName("settleDesc")
    String settleDesc;

    @SerializedName("createDate")
    String createDate;

    public float getCurBalance() {
        return curBalance;
    }

    public float getOldBalance() {
        return oldBalance;
    }

    public String getSettleDesc() {
        return settleDesc;
    }

    public String getSettleNumber() {
        return settleNumber;
    }

    public String getSettleNumberType() {
        return settleNumberType;
    }

    public float getSettlePrice() {
        return settlePrice;
    }

    public int getType() {
        return type;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getCreateDate() {
        return createDate;
    }
}
