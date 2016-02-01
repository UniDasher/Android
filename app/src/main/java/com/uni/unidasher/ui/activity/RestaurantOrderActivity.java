package com.uni.unidasher.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.uni.unidasher.BuildConfig;
import com.uni.unidasher.R;
import com.uni.unidasher.data.DataProvider;
import com.uni.unidasher.data.datamodel.databaseAndApi.ShopInfo;
import com.uni.unidasher.data.entity.OrderInfo;
import com.uni.unidasher.data.event.ReShopOrderListEvent;
import com.uni.unidasher.data.rest.RESTManager;
import com.uni.unidasher.data.status.Status;
import com.uni.unidasher.ui.dialog.AlertPrompt;
import com.uni.unidasher.ui.utils.Alert;
import com.uni.unidasher.ui.utils.Extras;
import com.uni.unidasher.ui.utils.HandlerHelper;

import java.util.ArrayList;

public class RestaurantOrderActivity extends EventBusActivity {
    private TextView txtvBack,txtvNavTitle;
    private ListView listvRestaurantOrder;
    private LayoutInflater inflater;
    private ResOrderAdapter adapter;
    private ShopInfo shopInfo;
    private TextView txtvShopName,txtvShopAddress;
    private DataProvider mDataProvider;
    private int CurrentItem;
    private LinearLayout layoutEmpty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_order);
        mDataProvider = DataProvider.getInstance(this);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null&&bundle.containsKey(Extras.Extra_ShopInfo)){
            shopInfo = (ShopInfo)bundle.getSerializable(Extras.Extra_ShopInfo);
        }
        findViews();
        setViews();
        setClickListeners();
        mDataProvider.retrieveShopOrderList(shopInfo.getSid());
    }

    private void findViews(){
        inflater = LayoutInflater.from(this);
        txtvBack = (TextView)findViewById(R.id.txtvBack);
        txtvNavTitle = (TextView)findViewById(R.id.txtvNavTitle);
        txtvNavTitle.setText("餐厅订单");
        txtvShopAddress = (TextView)findViewById(R.id.txtvShopAddress);
        txtvShopName = (TextView)findViewById(R.id.txtvShopName);
        layoutEmpty = (LinearLayout)findViewById(R.id.layoutEmpty);

        listvRestaurantOrder = (ListView)findViewById(R.id.listvRestaurantOrder);
        adapter = new ResOrderAdapter();
        listvRestaurantOrder.setAdapter(adapter);
    }

    private void setViews(){
        txtvShopName.setText(shopInfo.getName());
        txtvShopAddress.setText(shopInfo.getAddress());
    }

    private void setClickListeners(){
        listvRestaurantOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CurrentItem = position;
                OrderInfo orderInfo = (OrderInfo)adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Extras.Extra_Order_Detail_Info_Page, orderInfo);
                bundle.putBoolean(Extras.Extra_Is_Customer,false);
                startActivityForResult(new Intent(RestaurantOrderActivity.this, OrderDetailActivity.class).putExtras(bundle), Extras.Request_Code_Rob_Order);
//                startActivity(new Intent(RestaurantOrderActivity.this,OrderDetailActivity.class).putExtra(OrderDetailActivity.Status, OrderDetailStatus.Status_Get_Order_None));
            }
        });
        txtvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 接单
     * @param orderNum
     * @param position
     */
    public void receiveOrder(String orderNum,final int position){
        mDataProvider.receiveOrder(orderNum,new RESTManager.OnObjectDownloadedListener() {
            @Override
            public void onObjectDownloaded(final boolean success, String resultStr,final String tipStr) {
                if (BuildConfig.DEBUG) {
                    Log.i("receiveOrder:", success + "" + "---" + resultStr);
                }
                HandlerHelper.post(new HandlerHelper.onRun() {
                    @Override
                    public void run() {
                        if (success) {
                            adapter.deleteOrder(position);
                            Alert.showDefaultToast(RestaurantOrderActivity.this, "接单成功，请尽快配送!");
//                            Alert.showTip(RestaurantOrderActivity.this, "接单", "接单成功，请尽快配送！", true,null);
                        } else {
                            String errorMsg = "接单失败!";
                            if(!TextUtils.isEmpty(tipStr)){
                                errorMsg = tipStr;
                            }
                            Alert.showDefaultToast(RestaurantOrderActivity.this, errorMsg);
                        }
                    }
                });

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(resultCode){
            case Extras.Request_Code_Rob_Order:
                if(data == null){
                    return;
                }
                Bundle bundle = data.getExtras();
                if(bundle!=null){
                    boolean isUpdateData = bundle.getBoolean(Extras.Extra_Rob_Order_Flag,false);
                    if(isUpdateData){
                        adapter.deleteOrder(CurrentItem);
                    }
                    if(adapter.getCount()==0){
                        layoutEmpty.setVisibility(View.VISIBLE);
                        listvRestaurantOrder.setVisibility(View.GONE);
                    }
                }
                break;
        }
    }

    @Subscribe
    public void onShopOrderListEvent(final ReShopOrderListEvent reShopOrderListEvent){
        if(reShopOrderListEvent!=null){
            HandlerHelper.post(new HandlerHelper.onRun() {
                @Override
                public void run() {
                    if(reShopOrderListEvent.getOrderInfos()!=null&&reShopOrderListEvent.getOrderInfos().size()>0){
                        layoutEmpty.setVisibility(View.GONE);
                        listvRestaurantOrder.setVisibility(View.VISIBLE);
                        adapter.refreshData(reShopOrderListEvent.getOrderInfos());
                    }else{
                        layoutEmpty.setVisibility(View.VISIBLE);
                        listvRestaurantOrder.setVisibility(View.GONE);
                    }

                }
            });
        }
    }


    class ResOrderAdapter extends BaseAdapter{
        private ArrayList<OrderInfo> orderInfos = new ArrayList<>();

        public void refreshData(ArrayList<OrderInfo> ls){
            if(ls == null){
                orderInfos = new ArrayList<>();
            }else{
                orderInfos = ls;
            }
            notifyDataSetChanged();
        }

        public void deleteOrder(int position){
            if(orderInfos!=null&&position<orderInfos.size()){
                orderInfos.remove(position);
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return orderInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return orderInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            TextView txtvUserName,txtvTotalPrice,txtvRob,txtvDistance;
            ImageView imgvDirection;
            if(convertView == null){
                convertView = inflater.inflate(R.layout.adapter_listview_restaurant_order,parent,false);
            }
            txtvUserName = (TextView)convertView.findViewById(R.id.txtvUserName);
            txtvTotalPrice = (TextView)convertView.findViewById(R.id.txtvTotalPrice);
            txtvDistance = (TextView)convertView.findViewById(R.id.txtvDistance);
            txtvRob = (TextView)convertView.findViewById(R.id.txtvRob);
            imgvDirection = (ImageView)convertView.findViewById(R.id.imgvDirection);

            final OrderInfo orderInfo = orderInfos.get(position);
            txtvUserName.setText(orderInfo.getUserName());
            txtvTotalPrice.setText((orderInfo.getDishsMoney()+orderInfo.getCarriageMoney())+"");
            txtvDistance.setText((int)Math.floor(Double.parseDouble(orderInfo.getDistance()))+"");
            imgvDirection.getDrawable().setLevel(Status.Order.getImgLevel(orderInfo.getDirection()));
            txtvRob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    receiveOrder(orderInfo.getMid(),position);
                }
            });
            return convertView;
        }
    }
}
