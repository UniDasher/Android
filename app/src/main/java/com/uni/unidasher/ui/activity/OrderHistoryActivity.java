package com.uni.unidasher.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.uni.unidasher.R;
import com.uni.unidasher.data.DataProvider;
import com.uni.unidasher.data.entity.OrderInfo;
import com.uni.unidasher.data.event.ReOrderListEvent;
import com.uni.unidasher.data.status.Status;
import com.uni.unidasher.listener.OnRecyclerViewItemClickListener;
import com.uni.unidasher.ui.adapter.OrderListAdapter;
import com.uni.unidasher.ui.utils.Extras;
import com.uni.unidasher.ui.utils.HandlerHelper;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;

public class OrderHistoryActivity extends EventBusActivity implements BGARefreshLayout.BGARefreshLayoutDelegate{
    public static final int Page_Completed = 0;
    public static final int Page_Error = 1;
    private TextView txtvBack,txtvNavTitle;
    private DataProvider mDataProvider;
    private BGARefreshLayout mRefreshLayout;
    private RecyclerView listvOrder;
    private OrderListAdapter orderListAdapter;
    private TextView txtvCompletedOrder,txtvErrorOrder;
    private boolean isCustomer = true;
    private int currentPage = Page_Completed;
    private ArrayList<OrderInfo> completedOrderList;
    private ArrayList<OrderInfo> errorOrderList;
    private int OrderListStatus = Status.Order.Status_Completed_Order;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        mDataProvider = DataProvider.getInstance(this);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            isCustomer = bundle.getBoolean(Extras.Extra_Is_Customer,true);
        }
        findViews();
        setClickListeners();
        tabOnclick(currentPage);
    }

    private void findViews(){
        txtvBack = (TextView)findViewById(R.id.txtvBack);
        txtvNavTitle = (TextView)findViewById(R.id.txtvNavTitle);
        txtvNavTitle.setText("订单记录");
        txtvCompletedOrder = (TextView)findViewById(R.id.txtvCompletedOrder);
        txtvErrorOrder = (TextView)findViewById(R.id.txtvErrorOrder);

        initRefreshLayout();
        listvOrder = (RecyclerView)findViewById(R.id.listvOrder);
        listvOrder.setHasFixedSize(true);
        listvOrder.setLayoutManager(new LinearLayoutManager(this));
        listvOrder.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.lightGrayBg))
                .size(16)
                .build());
        listvOrder.setItemAnimator(new DefaultItemAnimator());

        orderListAdapter = new OrderListAdapter(this);
        listvOrder.setAdapter(orderListAdapter);
    }

    private void initRefreshLayout() {
        mRefreshLayout = (BGARefreshLayout)findViewById(R.id.refreshLayout);
        // 为BGARefreshLayout设置代理
        mRefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
//        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(this, true));
//        mRefreshLayout.setRefreshViewHolder(new BGAMoocStyleRefreshViewHolder(this, true));
        BGAStickinessRefreshViewHolder refreshViewHolder = new BGAStickinessRefreshViewHolder(this, false);
        refreshViewHolder.setStickinessColor(Color.parseColor("#e7290f"));
//        refreshViewHolder.setRotateDrawable(getResources().getDrawable(R.mipmap.custom_stickiness_roate));
        refreshViewHolder.setRefreshViewBackgroundColorRes(R.color.lightGrayBg);
        // 设置下拉刷新和上拉加载更多的风格
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
    }

    private void setClickListeners(){
        orderListAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                OrderInfo orderInfo = (OrderInfo)orderListAdapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Extras.Extra_Order_Detail_Info_Page, orderInfo);
                bundle.putBoolean(Extras.Extra_Is_Customer,isCustomer);
                startActivity(new Intent(OrderHistoryActivity.this,OrderDetailActivity.class).putExtras(bundle));
//                startActivityForResult(new Intent(getActivity(), OrderDetailActivity.class).putExtras(bundle), Extras.Request_Code_Compain_Or_Complete);
            }
        });
        txtvCompletedOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPage != Page_Completed){
                    currentPage = Page_Completed;
                    tabOnclick(currentPage);
                }
            }
        });

        txtvErrorOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPage != Page_Error){
                    currentPage = Page_Error;
                    tabOnclick(currentPage);
                }
            }
        });
        txtvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void tabOnclick(int page){
        if(page == Page_Completed){
            OrderListStatus = Status.Order.Status_Completed_Order;
            txtvCompletedOrder.setSelected(true);
            txtvErrorOrder.setSelected(false);
            if(completedOrderList == null){
                beginRefreshing();
            }else{
                orderListAdapter.refreshData(completedOrderList);
            }
        }else if(page == Page_Error){
            OrderListStatus = Status.Order.Status_Error_Order;
            txtvCompletedOrder.setSelected(false);
            txtvErrorOrder.setSelected(true);
            if(errorOrderList == null){
                beginRefreshing();
            }else{
                orderListAdapter.refreshData(errorOrderList);
            }
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        // 加载完毕后在UI线程结束下拉刷新
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(500);
                }catch (Exception e){
                    e.printStackTrace();
                }
                mDataProvider.retrieveOrderList(OrderListStatus,isCustomer);
            }
        }).start();
    }

    @Override
    public void onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        // 在这里加载更多数据，或者更具产品需求实现上拉刷新也可以

        // 加载完毕后在UI线程结束加载更多
        mRefreshLayout.endLoadingMore();
    }

    // 通过代码方式控制进入正在刷新状态。应用场景：某些应用在activity的onStart方法中调用，自动进入正在刷新状态获取最新数据
    public void beginRefreshing() {
        mRefreshLayout.beginRefreshing();
        onBGARefreshLayoutBeginRefreshing(mRefreshLayout);
    }

    // 通过代码方式控制进入加载更多状态
    public void beginLoadingMore() {
        mRefreshLayout.beginLoadingMore();
        onBGARefreshLayoutBeginLoadingMore(mRefreshLayout);
    }

    @Subscribe
    public void onRetrieveOrderList(ReOrderListEvent orderListEvent){
        if(orderListEvent!=null&&orderListEvent.getOrderInfos()!=null){
            final ArrayList<OrderInfo> orderInfos = orderListEvent.getOrderInfos();
            HandlerHelper.post(new HandlerHelper.onRun() {
                @Override
                public void run() {
                    if(currentPage == Page_Completed){
                        completedOrderList = orderInfos;
                        orderListAdapter.refreshData(completedOrderList);
                    }else if(currentPage == Page_Error){
                        errorOrderList = orderInfos;
                        orderListAdapter.refreshData(errorOrderList);
                    }
                    mRefreshLayout.endRefreshing();
                }
            });
        }
    }

}
