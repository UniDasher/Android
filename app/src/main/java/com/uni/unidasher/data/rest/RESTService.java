package com.uni.unidasher.data.rest;

import com.uni.unidasher.data.api.ResOrderInfo;
import com.uni.unidasher.data.api.request.DeviceInfo;
import com.uni.unidasher.data.api.request.ForgotPassword;
import com.uni.unidasher.data.api.request.PasswordInfo;
import com.uni.unidasher.data.api.request.PhoneInfo;
import com.uni.unidasher.data.api.request.SenderValidationInfo;
import com.uni.unidasher.data.entity.LoginInfo;
import com.uni.unidasher.data.entity.RegisterInfo;
import com.uni.unidasher.data.entity.post.PCompainInfo;
import com.uni.unidasher.data.entity.post.PCompleteOrder;
import com.uni.unidasher.data.entity.post.POrderStatus;

import java.util.Map;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by Administrator on 2015/5/15.
 */
public interface RESTService {
    public static final String HEADER_AUTH_TOKEN_KEY = "x-auth-token";
    public static final String AuthToken = "authCode";

    @POST("/phone/user/add")
    void signUp(@Body RegisterInfo registerInfo,RESTManager.BaseCallback callback);

    @POST("/phone/user/pwd/forget")
    void forgotPassowrd(@Body ForgotPassword forgotPassword,RESTManager.BaseCallback callback);

    @POST("/phone/user/push")
    void reportDeviceInfo(@Body DeviceInfo deviceInfo,RESTManager.BaseCallback callback);

    @POST("/phone/user/status")
    void reportSenderValidation(@Body SenderValidationInfo senderValidationInfo,RESTManager.BaseCallback callback);

    @POST("/phone/user/login")
    void signIn(@Body LoginInfo loginInfo,RESTManager.BaseCallback callback);

    @GET("/phone/user/logout")
    void loginOut(RESTManager.BaseCallback callback);

    @GET("/phone/user/info")
    void getUserInfo(@Query("uid")String userId,RESTManager.BaseCallback callback);

    @POST("/phone/user/pwd/update")
    void updatePassword(@Body PasswordInfo passwordInfo,RESTManager.BaseCallback callback);

    @POST("/phone/user/update/nickname")
    void updateNickName(@Body Map<String,String> map,RESTManager.BaseCallback callback);

    @POST("/phone/user/update/phone")
    void updatePhoneInfo(@Body PhoneInfo phoneInfo,RESTManager.BaseCallback callback);

    @GET("/phone/shop/list/near")
    void retrieveNearShopList(@Query("latitude")String latitude,
                              @Query("longitude")String longitude,
                              RESTManager.BaseCallback callback);

    @GET("/phone/menu/list/near/shop")
    void retrieveNearOrderShopList(@Query("latitude")String latitude,
                                   @Query("longitude")String longitude,
                                   RESTManager.BaseCallback callback);
    @GET("/phone/shop/info")
    void retrieveShopInfo(@Query("sid")String shopId,RESTManager.BaseCallback callback);

    @GET("/phone/dish/list")
    void retrieveShopDishList(@Query("sid")String sid,
                              RESTManager.BaseCallback callback);

    @POST("/phone/menu/add")
    void submitOrder(@Body ResOrderInfo resOrderInfo,RESTManager.BaseCallback callback);

    @GET("/phone/menu/user/list")
    void retrieveOrderList(@Query("uid")String userId,
                            @Query("type")int type,
                           @Query("userType")int userType,
                           RESTManager.BaseCallback callback);

    @GET("/phone/menu/user/list")
    void retrieveActiveOrderListForChat(@Query("uid")String userId,
                                        @Query("type")int type,
                                        @Query("userType")int userType,
                                        RESTManager.BaseCallback callback);

    @GET("/phone/menu/dish/list")
    void retrieveOrderDishList(@Query("mid")String orderNum,RESTManager.BaseCallback callback);

    @GET("/phone/menu/list/near/sid")
    void retrieveShopOrderList(@Query("sid")String shopId,RESTManager.BaseCallback callback);

    @POST("/phone/menu/receive")
    void receiveOrder(@Body Map<String,Object> map,RESTManager.BaseCallback callback);

    @POST("/phone/complain/add")
    void submitCompainInfo(@Body PCompainInfo compainInfo,RESTManager.BaseCallback callback);

    @POST("/phone/menu/complete")
    void completeOrder(@Body PCompleteOrder completeOrder,RESTManager.BaseCallback callback);

    @POST("/phone/menu/update/status")
    void updateOrderStatus(@Body POrderStatus pOrderStatus,RESTManager.BaseCallback callback);

    @PUT("/phone/user/update/file")
    @Multipart
    void uploadUserLogo(@Part("uid")String userId,
            @Part("file")TypedFile file,RESTManager.BaseCallback callback);

    @GET("/phone/earn/total")
    void retrieveEarn(RESTManager.BaseCallback callback);

    @GET("/phone/settle/user/list")
    void retrieveOutList(@Query("startDate")String startDate,
                     @Query("endDate")String endDate,
                     RESTManager.BaseCallback callback);

    @GET("/phone/earn/list")
    void retrieveEarnList(@Query("startDate")String startDate,
                     @Query("endDate")String endDate,
                     RESTManager.BaseCallback callback);

    @GET("/phone/shop/type")
    void retrieveShopTypeList(RESTManager.BaseCallback callback);
}
