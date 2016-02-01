package com.uni.unidasher.data.event;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/6/19.
 */
public class SignUpEvent {
    boolean success;
    String errorInfo;
    @SerializedName("uid")
    String userId;


    public SignUpEvent() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
