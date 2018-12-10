package com.yishion.record.frags;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import com.yishion.record.R;
import com.yishion.record.service.RecordService;


public class RecordFragment extends Fragment
        implements Chronometer.OnChronometerTickListener,
        View.OnClickListener {

    private Chronometer chronometer;
    private TextView textView;
    private FloatingActionButton fab;

    private View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            View view = inflater.inflate(R.layout.fragment_record, container, false);
            chronometer = view.findViewById(R.id.chronometer);
            textView = view.findViewById(R.id.status_text);
            fab = view.findViewById(R.id.fab);
            mRootView = view;
        }
        else {
            ViewGroup group = (ViewGroup) mRootView.getParent();
            if (group != null) {
                group.removeView(mRootView);
            }
        }
        return mRootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //绑定监听
        chronometer.setOnChronometerTickListener(this);
        fab.setOnClickListener(this);

    }

    private int count = 0;
    private String record_in_progress;

    private boolean isStart = false;//是否开始录音

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        record_in_progress = getResources().getString(R.string.record_in_progress);
    }

    @Override
    public void onChronometerTick(Chronometer chronometer) {
        //显示的内容更新
        switch (count % 3) {
            case 0:
                textView.setText(String.format("%s%s", record_in_progress, "."));
                break;
            case 1:
                textView.setText(String.format("%s%s", record_in_progress, ".."));
                break;
            case 2:
                textView.setText(String.format("%s%s", record_in_progress, "..."));
                break;
        }
        count++;


    }

    @Override
    public void onClick(View v) {
        if (getContext() != null && !isStart) {
            //录音标记设置为true
            isStart = true;
            startRecord(getContext());
        }
        else if (getContext() != null && isStart) {
            //录音标记设置为false
            isStart = false;
            stopRecord(getContext());
        }
        else {
            //not to do
        }

    }

    //开始录音
    public void startRecord(@NonNull Context context) {

        //更换图标
        fab.setImageResource(R.drawable.ic_media_stop);

        //设置开始时间
        chronometer.setBase(SystemClock.elapsedRealtime());
        //开始计时
        chronometer.start();
        //计数清零
        count = 0;

        //开启录音的服务
        Intent intent = new Intent(context, RecordService.class);
        context.startService(intent);

    }

    private void stopRecord(@NonNull Context context) {

        //设置开始时间
        chronometer.setBase(SystemClock.elapsedRealtime());
        //停止计时
        chronometer.stop();
        //计数清零
        count = 0;

        //更换图标
        fab.setImageResource(R.drawable.ic_mic_white_36dp);
        textView.setText(R.string.record_prompt);

        //停止录音服务
        Intent intent = new Intent(context, RecordService.class);
        context.stopService(intent);
    }


}
