package com.andbase.library.db.orm.annotation;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 关联关系操作类型
 */
public class ActionType {
    
    /** 对于关系表只关联查询，多个用符号"下横线"分割. */
    public static final String query = "query";
    
    /** 对于关系表只关联插入. */
    public static final String insert = "insert";

    /** 对于关系表只关联更新. */
    public static final String update = "update";
    
    /** 对于关系表只关联删除. */
    public static final String delete = "delete";

}
