package com.uni.unidasher.ui.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.uni.unidasher.AppConstants;
import com.uni.unidasher.R;
import com.uni.unidasher.data.datamodel.databaseAndApi.ShopInfo;
import com.uni.unidasher.data.entity.LoginInfo;
import com.uni.unidasher.listener.OnRecyclerViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/6/16.
 */
public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.ViewHolder>{
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    final ImageLoader imageLoader = ImageLoader.getInstance();
    ArrayList<ShopInfo> shopInfos = new ArrayList<>();

    public RestaurantListAdapter(){
    }

    public void refreshData(ArrayList<ShopInfo> ls){
        if(ls == null){
            shopInfos = new ArrayList<>();
        }else {
            shopInfos = ls;
        }
        notifyDataSetChanged();
    }

    public ArrayList<ShopInfo> getCurrentData(){
        return shopInfos;
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener){
        this.onRecyclerViewItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_listview_restaurant_list, viewGroup, false);
        return new ViewHolder(v,onRecyclerViewItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ShopInfo shopInfo = shopInfos.get(i);
        viewHolder.txtvShopName.setText(shopInfo.getName());
        viewHolder.txtvShopAddress.setText(shopInfo.getAddress());
        viewHolder.txtvShopType.setText(shopInfo.getTypeTab());
        viewHolder.txtvGood.setText(shopInfo.getGoodEvaluate()+"");
        viewHolder.txtvBad.setText(shopInfo.getBadEvaluate()+"");
        viewHolder.itemView.setTag(shopInfo);
        imageLoader.displayImage(AppConstants.HOST+shopInfo.getLogo(),
                viewHolder.imgvShopIcon,
                new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).resetViewBeforeLoading(true).build());
    }

    @Override
    public int getItemCount() {
        return shopInfos.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public Object getItem(int position){
        return shopInfos.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnRecyclerViewItemClickListener listener;
        ImageView imgvShopIcon;
        TextView txtvShopName,txtvShopType,txtvShopAddress,txtvGood,txtvBad;
        public ViewHolder(View itemView,OnRecyclerViewItemClickListener listener) {
            super(itemView);
            this.listener = listener;
            itemView.setOnClickListener(this);
            imgvShopIcon = (ImageView)itemView.findViewById(R.id.imgvShopIcon);
            txtvShopName = (TextView)itemView.findViewById(R.id.txtvShopName);
            txtvShopType = (TextView)itemView.findViewById(R.id.txtvShopType);
            txtvShopAddress = (TextView)itemView.findViewById(R.id.txtvShopAddress);
            txtvGood = (TextView)itemView.findViewById(R.id.txtvGood);
            txtvBad = (TextView)itemView.findViewById(R.id.txtvBad);
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
