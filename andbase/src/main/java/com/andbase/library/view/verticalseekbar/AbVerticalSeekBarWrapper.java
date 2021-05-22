package com.andbase.library.view.verticalseekbar;

import android.content.Context;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class AbVerticalSeekBarWrapper extends FrameLayout {
    public AbVerticalSeekBarWrapper(Context context) {
        this(context, null, 0);
    }

    public AbVerticalSeekBarWrapper(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbVerticalSeekBarWrapper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (useViewRotation()) {
            onSizeChangedUseViewRotation(w, h, oldw, oldh);
        } else {
            onSizeChangedTraditionalRotation(w, h, oldw, oldh);
        }
    }

    private void onSizeChangedTraditionalRotation(int w, int h, int oldw, int oldh) {
        final AbVerticalSeekBar seekBar = getChildSeekBar();

        if (seekBar != null) {
            final LayoutParams lp = (LayoutParams) seekBar.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = h;
            seekBar.setLayoutParams(lp);

            seekBar.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int seekBarWidth = seekBar.getMeasuredWidth();
            seekBar.measure(
                    MeasureSpec.makeMeasureSpec(w, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));

            lp.gravity = Gravity.TOP | Gravity.LEFT;
            lp.leftMargin = (w - seekBarWidth) / 2;
            seekBar.setLayoutParams(lp);
        }

        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void onSizeChangedUseViewRotation(int w, int h, int oldw, int oldh) {
        final AbVerticalSeekBar seekBar = getChildSeekBar();

        if (seekBar != null) {
            seekBar.measure(
                    MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(w, MeasureSpec.AT_MOST));
        }

        applyViewRotation(w, h);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final AbVerticalSeekBar seekBar = getChildSeekBar();
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        if ((seekBar != null) && (widthMode != MeasureSpec.EXACTLY)) {
            final int seekBarWidth;
            final int seekBarHeight;

            if (useViewRotation()) {
                seekBar.measure(heightMeasureSpec, widthMeasureSpec);
                seekBarWidth = seekBar.getMeasuredHeight();
                seekBarHeight = seekBar.getMeasuredWidth();
            } else {
                seekBar.measure(widthMeasureSpec, heightMeasureSpec);
                seekBarWidth = seekBar.getMeasuredWidth();
                seekBarHeight = seekBar.getMeasuredHeight();
            }

            final int measuredWidth = ViewCompat.resolveSizeAndState(seekBarWidth, widthMeasureSpec, 0);
            final int measuredHeight = ViewCompat.resolveSizeAndState(seekBarHeight, heightMeasureSpec, 0);

            setMeasuredDimension(measuredWidth, measuredHeight);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /*package*/ void applyViewRotation() {
        applyViewRotation(getWidth(), getHeight());
    }

    private void applyViewRotation(int w, int h) {
        final AbVerticalSeekBar seekBar = getChildSeekBar();

        if (seekBar != null) {
            final int rotationAngle = seekBar.getRotationAngle();
            final int seekBarMeasuredWidth = seekBar.getMeasuredWidth();
            final int seekBarMeasuredHeight = seekBar.getMeasuredHeight();
            final ViewGroup.LayoutParams lp = seekBar.getLayoutParams();

            lp.width = h;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            seekBar.setLayoutParams(lp);

            ViewCompat.setPivotX(seekBar, 0);
            ViewCompat.setPivotY(seekBar, 0);

            switch (rotationAngle) {
                case AbVerticalSeekBar.ROTATION_ANGLE_CW_90:
                    ViewCompat.setRotation(seekBar, 90);
                    ViewCompat.setTranslationY(seekBar, 0);
                    ViewCompat.setTranslationX(seekBar, (w + seekBarMeasuredHeight) / 2);
                    break;
                case AbVerticalSeekBar.ROTATION_ANGLE_CW_270:
                    ViewCompat.setRotation(seekBar, 270);
                    ViewCompat.setTranslationY(seekBar, seekBarMeasuredWidth);
                    ViewCompat.setTranslationX(seekBar, (w - seekBarMeasuredHeight) / 2);
                    break;
            }
        }
    }

    private AbVerticalSeekBar getChildSeekBar() {
        final View child = (getChildCount() > 0) ? getChildAt(0) : null;
        return (child instanceof AbVerticalSeekBar) ? (AbVerticalSeekBar) child : null;
    }

    private boolean useViewRotation() {
        final AbVerticalSeekBar seekBar = getChildSeekBar();
        if (seekBar != null) {
            return seekBar.useViewRotation();
        } else {
            return false;
        }
    }
}
