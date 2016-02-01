package com.uni.unidasher.ui.views.chart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.uni.unidasher.R;
import com.uni.unidasher.data.datamodel.databaseAndApi.DishInfo;
import com.uni.unidasher.data.entity.WeekEarnInfo;
import com.uni.unidasher.ui.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmoothLineChartEquallySpaced extends View {
	
	private static final int CHART_COLOR = 0xFF0099CC;
	private static final int CIRCLE_SIZE = 8;
	private static final int STROKE_SIZE = 1;
	private static final float SMOOTHNESS = 0.35f; // the higher the smoother, but don't go over 0.5
	
	private final Paint mPaint;
	private final Path mPath;
	private final float mCircleSize;
	private final float mStrokeSize;
	private final float mBorder;
//	private final float mLeftRightBorder;
	
	private float[] mValues;
	private float mMinY;
	private float mMaxY;
	private List<WeekEarnInfo> weekEarnInfoList;
	

	public SmoothLineChartEquallySpaced(Context context) {
		this(context, null, 0);
	}

	public SmoothLineChartEquallySpaced(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SmoothLineChartEquallySpaced(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		float scale = context.getResources().getDisplayMetrics().density;
		
		mCircleSize = scale * CIRCLE_SIZE;
		mStrokeSize = scale * STROKE_SIZE;
		mBorder = mCircleSize*4;
//		mLeftRightBorder = mCircleSize*5;
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(mStrokeSize);
		
		mPath = new Path();
	}
	
	public void setData(float[] values) {
		mValues = values;		
		
		if (values != null && values.length > 0) {
			mMaxY = values[0];
			//mMinY = values[0].y;
			for (float y : values) {
				if (y > mMaxY) 
					mMaxY = y;
				/*if (y < mMinY)
					mMinY = y;*/
			}
		}
				
		invalidate();
	}

	public void setData(List<WeekEarnInfo> weekEarnInfoList) {
		Collections.reverse(weekEarnInfoList);
		this.weekEarnInfoList = weekEarnInfoList;
		mValues = new float[weekEarnInfoList.size()];
		for(int i=0;i<weekEarnInfoList.size();i++){
			WeekEarnInfo weekEarnInfo = weekEarnInfoList.get(i);
//			if(i==6){
//				mValues[i] = 20;
//			}else{
			mValues[i] = weekEarnInfo.getCarriageMoney();
//			}

		}

		if (mValues != null && mValues.length > 0) {
			mMaxY = mValues[0];
			//mMinY = values[0].y;
			for (float y : mValues) {
				if (y > mMaxY)
					mMaxY = y;
				/*if (y < mMinY)
					mMinY = y;*/
			}
		}

		invalidate();
	}
	
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		if (mValues == null || mValues.length == 0)
			return;

		int size = mValues.length;
		
		final float height = getMeasuredHeight() - 2*mBorder;	
		final float width = getMeasuredWidth() - 2*mBorder;
		
		final float dX = mValues.length > 1 ? mValues.length-1  : (2);	
		final float dY = (mMaxY-mMinY) > 0 ? (mMaxY-mMinY) : (2);
				
		mPath.reset();
				
		// calculate point coordinates
		List<PointF> points = new ArrayList<PointF>(size);
		for (int i=0; i<size; i++) {
			float x = mBorder + i*width/dX;
			float y = mBorder + height - (mValues[i]-mMinY)*height/dY; 
			points.add(new PointF(x,y));
		}

		// calculate smooth path
		float lX = 0, lY = 0;
		mPath.moveTo(points.get(0).x, points.get(0).y);
		for (int i=1; i<size; i++) {	
			PointF p = points.get(i);	// current point
			
			// first control point
			PointF p0 = points.get(i-1);	// previous point
			float x1 = p0.x + lX; 	
			float y1 = p0.y + lY;
	
			// second control point
			PointF p1 = points.get(i+1 < size ? i+1 : i);	// next point
			lX = (p1.x-p0.x)/2*SMOOTHNESS;		// (lX,lY) is the slope of the reference line 
			lY = (p1.y-p0.y)/2*SMOOTHNESS;
			float x2 = p.x - lX;	
			float y2 = p.y - lY;

			// add line
//			mPath.cubicTo(x1,y1,x2, y2, p.x, p.y);
			mPath.lineTo(p.x, p.y);
		}
		

		// draw path
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Style.STROKE);
		canvas.drawPath(mPath, mPaint);
		
		// draw area
		if (size > 0) {
			mPaint.setStyle(Style.FILL);
			mPaint.setColor((Color.RED & 0xFFFFFF) | 0x10000000);
			mPath.lineTo(points.get(size-1).x, height+mBorder);	
			mPath.lineTo(points.get(0).x, height+mBorder);	
			mPath.close();
			canvas.drawPath(mPath, mPaint);
		}

		// draw circles
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Style.FILL_AND_STROKE);
//		for (PointF point : points) {
			canvas.drawCircle(points.get(points.size()-1).x, points.get(points.size()-1).y, mCircleSize/2, mPaint);
//		}
		mPaint.setStyle(Style.FILL);
		mPaint.setColor(Color.WHITE);
//		for (PointF point : points) {
			canvas.drawCircle(points.get(points.size()-1).x, points.get(points.size()-1).y, (mCircleSize-mStrokeSize)/2, mPaint);
//		}
		mPaint.setColor(getResources().getColor(R.color.mainTextColor));
		mPaint.setTextSize(30);
		for (int i = 0;i<points.size();i++) {
			PointF point = points.get(i);
			canvas.drawText(weekEarnInfoList.get(i).getDate().substring(5,10),point.x-38, getMeasuredHeight()-20, mPaint);
//			canvas.drawText("05-12",point.x-38, getMeasuredHeight()-5, mPaint);
			if(i==weekEarnInfoList.size()-1){
//				mPaint.setAlpha(0x80); //设置透明程度
				WeekEarnInfo weekEarnInfo = weekEarnInfoList.get(i);
				Paint paint = new Paint();
				paint.setColor(Color.WHITE);
				paint.setTextSize(35);

				TextView textView= new TextView(this.getContext());
				textView.setBackgroundResource(R.mipmap.chart_line_bottom);
				textView.setText("￥ "+weekEarnInfo.getCarriageMoney());
				textView.setTextColor(Color.WHITE);
				textView.setDrawingCacheEnabled(true);
				textView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
				textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
				Bitmap bitmap = textView.getDrawingCache();
//				Bitmap topBitmap = CommonUtils.getResBitmap(this.getContext(),R.mipmap.chart_line_bottom);
				if(bitmap!=null) {
					canvas.drawBitmap(bitmap, point.x - bitmap.getWidth() + 15, point.y - bitmap.getHeight() - 20, mPaint);
//					canvas.drawText("￥ " + weekEarnInfo.getDishMoney(), point.x - bitmap.getWidth() + 20, point.y - 45, paint);
				}
//				Bitmap bottomBitmap = CommonUtils.getResBitmap(this.getContext(),R.mipmap.chart_line_bottom);
//				if(topBitmap!=null){
////					canvas.drawText("￥ "+ weekEarnInfo.getCarriageMoney(),point.x,point.y,mPaint);
//					canvas.drawBitmap(bottomBitmap, point.x - 20, point.y + 20, mPaint);
//				}
			}
		}
	}
}
