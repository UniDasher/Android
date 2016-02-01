package com.uni.unidasher.ui.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.uni.unidasher.AppConstants;
import com.uni.unidasher.DasherApplication;

/**
 * Created by Administrator on 2015/6/19.
 */
public class DasherAppPrefs {
    private static final String SHARED_PREFERENCES = "DasherAppPrefs";
    private static final SharedPreferences sSharedPreferences = DasherApplication.getInstance().getSharedPreferences(
            SHARED_PREFERENCES, Context.MODE_PRIVATE );

    private static final String SHARED_USER_IDENTITY = ".UserIdentity";


    public static void setUserIdentity(int identity){
        sSharedPreferences.edit().putInt(SHARED_USER_IDENTITY, identity).commit();
    }

    public static int getUserIdentity(){
        return sSharedPreferences.getInt(SHARED_USER_IDENTITY,0);
    }

}
