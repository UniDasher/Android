package com.uni.unidasher.data.datamodel.databaseAndApi;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.uni.unidasher.AppConstants;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/6/23.
 */
@DatabaseTable(tableName = "ShopInfoTable")
public class ShopInfo implements DatabaseModelProvider,Serializable{
/*    sid	商家的编号	String
    name	商家店名	String
    logo	商家图片	String
    typeTab	商家类型标签	String
    goodEvaluate	商家的好评数	Int
    badEvaluate	商家的差评数	Int
    phone	商家的联系方式	String
    email	商家的邮件	String
    address	商家的地址	String
    subscribe	商家的描述	String
    longitude	经度	String
    latitude	纬度	String
    serviceTimes	订餐时间	String		多个时间段
    openService	是否营业	Int		0不营业
    1营业

    */

    @DatabaseField(id = true,columnName = "id", index = true, unique = true)
    private String userId;

    @SerializedName("sid")
    @DatabaseField(columnName = "sid")
    private String sid;

    @SerializedName("name")
    @DatabaseField(columnName = "name")
    private String name;

    @SerializedName("logo")
    @DatabaseField(columnName = "logo")
    private String logo;

    @SerializedName("serviceTimes")
    @DatabaseField(columnName = "serviceTimes")
    private String serviceTimes;

    @SerializedName("typeTab")
    @DatabaseField(columnName = "typeTab")
    private String typeTab;

    @SerializedName("goodEvaluate")
    @DatabaseField(columnName = "goodEvaluate")
    private int goodEvaluate;

    @SerializedName("badEvaluate")
    @DatabaseField(columnName = "badEvaluate")
    private int badEvaluate;

    @SerializedName("phone")
    @DatabaseField(columnName = "phone")
    private  String phone;

    @SerializedName("email")
    @DatabaseField(columnName = "email")
    private String email;

    @SerializedName("address")
    @DatabaseField(columnName = "address")
    private String address;

    @SerializedName("subscribe")
    @DatabaseField(columnName = "subscribe")
    private String subscribe;

    @SerializedName("latitude")
    @DatabaseField(columnName = "latitude")
    private String latitude;

    @SerializedName("longitude")
    @DatabaseField(columnName = "longitude")
    private String longitude;

    @SerializedName("orderCount")
    private int orderCount;

    @DatabaseField(columnName = "openService")
    @SerializedName("openService")
    private int openService;

    public ShopInfo() {
    }

    public int getBadEvaluate() {
        return badEvaluate;
    }

    public int getGoodEvaluate() {
        return goodEvaluate;
    }

    public String getLogo() {
        return logo;
    }

    public String getName() {
        return name;
    }

    public String getServiceTimes() {
        return serviceTimes;
    }

    public String getTypeTab() {
        return typeTab;
    }

    public String getSid() {
        return sid;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getSubscribe() {
        return subscribe;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public boolean isOpenService() {
        return openService == 0?false:true;
    }

    @Override
    public boolean hasStringMainKey() {
        return true;
    }

    @Override
    public void deleteNestedObjects(HashMap<Class, RuntimeExceptionDao<?, ?>> classRuntimeDaosMap) throws SQLException {

    }

    @Override
    public void rewriteCollections(HashMap<Class, RuntimeExceptionDao<?, ?>> classRuntimeDaosMap) throws SQLException {

    }
}
