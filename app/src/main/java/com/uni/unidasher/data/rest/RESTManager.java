package com.uni.unidasher.data.rest;

import android.content.Context;
import android.util.Log;

import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.uni.unidasher.BuildConfig;
import com.uni.unidasher.data.api.ResOrderInfo;
import com.uni.unidasher.data.api.request.DeviceInfo;
import com.uni.unidasher.data.api.request.ForgotPassword;
import com.uni.unidasher.data.api.request.PasswordInfo;
import com.uni.unidasher.data.api.request.PhoneInfo;
import com.uni.unidasher.data.api.request.SenderValidationInfo;
import com.uni.unidasher.data.entity.LoginInfo;
import com.uni.unidasher.data.entity.RegisterInfo;
import com.uni.unidasher.data.entity.UserLogoFile;
import com.uni.unidasher.data.entity.post.PCompainInfo;
import com.uni.unidasher.data.entity.post.PCompleteOrder;
import com.uni.unidasher.data.entity.post.POrderStatus;
import com.uni.unidasher.data.status.NetStatus;
import com.uni.unidasher.data.utils.Constants;
import com.uni.unidasher.data.utils.CustomNoConverter;

import org.json.JSONObject;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;

/**
 * Created by Administrator on 2015/5/15.
 */
public class RESTManager {
    private static final String TAG = "RESTManager";

    private static final Executor mRequestExecutor = Executors.newSingleThreadExecutor();
    private static final Executor mCallbackExecutor = Executors.newCachedThreadPool();
    private RESTService mRestService;
    EventBus mEventBus;
    Context mContext;
    private String currentHost;

    public RESTManager(Context context, EventBus eventBus, String host, String authToken) {
        mContext = context;
        mEventBus = eventBus;
        refreshRestService(host, authToken);
    }

    public void refreshRestService(String currentHost, final String authToken) {
        this.currentHost = currentHost;
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setExecutors(mRequestExecutor, mCallbackExecutor)
                .setEndpoint(currentHost)
                .setConverter(new CustomNoConverter(new Gson()))
                .setClient(new OkClient(new OkHttpClient()));
//                .setClient(new ApacheClient(TrustAllSSLClient.getClient()));
//        if (BuildConfig.DEBUG) {
//            builder.setLogLevel(RestAdapter.LogLevel.BASIC)
//                    .setLog(new AndroidLog("RESTManager: "));
//        }

        if (authToken != null) {
            builder.setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    Log.i("authToken:",authToken);
                    request.addHeader(RESTService.HEADER_AUTH_TOKEN_KEY, authToken);
                }
            });
        }
        mRestService = builder.build().create(RESTService.class);
    }

    public void signUp(final RegisterInfo registerInfo, final OnObjectDownloadedListener listener){
        mRestService.signUp(registerInfo, new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if(listener!=null){
                    listener.onObjectDownloaded(true,result.toString(),"");
                }
            }

            @Override
            public void onFailure(String error,String resultDesc) {
                if(listener!=null){
                    listener.onObjectDownloaded(false,error,resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return registerInfo;
            }
        });
    }

    public void loginOut(final OnObjectDownloadedListener listener){
        mRestService.loginOut(new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }

    public void signIn(final LoginInfo loginInfo,final OnObjectDownloadedListener listener){
        mRestService.signIn(loginInfo, new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return loginInfo;
            }
        });
    }

    /**
     * 忘记密码
     * @param forgotPassword
     * @param listener
     */
    public void forgotPassowrd(ForgotPassword forgotPassword,final OnObjectDownloadedListener listener){
        mRestService.forgotPassowrd(forgotPassword, new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }

    /**
     * 发送设备基本信息
     * @param deviceInfo
     */
    public void reportDeviceInfo(DeviceInfo deviceInfo,final OnObjectDownloadedListener listener){
        mRestService.reportDeviceInfo(deviceInfo, new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }

    /**
     * 送餐人身份验证
     * @param senderValidationInfo
     * @param listener
     */
    public void reportSenderValidation(SenderValidationInfo senderValidationInfo,final OnObjectDownloadedListener listener){
        mRestService.reportSenderValidation(senderValidationInfo, new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }

    /**
     * 获取收益信息
     */
    public void retrieveEarn(final OnObjectDownloadedListener listener){
        mRestService.retrieveEarn(new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }

    /**
     * 获取支出列表
     * @param startDate
     * @param endDate
     * @param listener
     */
    public void retrieveOutList(String startDate,String endDate,final OnObjectDownloadedListener listener){
        mRestService.retrieveOutList(startDate, endDate, new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }

    /**
     * 获取收益列表
     * @param startDate
     * @param endDate
     * @param listener
     */
    public void retrieveEarnList(String startDate,String endDate,final OnObjectDownloadedListener listener){
        mRestService.retrieveEarnList(startDate, endDate, new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }

    /**
     * 刷新用户基本信息
     * @param userId
     * @param listener
     */
    public void refreshUserInfo(String userId,final OnObjectDownloadedListener listener){
        mRestService.getUserInfo(userId, new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }

    /**
     * 修改密码
     * @param passwordInfo
     * @param listener
     */
    public void updatePassword(PasswordInfo passwordInfo,final OnObjectDownloadedListener listener){
        mRestService.updatePassword(passwordInfo, new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }

    /**
     * 修改用户昵称
     * @param map
     * @param listener
     */
    public void updateNickName(Map<String,String> map,final OnObjectDownloadedListener listener){
        mRestService.updateNickName(map, new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }

    /**
     * 修改手机号码
     * @param phoneInfo
     * @param listener
     */
    public void updatePhoneInfo(PhoneInfo phoneInfo,final OnObjectDownloadedListener listener){
        mRestService.updatePhoneInfo(phoneInfo, new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }
    /**
     * 上传用户头像
     * @param userId
     * @param path
     * @param listener
     */
    public void uploadUserLogo(String userId,String path, final OnObjectDownloadedListener listener){
        File image = new File(path);
        String mimeType = "image/jpeg";
        TypedFile imageFile = new TypedFile(mimeType, image);

        mRestService.uploadUserLogo(userId, imageFile, new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }

    /**
     * 获取附近商家
     * @param lat
     * @param log
     * @param listener
     */
    public void retrieveNearShopList(String lat,String log,final OnObjectDownloadedListener listener){
        mRestService.retrieveNearShopList(lat, log, new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }

    /**
     * 获取所有商家类型
     * @param listener
     */
    public void retrieveShopTypeList(final OnObjectDownloadedListener listener){
        mRestService.retrieveShopTypeList(new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }
    /**
     * 获取指定商家信息
     * @param shopId
     * @param listener
     */
    public void retrieveShopInfo(String shopId,final OnObjectDownloadedListener listener){
        mRestService.retrieveShopInfo(shopId, new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }

    /**
     * 获取附近订单商家的列表
     * @param lat
     * @param log
     * @param listener
     */
    public void retrieveNearOrderShopList(String lat,String log,final OnObjectDownloadedListener listener){
        mRestService.retrieveNearOrderShopList(lat, log, new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }

    /**
     * 获取商家的订单
     * @param shopId
     * @param listener
     */
    public void retrieveShopOrderList(String shopId, final OnObjectDownloadedListener listener){
        mRestService.retrieveShopOrderList(shopId, new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }

    /**
     * 接单
     * @param orderNum
     * @param listener
     */
    public void receiveOrder(String orderNum, final OnObjectDownloadedListener listener){
        Map<String,Object> map = new HashMap<>();
        map.put("mid",orderNum);
        mRestService.receiveOrder(map, new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }

    /**
     * 投诉
     * @param compainInfo
     * @param listener
     */
    public void submitCompainInfo(PCompainInfo compainInfo, final OnObjectDownloadedListener listener){
        mRestService.submitCompainInfo(compainInfo, new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }


    /**
     * 完成订单，签收订单并给予评价
     * @param completeOrder
     * @param listener
     */
    public void completeOrder(PCompleteOrder completeOrder, final OnObjectDownloadedListener listener){
        mRestService.completeOrder(completeOrder, new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }

    /**
     * 更新订单状态
     * @param pOrderStatus
     * @param listener
     */
    public void updateOrderStatus(POrderStatus pOrderStatus, final OnObjectDownloadedListener listener){
        mRestService.updateOrderStatus(pOrderStatus, new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }

    /**
     * 获取商家菜品列表
     * @param shopId
     */
    public void retrieveShopDishList(String shopId,final OnObjectDownloadedListener listener){
        mRestService.retrieveShopDishList(shopId, new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }

    /**
     * 提交订单
     * @param resOrderInfo
     * @param listener
     */
    public void submitOrder(ResOrderInfo resOrderInfo,final OnObjectDownloadedListener listener){
        mRestService.submitOrder(resOrderInfo, new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }

    /**
     * 获取订单列表
     * @param type 订单类型
     * @param listener
     */
    public void retrieveOrderList(String userId,int type,int userType, final OnObjectDownloadedListener listener){
        mRestService.retrieveOrderList(userId,type,userType, new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }

    /**
     * 获取订单的菜品列表
     * @param orderNum 订单号
     * @param listener
     */
    public void retrieveOrderDishList(String orderNum, final OnObjectDownloadedListener listener){
        mRestService.retrieveOrderDishList(orderNum, new BaseCallback() {
            @Override
            public void onSuccessObject(Object result, Response response) {
                if (listener != null) {
                    listener.onObjectDownloaded(true, result.toString(), "");
                }
            }

            @Override
            public void onFailure(String error, String resultDesc) {
                if (listener != null) {
                    listener.onObjectDownloaded(false, error, resultDesc);
                }
            }

            @Override
            public Object getRequestObject() {
                return null;
            }
        });
    }

    public abstract class BaseCallback implements Callback<Object> {
        @Override
        public void success(Object result, Response response) {
            try {
                JSONObject jsonObject = new JSONObject(result.toString());
                int resultCode = jsonObject.optInt("resultCode");
                if(resultCode == NetStatus.Code_Success){
                    onSuccessObject(result, response);
                }else if(resultCode == NetStatus.Code_Error) {
                    onFailure(result.toString(),jsonObject.optString("resultDesc",""));
                }else {
                    onFailure(result.toString(),"");
                }
            } catch (Exception exception) {
                onFailure(null,"");
            }
        }
        @Override
        public void failure(RetrofitError error) {
            String errorString = "";
            if (error != null) {
                errorString = "response - failure: ";
                if (error.getUrl() != null) {
                    errorString += error.getUrl();
                }
                if (error.getResponse() != null) {
                    errorString += ", status: " + error.getResponse().getStatus();
                }
                if (error.getMessage() != null) {
                    errorString += ", message: " + error.getMessage();
                }
                if (getRequestedObjectString() != null) {
                    errorString += ", request: " + getRequestedObjectString();
                }
                if(error.getResponse()!=null){
                    errorString +=",error: " + new String(((TypedByteArray)error.getResponse().getBody()).getBytes());
                }
                if (error.getCause() instanceof SocketTimeoutException) {
                    errorString +="Timeut";
                }
            }

            onFailure(errorString,"");
        }

        public abstract void onSuccessObject(Object result, Response response);

        public abstract void onFailure(String error, String resultDesc);

        public abstract Object getRequestObject();

        private String getRequestedObjectString() {
            try {
                Object object = getRequestObject();

                if (object != null) {
                    if (object.getClass().equals(String.class)) {
                        return (String) object;
                    } else {
                        return Constants.GSON_SENT.toJson(object);
                    }
                } else {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static interface OnObjectDownloadedListener {
        public void onObjectDownloaded(boolean success, String resultStr, String tipStr);
    }
}
