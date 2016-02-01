package com.uni.unidasher.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uni.unidasher.R;
import com.uni.unidasher.data.datamodel.databaseAndApi.DishInfo;
import com.uni.unidasher.listener.OnTakeDishListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/6/9.
 */
public class OrderMenuListAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<DishInfo> dishInfos = new ArrayList<>();
    private OnTakeDishListener onTakeDishListener;
    private boolean isShowAdd;

    public OrderMenuListAdapter(Context context,boolean isShowAdd) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.isShowAdd = isShowAdd;
    }

    public void refreshData(List<DishInfo> dishInfos){
        if(dishInfos == null){
            this.dishInfos = new ArrayList<>();
        }else {
            this.dishInfos = dishInfos;
        }
        notifyDataSetChanged();
    }

    public boolean add(int position){
        if(position<dishInfos.size()){
            DishInfo dishInfo = dishInfos.get(position);
            int count = dishInfo.getCount();
            count++;
            dishInfo.setCount(count);
            notifyDataSetChanged();
            return true;
        }else{
            return false;
        }
    }

    public void setOnTakeDishListener(OnTakeDishListener onTakeDishListener){
        this.onTakeDishListener = onTakeDishListener;
    }

    public boolean remove(int position){
        if(position<dishInfos.size()){
            DishInfo dishInfo = dishInfos.get(position);
            int count = dishInfo.getCount();
            if(count>0){
                count--;
                dishInfo.setCount(count);
                if(count == 0){
                    dishInfos.remove(position);
                }
                notifyDataSetChanged();
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }

    }

    @Override
    public int getCount() {
        return dishInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return dishInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.adapter_listview_order_menu_list,parent,false);
        }
        ViewHolder viewHolder = (ViewHolder)convertView.getTag();
        if(viewHolder==null){
            viewHolder = new ViewHolder();
            viewHolder.txtvDishName = (TextView)convertView.findViewById(R.id.txtvDishName);
            viewHolder.txtvDishPrice = (TextView)convertView.findViewById(R.id.txtvDishPrice);
            viewHolder.txtvDishCount = (TextView)convertView.findViewById(R.id.txtvDishCount);
            viewHolder.txtvAdd = (TextView)convertView.findViewById(R.id.txtvAdd);
            viewHolder.txtvRemove = (TextView)convertView.findViewById(R.id.txtvRemove);
            convertView.setTag(viewHolder);
        }
        DishInfo dishInfo = dishInfos.get(position);
        viewHolder.txtvDishName.setText(dishInfo.getDishName());
        viewHolder.txtvDishCount.setText(dishInfo.getCount()+"");
        viewHolder.txtvDishPrice.setText(dishInfo.getPrice()+"");
        if(!isShowAdd){
            viewHolder.txtvAdd.setVisibility(View.INVISIBLE);
            viewHolder.txtvRemove.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.txtvAdd.setVisibility(View.VISIBLE);
            viewHolder.txtvRemove.setVisibility(View.VISIBLE);
            viewHolder.txtvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onTakeDishListener!=null){
                        onTakeDishListener.onAdd(position);
                    }
                }
            });
            viewHolder.txtvRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onTakeDishListener!=null){
                        onTakeDishListener.onRemove(position);
                    }
                }
            });
        }

        return convertView;
    }

    class ViewHolder{
        TextView txtvDishName;
        TextView txtvDishPrice;
        TextView txtvDishCount;
        TextView txtvAdd;
        TextView txtvRemove;
    }
}
