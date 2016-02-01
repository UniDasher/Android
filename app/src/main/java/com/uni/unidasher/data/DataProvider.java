package com.uni.unidasher.data;

import android.content.Context;
import android.util.Log;

import com.google.common.eventbus.EventBus;
import com.igexin.sdk.PushManager;
import com.uni.unidasher.AppConstants;
import com.uni.unidasher.BuildConfig;
import com.uni.unidasher.chat.ui.DasherHXSDKHelper;
import com.uni.unidasher.data.api.ResOrderInfo;
import com.uni.unidasher.data.api.request.DeviceInfo;
import com.uni.unidasher.data.api.request.ForgotPassword;
import com.uni.unidasher.data.api.request.PasswordInfo;
import com.uni.unidasher.data.api.request.PhoneInfo;
import com.uni.unidasher.data.api.request.SenderValidationInfo;
import com.uni.unidasher.data.database.DatabaseHelper;
import com.uni.unidasher.data.datamodel.databaseAndApi.DishInfo;
import com.uni.unidasher.data.datamodel.databaseAndApi.UserAdressInfo;
import com.uni.unidasher.data.datamodel.databaseAndApi.ShoppingBasket;
import com.uni.unidasher.data.entity.LoginInfo;
import com.uni.unidasher.data.entity.OrderInfo;
import com.uni.unidasher.data.entity.RegisterInfo;
import com.uni.unidasher.data.entity.SignInReceiveObject;
import com.uni.unidasher.data.entity.UserInfo;
import com.uni.unidasher.data.entity.post.PCompainInfo;
import com.uni.unidasher.data.entity.post.PCompleteOrder;
import com.uni.unidasher.data.entity.post.POrderStatus;
import com.uni.unidasher.data.entity.shop.ShopDishList;
import com.uni.unidasher.data.entity.shop.ShopList;
import com.uni.unidasher.data.event.ReOrderDishListEvent;
import com.uni.unidasher.data.event.ReOrderListEvent;
import com.uni.unidasher.data.event.ReOrderListForChatEvent;
import com.uni.unidasher.data.event.ReShopOrderListEvent;
import com.uni.unidasher.data.event.RefreshUserInfoEvent;
import com.uni.unidasher.data.event.ShopDishEvent;
import com.uni.unidasher.data.event.ShopListEvent;
import com.uni.unidasher.data.event.SignInEvent;
import com.uni.unidasher.data.event.SignUpEvent;
import com.uni.unidasher.data.event.SignUpLoginInEvent;
import com.uni.unidasher.data.rest.RESTManager;
import com.uni.unidasher.data.status.SignInState;
import com.uni.unidasher.data.utils.Constants;
import com.uni.unidasher.ui.utils.Alert;
import com.uni.unidasher.ui.utils.DasherAppPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/5/15.
 */
public class DataProvider {
    private static DataProvider sDataProvider;

    private Context mContext;
    private RESTManager mRestManager;
    public static DatabaseHelper mDatabaseHelper;
    private static final EventBus mEventBus = new EventBus();

    public static DataProvider getInstance(Context context) {
        if (sDataProvider == null) {
            sDataProvider = new DataProvider(context.getApplicationContext());
        }
        return sDataProvider;
    }
    private DataProvider(final Context context) {
        mContext = context;
        if (DataPrefs.getSharedHost() == null) {
            DataPrefs.setSharedHost(AppConstants.HOST);
        }
        mDatabaseHelper = new DatabaseHelper(context);
        mEventBus.register(this);
        mRestManager = new RESTManager(mContext, mEventBus, DataPrefs.getSharedHost(), DataPrefs.getToken());
    }

    public static EventBus getEventBus() {
        return mEventBus;
    }

    public void signUp(RegisterInfo registerInfo){
        mRestManager.signUp(registerInfo, new RESTManager.OnObjectDownloadedListener() {
            @Override
            public void onObjectDownloaded(boolean success, String resultStr, String tipStr) {
                if (BuildConfig.DEBUG) {
                    Log.i("signUp:", success + "" + "---" + resultStr);
                }
                SignUpEvent signUpEvent;
                if (success) {
                    signUpEvent = Constants.GSON_RECEIVED.fromJson(resultStr, SignUpEvent.class);
                } else {
                    signUpEvent = new SignUpEvent();
                }
                signUpEvent.setSuccess(success);
                signUpEvent.setErrorInfo(tipStr);
                mEventBus.post(signUpEvent);
            }
        });
    }

    public void signIn(LoginInfo loginInfo,final boolean isLoginPage){
        mRestManager.signIn(loginInfo, new RESTManager.OnObjectDownloadedListener() {
            @Override
            public void onObjectDownloaded(boolean success, String resultStr, String tipStr) {
                if (BuildConfig.DEBUG) {
                    Log.i("signIn:", success + "" + "---" + resultStr);
                }
                if (success) {
                    SignInReceiveObject signInReceiveObject = Constants.GSON_RECEIVED.fromJson(resultStr, SignInReceiveObject.class);
                    setAuthToken(signInReceiveObject.getAuthToken());
                    setUserId(signInReceiveObject.getUserId());
//                    setHxId(signInReceiveObject.getHxId());
                    setUserName(signInReceiveObject.getUserName());
                    setUserLogo(signInReceiveObject.getLogo());
                    DasherAppPrefs.setUserIdentity(0);
                    mEventBus.post(isLoginPage ? new SignInEvent(SignInState.LOGGED, "") : new SignUpLoginInEvent(SignInState.LOGGED, ""));
                } else {
                    mEventBus.post(isLoginPage ? new SignInEvent(SignInState.NOT_LOGGED_IN, tipStr) : new SignUpLoginInEvent(SignInState.NOT_LOGGED_IN, tipStr));
                }

            }
        });
    }

    /**
     * 忘记密码
     * @param forgotPassword
     * @param listener
     */
    public void forgotPassowrd(ForgotPassword forgotPassword,final RESTManager.OnObjectDownloadedListener listener){
        mRestManager.forgotPassowrd(forgotPassword,listener);
    }

    public void loginOut(){
        mRestManager.loginOut(new RESTManager.OnObjectDownloadedListener() {
            @Override
            public void onObjectDownloaded(boolean success, String resultStr, String tipStr) {
                if(BuildConfig.DEBUG){
                    Log.i("loginOut",success+"----->"+resultStr);
                }
            }
        });
    }

    /**
     * 刷新用户基本信息
     */
    public void refreshUserInfo(){
        mRestManager.refreshUserInfo(getUserId(), new RESTManager.OnObjectDownloadedListener() {
            @Override
            public void onObjectDownloaded(boolean success, String resultStr, String tipStr) {
                if (BuildConfig.DEBUG) {
                    Log.i("refreshUserInfo:", success + "" + "---" + resultStr);
                }
                if (success) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultStr);
                        JSONObject dataObj = jsonObject.optJSONObject("data");
                        if (dataObj != null) {
                            UserInfo userInfo = Constants.GSON_RECEIVED.fromJson(dataObj.toString(), UserInfo.class);
                            DataPrefs.setUserInfo(userInfo);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mEventBus.post(new RefreshUserInfoEvent());
                }
            }
        });
    }

    /**
     * 送餐人身份审核
     * @param senderValidationInfo
     * @param listener
     */
    public void reportSenderValidation(SenderValidationInfo senderValidationInfo,final RESTManager.OnObjectDownloadedListener listener){
        mRestManager.reportSenderValidation(senderValidationInfo, listener);
    }

    /**
     * 提交设备基本信息
     * @param deviceInfo
     */
    public void reportDeviceInfo(DeviceInfo deviceInfo){
        mRestManager.reportDeviceInfo(deviceInfo, new RESTManager.OnObjectDownloadedListener() {
            @Override
            public void onObjectDownloaded(boolean success, String resultStr, String tipStr) {
                if (BuildConfig.DEBUG) {
                    Log.i("reportDeviceInfo:", success + "" + "---" + resultStr);
                }
            }
        });
    }

    /**
     * 获取收益信息
     */
    public void retrieveEarn(final RESTManager.OnObjectDownloadedListener listener){
        mRestManager.retrieveEarn(listener);
    }

    /**
     * 获取支出列表
     * @param startDate
     * @param endDate
     * @param listener
     */
    public void retrieveOutList(String startDate,String endDate,final RESTManager.OnObjectDownloadedListener listener){
        mRestManager.retrieveOutList(startDate, endDate, listener);
    }
    /**
     * 获取收益列表
     * @param startDate
     * @param endDate
     * @param listener
     */
    public void retrieveEarnList(String startDate,String endDate,final RESTManager.OnObjectDownloadedListener listener){
        mRestManager.retrieveEarnList(startDate, endDate, listener);
    }

    /**
     * 修改密码
     * @param oldPwd
     * @param newPwd
     * @param listener
     */
    public void updatePassword(String oldPwd,String newPwd,final RESTManager.OnObjectDownloadedListener listener){
        mRestManager.updatePassword(new PasswordInfo(getUserId(), oldPwd, newPwd), listener);
    }

    /**
     * 修改用户昵称
     * @param nickName
     * @param listener
     */
    public void updateNickName(String nickName,final RESTManager.OnObjectDownloadedListener listener){
        Map<String,String> map = new HashMap<>();
        map.put("nickName",nickName);
        mRestManager.updateNickName(map, listener);
    }

    /**
     * 修改手机号码
     * @param
     * @param listener
     */
    public void updatePhoneInfo(String phone,String phoneCode,final RESTManager.OnObjectDownloadedListener listener){
        mRestManager.updatePhoneInfo(new PhoneInfo(getUserId(), phone, phoneCode), listener);
    }
    /**
     * 上传用户头像
     * @param path
     * @param listener
     */
    public void uploadUserLogo(String path, final RESTManager.OnObjectDownloadedListener listener){
        mRestManager.uploadUserLogo(getUserId(), path, listener);
    }

    public UserInfo getUserInfo(){
        return DataPrefs.getUserInfo();
    }


    /**
     * 获取附近商家
     * @param lat 纬度
     * @param log 经度
     */
    public void retrieveNearShopList(String lat,String log){
        mRestManager.retrieveNearShopList(lat, log, new RESTManager.OnObjectDownloadedListener() {
            @Override
            public void onObjectDownloaded(boolean success, String resultStr, String tipStr) {
                if (BuildConfig.DEBUG) {
                    Log.i("retrieveNearShowList:", success + "" + "---" + resultStr);
                }
                if (success) {
                    ShopList shopList = Constants.GSON_RECEIVED.fromJson(resultStr, ShopList.class);
                    mEventBus.post(new ShopListEvent(shopList.getShopInfos(),resultStr));
                } else {
                    mEventBus.post(new ShopListEvent(null,resultStr));
                }
            }
        });
    }

    /**
     * 获取全部商家类型
     * @param listener
     */
    public void retrieveShopTypeList(final RESTManager.OnObjectDownloadedListener listener){
        mRestManager.retrieveShopTypeList(listener);
    }

    /**
     * 获取附近订单商家的列表
     * @param lat
     * @param log
     */
    public void retrieveNearOrderShopList(String lat,String log){
        mRestManager.retrieveNearOrderShopList(lat, log, new RESTManager.OnObjectDownloadedListener() {
            @Override
            public void onObjectDownloaded(boolean success, String resultStr, String tipStr) {
                if (BuildConfig.DEBUG) {
                    Log.i("getNearOrderShopList:", success + "" + "---" + resultStr);
                }
                if (success) {
                    ShopList shopList = Constants.GSON_RECEIVED.fromJson(resultStr, ShopList.class);
                    mEventBus.post(new ShopListEvent(shopList.getShopInfos(),resultStr));
                } else {
                    mEventBus.post(new ShopListEvent(null,resultStr));
                }
            }
        });
    }

    /**
     * 获取商家的订单
     * @param shopId
     */
    public void retrieveShopOrderList(String shopId){
        mRestManager.retrieveShopOrderList(shopId, new RESTManager.OnObjectDownloadedListener() {
            @Override
            public void onObjectDownloaded(boolean success, String resultStr, String tipStr) {
                if (BuildConfig.DEBUG) {
                    Log.i("retrieveShopOrderList:", success + "" + "---" + resultStr);
                }
                ArrayList<OrderInfo> orderInfos = new ArrayList<OrderInfo>();
                if(success){
                    try{
                        JSONObject jsonObject = new JSONObject(resultStr);
                        JSONArray dataArray = jsonObject.optJSONArray("list");
                        if(dataArray!=null){
                            for(int i = 0;i<dataArray.length();i++){
                                orderInfos.add(Constants.GSON_RECEIVED.fromJson(dataArray.optJSONObject(i).toString(),OrderInfo.class));
                            }
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                mEventBus.post(new ReShopOrderListEvent(orderInfos));
            }
        });
    }

    /**
     * 获取指定商家信息
     * @param shopId
     * @param listener
     */
    public void retrieveShopInfo(String shopId,final RESTManager.OnObjectDownloadedListener listener){
        mRestManager.retrieveShopInfo(shopId,listener);
    }

    /**
     * 接单
     * @param orderNum
     */
    public void receiveOrder(String orderNum, final RESTManager.OnObjectDownloadedListener listener){
        mRestManager.receiveOrder(orderNum, listener);
    }

    /**
     * 提交订单
     * @param resOrderInfo
     */
    public void submitOrder(ResOrderInfo resOrderInfo,final RESTManager.OnObjectDownloadedListener listener){
        mRestManager.submitOrder(resOrderInfo,listener);
    }

    /**
     * 投诉
     * @param compainInfo
     * @param listener
     */
    public void submitCompainInfo(PCompainInfo compainInfo, final RESTManager.OnObjectDownloadedListener listener){
        mRestManager.submitCompainInfo(compainInfo,listener);
    }

    /**
     * 完成订单，签收订单并给予评价
     * @param completeOrder
     * @param listener
     */
    public void completeOrder(PCompleteOrder completeOrder, final RESTManager.OnObjectDownloadedListener listener){
        mRestManager.completeOrder(completeOrder,listener);
    }

    /**
     * 更新订单状态
     * @param orderStatus
     * @param listener
     */
    public void updateOrderStatus(POrderStatus orderStatus, final RESTManager.OnObjectDownloadedListener listener) {
        mRestManager.updateOrderStatus(orderStatus, listener);
    }
    /**
     * 获取订单列表
     * @param type 订单状态 {@link com.uni.unidasher.data.status.Status.Order}
     */
    public void retrieveOrderList(int type,boolean isCustomer){
        mRestManager.retrieveOrderList(getUserId(),type,isCustomer?1:2, new RESTManager.OnObjectDownloadedListener() {
            @Override
            public void onObjectDownloaded(boolean success, String resultStr, String tipStr) {
                if (BuildConfig.DEBUG) {
                    Log.i("retrieveOrderList:", success + "" + "---" + resultStr);
                }
                ArrayList<OrderInfo> orderInfos = new ArrayList<OrderInfo>();
                if(success){
                    try{
                        JSONObject jsonObject = new JSONObject(resultStr);
                        JSONArray dataArray = jsonObject.optJSONArray("list");
                        if(dataArray!=null){
                            for(int i = 0;i<dataArray.length();i++){
                                orderInfos.add(Constants.GSON_RECEIVED.fromJson(dataArray.optJSONObject(i).toString(),OrderInfo.class));
                            }
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                mEventBus.post(new ReOrderListEvent(orderInfos));
            }
        });
    }

    /**
     * 获取当前订单，使用于聊天列表
     * @param type
     * @param isCustomer
     */
    public void retrieveActiveOrderListForChat(int type,boolean isCustomer){
        mRestManager.retrieveOrderList(getUserId(),type,isCustomer?1:2,new RESTManager.OnObjectDownloadedListener() {
            @Override
            public void onObjectDownloaded(boolean success, String resultStr, String tipStr) {
                if (BuildConfig.DEBUG) {
                    Log.i("retrieveOrderList:", success + "" + "---" + resultStr);
                }
                ArrayList<OrderInfo> orderInfos = new ArrayList<OrderInfo>();
                if(success){
                    try{
                        JSONObject jsonObject = new JSONObject(resultStr);
                        JSONArray dataArray = jsonObject.optJSONArray("list");
                        if(dataArray!=null){
                            for(int i = 0;i<dataArray.length();i++){
                                orderInfos.add(Constants.GSON_RECEIVED.fromJson(dataArray.optJSONObject(i).toString(),OrderInfo.class));
                            }
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                mEventBus.post(new ReOrderListForChatEvent(orderInfos));
            }
        });
    }

    /**
     * 获取订单的菜品列表
     * @param orderNum 订单号
     */
    public void retrieveOrderDishList(String orderNum){
        mRestManager.retrieveOrderDishList(orderNum, new RESTManager.OnObjectDownloadedListener() {
            @Override
            public void onObjectDownloaded(boolean success, String resultStr, String tipStr) {
                if (BuildConfig.DEBUG) {
                    Log.i("retrieveOrderDishList:", success + "" + "---" + resultStr);
                }
                ArrayList<DishInfo> dishInfos = new ArrayList<DishInfo>();
                if(success){
                    try{
                        JSONObject jsonObject = new JSONObject(resultStr);
                        JSONArray dataArray = jsonObject.optJSONArray("list");
                        if(dataArray!=null){
                            for(int i = 0;i<dataArray.length();i++){
                                dishInfos.add(Constants.GSON_RECEIVED.fromJson(dataArray.optJSONObject(i).toString(),DishInfo.class));
                            }
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                mEventBus.post(new ReOrderDishListEvent(dishInfos));
            }
        });
    }

    /**
     * 获取商家菜品列表
     * @param shopId
     */
    public void retrieveShopDishList(String shopId){
        mRestManager.retrieveShopDishList(shopId, new RESTManager.OnObjectDownloadedListener() {
            @Override
            public void onObjectDownloaded(boolean success, String resultStr, String tipStr) {
                if (BuildConfig.DEBUG) {
                    Log.i("retrieveNearShowList:", success + "" + "---" + resultStr);
                }
                if(success){
                    ShopDishList shopDishList = Constants.GSON_RECEIVED.fromJson(resultStr,ShopDishList.class);
                    mEventBus.post(new ShopDishEvent(shopDishList));
                }else{
                    mEventBus.post(new ShopDishEvent(null));
                }
            }
        });
    }


    /**
     * 获取购物篮信息
     * @return
     */
    public ShoppingBasket getShoppingBasket(){
        ShoppingBasket shoppingBasket = mDatabaseHelper.getByField(ShoppingBasket.class, ShoppingBasket.ID,getUserId());
        return shoppingBasket;
    }

    /**
     * 更新购物篮信息
     * @param shoppingBasket
     * @param listener
     */
    public void updateShoppingBasket(final ShoppingBasket shoppingBasket,final DatabaseHelper.OnDatabaseInsertedListener listener){
//        mDatabaseHelper.delete(mDatabaseHelper.getAll(ShoppingBasket.class), new DatabaseHelper.OnDatabaseInsertedListener() {
//            @Override
//            public void onDatabaseInserted() {
//
//            }
//        });
        shoppingBasket.setId(getUserId());
        mDatabaseHelper.insertIfNotExistsOrUpdate(shoppingBasket,listener);
    }

    public void clearShoppingBasket(DatabaseHelper.OnDatabaseInsertedListener listener){
        List<ShoppingBasket> shoppingBaskets = mDatabaseHelper.getAll(ShoppingBasket.class);
        mDatabaseHelper.delete(shoppingBaskets,listener);
    }

    /**
     * 获取用户地址信息
     * @return
     */
    public UserAdressInfo getUserAddressInfo(){
        return mDatabaseHelper.getByField(UserAdressInfo.class, UserAdressInfo.UserId,getUserId());
    }
    /**
     * 更新用户地址信息
     * @param userAddressInfo
     */
    public void updateUserAdressInfo(UserAdressInfo userAddressInfo){
        userAddressInfo.setUserId(getUserId());
        mDatabaseHelper.insertIfNotExistsOrUpdate(userAddressInfo,null);
    }



    public static DatabaseHelper getDatabaseHelper(){
        return mDatabaseHelper;
    }

    public boolean isUserLogged() {
        return (DataPrefs.getToken() != null);
    }

    private void setAuthToken(String token) {
        DataPrefs.setToken(token);
        mRestManager.refreshRestService(DataPrefs.getSharedHost(), DataPrefs.getToken());
    }

    private void setUserId(String userId){
        DataPrefs.setUserId(userId);
    }

    public String getUserId(){
        return DataPrefs.getUserId();
    }

    public void setUserLogo(String userLogo){
        DataPrefs.setUserLogo(userLogo);
    }

    public String getUserLogo(){
        return AppConstants.HOST+DataPrefs.getUserLogo();
    }

    private void setHxId(String hxId){
        DataPrefs.setHxId(hxId);
    }

    public String getHxId(){
        return DataPrefs.getHxId();
    }

    public String getUserName(){
        return DataPrefs.getUserName();
    }

    public void setUserName(String userName){
        DataPrefs.setUserName(userName);
    }

    public void logOut(){
        loginOut();
        DasherHXSDKHelper.getInstance().logout(null);
        DataPrefs.logOut();
    }
}
