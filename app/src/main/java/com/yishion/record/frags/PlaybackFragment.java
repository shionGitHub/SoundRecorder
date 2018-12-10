package com.yishion.record.frags;


import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yishion.record.R;
import com.yishion.record.bean.RecordItem;
import com.yishion.record.utils.DateUtil;

import java.io.IOException;

//播放录音的界面
public class PlaybackFragment extends DialogFragment {

    private View mRootView;
    private TextView tvTitle;
    private SeekBar seekBar;
    private TextView tvStart, tvEnd;
    private FloatingActionButton fab;

    private String title;
    private long totalTime = 0L;//总的时间
    private long currentTime = 0L;//当前时间
    private String path;//播放路径

    private static final String TITLE = "TITLE";
    private static final String TIME = "TIME";
    private static final String PATH = "PATH";

    private boolean isPlaying;//是否播放状态
    private MediaPlayer mediaPlayer;

    public static PlaybackFragment instance(RecordItem item) {
        PlaybackFragment fragment = new PlaybackFragment();

        Bundle bundle = new Bundle();
        bundle.putString(TITLE, item.recordName);
        bundle.putLong(TIME, item.recordTime);
        bundle.putString(PATH, item.recordPath);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            title = bundle.getString(TITLE);
            totalTime = bundle.getLong(TIME);
            path = bundle.getString(PATH);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if (window != null) {
            //无标题
            window.requestFeature(Window.FEATURE_NO_TITLE);
            //背景透明
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            View view = inflater.inflate(R.layout.fragment_playback, container, false);
            tvTitle = view.findViewById(R.id.file_name_text_view);
            seekBar = view.findViewById(R.id.seekbar);
            tvStart = view.findViewById(R.id.current_progress_text_view);
            tvEnd = view.findViewById(R.id.file_length_text_view);
            fab = view.findViewById(R.id.fab_play);
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
        tvTitle.setText(title);
        tvStart.setText(DateUtil.formatElapsedTime(currentTime));
        tvEnd.setText(DateUtil.formatElapsedTime(totalTime));
        //修改底部进度背景
        Drawable drawable = seekBar.getProgressDrawable();
        DrawableCompat.setTint(drawable, getResources().getColor(R.color.colorAccent));
        Drawable thumb = seekBar.getThumb();
        DrawableCompat.setTint(thumb, getResources().getColor(R.color.colorAccent));
        //点击事件
        fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onPlay();
                        isPlaying = !isPlaying;
                    }
                });
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progress,
                                                  boolean fromUser) {
                        if (fromUser) {//来自用户
                            int pro = (int) ((progress * 1.0f / seekBar.getMax()) * totalTime);
                            if (mediaPlayer == null) {//还没有开始
                                preparePlayFromPoint(pro);
                            }
                            else {//已经开始过了
                                mediaPlayer.seekTo(pro);
                            }
                            tvStart.setText(DateUtil.formatElapsedTime(pro));
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //从来没有开始的情况
                        //已经开始的情况，或者暂停或者正在播放
                        //停止更新进度条
                        mHandler.removeCallbacksAndMessages(null);
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (mediaPlayer != null) {
                            int pro = (int) ((seekBar.getProgress() * 1.0f / seekBar.getMax()) * totalTime);
                            mediaPlayer.seekTo(pro);
                            if (mediaPlayer.isPlaying()) {
                                updateSeekBar();
                            }
                        }
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        stop();
    }

    @Override
    public void onStop() {
        super.onStop();
        stop();
    }

    //设置初始播放的进度
    private void preparePlayFromPoint(int progress) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stop();
                    }
                });
                mediaPlayer.prepare();
                mediaPlayer.seekTo(progress);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    //播放 //暂停 //再次播放
    private void onPlay() {
        if (isPlaying) {//播放
            pause();
        }
        else {
            //1.还没有开始播放器
            //2.已经开始播放，但是现在是暂停
            if (mediaPlayer == null) {
                play();
            }
            else {
                resume();
            }
        }
    }

    //第一次开始录音的播放
    private void play() {
        fab.setImageResource(R.drawable.ic_media_pause);

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        mediaPlayer.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {

                        stop();
                    }
                });
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            updateSeekBar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //已经打开了播放器，现在要播放了
    private void resume() {
        fab.setImageResource(R.drawable.ic_media_pause);

        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }

        updateSeekBar();
    }

    //暂停
    private void pause() {
        fab.setImageResource(R.drawable.ic_media_play);

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }

        mHandler.removeCallbacksAndMessages(null);
    }

    //播放文成要停止播放
    private void stop() {
        fab.setImageResource(R.drawable.ic_media_play);
        tvStart.setText(tvEnd.getText());
        seekBar.setProgress(seekBar.getMax());
        mHandler.removeCallbacksAndMessages(null);
        isPlaying = !isPlaying;

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }


    }


    private Handler mHandler = new Handler(Looper.getMainLooper());
    Runnable mTaskRunnable = new Runnable() {
        @Override
        public void run() {
            //更新进度条
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {

                int curr = mediaPlayer.getCurrentPosition();
                int total = mediaPlayer.getDuration();
                if (curr < total) {
                    tvStart.setText(DateUtil.formatElapsedTime(curr));
//                    tvEnd.setText(DateUtil.formatElapsedTime(total));
                    seekBar.setProgress((int) (curr * 1.0f / total * seekBar.getMax()));
                    mHandler.postDelayed(this, 1000);
                }
                else {
                    tvStart.setText(tvEnd.getText());
//                    tvEnd.setText(DateUtil.formatElapsedTime(total));
                    seekBar.setProgress(seekBar.getMax());
                    mHandler.removeCallbacksAndMessages(null);
                }

            }
        }
    };

    //更新进度条
    private void updateSeekBar() {
        mHandler.post(mTaskRunnable);
    }


    //处理拖拽进度条的播放逻辑


}
