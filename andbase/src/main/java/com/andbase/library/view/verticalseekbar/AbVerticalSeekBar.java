package com.andbase.library.view.verticalseekbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.core.view.ViewCompat;
import androidx.appcompat.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ProgressBar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/16 13:27
 * Email 396196516@qq.com
 * Info 垂直方向的SeekBar
 */
public class AbVerticalSeekBar extends AppCompatSeekBar {
    public static final int ROTATION_ANGLE_CW_90 = 90;
    public static final int ROTATION_ANGLE_CW_270 = 270;

    private boolean isDragging;
    private Drawable thumbImage;
    private Method methodSetProgressFromUser;
    private int rotationAngle = ROTATION_ANGLE_CW_90;

    public AbVerticalSeekBar(Context context) {
        super(context);
        initialize(context, null, 0, 0);
    }

    public AbVerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0, 0);
    }

    public AbVerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs, defStyle, 0);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        ViewCompat.setLayoutDirection(this, ViewCompat.LAYOUT_DIRECTION_LTR);

        if (attrs != null) {
            final int rotationAngle = 270;
            if (isValidRotationAngle(rotationAngle)) {
                this.rotationAngle = rotationAngle;
            }
        }
    }

    @Override
    public void setThumb(Drawable thumb) {
        thumbImage = thumb;
        super.setThumb(thumb);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (useViewRotation()) {
            return onTouchEventUseViewRotation(event);
        } else {
            return onTouchEventTraditionalRotation(event);
        }
    }

    private boolean onTouchEventTraditionalRotation(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setPressed(true);
                onStartTrackingTouch();
                trackTouchEvent(event);
                attemptClaimDrag(true);
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                if (isDragging) {
                    trackTouchEvent(event);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (isDragging) {
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                    setPressed(false);
                } else {
                    // Touch up when we never crossed the touch slop threshold
                    // should
                    // be interpreted as a tap-seek to that location.
                    onStartTrackingTouch();
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                    attemptClaimDrag(false);
                }
                // ProgressBar doesn't know to repaint the thumb drawable
                // in its inactive state when the touch stops (because the
                // value has not apparently changed)
                invalidate();
                break;

            case MotionEvent.ACTION_CANCEL:
                if (isDragging) {
                    onStopTrackingTouch();
                    setPressed(false);
                }
                invalidate(); // see above explanation
                break;
        }
        return true;
    }

    private boolean onTouchEventUseViewRotation(MotionEvent event) {
        boolean handled = super.onTouchEvent(event);

        if (handled) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    attemptClaimDrag(true);
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    attemptClaimDrag(false);
                    break;
            }
        }

        return handled;
    }

    private void trackTouchEvent(MotionEvent event) {
        final int paddingLeft = super.getPaddingLeft();
        final int paddingRight = super.getPaddingRight();
        final int height = getHeight();

        final int available = height - paddingLeft - paddingRight;
        int y = (int) event.getY();

        final float scale;
        float value = 0;

        switch (rotationAngle) {
            case ROTATION_ANGLE_CW_90:
                value = y - paddingLeft;
                break;
            case ROTATION_ANGLE_CW_270:
                value = (height - paddingLeft) - y;
                break;
        }

        if (value < 0 || available == 0) {
            scale = 0.0f;
        } else if (value > available) {
            scale = 1.0f;
        } else {
            scale = value / (float) available;
        }

        final int max = getMax();
        final float progress = scale * max;

        _setProgressFromUser((int) progress, true);
    }


    private void attemptClaimDrag(boolean active) {
        final ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(active);
        }
    }


    private void onStartTrackingTouch() {
        isDragging = true;
    }


    private void onStopTrackingTouch() {
        isDragging = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isEnabled()) {
            final boolean handled;
            int direction = 0;

            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    direction = (rotationAngle == ROTATION_ANGLE_CW_90) ? 1 : -1;
                    handled = true;
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    direction = (rotationAngle == ROTATION_ANGLE_CW_270) ? 1 : -1;
                    handled = true;
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    // move view focus to previous/next view
                    return false;
                default:
                    handled = false;
                    break;
            }

            if (handled) {
                final int keyProgressIncrement = getKeyProgressIncrement();
                int progress = getProgress();

                progress += (direction * keyProgressIncrement);

                if (progress >= 0 && progress <= getMax()) {
                    _setProgressFromUser(progress, true);
                }

                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        if (!useViewRotation()) {
            refreshThumb();
        }
    }

    private synchronized void _setProgressFromUser(int progress, boolean fromUser) {
        if (methodSetProgressFromUser == null) {
            try {
                Method m;
                m = ProgressBar.class.getDeclaredMethod("setProgress", int.class, boolean.class);
                m.setAccessible(true);
                methodSetProgressFromUser = m;
            } catch (NoSuchMethodException e) {
            }
        }

        if (methodSetProgressFromUser != null) {
            try {
                methodSetProgressFromUser.invoke(this, progress, fromUser);
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
        } else {
            super.setProgress(progress);
        }
        refreshThumb();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (useViewRotation()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            super.onMeasure(heightMeasureSpec, widthMeasureSpec);

            final ViewGroup.LayoutParams lp = getLayoutParams();

            if (isInEditMode() && (lp != null) && (lp.height >= 0)) {
                setMeasuredDimension(super.getMeasuredHeight(), lp.height);
            } else {
                setMeasuredDimension(super.getMeasuredHeight(), super.getMeasuredWidth());
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (useViewRotation()) {
            super.onSizeChanged(w, h, oldw, oldh);
        } else {
            super.onSizeChanged(h, w, oldh, oldw);
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        if (!useViewRotation()) {
            switch (rotationAngle) {
                case ROTATION_ANGLE_CW_90:
                    canvas.rotate(90);
                    canvas.translate(0, -super.getWidth());
                    break;
                case ROTATION_ANGLE_CW_270:
                    canvas.rotate(-90);
                    canvas.translate(-super.getHeight(), 0);
                    break;
            }
        }

        super.onDraw(canvas);
    }

    public int getRotationAngle() {
        return rotationAngle;
    }

    public void setRotationAngle(int angle) {
        if (!isValidRotationAngle(angle)) {
            throw new IllegalArgumentException("Invalid angle specified :" + angle);
        }

        if (rotationAngle == angle) {
            return;
        }

        rotationAngle = angle;

        if (useViewRotation()) {
            AbVerticalSeekBarWrapper wrapper = getWrapper();
            if (wrapper != null) {
                wrapper.applyViewRotation();
            }
        } else {
            requestLayout();
        }
    }

    private void refreshThumb() {
        onSizeChanged(super.getWidth(), super.getHeight(), 0, 0);
    }

    boolean useViewRotation() {
        final boolean isSupportedApiLevel = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
        final boolean inEditMode = isInEditMode();
        return isSupportedApiLevel && !inEditMode;
    }

    private AbVerticalSeekBarWrapper getWrapper() {
        final ViewParent parent = getParent();

        if (parent instanceof AbVerticalSeekBarWrapper) {
            return (AbVerticalSeekBarWrapper) parent;
        } else {
            return null;
        }
    }

    private static boolean isValidRotationAngle(int angle) {
        return (angle == ROTATION_ANGLE_CW_90 || angle == ROTATION_ANGLE_CW_270);
    }
}
