package com.andbase.library.view.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.andbase.library.utils.AbImageUtil;

/**
 * 可旋转的进度条位图
 * 利用 {@link BitmapShader} 绘制出圆形图案，周围留出空白以便绘制进度条。
 */
public class AbRotatingProgressDrawable extends Drawable {

    private static final int ROTATION_DEFAULT_SPEED = 25;
    private Paint paint,innerPaint, progressPaint;
    private Bitmap bitmap;
    private int canvasWidth;
    private float mRotation;
    private RectF rectF;

    private float progress;//进度条
    private int progressPercent;//进度条宽度
    private int progressColor;//进度条颜色

    // 旋转控制
    private RotateHandler rotateHandler;

    public AbRotatingProgressDrawable(Drawable drawable) {
        initDrawable();
        bitmapFromDrawable(drawable);
    }

    public AbRotatingProgressDrawable(Bitmap bitmap) {
        initDrawable();
        this.bitmap = bitmap;
    }

    private void initDrawable() {
        progressPercent = 8;
        progress = 0f;
        progressColor = Color.rgb(255,89,131);
        rotateHandler = new RotateHandler(Looper.getMainLooper());
        rectF = new RectF();
        progressPaint = new Paint();
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setAntiAlias(true);

        paint = new Paint();
        paint.setAntiAlias(true);
        //圆形背景颜色
        paint.setColor(Color.rgb(220,220,220));

        innerPaint = new Paint();
        innerPaint.setAntiAlias(true);
        //圆形背景颜色
        innerPaint.setColor(Color.rgb(76,180,231));
    }

    public float getRotation() {
        return mRotation;
    }

    public void setRotation(float rotation) {
        mRotation = rotation;
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        canvasWidth = canvas.getWidth();
        float progressWidth = canvasWidth * progressPercent / 100f;
        // 画背景图
        canvas.save();
        canvas.rotate(mRotation, getBounds().centerX(), getBounds().centerY());
        canvas.drawCircle(canvasWidth / 2, canvasWidth / 2, canvasWidth / 2, paint);
        //内圆
        canvas.drawCircle(canvasWidth / 2, canvasWidth / 2, canvasWidth / 2 - progressWidth, innerPaint);
        canvas.drawBitmap(bitmap,(canvasWidth - bitmap.getWidth())/2,(canvasWidth - bitmap.getWidth())/2,paint);
        canvas.restore();

        // 画进度条
        rectF.set(progressWidth/2, progressWidth/2, canvasWidth - progressWidth/2, canvasWidth - progressWidth/2);
        progressPaint.setStrokeWidth(progressWidth);
        canvas.drawArc(rectF, -90, progress, false, progressPaint);

    }

    /**
     * 设置进度
     *
     * @param progress 0-100
     */
    public void setProgress(float progress) {
        if (progress < 0 || progress > 100)
            return;
        progress = progress * 360 / 100f;
        this.progress = progress;
        invalidateSelf();
    }

    /**
     * 设置进度条相对于图片的百分比，默认为5%
     *
     * @param percent 0-100
     */
    public void setProgressWidthPercent(int percent) {
        this.progressPercent = percent;
        invalidateSelf();
    }

    /**
     * 设置进度条的颜色
     *
     * @param progressColor
     */
    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
        progressPaint.setColor(progressColor);
        invalidateSelf();
    }

    /**
     * 是否开始旋转
     *
     * @param rotate
     */
    public void rotate(boolean rotate) {
        rotateHandler.removeMessages(0);
        if (rotate) {
            rotateHandler.sendEmptyMessage(0);
        }
    }

    private void bitmapFromDrawable(Drawable drawable) {
        bitmap = AbImageUtil.drawableToBitmap(drawable);
        bitmap = AbImageUtil.toRoundBitmap(bitmap);
    }

    @Override
    public int getIntrinsicWidth() {
        return canvasWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return canvasWidth;
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    private class RotateHandler extends Handler {

        RotateHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                mRotation = mRotation + 1;
                if (mRotation > 360) {
                    mRotation = 0;
                }
                setRotation(mRotation);
                rotateHandler.sendEmptyMessageDelayed(0, ROTATION_DEFAULT_SPEED);
            }
            super.handleMessage(msg);
        }

    }

}
