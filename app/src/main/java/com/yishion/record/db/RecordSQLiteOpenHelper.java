package com.yishion.record.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RecordSQLiteOpenHelper extends SQLiteOpenHelper {


    public RecordSQLiteOpenHelper(Context context) {
        super(context, RecordCommon.RECORD_DB, null, RecordCommon.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RecordCommon.SQL_RECORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(RecordCommon.DROP_RECORD);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
//        Log.e("1234", "-----数据库打开了 " + db.toString());
    }

    @Override
    public synchronized void close() {
        super.close();
    }
}
