package com.andbase.library.db.orm.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.andbase.library.db.base.AbBaseDBDao;
import com.andbase.library.db.orm.annotation.ActionType;
import com.andbase.library.db.orm.annotation.Column;
import com.andbase.library.db.orm.annotation.Id;
import com.andbase.library.db.orm.annotation.Relations;
import com.andbase.library.db.orm.annotation.RelationsType;
import com.andbase.library.db.orm.annotation.Table;
import com.andbase.library.db.orm.table.AbTableCreater;
import com.andbase.library.utils.AbLogUtil;
import com.andbase.library.utils.AbStrUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 数据库表操作类接口实现类
 */
public class AbDBDaoImpl<T> extends AbBaseDBDao implements AbDBDao<T> {
	
	/** The db helper. */
	private SQLiteOpenHelper dbHelper;
	
	/** 锁对象. */
    private final ReentrantLock lock = new ReentrantLock();
	
	/** The table name. */
	private String tableName;
	
	/** The id column. */
	private String idColumn;
	
	/** The clazz. */
	private Class<T> clazz;
	
	/** The all fields. */
	private List<Field> allFields;
	
	/** The Constant METHOD_INSERT. */
	private static final int METHOD_INSERT = 0;
	
	/** The Constant METHOD_UPDATE. */
	private static final int METHOD_UPDATE = 1;

	/** The Constant METHOD_DELETE. */
	private static final int METHOD_DELETE = 2;
	
	/** 这个Dao的数据库对象. */
	private SQLiteDatabase db = null;

	/**
	 * 用一个对象实体初始化这个数据库操作实现类.
	 *
	 * @param dbHelper 数据库操作实现类
	 * @param clazz 映射对象实体
	 */
	public AbDBDaoImpl(SQLiteOpenHelper dbHelper, Class<T> clazz) {
		this.dbHelper = dbHelper;
		if (clazz == null) {
			this.clazz = ((Class<T>) ((ParameterizedType) super
					.getClass().getGenericSuperclass())
					.getActualTypeArguments()[0]);
		} else {
			this.clazz = clazz;
		}

		if (this.clazz.isAnnotationPresent(Table.class)) {
			Table table = (Table) this.clazz.getAnnotation(Table.class);
			this.tableName = table.name();
		}

		// 加载所有字段
		this.allFields = AbTableCreater.joinFields(this.clazz.getDeclaredFields(),
				this.clazz.getSuperclass().getDeclaredFields());

		// 找到主键
		for (Field field : this.allFields) {
			if (field.isAnnotationPresent(Id.class)) {
				Column column = (Column) field.getAnnotation(Column.class);
				this.idColumn = column.name();
				break;
			}
		}

		AbLogUtil.d(AbDBDaoImpl.class, "clazz:" + this.clazz + " tableName:" + this.tableName
				+ " idColumn:" + this.idColumn);
	}

	/**
	 * 初始化这个数据库操作实现类.
	 *
	 * @param dbHelper 数据库操作实现类
	 */
	public AbDBDaoImpl(SQLiteOpenHelper dbHelper) {
		this(dbHelper, null);
	}

	/**
	 * 获取数据库辅助类.
	 * @return the db helper
	 */
	@Override
	public SQLiteOpenHelper getDBHelper() {
		return dbHelper;
	}

	
	/**
	 * 查询一条.
	 * @param id the id
	 * @return the t
	 */
	@Override
	public T queryOne(int id) {
		synchronized (lock) {
			String selection = this.idColumn + " = ?";
			String[] selectionArgs = { Integer.toString(id) };
			AbLogUtil.d(AbDBDaoImpl.class, "[queryOne]: select * from " + this.tableName + " where "
					+ this.idColumn + " = '" + id + "'");
			List<T> list = queryList(null, selection, selectionArgs, null, null, null,
					null);
			if ((list != null) && (list.size() > 0)) {
				return (T) list.get(0);
			}
			return null;
		}
	}

	/**
	 * 一种更灵活的方式查询，不支持对象关联，可以写完整的sql.
	 *
	 * @param sql 完整的sql如：select * from a ,b where a.id=b.id and a.id = ?
	 * @param selectionArgs 绑定变量值
	 * @param clazz  返回的对象类型
	 * @return the list
	 */
	@Override
	public List<T> rawQuery(String sql, String[] selectionArgs,Class<T> clazz) {

		List<T> list = new ArrayList<T>();
		Cursor cursor = null;
		try {
			lock.lock();
			checkDBOpened();
			AbLogUtil.d(AbDBDaoImpl.class, "[rawQuery]: " + getLogSql(sql, selectionArgs));
			cursor = db.rawQuery(sql, selectionArgs);
			getListFromCursor(clazz,list, cursor);
		} catch (Exception e) {
			AbLogUtil.e(AbDBDaoImpl.class, "[rawQuery] from DB Exception.");
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			lock.unlock();
		}
		
		return list;
	}

	/**
	 * 是否存在.
	 *
	 * @param sql the sql
	 * @param selectionArgs the selection args
	 * @return true, if is exist
	 */
	@Override
	public boolean isExist(String sql, String[] selectionArgs) {
		Cursor cursor = null;
		try {
			lock.lock();
			checkDBOpened();
			AbLogUtil.d(AbDBDaoImpl.class, "[isExist]: " + getLogSql(sql, selectionArgs));
			cursor = db.rawQuery(sql, selectionArgs);
			if (cursor.getCount() > 0) {
				return true;
			}
		} catch (Exception e) {
			AbLogUtil.e(AbDBDaoImpl.class, "[isExist] from DB Exception.");
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			lock.unlock();
		}
		return false;
	}

	/**
	 * 查询所有数据.
	 * @return the list
	 */
	@Override
	public List<T> queryList() {
		return queryList(null, null, null, null, null, null, null);
	}

	/**
	 * 查询列表.
	 *
	 * @param columns the columns
	 * @param selection the selection
	 * @param selectionArgs the selection args
	 * @param groupBy the group by
	 * @param having the having
	 * @param orderBy the order by
	 * @param limit the limit
	 * @return the list
	 */
	@Override
	public List<T> queryList(String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit) {
	
			List<T> list = new ArrayList<T>();
			Cursor cursor = null;
			try {
				lock.lock();
				checkDBOpened();
				String sql = getLogSql(selection,selectionArgs);

				AbLogUtil.d(AbDBDaoImpl.class, "[queryList] from "+this.tableName+" where "+ sql +" group by "+groupBy+" having "+having+" order by "+orderBy+" limit "+limit);
				cursor = db.query(this.tableName, columns, selection,
						selectionArgs, groupBy, having, orderBy, limit);
	
				getListFromCursor(this.clazz,list, cursor);
				
				closeCursor(cursor);
				
				//获取关联域的操作类型和关系类型
				String foreignKey = null;
				String type = null;
				String action = null;
				//需要判断是否有关联表
				for (Field relationsField : allFields) {
					if (!relationsField.isAnnotationPresent(Relations.class)) {
						continue;
					}
					
					Relations relations = (Relations) relationsField.getAnnotation(Relations.class);
					//获取外键列名
					foreignKey = relations.foreignKey();
					//关联类型
					type = relations.type();
					//操作类型
					action = relations.action();
					//设置可访问
					relationsField.setAccessible(true);
					
					if(action.indexOf(ActionType.query) == -1){
						continue;
					}

                    //主键的
                    Field primaryKeyField = null;

                    for (Field entityField : allFields) {
                        //设置可访问
                        entityField.setAccessible(true);
                        if (entityField.isAnnotationPresent(Id.class)){
                            primaryKeyField = entityField;
                            break;
                        }
                    }
					
					//得到关联表的表名查询
					for(T entity:list){

                        //主表的用于关联表的foreignKey值
                        String value = String.valueOf(primaryKeyField.get(entity));

                        if(RelationsType.one2one.equals(type)){
                            //一对一关系
                            //获取这个实体的表名
                            String relationsTableName = "";
                            if (relationsField.getType().isAnnotationPresent(Table.class)) {
                                Table table = (Table) relationsField.getType().getAnnotation(Table.class);
                                relationsTableName = table.name();
                            }

                            List<T> relationsList = new ArrayList<T>();
                            Field[] relationsEntityFields = relationsField.getType().getDeclaredFields();
                            for (Field relationsEntityField : relationsEntityFields) {
                                Column relationsEntityColumn = (Column) relationsEntityField.getAnnotation(Column.class);
                                //获取主键的值作为关联表的查询条件
                                if (relationsEntityColumn != null && relationsEntityColumn.name().equals(foreignKey)) {

									try {
										//查询数据设置给这个域
										cursor = db.query(relationsTableName, null, foreignKey+" = ?",new String[]{value}, null, null, null, null);
										getListFromCursor(relationsField.getType(),relationsList, cursor);
										if(relationsList.size()>0){
                                            //获取关联表的对象设置值
                                            relationsField.set(entity, relationsList.get(0));
                                        }
									}catch (Exception e) {
									}

									break;

                                }
                            }

                        }else if(RelationsType.one2many.equals(type) || RelationsType.many2many.equals(type)){
                            //一对多关系

                            //得到泛型里的class类型对象
                            Class listEntityClazz = null;
                            Class<?> fieldClass = relationsField.getType();
                            if(fieldClass.isAssignableFrom(List.class)){
                                 Type fc = relationsField.getGenericType();
                                 if(fc == null) continue;
                                 if(fc instanceof ParameterizedType) {
                                     ParameterizedType pt = (ParameterizedType) fc;
                                     listEntityClazz = (Class)pt.getActualTypeArguments()[0];
                                 }

                            }

                            if(listEntityClazz==null){
                                AbLogUtil.e(AbDBDaoImpl.class, "对象模型需要设置List的泛型");
                                return null;
                            }

                            //得到表名
                            String relationsTableName = "";
                            if (listEntityClazz.isAnnotationPresent(Table.class)) {
                                Table table = (Table) listEntityClazz.getAnnotation(Table.class);
                                relationsTableName = table.name();
                            }

                            List<T> relationsList = new ArrayList<T>();
                            Field[] relationsEntityFields = listEntityClazz.getDeclaredFields();
                            for (Field relationsEntityField : relationsEntityFields) {
                                Column relationsEntityColumn = (Column) relationsEntityField.getAnnotation(Column.class);
                                //获取外键的值作为关联表的查询条件
                                if (relationsEntityColumn != null && relationsEntityColumn.name().equals(foreignKey)) {
									try {
										//查询数据设置给这个域
										cursor = db.query(relationsTableName, null, foreignKey+" = ?",new String[]{value}, null, null, null, null);
										getListFromCursor(listEntityClazz,relationsList, cursor);
										if(relationsList.size()>0){
											//获取关联表的对象设置值
											relationsField.set(entity, relationsList);
										}
									}catch (Exception e) {
									}
                                    break;
                                }
                            }

                        }
					}
				}
				
			} catch (Exception e) {
				AbLogUtil.e(AbDBDaoImpl.class, "[queryList] from DB Exception");
				e.printStackTrace();
			} finally {
				closeCursor(cursor);
				lock.unlock();
			}
	
			return list;
	}
	
	
    /**
     * 简单一些的查询.
     *
     * @param selection the selection
     * @param selectionArgs the selection args
     * @return the list
     */
	@Override
	public List<T> queryList(String selection, String[] selectionArgs) {
		return queryList(null, selection,selectionArgs, null, null,null, null);
	}

	/**
	 * 从游标中获得映射对象列表.
	 *
	 * @param clazz the clazz
	 * @param list 返回的映射对象列表
	 * @param cursor 当前游标
	 * @return the list from cursor
	 * @throws IllegalAccessException the illegal access exception
	 * @throws InstantiationException the instantiation exception
	 */
	private void getListFromCursor(Class<?> clazz,List<T> list, Cursor cursor)
			throws IllegalAccessException, InstantiationException {
		while (cursor.moveToNext()) {
			Object entity = clazz.newInstance();
			// 加载所有字段
			List<Field> allFields = AbTableCreater.joinFields(entity.getClass().getDeclaredFields(),
					entity.getClass().getSuperclass().getDeclaredFields());
			

			for (Field field : allFields) {
				Column column = null;
				if (field.isAnnotationPresent(Column.class)) {
					column = (Column) field.getAnnotation(Column.class);

					field.setAccessible(true);
					Class<?> fieldType = field.getType();

					int c = cursor.getColumnIndex(column.name());
					if (c < 0) {
						continue; // 如果不存则循环下个属性值
					} else if ((Integer.TYPE == fieldType)
							|| (Integer.class == fieldType)) {
						field.set(entity, cursor.getInt(c));
					} else if (String.class == fieldType) {
						field.set(entity, cursor.getString(c));
					} else if ((Long.TYPE == fieldType)
							|| (Long.class == fieldType)) {
						field.set(entity, Long.valueOf(cursor.getLong(c)));
					} else if ((Float.TYPE == fieldType)
							|| (Float.class == fieldType)) {
						field.set(entity, Float.valueOf(cursor.getFloat(c)));
					} else if ((Short.TYPE == fieldType)
							|| (Short.class == fieldType)) {
						field.set(entity, Short.valueOf(cursor.getShort(c)));
					} else if ((Double.TYPE == fieldType)
							|| (Double.class == fieldType)) {
						field.set(entity, Double.valueOf(cursor.getDouble(c)));
					} else if (Date.class == fieldType) {// 处理java.util.Date类型,update2012-06-10
						Date date = new Date();
						date.setTime(cursor.getLong(c));
						field.set(entity, date);
					} else if (Blob.class == fieldType) {
						field.set(entity, cursor.getBlob(c));
					} else if (Character.TYPE == fieldType) {
						String fieldValue = cursor.getString(c);
						if ((fieldValue != null) && (fieldValue.length() > 0)) {
							field.set(entity, Character.valueOf(fieldValue.charAt(0)));
						}
					}else if ((Boolean.TYPE == fieldType) || (Boolean.class == fieldType)) {
                        String temp = cursor.getString(c);
                        if ("true".equals(temp) || "1".equals(temp)){
                            field.set(entity, true);
                        }else{
                            field.set(entity, false);
                        }
                    }

				}
			}

			list.add((T) entity);
		}
	}
	
	/**
	 * 插入实体.
	 * @param entity the entity
	 * @return the long
	 */
	@Override
	public long insert(T entity) {
			String sql = null;
			long rowId = -1;
			try {
				lock.lock();
				checkDBOpened();
				ContentValues cv = new ContentValues();
                sql = setContentValues(entity, cv,METHOD_INSERT);

				AbLogUtil.d(AbDBDaoImpl.class, "[insert]: insert into " + this.tableName + " " + sql);
				rowId = db.insert(this.tableName, null, cv);
                if(rowId < 0){
                    return rowId;
                }
                /*//获取插入成功的主键的值
                String last_insert_rowid = "select last_insert_rowid() from " + this.tableName ;
                Cursor cursor = db.rawQuery(last_insert_rowid, null);

                if(cursor.moveToFirst()){
                    rowId = cursor.getInt(0);
                }*/

				//获取关联域的操作类型和关系类型
				String foreignKey = null;
				String type = null;
				String action = null;
				//需要判断是否有关联表
				for (Field relationsField : allFields) {
					if (!relationsField.isAnnotationPresent(Relations.class)) {
						continue;
					}
					
					Relations relations = (Relations) relationsField.getAnnotation(Relations.class);
					//获取外键列名
					foreignKey = relations.foreignKey();
					//关联类型
					type = relations.type();
					//操作类型
					action = relations.action();
					//设置可访问
					relationsField.setAccessible(true);

                    //设置插入数据时关联子表
					if(action.indexOf(ActionType.insert) == -1){
						continue;
					}
					
					if(RelationsType.one2one.equals(type)){
						//一对一关系
						//获取关联表的对象
						T relationsEntity = (T)relationsField.get(entity);
						if(relationsEntity != null){
							ContentValues relationsCv = new ContentValues();

                            //外键的值为主表的主键的值
                            relationsCv.put(foreignKey,rowId);

                            sql = setContentValues(relationsEntity, relationsCv,METHOD_INSERT);

							String relationsTableName = "";
							if (relationsEntity.getClass().isAnnotationPresent(Table.class)) {
								Table table = (Table) relationsEntity.getClass().getAnnotation(Table.class);
								relationsTableName = table.name();
							}
							
							AbLogUtil.d(AbDBDaoImpl.class, "[insert]: insert into " + relationsTableName + " " + sql);
							try {
								db.insert(relationsTableName, null, relationsCv);
							} catch (Exception e) {
							}
						}
						
					}else if(RelationsType.one2many.equals(type) || RelationsType.many2many.equals(type)){
						//一对多关系
						//获取关联表的对象
						List<T> list = (List<T>)relationsField.get(entity);
						
						if(list!=null && list.size()>0){
							for(T relationsEntity:list){
								ContentValues relationsCv = new ContentValues();

                                //外键的值为主表的主键的值
                                relationsCv.put(foreignKey,rowId);

                                sql = setContentValues(relationsEntity, relationsCv,METHOD_INSERT);

								String relationsTableName = "";
								if (relationsEntity.getClass().isAnnotationPresent(Table.class)) {
									Table table = (Table) relationsEntity.getClass().getAnnotation(Table.class);
									relationsTableName = table.name();
								}
								
								AbLogUtil.d(AbDBDaoImpl.class, "[insert]: insert into " + relationsTableName + " " + sql);
								try {
									db.insert(relationsTableName, null, relationsCv);
								} catch (Exception e) {
								}
							}
						}
						
					}
				}
				
			} catch (Exception e) {
				AbLogUtil.d(AbDBDaoImpl.class, "[insert] into DB Exception.");
				e.printStackTrace();
			}finally {
				lock.unlock();
			}
			return rowId;
	}


	/**
	 * 插入列表.
	 *
	 * @param entityList the entity list
	 * @return the long[] 插入成功的数据ID
	 */
	@Override
	public long[] insertList(List<T> entityList) {
			String sql = null;
			long[] rowIds = new long[entityList.size()];
			for(int i=0;i<rowIds.length;i++){
				rowIds[i] = -1;
			}
			try {
				lock.lock();
				checkDBOpened();
				for(int i=0;i<entityList.size();i++){
					T entity  = entityList.get(i);
					ContentValues cv = new ContentValues();
                    sql = setContentValues(entity, cv,METHOD_INSERT);
					
					AbLogUtil.d(AbDBDaoImpl.class, "[insertList]: insert into " + this.tableName + " " + sql);
					rowIds[i] = db.insert(this.tableName, null, cv);

                    if(rowIds[i] < 0){
                        continue;
                    }
                    /*//获取插入成功的主键的值
                    String last_insert_rowid = "select last_insert_rowid() from " + this.tableName ;
                    Cursor cursor = db.rawQuery(last_insert_rowid, null);

                    if(cursor.moveToFirst()){
                        rowIds[i] = cursor.getInt(0);
                    }*/
					
					//获取关联域的操作类型和关系类型
					String foreignKey = null;
					String type = null;
					String action = null;
					Field field  = null;
					//需要判断是否有关联表
					for (Field relationsField : allFields) {
						if (!relationsField.isAnnotationPresent(Relations.class)) {
							continue;
						}
						
						Relations relations = (Relations) relationsField.getAnnotation(Relations.class);
						//获取外键列名
						foreignKey = relations.foreignKey();
						//关联类型
						type = relations.type();
						//操作类型
						action = relations.action();
						//设置可访问
						relationsField.setAccessible(true);
						field =  relationsField;
					}
					
					if(field == null){
						continue;
					}
					
					if(action.indexOf(ActionType.insert) == -1){
						continue;
					}
					
					if(RelationsType.one2one.equals(type)){
						//一对一关系
						//获取关联表的对象
						T relationsEntity = (T)field.get(entity);
						if(relationsEntity != null){
							ContentValues relationsCv = new ContentValues();
                            //外键的值为主表的主键的值
                            relationsCv.put(foreignKey,rowIds[i]);
                            sql = setContentValues(relationsEntity, relationsCv,METHOD_INSERT);
							String relationsTableName = "";
							if (relationsEntity.getClass().isAnnotationPresent(Table.class)) {
								Table table = (Table) relationsEntity.getClass().getAnnotation(Table.class);
								relationsTableName = table.name();
							}
							
							AbLogUtil.d(AbDBDaoImpl.class, "[insertList]: insert into " + relationsTableName + " " + sql);
							try {
								db.insert(relationsTableName, null, relationsCv);
							} catch (Exception e) {
							}
						}
						
					}else if(RelationsType.one2many.equals(type) || RelationsType.many2many.equals(type)){
						//一对多关系
						//获取关联表的对象
						List<T> list = (List<T>)field.get(entity);
						if(list!=null && list.size()>0){
							for(T relationsEntity:list){
								ContentValues relationsCv = new ContentValues();
                                //外键的值为主表的主键的值
                                relationsCv.put(foreignKey,rowIds[i]);
                                sql = setContentValues(relationsEntity, relationsCv,METHOD_INSERT);
								String relationsTableName = "";
								if (relationsEntity.getClass().isAnnotationPresent(Table.class)) {
									Table table = (Table) relationsEntity.getClass().getAnnotation(Table.class);
									relationsTableName = table.name();
								}
								
								AbLogUtil.d(AbDBDaoImpl.class, "[insertList]: insert into " + relationsTableName + " " + sql);
								try {
									db.insert(relationsTableName, null, relationsCv);
								} catch (Exception e) {
								}
							}
						}
						
					}
				}
			} catch (Exception e) {
				AbLogUtil.d(AbDBDaoImpl.class, "[insertList] into DB Exception.");
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
	
			return rowIds;
	}

	

	/**
	 * 按id删除.
	 *
	 * @param id the id
	 * @return the int
	 */
	@Override
	public int delete(int id) {
		int rows = 0;
		try {
			lock.lock();
			checkDBOpened();
			String where = this.idColumn + " = ?";
		    String[] whereValue = { Integer.toString(id) };
			AbLogUtil.d(AbDBDaoImpl.class, "[delete]: delelte from " + this.tableName + " where "
					+ where.replace("?", String.valueOf(id)));
			rows =  db.delete(this.tableName, where, whereValue);

			//获取关联域的操作类型和关系类型
			String foreignKey = null;
			String action = null;
			String relationsTableName = null;
			//需要判断是否有关联表
			for (Field relationsField : allFields) {
				if (!relationsField.isAnnotationPresent(Relations.class)) {
					continue;
				}

				Relations relations = (Relations) relationsField.getAnnotation(Relations.class);
				//获取外键列名
				foreignKey = relations.foreignKey();
				//操作类型
				action = relations.action();
				//表名
				relationsTableName = relations.name();
				//设置可访问
				relationsField.setAccessible(true);

				//判断关系类型
				if (action.indexOf(ActionType.delete) == -1) {
					continue;
				}

				AbLogUtil.d(AbDBDaoImpl.class, "[delete]: delete from " + relationsTableName + " where "+foreignKey+" = "+String.valueOf(id)+";");

				//获取关联的表名，清空
				try {
					db.delete(relationsTableName, foreignKey+" = ?", new String[]{String.valueOf(id)});
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			lock.unlock();
	    }
		return rows;
	}

	/**
	 * 按id删除.
	 *
	 * @param ids the ids
	 * @return the int
	 */
	@Override
	public int delete(Integer... ids) {
		int rows = 0;
		if (ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				rows += delete(ids[i]);
			}
		}
		return rows;
	}
	

	/**
	 * 按条件删除数据.
	 *
	 * @param whereClause the where clause
	 * @param whereArgs the where args
	 * @return the int
	 */
	@Override
	public int delete(String whereClause, String[] whereArgs) {
		int rows = 0;
		try {
			lock.lock();
			checkDBOpened();
			String sql = getLogSql(whereClause,whereArgs);
			if(!AbStrUtil.isEmpty(sql)){
				sql =" where " + sql;
			}
			AbLogUtil.d(AbDBDaoImpl.class, "[delete]: delete from " + this.tableName + sql);
		    rows = db.delete(this.tableName, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
		return rows;
	}

	/**
	 * 清空数据.
	 *
	 * @return the int 影响的行
	 */
	@Override
	public int deleteAll() {
		int rows = 0;
		try {
			lock.lock();
			checkDBOpened();
			AbLogUtil.d(AbDBDaoImpl.class, "[delete]: delete from " + this.tableName );
			rows = db.delete(this.tableName,null,null);

			//获取关联域的操作类型和关系类型
			String foreignKey = null;
			String type = null;
			String action = null;
			String relationsTableName = null;
			//需要判断是否有关联表
			for (Field relationsField : allFields) {
				if (!relationsField.isAnnotationPresent(Relations.class)) {
					continue;
				}

				Relations relations = (Relations) relationsField.getAnnotation(Relations.class);
				//获取外键列名
				foreignKey = relations.foreignKey();
				//关联类型
				type = relations.type();
				//操作类型
				action = relations.action();
				//表名
				relationsTableName = relations.name();
				//设置可访问
				relationsField.setAccessible(true);

				//判断关系类型
				if (action.indexOf(ActionType.delete) == -1) {
					continue;
				}

				AbLogUtil.d(AbDBDaoImpl.class, "[delete]: delete from " + relationsTableName + ";");

				//获取关联的表名，清空
				try {
					db.delete(relationsTableName, null, null);
				}catch(Exception e){
				}
			}

		} catch (Exception e) {
			AbLogUtil.d(AbDBDaoImpl.class, "[delete] DB Exception.");
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
		return rows;
	}

	/**
	 * 更新实体.
	 * @param entity the entity
	 * @return the int 影响的行
	 */
	@Override
	public int update(T entity) {
		int rows = 0;
		try {
			lock.lock();
			checkDBOpened();
			ContentValues cv = new ContentValues();

			//注意返回的sql中包含主键列
			String sql = setContentValues(entity, cv,METHOD_UPDATE);

			String where = this.idColumn + " = ?";
			int id = Integer.parseInt(cv.get(this.idColumn).toString());
			//set sql中不能包含主键列
			cv.remove(this.idColumn);
			
			AbLogUtil.d(AbDBDaoImpl.class, "[update]: update " + this.tableName + " set " + sql
					+ " where " + where.replace("?", String.valueOf(id)));

			String[] whereValue = { Integer.toString(id) };
			rows = db.update(this.tableName, cv, where, whereValue);
		} catch (Exception e) {
			AbLogUtil.d(AbDBDaoImpl.class, "[update] DB Exception.");
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	    return rows;
	}
	

	/**
	 * 更新列表.
	 *
	 * @param entityList the entity list
	 * @return the int 影响的行
	 */
	@Override
	public int updateList(List<T> entityList) {
			String sql = null;
			int rows = 0;
			try {
				lock.lock();
				checkDBOpened();
				for(T entity:entityList){
					ContentValues cv = new ContentValues();
	
					sql = setContentValues(entity, cv,METHOD_UPDATE);
	
					String where = this.idColumn + " = ?";
					int id = Integer.parseInt(cv.get(this.idColumn).toString());
					cv.remove(this.idColumn);
	
					AbLogUtil.d(AbDBDaoImpl.class, "[update]: update " + this.tableName + " set " + sql
							+ " where " + where.replace("?", String.valueOf(id)));
	
					String[] whereValue = { Integer.toString(id) };
					rows += db.update(this.tableName, cv, where, whereValue);
				}
			} catch (Exception e) {
				AbLogUtil.d(AbDBDaoImpl.class, "[update] DB Exception.");
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
	
			return rows;
	}

	/**
	 * 设置这个ContentValues.
	 *
	 * @param entity 映射实体
	 * @param cv the cv
	 * @param method 预执行的操作
	 * @return sql的字符串
	 * @throws IllegalAccessException the illegal access exception
	 */
	private String setContentValues(T entity, ContentValues cv,int method) throws IllegalAccessException {

		if (method == METHOD_INSERT || method == METHOD_UPDATE) {
			StringBuffer strField = new StringBuffer("(");
			StringBuffer strValue = new StringBuffer(" values(");
			StringBuffer strUpdate = new StringBuffer(" ");

			// 加载所有字段
			List<Field> allFields = AbTableCreater.joinFields(entity.getClass().getDeclaredFields(),
					entity.getClass().getSuperclass().getDeclaredFields());
			for (Field field : allFields) {
				if (!field.isAnnotationPresent(Column.class)) {
					continue;
				}
				Id id = (Id) field.getAnnotation(Id.class);
				Column column = (Column) field.getAnnotation(Column.class);

				field.setAccessible(true);
				Object fieldValue = field.get(entity);
				if (fieldValue == null)
					continue;

				if (method == METHOD_INSERT) {
					//设置为自动增长的列不用插入数据
					if ((field.isAnnotationPresent(Id.class) && id.autoincrement() == 1)
							&& ((field.getType() == Integer.TYPE) || (field.getType() == Integer.class))) {
						continue;
					}
				}

				// 处理java.util.Date类型,update
				if (Date.class == field.getType()) {
					// 2012-06-10
					cv.put(column.name(), ((Date) fieldValue).getTime());
					continue;
				}
				String value = String.valueOf(fieldValue);
				cv.put(column.name(), value);
				if (method == METHOD_INSERT) {
					strField.append(column.name()).append(",");
					strValue.append("'").append(value).append("',");
				} else if (method == METHOD_UPDATE) {
					strUpdate.append(column.name()).append("=").append("'").append(
							value).append("',");
				}

			}
			if (method == METHOD_INSERT) {
				strField.deleteCharAt(strField.length() - 1).append(")");
				strValue.deleteCharAt(strValue.length() - 1).append(")");
				return strField.toString() + strValue.toString();
			} else if (method == METHOD_UPDATE) {
				return strUpdate.deleteCharAt(strUpdate.length() - 1).append(" ").toString();
			}
		}else if(method == METHOD_DELETE){
			String  tableName = (String)entity;


		}
		return "";
	}

	/**
	 * 查询为map列表.
	 *
	 * @param sql the sql
	 * @param selectionArgs the selection args
	 * @return the list
	 */
	@Override
	public List<Map<String, String>> queryMapList(String sql,String[] selectionArgs) {
		Cursor cursor = null;
		List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
		try {
			lock.lock();
			checkDBOpened();
			AbLogUtil.d(AbDBDaoImpl.class, "[queryMapList]: " + getLogSql(sql, selectionArgs));
			cursor = db.rawQuery(sql, selectionArgs);
			while (cursor.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				for (String columnName : cursor.getColumnNames()) {
					int c = cursor.getColumnIndex(columnName);
					if (c < 0) {
                        // 如果不存在循环下个属性值
						continue;
					} else {
						map.put(columnName.toLowerCase(), cursor.getString(c));
					}
				}
				retList.add(map);
			}
		} catch (Exception e) {
			AbLogUtil.e(AbDBDaoImpl.class, "[queryMapList] from DB exception");
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			lock.unlock();
		}

		return retList;
	}
	
	
	/**
	 * 查询数量.
	 * @param sql the sql
	 * @param selectionArgs the selection args
	 * @return the int
	 */
	@Override
	public int queryCount(String sql, String[] selectionArgs) {
	   Cursor cursor = null;
       int count = 0;
       try{
    	   lock.lock();
    	   checkDBOpened();
    	   AbLogUtil.d(AbDBDaoImpl.class, "[queryCount]: " + getLogSql(sql, selectionArgs));
           cursor = db.query(this.tableName, null, sql, selectionArgs, null, null,null);
           if(cursor != null){
        	   count = cursor.getCount();
           }
       }catch (Exception e){
    	   AbLogUtil.e(AbDBDaoImpl.class, "[queryCount] from DB exception");
           e.printStackTrace();
       }finally{
    	   closeCursor(cursor);
    	   lock.unlock();
       }
       return count;
	}

	/**
	 * 执行特定的sql.
	 *
	 * @param sql the sql
	 * @param selectionArgs the selection args
	 */
	@Override
	public void execSql(String sql, Object[] selectionArgs) {
		try {
			lock.lock();
			checkDBOpened();
			AbLogUtil.d(AbDBDaoImpl.class, "[execSql]: " + getLogSql(sql, selectionArgs));
			if (selectionArgs == null) {
				db.execSQL(sql);
			} else {
				db.execSQL(sql, selectionArgs);
			}
		} catch (Exception e) {
			AbLogUtil.e(AbDBDaoImpl.class, "[execSql] DB exception.");
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

    /**
     * 获取读数据库，数据操作前必须调用.
     */
    @Override
    public void startReadableDatabase(){
        try {
            lock.lock();
            if(db == null || !db.isOpen()){
                db = this.dbHelper.getReadableDatabase();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
            lock.unlock();
        }

    }

    /**
     * 获取写数据库，数据操作前必须调用,不开启事务.
     */
    @Override
    public void startWritableDatabase(){
        startWritableDatabase(false);
    }
	
	/**
	 * 获取写数据库，数据操作前必须调用.
	 * @param transaction 是否开启事务
	 */
    @Override
	public void startWritableDatabase(boolean transaction){
		try {
			lock.lock();
			if(db == null || !db.isOpen()){
			    db = this.dbHelper.getWritableDatabase();
			}
			if(db!=null && transaction){
				db.beginTransaction();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
	}
	
	/**
	 * 关闭数据库，数据操作后必须调用.
	 */
    @Override
	public void closeDatabase(){
		try {
			lock.lock();
			if(db!=null){
				if(db.inTransaction()){
					db.setTransactionSuccessful();
					db.endTransaction();
				}
				if(db.isOpen()){
					db.close();
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
	}

    /**
     *
     * 检查DB是否已经打开.
     */
    private void checkDBOpened(){
        if(db == null){
            throw new RuntimeException("先调用 startReadableDatabase()或者startWritableDatabase(boolean transaction)初始化数据库。");
        }
    }

	/**
	 * 打印当前sql语句.
	 *
	 * @param sql sql语句，带？
	 * @param args 绑定变量
	 * @return 完整的sql
	 */
	private String getLogSql(String sql, Object[] args) {
		if (args == null || args.length == 0) {
			return sql;
		}
		for (int i = 0; i < args.length; i++) {
			//？号的前一个是% 不拼接''
			int index = sql.indexOf("?");
			if("%".equals(String.valueOf(sql.charAt(index-1)))){
				sql = sql.replaceFirst("\\?", String.valueOf(args[i]));
			}else{
				sql = sql.replaceFirst("\\?", "'" + String.valueOf(args[i]) + "'");
			}

		}
		return sql;
	}
	

}
