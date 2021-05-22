package com.andbase.library.view.sample;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.andbase.library.R;
import com.andbase.library.view.listener.AbOnScrollChangedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2017/4/24 13:27
 * Email 396196516@qq.com
 * Info 返回滚动距离的ScrollView
 */
public class AbScrollView extends ScrollView {

    private List<AbOnScrollChangedListener> scrollChangedListeners;

    public AbScrollView(Context context) {
        this(context, null);
    }

    public AbScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addOnScrollChangedListener(AbOnScrollChangedListener onScrollChangedListener) {
        if(this.scrollChangedListeners == null) {
            this.scrollChangedListeners = new ArrayList();
        }
        this.scrollChangedListeners.add(onScrollChangedListener);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (this.scrollChangedListeners != null && this.scrollChangedListeners.size() > 0) {
            for(AbOnScrollChangedListener listener:this.scrollChangedListeners){
                if(listener!=null){
                    listener.onScrollChanged(l, t, oldl, oldt);
                }

            }

        }
    }


    public float getScrollAlpha(int top){
        //0 100  0 -1
        int offset = top;
        if (top > 200) {
            offset = 200;
        }
        float alpha = offset / 200f;
        if (alpha == 0) {
            return 0f;
        } else if (alpha == 1) {
            return alpha;
        } else {
            return alpha;
        }

    }

}


