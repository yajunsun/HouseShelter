package com.hiibox.houseshelter.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;

import android.view.View;

import com.zgan.youbao.R;

    
  
  
  
  
  
  
  
public class TemperatureTrendView extends View {

	private Paint mPointPaint;                
	private Paint mTextPaint;
	private Paint mLinePaint;
	private Context mContext;
	private int width;
	private int height;
	private int columnSpacing = 0;         
	private int rowSpacing = 0;         
	private int temperatures[];             
	private int pointCoordinateX[];                 
	private int pointCoordinateY[];                 

	public TemperatureTrendView(Context context) {
		super(context);
	}

	public TemperatureTrendView(Context context, AttributeSet attr) {
		super(context, attr);
	}

	public TemperatureTrendView(Context context, int width, int height, int temp[]) {
		super(context);
		this.mContext = context;
		this.width = width;
		this.height = height;
		temperatures = new int[10];
		for (int i = 0; i < temp.length; i ++) {
		    if (temp[temp.length-1-i] > 80) {
		        this.temperatures[i] = 80;
		    } else if (temp[temp.length-1-i] < -20) {
		        this.temperatures[i] = -20;
		    } else {
		        this.temperatures[i] = temp[temp.length-1-i];
		    }
		}
		initPointCoordinate();
		initPointPaint();
        initLinePaint();
        initTextPaint();
	}

	    
  
  
    private void initPointCoordinate() {
    	int max = temperatures[0];
    	int min = temperatures[0];
    	for (int i = 1; i < temperatures.length; i ++) {
    		max = (max > temperatures[i]) ? max : temperatures[i];
    		min = (min < temperatures[i]) ? min : temperatures[i];
    	}
    	int diff = 0;
    	if (max >= 0 && min >= 0) {
    		diff = max + 10;
    	} else if (max >= 0 && min < 0) {
    		diff = max - min + 10;
    	} else if (max <= 0) {
    		diff = Math.abs(min) + 10;
    	}
        columnSpacing = width / 10;
		rowSpacing = height / diff;
		pointCoordinateX = new int[10];
		pointCoordinateY = new int[10];
		
		for (int i = 0; i < temperatures.length; i ++) {
		    pointCoordinateX[i] = i*columnSpacing + columnSpacing/2;             
		    pointCoordinateY[i] = height-temperatures[i]*rowSpacing-15;             
		   
        }
    }

    private void initPointPaint() {
        mPointPaint = new Paint();
		mPointPaint.setAntiAlias(true);
		mPointPaint.setColor(mContext.getResources().getColor(R.color.sliding_menu_tv));
    }
    
    private void initLinePaint() {
        mLinePaint = new Paint();
        mLinePaint.setColor(Color.WHITE);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(8);
        mLinePaint.setStyle(Style.FILL);
    }
    
    private void initTextPaint() {
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mContext.getResources().getColor(R.color.sliding_menu_tv));
        mTextPaint.setTextSize(20f);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTendencyChart(canvas);
    }
    
        
  
  
  
  
    private void drawTendencyChart(Canvas canvas) {
        if (null == pointCoordinateX && null == pointCoordinateY) {
            return;
        }
        int len = temperatures.length;
            
  
  
        for (int i = 0; i < len-1; i ++) {
            canvas.drawLine(pointCoordinateX[i], pointCoordinateY[i], pointCoordinateX[i+1], pointCoordinateY[i+1], mLinePaint);
        }
            
  
  
        for (int i = 0; i < len; i ++) {
            canvas.drawCircle(pointCoordinateX[i]+5, pointCoordinateY[i], 10, mPointPaint);
            canvas.drawText(temperatures[i]+"", pointCoordinateX[i]-20, pointCoordinateY[i]-20, mTextPaint);
        }
        
    }

}
