package com.uni.unidasher.ui.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/6/22.
 */
public class CommonUtils {
    public static String isCurrentInTimePeriod(String serviceTimes){
        if(TextUtils.isEmpty(serviceTimes)){
            serviceTimes = "00:00-24:00";
        }
        boolean isIn = false;
        String timerPeriod = "";
        String service[] = serviceTimes.split(",");
        long currentTime = new Date().getTime();
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(currentTime);
        int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY);
        int currentMins = currentCalendar.get(Calendar.MINUTE);
        for(int i = 0;i<service.length;i++){
            String startTimePeriod = service[i].substring(0,5);
            String endTimePeriod = service[i].substring(6,11);
            String[] startArray = startTimePeriod.split(":");
            String[] endArray = endTimePeriod.split(":");
            int startHour = Integer.parseInt(startArray[0]);
            int startMins = Integer.parseInt(startArray[1]);
            int endHour = Integer.parseInt(endArray[0]);
            int endMins = Integer.parseInt(endArray[1]);
            if(currentHour>startHour&&currentHour<endHour){
                isIn = true;
            }

            if(currentHour == endHour){
                if(currentMins <= endMins){
                    isIn = true;
                }
            }
            if(currentHour == startHour){
                if(currentMins>startMins){
                    isIn = true;
                }
            }
            if(isIn){
                timerPeriod = service[i];
                break;
            }
        }
        return timerPeriod;
    }

    public static List<String> getTimePeriod(String timePeriod){
//        String startTimePeriod = timePeriod.substring(0,5);
//        String endTimePeriod = timePeriod.substring(6,11);
//        String[] startArray = startTimePeriod.split(":");
//        String[] endArray = endTimePeriod.split(":");
//        int startHour = Integer.parseInt(startArray[0]);
//        int startMins = Integer.parseInt(startArray[1]);
//        int endHour = Integer.parseInt(endArray[0]);
//        int endMins = Integer.parseInt(endArray[1]);
//        if(endMins == 0){
//            endMins = 30;
//        }else if(endMins == 30){
//            endMins = 0;
//            endHour+=1;
//        }
        if(TextUtils.isEmpty(timePeriod)){
            return new ArrayList<>();
        }
        long currentTime = new Date().getTime();
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(currentTime);
        int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY);
        int currentMins = currentCalendar.get(Calendar.MINUTE);

        int selectHour = currentHour;
        int selectMins = currentMins;
        List<String> ls = new ArrayList<>();
//        String hour = selectHour>9?selectHour+"":"0"+selectHour;
//        String mins = selectMins>9?selectMins+"":"0"+selectMins;
//        String time = hour+":"+mins;
        boolean flag = true;
        while(flag){
            selectMins+=30;
            if(selectMins>30&&selectMins<=60){
                selectMins = 0;
                selectHour+=1;
            }
            if(selectMins>60){
                selectHour+=1;
                selectMins = 30;
            }
            String h = selectHour>9?selectHour+"":"0"+selectHour;
            String m = selectMins>9?selectMins+"":"0"+selectMins;
            String time = h+":"+m;
            if(isPointInPeriod(time, timePeriod)){
                ls.add(time);
            }else{
                flag = false;
            }
        }
        return ls;
    }

    public static boolean isIn(int startHour,int startMins,int endHour,int endMins,int cHour,int cMins){
        boolean isIn = false;
        if(cHour>=startHour&&cHour<endHour){
            isIn = true;
        }
        if(cHour == startHour&&startHour == endHour){
            if(cMins>startMins&&cMins<endMins){
                isIn = true;
            }
        }
        if(cHour == endHour){
            if(cMins<=endMins){
                isIn = true;
            }
        }
        return isIn;
    }

    public static boolean isPointInPeriod(String ctime, String timePeriod){
//        long currentTime = new Date().getTime();
        String startTimePeriod = timePeriod.substring(0, 5);
        String endTimePeriod = timePeriod.substring(6,11);
        String[] startArray = startTimePeriod.split(":");
        String[] endArray = endTimePeriod.split(":");
        String[] cArray = ctime.split(":");
        int startHour = Integer.parseInt(startArray[0]);
        int startMins = Integer.parseInt(startArray[1]);
        int endHour = Integer.parseInt(endArray[0]);
//        if(endHour == 24){
//            endHour = 0;
//        }
        int endMins = Integer.parseInt(endArray[1]);
        int cHour = Integer.parseInt(cArray[0]);
        int cMins = Integer.parseInt(cArray[1]);
        endMins+=30;
        if(endMins>30&&endMins<=60){
            endMins = 0;
            endHour+=1;
        }
        if(endMins>60){
            endHour+=1;
            endMins = 30;
        }
        if(endHour == 24 && endMins > 0){
            endMins = 0;
        }
        boolean isIn = false;
        if(cHour>startHour&&cHour<endHour){
            isIn = true;
        }

        if(cHour == endHour){
            if(cMins <= endMins){
                isIn = true;
            }
        }
        if(cHour == startHour){
            if(cMins>startMins){
                isIn = true;
            }
        }

        return isIn;


//        long startTime, stopTime, cTime;
//        Calendar currentCalendar = Calendar.getInstance();
//        currentCalendar.setTimeInMillis(currentTime);
//
//        currentCalendar.clear(Calendar.HOUR_OF_DAY);
//        currentCalendar.clear(Calendar.MINUTE);
//        currentCalendar.set(Calendar.HOUR_OF_DAY, startHour);
//        currentCalendar.set(Calendar.MINUTE, startMins);
//        startTime = currentCalendar.getTimeInMillis();
//
//        currentCalendar.clear(Calendar.HOUR_OF_DAY);
//        currentCalendar.clear(Calendar.MINUTE);
//        currentCalendar.set(Calendar.HOUR_OF_DAY, cHour);
//        currentCalendar.set(Calendar.MINUTE, cMins);
//        cTime = currentCalendar.getTimeInMillis();
//
//        if(endHour == 0){
//            currentCalendar.add(Calendar.DAY_OF_MONTH, 1);
//        }
//        currentCalendar.clear(Calendar.HOUR_OF_DAY);
//        currentCalendar.clear(Calendar.MINUTE);
//        currentCalendar.set(Calendar.HOUR_OF_DAY, endHour);
//        currentCalendar.set(Calendar.MINUTE, endMins+30);
//        stopTime = currentCalendar.getTimeInMillis();
//        return ((startTime < cTime && cTime <= stopTime) ? true : false);
    }

    public static Bitmap getLoacalBitmap(String url) {
        try {
            return BitmapFactory.decodeFile(url);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap getResBitmap(Context context,int res){
        BitmapDrawable draw=(BitmapDrawable) context.getResources().getDrawable(res);
        Bitmap m=draw.getBitmap();
        return m;
    }

    /**
     * 判断是否运行在后台
     * @param context
     * @return
     */
    public static boolean isRunningForeground (Context context)
    {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if(!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName()))
        {
            return true ;
        }
        return false ;
    }
}
