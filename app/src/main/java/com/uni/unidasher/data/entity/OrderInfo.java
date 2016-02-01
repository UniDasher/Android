package com.uni.unidasher.data.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/6/29.
 * 订单信息
 */

public class OrderInfo implements Serializable{
    /*
    mid	订单编号	String
    sid	商家编号	String
    shopName	商家名	String
    uid	订餐用户的编号	String
    userName	用户编号	String
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
    status	订单的状态	Int
    createDate	订单的创建时间	Datetime
    wid	送餐人的编号	String
    serverName	送餐人名字	String
    startDate	开始送餐时间	Datetime
    endDate	完成订餐配送时间	Datetime
    cancleDate	订单取消时间	datetime
    overTimeDate	订单超时时间	Datetime
    complainDate	订单投诉时间	Datetime

    */

    @SerializedName("mid")
    private String mid;

    @SerializedName("sid")
    private String sid;

    @SerializedName("shopName")
    private String shopName;

    @SerializedName("uid")
    private String uid;

    @SerializedName("wid")
    private String wid;

    @SerializedName("serverName")
    private String sendUserName;

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

    @SerializedName("address")
    private String address;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("status")
    private int status;

    @SerializedName("createDate")
    private String createDate;

    @SerializedName("startDate")
    private String startDate;

    @SerializedName("cancleDate")
    private String cancleDate;

    @SerializedName("overTimeDate")
    private String overTimeDate;

    @SerializedName("complainDate")
    private String complainDate;

    @SerializedName("endDate")
    private String endDate;

    @SerializedName("serverLogo")
    private String sendUserHead;

    @SerializedName("serverPhone")
    private String sendUserPhone;

    @SerializedName("userName")
    private String userName;

    @SerializedName("userLogo")
    private String userLogo;

    @SerializedName("userPhone")
    private String userPhone;

    @SerializedName("shopAddress")
    private String shopAddress;

    @SerializedName("shopLatitude")
    private String shopLatitude;

    @SerializedName("shopLongitude")
    private String shopLongtitude;

    @SerializedName("mealStartDate")
    private String mealStartDate;

    @SerializedName("mealEndDate")
    private String mealEndDate;

    @SerializedName("distance")
    private String distance;

    @SerializedName("direction")
    private String direction;


    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSenderId() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public String getSendUserName() {
        return sendUserName;
    }

    public void setSendUserName(String sendUserName) {
        this.sendUserName = sendUserName;
    }

    public int getMenuCount() {
        return menuCount;
    }

    public void setMenuCount(int menuCount) {
        this.menuCount = menuCount;
    }

    public float getDishsMoney() {
        return dishsMoney;
    }

    public void setDishsMoney(float dishsMoney) {
        this.dishsMoney = dishsMoney;
    }

    public float getCarriageMoney() {
        return carriageMoney;
    }

    public void setCarriageMoney(float carriageMoney) {
        this.carriageMoney = carriageMoney;
    }

    public float getTaxesMoney() {
        return taxesMoney;
    }

    public void setTaxesMoney(float taxesMoney) {
        this.taxesMoney = taxesMoney;
    }

    public float getServiceMoney() {
        return serviceMoney;
    }

    public void setServiceMoney(float serviceMoney) {
        this.serviceMoney = serviceMoney;
    }

    public float getTipMoney() {
        return tipMoney;
    }

    public void setTipMoney(float tipMoney) {
        this.tipMoney = tipMoney;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        status = status;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getCancleDate() {
        return cancleDate;
    }

    public void setCancleDate(String cancleDate) {
        this.cancleDate = cancleDate;
    }

    public String getOverTimeDate() {
        return overTimeDate;
    }

    public void setOverTimeDate(String overTimeDate) {
        this.overTimeDate = overTimeDate;
    }

    public String getComplainDate() {
        return complainDate;
    }

    public void setComplainDate(String complainDate) {
        this.complainDate = complainDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getSendUserHead() {
        return sendUserHead;
    }

    public String getSendUserPhone() {
        return sendUserPhone;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserLogo() {
        return userLogo;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public String getShopLatitude() {
        return shopLatitude;
    }

    public String getShopLongtitude() {
        return shopLongtitude;
    }

    public String getMealEndDate() {
        return mealEndDate;
    }

    public String getMealStartDate() {
        return mealStartDate;
    }

    public String getDistance() {
        return distance;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setMealEndDate(String mealEndDate) {
        this.mealEndDate = mealEndDate;
    }

    public void setMealStartDate(String mealStartDate) {
        this.mealStartDate = mealStartDate;
    }

    public void setSendUserHead(String sendUserHead) {
        this.sendUserHead = sendUserHead;
    }

    public void setSendUserPhone(String sendUserPhone) {
        this.sendUserPhone = sendUserPhone;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public void setShopLatitude(String shopLatitude) {
        this.shopLatitude = shopLatitude;
    }

    public void setShopLongtitude(String shopLongtitude) {
        this.shopLongtitude = shopLongtitude;
    }

    public void setUserLogo(String userLogo) {
        this.userLogo = userLogo;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getWid() {
        return wid;
    }
}
