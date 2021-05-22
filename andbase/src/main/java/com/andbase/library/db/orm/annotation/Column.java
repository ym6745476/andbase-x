package com.andbase.library.db.orm.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 列
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { java.lang.annotation.ElementType.FIELD })
public @interface Column {
	
	/**
	 * 列名.
	 * @return the string
	 */
	public abstract String name();

	/**
	 * 列类型.
	 * 支持：INTEGER，INT，BIGINT，FLOAT，DOUBLE，BLOB
	 * @Column(name = "xxx", type = "INTEGER")
	 * @return the string
	 */
	public abstract String type() default "";

	/**
	 * 字段长度.
	 * @return the int
	 */
	public abstract int length() default 0;
	
}

