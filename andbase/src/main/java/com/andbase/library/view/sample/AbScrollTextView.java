
package com.andbase.library.view.sample;

import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 跑马灯一直跑
 */
public class AbScrollTextView extends AppCompatTextView {


	public AbScrollTextView(Context context) {
		this(context,null);
	}

	public AbScrollTextView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}


	public AbScrollTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		//关键
		this.setSingleLine(true);

	}

	/**
	 * 设置为焦点，能一直滚动.
	 */
	@Override
	public boolean isFocused() {
		return true;
	}

}
