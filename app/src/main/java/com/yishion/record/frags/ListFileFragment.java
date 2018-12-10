package com.yishion.record.frags;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yishion.record.R;
import com.yishion.record.adapter.ItemClickListener;
import com.yishion.record.adapter.RecordAdapter;
import com.yishion.record.bean.RecordItem;
import com.yishion.record.db.OnDatabaseChangeListener;
import com.yishion.record.db.RecordDbUtils;


public class ListFileFragment extends Fragment implements
        ItemClickListener<RecordItem>,
        OnDatabaseChangeListener<RecordItem> {


    private View mRootView;
    private RecyclerView mRecyclerView;
    private RecordAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecordDbUtils.addListener(this);//注册观察者
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RecordDbUtils.deleteListener(this);//解除观察者
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            View view = inflater.inflate(R.layout.fragment_list_file, container, false);
            mRecyclerView = view.findViewById(R.id.recycler);
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new RecordAdapter();
        mAdapter.setItemItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.addDatas(RecordDbUtils.queryAll());

    }

    //点击
    @Override
    public void onItemClick(int pos, RecordItem item) {
        PlaybackFragment fragment = PlaybackFragment.instance(item);
        fragment.show(getChildFragmentManager(), "PlaybackFragment");
    }

    //长按
    @Override
    public void onItemLongClick(int pos, RecordItem item) {
        RecordFileViewerFragment fragment = RecordFileViewerFragment.newInstance(item);
        fragment.show(getChildFragmentManager(), "RecordFileViewerFragment");
    }


    @Override
    public void onDataAdd(RecordItem... t) {
        mAdapter.addDatas(t);
    }

    @Override
    public void onDataUpdate(RecordItem... t) {
        mAdapter.updateDatas(t);
    }

    @Override
    public void onDataDelete(RecordItem... t) {
        mAdapter.deleteDatas(t);
    }
}
