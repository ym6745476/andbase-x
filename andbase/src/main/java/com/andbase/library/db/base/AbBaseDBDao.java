package com.andbase.library.db.base;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 普通的基础操作类
 */
public class AbBaseDBDao {
	
	/**
	 * 得到列值.
	 * @param columnName the column name
	 * @param cursor the cursor
	 * @return the string column value
	 */
	public String getStringColumnValue(String columnName, Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(columnName));
	}
	
	/**
	 * 得到列值.
	 * @param columnName the column name
	 * @param cursor the cursor
	 * @return the int column value
	 */
	public int getIntColumnValue(String columnName, Cursor cursor) {
		return cursor.getInt(cursor.getColumnIndex(columnName));
	}
	
	/**
	 * 关闭数据库与游标.
	 * @param cursor the cursor
	 * @param db the db
	 */
	public void closeDatabase(Cursor cursor, SQLiteDatabase db) {
		closeCursor(cursor);
		if (db != null && db.isOpen()) {
			db.close();
			db = null;
		}
	}
	
	/**
	 * 关闭游标.
	 * @param cursor the cursor
	 */
	public void closeCursor(Cursor cursor) {
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
	}
}
