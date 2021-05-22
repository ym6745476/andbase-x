package com.andbase.library.app.base;


import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andbase.library.R;
import com.andbase.library.app.global.AbActivityManager;
import com.andbase.library.okhttp.AbOkHttpManager;
import com.andbase.library.utils.AbAppUtil;
import com.andbase.library.utils.AbLogUtil;
import com.andbase.library.utils.AbViewUtil;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/16 13:27
 * Email 396196516@qq.com
 * Info 所有Activity要继承这个父类，便于统一管理
 */
public abstract class AbBaseActivity extends AppCompatActivity {

	/** 类名 */
	public String pageClassName = null;

	public View rootView = null;

    /** 网络请求 */
	public AbOkHttpManager httpManager = null;

	/** Can not perform this action after onSaveInstanceState */
	public boolean saveInstanceState;

	/** 主色调 */
	public int colorPrimary;
	public int colorAccent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.pageClassName = this.getClass().getSimpleName();

		AbLogUtil.d(this,"------------------onCreate-----------------------");

		this.httpManager = new AbOkHttpManager(this,AbOkHttpManager.CACHAE_TYPE_NOCACHE);

		AbActivityManager.getInstance().addActivity(this);

		//状态栏颜色
		AbAppUtil.setWindowStatusBarTransparent(this,false);

		//默认主色调
		this.colorPrimary = getResources().getColor(R.color.concise);
		this.colorAccent = getResources().getColor(R.color.concise_dark);

    }

	public void setToolbarView(String title,boolean showBack){
		TextView titleView = (TextView)this.findViewById(R.id.title_text);
		ImageView backBtn = (ImageView)this.findViewById(R.id.back_btn);
		if(title == null){
			title = "";
		}
		if(titleView!=null){
			titleView.setText(title);
		}

		if(!showBack){
			backBtn.setVisibility(View.GONE);
		}else{
			backBtn.setVisibility(View.VISIBLE);
			backBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}

	}

	public void setToolbarView(String title, View.OnClickListener onClickListener){
		TextView titleView = (TextView)this.findViewById(R.id.title_text);
		ImageView backBtn = (ImageView)this.findViewById(R.id.back_btn);
		if(title == null){
			title = "";
		}
		if(titleView!=null){
			titleView.setText(title);
		}
		backBtn.setVisibility(View.VISIBLE);
		backBtn.setOnClickListener(onClickListener);
	}

	/**
	 * 万能屏幕适配的setContentView方法
	 * @param layoutResID  布局ID
	 */
	public void setAbContentView(@LayoutRes int layoutResID) {
		View contentView = View.inflate(this,layoutResID,null);
		AbViewUtil.scaleContentView(contentView);
		setContentView(contentView);
	}

	/**
	 * 保存rootView
	 * @param layoutResID
	 */
	public void setContentView(@LayoutRes int layoutResID) {
		rootView = View.inflate(this,layoutResID,null);
		setContentView(rootView);
	}

	/**
	 * 更新主题
	 */
	public void updateAppTheme(){
		this.recreate();
	}

	/**
	 * 返回默认
	 * @param view
	 */
	public void back(View view){
		finish();
	}

	/**
	 * 拦截返回键
	 */
	@Override
	public void onBackPressed() {
		back(null);
	}

	/**
	 * 拦截返回键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 是否触发按键为back键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onBackPressed();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		saveInstanceState = true;
		//触发home之后activity销毁问题
        AbLogUtil.d(this,"------------------onSaveInstanceState-----------------------");
		outState.putString("Activity", pageClassName);
		super.onSaveInstanceState(outState);
	}


	@Override
	protected void onStart() {
		saveInstanceState = false;
        AbLogUtil.d(this,"------------------onStart-----------------------");
        super.onStart();
    }

    @Override
	protected void onResume() {
		saveInstanceState = false;
        AbLogUtil.d(this,"------------------onResume-----------------------");
        super.onResume();
    }

    @Override
	protected void onPause() {
        AbLogUtil.d(this,"------------------onResume-----------------------");
        super.onPause();
    }

    @Override
	protected void onStop() {
		saveInstanceState = true;
        AbLogUtil.d(this,"------------------onStop-----------------------");
        AbLogUtil.prepareLog(this);
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        AbLogUtil.d(this,"------------------onLowMemory-----------------------");
        super.onLowMemory();
    }


    @Override
	protected void onDestroy() {
        AbLogUtil.d(this,"------------------onDestroy-----------------------");
        AbLogUtil.d(this,"onDestroy",true);
        super.onDestroy();
    }

    /**
	 * 结束
	 */
	@Override
	public void finish() {
		AbLogUtil.d(this,"------------------finish-----------------------");
		AbActivityManager.getInstance().onlyRemoveActivity(this);
		if(httpManager!=null){
			httpManager.cancelAll();
			httpManager = null;
		}

		super.finish();
	}

	/**
	 * 显示页面
	 * @param rootView
	 * @param layoutResID  布局ID
	 */
	public View showPageView(View rootView,@LayoutRes int layoutResID) {
		View pageView = View.inflate(this,layoutResID,null);
		((ViewGroup)rootView).addView(pageView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
		return pageView;
	}

	/**
	 * 隐藏页面
	 * @param rootView
	 */
	public void hidePageView(View rootView,View pageView) {
		if(pageView !=null){
			((ViewGroup)rootView).removeView(pageView);
		}
	}
}

