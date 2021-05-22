package com.andbase.library.db.orm.dao;

import java.util.List;
import java.util.Map;

import android.database.sqlite.SQLiteOpenHelper;


/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 数据库表操作类接口
 */
public interface AbDBDao<T> {

	/**
	 * 获取数据库.
	 * @return the db helper
	 */
	public SQLiteOpenHelper getDBHelper();

	/**
	 * 插入实体类.
	 * @param entity 映射实体
	 * @return 插入成功的数据ID
	 */
	public abstract long insert(T entity);
	
	/**
	 * 插入实体类列表，默认主键自增.
	 * @param entityList 映射实体列表
	 * @return 插入成功的数据行号
	 */
	public abstract long[]  insertList(List<T> entityList);


	/**
	 * 根据ID删除数据.
	 * @param id 数据ID主键
	 * @return the int 影响的行数
	 */
	public abstract int delete(int id);

	/**
	 * 根据ID删除数据（多个）.
	 * @param ids 数据ID主键
	 * @return the int 影响的行数
	 */
	public abstract int delete(Integer... ids);
	
	/**
	 * 根据where删除数据.
	 * @param whereClause where语句
	 * @param whereArgs  where参数
	 * @return the int 影响的行数
	 */
	public abstract int delete(String whereClause, String[] whereArgs);

	/**
	 * 删除所有数据.
	 * @return the int 影响的行数
	 */
	public abstract int deleteAll();
	
	/**
	 * 更新数据.
	 * @param entity 数据,ID主键
	 * @return 影响的行数
	 */
	public abstract int update(T entity);
	
	/**
	 * 更新数据.
	 * @param entityList 数据列表,ID主键
	 * @return 影响的行数
	 */
	public abstract int updateList(List<T> entityList);

	/**
	 * 根据获取一条数据.
	 * @param id 数据ID主键
	 * @return 一条数据映射实体
	 */
	public abstract T queryOne(int id);

	/**
	 * 执行查询语句.
	 * @param sql sql语句
	 * @param selectionArgs 绑定变量的参数值
	 * @param clazz  返回的对象类型
	 * @return 映射实体列表
	 */
	public abstract List<T> rawQuery(String sql, String[] selectionArgs, Class<T> clazz);

	/**
	 * 查询列表.
	 * @return 映射实体列表
	 */
	public abstract List<T> queryList();

	/**
	 * 映射实体列表.
	 * @param columns 查询的列
	 * @param selection where语句的sql
	 * @param selectionArgs where语句的sql的绑定变量的参数
	 * @param groupBy 分组语句
	 * @param having 分组后的过滤语句
	 * @param orderBy 排序
	 * @param limit limit语句
	 * @return 映射实体列表
	 */
	public abstract List<T> queryList(String[] columns, String selection,
									  String[] selectionArgs, String groupBy, String having,
									  String orderBy, String limit);
	
	/**
	 * 映射实体列表.
	 * @param selection where语句的sql
	 * @param selectionArgs where语句的sql的绑定变量的参数
	 * @return 映射实体列表
	 */
	public abstract List<T> queryList(String selection, String[] selectionArgs);

	/**
	 * 检查是否存在数据.
	 * @param sql sql语句
	 * @param selectionArgs 绑定变量的参数值
	 * @return 如果存在返回true, 不存在为false
	 */
	public abstract boolean isExist(String sql, String[] selectionArgs);

	/**
	 * 将查询的结果保存为名值对map.
	 * @param sql 查询sql
	 * @param selectionArgs 绑定变量的参数值
	 * @return 返回的Map中的key全部是小写形式.
	 */
	public abstract List<Map<String, String>> queryMapList(String sql, String[] selectionArgs);
	
	/**
	 * 返回一个查询的结果条数.
	 * @param sql 查询sql
	 * @param selectionArgs 绑定变量的参数值
	 * @return 总条数.
	 */
	public abstract int queryCount(String sql, String[] selectionArgs);

	/**
	 * 封装执行sql代码.
	 * @param sql sql语句
	 * @param selectionArgs 绑定变量的参数值
	 */
	public abstract void execSql(String sql, Object[] selectionArgs);

    /**
     * 获取读数据库，数据操作前必须调用.
     */
    public abstract void startReadableDatabase();

    /**
     * 获取写数据库，数据操作前必须调用,不开启事务.
     */
    public abstract void startWritableDatabase();

    /**
     * 获取写数据库，数据操作前必须调用.
     * @param transaction 是否开启事务
     */
    public abstract void startWritableDatabase(boolean transaction);


    /**
     * 关闭数据库，数据操作后必须调用.
     */
    public abstract void closeDatabase();

}
