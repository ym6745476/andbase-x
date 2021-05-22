
package com.andbase.library.db.orm.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info SD卡中保存数据库
 */
public abstract class AbSDSQLiteOpenHelper extends SQLiteOpenHelper{
	
    /** 应用Context. */
    private final Context mContext;
    
    /** 数据库名. */
    private final String mName;
    
    /** 数据库文件保存文件夹全路径. */
    private final String mDir;
    
    /** 数据库查询的游标工厂. */
    private final CursorFactory mFactory;
    
    /** 数据库的新版本号. */
    private final int mNewVersion;
    
    /** 数据库对象. */
    private SQLiteDatabase mDatabase = null;
    
    /** 是否已经初始化过. */
    private boolean mIsInitializing = false;
    
    
    /**
     * 初始化一个AbSDSQLiteOpenHelper对象.
     *
     * @param context 应用Context
     * @param dir 要放到SDCard下的文件夹路径
     * @param name 数据库名
     * @param factory 数据库查询的游标工厂
     * @param version 数据库的新版本号
     */
    public AbSDSQLiteOpenHelper(Context context, String dir, String name,
                                CursorFactory factory, int version) {
		super(context, name, factory, version);
		if (version < 1) throw new IllegalArgumentException("Version must be >= 1, was " + version);
		mContext = context;
		mDir = dir;
	    mName = name;
	    mFactory = factory;
	    mNewVersion = version;
	}

    /**
     * 获取可写权限的数据库对象.
     *
     * @return 数据库对象
     */
    public synchronized SQLiteDatabase getWritableDatabase() {
        if (mDatabase != null && mDatabase.isOpen() && !mDatabase.isReadOnly()) {
        	//已经获取过
        	return mDatabase;  
        }
        if (mIsInitializing) {
            throw new IllegalStateException("数据库已被占用getWritableDatabase()");
        }
        boolean success = false;
        SQLiteDatabase db = null;
        try {
            mIsInitializing = true;
            if (mName == null) {
            	//创建一个内存支持SQLite数据库
                db = SQLiteDatabase.create(null);
            } else {
            	//创建一个文件支持SQLite数据库
            	String path = mDir + File.separator + mName;
                db = SQLiteDatabase.openOrCreateDatabase(path,mFactory);
            }
            int version = db.getVersion();
            if (version != mNewVersion) {
                db.beginTransaction();
                try {
                    if (version == 0) {
                        onCreate(db);
                    } else {
                        onUpgrade(db, version, mNewVersion);
                    }
                    db.setVersion(mNewVersion);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }
            onOpen(db);
            success = true;
            return db;
        } finally {
        	//释放占有
            mIsInitializing = false;
            if (success) {
                if (mDatabase != null) {
                    try { 
                    	mDatabase.close(); 
                    } catch (Exception e) {
                    }
                }
                mDatabase = db;
            } else {
                if (db != null) db.close();
            }
        }
    }

    /**
     * 获取可读权限的数据库对象..
     *
     * @return 数据库对象
     */
    public synchronized SQLiteDatabase getReadableDatabase() {
        if (mDatabase != null && mDatabase.isOpen()) {
        	//已经获取过
            return mDatabase; 
        }
        if (mIsInitializing) {
            throw new IllegalStateException("数据库已被占用getReadableDatabase()");
        }
        
        //都是写获取写的数据库
        SQLiteDatabase db = null;
        
        try {
			 db = getWritableDatabase();
			 mDatabase = db;
		} catch (Exception e1) {
			try {
	            mIsInitializing = true;
	            String path = mDir + File.separator + mName;
	            db = SQLiteDatabase.openDatabase(path, mFactory, SQLiteDatabase.OPEN_READONLY);
	            if (db.getVersion() != mNewVersion) {
	                throw new SQLiteException("不能更新只读数据库的版本 from version " +
	                        db.getVersion() + " to " + mNewVersion + ": " + path);
	            }
	            onOpen(db);
	            mDatabase = db;
	            return mDatabase;
	        }catch (SQLiteException e) {
	        	
	        } finally {
	            mIsInitializing = false;
	            if (db != null && db != mDatabase) db.close();
	        }
		}
        
        return mDatabase;
    }
    
    /**
     * 数据库被打开.
     *
     * @param db 被打开的数据库
     */
    public void onOpen(SQLiteDatabase db) {}

    /**
     * 数据库被关闭.
     *
     */
    public synchronized void close() {
        if (mIsInitializing) throw new IllegalStateException("Closed during initialization");
        if (mDatabase != null && mDatabase.isOpen()) {
            mDatabase.close();
            mDatabase = null;
        }
    }

    /**
     * 数据库被创建事件.
     *
     * @param db 被创建的数据库
     */
    public abstract void onCreate(SQLiteDatabase db);
    
    /**
     * 数据库被重建.
     *
     * @param db 被创建的数据库
     * @param oldVersion 原来的数据库版本
     * @param newVersion 新的数据库版本
     */
    public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    
}
