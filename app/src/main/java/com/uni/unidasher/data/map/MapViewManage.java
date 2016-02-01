package com.uni.unidasher.data.map;

import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.uni.unidasher.R;
import com.uni.unidasher.data.datamodel.databaseAndApi.ShopInfo;
import com.uni.unidasher.ui.utils.Extras;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/19.
 */
public class MapViewManage {
    private MapView mapView;
    private BaiduMap mBaiduMap = null;
    private Marker selfMarker,moveMarker;
    private List<Marker> shopMarkers;

    private BaiduMap.OnMarkerDragListener markerDragListener;
    private BaiduMap.OnMapClickListener mapClickListener;
    private BaiduMap.OnMarkerClickListener markerClickListener;

    public MapViewManage(MapView mapView){
        this.mapView = mapView;
        initMapView();
    }

    private void initMapView(){
        mapView.showZoomControls(false);
        mBaiduMap = mapView.getMap();
    }

    public BaiduMap getBaiduMap(){
        return mBaiduMap;
    }

    public void registerMapClickListener(BaiduMap.OnMapClickListener listener){
        mBaiduMap.setOnMapClickListener(listener);
    }

    public void registerMarkerClickListener(BaiduMap.OnMarkerClickListener listener){
        mBaiduMap.setOnMarkerClickListener(listener);
    }

    public void registerMarkerDragListener(BaiduMap.OnMarkerDragListener listener){
        mBaiduMap.setOnMarkerDragListener(listener);
    }

    public void registerMapStatusChangeListener(BaiduMap.OnMapStatusChangeListener listener){
        mBaiduMap.setOnMapStatusChangeListener(listener);
    }

    //展示自己当前位置
    public Marker addSelfMarker(double latitude,double longitude){
        mBaiduMap.clear();
        LatLng point = new LatLng(latitude, longitude);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.self_marker);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().anchor(0.5f,0.5f)
                .position(point)
                .icon(bitmap)
                .draggable(false);
        //在地图上添加Marker，并显示
        selfMarker = (Marker)mBaiduMap.addOverlay(option);
        animateToCenter(point);
        Bundle b = new Bundle();
        b.putString("name", "经纬度:");
        selfMarker.setExtraInfo(b);
        return selfMarker;
    }

    /**
     * 添加用户移动的位置
     * @param latitude
     * @param longitude
     * @return
     */
    public Marker addMoveMarker(double latitude,double longitude){
        LatLng point = new LatLng(latitude, longitude);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.center_marker_lock);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap)
                .draggable(false);
        //在地图上添加Marker，并显示
        moveMarker = (Marker)mBaiduMap.addOverlay(option);
        animateToCenter(point);
//        Bundle b = new Bundle();
//        b.putString("name", "经纬度:");
//        selfMarker.setExtraInfo(b);
        return moveMarker;
    }

    public void animateToCenter(LatLng point){
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(point)
                .zoom(18)
                .build();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mMapStatus));
    }



    public Marker addOtherMarker(double latitude,double longitude){
        mBaiduMap.clear();
        LatLng point = new LatLng(latitude, longitude);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.self_marker);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap)
                .draggable(false);
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(point)
                .zoom(18)
                .build();
        //在地图上添加Marker，并显示
        selfMarker = (Marker)mBaiduMap.addOverlay(option);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mMapStatus));
        Bundle b = new Bundle();
        b.putString("name", "经纬度:");
        selfMarker.setExtraInfo(b);
        return selfMarker;
    }

    public void addShopMarker(ArrayList<ShopInfo> shopInfos,boolean isCustomer){
        clearShopMarkers();
        if(shopInfos!=null&&shopInfos.size()>0){
            for(ShopInfo shopInfo:shopInfos){
                LatLng point = new LatLng(Double.parseDouble(shopInfo.getLatitude()), Double.parseDouble(shopInfo.getLongitude()));
                //构建Marker图标
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(isCustomer?R.mipmap.shop_marker_normal:R.mipmap.order_shop_marker);
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions()
                        .position(point)
                        .icon(bitmap)
                        .draggable(false);
                Marker marker = (Marker)mBaiduMap.addOverlay(option);
                Bundle b = new Bundle();
                b.putSerializable(Extras.Extra_ShopInfo,shopInfo);
                marker.setExtraInfo(b);
                shopMarkers.add(marker);
            }
        }
    }

    public void clearShopMarkers(){
        if(shopMarkers!=null&&shopMarkers.size()>0){
            for(Marker marker:shopMarkers){
                marker.remove();
            }
        }
        shopMarkers = new ArrayList<>();
    }

    public void removeMoveMarker(){
        if(moveMarker!=null){
            moveMarker.remove();
        }
    }
}
