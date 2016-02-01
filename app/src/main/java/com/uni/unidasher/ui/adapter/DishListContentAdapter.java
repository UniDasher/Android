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

/**
 * Created by Administrator on 2015/6/8.
 */
public class DishListContentAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<DishInfo> dishInfos = new ArrayList<>();
    private OnTakeDishListener onTakeDishListener;

    public DishListContentAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    public void refreshData(ArrayList<DishInfo> ls){
        if(ls == null){
            dishInfos = new ArrayList<>();
        }else{
            dishInfos = ls;
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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return dishInfos.get(position);
    }

    @Override
    public int getCount() {
        return dishInfos.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.adapter_listview_menu_list_content,parent,false);
        }
        ViewHolder viewHolder = (ViewHolder)convertView.getTag();
        if(viewHolder == null){
            viewHolder = new ViewHolder();
            viewHolder.txtvDishName = (TextView)convertView.findViewById(R.id.txtvDishName);
            viewHolder.txtvTaste = (TextView)convertView.findViewById(R.id.txtvTaste);
            viewHolder.txtvPrice = (TextView)convertView.findViewById(R.id.txtvPrice);
            viewHolder.txtvDescription = (TextView)convertView.findViewById(R.id.txtvDescription);
            viewHolder.txtvCount = (TextView)convertView.findViewById(R.id.txtvCount);
            viewHolder.txtvAdd = (TextView)convertView.findViewById(R.id.txtvAdd);
            viewHolder.txtvRemove = (TextView)convertView.findViewById(R.id.txtvRemove);
            convertView.setTag(viewHolder);
        }
        DishInfo dishInfo = dishInfos.get(position);
        viewHolder.txtvDishName.setText(dishInfo.getDishName());
        viewHolder.txtvTaste.setText(dishInfo.getTaste());
        viewHolder.txtvPrice.setText(dishInfo.getPrice()+"");
        viewHolder.txtvDescription.setText(dishInfo.getDescription());
        viewHolder.txtvCount.setText(dishInfo.getCount()+"");
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
        return convertView;
    }

    class ViewHolder{
        TextView txtvDishName;
        TextView txtvTaste;
        TextView txtvPrice;
        TextView txtvDescription;
        TextView txtvCount;
        TextView txtvAdd;
        TextView txtvRemove;
    }

}
