package com.uni.unidasher.data.status;

/**
 * Created by Administrator on 2015/6/27.
 */
public class Status {
    public class ModifyBaseInfo{
        public static final int Status_Modify_Password = 0;//密码修改
        public static final int Status_Account_Info = 1;//账号基本信息
        public static final int Status_Send_Validate_Info = 2;//身份验证信息
    }

    /**
     * 订单的一些状态参数
     */
    public static class Order{
        public static final int SendPrice = 3;//服务费

        //获取订单列表
        public static final int Status_In_Progress = 1;//正在进行的订单
        public static final int Status_Completed_Order = 2;//完成的订单
        public static final int Status_Error_Order = 3;//异常中止的订单

        //订单的状态
        /*
        1预订单（用户下单，尚未有人接单）
        2配送中（送餐人员接收订单）
        3完成订单（配送完成）
        4取消订单
        5配送投诉
        6订单延时
        7取消处理
        8投诉处理
        9延时处理
        */
        public static final int Status_Order_Pre_Order = 1;
        public static final int Status_Order_Receieve = 2;
        public static final int Status_Order_Completed = 3;
        public static final int Status_Order_Cancel = 4;
        public static final int Status_Order_Complain = 5;
        public static final int Status_Order_OverTime = 6;
        public static final int Status_Order_Cancel_Completed = 7;
        public static final int Status_Order_Complain_Completed = 8;
        public static final int Status_Order_OverTime_Completed = 9;


        public static final String timerDes[] = {"下单时间","接单时间","完成时间","取消时间","投诉时间","超时时间","退款成功","退款成功","退款成功"};
        public static final String orderStatusDesc[] = {"已下单","已接单","订单完成","订单取消","订单投诉","订单超时","退款成功","退款成功","退款成功"};
        public static final int orderStatus[] = {1,2};

        /* 获取对应状态数值的描述
         */
        public static String getStatusText(int status){
            if(status>(orderStatusDesc.length)){
                return "错误";
            }
            return orderStatusDesc[status-1];
        }

        public static String getTimeFormaterText(int status){
            if(status>timerDes.length){
                return "";
            }else{
                return timerDes[status-1];
            }
        }

        public static String[] getTimerFormater(int status){
            if(status>(timerDes.length)){
                return null;
            }
            String timeFormater[] = new String[status];
            for(int i = 0;i<(status);i++){
                timeFormater[i] = timerDes[i];
            }
            return timeFormater;
        }

        public static final String direction[] = new String[]{"正东","正南","正西","正北","东南","西南","西北","东北"};

        public static int getImgLevel(String dir){
            int level = 0;
            for(int i = 0;i<direction.length;i++){
                if(dir.equals(direction[i])){
                    level = i;
                    break;
                }
            }
            return level;
        }
    }

    public static class User{
        /*0未审核
        1审核中
        2审核通过
        3冻结状态
        4审核未通过*/
        public static final int Validate_Success = 2;
        public static final String IdentityVerificationStatus[] = new String[]{"未审核","审核中","审核通过","冻结状态","审核未通过"};

        public static final int Modify_User_Phone = 0;
        public static final int Modify_User_NickName = 1;

    }
}
