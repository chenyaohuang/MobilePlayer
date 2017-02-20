package com.guohui.fasttransfer.base;

/**
 * Created by nangua on 2016/5/19.
 */
public class DBNote {
    //表名
    public static final String DEVICE = "devices";
    //字段名
    public static final String _DEVICE_NAME = "name";

    //表的所有的字段
    public static final String[] all = {_DEVICE_NAME};

    //SQL建表语句
    public static final String CREATE_DEVICE_TABLE = String.format(
            "create table %s(%s text)",
            DEVICE,
            _DEVICE_NAME
    );

    //SQL删表语句
    public static final String DROP_DEVICE = String.format(
            "drop table if exists %s",
            DEVICE
    );
}
