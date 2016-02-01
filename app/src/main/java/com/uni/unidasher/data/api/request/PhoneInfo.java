package com.uni.unidasher.data.api.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/7/10.
 */
public class PhoneInfo {
    @SerializedName("uid")
    String userId;

    @SerializedName("mobilePhone")
    String mobilePhone;

    @SerializedName("phoneCode")
    String phoneCode;

    public PhoneInfo(String userId, String mobilePhone, String phoneCode) {
        this.userId = userId;
        this.mobilePhone = mobilePhone;
        this.phoneCode = phoneCode;
    }
}
