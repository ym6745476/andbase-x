package com.andbase.library.camera;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.andbase.library.R;
import com.andbase.library.utils.AbGraphicUtil;
import com.andbase.library.utils.AbViewUtil;

import java.util.Collection;
import java.util.HashSet;

/**
 * 自定义组件实现,扫描功能
 */
public class AbScanRectView extends View {

    private Context context = null;
    private final long ANIMATION_DELAY = 80L;
    private final int OPAQUE = 0xFF;

    private Paint paint;
    private TextPaint textPaint;
    private int possiblePointColor;
    private Collection<Point> possibleResultPoints;
    private Collection<Point> lastPossibleResultPoints;

    // 扫描线移动的y
    private int scanLineY;
    // 扫描线移动速度
    private int scanVelocity = 10;
    // 扫描线
    private Bitmap scanLight;

    // 扫描框边角颜色
    private int innerCornerColor;
    // 扫描框边角长度
    private int innerCornerLength;
    // 扫描框边角宽度
    private int innerCornerWidth;

    private Rect frameRect = null;

    private String tips = "";

    public AbScanRectView(Context context) {
        this(context, null);
    }

    public AbScanRectView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);

    }

    public AbScanRectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 初始化内部框的大小
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AbScanRectView);
        // 扫描框边角颜色
        innerCornerColor = typedArray.getColor(R.styleable.AbScanRectView_inner_corner_color, Color.parseColor("#2AB4FB"));
        // 扫描框边角长度
        innerCornerLength = (int) typedArray.getDimension(R.styleable.AbScanRectView_inner_corner_length, 50);
        // 扫描框边角宽度
        innerCornerWidth = (int) typedArray.getDimension(R.styleable.AbScanRectView_inner_corner_width, 10);
        // 扫描控件
        scanLight = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(R.styleable.AbScanRectView_inner_scan_bitmap, -1));

        typedArray.recycle();

        paint = new Paint();
        possiblePointColor = Color.parseColor("#C0FFFF00");
        possibleResultPoints = new HashSet<>(5);

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setStrokeWidth(1f);
        textPaint.setTextSize(AbViewUtil.sp2px(context,13));
    }

    @Override
    public void onDraw(Canvas canvas) {
        int width = canvas.getWidth();
        if(frameRect != null){
            paint.setColor(Color.rgb(220,220,220));
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(frameRect, paint);

            textPaint.setColor(Color.rgb(220,220,220));
            float tipsWidth = AbGraphicUtil.getDesiredWidth(tips,textPaint);
            canvas.drawText(tips,(width-tipsWidth)/2,frameRect.bottom + AbViewUtil.dip2px(context,25),textPaint);

            paint.setColor(innerCornerColor);
            drawFrameBounds(canvas, frameRect);
            drawScanLight(canvas, frameRect);

            Collection<Point> currentPossible = possibleResultPoints;
            Collection<Point> currentLast = lastPossibleResultPoints;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new HashSet<Point>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(OPAQUE);
                paint.setColor(possiblePointColor);

                for (Point point : currentPossible) {
                    canvas.drawCircle(frameRect.left + point.x, frameRect.top + point.y, 6.0f, paint);
                }
            }
            if (currentLast != null) {
                paint.setAlpha(OPAQUE / 2);
                paint.setColor(possiblePointColor);

                for (Point point : currentLast) {
                    canvas.drawCircle(frameRect.left + point.x, frameRect.top + point.y, 3.0f, paint);
                }
            }

            postInvalidateDelayed(ANIMATION_DELAY, frameRect.left, frameRect.top, frameRect.right, frameRect.bottom);
        }

    }

    /**
     * 设置扫描框尺寸
     */
    public void initQRScanRectView(String text){
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        Rect frameRect = new Rect();
        int frameWidth = (int)(width * 0.6);
        frameRect.set((int)(width*0.2),(height-frameWidth)/2,width-(int)(width*0.2),(height-frameWidth)/2 + frameWidth);
        setFrameRect(frameRect);
        if(text != null){
            setTips(text);
        }
    }


    /**
     * 绘制移动扫描线
     *
     * @param canvas
     * @param frame
     */
    private void drawScanLight(Canvas canvas, Rect frame) {
        if (scanLineY == 0) {
            scanLineY = frame.top;
        }
        if (scanLineY >= frame.bottom - 10) {
            scanLineY = frame.top;
        } else {
            scanLineY += scanVelocity;
        }
        Rect scanRect = new Rect(frame.left, scanLineY, frame.right, scanLineY + 10);
        canvas.drawBitmap(scanLight, null, scanRect, paint);
    }

    /**
     * 绘制取景框边框
     *
     * @param canvas
     * @param frame
     */
    private void drawFrameBounds(Canvas canvas, Rect frame) {

        paint.setColor(innerCornerColor);
        paint.setStyle(Paint.Style.FILL);

        // 左上角
        canvas.drawRect(frame.left, frame.top, frame.left + innerCornerWidth, frame.top + innerCornerLength, paint);
        canvas.drawRect(frame.left, frame.top, frame.left + innerCornerLength, frame.top + innerCornerWidth, paint);
        // 右上角
        canvas.drawRect(frame.right - innerCornerWidth, frame.top, frame.right, frame.top + innerCornerLength, paint);
        canvas.drawRect(frame.right - innerCornerLength, frame.top, frame.right, frame.top + innerCornerWidth, paint);
        // 左下角
        canvas.drawRect(frame.left, frame.bottom - innerCornerLength, frame.left + innerCornerWidth, frame.bottom, paint);
        canvas.drawRect(frame.left, frame.bottom - innerCornerWidth, frame.left + innerCornerLength, frame.bottom, paint);
        // 右下角
        canvas.drawRect(frame.right - innerCornerWidth, frame.bottom - innerCornerLength, frame.right, frame.bottom, paint);
        canvas.drawRect(frame.right - innerCornerLength, frame.bottom - innerCornerWidth, frame.right, frame.bottom, paint);
    }

    public void addPossibleResultPoint(Point point) {
        possibleResultPoints.add(point);
    }

    public Rect getFrameRect() {
        return frameRect;
    }

    public void setFrameRect(Rect frameRect) {
        this.frameRect = frameRect;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }
}
