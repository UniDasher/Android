package com.uni.unidasher.ui.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.uni.unidasher.BuildConfig;
import com.uni.unidasher.R;
import com.uni.unidasher.data.DataProvider;
import com.uni.unidasher.data.datamodel.databaseAndApi.ShopInfo;
import com.uni.unidasher.data.entity.OrderInfo;
import com.uni.unidasher.data.entity.shop.ShopTypeInfo;
import com.uni.unidasher.data.rest.RESTManager;
import com.uni.unidasher.data.utils.Constants;
import com.uni.unidasher.ui.widget.GridViewForScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2015/6/17.
 */
public class ResFilterPopDialog extends PopupWindow {
    public static final String AllType = "全部";

    private TextView txtvBack,txtvConfirm;
    private GridViewForScrollView gridViewType;
    private LayoutInflater inflater;
    private TypeAdapter adapter;
    private TextView txtvEvaluationFilter,txtvDistanceFilter,txtvRes,txtvSuper,txtvType;
    private int currentType = -1;
    private DataProvider mDataProvider;
    private OnFilterListener onFilterListener;


    public ResFilterPopDialog(Context context,OnFilterListener onFilterListener){
        inflater = LayoutInflater.from(context);
        this.onFilterListener = onFilterListener;
        mDataProvider = DataProvider.getInstance(context);
        this.setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setAnimationStyle(R.style.AnimBottom);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_res_filter,null);
        findViews(view);
        setViews();
        setClickListener();
        this.setContentView(view);
        this.setOutsideTouchable(true);
        this.setFocusable(true);
        this.setTouchable(true);
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));
    }

    private void findViews(View view){
        txtvBack = (TextView)view.findViewById(R.id.txtvBack);
        txtvBack.setText("筛选条件");
        txtvConfirm = (TextView)view.findViewById(R.id.txtvConfirm);
        txtvEvaluationFilter = (TextView)view.findViewById(R.id.txtvEvaluationFilter);
        txtvDistanceFilter = (TextView)view.findViewById(R.id.txtvDistanceFilter);
        txtvRes = (TextView)view.findViewById(R.id.txtvRes);
        txtvSuper = (TextView)view.findViewById(R.id.txtvSuper);
        txtvType = (TextView)view.findViewById(R.id.txtvType);
        gridViewType = (GridViewForScrollView)view.findViewById(R.id.gridViewType);
        adapter = new TypeAdapter();
        gridViewType.setAdapter(adapter);

    }

    private void setViews(){
        txtvEvaluationFilter.setSelected(true);
        switchType(0);
        mDataProvider.retrieveShopTypeList(new RESTManager.OnObjectDownloadedListener() {
            @Override
            public void onObjectDownloaded(boolean success, String resultStr, String tipStr) {
                if (BuildConfig.DEBUG) {
                    Log.i("retrieveShopTypeList", success + "--->" + resultStr);
                }
                List<ShopTypeInfo> shopTypeInfoList = new ArrayList<>();
                shopTypeInfoList.add(new ShopTypeInfo(AllType));
                if (success) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultStr);
                        JSONArray dataArray = jsonObject.optJSONArray("list");
                        if (dataArray != null) {
                            for (int i = 0; i < dataArray.length(); i++) {
                                shopTypeInfoList.add(Constants.GSON_RECEIVED.fromJson(dataArray.optJSONObject(i).toString(), ShopTypeInfo.class));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.refreshData(shopTypeInfoList);
            }
        });
    }

    private void setClickListener(){
        txtvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        gridViewType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSeleted(position);
            }
        });
        txtvEvaluationFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchSort();
            }
        });
        txtvDistanceFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchSort();
            }
        });
        txtvRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchType(0);
            }
        });
        txtvSuper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchType(1);
            }
        });
        txtvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onFilterListener!=null){
                    onFilterListener.onFilter(txtvEvaluationFilter.isSelected()?0:1,adapter.getSelectedItem());
                }
                dismiss();
            }
        });
    }

    private void switchType(int which){
        if(currentType == which){
            return;
        }
        if(which == 0){
            txtvRes.setSelected(true);
            txtvSuper.setSelected(false);
            txtvType.setText("饭店类型");
        }else{
            txtvRes.setSelected(false);
            txtvSuper.setSelected(true);
            txtvType.setText("超市类型");
        }
        currentType = which;
    }

    private void switchSort(){
        if(txtvEvaluationFilter.isSelected()){
            txtvEvaluationFilter.setSelected(false);
            txtvDistanceFilter.setSelected(true);
        }else{
            txtvEvaluationFilter.setSelected(true);
            txtvDistanceFilter.setSelected(false);
        }
    }

    public void show(View showView){
        this.showAtLocation(showView,Gravity.CENTER,0,0);
    }

    class TypeAdapter extends BaseAdapter{
        private int currentItem = 0;
        List<ShopTypeInfo> shopTypeInfos = new ArrayList<>();

        public void setSeleted(int position){
            currentItem = position;
            notifyDataSetChanged();
        }

        public void refreshData(List<ShopTypeInfo> ls){
            if(ls == null){
                shopTypeInfos = new ArrayList<>();
            }else{
                shopTypeInfos = ls;
            }
            notifyDataSetChanged();
        }

        public String getSelectedItem(){
            if(currentItem<shopTypeInfos.size()){
                return shopTypeInfos.get(currentItem).getTypeName();
            }else{
                return AllType;
            }
        }

        @Override
        public int getCount() {
            return shopTypeInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return shopTypeInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView txtvType;
            if(convertView == null){
                convertView = inflater.inflate(R.layout.adapter_gridview_filter_type,parent,false);
            }
            txtvType = (TextView)convertView.findViewById(R.id.txtvType);
            ShopTypeInfo shopTypeInfo = shopTypeInfos.get(position);
            txtvType.setText(shopTypeInfo.getTypeName());
            if(position == currentItem){
                txtvType.setSelected(true);
            }else{
                txtvType.setSelected(false);
            }
            return convertView;
        }
    }

    public interface OnFilterListener{
        void onFilter(int filterType,String shopType);
    }


}
