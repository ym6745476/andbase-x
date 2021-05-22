package com.andbase.library.db.orm.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.andbase.library.db.orm.table.AbTableCreater;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 手机内部数据库 在data/data下面的数据库
 */
public class AbDBHelper extends SQLiteOpenHelper{
	
	/** The model classes. */
	private Class<?>[] modelClasses;
	
	
	/**
	 * 初始化一个DBHelper.
	 * @param context 应用context
	 * @param name 数据库名
	 * @param factory 数据库查询的游标工厂
	 * @param version 数据库的新版本号
	 * @param modelClasses 要初始化的表的对象
	 */
	public AbDBHelper(Context context, String name,
			CursorFactory factory, int version,Class<?>[] modelClasses) {
		super(context, name, factory, version);
		this.modelClasses = modelClasses;
	}
	
	
	/**
     * 表的创建.
     * @param db 数据库对象
     */
    public void onCreate(SQLiteDatabase db) {
		AbTableCreater.createTablesByClasses(db, this.modelClasses);
	}

	/**
	 * 表的重建.
	 * @param db 数据库对象
	 * @param oldVersion 旧版本号
	 * @param newVersion 新版本号
	 */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		AbTableCreater.dropTablesByClasses(db, this.modelClasses);
		onCreate(db);
	}
}
