package com.uni.unidasher.data.event;

import com.uni.unidasher.data.datamodel.databaseAndApi.DishInfo;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/6/30.
 */
public class ReOrderDishListEvent {
    private ArrayList<DishInfo> dishInfos;

    public ReOrderDishListEvent(ArrayList<DishInfo> dishInfos) {
        this.dishInfos = dishInfos;
    }

    public ArrayList<DishInfo> getDishInfos() {
        return dishInfos;
    }
}
