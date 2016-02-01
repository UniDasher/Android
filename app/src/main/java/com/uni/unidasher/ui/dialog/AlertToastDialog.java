package com.uni.unidasher.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.uni.unidasher.R;

/**
 * Created by Administrator on 2015/7/20.
 */
public class AlertToastDialog extends Dialog {
    public AlertToastDialog(Context context,String title,String content){
        super(context);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_alert_toast,null);
        TextView txtvTitle = (TextView)view.findViewById(R.id.txtvTitle);
        txtvTitle.setText(title);
        TextView txtvContent = (TextView)view.findViewById(R.id.txtvContent);
        txtvContent.setText(content);
        setContentView(view);
        Window window = this.getWindow();
        WindowManager m = ((Activity)context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65
//        p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的0.6
        window.setAttributes(p);
    }

    public AlertToastDialog(Context context) {
        super(context);
    }

    public AlertToastDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public AlertToastDialog(Context context, int theme) {
        super(context, theme);
    }
}
