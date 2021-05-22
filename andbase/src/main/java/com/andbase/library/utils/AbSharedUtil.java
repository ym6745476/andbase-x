package com.andbase.library.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.andbase.library.app.global.AbAppConfig;


/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 保存到 SharedPreferences 的数据
 */
public class AbSharedUtil {

	public static  String SHARED_PATH = AbAppConfig.SHARED_PATH;

	private static SharedPreferences sharedPreferences;

	public static SharedPreferences getDefaultSharedPreferences(Context context) {
		if(sharedPreferences == null){
			sharedPreferences = context.getSharedPreferences(SHARED_PATH, Context.MODE_PRIVATE);
		}
		return sharedPreferences;
	}
	
	public static void putInt(Context context,String key, int value) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		Editor edit = sharedPreferences.edit();
		edit.remove(key);
		edit.putInt(key, value);
		edit.commit();
	}

	public static int getInt(Context context,String key,int defValue) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		return sharedPreferences.getInt(key, defValue);
	}
	
	public static void putString(Context context,String key, String value) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		Editor edit = sharedPreferences.edit();
		edit.remove(key);
		edit.putString(key, value);
		edit.commit();
	}

	public static String getString(Context context,String key,String defValue) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		return sharedPreferences.getString(key,defValue);
	}
	
	public static void putBoolean(Context context,String key, boolean value) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		Editor edit = sharedPreferences.edit();
		edit.remove(key);
		edit.putBoolean(key, value);
		edit.commit();
	}

	public static boolean getBoolean(Context context,String key,boolean defValue) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		return sharedPreferences.getBoolean(key,defValue);
	}
	
	public static void remove(Context context,String key) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		Editor edit = sharedPreferences.edit();
		edit.remove(key);
		edit.commit();
	}

}
