package com.yishion.record.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.yishion.record.R;
import com.yishion.record.bean.RecordItem;
import com.yishion.record.db.RecordDbUtils;

import java.io.File;
import java.io.IOException;

public class RecordService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private MediaRecorder mMediaRecorder;
    private String mFileName;
    private File mFilePath;
    private long mRecordStartTime = 0L;
    private long mRecordEndTime = 0L;

    //开始录音
    private void startRecording() {
        if (mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();

        //设置音频源
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置输出文件格式
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //设置输出文件名
        mMediaRecorder.setOutputFile(mFilePath.getAbsolutePath());
        //设置音频编码器
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        //这两个参数是高质量录音，可以设置或者不设置
        mMediaRecorder.setAudioSamplingRate(44100);
        mMediaRecorder.setAudioEncodingBitRate(192000);

        try {
            //准备
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("TAG", "prepare fail !----------" + e.getMessage());
            mRecordStartTime = 0L;
            return;
        }
        //开始
        mMediaRecorder.start();

        //计时
        mRecordStartTime = System.currentTimeMillis();
        Toast.makeText(getApplicationContext(), "Recording Start !", Toast.LENGTH_LONG).show();
    }

    //停止录音
    private void stopRecording() {
        if (mMediaRecorder != null) {
            //停止
            mMediaRecorder.stop();
            //统计一下结束时间
            mRecordEndTime = (mRecordStartTime == 0L) ? 0L : System.currentTimeMillis();
            //释放
            mMediaRecorder.release();
            //内存回收
            mMediaRecorder = null;
        }
    }


    //设置文件名和路径
    private void setFileNameAndPath() {
        //文件公共路径
        File commonFile = this.getExternalFilesDir("MySounder");
        //文件公共前缀
        String filePref = getResources().getString(R.string.default_file_name);
        //当前数据库中的录音数据个数
        int count = RecordDbUtils.count();

        do {
            //创建出来的文件名称可能已经存在了，所以要判断一下
            mFileName = filePref + "_" + (++count) + ".3gp";
            mFilePath = new File(commonFile, mFileName);
            //当是个文件夹或者是文件已经存在就要重新创建一次
        } while (mFilePath.isDirectory()
                || mFilePath.exists());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setFileNameAndPath();
        startRecording();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopRecording();
        saveRecordFiletoDatabase();
        super.onDestroy();
    }

    //保存录音记录到数据库
    private void saveRecordFiletoDatabase() {
        RecordItem item = new RecordItem();
        item.recordName = mFileName;
        item.recordPath = mFilePath.getAbsolutePath();
        item.recordTime = mRecordEndTime - mRecordStartTime;
        item.recordAddTime = mFilePath.lastModified();//使用最后修改时间
        RecordDbUtils.saveData(item);
        String path = getString(R.string.toast_recording_finish) + " " + mFilePath;
        Toast.makeText(getApplicationContext(), path, Toast.LENGTH_SHORT).show();
    }

}
