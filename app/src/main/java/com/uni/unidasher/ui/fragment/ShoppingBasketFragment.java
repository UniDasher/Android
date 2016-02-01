package com.uni.unidasher.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.uni.unidasher.BuildConfig;
import com.uni.unidasher.R;
import com.uni.unidasher.data.DataPrefs;
import com.uni.unidasher.data.DataProvider;
import com.uni.unidasher.data.api.ResOrderInfo;
import com.uni.unidasher.data.datamodel.databaseAndApi.DishInfo;
import com.uni.unidasher.data.datamodel.databaseAndApi.ShopInfo;
import com.uni.unidasher.data.datamodel.databaseAndApi.ShoppingBasket;
import com.uni.unidasher.data.datamodel.databaseAndApi.UserAdressInfo;
import com.uni.unidasher.data.entity.OrderInfo;
import com.uni.unidasher.data.rest.RESTManager;
import com.uni.unidasher.data.status.Status;
import com.uni.unidasher.data.utils.Constants;
import com.uni.unidasher.ui.activity.TabActivity;
import com.uni.unidasher.ui.activity.restaurant.RestaurantActivity;
import com.uni.unidasher.ui.adapter.OrderMenuListAdapter;
import com.uni.unidasher.ui.dialog.AlertPrompt;
import com.uni.unidasher.ui.dialog.OrderConfirmDialog;
import com.uni.unidasher.ui.utils.Alert;
import com.uni.unidasher.ui.utils.CommonUtils;
import com.uni.unidasher.ui.utils.Extras;
import com.uni.unidasher.ui.utils.HandlerHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShoppingBasketFragment extends EventBusFragment {

    private View mRootView;
    private DataProvider mDataProvider;
    private ShopInfo shopInfo = null;
    private TextView txtvShopName,txtvShopAddress;
    private TextView txtvDishPrice,txtvSendPrice,txtvTotalPrice,txtvPay,txtvTip,txtvInto;
    private ListView listvMenuList;
    private OrderMenuListAdapter adapter;
    private LinearLayout layoutEmpty,layoutData;

    public static ShoppingBasketFragment newInstance(String param1, String param2) {
        ShoppingBasketFragment fragment = new ShoppingBasketFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ShoppingBasketFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataProvider = DataProvider.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        findViews(inflater,container);
        setClickListeners();
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshShopInfo();
    }

    public void findViews(LayoutInflater inflater, ViewGroup container){
        mRootView = inflater.inflate(R.layout.fragment_shopping_basket,container,false);
        layoutEmpty = (LinearLayout)mRootView.findViewById(R.id.layoutEmpty);
        layoutData = (LinearLayout)mRootView.findViewById(R.id.layoutData);
        txtvShopName = (TextView)mRootView.findViewById(R.id.txtvShopName);
        txtvShopAddress = (TextView)mRootView.findViewById(R.id.txtvAddress);
        txtvDishPrice = (TextView)mRootView.findViewById(R.id.txtvDishPrice);
        txtvSendPrice = (TextView)mRootView.findViewById(R.id.txtvSendPrice);
        txtvTotalPrice = (TextView)mRootView.findViewById(R.id.txtvTotalPrice);
        txtvPay = (TextView)mRootView.findViewById(R.id.txtvPay);
        txtvTip = (TextView)mRootView.findViewById(R.id.txtvTip);
        txtvInto = (TextView)mRootView.findViewById(R.id.txtvInto);

        listvMenuList = (ListView)mRootView.findViewById(R.id.listvMenuList);
        adapter = new OrderMenuListAdapter(getActivity(),false);
        listvMenuList.setAdapter(adapter);
    }

    public void setClickListeners(){
        txtvInto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Extras.Extra_ShopInfo,shopInfo);
                startActivity(new Intent(getActivity(), RestaurantActivity.class).putExtras(bundle));
            }
        });

        txtvPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String periodTime = CommonUtils.isCurrentInTimePeriod(shopInfo.getServiceTimes());
                if(!TextUtils.isEmpty(periodTime)){
                    List<String> ls = CommonUtils.getTimePeriod(periodTime);
                    new OrderConfirmDialog(getActivity(),mDataProvider.getUserInfo(),mDataProvider.getUserAddressInfo(), ls,new OrderConfirmDialog.OnConfirmListener() {
                        @Override
                        public void onConfirm(String sendTime,boolean isOverTime) {
                            String overTime = isOverTime?sendTime:periodTime.substring(6,11);
                            ShoppingBasket shoppingBasket = mDataProvider.getShoppingBasket();
                            UserAdressInfo userAdressInfo = mDataProvider.getUserAddressInfo();
                            if(shoppingBasket == null || userAdressInfo == null){
                                return;
                            }
                            ResOrderInfo resOrderInfo = new ResOrderInfo();
                            resOrderInfo.setSid(shopInfo.getSid());
                            resOrderInfo.setUid(mDataProvider.getUserId());
                            resOrderInfo.setDishsMoney(Float.parseFloat(txtvDishPrice.getText().toString()));
                            resOrderInfo.setCarriageMoney(Float.parseFloat(txtvSendPrice.getText().toString()));//运费
                            resOrderInfo.setTaxesMoney(0);//税费
                            resOrderInfo.setServiceMoney(0);//服务费
                            resOrderInfo.setTipMoney(0);
                            resOrderInfo.setPayType(0);
                            resOrderInfo.setMealStartDate(sendTime);
                            resOrderInfo.setMealEndDate(overTime);
                            resOrderInfo.setAddress(userAdressInfo.getAddress());
                            resOrderInfo.setLatitude(userAdressInfo.getLatitude());
                            resOrderInfo.setLongitude(userAdressInfo.getLongitude());
                            resOrderInfo.setDishInfos(shoppingBasket.getDishInfos());
                            resOrderInfo.setMenuCount(getDishTotalCount(shoppingBasket.getDishInfos()));
                            mDataProvider.submitOrder(resOrderInfo, new RESTManager.OnObjectDownloadedListener() {
                                @Override
                                public void onObjectDownloaded(final boolean success, String resultStr, final String tipStr) {
                                    if (BuildConfig.DEBUG) {
                                        Log.i("submitOrder:", success + "" + "---" + resultStr);
                                    }
                                    HandlerHelper.post(new HandlerHelper.onRun() {
                                        @Override
                                        public void run() {
                                            if(success){
                                                Alert.showTip(getActivity(), "付款", "下单成功，请等候送单员接单！", false, new AlertPrompt.OnAlertClickListener() {
                                                    @Override
                                                    public void onClick() {
                                                        mDataProvider.clearShoppingBasket(null);
                                                        refreshShopInfo();
                                                    }
                                                });
                                            } else {
                                                String errorMsg = "下单失败!";
                                                if(!TextUtils.isEmpty(tipStr)){
                                                    errorMsg = tipStr;
                                                }
                                                Alert.showDefaultToast(getActivity(), errorMsg);
                                            }
                                        }
                                    });

                                }
                            });
                        }
                    }).show();
                }else{
                    Alert.showTip(getActivity(),"提示","抱歉，当前时间商家不营业!",true,null);
                }
            }
        });
    }

    public void setViews(ShopInfo shopInfo,List<DishInfo> dishInfos){
        if(shopInfo == null||dishInfos==null||dishInfos.size()==0){
            layoutEmpty.setVisibility(View.VISIBLE);
            layoutData.setVisibility(View.GONE);
        }else{
            layoutEmpty.setVisibility(View.GONE);
            layoutData.setVisibility(View.VISIBLE);
            txtvShopName.setText(shopInfo.getName());
            txtvShopAddress.setText(shopInfo.getAddress());

            if(shopInfo.isOpenService()&& !TextUtils.isEmpty(CommonUtils.isCurrentInTimePeriod(shopInfo.getServiceTimes()))){
                txtvPay.setVisibility(View.VISIBLE);
                txtvTip.setVisibility(View.GONE);
            }else{
                txtvPay.setVisibility(View.GONE);
                txtvTip.setVisibility(View.VISIBLE);
            }
            adapter.refreshData(dishInfos);
            updateTotalPrice(dishInfos);
        }
    }

    /**
     * 更新价格
     * @param dishInfos
     */
    private void updateTotalPrice(List<DishInfo> dishInfos){
        int totalPrice = 0;
        if(dishInfos!=null){
            for(DishInfo dishInfo:dishInfos){
                totalPrice+=(dishInfo.getCount()*dishInfo.getPrice());
            }
        }
        txtvDishPrice.setText(totalPrice+"");
        txtvSendPrice.setText(Status.Order.SendPrice+"");
        txtvTotalPrice.setText((totalPrice+Status.Order.SendPrice)+"");
    }

    private int getDishTotalCount(List<DishInfo> dishInfos){
        int totalCount = 0;
        for(DishInfo dishInfo:dishInfos){
            totalCount +=dishInfo.getCount();
        }
        return totalCount;
    }

    /**
     * 刷新餐厅数据
     */
    public void refreshShopInfo(){
//        shopInfo = null;
        ShoppingBasket shoppingBasket = mDataProvider.getShoppingBasket();
        if(shoppingBasket==null||shoppingBasket.getDishInfos()==null||shoppingBasket.getDishInfos().size()==0||shoppingBasket.getShopInfo()==null){
            HandlerHelper.post(new HandlerHelper.onRun() {
                @Override
                public void run() {
                    setViews(null, null);
                }
            });
            return;
        }
        final List<DishInfo> dishInfos = shoppingBasket.getDishInfos();
        ShopInfo sbShopInfo = shoppingBasket.getShopInfo();
        mDataProvider.retrieveShopInfo(sbShopInfo.getSid(), new RESTManager.OnObjectDownloadedListener() {
            @Override
            public void onObjectDownloaded(boolean success, String resultStr, String tipStr) {
                if(BuildConfig.DEBUG){
                    Log.i("retrieveShopInfo",success+"->"+resultStr);
                }
                if(success){
                    try{
                        JSONObject jsonObject = new JSONObject(resultStr);
                        JSONObject dataObj = jsonObject.optJSONObject("data");
                        if(dataObj!=null){
                            shopInfo = Constants.GSON_RECEIVED.fromJson(dataObj.toString(), ShopInfo.class);
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                HandlerHelper.post(new HandlerHelper.onRun() {
                    @Override
                    public void run() {
                        setViews(shopInfo,dishInfos);
                    }
                });
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refreshShopInfo();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
