package com.uni.unidasher.data.status;

/**
 * Created by Administrator on 2015/6/16.
 */
public class OrderDetailStatus {
    public static final int Status_Order_None = 0;//下单，未接
    public static final int Status_Order_Receive = 1;//下单，已接
    public static final int Status_Get_Order_None = 2;//抢单，未抢
    public static final int Status_Get_Order_Receive = 3;//抢单，已抢
    public static final int Status_Order_Error_Or_Completed = 4;//终止订单或完成订单
    public static final int Status_Order_Not_Receieved_Error = 5;//订单未接异常终止
}
