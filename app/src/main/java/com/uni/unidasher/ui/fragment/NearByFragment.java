package com.uni.unidasher.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.common.eventbus.Subscribe;
import com.uni.unidasher.R;
import com.uni.unidasher.data.DasherMgr;
import com.uni.unidasher.data.DataProvider;
import com.uni.unidasher.data.datamodel.databaseAndApi.UserAdressInfo;
import com.uni.unidasher.data.datamodel.databaseAndApi.ShopInfo;
import com.uni.unidasher.data.entity.UserInfo;
import com.uni.unidasher.data.event.ShopListEvent;
import com.uni.unidasher.data.status.Status;
import com.uni.unidasher.data.utils.BeanUtil;
import com.uni.unidasher.listener.OnRecyclerViewItemClickListener;
import com.uni.unidasher.ui.activity.AdressSearchActivity;
import com.uni.unidasher.ui.activity.ModifyInfoActivity;
import com.uni.unidasher.ui.activity.RestaurantOrderActivity;
import com.uni.unidasher.ui.activity.TabActivity;
import com.uni.unidasher.ui.activity.restaurant.RestaurantActivity;
import com.uni.unidasher.ui.adapter.RestaurantListAdapter;
import com.uni.unidasher.ui.dialog.AlertToastDialog;
import com.uni.unidasher.ui.dialog.ResFilterPopDialog;
import com.uni.unidasher.ui.utils.Alert;
import com.uni.unidasher.ui.utils.Extras;
import com.uni.unidasher.ui.utils.HandlerHelper;
import com.uni.unidasher.ui.views.MapInfoWindowView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2015/5/19.
 */
public class NearByFragment extends EventBusFragment
        implements BDLocationListener,BaiduMap.OnMarkerClickListener,BaiduMap.OnMarkerDragListener,BaiduMap.OnMapClickListener,OnGetGeoCoderResultListener, BaiduMap.OnMapStatusChangeListener {

    private View mRootView;
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private Marker selfMarker;
    private Marker moveMarker;
    private LatLng currentLatLng;
    private InfoWindow mInfoWindow;
    private DasherMgr mDasherMgr;
    private DasherMgr.MapViewClass mapViewMgr;
    private DasherMgr.LocationClass locationMgr;
    private TextView txtvMapAdressSearch;
    private TextView txtvReLocation;
    private TextView txtvShop;
    private String currentCity;
    private ImageView imgvUserSwitch,imgvModeSwitch;
    private ImageView imgvMoveMarker;
    private RecyclerView recyclerView;
    private RestaurantListAdapter adapter;
    private RelativeLayout layoutMap;
    private LinearLayout layoutList;
    private TextView txtvFilter;
    private DataProvider mDataProvider;
    private ArrayList<ShopInfo> shopInfos;
    private boolean isCanMoveLocation = true;
    private ArrayList<ShopInfo> filterShopInfos;
    private EditText etFilter;
    private AlertToastDialog alertToastDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataProvider = DataProvider.getInstance(getActivity());
        SDKInitializer.initialize(getActivity().getApplicationContext());
        mDasherMgr = DasherMgr.getInstance(getActivity().getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        findViews(inflater, container);
        init();
        setClickListeners();
        setViews();
        return mRootView;
    }

    private void findViews(LayoutInflater inflater, ViewGroup container) {
        mRootView = inflater.inflate(R.layout.fragment_nearby, container, false);
        layoutMap = (RelativeLayout)mRootView.findViewById(R.id.layoutMap);
        layoutList = (LinearLayout)mRootView.findViewById(R.id.layoutList);
        mMapView = (MapView) mRootView.findViewById(R.id.mapView);
        txtvMapAdressSearch = (TextView)mRootView.findViewById(R.id.txtvMapAdressSearch);
        txtvShop = (TextView)mRootView.findViewById(R.id.txtvShop);
        txtvReLocation = (TextView)mRootView.findViewById(R.id.txtvReLocation);
        imgvUserSwitch = (ImageView)mRootView.findViewById(R.id.imgvUserSwitch);
        imgvModeSwitch = (ImageView)mRootView.findViewById(R.id.imgvModeSwitch);
        imgvMoveMarker = (ImageView)mRootView.findViewById(R.id.imgvMoveMarker);
        txtvFilter = (TextView)mRootView.findViewById(R.id.txtvFilter);
        etFilter = (EditText)mRootView.findViewById(R.id.etFilter);
        recyclerView = (RecyclerView)mRootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .color(getResources().getColor(R.color.lightGrayBg))
                .size(4)
                .build());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new RestaurantListAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void init(){
        //地图初始化 定位初始化
        mapViewMgr = mDasherMgr.initMap(mMapView);
        locationMgr = mDasherMgr.initLocation();
        mBaiduMap = mapViewMgr.getBaiduMap();
        initMapListener();
        locationMgr.requestionLocation();
    }
    private void initMapListener(){
        mapViewMgr.registerMarkerDragListener(this);
        mapViewMgr.registerMarkerClickListener(this);
        mapViewMgr.registerMapClickListener(this);
        mapViewMgr.registerMapStatusChangeListener(this);
        locationMgr.registerLocationListener(this);
        locationMgr.registerGeoCoderListener(this);
    }

    private void setViews(){
        switchUserIdentityImg();
        initMapStatus();
    }

    private void setClickListeners(){
        txtvMapAdressSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivityForResult(new Intent(getActivity(), AdressSearchActivity.class).putExtra(AdressSearchActivity.City, currentCity), TabActivity.QequestCode_SearchAddress);
            }
        });
        txtvReLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reGetLocation();
            }
        });
        imgvUserSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchUserIdentityFromAccount();
//                UserInfo userInfo = mDataProvider.getUserInfo();
//                if(userInfo.getStatus() == Status.User.Validate_Success){
//                    initMapStatus();
//                    mDasherMgr.switchUserIdentity();
//                    switchUserIdentity();
//                    reGetLocation();
//                    userAlertTip();
//                }else{
//                    if(getActivity()!=null){
//                        startActivity(new Intent(getActivity(), ModifyInfoActivity.class).putExtra(Extras.Extra_Modify_Base_Info_Page, Status.ModifyBaseInfo.Status_Send_Validate_Info));
//                    }
//                }
            }
        });
        imgvModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layoutMap.getVisibility() == View.GONE){
                    layoutMap.setVisibility(View.VISIBLE);
                    layoutList.setVisibility(View.GONE);
                    imgvModeSwitch.setImageResource(R.mipmap.nav_left);
                    imgvUserSwitch.setVisibility(View.VISIBLE);
                }else{
                    imgvModeSwitch.setImageResource(R.mipmap.nav_left_map);
                    layoutMap.setVisibility(View.GONE);
                    layoutList.setVisibility(View.VISIBLE);
                    adapter.refreshData(shopInfos);
                    imgvUserSwitch.setVisibility(View.INVISIBLE);
                }
            }
        });
        adapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                ShopInfo shopInfo = (ShopInfo) adapter.getItem(position);
                if (mDasherMgr.isCustomer()) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Extras.Extra_ShopInfo, shopInfo);
                    startActivity(new Intent(getActivity(), RestaurantActivity.class).putExtras(bundle));
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Extras.Extra_ShopInfo, shopInfo);
                    startActivity(new Intent(getActivity(), RestaurantOrderActivity.class).putExtras(bundle));
                }
            }
        });
        txtvFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ResFilterPopDialog(getActivity(), new ResFilterPopDialog.OnFilterListener() {
                    @Override
                    public void onFilter(int filterType, String shopType) {
                        ArrayList<ShopInfo> ls = BeanUtil.cloneTo(shopInfos);
                        filterShopInfos = new ArrayList<>();
                        if (shopType.equals(ResFilterPopDialog.AllType)) {
                            filterShopInfos = ls;
                        } else {
                            for (ShopInfo shopInfo : ls) {
                                if (shopInfo.getTypeTab().equals(shopType)) {
                                    filterShopInfos.add(shopInfo);
                                }
                            }
                        }
                        Collections.sort(filterShopInfos, new ComparatorValues());
                        adapter.refreshData(filterShopInfos);
                    }
                }).show(txtvFilter);
            }
        });

//        imgvMoveMarker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                isCanMoveLocation = !isCanMoveLocation;
//                if(isCanMoveLocation){
//                    imgvMoveMarker.setImageResource(R.mipmap.center_marker);
//                }else{
//                    imgvMoveMarker.setImageResource(R.mipmap.center_marker_lock);
//                }
//            }
//        });

        txtvShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCanMoveLocation){
                    if(currentLatLng == null){
                        Alert.showDefaultToast(getActivity(),"定位中...");
                        return;
                    }
                    imgvMoveMarker.setVisibility(View.GONE);
                    txtvShop.setText("重新定位");
                    txtvMapAdressSearch.setEnabled(false);
                    txtvReLocation.setEnabled(false);
                    updateUserAdressInfo();
                    if(moveMarker!=null){
                        moveMarker.remove();
                    }
                    moveMarker = mapViewMgr.addMoveMarker(currentLatLng.latitude,currentLatLng.longitude);
                    isCanMoveLocation = ! isCanMoveLocation;
                }else{
                    mapViewMgr.animateToCenter(currentLatLng);
                    moveMarker.remove();
                    mapViewMgr.clearShopMarkers();
                    imgvMoveMarker.setVisibility(View.VISIBLE);
                    txtvMapAdressSearch.setEnabled(true);
                    txtvReLocation.setEnabled(true);
                    txtvShop.setText("显示" + (mDasherMgr.isCustomer() ? "商家" : "订单"));
                    isCanMoveLocation = ! isCanMoveLocation;
                }
            }
        });

        etFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String value = etFilter.getText().toString();
                if (TextUtils.isEmpty(value)) {
                    if (filterShopInfos != null) {
                        adapter.refreshData(filterShopInfos);
                    } else {
                        adapter.refreshData(shopInfos);
                    }
                } else {
                    ArrayList<ShopInfo> ls = filterShopInfos == null ? shopInfos : filterShopInfos;
                    ArrayList<ShopInfo> selectList = new ArrayList<ShopInfo>();
                    for (ShopInfo shopInfo : ls) {
                        if (shopInfo.getName().contains(value)) {
                            selectList.add(shopInfo);
                        }
                    }
                    adapter.refreshData(selectList);
                }
            }
        });
    }

    public void reGetLocation(){
        if (locationMgr != null) {
            txtvMapAdressSearch.setText("定位中...");
            locationMgr.requestionLocation();
        }
    }

    public void initMapStatus(){
        isCanMoveLocation = true;
        imgvMoveMarker.setVisibility(View.VISIBLE);
        txtvMapAdressSearch.setEnabled(true);
        txtvReLocation.setEnabled(true);
        txtvShop.setText("显示"+(mDasherMgr.isCustomer()?"商家":"订单"));
        mapViewMgr.clearShopMarkers();
        if(moveMarker!=null){
            moveMarker.remove();
        }
    }

    //切换导航栏身份图片
    public void switchUserIdentityImg(){
        if(mDasherMgr.isCustomer()){
            imgvUserSwitch.setImageResource(R.mipmap.nav_right_send);
        }else{
            imgvUserSwitch.setImageResource(R.mipmap.nav_right_customer);
        }
        ((TabActivity)getActivity()).refreshTabUI();
    }

    /**
     * 搜索地址
     * @param city
     * @param address
     */
    public void updateSearchAddress(String city,String address){
        txtvMapAdressSearch.setText("查询中...");
        locationMgr.getLocationFromAddress(city, address);
    }

    /**
     * 更行地址信息
     */
    public void updateUserAdressInfo(){
        String address = txtvMapAdressSearch.getText().toString();
        String lat = String.valueOf(currentLatLng.latitude);
        String log = String.valueOf(currentLatLng.longitude);
        UserAdressInfo userAdressInfo = new UserAdressInfo();
        userAdressInfo.setAddress(address);
        userAdressInfo.setLatitude(lat);
        userAdressInfo.setLongitude(log);
        mDataProvider.updateUserAdressInfo(userAdressInfo);
        refreshNearyByShop(lat, log);
    }

    /**
     * 更新当前地址周围的商家
     * @param lat
     * @param log
     */
    public void refreshNearyByShop(String lat,String log){
        Log.d("CurrentLocation", "lat--->" + lat + ",lng--->" + log);
        if(mDasherMgr.isCustomer()){
            mDataProvider.retrieveNearShopList(lat,log);
        }else{
            mDataProvider.retrieveNearOrderShopList(lat,log);
        }
        Alert.showDefaultToast(getActivity(), "数据请求已发送！");
    }

    /**
     * 从account界面切换身份
     */
    public void switchUserIdentityFromAccount(){
        HandlerHelper.post(new HandlerHelper.onRun() {
            @Override
            public void run() {
                UserInfo userInfo = mDataProvider.getUserInfo();
                if (userInfo.getStatus() == Status.User.Validate_Success) {
                    mDasherMgr.switchUserIdentity();
                    initMapStatus();
                    switchUserIdentityImg();
                    reGetLocation();
                    userAlertTip();
                } else {
                    if (getActivity() != null) {
                        startActivity(new Intent(getActivity(), ModifyInfoActivity.class).putExtra(Extras.Extra_Modify_Base_Info_Page, Status.ModifyBaseInfo.Status_Send_Validate_Info));
                    }
                }
            }
        });
    }

    /**
     * 身份切换提示
     */
    public void userAlertTip(){
        alertToastDialog = new AlertToastDialog(getActivity(),"身份切换",mDasherMgr.isCustomer()?"现在是用户身份，可选择商家下单！":"现在是接单员身份，可选择订单接单！");
        alertToastDialog.show();
        HandlerHelper.postDelayed(new HandlerHelper.onRun() {
            @Override
            public void run() {
                if(alertToastDialog!=null){
                    alertToastDialog.dismiss();
                }
            }
        },1000);
    }


    /**
     * map的点击事件
     * @param latLng
     */
    @Override
    public void onMapClick(LatLng latLng) {
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    /**
     * marker的点击事件
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        if(mInfoWindow!=null){
            mBaiduMap.hideInfoWindow();
        }
        if (marker != selfMarker && marker != moveMarker) {
            final ShopInfo shopInfo = (ShopInfo)marker.getExtraInfo().getSerializable(Extras.Extra_ShopInfo);

            View view = new MapInfoWindowView(getActivity(),shopInfo);
            LatLng l = new LatLng(Double.parseDouble(shopInfo.getLatitude()),Double.parseDouble(shopInfo.getLongitude()));
            mInfoWindow = new InfoWindow(view, l, -60);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mDasherMgr.isCustomer()){
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Extras.Extra_ShopInfo,shopInfo);
                        startActivity(new Intent(getActivity(), RestaurantActivity.class).putExtras(bundle));
                    }else{
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Extras.Extra_ShopInfo,shopInfo);
                        startActivityForResult(new Intent(getActivity(), RestaurantOrderActivity.class).putExtras(bundle), Extras.Request_Code_Compain_Or_Complete);
                    }
                }
            });
            mBaiduMap.showInfoWindow(mInfoWindow);
        }
        return true;
    }

    /**
     * marker的拖拽事件
     * @param marker
     */
    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
    }

    //定位数据回调
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if(bdLocation==null){
            return;
        }
        currentCity = bdLocation.getCity();
        selfMarker = mapViewMgr.addSelfMarker(bdLocation.getLatitude(), bdLocation.getLongitude());
        currentLatLng = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
        txtvMapAdressSearch.setText(bdLocation.getAddrStr());//显示地址

//        if(!isCanMoveLocation){
//            moveMarker = mapViewMgr.addMoveMarker(bdLocation.getLatitude(), bdLocation.getLongitude());
//            String latitude = String.valueOf(bdLocation.getLatitude());
//            String longitude = String.valueOf(bdLocation.getLongitude());
//            updateUserAdressInfo(bdLocation.getAddrStr(), latitude, longitude);
//        }
    }

    //地址 -> 经纬度 和 经纬度->地址
    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            //没有检索到结果
            txtvMapAdressSearch.setText("抱歉，查询错误");
            return;
        }
        currentLatLng = geoCodeResult.getLocation();
        txtvMapAdressSearch.setText(geoCodeResult.getAddress());
        mapViewMgr.animateToCenter(currentLatLng);
//        updateUserAdressInfo(geoCodeResult.getAddress(), String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (reverseGeoCodeResult == null
                || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            // 没有检测到结果
            txtvMapAdressSearch.setText("抱歉，查询错误");
            return;
        }

//        updateUserAdressInfo(reverseGeoCodeResult.getAddress(), String.valueOf(reverseGeoCodeResult.getLocation().latitude), String.valueOf(reverseGeoCodeResult.getLocation().longitude));
        txtvMapAdressSearch.setText(reverseGeoCodeResult.getAddress());
        currentLatLng = reverseGeoCodeResult.getLocation();
    }

    //地图状态的变化
    @Override
    public void onMapStatusChange(MapStatus mapStatus) {
    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        if(isCanMoveLocation){
            LatLng latLng = mapStatus.target;
            locationMgr.getAddressFromLocation(latLng);
        }
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {

    }

    //获取商家列表，数据返回
    @Subscribe
    public void OnShopListEvent(final ShopListEvent shopListEvent){
        if(shopListEvent!=null){
            shopInfos = shopListEvent.getShopList();
            HandlerHelper.post(new HandlerHelper.onRun() {
                @Override
                public void run() {
                    if(shopInfos!=null&&shopInfos.size()>0){
                        Alert.showDefaultToast(getActivity(),"数据请求成功!");
                    }
                    if(shopInfos == null||shopInfos.size() == 0){
                        Alert.showDefaultToast(getActivity(),shopListEvent.getErrorMsg());
                    }
                    mapViewMgr.addShopMarker(shopInfos,mDasherMgr.isCustomer());
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMapView.onDestroy();
        locationMgr.stopLocation();
    }

    public final class ComparatorValues implements Comparator<ShopInfo> {

        @Override
        public int compare(ShopInfo object1, ShopInfo object2) {
            int m1=object1.getGoodEvaluate();
            int m2=object2.getGoodEvaluate();
            int result=0;
            if(m1>m2)
            {
                result=-1;
            }
            if(m1<m2)
            {
                result=1;
            }
            return result;
        }
    }
}
