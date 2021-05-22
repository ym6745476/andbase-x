package com.andbase.library.view.viewpager;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.andbase.library.R;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 指示器
 */
public class AbIndicatorView extends View {

    /**
     * 可用状态列表
     */
    private final static int[][] STATE_LIST = {
            {-android.R.attr.state_selected, -android.R.attr.state_pressed, -android.R.attr.state_checked, -android.R.attr.state_enabled},
            {android.R.attr.state_selected, android.R.attr.state_pressed, android.R.attr.state_checked, android.R.attr.state_enabled}};

    /**
     * 画笔设置抗锯齿
     */
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * 每个绘图单元的个数
     */
    private int count;
    /**
     * 被选中绘图单元的索引
     */
    private int select = 0;
    /**
     * 被选中绘图单元的放缩比例
     */
    private float selectScale;
    /**
     * 绘图单元的颜色
     */
    private int color;
    /**
     * 绘图单元的 Drawable
     */
    private StateListDrawable unitDrawable = null;
    /**
     * 绘图单元的Rect
     */
    private Rect bounds;
    /**
     * 绘图单元的半径
     */
    private float radius;
    /**
     * 画笔宽度
     */
    private float strokeWidth;

    public AbIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbIndicatorView(Context context) {
        this(context, null);
    }

    public AbIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AbIndicatorView);
        count = ta.getInt(R.styleable.AbIndicatorView_indicator_count, 3);
        color = ta.getColor(R.styleable.AbIndicatorView_indicator_color, Color.RED);
        radius = ta.getDimension(R.styleable.AbIndicatorView_indicator_radius, 10);
        Drawable tempDrawable = ta.getDrawable(R.styleable.AbIndicatorView_indicator_drawable);
        selectScale = ta.getFloat(R.styleable.AbIndicatorView_indicator_select_scale, 1.0f);
        select = ta.getInt(R.styleable.AbIndicatorView_indicator_select, 0);
        ta.recycle();

        if (tempDrawable instanceof StateListDrawable) {
            unitDrawable = (StateListDrawable) tempDrawable;
        }
        setSelect(select);
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 得到模式和对应值
        int withMode = MeasureSpec.getMode(widthMeasureSpec);
        int withSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // 设置默认宽高
        int width = (int) ((strokeWidth + radius + radius) * 2 * count + getPaddingLeft() + getPaddingRight());
        int height = (int) ((strokeWidth + radius + radius) * 2 + getPaddingTop() + getPaddingBottom());

        int w, h;
        if (withMode == MeasureSpec.AT_MOST || withMode == MeasureSpec.UNSPECIFIED) {
            w = width;
        } else {
            w = withSize;
        }
        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            h = height;
        } else {
            h = heightSize;
        }

        setMeasuredDimension(w, h);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        bounds = new Rect(0, 0, (int) (radius * 2), (int) (radius * 2));
        if (unitDrawable != null) {
            unitDrawable.setBounds(bounds);
        }

        // 设置画笔
        strokeWidth = radius / 10;
        paint.setStrokeWidth(strokeWidth);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < count; i++) {
            canvas.save();
            canvas.translate(radius * i * 4 + getPaddingLeft(), getPaddingTop());

            if (unitDrawable != null) {
                drawDrawableUnit(canvas, i == select);
            } else {
                drawDefaultUnit(canvas, i == select);
            }
            canvas.restore();
        }
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        paint.setColor(color);
        invalidate();
    }

    /**
     * 用来绘制特定Drawable指示器单元显示
     */
    private void drawDrawableUnit(Canvas canvas, boolean isSelect) {
        canvas.save();
        // 居中
        canvas.translate((getHeight() - bounds.height()) / 2, (getHeight() - bounds.height()) / 2);
        if (isSelect) {
            unitDrawable.setState(STATE_LIST[1]);
            canvas.scale(selectScale, selectScale, bounds.centerX(), bounds.centerY());
        } else {
            unitDrawable.setState(STATE_LIST[0]);
        }
        unitDrawable.draw(canvas);
        canvas.restore();
    }

    /**
     * 用来绘制默认指示器单元显示
     */
    private void drawDefaultUnit(Canvas canvas, boolean isSelect) {
        if (isSelect) {
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawCircle(bounds.width(), bounds.height(), radius * selectScale, paint);
        } else {
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(bounds.width(), bounds.height(), radius, paint);
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        if (count < 0) {
            this.count = 0;
        }
        this.count = count;
        requestLayout();
    }

    public int getSelect() {
        return select;
    }

    /**
     * 设置选中的unit的index
     *
     * @param select
     */
    public void setSelect(int select) {
        this.select = (select % count + count) % count;
        invalidate();
    }

    public StateListDrawable getUnitDrawable() {
        return unitDrawable;
    }

    public void setUnitDrawable(Drawable unitDrawable) {
        if (unitDrawable instanceof StateListDrawable) {
            unitDrawable = (StateListDrawable) unitDrawable;
            unitDrawable.setBounds(bounds);
            invalidate();
        }
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        if (radius < 0)
            return;
        this.radius = radius;
        // 设置画笔
        strokeWidth = radius / 10;
        paint.setStrokeWidth(strokeWidth);
        invalidate();
    }

    public float getSelectScale() {
        return selectScale;
    }

    /**
     * @param selectScale
     */
    public void setSelectScale(float selectScale) {
        this.selectScale = selectScale;
        invalidate();
    }

    /**
     * 将当前指示器的位置向前移动
     */
    public void next() {
        select = (select + 1) % count;
        invalidate();
    }

    /**
     * 将当前指示器的位置向后移动
     */
    public void previous() {
        select = (select - 1 + count) % count;
        invalidate();
    }

}
