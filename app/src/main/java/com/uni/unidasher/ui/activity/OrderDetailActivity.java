package com.uni.unidasher.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.uni.unidasher.AppConstants;
import com.uni.unidasher.BuildConfig;
import com.uni.unidasher.R;
import com.uni.unidasher.chat.ui.activity.ChatActivity;
import com.uni.unidasher.data.DasherMgr;
import com.uni.unidasher.data.DataProvider;
import com.uni.unidasher.data.datamodel.databaseAndApi.DishInfo;
import com.uni.unidasher.data.entity.OrderInfo;
import com.uni.unidasher.data.entity.post.PCompainInfo;
import com.uni.unidasher.data.entity.post.PCompleteOrder;
import com.uni.unidasher.data.entity.post.POrderStatus;
import com.uni.unidasher.data.event.ReOrderDishListEvent;
import com.uni.unidasher.data.rest.RESTManager;
import com.uni.unidasher.data.status.OrderDetailStatus;
import com.uni.unidasher.data.status.Status;
import com.uni.unidasher.ui.dialog.AlertConfirmCancel;
import com.uni.unidasher.ui.dialog.AlertPrompt;
import com.uni.unidasher.ui.dialog.EvaluationDialog;
import com.uni.unidasher.ui.utils.Alert;
import com.uni.unidasher.ui.utils.Extras;
import com.uni.unidasher.ui.utils.HandlerHelper;
import com.uni.unidasher.ui.views.TimeFormaterWidget;
import com.uni.unidasher.ui.widget.ListViewForScrollView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderDetailActivity extends EventBusActivity {
    private int CurrentOrderDetailStatusScreen;
    private DataProvider mDataProvider;

    private TextView txtvBack,txtvNavTitle;
    private ListViewForScrollView listvOrderMenu;
    private LayoutInflater inflater;
    private OrderMenuAdapter adapter;
    private ScrollView scrollerView;
    private LinearLayout layoutTime;

    //下单详情
    private HorizontalScrollView timeForamaterScorllView;
    // ----未接单
    private LinearLayout layoutBtnCancelOrder;//取消订单布局
    private TextView txtvCancelOrder;
    //----已接单
    private LinearLayout layoutUserInfo;//用户信息布局
//    private LinearLayout layoutEvaluation;//评价布局
    private LinearLayout layoutCall;//联系布局
    private LinearLayout layoutBtnComplete;//完成订单布局
    private TextView txtvComplain,txtvComplete;
    //接单详情
    private LinearLayout layoutTimePeriod;//时间段布局
    private LinearLayout layoutMapNav;//导航布局
    //----未抢单
    private LinearLayout layoutBtnGetOrder;//抢单布局
    private TextView txtvRob;

    private OrderInfo orderInfo;
    private LinkedHashMap<String,String> timeFormaterMap;
    private DasherMgr mDasherMgr;
    private boolean isCustomer;

    private TextView txtvOrderNum,txtvShopName,txtvOrderStatusDes,txtvSendPrice,txtvDishTotalPrice,txtvTotalPrice;
    private CircleImageView imgvSendUserHead;
    private TextView txtvName;
    private TextView txtvChat;
    private TextView txtvCallSendUser;
    private TextView txtvShopAddress;
    private TextView txtvUserAddress;
    private TextView txtvTime;
    private TextView txtvMapNav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        mDasherMgr = DasherMgr.getInstance(this);
        mDataProvider = DataProvider.getInstance(this);
//        isCustomer = mDasherMgr.isCustomer();
        Bundle bundle = this.getIntent().getExtras();
        if(bundle!=null){
            orderInfo = (OrderInfo)bundle.getSerializable(Extras.Extra_Order_Detail_Info_Page);
            isCustomer = bundle.getBoolean(Extras.Extra_Is_Customer,true);
        }
        findViews();
        setViews();
        setClickListeners();
    }

    private void findViews(){
        inflater = LayoutInflater.from(this);
        txtvBack = (TextView)findViewById(R.id.txtvBack);
        txtvNavTitle = (TextView)findViewById(R.id.txtvNavTitle);
        txtvNavTitle.setText("订单详情");
        scrollerView = (ScrollView)findViewById(R.id.scrollerView);
        txtvOrderNum = (TextView)findViewById(R.id.txtvOrderNum);
        txtvShopName = (TextView)findViewById(R.id.txtvShopName);
        txtvOrderStatusDes = (TextView)findViewById(R.id.txtvOrderStatusDes);
        txtvSendPrice = (TextView)findViewById(R.id.txtvSendPrice);
        txtvDishTotalPrice = (TextView)findViewById(R.id.txtvDishTotalPrice);
        txtvTotalPrice = (TextView)findViewById(R.id.txtvTotalPrice);
        //接单人信息布局
        imgvSendUserHead = (CircleImageView)findViewById(R.id.imgvSendUserHead);
        txtvName = (TextView)findViewById(R.id.txtvName);
        txtvCallSendUser = (TextView)findViewById(R.id.txtvCallSendUser);
        txtvChat = (TextView)findViewById(R.id.txtvChat);

        timeForamaterScorllView = (HorizontalScrollView)findViewById(R.id.timeForamaterScorllView);
        layoutTime = (LinearLayout)findViewById(R.id.layoutTime);
        listvOrderMenu = (ListViewForScrollView)findViewById(R.id.listvOrderMenu);
        layoutBtnCancelOrder = (LinearLayout)findViewById(R.id.layoutBtnCancelOrder);
        txtvCancelOrder = (TextView)findViewById(R.id.txtvCancel);

        layoutUserInfo = (LinearLayout)findViewById(R.id.layoutUserInfo);
//        layoutEvaluation = (LinearLayout)findViewById(R.id.layoutEvaluation);
        layoutCall = (LinearLayout)findViewById(R.id.layoutCall);
        layoutBtnComplete = (LinearLayout)findViewById(R.id.layoutBtnComplete);
        txtvComplain = (TextView)findViewById(R.id.txtvComplain);
        txtvComplete = (TextView)findViewById(R.id.txtvComplete);

        layoutTimePeriod = (LinearLayout)findViewById(R.id.layoutTimePeriod);
        layoutBtnGetOrder = (LinearLayout)findViewById(R.id.layoutBtnGetOrder);
        txtvBack = (TextView)findViewById(R.id.txtvBack);
        txtvRob = (TextView)findViewById(R.id.txtvRob);
        layoutMapNav = (LinearLayout)findViewById(R.id.layoutMapNav);
        txtvShopAddress = (TextView)findViewById(R.id.txtvShopAddress);
        txtvUserAddress = (TextView)findViewById(R.id.txtvUserAddress);
        txtvTime = (TextView)findViewById(R.id.txtvTime);
        txtvMapNav = (TextView)findViewById(R.id.txtvMapNav);

        getStatus();
//        initView();
//        setViews();
    }

    private void getStatus(){
        if(orderInfo==null){
            return;
        }
        timeFormaterMap = new LinkedHashMap<>();
        int orderStatus = orderInfo.getStatus();
        if(orderStatus == Status.Order.Status_Order_Pre_Order){//下单
            if(isCustomer){
                CurrentOrderDetailStatusScreen =  OrderDetailStatus.Status_Order_None;
                timeFormaterMap.put(Status.Order.getTimeFormaterText(1),orderInfo.getCreateDate().substring(5,16));
            }else{
                CurrentOrderDetailStatusScreen = OrderDetailStatus.Status_Get_Order_None;
            }
        }else if(orderStatus == com.uni.unidasher.data.status.Status.Order.Status_Order_Cancel){//取消
            if(isCustomer){
                CurrentOrderDetailStatusScreen =  OrderDetailStatus.Status_Order_Not_Receieved_Error;
                timeFormaterMap.put(Status.Order.getTimeFormaterText(1),orderInfo.getCreateDate().substring(5,16));
                timeFormaterMap.put(Status.Order.getTimeFormaterText(4),orderInfo.getCancleDate().substring(5,16));
            }else{
                //抢单身份获取不到此订单
                CurrentOrderDetailStatusScreen = OrderDetailStatus.Status_Get_Order_None;
            }
        }else if(orderStatus == com.uni.unidasher.data.status.Status.Order.Status_Order_Receieve){//已接
            if(isCustomer){
                CurrentOrderDetailStatusScreen =  OrderDetailStatus.Status_Order_Receive;
                timeFormaterMap.put(Status.Order.getTimeFormaterText(1),orderInfo.getCreateDate().substring(5,16));
                timeFormaterMap.put(Status.Order.getTimeFormaterText(2),orderInfo.getStartDate().substring(5,16));
            }else{
                //抢单身份获取不到此订单
                CurrentOrderDetailStatusScreen = OrderDetailStatus.Status_Get_Order_Receive;
            }

        }else if(orderStatus == com.uni.unidasher.data.status.Status.Order.Status_Order_OverTime){//超时
            if(isCustomer){
                CurrentOrderDetailStatusScreen =  OrderDetailStatus.Status_Order_Not_Receieved_Error;
                timeFormaterMap.put(Status.Order.getTimeFormaterText(1),orderInfo.getCreateDate().substring(5,16));
                timeFormaterMap.put(Status.Order.getTimeFormaterText(6),orderInfo.getOverTimeDate().substring(5,16));
            }else{
                //抢单身份获取不到此订单
                CurrentOrderDetailStatusScreen = OrderDetailStatus.Status_Get_Order_None;
            }

        }else if(orderStatus == com.uni.unidasher.data.status.Status.Order.Status_Order_Complain){//投诉
            if(isCustomer){
                CurrentOrderDetailStatusScreen =  OrderDetailStatus.Status_Order_Error_Or_Completed;
                timeFormaterMap.put(Status.Order.getTimeFormaterText(1),orderInfo.getCreateDate().substring(5,16));
                timeFormaterMap.put(Status.Order.getTimeFormaterText(2),orderInfo.getStartDate().substring(5,16));
                timeFormaterMap.put(Status.Order.getTimeFormaterText(5),orderInfo.getComplainDate().substring(5,16));
            }else{
                CurrentOrderDetailStatusScreen = OrderDetailStatus.Status_Get_Order_Receive;
            }

        }else if(orderStatus == com.uni.unidasher.data.status.Status.Order.Status_Order_Complain_Completed){//投诉完成
            if(isCustomer){
                CurrentOrderDetailStatusScreen =  OrderDetailStatus.Status_Order_Error_Or_Completed;
                timeFormaterMap.put(Status.Order.getTimeFormaterText(1),orderInfo.getCreateDate().substring(5,16));
                timeFormaterMap.put(Status.Order.getTimeFormaterText(2),orderInfo.getStartDate().substring(5,16));
                timeFormaterMap.put(Status.Order.getTimeFormaterText(5),orderInfo.getComplainDate().substring(5,16));
                timeFormaterMap.put(Status.Order.getTimeFormaterText(8),orderInfo.getEndDate().substring(5,16));
            }else{
                CurrentOrderDetailStatusScreen = OrderDetailStatus.Status_Get_Order_Receive;
            }

        }else if(orderStatus == com.uni.unidasher.data.status.Status.Order.Status_Order_OverTime_Completed){//超时完成
            if(isCustomer){
                CurrentOrderDetailStatusScreen =  OrderDetailStatus.Status_Order_Not_Receieved_Error;
                timeFormaterMap.put(Status.Order.getTimeFormaterText(1),orderInfo.getCreateDate().substring(5,16));
                timeFormaterMap.put(Status.Order.getTimeFormaterText(6),orderInfo.getOverTimeDate().substring(5,16));
                timeFormaterMap.put(Status.Order.getTimeFormaterText(9),orderInfo.getEndDate().substring(5,16));
            }else{
                CurrentOrderDetailStatusScreen = OrderDetailStatus.Status_Get_Order_Receive;
            }

        }else if(orderStatus == com.uni.unidasher.data.status.Status.Order.Status_Order_Completed){//完成
            if(isCustomer){
                CurrentOrderDetailStatusScreen =  OrderDetailStatus.Status_Order_Error_Or_Completed;
                timeFormaterMap.put(Status.Order.getTimeFormaterText(1),orderInfo.getCreateDate().substring(5,16));
                timeFormaterMap.put(Status.Order.getTimeFormaterText(2),orderInfo.getStartDate().substring(5,16));
                timeFormaterMap.put(Status.Order.getTimeFormaterText(3), orderInfo.getEndDate().substring(5, 16));
            }else{
                CurrentOrderDetailStatusScreen = OrderDetailStatus.Status_Get_Order_Receive;
            }
        }else if(orderStatus == Status.Order.Status_Order_Cancel_Completed){//取消完成
            if(isCustomer){
                CurrentOrderDetailStatusScreen =  OrderDetailStatus.Status_Order_Not_Receieved_Error;
                timeFormaterMap.put(Status.Order.getTimeFormaterText(1),orderInfo.getCreateDate().substring(5,16));
                timeFormaterMap.put(Status.Order.getTimeFormaterText(4),orderInfo.getCancleDate().substring(5,16));
                timeFormaterMap.put(Status.Order.getTimeFormaterText(7),orderInfo.getEndDate().substring(5,16));
            }else{
                //抢单身份获取不到此订单
                CurrentOrderDetailStatusScreen = OrderDetailStatus.Status_Get_Order_Receive;
            }
        }
    }

    private void setViews(){
        DataProvider.getInstance(this).retrieveOrderDishList(orderInfo.getMid());
        txtvOrderNum.setText(orderInfo.getMid());
        txtvShopName.setText(orderInfo.getShopName());
        txtvOrderStatusDes.setText(Status.Order.getStatusText(orderInfo.getStatus()));

        switch (CurrentOrderDetailStatusScreen){
            case OrderDetailStatus.Status_Order_None:
                setOrderInProgressViews();
                break;
            case OrderDetailStatus.Status_Order_Receive:
                setOrderReceiveViews();
                break;
            case OrderDetailStatus.Status_Get_Order_None:
                setOrderGetViews();
                break;
            case OrderDetailStatus.Status_Get_Order_Receive:
                setOrderRobViews();
                break;
            case OrderDetailStatus.Status_Order_Error_Or_Completed:
                setOrderReceiveViews();
                hideBottomBtnLayout();
                break;
            case OrderDetailStatus.Status_Order_Not_Receieved_Error:
                setOrderInProgressViews();
                hideBottomBtnLayout();
                break;
        }
    }

    private void hideBottomBtnLayout(){
        layoutBtnCancelOrder.setVisibility(View.GONE);
        layoutBtnComplete.setVisibility(View.GONE);
        layoutBtnGetOrder.setVisibility(View.GONE);
    }

    private void setOrderInProgressViews(){
        timeForamaterScorllView.setVisibility(View.VISIBLE);
        layoutUserInfo.setVisibility(View.GONE);
        layoutTimePeriod.setVisibility(View.GONE);
        layoutMapNav.setVisibility(View.GONE);
        //最下方按钮布局
        layoutBtnCancelOrder.setVisibility(View.VISIBLE);
        layoutBtnComplete.setVisibility(View.GONE);
        layoutBtnGetOrder.setVisibility(View.GONE);
        setTimeLine();
        txtvCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                POrderStatus orderStatus = new POrderStatus(orderInfo.getMid(), Status.Order.Status_Order_Cancel);
                mDataProvider.updateOrderStatus(orderStatus, new RESTManager.OnObjectDownloadedListener() {
                    @Override
                    public void onObjectDownloaded(final boolean success, String resultStr, final String tipStr) {
                        if (BuildConfig.DEBUG) {
                            Log.i("updateOrderStatus:", success + "" + "---" + resultStr);
                        }
                        HandlerHelper.post(new HandlerHelper.onRun() {
                            @Override
                            public void run() {
                                if (success) {
                                    Alert.showTip(OrderDetailActivity.this, "取消", "订单已取消，所有款项将在3-10个工作日内退还。", false, new AlertPrompt.OnAlertClickListener() {
                                        @Override
                                        public void onClick() {
                                            txtvComplain.setEnabled(false);
                                            Bundle bundle = new Bundle();
                                            bundle.putBoolean(Extras.Extra_Compain_Or_Complete_Flag, true);
                                            setResult(Extras.Request_Code_Compain_Or_Complete, new Intent().putExtras(bundle));
                                            finish();
                                        }
                                    });
                                } else {
                                    String errorMsg = "取消失败!";
                                    if(!TextUtils.isEmpty(tipStr)){
                                        errorMsg = tipStr;
                                    }
                                    Alert.showDefaultToast(OrderDetailActivity.this,errorMsg);
                                }
                            }
                        });

                    }
                });
            }
        });
    }

    private void setOrderReceiveViews(){
        timeForamaterScorllView.setVisibility(View.VISIBLE);
        layoutUserInfo.setVisibility(View.VISIBLE);
//        layoutEvaluation.setVisibility(View.VISIBLE);
        layoutTimePeriod.setVisibility(View.GONE);
        layoutCall.setVisibility(View.VISIBLE);
        layoutMapNav.setVisibility(View.GONE);
        //最下方按钮布局
        layoutBtnCancelOrder.setVisibility(View.GONE);
        layoutBtnComplete.setVisibility(View.VISIBLE);
        layoutBtnGetOrder.setVisibility(View.GONE);
        setTimeLine();
        txtvName.setText(orderInfo.getSendUserName());
        ImageLoader.getInstance().displayImage(AppConstants.HOST + orderInfo.getSendUserHead(), imgvSendUserHead);
        txtvChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailActivity.this, ChatActivity.class).putExtra(Extras.Extra_Chat_Order_ID, orderInfo.getMid());

                // it is single chat
                intent.putExtra(Extras.Extra_Chat_ID, isCustomer ? orderInfo.getSenderId() : orderInfo.getUid());
                intent.putExtra(Extras.Extra_Chat_Order_ID, orderInfo.getMid());
                intent.putExtra(Extras.Extra_Chat_Name, isCustomer ? orderInfo.getSendUserName() : orderInfo.getUserName());
                intent.putExtra(Extras.Extra_Can_SendMsg,orderInfo.getStatus()==2?true:false);
                intent.putExtra(Extras.Extra_Chat_Logo,orderInfo.getSendUserHead());
                startActivity(intent);
            }
        });

        txtvCallSendUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(isCustomer?orderInfo.getSendUserPhone():orderInfo.getUserPhone());
            }
        });

        txtvComplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PCompainInfo compainInfo = new PCompainInfo(orderInfo.getMid(), orderInfo.getUid(), orderInfo.getSenderId(), 1, "投诉");
                mDataProvider.submitCompainInfo(compainInfo, new RESTManager.OnObjectDownloadedListener() {
                    @Override
                    public void onObjectDownloaded(final boolean success, String resultStr, String tipStr) {
                        if (BuildConfig.DEBUG) {
                            Log.i("submitCompainInfo:", success + "" + "---" + resultStr);
                        }
                        HandlerHelper.post(new HandlerHelper.onRun() {
                            @Override
                            public void run() {
                                if (success) {
                                    Alert.showTip(OrderDetailActivity.this, "投诉", "订单已投诉，等待客服确认！", false, new AlertPrompt.OnAlertClickListener() {
                                        @Override
                                        public void onClick() {
                                            txtvComplain.setEnabled(false);
                                            Bundle bundle = new Bundle();
                                            bundle.putBoolean(Extras.Extra_Compain_Or_Complete_Flag, true);
                                            setResult(Extras.Request_Code_Compain_Or_Complete, new Intent().putExtras(bundle));
                                            finish();
                                        }
                                    });
                                } else {
                                    Alert.showDefaultToast(OrderDetailActivity.this,"投诉失败!");
                                }
                            }
                        });

                    }
                });
            }
        });

        txtvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EvaluationDialog(OrderDetailActivity.this, orderInfo.getSendUserName(), orderInfo.getShopName(), new EvaluationDialog.OnCompainOrderListener() {
                    @Override
                    public void onConfirm(int senderEvaluation, int shopEvaluation) {
                        PCompleteOrder completeOrder = new PCompleteOrder(orderInfo.getMid(),senderEvaluation,shopEvaluation);
                        mDataProvider.completeOrder(completeOrder, new RESTManager.OnObjectDownloadedListener() {
                            @Override
                            public void onObjectDownloaded(final boolean success, String resultStr, String tipStr) {
                                if (BuildConfig.DEBUG) {
                                    Log.i("completeOrder:", success + "" + "---" + resultStr);
                                }
                                HandlerHelper.post(new HandlerHelper.onRun() {
                                    @Override
                                    public void run() {
                                        if(success){
                                            Alert.showTip(OrderDetailActivity.this, "完成", "订单已完成，欢迎下次点单！", false, new AlertPrompt.OnAlertClickListener() {
                                                @Override
                                                public void onClick() {
                                                    txtvComplain.setEnabled(false);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putBoolean(Extras.Extra_Compain_Or_Complete_Flag, true);
                                                    setResult(Extras.Request_Code_Compain_Or_Complete, new Intent().putExtras(bundle));
                                                    finish();
                                                }
                                            });
                                        }else{
                                            Alert.showDefaultToast(OrderDetailActivity.this,"订单签收失败！");
                                        }
                                    }
                                });
                            }
                        });
                    }
                }).show();
            }
        });
    }

    private void setOrderGetViews(){
        timeForamaterScorllView.setVisibility(View.GONE);
        layoutUserInfo.setVisibility(View.VISIBLE);
//        layoutEvaluation.setVisibility(View.GONE);
        layoutTimePeriod.setVisibility(View.VISIBLE);
        layoutCall.setVisibility(View.GONE);
        layoutMapNav.setVisibility(View.VISIBLE);
        //最下方按钮布局
        layoutBtnCancelOrder.setVisibility(View.GONE);
        layoutBtnComplete.setVisibility(View.GONE);
        layoutBtnGetOrder.setVisibility(View.VISIBLE);
        setUserInfo();
        txtvRob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataProvider.receiveOrder(orderInfo.getMid(), new RESTManager.OnObjectDownloadedListener() {
                    @Override
                    public void onObjectDownloaded(final boolean success, String resultStr, final String tipStr) {
                        if (BuildConfig.DEBUG) {
                            Log.i("receiveOrder:", success + "" + "---" + resultStr);
                        }
                        HandlerHelper.post(new HandlerHelper.onRun() {
                            @Override
                            public void run() {
                                if (success) {
                                    Alert.showTip(OrderDetailActivity.this, "接单", "接单成功，请尽快配送！", false, new AlertPrompt.OnAlertClickListener() {
                                        @Override
                                        public void onClick() {
                                            Intent intent = new Intent();
                                            Bundle bundle = new Bundle();
                                            bundle.putBoolean(Extras.Extra_Rob_Order_Flag, true);
                                            intent.putExtras(bundle);
                                            setResult(Extras.Request_Code_Rob_Order, intent);
                                            finish();
                                        }
                                    });
                                } else {
                                    String errorMsg = "接单失败!";
                                    if (!TextUtils.isEmpty(tipStr)) {
                                        errorMsg = tipStr;
                                    }
                                    Alert.showDefaultToast(OrderDetailActivity.this, errorMsg);
                                }
                            }
                        });

                    }
                });
            }
        });

        txtvMapNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(Extras.Extra_User_Latitude,orderInfo.getLatitude());
                bundle.putString(Extras.Extra_User_Longtitude,orderInfo.getLongitude());
                bundle.putString(Extras.Extra_Shop_Latitude,orderInfo.getShopLatitude());
                bundle.putString(Extras.Extra_Shop_Longtitude,orderInfo.getShopLongtitude());
                startActivity(new Intent(OrderDetailActivity.this, MapNavActivity.class).putExtras(bundle));
            }
        });
    }

    private void setOrderRobViews(){
        timeForamaterScorllView.setVisibility(View.GONE);
        layoutUserInfo.setVisibility(View.VISIBLE);
//                layoutEvaluation.setVisibility(View.GONE);
        layoutTimePeriod.setVisibility(View.VISIBLE);
        layoutCall.setVisibility(View.VISIBLE);
        layoutMapNav.setVisibility(View.VISIBLE);
        //最下方按钮布局
        layoutBtnCancelOrder.setVisibility(View.GONE);
        layoutBtnComplete.setVisibility(View.GONE);
        layoutBtnGetOrder.setVisibility(View.GONE);
        setUserInfo();
        txtvChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailActivity.this, ChatActivity.class).putExtra(Extras.Extra_Chat_Order_ID, orderInfo.getMid());

                // it is single chat
                intent.putExtra(Extras.Extra_Chat_ID, isCustomer ? orderInfo.getSenderId() : orderInfo.getUid());
                intent.putExtra(Extras.Extra_Chat_Order_ID, orderInfo.getMid());
                intent.putExtra(Extras.Extra_Chat_Name, isCustomer ? orderInfo.getSendUserName() : orderInfo.getUserName());
                intent.putExtra(Extras.Extra_Can_SendMsg, orderInfo.getStatus() == 2 ? true : false);
                startActivity(intent);
            }
        });
        txtvCallSendUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(isCustomer ? orderInfo.getSendUserPhone() : orderInfo.getUserPhone());
            }
        });

        txtvMapNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(Extras.Extra_User_Latitude,orderInfo.getLatitude());
                bundle.putString(Extras.Extra_User_Longtitude,orderInfo.getLongitude());
                bundle.putString(Extras.Extra_Shop_Latitude,orderInfo.getShopLatitude());
                bundle.putString(Extras.Extra_Shop_Longtitude,orderInfo.getShopLongtitude());
                startActivity(new Intent(OrderDetailActivity.this,MapNavActivity.class).putExtras(bundle));
            }
        });
    }

    /**
     * 时间轴
     */
    private void setTimeLine(){
        if(timeFormaterMap!=null){
            TimeFormaterWidget t = null;
            Iterator<Map.Entry<String, String>> it = timeFormaterMap.entrySet().iterator();
            int count = 0;
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                t = new TimeFormaterWidget(OrderDetailActivity.this);
                t.setStatus(entry.getKey(),entry.getValue(),count == (timeFormaterMap.entrySet().size()-1)?true:false);
                layoutTime.addView(t);
                count++;
            }
        }
    }

    /**
     * 设置下单人信息
     */
    private void setUserInfo(){
        txtvShopAddress.setText(orderInfo.getShopAddress());
        txtvUserAddress.setText(orderInfo.getAddress());
        txtvName.setText(orderInfo.getUserName());
        txtvTime.setText(orderInfo.getMealEndDate().substring(0, 5));
        ImageLoader.getInstance().displayImage(AppConstants.HOST+orderInfo.getUserLogo(), imgvSendUserHead);
    }

    private void setClickListeners(){
        txtvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 拨号
     */
    public void call(final String number){
        new AlertConfirmCancel(this, "联系", number, new AlertConfirmCancel.OnAlertClickListener() {
            @Override
            public void onConfirm() {
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+number));
                startActivity(callIntent);
            }

            @Override
            public void onCancel() {

            }
        }).show();
    }


    @Subscribe
    public void onReOrderDishList(final ReOrderDishListEvent reOrderDishListEvent){
        if(reOrderDishListEvent!=null&&reOrderDishListEvent.getDishInfos()!=null){
            HandlerHelper.post(new HandlerHelper.onRun() {
                @Override
                public void run() {
                    adapter = new OrderMenuAdapter(reOrderDishListEvent.getDishInfos());
                    listvOrderMenu.setAdapter(adapter);
                    scrollerView.smoothScrollTo(0,0);
                    txtvDishTotalPrice.setText((int)orderInfo.getDishsMoney()+"");
                    txtvSendPrice.setText((int)orderInfo.getCarriageMoney()+"");
                    txtvTotalPrice.setText((int)(orderInfo.getDishsMoney()+orderInfo.getCarriageMoney())+"");
                }
            });
        }
    }


    class OrderMenuAdapter extends BaseAdapter{
        private ArrayList<DishInfo> dishInfos;
        public OrderMenuAdapter(ArrayList<DishInfo> ls){
            if(ls == null){
                dishInfos = new ArrayList<>();
            }else{
                dishInfos = ls;
            }
        }

        @Override
        public int getCount() {
            return dishInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = inflater.inflate(R.layout.adapter_listview_completed_order_menu_list,parent,false);
            }
            ViewHolder viewHolder = (ViewHolder)convertView.getTag();
            if(viewHolder == null){
                viewHolder = new ViewHolder();
                viewHolder.txtvDishName = (TextView)convertView.findViewById(R.id.txtvDishName);
                viewHolder.txtvDishPrice = (TextView)convertView.findViewById(R.id.txtvDishPrice);
                viewHolder.txtvDishCount = (TextView)convertView.findViewById(R.id.txtvDishCount);
                convertView.setTag(viewHolder);
            }
            DishInfo dishInfo = dishInfos.get(position);
            viewHolder.txtvDishName.setText(dishInfo.getDishName());
            viewHolder.txtvDishPrice.setText(dishInfo.getPrice()+"");
            viewHolder.txtvDishCount.setText(dishInfo.getCount()+"");
            return convertView;
        }
        class ViewHolder {
            TextView txtvDishName;
            TextView txtvDishPrice;
            TextView txtvDishCount;
        }
    }
}
