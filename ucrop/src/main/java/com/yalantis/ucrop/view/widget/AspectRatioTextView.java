package com.yalantis.ucrop.view.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import com.yalantis.ucrop.R;
import com.yalantis.ucrop.model.AspectRatio;
import com.yalantis.ucrop.view.CropImageView;

import java.util.Locale;

public class AspectRatioTextView extends AppCompatTextView {

    private Context context;
    private final Rect mCanvasClipBounds = new Rect();
    private Paint mDotPaint;
    private int mDotSize;
    private float mAspectRatio;

    private String mAspectRatioTitle;
    private float mAspectRatioX, mAspectRatioY;

    public AspectRatioTextView(Context context) {
        this(context, null);
    }

    public AspectRatioTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AspectRatioTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ucrop_AspectRatioTextView);
        init(a);
    }

    public void setActiveColor(@ColorInt int activeColor) {
        applyActiveColor(activeColor);
        invalidate();
    }

    public void setAspectRatio(@NonNull AspectRatio aspectRatio) {
        mAspectRatioTitle = aspectRatio.getAspectRatioTitle();
        mAspectRatioX = aspectRatio.getAspectRatioX();
        mAspectRatioY = aspectRatio.getAspectRatioY();

        if (mAspectRatioX == CropImageView.SOURCE_IMAGE_ASPECT_RATIO || mAspectRatioY == CropImageView.SOURCE_IMAGE_ASPECT_RATIO) {
            mAspectRatio = CropImageView.SOURCE_IMAGE_ASPECT_RATIO;
        } else {
            mAspectRatio = mAspectRatioX / mAspectRatioY;
        }

        setTitle();
    }

    public float getAspectRatio(boolean toggleRatio) {
        if (toggleRatio) {
            toggleAspectRatio();
            setTitle();
        }
        return mAspectRatio;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isSelected()) {
            canvas.getClipBounds(mCanvasClipBounds);
            canvas.drawCircle((mCanvasClipBounds.right - mCanvasClipBounds.left) / 2.0f, mCanvasClipBounds.bottom - mDotSize,
                    mDotSize / 2, mDotPaint);
        }
    }

    private void init(@NonNull TypedArray a) {
        setGravity(Gravity.CENTER_HORIZONTAL);

        mAspectRatioTitle = a.getString(R.styleable.ucrop_AspectRatioTextView_ucrop_artv_ratio_title);
        mAspectRatioX = a.getFloat(R.styleable.ucrop_AspectRatioTextView_ucrop_artv_ratio_x, CropImageView.SOURCE_IMAGE_ASPECT_RATIO);
        mAspectRatioY = a.getFloat(R.styleable.ucrop_AspectRatioTextView_ucrop_artv_ratio_y, CropImageView.SOURCE_IMAGE_ASPECT_RATIO);

        if (mAspectRatioX == CropImageView.SOURCE_IMAGE_ASPECT_RATIO || mAspectRatioY == CropImageView.SOURCE_IMAGE_ASPECT_RATIO) {
            mAspectRatio = CropImageView.SOURCE_IMAGE_ASPECT_RATIO;
        } else {
            mAspectRatio = mAspectRatioX / mAspectRatioY;
        }

        mDotSize = getContext().getResources().getDimensionPixelSize(R.dimen.ucrop_size_dot_scale_text_view);
        mDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDotPaint.setStyle(Paint.Style.FILL);

        setTitle();

        applyActiveColor(Color.BLACK);

        a.recycle();
    }

    private void applyActiveColor(@ColorInt int activeColor) {
        if (mDotPaint != null) {
            mDotPaint.setColor(activeColor);
        }
        ColorStateList textViewColorStateList = (ColorStateList)context.getResources().getColorStateList(R.color.ucrop_text_view_selector);
        setTextColor(textViewColorStateList);
    }

    private void toggleAspectRatio() {
        if (mAspectRatio != CropImageView.SOURCE_IMAGE_ASPECT_RATIO) {
            float tempRatioW = mAspectRatioX;
            mAspectRatioX = mAspectRatioY;
            mAspectRatioY = tempRatioW;
            mAspectRatio = mAspectRatioX / mAspectRatioY;
        }
    }

    private void setTitle() {
        if (!TextUtils.isEmpty(mAspectRatioTitle)) {
            setText(mAspectRatioTitle);
        } else {
            setText(String.format(Locale.US, "%d:%d", (int) mAspectRatioX, (int) mAspectRatioY));
        }
    }

}
