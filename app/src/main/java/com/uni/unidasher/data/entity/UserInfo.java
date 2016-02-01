package com.uni.unidasher.data.entity;

import com.google.gson.annotations.SerializedName;
import com.uni.unidasher.AppConstants;

/**
 * Created by Administrator on 2015/6/26.
 */
public class UserInfo {
/*    uid	用户编号	String
    firstName	姓	String
    lastName	名	String
    logo	头像图片名	String
    totalRevenue	用户总收益	Float
    balance	账户余额	Float
    bankAccount	银行账号	String
    bankType	银行类型	String
    mobilePhone	手机号	String
    email	邮箱	String
    goodEvaluate	好评数	Int
    badEvaluate	差评数	Int
    status	状态	Int
    createDate	创建时间	DateTime

    -1不可用状态
    0默认状态
    1审核中
    2审核通过
    3冻结状态
    4审核未通过

    */

    @SerializedName("nickName")
    String nickName;

    @SerializedName("firstName")
    String firstName;

    @SerializedName("lastName")
    String lastName;

    @SerializedName("logo")
    String logo;

    @SerializedName("bankAccount")
    String bankAccount;

    @SerializedName("bankType")
    String bankType;

    @SerializedName("mobilePhone")
    String mobilePhone;

    @SerializedName("email")
    String email;

    @SerializedName("goodEvaluate")
    int goodEvaluate;

    @SerializedName("badEvaluate")
    int badEvaluate;

    @SerializedName("status")
    int status;

    public UserInfo(){}

    public UserInfo(String nickName,String firstName, String lastName, String logo, String bankAccount, String bankType, String mobilePhone, String email, int goodEvaluate, int badEvaluate, int status) {
        this.nickName = nickName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.logo = logo;
        this.bankAccount = bankAccount;
        this.bankType = bankType;
        this.mobilePhone = mobilePhone;
        this.email = email;
        this.goodEvaluate = goodEvaluate;
        this.badEvaluate = badEvaluate;
        this.status = status;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLogo() {
        return logo;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public String getBankType() {
        return bankType;
    }

    public String getEmail() {
        return email;
    }

    public int getGoodEvaluate() {
        return goodEvaluate;
    }

    public int getBadEvaluate() {
        return badEvaluate;
    }

    public int getStatus() {
        return status;
    }

    public String getNickName() {
        return nickName;
    }
}
