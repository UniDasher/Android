package com.uni.unidasher.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.common.eventbus.Subscribe;
import com.uni.unidasher.R;
import com.uni.unidasher.data.DasherMgr;
import com.uni.unidasher.data.DataProvider;
import com.uni.unidasher.data.entity.OrderInfo;
import com.uni.unidasher.data.event.ReOrderListEvent;
import com.uni.unidasher.data.status.Status;
import com.uni.unidasher.listener.OnRecyclerViewItemClickListener;
import com.uni.unidasher.ui.activity.OrderDetailActivity;
import com.uni.unidasher.ui.adapter.OrderListAdapter;
import com.uni.unidasher.ui.utils.Extras;
import com.uni.unidasher.ui.utils.HandlerHelper;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;

/**
 * Created by Administrator on 2015/5/21.
 */
public class OrderFragment extends EventBusFragment implements BGARefreshLayout.BGARefreshLayoutDelegate {
    private View mRootView;
    private BGARefreshLayout mRefreshLayout;
    private RecyclerView listvOrder;
    private OrderListAdapter orderListAdapter;
    private DataProvider mDataProvider;
    private DasherMgr mDasherMgr;
    private int CurrentItem;
    private LinearLayout layoutEmpty;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDasherMgr = DasherMgr.getInstance(getActivity());
        mDataProvider = DataProvider.getInstance(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        findViews(inflater, container);
        setClickListeners();
        return mRootView;
    }

    private void findViews(LayoutInflater inflater, ViewGroup container) {
        mRootView = inflater.inflate(R.layout.fragment_order, container, false);
        initRefreshLayout();
        layoutEmpty = (LinearLayout)mRootView.findViewById(R.id.layoutEmpty);
        listvOrder = (RecyclerView)mRootView.findViewById(R.id.listvOrder);
        listvOrder.setHasFixedSize(true);
        listvOrder.setLayoutManager(new LinearLayoutManager(getActivity()));
        listvOrder.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .color(getResources().getColor(R.color.lightGrayBg))
                .size(16)
                .build());
        listvOrder.setItemAnimator(new DefaultItemAnimator());

        orderListAdapter = new OrderListAdapter(getActivity());
        listvOrder.setAdapter(orderListAdapter);
//        for(int i = 0;i<orderListAdapter.getItemCount();i++){
//            View view = listvOrder.getLayoutManager().getChildAt(i);
//            ((HorizontalScrollView)view.findViewById(R.id.scrollerView)).fullScroll(HorizontalScrollView.FOCUS_RIGHT);
//        }
    }

    private void initRefreshLayout() {
        mRefreshLayout = (BGARefreshLayout)mRootView.findViewById(R.id.refreshLayout);
        // 为BGARefreshLayout设置代理
        mRefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
//        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(this, true));
//        mRefreshLayout.setRefreshViewHolder(new BGAMoocStyleRefreshViewHolder(this, true));
        BGAStickinessRefreshViewHolder refreshViewHolder = new BGAStickinessRefreshViewHolder(getActivity(), false);
        refreshViewHolder.setStickinessColor(Color.parseColor("#e7290f"));
//        refreshViewHolder.setRotateDrawable(getResources().getDrawable(R.mipmap.custom_stickiness_roate));
        refreshViewHolder.setRefreshViewBackgroundColorRes(R.color.lightGrayBg);
        // 设置下拉刷新和上拉加载更多的风格
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);


        // 为了增加下拉刷新头部和加载更多的通用性，提供了以下可选配置选项  -------------START
        // 设置正在加载更多时的文本
//        refreshViewHolder.setLoadingMoreText("正在加载...");
        // 设置整个加载更多控件的背景颜色资源id
//        refreshViewHolder.setLoadMoreBackgroundColorRes(R.color.red);
        // 设置整个加载更多控件的背景drawable资源id
//        refreshViewHolder.setLoadMoreBackgroundDrawableRes(loadMoreBackgroundDrawableRes);
        // 设置下拉刷新控件的背景颜色资源id
//        refreshViewHolder.setRefreshViewBackgroundColorRes(R.color.grape_dark);
        // 设置下拉刷新控件的背景drawable资源id
//        refreshViewHolder.setRefreshViewBackgroundDrawableRes(refreshViewBackgroundDrawableRes);
        // 设置自定义头部视图（也可以不用设置）     参数1：自定义头部视图（例如广告位）， 参数2：上拉加载更多是否可用
//        mRefreshLayout.setCustomHeaderView(mBanner, false);
        // 可选配置  -------------END
    }

    private void setClickListeners(){
        orderListAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                CurrentItem = position;
                OrderInfo orderInfo = (OrderInfo)orderListAdapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Extras.Extra_Order_Detail_Info_Page, orderInfo);
                bundle.putBoolean(Extras.Extra_Is_Customer, mDasherMgr.isCustomer());
                startActivityForResult(new Intent(getActivity(), OrderDetailActivity.class).putExtras(bundle), Extras.Request_Code_Compain_Or_Complete);
            }
        });
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
                mDataProvider.retrieveOrderList(Status.Order.Status_In_Progress,mDasherMgr.isCustomer());
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

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 重新加载数据
     */
    public void refreshOrderList(){
        HandlerHelper.post(new HandlerHelper.onRun() {
            @Override
            public void run() {
                beginRefreshing();
            }
        });
    }

    /**
     * 删除指定position
     */
    public void deleteOrder(){
        HandlerHelper.post(new HandlerHelper.onRun() {
            @Override
            public void run() {
                orderListAdapter.deleteOrder(CurrentItem);
            }
        });
    }
    @Subscribe
    public void onRetrieveOrderList(ReOrderListEvent orderListEvent){
        if(orderListEvent!=null&&orderListEvent.getOrderInfos()!=null){
            final ArrayList<OrderInfo> orderInfos = orderListEvent.getOrderInfos();
            HandlerHelper.post(new HandlerHelper.onRun() {
                @Override
                public void run() {
                    if(orderInfos !=null && orderInfos.size()>0){
                        orderListAdapter.refreshData(orderInfos);
                        listvOrder.setVisibility(View.VISIBLE);
                        layoutEmpty.setVisibility(View.GONE);
                    }else{
                        layoutEmpty.setVisibility(View.VISIBLE);
                        listvOrder.setVisibility(View.GONE);
                    }
                    mRefreshLayout.endRefreshing();
                }
            });
        }
    }
}
