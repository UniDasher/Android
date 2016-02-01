package com.uni.unidasher.ui.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uni.unidasher.R;
import com.uni.unidasher.data.datamodel.databaseAndApi.ShopInfo;

/**
 * Created by Administrator on 2015/6/3.
 */
public class MapInfoWindowView extends LinearLayout {

    public MapInfoWindowView(Context context,ShopInfo shopInfo){
        super(context);
        this.setBackgroundColor(Color.TRANSPARENT);
        View view = LayoutInflater.from(context).inflate(R.layout.map_infowindow_view,null);
        addView(view);
        TextView txtvShopName = (TextView)view.findViewById(R.id.txtvShopName);
        TextView txtvGood = (TextView)view.findViewById(R.id.txtvGood);
        TextView txtvBad = (TextView)view.findViewById(R.id.txtvBad);
        TextView txtvOrderCount = (TextView)view.findViewById(R.id.txtvOrderCount);
        TextView txtvShopType = (TextView)view.findViewById(R.id.txtvShopType);
        LinearLayout layoutEvaluation = (LinearLayout)view.findViewById(R.id.layoutEvaluation);
        txtvShopName.setText(shopInfo.getName());
        txtvGood.setText(shopInfo.getGoodEvaluate()+"");
        txtvBad.setText(shopInfo.getBadEvaluate()+"");
        txtvShopType.setText(shopInfo.getTypeTab());
        if(shopInfo.getOrderCount()>0){
            layoutEvaluation.setVisibility(View.GONE);
            txtvOrderCount.setVisibility(View.VISIBLE);
            txtvOrderCount.setText("订单数量:"+shopInfo.getOrderCount()+"");
        }else{
            layoutEvaluation.setVisibility(View.VISIBLE);
            txtvOrderCount.setVisibility(View.GONE);
        }
    }


    public MapInfoWindowView(Context context) {
        super(context);
    }

    public MapInfoWindowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapInfoWindowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
