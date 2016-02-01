package com.uni.unidasher.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2015/7/13.
 */
public class EarnInfo {
    /*
    weekEarn	送餐人本周收益	Float
    totalEarn	送餐人累计运费收益	Float
    totalMoney	送餐人累计总收益	Float
    balance	未支付净额	Float
    settleDate	上次结算时间	String
            list
    */

    @SerializedName("weekEarn")
    Float weekEarn;

    @SerializedName("totalEarn")
    Float totalServiceEarn;

    @SerializedName("totalMoney")
    Float totalMoney;

    @SerializedName("balance")
    Float balance;

    @SerializedName("settleDate")
    String lastDate;

    @SerializedName("list")
    List<WeekEarnInfo> weekEarnInfoList;

    public Float getBalance() {
        return balance;
    }

    public String getLastDate() {
        return lastDate;
    }

    public Float getTotalMoney() {
        return totalMoney;
    }

    public Float getTotalServiceEarn() {
        return totalServiceEarn;
    }

    public Float getWeekEarn() {
        return weekEarn;
    }

    public List<WeekEarnInfo> getWeekEarnInfoList() {
        return weekEarnInfoList;
    }
}
