package com.uni.unidasher.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.uni.unidasher.AppConstants;
import com.uni.unidasher.DasherApplication;
import com.uni.unidasher.data.entity.UserInfo;

/**
 * Created by Administrator on 2015/5/15.
 */
public class DataPrefs {
    private static final String SHARED_PREFERENCES = "DasherMainPrefs";
    private static final SharedPreferences sSharedPreferences = DasherApplication.getInstance().getSharedPreferences(
            SHARED_PREFERENCES, Context.MODE_PRIVATE );

    private static final String SHARED_HOST = ".Host";
    private static final String SHARED_TOKEN = ".Token";
    private static final String SHARED_USERID = ".UserId";
    private static final String SHARED_HX_ID = ".HxId";
    private static final String SHARED_UserName = ".UserName";

    //用户基本信息
    private static final String SHARED_NICK_NAME = ".NickName";
    private static final String SHARED_FIRST_NMAE = ".FirstName";
    private static final String SHARED_LAST_NMAE = ".LastName";
    private static final String SHARED_LOGO = ".UserLogo";
    private static final String SHARED_BANK_ACOUNT = ".BankAcount";
    private static final String SHARED_BANK_TYPE = ".BankType";
    private static final String SHARED_PHONE = ".Phone";
    private static final String SHARED_EMAIL = ".Email";
    private static final String SHARED_GOOD = ".Good";
    private static final String SHARED_BAD = ".Bad";
    private static final String SHARED_STATUS = ".Status";

    static void setUserInfo(UserInfo userInfo){
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.putString(SHARED_NICK_NAME,userInfo.getNickName());
        editor.putString(SHARED_FIRST_NMAE, userInfo.getFirstName());
        editor.putString(SHARED_LAST_NMAE, userInfo.getLastName());
        editor.putString(SHARED_LOGO, userInfo.getLogo());
        editor.putString(SHARED_BANK_ACOUNT, userInfo.getBankAccount());
        editor.putString(SHARED_BANK_TYPE, userInfo.getBankType());
        editor.putString(SHARED_PHONE, userInfo.getMobilePhone());
        editor.putString(SHARED_EMAIL, userInfo.getEmail());
        editor.putInt(SHARED_GOOD, userInfo.getGoodEvaluate());
        editor.putInt(SHARED_BAD, userInfo.getBadEvaluate());
        editor.putInt(SHARED_STATUS, userInfo.getStatus());
        editor.commit();
    }



    static UserInfo getUserInfo(){
        return new UserInfo(
                sSharedPreferences.getString(SHARED_NICK_NAME,null),
                sSharedPreferences.getString(SHARED_FIRST_NMAE,null),
                sSharedPreferences.getString(SHARED_LAST_NMAE,null),
                sSharedPreferences.getString(SHARED_LOGO,null),
                sSharedPreferences.getString(SHARED_BANK_ACOUNT,null),
                sSharedPreferences.getString(SHARED_BANK_TYPE,null),
                sSharedPreferences.getString(SHARED_PHONE,null),
                sSharedPreferences.getString(SHARED_EMAIL,null),
                sSharedPreferences.getInt(SHARED_GOOD, 0),
                sSharedPreferences.getInt(SHARED_BAD, 0),
                sSharedPreferences.getInt(SHARED_STATUS, 0)
        );
    }



    static String getSharedHost() {
        return sSharedPreferences.getString(SHARED_HOST, AppConstants.HOST);
    }

    static void setSharedHost(String host) {
        sSharedPreferences.edit().putString(SHARED_HOST, host).apply();
    }

    static void setToken(String token) {
        sSharedPreferences.edit().putString(SHARED_TOKEN, token).commit();
    }

    static String getToken() {
        return sSharedPreferences.getString(SHARED_TOKEN, null);
    }

    static void setUserId(String userId){
        sSharedPreferences.edit().putString(SHARED_USERID, userId).commit();
    }

    static String getUserId(){
        return sSharedPreferences.getString(SHARED_USERID,"");
    }

    static String getUserLogo(){
        return sSharedPreferences.getString(SHARED_LOGO,"");
    }

    static void  setUserLogo(String userLogo){
        sSharedPreferences.edit().putString(SHARED_LOGO, userLogo).commit();
    }

    static String getHxId(){
        return sSharedPreferences.getString(SHARED_HX_ID,"zdd5");
    }

    static void setHxId(String hxId){
        sSharedPreferences.edit().putString(SHARED_HX_ID, hxId).commit();
    }

    static String getUserName(){
        return sSharedPreferences.getString(SHARED_UserName,"");
    }

    static void setUserName(String userName){
        sSharedPreferences.edit().putString(SHARED_UserName, userName).commit();
    }

    static void logOut(){
        setToken(null);
        setUserId(null);
        setHxId(null);
        setUserName(null);
    }


}
