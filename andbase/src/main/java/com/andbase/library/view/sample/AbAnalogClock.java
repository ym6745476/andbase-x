
package com.andbase.library.view.sample;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;

import com.andbase.library.R;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/12/13 09:13
 * Email 396196516@qq.com
 * Info 自定义模拟时钟
 */

public class AbAnalogClock extends View {

	/** 上下文. */
	private Context context;

	/** 时针图标 */
	private Drawable hourDrawable;

	/** 分针图标 */
	private Drawable minuteDrawable;

	/** 秒针图标 */
	private Drawable secondDrawable;

	/** 表盘图标 */
	private Drawable dialDrawable;

	/** 表盘图片的宽度 */
	private int dialWidth;

	/** 表盘图片的高度 */
	private int dialHeight;

	/** 附加到window 状态. */
	private boolean attached;

	/** 当前时间 */
	private Time currentTime;

	/** 当前的分钟. */
	private float minutes;

	/** 当前的小时. */
	private float hour;

	/** 当前的秒. */
	private float second;

	/**
	 * 构造函数.
	 * @param context the context
	 */
	public AbAnalogClock(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 构造函数XML.
	 * @param context the context
	 */
	public AbAnalogClock(Context context, AttributeSet attrs) {
		super(context, attrs);

		init(context);

		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AbAnalogClock);
		dialDrawable = typedArray.getDrawable(R.styleable.AbAnalogClock_dialDrawable);
		hourDrawable = typedArray.getDrawable(R.styleable.AbAnalogClock_hourDrawable);
		minuteDrawable = typedArray.getDrawable(R.styleable.AbAnalogClock_minuteDrawable);
		secondDrawable = typedArray.getDrawable(R.styleable.AbAnalogClock_secondDrawable);
		typedArray.recycle();

		if(dialDrawable != null){
			//表盘的宽度和高度
			dialWidth = dialDrawable.getIntrinsicWidth();
			dialHeight = dialDrawable.getIntrinsicHeight();
		}

	}

	/**
	 * 初始化函数
	 * @param context
     */
	private void init(Context context){
		this.context = context;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		//当前时间，可能有时区变化
		currentTime = new Time();

		// 更新当前时间每秒调用一次
		onTimeChanged();

	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (attached) {
			attached = false;
		}
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		//View 的大小适配成表盘宽高比的大小
		float hScale = 1.0f;
		float vScale = 1.0f;

        //指定的宽度小于表盘宽度  大于的说明可以放下表盘，就不需要重设尺寸
		if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < dialWidth) {
			hScale = (float) widthSize / (float) dialWidth;
		}

        //指定的高度小于表盘高度
		if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < dialHeight) {
			vScale = (float) heightSize / (float) dialHeight;
		}
        //能包裹表盘的最小尺寸
		float scale = Math.min(hScale, vScale);

        //设置View的尺寸
		setMeasuredDimension(dialWidth * (int)scale,dialHeight * (int)scale);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

        //view的宽度和高度
		int availableWidth = getRight() - getLeft();
		int availableHeight = getBottom() - getTop();

        //中心坐标
		int x = availableWidth / 2;
		int y = availableHeight / 2;

        //表盘绘制
		int w = dialDrawable.getIntrinsicWidth();
		int h = dialDrawable.getIntrinsicHeight();

        //画布的缩放状态
		boolean scaled = false;

		if (availableWidth < w || availableHeight < h) {
			scaled = true;
			float scale = Math.min((float) availableWidth / (float) w,(float) availableHeight / (float) h);
			//保存画布
            canvas.save();
            //缩放
			canvas.scale(scale, scale, x, y);
		}

        //表盘的绘制位置 待会制图片的左上角的坐标  右下角坐标
        dialDrawable.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        dialDrawable.draw(canvas);
        if(scaled){
            canvas.restore();
        }


		//时针
		canvas.save();
        //一周分为12份
		canvas.rotate(hour / 12.0f * 360.0f, x, y);
        w = hourDrawable.getIntrinsicWidth();
        h = hourDrawable.getIntrinsicHeight();
        hourDrawable.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        hourDrawable.draw(canvas);
		canvas.restore();

		//分针
		canvas.save();
		canvas.rotate(minutes / 60.0f * 360.0f, x, y);

        w = minuteDrawable.getIntrinsicWidth();
        h = minuteDrawable.getIntrinsicHeight();
        minuteDrawable.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        minuteDrawable.draw(canvas);
		canvas.restore();

		//秒针
		canvas.save();
		canvas.rotate(second / 60.0f * 360.0f, x, y);

        w = secondDrawable.getIntrinsicWidth();
        h = secondDrawable.getIntrinsicHeight();
        secondDrawable.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        secondDrawable.draw(canvas);
		canvas.restore();
	}

	/**
	 * 时间变化.
	 */
	private void onTimeChanged() {

		//当前时间
		currentTime.setToNow();

		int mHour = currentTime.hour;
		int mMinute = currentTime.minute;
		int mSecond = currentTime.second;
		this.second =  mSecond;
		this.minutes = mMinute + second / 60.0f;
		this.hour = mHour + this.minutes / 60.0f;

		updateContentDescription(currentTime);

		//1秒更新
        new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				onTimeChanged();
			}
		}, 1000);

		//刷新界面
        invalidate();
	}


	/**
	 * 更新控件的描述信息，并没实际用
	 * @param time
     */
	private void updateContentDescription(Time time) {
		final int flags = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR;
		String contentDescription = DateUtils.formatDateTime(getContext(),time.toMillis(false), flags);
		setContentDescription(contentDescription);
	}

	public Drawable getHourDrawable() {
		return hourDrawable;
	}

	public void setHourDrawable(Drawable hourDrawable) {
		this.hourDrawable = hourDrawable;
	}

	public Drawable getMinuteDrawable() {
		return minuteDrawable;
	}

	public void setMinuteDrawable(Drawable minuteDrawable) {
		this.minuteDrawable = minuteDrawable;
	}

	public Drawable getSecondDrawable() {
		return secondDrawable;
	}

	public void setSecondDrawable(Drawable secondDrawable) {
		this.secondDrawable = secondDrawable;
	}

	public Drawable getDialDrawable() {
		return dialDrawable;
	}

	public void setDialDrawable(Drawable dialDrawable) {
		this.dialDrawable = dialDrawable;

		//表盘的宽度和高度
		dialWidth = dialDrawable.getIntrinsicWidth();
		dialHeight = dialDrawable.getIntrinsicHeight();
	}
}
