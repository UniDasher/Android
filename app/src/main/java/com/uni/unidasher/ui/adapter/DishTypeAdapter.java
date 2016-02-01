package com.uni.unidasher.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uni.unidasher.R;
import com.uni.unidasher.data.entity.shop.TypeInfo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/6/8.
 */
public class DishTypeAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<TypeInfo> typeInfos = new ArrayList<>();
    private int currentItem = -1;

    public DishTypeAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    public void refreshData(ArrayList<TypeInfo> ls){
        if(ls == null){
            typeInfos = new ArrayList<>();
        }else{
            typeInfos = ls;
        }
        notifyDataSetChanged();
    }

    public void refreshState(int position){
        currentItem = position;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return typeInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return typeInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.adapter_listview_menu_type,parent,false);
        }
        ViewHolder viewHolder = (ViewHolder)convertView.getTag();
        if(viewHolder==null){
            viewHolder = new ViewHolder();
            viewHolder.txtvMenuType = (TextView)convertView.findViewById(R.id.txtvMenuType);
            convertView.setTag(viewHolder);
        }
        TypeInfo typeInfo = typeInfos.get(position);
        viewHolder.txtvMenuType.setText(typeInfo.getTypeName());
//        if(currentItem == position){
            convertView.setBackgroundColor(Color.WHITE);
//        }else{
//            convertView.setBackgroundColor(Color.TRANSPARENT);
//        }
        return convertView;
    }

    class ViewHolder{
        TextView txtvMenuType;
    }
}
