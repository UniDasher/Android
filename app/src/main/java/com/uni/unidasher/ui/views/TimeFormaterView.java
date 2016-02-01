package com.uni.unidasher.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.uni.unidasher.R;

/**
 * Created by Administrator on 2015/6/13.
 */
public class TimeFormaterView extends LinearLayout {

    private TimeFormaterWidget[] timeFormaterWidget;
    public TimeFormaterView(Context context) {
        super(context);
    }

    public TimeFormaterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ViewGroup.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        View view = LayoutInflater.from(context).inflate(R.layout.view_time_formater_modify, this, true);
        initView(view);
    }
    public TimeFormaterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initView(View view){
        LinearLayout layoutTimer = (LinearLayout)view.findViewById(R.id.layoutTimer);
        int childNum = layoutTimer.getChildCount();
        timeFormaterWidget = new TimeFormaterWidget[childNum];
        for(int i = 0;i<childNum;i++){
            timeFormaterWidget[i] = (TimeFormaterWidget)layoutTimer.getChildAt(i);
            timeFormaterWidget[i].setVisibility(View.GONE);
        }
    }

    public void setResource(int position,int length){
//        for(int i = 0;i<length;i++){
//            timeFormaterWidget[i].setVisibility(View.VISIBLE);
//            timeFormaterWidget[i].setStatus(position+""+i,i==(length-1)?true:false);
//        }
    }

    public void setImgTag(int position){
        for(int i = 0;i<timeFormaterWidget.length;i++){
            timeFormaterWidget[i].setImgTag(position+""+i);
        }
    }
}
