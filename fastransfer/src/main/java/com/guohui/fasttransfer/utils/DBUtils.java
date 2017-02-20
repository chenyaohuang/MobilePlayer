package com.guohui.fasttransfer.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by nangua on 2016/5/19.
 */
public class DBUtils  {
    //数据库版本号
    final static int VERSION = 1;
    //声明数据库
    private SQLiteDatabase db;
    public DBUtils(Context context,String dbname) {
        DbHelper helper = new DbHelper(context,dbname,null,1);
        //获得数据库
        db = helper.getWritableDatabase();
    }

    public boolean insertDevice(String filename) {
        ContentValues values = new ContentValues();
        values.put("name", filename);
        long result = db.insert("Device",null,values);
        return true?false:result!=-1;
    };

    public  ArrayList<String>  queryDevice() {
        ArrayList<String> filenames = new ArrayList<>();
        //查询Device表中所有的数据
        Cursor cursor = db.query("Device",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do{
                filenames.add(cursor.getString(cursor.getColumnIndex("name")));
            } while (cursor.moveToNext());
        }
        return filenames;
    }
}
