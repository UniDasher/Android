package com.uni.unidasher.data.event;

import com.uni.unidasher.data.datamodel.databaseAndApi.ShopInfo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/6/23.
 */
public class ShopListEvent {
    ArrayList<ShopInfo> shopInfos;
    String errorMsg;

    public ShopListEvent(ArrayList<ShopInfo> shopInfos,String errorMsg) {
        this.shopInfos = shopInfos;
        this.errorMsg = errorMsg;
    }

    public ArrayList<ShopInfo> getShopList() {
        return shopInfos;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
