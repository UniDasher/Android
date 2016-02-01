package com.uni.unidasher.ui.activity.restaurant;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.uni.unidasher.BuildConfig;
import com.uni.unidasher.R;
import com.uni.unidasher.data.DataProvider;
import com.uni.unidasher.data.api.ResOrderInfo;
import com.uni.unidasher.data.database.DatabaseHelper;
import com.uni.unidasher.data.datamodel.databaseAndApi.DishInfo;
import com.uni.unidasher.data.datamodel.databaseAndApi.UserAdressInfo;
import com.uni.unidasher.data.datamodel.databaseAndApi.ShopInfo;
import com.uni.unidasher.data.datamodel.databaseAndApi.ShoppingBasket;
import com.uni.unidasher.data.rest.RESTManager;
import com.uni.unidasher.data.status.Status;
import com.uni.unidasher.data.utils.BeanUtil;
import com.uni.unidasher.listener.OnTakeDishListener;
import com.uni.unidasher.ui.activity.EventBusActivity;
import com.uni.unidasher.ui.activity.TabActivity;
import com.uni.unidasher.ui.adapter.OrderMenuListAdapter;
import com.uni.unidasher.ui.dialog.AlertPrompt;
import com.uni.unidasher.ui.dialog.OrderConfirmDialog;
import com.uni.unidasher.ui.utils.Alert;
import com.uni.unidasher.ui.utils.CommonUtils;
import com.uni.unidasher.ui.utils.Extras;
import com.uni.unidasher.ui.utils.HandlerHelper;

import java.util.ArrayList;
import java.util.List;

public class OrderPayActivity extends EventBusActivity {
    private TextView txtvBack,txtvNavTitle;
    private ListView listvMenuList;
    private OrderMenuListAdapter adapter;
    private DataProvider mDataProvider;
    private TextView txtvShopName,txtvShopAddress;
    private TextView txtvDishPrice,txtvSendPrice,txtvTotalPrice,txtvPay;
    private ShopInfo shopInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_pay);
        mDataProvider = DataProvider.getInstance(this);
        findViews();
        setViews();
        setClickListeners();
    }

    private void findViews(){
        txtvBack = (TextView)findViewById(R.id.txtvBack);
        txtvNavTitle = (TextView)findViewById(R.id.txtvNavTitle);
        txtvNavTitle.setText("查看订单");
        txtvShopName = (TextView)findViewById(R.id.txtvShopName);
        txtvShopAddress = (TextView)findViewById(R.id.txtvAddress);
        txtvDishPrice = (TextView)findViewById(R.id.txtvDishPrice);
        txtvSendPrice = (TextView)findViewById(R.id.txtvSendPrice);
        txtvTotalPrice = (TextView)findViewById(R.id.txtvTotalPrice);
        txtvPay = (TextView)findViewById(R.id.txtvPay);

        listvMenuList = (ListView)findViewById(R.id.listvMenuList);
        adapter = new OrderMenuListAdapter(this,true);
        listvMenuList.setAdapter(adapter);

    }

    private void setViews(){
        ShoppingBasket shoppingBasket = mDataProvider.getShoppingBasket();
        if(shoppingBasket == null)
            return;
        shopInfo = shoppingBasket.getShopInfo();

        if(shopInfo == null)
            return;
        txtvShopName.setText(shopInfo.getName());
        txtvShopAddress.setText(shopInfo.getAddress());
        List<DishInfo> dishInfos = shoppingBasket.getDishInfos();
        if(dishInfos == null)
            return;
        updateTotalPrice(dishInfos);
        adapter.refreshData(dishInfos);
    }

    private void updateTotalPrice(List<DishInfo> dishInfos){
        int totalPrice = 0;
        if(dishInfos!=null){
            for(DishInfo dishInfo:dishInfos){
                totalPrice+=(dishInfo.getCount()*dishInfo.getPrice());
            }
        }
        txtvDishPrice.setText(totalPrice + "");
        txtvSendPrice.setText(Status.Order.SendPrice + "");
        txtvTotalPrice.setText((totalPrice + Status.Order.SendPrice) + "");
    }

    private void setClickListeners(){
        txtvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        adapter.setOnTakeDishListener(new OnTakeDishListener() {
            @Override
            public void onAdd(int position) {
                doDish(position, true);
            }

            @Override
            public void onRemove(int position) {
                DishInfo dishInfo = (DishInfo)adapter.getItem(position);
                if(dishInfo.getCount()>0){
                    doDish(position, false);
                }
            }
        });

        txtvPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String periodTime = CommonUtils.isCurrentInTimePeriod(shopInfo.getServiceTimes());
                if(shopInfo.isOpenService()&&!TextUtils.isEmpty(periodTime)){
                    List<String> ls = CommonUtils.getTimePeriod(periodTime);
                    new OrderConfirmDialog(OrderPayActivity.this,mDataProvider.getUserInfo(),mDataProvider.getUserAddressInfo(), ls,new OrderConfirmDialog.OnConfirmListener() {
                        @Override
                        public void onConfirm(String sendTime,boolean isOverTime) {
//                            if(!TextUtils.isEmpty(CommonUtils.isCurrentInTimePeriod(periodTime))&&CommonUtils.isPointInPeriod(sendTime,periodTime)){
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
                                                    Alert.showTip(OrderPayActivity.this, "付款", "下单成功，请等候送单员接单！", false, new AlertPrompt.OnAlertClickListener() {
                                                        @Override
                                                        public void onClick() {
                                                            mDataProvider.clearShoppingBasket(null);
                                                            startActivity(new Intent(OrderPayActivity.this, TabActivity.class));
                                                            finish();
                                                        }
                                                    });
                                                }else{
                                                    String errorMsg = "下单失败!";
                                                    if(!TextUtils.isEmpty(tipStr)){
                                                        errorMsg = tipStr;
                                                    }
                                                    Alert.showDefaultToast(OrderPayActivity.this, errorMsg);
//                                                    Alert.showTip(OrderPayActivity.this,"付款","下单失败",true,null);
                                                }
                                            }
                                        });
                                    }
                                });
                        }
                    }).show();
                }else{
                    Alert.showDefaultToast(OrderPayActivity.this,"抱歉，当前时间商家不营业!");
//                    Alert.showTip(OrderPayActivity.this,"提示","抱歉，当前时间商家不营业!",true,null);
                }
            }
        });
    }

    private int getDishTotalCount(List<DishInfo> dishInfos){
        int totalCount = 0;
        for(DishInfo dishInfo:dishInfos){
            totalCount +=dishInfo.getCount();
        }
        return totalCount;
    }
    /**
     * 增加或减少菜品-------点菜
     * @param position
     * @param isAdd
     */
    public synchronized void doDish(final int position, final boolean isAdd) {
        DishInfo dishInfo = BeanUtil.cloneTo((DishInfo) adapter.getItem(position));
        ShoppingBasket shoppingBasket = mDataProvider.getShoppingBasket();
        if (shoppingBasket != null) {
            List<DishInfo> shoppingDishInfos = shoppingBasket.getDishInfos();
            if (shoppingDishInfos != null) {
                boolean isExist = false;
                for (DishInfo d : shoppingDishInfos) {//判断购物篮是否存在此类菜品
                    if (dishInfo.getDishId().equals(d.getDishId())) {
                        int count = d.getCount();
                        if (isAdd) {
                            count++;
                        } else {
                            if (count > 0) {
                                count--;
                            }
                        }
                        d.setCount(count);
                        isExist = true;
                        break;
                    }
                }
                if (!isExist && isAdd) {
                    int count = dishInfo.getCount();
                    count++;
                    dishInfo.setCount(count);
                    shoppingDishInfos.add(dishInfo);
                }
            } else {
                shoppingDishInfos = new ArrayList<>();
                int count = dishInfo.getCount();
                count++;
                dishInfo.setCount(count);
                shoppingDishInfos.add(dishInfo);
            }
            shoppingBasket.setDishInfos(shoppingDishInfos);
        } else {
            shoppingBasket = new ShoppingBasket();
            List<DishInfo> dishInfos = new ArrayList<>();
            int count = dishInfo.getCount();
            count++;
            dishInfo.setCount(count);
            dishInfos.add(dishInfo);
            shoppingBasket.setDishInfos(dishInfos);
        }
        mDataProvider.updateShoppingBasket(shoppingBasket, new DatabaseHelper.OnDatabaseInsertedListener() {
            @Override
            public void onDatabaseInserted() {
                HandlerHelper.post(new HandlerHelper.onRun() {
                    @Override
                    public void run() {
                        if(isAdd){
                            adapter.add(position);
                        }else{
                            adapter.remove(position);
                        }
                        refreshTotalPrice();
                    }
                });
            }
        });
    }

    public void refreshTotalPrice(){
        int totalPrice = 0;
        ShoppingBasket shoppingBasket = mDataProvider.getShoppingBasket();
        if (shoppingBasket!=null&&shoppingBasket.getDishInfos()!=null&&shoppingBasket.getDishInfos().size()>0){
            List<DishInfo> shoppingDishInfos = shoppingBasket.getDishInfos();
            for(DishInfo dishInfo:shoppingDishInfos){
                totalPrice+=(dishInfo.getCount()*dishInfo.getPrice());
            }
        }
        txtvDishPrice.setText(totalPrice+"");
        txtvTotalPrice.setText((totalPrice+Status.Order.SendPrice)+"");
        updatePreDishList();
    }

    public void updatePreDishList(){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Extras.Extra_Update_Dishes_Of_Pay_Flag,true);
        intent.putExtras(bundle);
        setResult(Extras.Request_Code_Update_Dishes_Of_Pay,intent);
    }
}
