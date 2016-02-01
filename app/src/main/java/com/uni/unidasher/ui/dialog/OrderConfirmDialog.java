package com.uni.unidasher.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.uni.unidasher.R;
import com.uni.unidasher.data.datamodel.databaseAndApi.UserAdressInfo;
import com.uni.unidasher.data.entity.UserInfo;
import com.uni.wheelpickerlibrary.adapter.ArrayWheelAdapter;
import com.uni.wheelpickerlibrary.view.AbstractWheel;

import java.util.List;

/**
 * Created by Administrator on 2015/6/10.
 */
public class OrderConfirmDialog extends Dialog {

    private OnConfirmListener onConfirmListener;
    private Context mContext;
    private TextView txtvConfirm;
    private AbstractWheel wheelTime;
    private UserAdressInfo placeOrderUserInfo;
    private EditText editOrderName,editCallPhoneNum,editSendAddress;
    private UserInfo userInfo;
    private String times[];
    private ArrayWheelAdapter<String> adapter;

    public OrderConfirmDialog(Context context,UserInfo userInfo,UserAdressInfo placeOrderUserInfo,List<String> times,final OnConfirmListener onConfirmListener){
        super(context);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = context;
        this.onConfirmListener = onConfirmListener;
        this.placeOrderUserInfo = placeOrderUserInfo;
        this.userInfo = userInfo;
        this.times = (String[])times.toArray(new String[times.size()]);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_order_confirm,null);
        findViews(view);
        setViews();
        setContentView(view);
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
        txtvTitle.setText("订单确认");
        editOrderName = (EditText)view.findViewById(R.id.editOrderName);
        editCallPhoneNum = (EditText)view.findViewById(R.id.editCallPhoneNum);
        editSendAddress = (EditText)view.findViewById(R.id.editSendAddress);
        txtvConfirm = (TextView)view.findViewById(R.id.txtvConfirm);
        txtvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(onConfirmListener!=null){
                    boolean isOverTime = false;
                    int currentItem = wheelTime.getCurrentItem();
                    String sendTime = String.valueOf(adapter.getItemText(currentItem));
                    if(currentItem == (adapter.getItemsCount()-1)){
                        if(sendTime.equals("24:00")){
                            isOverTime = true;
                        }else{
                            isOverTime = false;
                        }
                    }else{
                        isOverTime = true;
                    }
                    onConfirmListener.onConfirm(sendTime,isOverTime);
                }
            }
        });

        wheelTime = (AbstractWheel)view.findViewById(R.id.wheelTime);
        adapter =
                new ArrayWheelAdapter<String>(mContext, times);
        adapter.setItemResource(R.layout.wheel_text_centered);
        adapter.setItemTextResource(R.id.text);
        wheelTime.setViewAdapter(adapter);
    }

    private void setViews(){
        if(placeOrderUserInfo==null)
            return;
        editSendAddress.setText(placeOrderUserInfo.getAddress());
        if(userInfo==null)
            return;
        editOrderName.setText(userInfo.getNickName());
        editCallPhoneNum.setText(userInfo.getMobilePhone());
    }


    public OrderConfirmDialog(Context context) {
        super(context);
    }

    public OrderConfirmDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public OrderConfirmDialog(Context context, int theme) {
        super(context, theme);
    }

    public interface OnConfirmListener{
        void onConfirm(String sendTime,boolean isOverTime);
    }
}
