package com.andbase.library.view.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.andbase.library.utils.AbGraphicUtil;
import com.andbase.library.utils.AbViewUtil;

import java.util.Calendar;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/16 15:26
 * Email 396196516@qq.com
 * Info 日日历控件头部绘制类
 */

public class AbCalendarHeader extends View {

	private Context context;

	private final Paint paint;

	private RectF rect = new RectF();

	/** 星期的数据. */
	private String[] dayNameArray = new String[10];

	/** 每个单元格的宽度. */
	private int cellWidth = 0;

	/** 文字颜色. */
	private int defaultTextColor = Color.rgb(86, 86, 86);

	/** 特别文字颜色. */
	private int specialTextColor = Color.rgb(240, 140, 26);

	/** 字体大小. */
	private float textSize = 12;

	/** 字体是否加粗. */
	private boolean defaultTextBold = false;

	/**
	 * 日历头.
	 * @param context the context
	 */
	public AbCalendarHeader(Context context) {
		this(context, null);
	}


	public AbCalendarHeader(Context context, AttributeSet attributeset) {
		super(context);
		this.context = context;
		dayNameArray[Calendar.SUNDAY] = "日";
		dayNameArray[Calendar.MONDAY] = "一";
		dayNameArray[Calendar.TUESDAY] = "二";
		dayNameArray[Calendar.WEDNESDAY] = "三";
		dayNameArray[Calendar.THURSDAY] = "四";
		dayNameArray[Calendar.FRIDAY] = "五";
		dayNameArray[Calendar.SATURDAY] = "六";
		paint = new Paint(); 
        paint.setColor(defaultTextColor);
        paint.setAntiAlias(true); 
        paint.setTypeface(Typeface.DEFAULT);
		textSize = AbViewUtil.dip2px(context,textSize);
        paint.setTextSize(textSize);
        
        WindowManager wManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);    	
		Display display = wManager.getDefaultDisplay();    	
		cellWidth = display.getWidth()/7;
	}
	

	
	/**
	 * 设置文字大小.
	 * @param textSize
	 */
	public void setTextSize(int textSize) {
		this.textSize = AbViewUtil.dip2px(context,textSize);
		paint.setTextSize(textSize);
		this.invalidate();
	}  


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);


		//设置矩形大小
		rect.set(0, 0, this.getWidth(),this.getHeight());
		rect.inset(0.5f,0.5f);
		// 绘制日历头部
		drawDayHeader(canvas);

        paint.setColor(Color.rgb(230, 230, 230));
        canvas.drawLine(0,this.getHeight(),this.getWidth(), this.getHeight(),paint);
	}


	private void drawDayHeader(Canvas canvas) {
		// 写入日历头部，设置画笔参数

		if(defaultTextBold){
			paint.setFakeBoldText(true);
		}
		paint.setColor(defaultTextColor);
		
		for (int iDay = 1; iDay < 8; iDay++) {
			if(iDay==1 || iDay==7){
				paint.setColor(specialTextColor);
			}
			// draw day name
			final String dayName = getWeekDayName(iDay);
			
			TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
			textPaint.setTypeface(Typeface.DEFAULT_BOLD);
			textPaint.setTextSize(textSize);
	        FontMetrics fm  = textPaint.getFontMetrics();
	        //得到行高
	        int textHeight = (int)Math.ceil(fm.descent - fm.ascent);
	        int textWidth = (int) AbGraphicUtil.getStringWidth(dayName,textPaint);
			
			final int x = (int) rect.left +cellWidth*(iDay-1)+(cellWidth-textWidth)/2;
			final int y = (int) (this.getHeight() - (this.getHeight() - textHeight) / 2 - paint.getFontMetrics().bottom);
			canvas.drawText(dayName, x, y, paint);
			paint.setColor(defaultTextColor);
		}
		
	}
	
	/**
	 * 获取星期的文字.
	 * @param calendarDay the calendar day
	 * @return the week day name
	 */
	public String getWeekDayName(int calendarDay) {
		return dayNameArray[calendarDay];
	}
	
}
