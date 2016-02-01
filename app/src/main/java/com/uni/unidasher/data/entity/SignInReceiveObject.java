package com.uni.unidasher.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/6/19.
 */
public class SignInReceiveObject {
    @SerializedName("uid")
    String userId;

    @SerializedName("authCode")
    String authToken;

    @SerializedName("name")
    String userName;

    @SerializedName("cid")
    String hxId;

    @SerializedName("logo")
    String logo;

    public String getAuthToken() {
        return authToken;
    }

    public String getHxId() {
        return hxId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getLogo() {
        return logo;
    }
}
