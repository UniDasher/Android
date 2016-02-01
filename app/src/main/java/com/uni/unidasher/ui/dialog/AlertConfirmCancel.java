package com.uni.unidasher.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.uni.unidasher.R;

/**
 * Created by Administrator on 2015/6/25.
 */
public class AlertConfirmCancel extends Dialog {

    public AlertConfirmCancel(Context context,String title,String content,OnAlertClickListener listener){
        super(context);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_alert_confirm_cancel,null);
        findViews(view,title,content,listener);
        setContentView(view);
        Window window = this.getWindow();
        WindowManager m = ((Activity)context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65
//        p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的0.6
        window.setAttributes(p);
    }

    private void findViews(View view,String title,String content, final OnAlertClickListener listener){
        TextView txtvTitle = (TextView)view.findViewById(R.id.txtvTitle);
        TextView txtvContent = (TextView)view.findViewById(R.id.txtvContent);
        TextView txtvCancel = (TextView)view.findViewById(R.id.txtvCancel);
        TextView txtvConfirm = (TextView)view.findViewById(R.id.txtvConfirm);
        if(TextUtils.isEmpty(title)){
            txtvTitle.setVisibility(View.GONE);
        }else{
            txtvTitle.setText(title);
        }
        txtvContent.setText(content);
        txtvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(listener!=null){
                    listener.onCancel();
                }
            }
        });
        txtvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(listener!=null){
                    listener.onConfirm();
                }
            }
        });
    }


    public AlertConfirmCancel(Context context) {
        super(context);
    }

    public AlertConfirmCancel(Context context, int theme) {
        super(context, theme);
    }

    public AlertConfirmCancel(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public interface OnAlertClickListener{
        void onConfirm();
        void onCancel();
    }
}
