package com.uni.unidasher.data.event;

import com.uni.unidasher.data.entity.shop.ShopDishList;

/**
 * Created by Administrator on 2015/6/23.
 */
public class ShopDishEvent {
    ShopDishList shopDishList;

    public ShopDishEvent(ShopDishList shopDishList) {
        this.shopDishList = shopDishList;
    }

    public ShopDishList getShopDishList() {
        return shopDishList;
    }
}
