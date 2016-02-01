package com.uni.unidasher.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.uni.unidasher.R;
import com.uni.unidasher.ui.utils.Extras;

import java.util.ArrayList;
import java.util.List;

public class MapNavActivity extends EventBusActivity implements BDLocationListener{
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    public LocationClient mLocationClient = null;
    private RoutePlanSearch mSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map_nav);
        findViews();
    }

    private void findViews(){
        mMapView = (MapView)findViewById(R.id.mapView);
        mBaiduMap = mMapView.getMap();
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(this);
        initLocationOption();
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(listener);

        mLocationClient.requestLocation();
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

    private void searchRoute(LatLng currentLatLng){
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
//            String senderLatitude = bundle.getString(Extras.Extra_Sender_Latitude);
//            String senderLongtitude = bundle.getString(Extras.Extra_Sender_Longtitude);
//            LatLng senderLatLng = new LatLng(Double.parseDouble(senderLatitude),Double.parseDouble(senderLongtitude));

            String shopLatitude = bundle.getString(Extras.Extra_Shop_Latitude);
            String shopLongtitude = bundle.getString(Extras.Extra_Shop_Longtitude);
            LatLng shopLatLng = new LatLng(Double.parseDouble(shopLatitude.trim()),Double.parseDouble(shopLongtitude.trim()));

            String userLatitude = bundle.getString(Extras.Extra_User_Latitude);
            String userLongtitude = bundle.getString(Extras.Extra_User_Longtitude);
            LatLng userLatLng = new LatLng(Double.parseDouble(userLatitude),Double.parseDouble(userLongtitude));

//            mSearch.walkingSearch(new WalkingRoutePlanOption()
//                    .from(PlanNode.withLocation(currentLatLng))
//                    .to(PlanNode.withLocation(shopLatLng)));
            addMarker(shopLatLng);
            List<PlanNode> planNodes = new ArrayList<>();
            planNodes.add(PlanNode.withLocation(shopLatLng));
            mSearch.drivingSearch(new DrivingRoutePlanOption()
                    .from(PlanNode.withLocation(currentLatLng))
                    .to(PlanNode.withLocation(userLatLng))
                    .passBy(planNodes));
        }
    }

    public void addMarker(LatLng point){
        mBaiduMap.clear();
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.map_nav_shop_icon);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap)
                .draggable(false);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
    }

    OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
        public void onGetWalkingRouteResult(WalkingRouteResult result) {
            //获取步行线路规划结果
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(MapNavActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                WalkingRouteOverlay overlay = new WalkingRouteOverlay(mBaiduMap);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            }
        }
        public void onGetTransitRouteResult(TransitRouteResult result) {
            //获取公交换乘路径规划结果
        }
        public void onGetDrivingRouteResult(DrivingRouteResult result) {
            //获取驾车线路规划结果
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(MapNavActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            }
        }
    };

    //定位数据回调
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if(bdLocation==null){
            return;
        }
        LatLng currentLatLng = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
        searchRoute(currentLatLng);
    }

}
