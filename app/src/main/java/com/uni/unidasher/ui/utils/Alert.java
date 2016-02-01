package com.uni.unidasher.ui.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.uni.unidasher.R;
import com.uni.unidasher.data.status.PushCode;
import com.uni.unidasher.ui.activity.MainActivity;
import com.uni.unidasher.ui.activity.TabActivity;
import com.uni.unidasher.ui.dialog.AlertPrompt;

/**
 * Created by Administrator on 2015/7/1.
 */
public class Alert {
    private Alert(){}

    public static void showTip(Context context,String title,String content,boolean isCancel,AlertPrompt.OnAlertClickListener onAlertClickListener){
        new AlertPrompt(context,title,content,isCancel,onAlertClickListener).show();
    }

    public static void showDefaultToast(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }


    public static final int NOTIFICATION_FLAG = 1;
    public static void notifyMsg(Context context,String userType,String pushCode,String title,String msg){
        Intent intent = new Intent(context, TabActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(PushCode.PushChatExtrasCode,Integer.parseInt(pushCode));
        bundle.putString(PushCode.PushExtrasUserType,userType);
        intent.putExtras(bundle);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Uri standardSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(msg)
                .setTicker(msg)
                .setSmallIcon(R.mipmap.ic_launcher)
//                .setLargeIcon(drawableToBitmap(context.getResources().getDrawable(R.mipmap.ic_launcher)))
                .setContentIntent(pIntent)
                .setSound(standardSoundUri)
                .setAutoCancel(true);

        Notification notification;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.getNotification();
        } else {
            notification = builder.build();
        }


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_FLAG, notification);
    }

    public static void cancelNotify(Context context){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }


    public static Bitmap drawableToBitmap(Drawable drawable)
    {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
