package com.uni.unidasher.data.entity.shop;

import com.google.gson.annotations.SerializedName;
import com.uni.unidasher.data.datamodel.databaseAndApi.DishInfo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/6/23.
 */
public class ShopDishList implements Serializable {
    @SerializedName("list")
    ArrayList<DishInfo> dishInfos;

    @SerializedName("typelist")
    ArrayList<TypeInfo> typeInfos;

    public ArrayList<DishInfo> getDishInfos() {
        return dishInfos;
    }

    public ArrayList<TypeInfo> getTypeInfos() {
        return typeInfos;
    }
}
