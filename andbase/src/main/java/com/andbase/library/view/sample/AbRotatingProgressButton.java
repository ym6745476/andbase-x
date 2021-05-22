package com.andbase.library.view.sample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.appcompat.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.ImageView;

public class AbRotatingProgressButton extends AppCompatImageButton {

    private Context context;
    private AbRotatingProgressDrawable coverDrawable;
    private int percent = 8, color= Color.rgb(255,89,131);
    private float progress = 0f;
    private boolean isRotation = false;

    public AbRotatingProgressButton(Context context) {
        super(context);
        init(context);
    }

    public AbRotatingProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AbRotatingProgressButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context){
        this.context = context;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        setMaxImageSize();
    }

    public void setMaxImageSize() {
        try {
           this.setScaleType(ImageView.ScaleType.FIT_XY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        postInvalidate();
    }


    public void config(int percent, int color) {
        this.percent = percent;
        this.color = color;
        this.isRotation = false;
        config();
    }

    public void config() {
        if (coverDrawable != null) {
            coverDrawable.setProgressWidthPercent(percent);
            coverDrawable.setProgressColor(color);
            coverDrawable.setProgress(progress);
            coverDrawable.rotate(isRotation);
            setMaxImageSize();
        }
    }

    /**
     * 设置进度
     * @param progress
     */
    public void setProgress(float progress) {
        this.progress = progress;
        if (coverDrawable != null) {
            coverDrawable.setProgress(progress);
        }
    }

    /**
     * 设置按钮背景
     * @param drawable
     */
    public void setCoverDrawable(Drawable drawable) {
        this.coverDrawable = new AbRotatingProgressDrawable(drawable);
        config();
        setBackgroundDrawable(this.coverDrawable);
        postInvalidate();
    }

    public void setCover(Bitmap bitmap) {
        coverDrawable = new AbRotatingProgressDrawable(bitmap);
        config();
        setBackgroundDrawable(this.coverDrawable);
        postInvalidate();
    }

    public void rotate(boolean rotate) {
        coverDrawable.rotate(rotate);
        isRotation = rotate;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable state = super.onSaveInstanceState();
        SavedState savedState = new SavedState(state);
        savedState.childrenStates = new SparseArray();
        savedState.childrenStates.put(1,isRotation);
        savedState.childrenStates.put(2,progress);
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        if(state instanceof SavedState){
            SavedState savedState = (SavedState) state;
            isRotation = (boolean)savedState.childrenStates.get(1);
            progress = (float)savedState.childrenStates.get(2);
            requestLayout();
        }
    }

    class SavedState extends BaseSavedState {
        SparseArray childrenStates;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in, ClassLoader classLoader) {
            super(in);
            childrenStates = in.readSparseArray(classLoader);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeSparseArray(childrenStates);
        }

        public final ClassLoaderCreator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source, ClassLoader loader) {
                return new SavedState(source, loader);
            }

            @Override
            public SavedState createFromParcel(Parcel source) {
                return createFromParcel(null);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}
