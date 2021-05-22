
package com.andbase.library.view.dialog;

import android.animation.Animator;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/16 13:27
 * Email 396196516@qq.com
 * Info 弹出框
 */
public class AbDialogFragment extends DialogFragment {
	
	/** 主题. */
	private int theme;
	
	/** 样式. */
	private int style;
	
	/** 内容View. */
	private View contentView;
	
	/** 用户取消监听器. */
	private DialogInterface.OnCancelListener onCancelListener = null;
	
	/** 用户隐藏监听器. */
	private DialogInterface.OnDismissListener onDismissListener = null;
	
	/** 弹出位置. */
	private int gravity;
	
	
	/**
	 * 创建实例
	 * @param style the style
	 * @param theme the theme
	 * @param gravity the gravity
	 * @return the ab sample dialog fragment
	 */
	public static AbDialogFragment newInstance(int style, int theme, int gravity) {
		AbDialogFragment f = new AbDialogFragment();
		Bundle args = new Bundle();
		args.putInt("style", style);
		args.putInt("theme", theme);
		args.putInt("gravity", gravity);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		style = getArguments().getInt("style");
		theme = getArguments().getInt("theme");
		gravity = getArguments().getInt("gravity");
		setStyle(style, theme);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		// 用户中断
		if(onCancelListener != null){
			onCancelListener.onCancel(dialog);
		}
		
		super.onCancel(dialog);
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		// 用户隐藏
		if(onDismissListener != null){
		    onDismissListener.onDismiss(dialog);
		}
		super.onDismiss(dialog);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return contentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Window window = getDialog().getWindow();
		WindowManager.LayoutParams attributes = window.getAttributes();
		attributes.gravity = gravity;
		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		window.setBackgroundDrawableResource(android.R.color.transparent);
		super.onActivityCreated(savedInstanceState);
	}

	/**
	 * 获取Dialog的ContentView.
	 * @return the content view
	 */
	public View getContentView() {
		return contentView;
	}

	/**
	 * 设置Dialog的ContentView.
	 * @param contentView the new content view
	 */
	public void setContentView(View contentView) {
		this.contentView = contentView;
	}

	@Override
	public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
		return super.onCreateAnimator(transit, enter, nextAnim);
	}
	
}
