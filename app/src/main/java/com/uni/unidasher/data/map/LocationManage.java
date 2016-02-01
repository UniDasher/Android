package com.uni.unidasher.data.map;

import android.content.Context;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;

/**
 * Created by Administrator on 2015/5/21.
 */
public class LocationManage {
    public LocationClient mLocationClient = null;
    public GeoCoder mSearch;
    public LocationManage(Context context){
        mLocationClient = new LocationClient(context);
        mSearch = GeoCoder.newInstance();
        initLocationOption();

    }
    private void initLocationOption(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
//        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
//        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
        option.setOpenGps(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    public void requestionLocation(){
        if(mLocationClient.isStarted()){
            mLocationClient.requestLocation();
        }
    }

    public void stopLocation(){
        if(mLocationClient.isStarted()){
            mLocationClient.stop();
        }
        if(mSearch!=null){
            mSearch.destroy();
        }
    }

    public void getAddressFromLocation(LatLng latLng){
        mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
    }

    public void getLocationFromAddress(String city,String address){
        mSearch.geocode(new GeoCodeOption().city(city).address(address));
    }

    public void registerLocationListener(BDLocationListener locationListener){
        mLocationClient.registerLocationListener(locationListener);
    }

    public void registerGeoCoderListener(OnGetGeoCoderResultListener listener){
        mSearch.setOnGetGeoCodeResultListener(listener);
    }
}
