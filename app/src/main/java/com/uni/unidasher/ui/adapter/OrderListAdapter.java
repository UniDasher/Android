package com.uni.unidasher.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uni.unidasher.R;
import com.uni.unidasher.data.entity.LoginInfo;
import com.uni.unidasher.data.entity.OrderInfo;
import com.uni.unidasher.data.status.OrderDetailStatus;
import com.uni.unidasher.data.status.Status;
import com.uni.unidasher.listener.OnRecyclerViewItemClickListener;
import com.uni.unidasher.ui.views.RightAlignedHorizontalScrollView;
import com.uni.unidasher.ui.views.TimeFormaterView;
import com.uni.unidasher.ui.views.TimeFormaterWidget;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/6/12.
 */
public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private Context mContext;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    ArrayList<OrderInfo> orderInfos = new ArrayList<>();

    public OrderListAdapter(Context context){
        mContext = context;
    }


    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener){
        this.onRecyclerViewItemClickListener = listener;
    }

    public void deleteOrder(int position){
        orderInfos.remove(position);
        notifyItemRemoved(position);
    }

    public void refreshData(ArrayList<OrderInfo> ls){
        if(ls == null){
            orderInfos = new ArrayList<>();
        }else {
            orderInfos = ls;
        }
        notifyDataSetChanged();
    }

    public Object getItem(int position){
        return orderInfos.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_listview_order_list, viewGroup, false);
        return new ViewHolder(v,onRecyclerViewItemClickListener,i);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        OrderInfo orderInfo = orderInfos.get(position);
        viewHolder.txtvOrderNum.setText(orderInfo.getMid());
        viewHolder.txtvShopName.setText(orderInfo.getShopName());
        viewHolder.txtvStatusTxt.setText(Status.Order.getStatusText(orderInfo.getStatus()));
        viewHolder.layoutTime.removeAllViews();
        LinkedHashMap<String,String> timeLineMap = new LinkedHashMap();
        int status = orderInfo.getStatus();
        if(status == Status.Order.Status_Order_Pre_Order){
            timeLineMap.put(Status.Order.getTimeFormaterText(1),orderInfo.getCreateDate().substring(5,16));
        }else if(status == Status.Order.Status_Order_Receieve){
            timeLineMap.put(Status.Order.getTimeFormaterText(1),orderInfo.getCreateDate().substring(5,16));
            timeLineMap.put(Status.Order.getTimeFormaterText(2),orderInfo.getStartDate().substring(5,16));
        }else if(status == Status.Order.Status_Order_Cancel){
            timeLineMap.put(Status.Order.getTimeFormaterText(1),orderInfo.getCreateDate().substring(5,16));
            timeLineMap.put(Status.Order.getTimeFormaterText(4),orderInfo.getCancleDate().substring(5,16));
        }else if(status == Status.Order.Status_Order_OverTime){
            timeLineMap.put(Status.Order.getTimeFormaterText(1),orderInfo.getCreateDate().substring(5,16));
            timeLineMap.put(Status.Order.getTimeFormaterText(6),orderInfo.getOverTimeDate().substring(5,16));
        }else if(status == Status.Order.Status_Order_Complain){
            timeLineMap.put(Status.Order.getTimeFormaterText(1),orderInfo.getCreateDate().substring(5,16));
            timeLineMap.put(Status.Order.getTimeFormaterText(2),orderInfo.getStartDate().substring(5,16));
            timeLineMap.put(Status.Order.getTimeFormaterText(5),orderInfo.getComplainDate().substring(5,16));
        }else if(status == Status.Order.Status_Order_Complain_Completed){
            timeLineMap.put(Status.Order.getTimeFormaterText(1),orderInfo.getCreateDate().substring(5,16));
            timeLineMap.put(Status.Order.getTimeFormaterText(2),orderInfo.getStartDate().substring(5,16));
            timeLineMap.put(Status.Order.getTimeFormaterText(5),orderInfo.getComplainDate().substring(5,16));
            timeLineMap.put(Status.Order.getTimeFormaterText(8),orderInfo.getEndDate().substring(5,16));
        }else if(status == Status.Order.Status_Order_OverTime_Completed){
            timeLineMap.put(Status.Order.getTimeFormaterText(1),orderInfo.getCreateDate().substring(5,16));
            timeLineMap.put(Status.Order.getTimeFormaterText(6),orderInfo.getOverTimeDate().substring(5,16));
            timeLineMap.put(Status.Order.getTimeFormaterText(9),orderInfo.getEndDate().substring(5,16));
        }else if(status == Status.Order.Status_Order_Completed){
            timeLineMap.put(Status.Order.getTimeFormaterText(1),orderInfo.getCreateDate().substring(5,16));
            timeLineMap.put(Status.Order.getTimeFormaterText(2),orderInfo.getStartDate().substring(5,16));
            timeLineMap.put(Status.Order.getTimeFormaterText(3),orderInfo.getEndDate().substring(5,16));
        }else if(status == Status.Order.Status_Order_Cancel_Completed){
            timeLineMap.put(Status.Order.getTimeFormaterText(1),orderInfo.getCreateDate().substring(5,16));
            timeLineMap.put(Status.Order.getTimeFormaterText(4),orderInfo.getCancleDate().substring(5,16));
            timeLineMap.put(Status.Order.getTimeFormaterText(7),orderInfo.getEndDate().substring(5,16));
        }
        if(timeLineMap!=null){
            TimeFormaterWidget t = null;
            Iterator<Map.Entry<String, String>> it = timeLineMap.entrySet().iterator();
            int count = 0;
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                t = new TimeFormaterWidget(mContext);
                t.setStatus(entry.getKey(),entry.getValue(),count == (timeLineMap.entrySet().size()-1)?true:false);
                viewHolder.layoutTime.addView(t);
                count++;
            }
        }
        viewHolder.itemView.setTag(orderInfo);
    }

    @Override
    public int getItemCount() {
        return orderInfos.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnRecyclerViewItemClickListener listener;
        RightAlignedHorizontalScrollView scrollerView;
        LinearLayout layoutTime;
        TextView txtvOrderNum,txtvShopName,txtvStatusTxt;

        public ViewHolder(View itemView,OnRecyclerViewItemClickListener listener,int position) {
            super(itemView);
            this.listener = listener;
            scrollerView = (RightAlignedHorizontalScrollView)itemView.findViewById(R.id.scrollerView);
            layoutTime = (LinearLayout)itemView.findViewById(R.id.layoutTime);
            txtvOrderNum = (TextView)itemView.findViewById(R.id.txtvOrderNum);
            txtvShopName = (TextView)itemView.findViewById(R.id.txtvShopName);
            txtvStatusTxt = (TextView)itemView.findViewById(R.id.txtvStatusTxt);
            itemView.setOnClickListener(this);
        }
        /**
         * 点击监听
         */
        @Override
        public void onClick(View v) {
            if(listener != null){
                listener.onItemClickListener(v,getPosition());
            }
        }

    }

}
