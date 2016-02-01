package com.uni.unidasher.data.entity.shop;

import com.google.gson.annotations.SerializedName;
import com.uni.unidasher.data.datamodel.databaseAndApi.ShopInfo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/6/23.
 */
public class ShopList implements Serializable {
    @SerializedName("list")
    ArrayList<ShopInfo> shopInfos;

    public ArrayList<ShopInfo> getShopInfos() {
        return shopInfos;
    }
}
