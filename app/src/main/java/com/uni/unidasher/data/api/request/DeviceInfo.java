package com.uni.unidasher.data.api.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/7/9.
 */
public class DeviceInfo {
    @SerializedName("cid")//push id
    String cid;

    public DeviceInfo(String cid) {
        this.cid = cid;
    }
}
