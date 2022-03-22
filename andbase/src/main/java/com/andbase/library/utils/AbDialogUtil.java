
package com.andbase.library.utils;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import androidx.fragment.app.FragmentActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andbase.library.R;
import com.andbase.library.view.dialog.AbDialogFragment;
import com.andbase.library.view.imageview.AbSuitableImageView;
import com.bumptech.glide.Glide;


/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info Dialog工具类
 */

public class AbDialogUtil {
	
	/** dialog 标记*/
	public static String dialogTag = "dialog";
	public static String loadingDialogTag = "loadingDialog";
	public static int ThemeHoloLightDialog = android.R.style.Theme_Holo_Light_Dialog;
	
	/**
	 * 显示一个全屏对话框
	 * @param view
	 * @return
	 */
	public static AbDialogFragment showFullDialog(View view) {
		AbDialogFragment dialogFragment = null;
		try{
			FragmentActivity activity = (FragmentActivity)view.getContext();
			dialogFragment = AbDialogFragment.newInstance(DialogFragment.STYLE_NORMAL,android.R.style.Theme_Black_NoTitleBar_Fullscreen,Gravity.CENTER);
			dialogFragment.setContentView(view);
			FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
			// 指定一个系统转场动画
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			dialogFragment.show(ft, dialogTag);
		}catch(Exception e){
			e.printStackTrace();
		}
        return dialogFragment;
    }

	/**
	 * 显示一个面板
	 * @param view
	 * @return
	 */
	public static AbDialogFragment showPanel(View view) {
		AbDialogFragment dialogFragment = null;
		try{
			FragmentActivity activity = (FragmentActivity)view.getContext();
			dialogFragment = AbDialogFragment.newInstance(DialogFragment.STYLE_NORMAL,android.R.style.Theme_Panel,Gravity.TOP);
			dialogFragment.setContentView(view);
			FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
			// 指定一个系统转场动画
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			dialogFragment.show(ft, dialogTag);
		}catch(Exception e){
			e.printStackTrace();
		}
		return dialogFragment;
	}

	/**
	 * 显示一个居中的对话框.
	 * @param view
	 */
	public static AbDialogFragment showDialog(View view) {
		return showDialog(view,Gravity.CENTER);
	}

	/**
	 *
	 * 显示一个指定位置对话框.
	 * @param view
	 * @param gravity 位置
	 * @return
	 */
	public static AbDialogFragment showDialog(View view, int gravity) {
		return showDialog(view,gravity,dialogTag,ThemeHoloLightDialog);
    }

	/**
	 * 显示一个指定位置的对话框.
	 * @param view
	 * @param tag
	 */
	public static AbDialogFragment showDialog(View view, int gravity, String tag) {
		return showDialog(view,gravity,tag,ThemeHoloLightDialog);
	}
	
	/**
	 * 
	 * 自定义的对话框面板.
	 * @param view    View
	 * @param gravity 位置
	 * @param style   样式 ThemeHoloLightDialog  ThemeLightPanel
	 * @return
	 */
	private static AbDialogFragment showDialog(View view, int gravity, String tag, int style) {
		AbDialogFragment dialogFragment = null;
		try{
			FragmentActivity activity = (FragmentActivity)view.getContext();
			dialogFragment = AbDialogFragment.newInstance(DialogFragment.STYLE_NO_TITLE,style,gravity);
			dialogFragment.setContentView(view);
			FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
			// 指定一个系统转场动画
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			dialogFragment.show(ft, tag);
		}catch(Exception e){
			e.printStackTrace();
		}
		return dialogFragment;
    }

    /**
     * 显示默认样式加载框
     * @param context the context
     */
    public static AbDialogFragment showLoadingDialog(Context context, int indeterminateDrawable, String message) {
        View view = View.inflate(context,R.layout.ab_view_loading_dialog,null);
        TextView textView = (TextView)view.findViewById(R.id.loading_text);
        ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.loading_progress);
        if(message != null){
            textView.setText(message);
        }
        if(indeterminateDrawable > 0){
            progressBar.setIndeterminateDrawable(context.getResources().getDrawable(indeterminateDrawable));
        }
        return showLoadingDialog(view);
    }

	/**
	 * 显示gif加载框.
	 * @param context the context
	 */
	public static AbDialogFragment showLoadingDialogGif(Context context, int indeterminateDrawable, String message) {

		View view = View.inflate(context,R.layout.ab_view_loading_dialog_gif,null);
		TextView textView = (TextView)view.findViewById(R.id.loading_text);
		ImageView imageView = (ImageView)view.findViewById(R.id.loading_progress);
		if(message != null){
			textView.setText(message);
		}else{
			textView.setVisibility(View.GONE);
		}
		if(indeterminateDrawable > 0){
			Glide.with(context).load(indeterminateDrawable).into(imageView);
		}
		return showLoadingDialog(view);
	}

	/**
	 * 显示加载框.
	 * @param view View
	 */
	public static AbDialogFragment showLoadingDialog(View view) {
		AbDialogFragment dialogFragment = null;
		try{
			FragmentActivity activity = (FragmentActivity)view.getContext();
			dialogFragment = AbDialogFragment.newInstance(DialogFragment.STYLE_NORMAL,R.style.Dialog_Loading,Gravity.CENTER);
			dialogFragment.setContentView(view);
			FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			dialogFragment.show(ft, loadingDialogTag);
		}catch(Exception e){
			e.printStackTrace();
		}
		return dialogFragment;
	}

	public static AbDialogFragment showMessageDialog(final Context context,String title,String message,String okButtonText,String cancelButtonText,
														final View.OnClickListener onOkClickListener,final View.OnClickListener onCancelClickListener){
		View view = View.inflate(context, R.layout.ab_view_message_dialog,null);
		Button cancelButton = (Button)view.findViewById(R.id.dialog_button_cancel);
		if(cancelButtonText == null){
			cancelButton.setVisibility(View.GONE);
		}else{
			cancelButton.setText(cancelButtonText);
		}
		Button okButton = (Button)view.findViewById(R.id.dialog_button_ok);
		okButton.setText(okButtonText);

		TextView titleText =  (TextView)view.findViewById(R.id.dialog_title);
		LinearLayout titleTextLayout =  (LinearLayout)view.findViewById(R.id.dialog_title_layout);
		if(title == null){
			titleTextLayout.setVisibility(View.GONE);
		}else{
			titleText.setText(title);
		}
		TextView messageText =  (TextView)view.findViewById(R.id.dialog_message);
		messageText.setText(message);

		ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams)messageText.getLayoutParams();
		lp.width = AbAppUtil.getDisplayMetrics(context).widthPixels / 3 * 2;
		messageText.setLayoutParams(lp);

		okButton.setOnClickListener(onOkClickListener);
		cancelButton.setOnClickListener(onCancelClickListener);
		return AbDialogUtil.showDialog(view);
	}

	public static AbDialogFragment showConfirmDialog(final Context context,String title, String message,String okButtonText,View.OnClickListener onOkClickListener){
		AbDialogFragment fragment = AbDialogUtil.showMessageDialog(context,title,message,okButtonText,null,onOkClickListener,null);
		fragment.setCancelable(false);
		return fragment;
	}

	public static AbDialogFragment showListDialog(final Context context,String[] list,int defaultPosition,final AdapterView.OnItemClickListener onItemClickListener){
		View view = View.inflate(context, R.layout.ab_view_list_dialog,null);
		final ListView listView = (ListView)view.findViewById(R.id.list);
		listView.setAdapter(new ArrayAdapter<String>(context,R.layout.ab_view_list_item_checked, list));


		listView.setItemChecked(defaultPosition,true);

		Button buttonOk = (Button)view.findViewById(R.id.dialog_button2);
		Button buttonCancel = (Button)view.findViewById(R.id.dialog_button1);

		buttonOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int position = listView .getCheckedItemPosition();
				onItemClickListener.onItemClick(null,v,position,position);
				AbDialogUtil.removeDialog(context);

			}
		});
		buttonCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AbDialogUtil.removeDialog(context);
			}
		});

		return AbDialogUtil.showDialog(view);

	}

	public static AbDialogFragment showBeautyDialog(final Context context, String htmlMessage){
		View view = View.inflate(context,R.layout.ab_view_beauty_dialog, null);
		AbDialogFragment fragment = AbDialogUtil.showDialog(view);
		fragment.setCancelable(false);
		TextView textView = (TextView)view.findViewById(R.id.dialog_message);
		textView.setText(Html.fromHtml(htmlMessage));
		AbSuitableImageView imageView = (AbSuitableImageView)view.findViewById(R.id.dialog_image);
		imageView.setVisibility(View.GONE);
		Button closeButton = (Button)view.findViewById(R.id.dialog_close_button);
		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				AbDialogUtil.removeDialog(context);
			}
		});

		return fragment;

	}

	public static AbDialogFragment showBeautyDialog(final Context context, String message, int imageResId){
		View view = View.inflate(context,R.layout.ab_view_beauty_dialog, null);
		AbDialogFragment fragment = AbDialogUtil.showDialog(view);
		fragment.setCancelable(false);
		TextView textView = (TextView)view.findViewById(R.id.dialog_message);
		textView.setText(message);
		AbSuitableImageView imageView = (AbSuitableImageView)view.findViewById(R.id.dialog_image);
		imageView.setImageResource(imageResId);
		Button closeButton = (Button)view.findViewById(R.id.dialog_close_button);
		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				AbDialogUtil.removeDialog(context);
			}
		});
		return fragment;
	}

	/**
	 * 移除Fragment.
	 * @param context the context
	 */
	public static void removeDialog(final Context context){
		removeDialog(context,dialogTag);
	}

	/**
	 * 移除Fragment.
	 * @param context the context
	 */
	public static void removeLoadingDialog(final Context context){
		removeDialog(context,loadingDialogTag);
	}

	/**
	 * 移除Fragment.
	 * @param context the context
	 * @param tag the tag
	 */
	public static void removeDialog(final Context context,String tag){
		try {
			FragmentActivity activity = (FragmentActivity)context;
			FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
			// 指定一个系统转场动画
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
			Fragment prev = activity.getFragmentManager().findFragmentByTag(tag);
			if (prev != null) {
				AbLogUtil.i(activity,"removeDialog:" + tag);
				ft.remove(prev);
			}
			//不能加入到back栈
			//ft.addToBackStack(null);
			ft.commit();
		} catch (Exception e) {
			//可能有Activity已经被销毁的异常
			e.printStackTrace();
		}
	}

	/**
	 * 移除Fragment和View
	 * @param view
	 */
	public static void removeDialog(View view){
		removeDialog(view.getContext());
		AbViewUtil.removeSelfFromParent(view);
	}

	public static void removeDialog(View view,String tag){
		removeDialog(view.getContext(),tag);
		AbViewUtil.removeSelfFromParent(view);
	}

}
