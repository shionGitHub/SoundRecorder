package com.yishion.record.adapter;

public interface ItemClickListener<T> {

    void onItemClick(int pos,T t);

    void onItemLongClick(int pos,T t);
}
