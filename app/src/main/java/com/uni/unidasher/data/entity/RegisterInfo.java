package com.uni.unidasher.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/5/22.
 */
public class RegisterInfo {
    @SerializedName("mobilePhone")
    private String phoneNum;

    @SerializedName("password")
    private String pwd;

    @SerializedName("phoneCode")
    private String validateNum;

    @SerializedName("nickName")
    private String userName;

    public RegisterInfo(String userName, String phoneNum, String pwd, String validateNum) {
        this.userName = userName;
        this.phoneNum = phoneNum;
        this.pwd = pwd;
        this.validateNum = validateNum;
    }
}
