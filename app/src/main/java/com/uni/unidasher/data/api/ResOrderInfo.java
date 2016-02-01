package com.uni.unidasher.data.api;

import com.google.gson.annotations.SerializedName;
import com.uni.unidasher.data.datamodel.databaseAndApi.DishInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/6/27.
 */
public class ResOrderInfo implements Serializable {
    /*
    sid 商家编号	String
    uid	订餐用户的编号	String
    menuCount	订单菜品的总数	Int
    dishsMoney	订餐餐费	Float
    carriageMoney	运费	Float
    taxesMoney	税费	Float
    serviceMoney	服务费	Float
    tipMoney	小费	Float
    payType	支付方式	Int		1线上支付
    2线下支付
    mealStartDate	吃饭起始时间	Datetime
    mealEndDate	吃饭结束时间	Datetime
    address	送餐地址	String
    longitude	地址经度	String
    latitude	地址纬度	String
            dishs*/

    @SerializedName("sid")
    private String sid;

    @SerializedName("uid")
    private String uid;

    @SerializedName("menuCount")
    private int menuCount;

    @SerializedName("dishsMoney")
    private float dishsMoney;

    @SerializedName("carriageMoney")
    private float carriageMoney;

    @SerializedName("taxesMoney")
    private float taxesMoney;

    @SerializedName("serviceMoney")
    private float serviceMoney;

    @SerializedName("tipMoney")
    private float tipMoney;

    @SerializedName("payType")
    private int payType;

    @SerializedName("mealStartDate")
    private String mealStartDate;

    @SerializedName("mealEndDate")
    private String mealEndDate;

    @SerializedName("address")
    private String address;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("dishs")
    private List<DishInfo> dishInfos;

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setMenuCount(int menuCount) {
        this.menuCount = menuCount;
    }

    public void setDishsMoney(float dishsMoney) {
        this.dishsMoney = dishsMoney;
    }

    public void setCarriageMoney(float carriageMoney) {
        this.carriageMoney = carriageMoney;
    }

    public void setTaxesMoney(float taxesMoney) {
        this.taxesMoney = taxesMoney;
    }

    public void setServiceMoney(float serviceMoney) {
        this.serviceMoney = serviceMoney;
    }

    public void setTipMoney(float tipMoney) {
        this.tipMoney = tipMoney;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public void setMealStartDate(String mealStartDate) {
        this.mealStartDate = mealStartDate;
    }

    public void setMealEndDate(String mealEndDate) {
        this.mealEndDate = mealEndDate;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setDishInfos(List<DishInfo> dishInfos) {
        this.dishInfos = dishInfos;
    }
}
