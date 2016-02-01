package com.uni.unidasher.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/5/23.
 */
public class LoginInfo {
    @SerializedName("mobilePhone")
    private String userName;

    @SerializedName("password")
    private String pwd;

    public LoginInfo(String userName, String pwd) {
        this.userName = userName;
        this.pwd = pwd;
    }
}
