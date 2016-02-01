package com.uni.unidasher.data.api.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/7/10.
 */
public class PasswordInfo {
    @SerializedName("uid")
    String userId;
    @SerializedName("oldPassword")
    String oldPwd;
    @SerializedName("newPassword")
    String newPwd;

    public PasswordInfo(String userId, String oldPwd, String newPwd) {
        this.userId = userId;
        this.oldPwd = oldPwd;
        this.newPwd = newPwd;
    }
}
