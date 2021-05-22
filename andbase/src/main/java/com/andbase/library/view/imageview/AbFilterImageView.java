package com.andbase.library.view.imageview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.andbase.library.view.listener.AbOnClickListener;
import com.andbase.library.view.listener.AbOnFocusChangeListener;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 滤镜效果的ImageView，获取焦点产生滤镜效果
 */
public class AbFilterImageView extends AppCompatImageView {

    public AbOnClickListener onClickListener;
    private AbOnFocusChangeListener onFocusChangeListener;
    private int touchFlag = 0;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){
                setFilter();
            }else{
                removeFilter();
            }

        }
    };

    public AbFilterImageView(Context context) {
        super(context);
        initFocusable();
    }

    public AbFilterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFocusable();
    }

    public AbFilterImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFocusable();
    }

    public void initFocusable(){

        this.setClickable(true);
        this.setFocusable(true);

        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    handler.sendEmptyMessage(0);

                }else{
                    handler.sendEmptyMessage(1);
                }
                if(onFocusChangeListener!=null){
                    onFocusChangeListener.onFocusChange(v,hasFocus);
                }
            }
        });

        this.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(touchFlag == 1){
                    return true;
                }
                touchFlag = 1;
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    handler.sendEmptyMessage(0);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    handler.sendEmptyMessage(1);
                    if(onClickListener!=null){
                        onClickListener.onClick(v);
                    }
                }else if(event.getAction() == MotionEvent.ACTION_CANCEL){
                    handler.sendEmptyMessage(1);
                }
                touchFlag = 0;
                return true;
            }

        });

    }

    /**
     *   设置滤镜
     */
    private void setFilter() {
        //先获取设置的src图片
        Drawable drawable = getDrawable();
        //当src图片为Null，获取背景图片
        if (drawable==null) {
            drawable = getBackground();
            if(drawable!=null){
                //设置滤镜
                drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                this.setBackgroundDrawable(drawable);
            }

        }else{
            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            this.setImageDrawable(drawable);
        }


    }

    /**
     *   清除滤镜
     */
    private void removeFilter() {
        //先获取设置的src图片
        Drawable drawable = getDrawable();
        //当src图片为Null，获取背景图片
        if (drawable == null) {
            drawable = getBackground();
            if(drawable!=null){
                //清除滤镜
                drawable.clearColorFilter();
                this.setBackgroundDrawable(drawable);
            }

        }else{
            //清除滤镜
            drawable.clearColorFilter();
            this.setImageDrawable(drawable);
        }

    }

    /**
     * 设置焦点事件
     * @param onFocusChangeListener
     */
    public void setOnFocusChangeListener(AbOnFocusChangeListener onFocusChangeListener) {
        this.onFocusChangeListener = onFocusChangeListener;
    }



    /**
     * 设置点击事件
     * @param onClickListener
     */
    public void setOnClickListener(AbOnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
