package com.uni.unidasher.ui.utils;

/**
 * Created by Administrator on 2015/6/23.
 */
public class Extras {
    public static final String Extra_ShopInfo = "ShopInfo";//点击marker，marker保存的商家基本信息
    public static final String Extra_PlaceOrderUserInfo = "PlaceOrderUserInfo";//下单信息，地址，经纬度， 姓名
    public static final String Extra_Modify_Base_Info_Page = "ModifyBaseInfoPage";//修改用户信息界面，密码。。。
    public static final String Extra_Order_Detail_Info_Page = "OrderDetailInfo";//订单详细信息
    public static final String Extra_Is_Customer = "IsCustomer";//当前身份
    public static final String Extra_Modify_BaseInfo_Type = "ModifyBaserInfoType";
    /**
     * {@link com.uni.unidasher.ui.activity.OrderDetailActivity}
     * {@link com.uni.unidasher.ui.activity.RestaurantOrderActivity}
     *  是否抢单，刷新上个界面
     *  Activity的request code
     */
    public static final int Request_Code_Rob_Order = 0;
    public static final String Extra_Rob_Order_Flag = "RobOrderFlag";

    /**
     * {@link com.uni.unidasher.ui.activity.OrderDetailActivity}
     * {@link com.uni.unidasher.ui.activity.TabActivity}
     *  是否投诉或者完成订单，返回需要刷新上个界面
     *  Activity的request code
     */
    public static final int Request_Code_Compain_Or_Complete = 1;
    public static final String Extra_Compain_Or_Complete_Flag = "CompainOrCompleteFlag";

    /**
     * {@link com.uni.unidasher.ui.activity.restaurant.OrderPayActivity}
     * {@link com.uni.unidasher.ui.activity.restaurant.RestaurantActivity}
     *  是否投诉或者完成订单，返回需要刷新上个界面
     *  Activity的request code
     */
    public static final int Request_Code_Update_Dishes_Of_Pay = 2;
    public static final String Extra_Update_Dishes_Of_Pay_Flag = "UpdateDishesOfPayFlag";

    /**
     * 聊天记录ID
     */
    public static final String Extra_Chat_Order_ID = "ChatOrderId";
    public static final String Extra_Chat_Name = "ChatName";
    public static final String Extra_Chat_ID = "ChatId";
    public static final String Extra_Can_SendMsg = "IsCanSendMsg";
    public static final String Extra_Chat_Logo = "ChatUserHeadLogo";

    /**
     * 拍照
     */
    public static final int Request_Code_Take_Photo = 0;
    public static final int Request_Code_Pick_Photo = 1;

    /**
     * 密码修改 {@link com.uni.unidasher.ui.activity.TabActivity}
     */

    public static final int Request_Code_Modify_Password = 2;
    public static final String Extra_Modify_Password_Flag = "ModifyPasswordFlag";

    /**
     * 手机号 {@link com.uni.unidasher.ui.activity.ForgotPasswordActivity}
     */
    public static final String Extra_Phone_Num = "PhoneNum";

    /**
     * 经纬度{@link com.uni.unidasher.ui.activity.MapNavActivity}
     */
    public static final String Extra_Shop_Latitude = "ShopLatitude";
    public static final String Extra_Shop_Longtitude = "ShopLongtitude";
    public static final String Extra_Sender_Latitude = "SenderLatitude";
    public static final String Extra_Sender_Longtitude = "SenderLongtitude";
    public static final String Extra_User_Latitude = "UserLatitude";
    public static final String Extra_User_Longtitude = "UserLongtitude";

}
