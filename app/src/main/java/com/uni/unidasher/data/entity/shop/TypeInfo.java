package com.uni.unidasher.data.entity.shop;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/6/23.
 * dish type
 */
public class TypeInfo implements Serializable {
    @SerializedName("name")
    String typeName;

    @SerializedName("id")
    int id;

    public String getTypeName() {
        return typeName;
    }

    public int getId() {
        return id;
    }
}
