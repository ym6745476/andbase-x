
package com.andbase.library.db.orm.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.andbase.library.db.orm.table.AbTableCreater;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info SD卡中保存数据库辅助类
 */
public class AbSDDBHelper extends AbSDSQLiteOpenHelper {
	
	/** The model classes. */
	private Class<?>[] modelClasses;

	/**
	 * 初始化一个SD DBHelper.
	 * @param context 应用context
	 * @param dir 数据库文件保存文件夹全路径
	 * @param name 数据库文件名
	 * @param factory 数据库查询的游标工厂
	 * @param version 数据库的新版本号
	 * @param modelClasses 要初始化的表的对象
	 */
	public AbSDDBHelper(Context context, String dir,String name,
			SQLiteDatabase.CursorFactory factory, int version,
			Class<?>[] modelClasses) {
        super(context, dir,name, null, version);
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
