package com.uni.unidasher.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.uni.unidasher.R;
import com.uni.unidasher.ui.widget.wheel.AbstractWheelTextAdapter;
import com.uni.unidasher.ui.widget.wheel.WheelView;
import com.uni.wheelpickerlibrary.adapter.ArrayWheelAdapter;
import com.uni.wheelpickerlibrary.view.AbstractWheel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/6/10.
 */
public class TimeWheelDialog  extends Dialog {
    private Context mContext;
    private WheelView wheelViewStart,wheelViewEnd;
    private WheelAdapter startAdapter,endAdapter;
    private AbstractWheel wheelTime;

    public TimeWheelDialog(Context context,String str){
        super(context);
        mContext = context;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_time_wheel, null);
        findViews(view);
        setContentView(view);

        Window window = this.getWindow();
        WindowManager m = ((Activity)context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.65
//        p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的0.6
        window.setAttributes(p);

//        this.setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        this.setContentView(view);
//        this.setOutsideTouchable(true);
//        this.setFocusable(true);
    }

    private void findViews(View view){
        wheelViewStart = (WheelView)view.findViewById(R.id.wheelStart);
        wheelViewEnd = (WheelView)view.findViewById(R.id.wheelEnd);

        wheelViewStart.setVisibleItems(3); // Number of items
        wheelViewStart.setWheelBackground(R.drawable.wheel_bg_holo);
        wheelViewStart.setWheelForeground(R.drawable.wheel_val_holo);
        wheelViewStart.setShadowColor(0xFFffffff, 0x88ffffff, 0x88ffffff);

        wheelViewEnd.setVisibleItems(3); // Number of items
        wheelViewEnd.setWheelBackground(R.drawable.wheel_bg_holo);
        wheelViewEnd.setWheelForeground(R.drawable.wheel_val_holo);
        wheelViewEnd.setShadowColor(0xFFffffff, 0x88ffffff, 0x88ffffff);

        wheelTime = (AbstractWheel)view.findViewById(R.id.wheelTime);
        ArrayWheelAdapter<String> ampmAdapter =
                new ArrayWheelAdapter<String>(mContext, new String[] {"01", "02", "03"});
        ampmAdapter.setItemResource(R.layout.wheel_text_centered);
        ampmAdapter.setItemTextResource(R.id.text);
        wheelTime.setViewAdapter(ampmAdapter);

        startAdapter = new WheelAdapter();
        List<String> data = new ArrayList<>();
        data.add("1");
        data.add("2");
        data.add("3");
        data.add("4");
        data.add("5");
        data.add("6");
        data.add("7");
        data.add("8");
        data.add("9");
        startAdapter.setWheelData(data);
        wheelViewStart.setViewAdapter(startAdapter);
        wheelViewEnd.setViewAdapter(startAdapter);
    }

//    public void show(View view){
//        this.showAtLocation(view, Gravity.CENTER,0,0);
//    }


    private class WheelAdapter extends AbstractWheelTextAdapter {
        // City names
        List<String> data;
        /**
         * Constructor
         */
        protected WheelAdapter() {

            super(mContext, R.layout.wheel_data_layout, NO_RESOURCE);

            setItemTextResource(R.id.wheel_name);
        }

        public void setWheelData(List<String> data){
            if(data != null&&data.size()>0){
                this.data = data;
            }else{
                this.data = new ArrayList<String>();
            }
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return data.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return data.get(index);
        }

    }

    public TimeWheelDialog(Context context) {
        super(context);
    }

    public TimeWheelDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public TimeWheelDialog(Context context, int theme) {
        super(context, theme);
    }
}
