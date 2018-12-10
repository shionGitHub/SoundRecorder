package com.yishion.record.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yishion.record.App;
import com.yishion.record.bean.RecordItem;

import java.util.ArrayList;
import java.util.List;

public class RecordDbUtils {

    //这里采用观察者模式来动态更新数据库数据的变更

    private static List<OnDatabaseChangeListener<RecordItem>> sListener = new ArrayList<>();

    //添加观察者
    public static void addListener(OnDatabaseChangeListener<RecordItem> listener) {
        if (listener != null) {
            if (!sListener.contains(listener)) {
                sListener.add(listener);
            }
        }
    }

    //删除观察者
    public static void deleteListener(OnDatabaseChangeListener<RecordItem> listener) {
        if (listener != null) {
            sListener.remove(listener);
        }
    }


    private static RecordSQLiteOpenHelper mHelper;


    private static void init() {
        if (mHelper == null) {
            mHelper = new RecordSQLiteOpenHelper(App.getInstance());
        }
    }

    public static synchronized void saveData(RecordItem item) {
        init();
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RecordCommon.ID, item.uuid);
        values.put(RecordCommon.NAME, item.recordName);
        values.put(RecordCommon.PATH, item.recordPath);
        values.put(RecordCommon.TIME, item.recordTime);
        values.put(RecordCommon.CREATE_TIME, item.recordAddTime);
        long insert_id = db.insert(RecordCommon.RECORD_BOOK, null, values);
        Log.e("1234", "------------------insert_id: " + insert_id);
        if (insert_id > 0) {
            for (OnDatabaseChangeListener<RecordItem> listener : sListener) {
                listener.onDataAdd(item);
            }
        }
    }

    public static synchronized void deleteData(RecordItem item) {
        init();
        SQLiteDatabase db = mHelper.getWritableDatabase();

        String whereCause = RecordCommon.ID + " = ? or " + RecordCommon.NAME + " = ?";
        String args[] = new String[]{item.uuid, item.recordName};

        int i = db.delete(RecordCommon.RECORD_BOOK, whereCause, args);

        Log.e("1234", "------------------delete_id: " + i);
        if (i > 0) {
            for (OnDatabaseChangeListener<RecordItem> listener : sListener) {
                listener.onDataDelete(item);
            }
        }
    }


    public static synchronized void updateData(RecordItem item) {
        init();
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RecordCommon.ID, item.uuid);
        values.put(RecordCommon.NAME, item.recordName);
        values.put(RecordCommon.PATH, item.recordPath);
        values.put(RecordCommon.TIME, item.recordTime);
        values.put(RecordCommon.CREATE_TIME, item.recordAddTime);

        String whereCause = RecordCommon.ID + " = ? or " + RecordCommon.NAME + " = ?";
        String args[] = new String[]{item.uuid, item.recordName};


        long update_id = db.update(RecordCommon.RECORD_BOOK, values, whereCause, args);

        Log.e("1234", "------------------update_id: " + update_id);
        if (update_id > 0) {
            for (OnDatabaseChangeListener<RecordItem> listener : sListener) {
                listener.onDataUpdate(item);
            }
        }
    }

    public static synchronized List<RecordItem> queryAll() {
        init();
        SQLiteDatabase db = mHelper.getWritableDatabase();
        List<RecordItem> list = new ArrayList<>();
        Cursor cursor = db.query(RecordCommon.RECORD_BOOK, null,
                null, null,
                null, null, null);
        int uuid_index = cursor.getColumnIndex(RecordCommon.ID);
        int name_index = cursor.getColumnIndex(RecordCommon.NAME);
        int path_index = cursor.getColumnIndex(RecordCommon.PATH);
        int time_index = cursor.getColumnIndex(RecordCommon.TIME);
        int create_time_index = cursor.getColumnIndex(RecordCommon.CREATE_TIME);
        while (cursor.moveToNext()) {
            RecordItem item = new RecordItem();
            item.uuid = cursor.getString(uuid_index);
            item.recordName = cursor.getString(name_index);
            item.recordPath = cursor.getString(path_index);
            item.recordTime = cursor.getLong(time_index);
            item.recordAddTime = cursor.getLong(create_time_index);
            list.add(item);
        }
        cursor.close();
        return list;
    }


    public static synchronized int count() {
        init();
        SQLiteDatabase db = mHelper.getWritableDatabase();
        Cursor cursor = db.query(RecordCommon.RECORD_BOOK, null,
                null, null,
                null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

}
