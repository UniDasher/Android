package com.uni.unidasher.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.uni.unidasher.R;

/**
 * Created by Administrator on 2015/7/2.
 * 评价弹出框
 */
public class EvaluationDialog extends Dialog {

    public EvaluationDialog(Context context,String senderName,String shopName,OnCompainOrderListener onCompainOrderListener){
        super(context);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_evaluation_view,null);
        findViews(view,senderName,shopName,onCompainOrderListener);
        setContentView(view);
        Window window = this.getWindow();
        WindowManager m = ((Activity)context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 0.98); // 宽度设置为屏幕的0.65
//        p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的0.6
        window.setAttributes(p);
    }

    private void findViews(View view,String senderName,String shopName, final OnCompainOrderListener onCompainOrderListener){
        TextView txtvTitle = (TextView)view.findViewById(R.id.txtvTitle);
        txtvTitle.setText("签收确认");
        TextView txtvSenderName = (TextView)view.findViewById(R.id.txtvSenderName);
        txtvSenderName.setText(senderName);
        TextView txtvShopName = (TextView)view.findViewById(R.id.txtvShopName);
        txtvShopName.setText(shopName);
        TextView txtvConfirm = (TextView)view.findViewById(R.id.txtvConfirm);

        final RadioGroup rbSenderGroup = (RadioGroup)view.findViewById(R.id.rbSenderGroup);
        final RadioGroup rbShopGroup = (RadioGroup)view.findViewById(R.id.rbShopGroup);
        txtvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onCompainOrderListener!=null){
                    onCompainOrderListener.onConfirm(rbSenderGroup.getCheckedRadioButtonId() == R.id.rbSenderGood?1:0,
                            rbShopGroup.getCheckedRadioButtonId() == R.id.rbShopGood?1:0);
                }
            }
        });
    }

    public EvaluationDialog(Context context) {
        super(context);
    }

    public EvaluationDialog(Context context, int theme) {
        super(context, theme);
    }

    public EvaluationDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public interface OnCompainOrderListener{
        void onConfirm(int senderEvaluation,int shopEvaluation);
    }

}
