package com.uni.unidasher.data.entity;

import com.google.gson.annotations.SerializedName;

import java.io.File;

import retrofit.mime.TypedFile;

/**
 * Created by Administrator on 2015/7/8.
 */
public class UserLogoFile {
    @SerializedName("uid")
    String userId;
    @SerializedName("file")
    File file;

    public UserLogoFile(File file, String userId) {
        this.file = file;
        this.userId = userId;
    }
}
