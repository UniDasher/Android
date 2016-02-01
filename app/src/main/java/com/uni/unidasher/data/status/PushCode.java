package com.uni.unidasher.data.status;

/**
 * Created by Administrator on 2015/7/16.
 */
public class PushCode {
    public static final String PushChatExtrasCode = "PushCode";
    public static final String PushExtrasUserType = "UserType";

    public static final int Code_Chat_Msg = 20;//环信聊天消息
    public static final int Code_Apply_Sender = 1;
    public static final int Code_Order_Received = 2;
    public static final int Code_Order_Completed = 3;
    public static final int Code_Order_TimeOut = 4;
    public static final int Code_Order_Complained = 5;
    public static final int Code_Order_Cancel_Completed = 6;
    public static final int Code_Order_TimeOut_Completed = 7;
    public static final int Code_Order_Complained_Completed = 8;
    public static final int Code_Sender_Get_Money = 9;



   /*
    身份#命令#标题#内容
    1.	用户申请送餐人推送
    2.	用户订单接收推送
    3.	用户订单完成推送（推送给送餐人）
    4.	用户订单超时推送
    5.	用户订单投诉推送（推送给送餐人）
    6.	用户订单取消处理完成，退款推送
    7.	用户订单超时处理完成，退款推送
    8.	用户订单投诉处理完成，退款推送
    9.	送餐人结算推送（推送给送餐人）
    */



}
