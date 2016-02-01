package com.uni.unidasher.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/7/13.
 */
public class WeekEarnInfo {
    /*
    date	日期	String
    dishMoney	餐品总收益	Float
    carriageMoney	运费总收益	Float*/

    @SerializedName("date")
    String date;

    @SerializedName("dishMoney")
    float dishMoney;

    @SerializedName("carriageMoney")
    float carriageMoney;

    public String getDate() {
        return date;
    }

    public float getDishMoney() {
        return dishMoney;
    }

    public float getCarriageMoney() {
        return carriageMoney;
    }
}
