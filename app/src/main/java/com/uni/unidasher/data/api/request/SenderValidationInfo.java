package com.uni.unidasher.data.api.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/7/9.
 */
public class SenderValidationInfo {
    @SerializedName("firstName")
    String firstName;
    @SerializedName("lastName")
    String lastName;
    @SerializedName("bankAccount")
    String bankNum;
    @SerializedName("bankType")
    String bankDes;

//    @SerializedName("status")
//    int status;

    public SenderValidationInfo(String firstName,String bankNum,String bankDes) {
        this.bankDes = bankDes;
        this.bankNum = bankNum;
        this.firstName = firstName;
        this.lastName = "";
    }
}
