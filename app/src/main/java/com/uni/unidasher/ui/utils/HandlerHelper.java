package com.uni.unidasher.ui.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by Administrator on 2015/6/19.
 */
public class HandlerHelper {
    public static void post(final onRun o){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                o.run();
            }
        });
    }

    public static void postDelayed(final onRun o,long time){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                o.run();
            }
        },time);
    }
    public interface onRun{
        void run();
    }
}
