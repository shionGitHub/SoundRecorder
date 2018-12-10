package com.yishion.record.db;

public interface RecordCommon {

    String RECORD_DB = "RecordItem.db";
    String RECORD_BOOK = "Record";
    int VERSION = 1;

    String ID = "id";
    String NAME = "name";
    String PATH = "path";
    String TIME = "time";
    String CREATE_TIME = "create_time";


    String SQL_RECORD = "create table " + RECORD_BOOK + "(" + ID + " text primary key," + NAME + " text not null," + PATH + " text not null," + TIME + " integer not null," + CREATE_TIME + " integer not null);";

    String DROP_RECORD=" drop table "+RECORD_BOOK +" if exists";

}
