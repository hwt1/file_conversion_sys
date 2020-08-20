package com.hwt.constant;



/**
 * @author 黄雯婷
 * @version 1.0
 * @description: sql导入常量
 * @date 2020-08-18 10:10:11
 **/
public class SQLConstant {
    public static final String[] SQL_EXCEL_HEADER={"字段名","字段名描述","类型","主键(yes/no)","允许null(yes/no)","默认值","备注","是否索引(yes/no)"};
    //sql类型
    public static final Integer CREATE_FLAG=1;//建表
    public static final Integer MODIFY_FLAG=2;//修改表
}
