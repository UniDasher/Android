package com.uni.unidasher.data.datamodel.databaseAndApi;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2015/6/24.
 */
@DatabaseTable(tableName = "ShoppingBasketTable")
public class ShoppingBasket implements DatabaseModelProvider,Serializable{

    public static final String ID = "uid";

    @DatabaseField(id = true,columnName = ID, index = true, unique = true)
    private String id;

    @DatabaseField(columnName = "sendAddress")
    private String sendAddress;

    @DatabaseField (foreign = true,foreignAutoRefresh = true, columnName = "shopInfoId")
    private ShopInfo shopInfo;

//    @DatabaseField (foreign = true,foreignAutoRefresh = true, foreignAutoCreate  = true, columnName = "placeOrderUserInfoId")
//    private PlaceOrderUserInfo placeOrderUserInfo;

    private List<DishInfo> dishInfos;

    @ForeignCollectionField(columnName = "dishInfos",eager = true)
    private ForeignCollection<DishInfo> dishInfoForeignCollection;

    public ShoppingBasket() {
    }

    public List<DishInfo> getDishInfos() {
        return dishInfos;
    }

    public void setDishInfos(List<DishInfo> dishInfos) {
        this.dishInfos = dishInfos;
    }

//    public long getmDatabaseId() {
//        return mDatabaseId;
//    }
//
//    public void setmDatabaseId(long mDatabaseId) {
//        this.mDatabaseId = mDatabaseId;
//    }


    public void setId(String id) {
        this.id = id;
    }

    public String getSendAddress() {
        return sendAddress;
    }

    public void setSendAddress(String sendAddress) {
        this.sendAddress = sendAddress;
    }

    public ShopInfo getShopInfo() {
        return shopInfo;
    }

    public void setShopInfo(ShopInfo shopInfo) {
        this.shopInfo = shopInfo;
    }

//    public PlaceOrderUserInfo getPlaceOrderUserInfo() {
//        return placeOrderUserInfo;
//    }
//
//    public void setPlaceOrderUserInfo(PlaceOrderUserInfo placeOrderUserInfo) {
//        this.placeOrderUserInfo = placeOrderUserInfo;
//    }

    @Override
    public boolean hasStringMainKey() {
        return false;
    }

    @Override
    public void rewriteCollections(HashMap<Class, RuntimeExceptionDao<?, ?>> classRuntimeDaosMap) throws SQLException {
        if (dishInfos != null) {
            RuntimeExceptionDao<ShoppingBasket, Long> sequenceDao = (RuntimeExceptionDao<ShoppingBasket, Long>) classRuntimeDaosMap.get(this.getClass());
            sequenceDao.assignEmptyForeignCollection(this, "dishInfos");
            if(dishInfoForeignCollection!=null){
                dishInfoForeignCollection.clear();
//                Iterator iterator = dishInfoForeignCollection.iterator();
//                while (iterator.hasNext()){
//                    DishInfo d = (DishInfo)iterator.next();
////                    if(d.getDishId().equals(dishInfo.getDishId())){
//                        dishInfoForeignCollection.remove(d);
////                        break;
////                    }
//                }
            }
            for (DishInfo dishInfo : dishInfos) {
                if(dishInfo.getCount()>0){
                    dishInfoForeignCollection.add(dishInfo);
                }
                dishInfo.rewriteCollections(classRuntimeDaosMap);
            }
        } else if (dishInfos == null && dishInfoForeignCollection != null) {
            dishInfos = new ArrayList<DishInfo>();
            for (DishInfo dishInfo : dishInfoForeignCollection) {
                dishInfos.add(dishInfo);
                dishInfo.rewriteCollections(classRuntimeDaosMap);
            }
        }
    }

    @Override
    public void deleteNestedObjects(HashMap<Class, RuntimeExceptionDao<?, ?>> classRuntimeDaosMap) throws SQLException {
        if (dishInfoForeignCollection != null) {
            RuntimeExceptionDao<DishInfo, String> responseDao = (RuntimeExceptionDao<DishInfo, String>) classRuntimeDaosMap.get(DishInfo.class);
            for (DishInfo dishInfo : dishInfoForeignCollection) {
                dishInfo.deleteNestedObjects(classRuntimeDaosMap);
                responseDao.deleteById(dishInfo.getDishId());
            }
        }
    }
}
