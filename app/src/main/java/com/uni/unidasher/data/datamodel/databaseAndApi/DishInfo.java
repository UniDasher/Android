package com.uni.unidasher.data.datamodel.databaseAndApi;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/6/23.
 */
@DatabaseTable(tableName = "DishInfoTable")
public class DishInfo implements DatabaseModelProvider, Serializable {
    /*  did	菜品的编号	Int
        sid	商家的编号	String
        name	餐品名	String
        price	餐品的价格	Int
        typeId	餐品的类型编号	Int
        typeName	餐品的类型名	String
        chilies	餐品的味道	String
        description	餐品的描述	String
        */
//    @DatabaseField(columnName = "databaseId", generatedId = true, index = true, unique = true)
//    private long mDatabaseId;

    @DatabaseField(id = true, index = true, unique = true, columnName = "id")
//    @DatabaseField(columnName = "id")
    @SerializedName("did")
    private String dishId;

    @DatabaseField(columnName = "sid")
    @SerializedName("sid")
    private String sid;

    @DatabaseField(columnName = "dishName")
    @SerializedName("name")
    private String dishName;

    @DatabaseField(columnName = "price")
    @SerializedName("price")
    private int price;

    @DatabaseField(columnName = "typeId")
    @SerializedName("typeId")
    private int typeId;

    @DatabaseField(columnName = "typeName")
    @SerializedName("typeName")
    private String typeName;

    @DatabaseField(columnName = "taste")
    @SerializedName("chilies")
    private String taste;

    @DatabaseField(columnName = "description")
    @SerializedName("description")
    private String description;

    @DatabaseField(columnName = "count")
    @SerializedName("count")
    private int count = 0;

    @SerializedName("orderCount")
    private int orderCount;

    @DatabaseField(foreign=true, foreignAutoCreate=true, foreignAutoRefresh=true, columnName = "shoppingBasket")
    private ShoppingBasket shoppingBasket;


    public DishInfo() {
    }

    public String getDescription() {
        return description;
    }

    public String getDishName() {
        return dishName;
    }

    public String getDishId() {
        return dishId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setTaste(String taste) {
        this.taste = taste;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getPrice() {
        return price;
    }

    public String getSid() {
        return sid;
    }

    public String getTaste() {
        return taste;
    }

    public int getTypeId() {
        return typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

//    public long getmDatabaseId() {
//        return mDatabaseId;
//    }
//
//    public void setmDatabaseId(long mDatabaseId) {
//        this.mDatabaseId = mDatabaseId;
//    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    @Override
    public boolean hasStringMainKey() {
        return false;
    }

    @Override
    public void deleteNestedObjects(HashMap<Class, RuntimeExceptionDao<?, ?>> classRuntimeDaosMap) throws SQLException {

    }

    @Override
    public void rewriteCollections(HashMap<Class, RuntimeExceptionDao<?, ?>> classRuntimeDaosMap) throws SQLException {

    }
}
