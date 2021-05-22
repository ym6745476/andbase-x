package com.andbase.library.view.calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout.LayoutParams;

import com.andbase.library.utils.AbViewUtil;
import com.andbase.library.view.listener.AbOnItemClickListener;

import java.util.Calendar;


/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/16 15:26
 * Email 396196516@qq.com
 * Info 日历控件单元格绘制类
 */

public class AbCalendarCell extends View {

	private Context context;

	private AbCalendarView calendarView;

	/** 字体大小. */
	private float textSize = 12;

	/** The m on item click listener. */
	private AbOnItemClickListener onItemClickListener;

	/** The pt. */
	private Paint linePaint = new Paint();

	/** The rect. */
	private RectF rect = new RectF();

	/** 显示的文字. */
	private String textDateValue = "";

	/** The i date year. */
	public int iDateYear = 0;

	/** The i date month. */
	public int iDateMonth = 0;

	/** The i date day. */
	public int iDateDay = 0;

	/** The is selected. */
	private boolean isSelected = false;

	/** The is active month. */
	private boolean isActiveMonth = false;

	/** The is today. */
	private boolean isToday = false;

	/** The b touched down. */
	private boolean bTouchedDown = false;

	/** holiday 影响cell文字颜色 */
	private boolean isHoliday = false;

	/** record 影响cell内的小图标 */
	private boolean isRecord = false;

    /** attend 影响日历的文字颜色. */
    private boolean isAttend = false;

	/** remind 影响日历的文字颜色 背景. */
	private boolean isRemind = false;

    /** important 影响日历的文字颜色 背景. */
    private boolean isImportant = false;

	/** editable 点击事件 */
	private boolean editable = true;

	/** 当前cell的序号. */
	private int position = 0;

	/** The anim alpha duration. */
	public static int ANIM_ALPHA_DURATION = 100;

	private Bitmap  recordBitmap = null;

	/**
	 * 构造函数.
	 * @param context the context
	 * @param position the position
	 * @param calendarView the calendarView
	 * @param iWidth the i width
	 * @param iHeight the i height
	 */
	public AbCalendarCell(Context context, AbCalendarView calendarView, int position, int iWidth, int iHeight) {
		super(context);
		this.context = context;
		this.calendarView = calendarView;
		setFocusable(true);
		setLayoutParams(new LayoutParams(iWidth, iHeight));
		this.position = position;
		this.textSize = AbViewUtil.dip2px(context,textSize);
		recordBitmap = calendarView.recordBitmap;
	}

	/**
	 * 获取这个Cell的日期.
	 * @return
	 */
	public Calendar getCellDate() {
		Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, iDateYear);
        calendar.set(Calendar.MONTH, iDateMonth);
        calendar.set(Calendar.DAY_OF_MONTH, iDateDay);
		return calendar;
	}

	/**
	 * 设置这个Cell的数据.
	 * @param iYear the i year
	 * @param iMonth the i month
	 * @param iDay the i day
	 * @param isToday the is today
	 * @param isSelected the is selected
	 * @param isHoliday the is holiday
	 * @param isActiveMonth the is active month
	 */
	public void setCellData(int iYear, int iMonth, int iDay, Boolean isToday,Boolean isSelected,
			Boolean isHoliday, int isActiveMonth) {
		iDateYear = iYear;
		iDateMonth = iMonth;
		iDateDay = iDay;

		this.textDateValue = Integer.toString(iDateDay);
		this.isActiveMonth = (iDateMonth == isActiveMonth);
		this.isToday = isToday;
		this.isHoliday = isHoliday;
		this.isSelected = isSelected;
	}

	/**
	 * 重载绘制方法.
	 * @param canvas the canvas
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawColor(calendarView.calendarConfig.gridLineColor);
		rect.set(0, 0, this.getWidth(), this.getHeight());
		rect.inset(0.5f, 0.5f);

		//绘制日历方格
		drawDayView(canvas);

		//绘制方格内文字
		drawDayText(canvas);

	}



	/**
	 * 绘制日历方格.
	 * @param canvas the canvas
	 */
	private void drawDayView(Canvas canvas) {

		linePaint.setColor(calendarView.calendarConfig.cellBackgroundColor);
		canvas.drawRect(rect, linePaint);

		if(!isActiveMonth){
		    return;
        }

		if (isSelected){

		    if(calendarView.calendarConfig.selectedCellShape == 0){
                Paint paint = new Paint();
                paint.setStrokeWidth(2);
                paint.setColor(calendarView.calendarConfig.selectedCellBackgroundColor);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(rect.centerX(),rect.centerY(),rect.width()/3,paint);
            }

		}

		if(isRemind){
			if(calendarView.calendarConfig.selectedCellShape == 0){
				Paint paint = new Paint();
				paint.setStrokeWidth(2);
				paint.setColor(calendarView.calendarConfig.primaryColor);
				paint.setStyle(Paint.Style.STROKE);
				canvas.drawCircle(rect.centerX(),rect.centerY(),rect.width()/3,paint);
			}
		}

		if(isImportant){
            if(calendarView.calendarConfig.selectedCellShape == 0){
                Paint paint = new Paint();
                paint.setStrokeWidth(2);
                paint.setColor(calendarView.calendarConfig.importantCellBackgroundColor);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(rect.centerX(),rect.centerY(),rect.width()/3,paint);
            }
        }

		if (isRecord && recordBitmap!=null) {
			Matrix matrix = new Matrix();
			matrix.setScale(20/recordBitmap.getWidth(), 20/recordBitmap.getHeight());
            matrix.setTranslate((getWidth()-recordBitmap.getWidth())/2,getHeight() - recordBitmap.getHeight()-5);
			canvas.drawBitmap(recordBitmap, matrix, linePaint);
		}

	}

	/**
	 * 绘制日历中的数字.
	 * @param canvas the canvas
	 */
	public void drawDayText(Canvas canvas) {
		linePaint.setTypeface(null);
		linePaint.setAntiAlias(true);
		linePaint.setShader(null);
		linePaint.setFakeBoldText(false);
		linePaint.setTextSize(textSize);
		linePaint.setColor(calendarView.calendarConfig.dayTextColor);
		linePaint.setUnderlineText(false);

        if (isHoliday){
            linePaint.setColor(calendarView.calendarConfig.holidayCellTextColor);
        }

		if(isToday){
			linePaint.setColor(calendarView.calendarConfig.primaryColor);
		}

        if(isAttend){
            linePaint.setColor(calendarView.calendarConfig.attendCellTextColor);
        }

		if(isRemind){
			linePaint.setColor(calendarView.calendarConfig.primaryColor);
		}

        if (isImportant){
            linePaint.setColor(calendarView.calendarConfig.importantCellTextColor);
        }

        if(isSelected){
            linePaint.setColor(calendarView.calendarConfig.selectedCellTextColor);
        }

		if (!editable){
			linePaint.setColor(calendarView.calendarConfig.unEditableDayTextColor);
		}

        if (!isActiveMonth){
            linePaint.setColor(calendarView.calendarConfig.notActiveDayTextColor);
        }

		if(isToday){
			textDateValue = "今";
		}

		final int iPosX = (int) rect.left + ((int) rect.width() >> 1) - ((int) linePaint.measureText(textDateValue) >> 1);
		final int iPosY = (int) (this.getHeight() - (this.getHeight() - getTextHeight()) / 2 - linePaint.getFontMetrics().bottom);

		canvas.drawText(textDateValue, iPosX, iPosY, linePaint);

	}


	/**
	 * 得到字体高度.
	 * @return the text height
	 */
	private int getTextHeight() {
		return (int) (-linePaint.ascent() + linePaint.descent());
	}


	/**
	 * 设置是否被选中.
	 * @param selected the new selected
	 */
	@Override
	public void setSelected(boolean selected) {
		if(editable){
			if (this.isSelected != selected) {
				this.isSelected = selected;
				this.invalidate();
			}
		}

	}

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    /**
	 * 设置是否有数据.
	 * @param record
	 */
	public void setRecord(boolean record) {
		if (this.isRecord != record) {
			this.isRecord = record;
			this.invalidate();
		}
	}

    public boolean isRemind() {
        return isRemind;
    }

    public void setRemind(boolean remind) {
        if (this.isRemind != remind) {
            this.isRemind = remind;
            this.invalidate();
        }
    }

	public boolean isAttend() {
		return isAttend;
	}

	public void setAttend(boolean attend) {
		if (this.isAttend != attend) {
			this.isAttend = attend;
			this.invalidate();
		}
	}

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(boolean important) {
        if (this.isImportant != important) {
            this.isImportant = important;
            this.invalidate();
        }
    }

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * 设置点击事件.
	 * @param onItemClickListener the new on item click listener
	 */
	public void setOnItemClickListener(AbOnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	/**
	 * 执行点击事件.
	 */
	public void doItemClick() {
		if (onItemClickListener != null){
			onItemClickListener.onItemClick(null,position);
	    }
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean bHandled = false;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			bHandled = true;
			bTouchedDown = true;
			invalidate();
			startAlphaAnimIn(AbCalendarCell.this);
		}
		if (event.getAction() == MotionEvent.ACTION_CANCEL) {
			bHandled = true;
			bTouchedDown = false;
			invalidate();
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			bHandled = true;
			bTouchedDown = false;
			invalidate();
			doItemClick();
		}
		return bHandled;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean bResult = super.onKeyDown(keyCode, event);
		if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
				|| (keyCode == KeyEvent.KEYCODE_ENTER)) {
			doItemClick();
		}
		return bResult;
	}

	/**
	 * 动画不透明度渐变.
	 * @param view the view
	 */
	public static void startAlphaAnimIn(View view) {
		AlphaAnimation anim = new AlphaAnimation(0.5F, 1);
		anim.setDuration(ANIM_ALPHA_DURATION);
		anim.startNow();
		view.startAnimation(anim);
	}

	public boolean isViewFocused() {
		return (this.isFocused() || bTouchedDown);
	}

	/**
	 * 是否为活动的月.
	 * @return true, if is active month
	 */
	public boolean isActiveMonth() {
		return isActiveMonth;
	}


}
