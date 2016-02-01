package com.uni.unidasher.data.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/6/26.
 */
public class ResUserInfo {
    @SerializedName("uid")
    String userId;

    public ResUserInfo(String userId) {
        this.userId = userId;
    }
}
