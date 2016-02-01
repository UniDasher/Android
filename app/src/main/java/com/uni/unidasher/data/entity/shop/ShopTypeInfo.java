package com.uni.unidasher.data.entity.shop;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/7/15.
 */
public class ShopTypeInfo {
    @SerializedName("typeTab")
    String typeName;

    public ShopTypeInfo(){}

    public ShopTypeInfo(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
