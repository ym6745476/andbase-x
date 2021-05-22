package com.andbase.library.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andbase.library.R;
import com.andbase.library.utils.AbImageUtil;
import com.andbase.library.utils.AbStrUtil;
import com.andbase.library.utils.AbViewUtil;


/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 提示工具类
 */

public class AbToastUtil {

    private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#FFFFFF");
    private static final int ERROR_COLOR = Color.parseColor("#FF5252");
    private static final int INFO_COLOR = Color.parseColor("#01BCC3");
    private static final int SUCCESS_COLOR = Color.parseColor("#00c853");
    private static final int WARNING_COLOR = Color.parseColor("#FFAB40");
    private static final String TOAST_TYPEFACE = "sans-serif-condensed";

    /**
     * Toast提示文本.
     *
     * @param context
     * @param text    文本
     */
    public static void showToast(Context context, String text) {
        normal(context, text);
    }

    /**
     * Toast提示文本.
     *
     * @param context
     * @param resId   文本的资源ID
     */
    public static void showToast(Context context, int resId) {
        normal(context, context.getResources().getText(resId));
    }

    public static Toast normal(Context context,CharSequence message) {
        return normal(context, message, Toast.LENGTH_SHORT, null, false);
    }

    public static Toast normal(Context context, CharSequence message, Drawable icon) {
        return normal(context, message, Toast.LENGTH_SHORT, icon, true);
    }

    public static Toast normal(Context context,CharSequence message, int duration) {
        return normal(context, message, duration, null, false);
    }

    public static Toast normal(Context context, CharSequence message, int duration,
                               Drawable icon) {
        return normal(context, message, duration, icon, true);
    }

    public static Toast normal(Context context, CharSequence message, int duration,
                               Drawable icon, boolean withIcon) {
        return custom(context, message, icon, DEFAULT_TEXT_COLOR, duration, withIcon);
    }

    public static Toast warning(Context context, CharSequence message) {
        return warning(context, message, Toast.LENGTH_SHORT, true);
    }

    public static Toast warning(Context context, CharSequence message, int duration) {
        return warning(context, message, duration, true);
    }

    public static Toast warning(Context context, CharSequence message, int duration, boolean withIcon) {
        return custom(context, message, AbViewUtil.getDrawable(context, R.drawable.ic_toast_warning),
                DEFAULT_TEXT_COLOR, WARNING_COLOR, duration, withIcon, true);
    }

    public static Toast info(Context context, CharSequence message) {
        return info(context, message, Toast.LENGTH_SHORT, true);
    }

    public static Toast info(Context context, CharSequence message, int duration) {
        return info(context, message, duration, true);
    }

    public static Toast info(Context context, CharSequence message, int duration, boolean withIcon) {
        return custom(context, message, AbViewUtil.getDrawable(context, R.drawable.ic_toast_info),
                DEFAULT_TEXT_COLOR, INFO_COLOR, duration, withIcon, true);
    }

    public static Toast success(Context context, CharSequence message) {
        return success(context, message, Toast.LENGTH_SHORT, true);
    }

    public static Toast success(Context context, CharSequence message, int duration) {
        return success(context, message, duration, true);
    }

    public static Toast success(Context context, CharSequence message, int duration, boolean withIcon) {
        return custom(context, message, AbViewUtil.getDrawable(context, R.drawable.ic_toast_success),
                DEFAULT_TEXT_COLOR, SUCCESS_COLOR, duration, withIcon, true);
    }

    public static Toast error(Context context, CharSequence message) {
        return error(context, message, Toast.LENGTH_SHORT, true);
    }

    public static Toast error(Context context, CharSequence message, int duration) {
        return error(context, message, duration, true);
    }

    public static Toast error(Context context, CharSequence message, int duration, boolean withIcon) {
        return custom(context, message, AbViewUtil.getDrawable(context, R.drawable.ic_toast_failure),
                DEFAULT_TEXT_COLOR, ERROR_COLOR, duration, withIcon, true);
    }

    public static Toast custom(Context context, CharSequence message, Drawable icon,
                               int textColor, int duration, boolean withIcon) {
        return custom(context, message, icon, textColor, -1, duration, withIcon, false);
    }

    public static Toast custom(Context context, CharSequence message,int iconRes,
                               int textColor, int tintColor, int duration,
                               boolean withIcon, boolean shouldTint) {
        return custom(context, message, AbViewUtil.getDrawable(context, iconRes), textColor,
                tintColor, duration, withIcon, shouldTint);
    }

    public static Toast custom(Context context, CharSequence message, Drawable icon,
                               int textColor, int tintColor, int duration,
                               boolean withIcon, boolean shouldTint) {

        final Toast currentToast = new Toast(context);
        final View toastLayout = LayoutInflater.from(context).inflate(R.layout.ab_toast_layout, null);
        final ImageView toastIcon = (ImageView) toastLayout.findViewById(R.id.toast_icon);
        final TextView toastTextView = (TextView) toastLayout.findViewById(R.id.toast_text);
        Drawable drawableFrame;
        if (shouldTint) {
            drawableFrame = AbImageUtil.tint9PatchDrawableFrame(context, tintColor);
        }else {
            drawableFrame = AbViewUtil.getDrawable(context, R.drawable.ic_toast_frame);
        }
        AbViewUtil.setBackground(toastLayout, drawableFrame);

        if (withIcon) {
            if (icon != null){
                AbViewUtil.setBackground(toastIcon, icon);
            }else{
                toastIcon.setVisibility(View.GONE);
            }

        } else {
            toastIcon.setVisibility(View.GONE);
        }

        toastTextView.setTextColor(textColor);
        if (AbStrUtil.isEmpty(String.valueOf(message))) {
            toastTextView.setText("没有相关错误信息!");
        }else{
            toastTextView.setText(message);
        }

        toastTextView.setTypeface(Typeface.create(TOAST_TYPEFACE, Typeface.NORMAL));
        currentToast.setView(toastLayout);
        currentToast.setDuration(duration);
        currentToast.show();
        return currentToast;
    }

}
