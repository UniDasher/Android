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
import android.widget.TextView;

import com.uni.unidasher.R;
import com.uni.unidasher.ui.widget.wheel.AbstractWheelTextAdapter;
import com.uni.unidasher.ui.widget.wheel.NumericWheelAdapter;
import com.uni.unidasher.ui.widget.wheel.OnWheelScrollListener;
import com.uni.unidasher.ui.widget.wheel.WheelView;
import com.uni.unidasher.ui.widget.wheel.WheelViewAdapter;
import com.uni.wheelpickerlibrary.adapter.ArrayWheelAdapter;
import com.uni.wheelpickerlibrary.view.AbstractWheel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/7/14.
 */
public class DatePicker extends Dialog {
    private Context mContext;
    private int selectYear,selectDay,selectMonth;
    private boolean yearScrolling = false;
    WheelView wheelYear,wheelMonth,wheelDay;
    NumericWheelAdapter yearAdapter,monthAdapter,dayAdapter;
    OnDateSelectListener onDateSelectListener;
    private String scrollYear,scrollMonth;

    public DatePicker(Context context,int year,int month,int day,OnDateSelectListener onDateSelectListener){
        super(context);
        mContext = context;
        selectYear = year;
        selectMonth = month;
        selectDay = day;
        this.onDateSelectListener = onDateSelectListener;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(R.layout.alert_date_picker, null);
        setContentView(view);
        findViews(view);
        Window window = this.getWindow();
        WindowManager m = ((Activity)context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.65
//        p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的0.6
        window.setAttributes(p);
    }

    private void findViews(View view){
        TextView txtvTitle = (TextView)view.findViewById(R.id.txtvTitle);
        txtvTitle.setText("日期查询");
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTimeInMillis(date.getTime());
//        calendar.add(Calendar.DAY_OF_MONTH, -1);
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH)+1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        wheelYear = (WheelView) findViewById(R.id.wheelYear);
        yearAdapter = new NumericWheelAdapter(mContext, 2000, currentYear, "%d");
        wheelYear.setViewAdapter(yearAdapter);
        wheelYear.setCurrentItem(selectYear - 2000);
        int dataMonth = 12;
        if(currentYear == selectYear){
            dataMonth = currentMonth;
        }

        wheelMonth = (WheelView) findViewById(R.id.wheelMonth);
        monthAdapter = new NumericWheelAdapter(mContext, 1, dataMonth, "%02d");
        wheelMonth.setViewAdapter(monthAdapter);
        wheelMonth.setCurrentItem(selectMonth - 1);

        int dataDay = days(selectYear, selectMonth);
        if(currentYear == selectYear&&selectMonth == currentMonth){
            dataDay = currentDay;
        }
        wheelDay = (WheelView) findViewById(R.id.wheelDay);
        dayAdapter = new NumericWheelAdapter(mContext, 1, dataDay, "%02d");
        wheelDay.setViewAdapter(dayAdapter);
        wheelDay.setCurrentItem(selectDay - 1);

        TextView txtvCancel = (TextView)view.findViewById(R.id.txtvCancel);
        TextView txtvConfirm = (TextView)view.findViewById(R.id.txtvConfirm);
        txtvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        txtvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onDateSelectListener!=null){
                    String year = yearAdapter.getItemText(wheelYear.getCurrentItem()).toString();
                    String month = monthAdapter.getItemText(wheelMonth.getCurrentItem()).toString();
                    String day = dayAdapter.getItemText(wheelDay.getCurrentItem()).toString();
                    onDateSelectListener.onSelect(year,month,day);
                    dismiss();
                }
            }
        });

        wheelYear.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
                yearScrolling = true;
                scrollYear = yearAdapter.getItemText(wheelYear.getCurrentItem()).toString();
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                yearScrolling = false;
                if(!scrollYear.equals(yearAdapter.getItemText(wheelYear.getCurrentItem()).toString())){
                    yearScroll();
                }

            }
        });

        wheelMonth.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
                scrollMonth = monthAdapter.getItemText(wheelMonth.getCurrentItem()).toString();
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                if(!scrollMonth.equals(monthAdapter.getItemText(wheelMonth.getCurrentItem()).toString())){
                    monthScroll();
                }
            }
        });
    }

    public void yearScroll(){
        int year = Integer.parseInt(yearAdapter.getItemText(wheelYear.getCurrentItem()).toString());
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTimeInMillis(date.getTime());
//        calendar.add(Calendar.DAY_OF_MONTH, -1);
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH)+1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int dataMonth = 12;
        if(year == currentYear){
            dataMonth = currentMonth;
        }
        monthAdapter = new NumericWheelAdapter(mContext, 1, dataMonth, "%02d");
        wheelMonth.setViewAdapter(monthAdapter);
        wheelMonth.setCurrentItem(0);
        int dataDay = days(year, 1);
        if(currentYear == year&&1 == currentMonth){
            dataDay = 1;
        }
        dayAdapter = new NumericWheelAdapter(mContext, 1, dataDay, "%02d");
        wheelDay.setViewAdapter(dayAdapter);
        wheelDay.setCurrentItem(0);
    }

    public void monthScroll(){
        if(!yearScrolling){
            int year = Integer.parseInt(yearAdapter.getItemText(wheelYear.getCurrentItem()).toString());
            int month = Integer.parseInt(monthAdapter.getItemText(wheelMonth.getCurrentItem()).toString());
            Calendar calendar = Calendar.getInstance();
            Date date = new Date();
            calendar.setTimeInMillis(date.getTime());
//            calendar.add(Calendar.DAY_OF_MONTH, -1);
            int currentYear = calendar.get(Calendar.YEAR);
            int currentMonth = calendar.get(Calendar.MONTH)+1;
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

            int dataDay = days(year, month);
            if(year == currentYear&&month == currentMonth){
                dataDay = currentDay;
            }
            dayAdapter = new NumericWheelAdapter(mContext, 1, dataDay, "%02d");
            wheelDay.setViewAdapter(dayAdapter);
            wheelDay.setCurrentItem(0);
        }
    }



    public DatePicker(Context context) {
        super(context);
    }

    public DatePicker(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public DatePicker(Context context, int theme) {
        super(context, theme);
    }

    public static int days(int year, int month) {
        int days = 0;
        if (month != 2) {
            switch (month) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    days = 31;
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    days = 30;

            }
        } else {
            //闰年
            if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0)
                days = 29;
            else
                days = 28;

        }
        return days;

    }

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

    public interface OnDateSelectListener{
        void onSelect(String year,String month,String day);
    }
}
