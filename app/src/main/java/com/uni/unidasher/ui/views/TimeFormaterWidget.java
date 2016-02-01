package com.uni.unidasher.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uni.unidasher.R;

/**
 * Created by Administrator on 2015/6/16.
 */
public class TimeFormaterWidget extends LinearLayout {

    private ImageView imgvStatus;
    private TextView txtvDes,txtvTime;
    public TimeFormaterWidget(Context context) {
        super(context);
        ViewGroup.LayoutParams params = new LayoutParams(330, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        View view = LayoutInflater.from(context).inflate(R.layout.view_time_formater, this, true);
        init(view);
    }
    public TimeFormaterWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
//        ViewGroup.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        setLayoutParams(params);
//        View view = LayoutInflater.from(context).inflate(R.layout.view_time_formater, this, true);
//        init(view);
    }

    public TimeFormaterWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(View view){
        imgvStatus = (ImageView)view.findViewById(R.id.imgvStatus);
        txtvDes = (TextView)view.findViewById(R.id.txtvDes);
        txtvTime = (TextView)view.findViewById(R.id.txtvTime);
    }

    public void setStatus(String description, String time, boolean isNew){
//        if(((String)imgvStatus.getTag()).equals(tag)){
            if(isNew){
                imgvStatus.setImageResource(R.mipmap.current_time_formater);
            }else{
                imgvStatus.setImageResource(R.mipmap.before_time_formater);
                txtvDes.setTextColor(getResources().getColor(R.color.secondTextColor));
                txtvTime.setTextColor(getResources().getColor(R.color.secondTextColor));
            }
            txtvDes.setText(description);
            txtvTime.setText(time);

//        }
    }

    public void setImgTag(String tag){
        imgvStatus.setTag(tag);
    }

}
