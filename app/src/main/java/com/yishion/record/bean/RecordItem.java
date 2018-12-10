package com.yishion.record.bean;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

//录音文件的信息类
public class RecordItem implements Serializable {

    public String uuid = UUID.randomUUID().toString();//录音的唯一及标记
    public String recordName;//录音文件的名称
    public long recordTime;//录音的时长
    public String recordPath;//录音文件的存储地址
    public long recordAddTime;//录音的添加日期

    public RecordItem() {
    }

    public RecordItem(String recordName, long recordTime, String recordPath) {
        this.recordName = recordName;
        this.recordTime = recordTime;
        this.recordPath = recordPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecordItem item = (RecordItem) o;
        return Objects.equals(uuid, item.uuid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "RecordItem{" +
                "uuid='" + uuid + '\'' +
                ", recordName='" + recordName + '\'' +
                ", recordTime=" + recordTime +
                ", recordPath='" + recordPath + '\'' +
                '}';
    }


}
