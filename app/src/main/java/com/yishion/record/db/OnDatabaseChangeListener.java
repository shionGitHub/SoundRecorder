package com.yishion.record.db;

/**
 * 数据库数据有变化的时候的更新
 */
public interface OnDatabaseChangeListener<T> {

    void onDataAdd(T... t);//数据增加

    void onDataUpdate(T... t);//数据跟新

    void onDataDelete(T... t);//数据删除
}
