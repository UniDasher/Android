package com.uni.unidasher.data;

import android.content.Context;

import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.uni.unidasher.data.datamodel.databaseAndApi.ShopInfo;
import com.uni.unidasher.data.map.LocationManage;
import com.uni.unidasher.data.map.MapViewManage;
import com.uni.unidasher.ui.utils.DasherAppPrefs;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/5/19.
 */
public class DasherMgr {
    public static int UserIdentityCustomer = 0;
    public static int UserIdentitySender = 1;

    public static DasherMgr dasherMgr;
    public Context mContext;
    public MapViewClass mMapViewClass;
    public LocationClass locationClass;

    public static DasherMgr getInstance(Context context){
        if(dasherMgr == null){
            dasherMgr = new DasherMgr(context);
        }
        return dasherMgr;
    }

    public DasherMgr(Context context){
        mContext = context;
    }


    /**
     * 当前身份判断以及身份切换
     * @return
     */
    public boolean isCustomer(){
        return DasherAppPrefs.getUserIdentity() == UserIdentityCustomer?true:false;
    }

    public void switchUserIdentity(){
        if(DasherAppPrefs.getUserIdentity() == UserIdentityCustomer){
            DasherAppPrefs.setUserIdentity(UserIdentitySender);
        }else{
            DasherAppPrefs.setUserIdentity(UserIdentityCustomer);
        }
    }

    /**
     * 地图以及定位的初始化，获取管理对象
     * @param mapView
     */
    public MapViewClass initMap(MapView mapView){
        return mMapViewClass = new MapViewClass(mapView);
    }

    public LocationClass initLocation(){
        return locationClass = new LocationClass(mContext);
    }

    public class MapViewClass{
        public MapViewManage mMapViewManage;

        public MapViewClass(MapView mapView){
            initMap(mapView);
        }

        public void initMap(MapView mapView){
            mMapViewManage = new MapViewManage(mapView);
        }

        public BaiduMap getBaiduMap(){
            return mMapViewManage.getBaiduMap();
        }

        public Marker addSelfMarker(double latitude,double longitude){
            return mMapViewManage.addSelfMarker(latitude, longitude);
        }

        public Marker addMoveMarker(double latitude,double longitude){
            return mMapViewManage.addMoveMarker(latitude, longitude);
        }

        public void registerMapClickListener(BaiduMap.OnMapClickListener listener){
            mMapViewManage.registerMapClickListener(listener);
        }

        public void registerMarkerClickListener(BaiduMap.OnMarkerClickListener listener){
            mMapViewManage.registerMarkerClickListener(listener);
        }

        public void registerMarkerDragListener(BaiduMap.OnMarkerDragListener listener){
            mMapViewManage.registerMarkerDragListener(listener);
        }

        public void registerMapStatusChangeListener(BaiduMap.OnMapStatusChangeListener listener){
            mMapViewManage.registerMapStatusChangeListener(listener);
        }

        public void animateToCenter(LatLng point){
            if(point != null){
                mMapViewManage.animateToCenter(point);
            }
        }

        public void removeMoveMarker(){
            mMapViewManage.removeMoveMarker();
        }


        public void addShopMarker(ArrayList<ShopInfo> shopInfos,boolean isCustomer){
            mMapViewManage.addShopMarker(shopInfos,isCustomer);
        }

        public void clearShopMarkers(){
            mMapViewManage.clearShopMarkers();
        }

    }

    public class LocationClass{
        public LocationManage locationManage;
        public LocationClass(Context context){
            locationManage = new LocationManage(context);
        }

        public void requestionLocation(){
            locationManage.requestionLocation();
        }

        public void stopLocation(){
            locationManage.stopLocation();
        }

        public void getAddressFromLocation(LatLng latLng){
            locationManage.getAddressFromLocation(latLng);
        }
        public void getLocationFromAddress(String city,String address){
            locationManage.getLocationFromAddress(city,address);
        }

        public void registerLocationListener(BDLocationListener locationListener){
            locationManage.registerLocationListener(locationListener);
        }

        public void registerGeoCoderListener(OnGetGeoCoderResultListener listener){
            locationManage.registerGeoCoderListener(listener);
        }
    }
}
