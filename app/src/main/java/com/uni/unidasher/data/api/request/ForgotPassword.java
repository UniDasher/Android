package com.uni.unidasher.data.api.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/7/22.
 */
public class ForgotPassword {
    @SerializedName("mobilePhone")
    String phone;

    @SerializedName("phoneCode")
    String phoneCode;

    @SerializedName("newPassword")
    String newPwd;

    public ForgotPassword(String phone, String newPwd, String phoneCode) {
        this.phone = phone;
        this.newPwd = newPwd;
        this.phoneCode = phoneCode;
    }
}
