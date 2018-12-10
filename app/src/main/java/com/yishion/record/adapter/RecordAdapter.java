package com.yishion.record.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yishion.record.R;
import com.yishion.record.bean.RecordItem;
import com.yishion.record.utils.DateUtil;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder>
        implements View.OnClickListener, View.OnLongClickListener {

    private List<RecordItem> mDataList;

    public RecordAdapter() {
        this.mDataList = new ArrayList<>();
    }

    private ItemClickListener<RecordItem> itemItemClickListener;

    public void setItemItemClickListener(ItemClickListener<RecordItem> itemItemClickListener) {
        this.itemItemClickListener = itemItemClickListener;
    }

    public void addDatas(RecordItem... items) {
        int start = mDataList.size();
        Collections.addAll(mDataList, items);
        int end = mDataList.size();
        notifyItemRangeInserted(start, end - start);
    }

    public void addDatas(List<RecordItem> items) {
        int start = mDataList.size();
        mDataList.addAll(items);
        int end = mDataList.size();
        notifyItemRangeInserted(start, end - start);
    }

    public void deleteDatas(RecordItem... items) {
        if (items != null && items.length > 0) {
            for (RecordItem item : items) {
                int index = mDataList.indexOf(item);
                mDataList.remove(item);
                notifyItemRemoved(index);
            }
        }

    }

    public void updateDatas(RecordItem... items) {
        if (items != null && items.length > 0) {
            for (RecordItem item : items) {
                int index = mDataList.indexOf(item);
                mDataList.set(index, item);
                notifyItemChanged(index);
            }
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View v = inflater.inflate(R.layout.cell_record, viewGroup, false);
        ViewHolder holder = new ViewHolder(v);
        v.setTag(R.id.id_tag_recycler, holder);
        v.setOnClickListener(this);
        v.setOnLongClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        RecordItem item = mDataList.get(i);
        viewHolder.name.setText(item.recordName);
        viewHolder.time.setText(DateUtil.formatElapsedTime(item.recordTime));
        viewHolder.date.setText(DateUtil.format(item.recordAddTime));
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public void onClick(View v) {
        ItemClickListener<RecordItem> listener = itemItemClickListener;
        if (listener != null) {
            ViewHolder holder = (ViewHolder) v.getTag(R.id.id_tag_recycler);
            int pos = holder.getAdapterPosition();
            listener.onItemClick(pos, mDataList.get(pos));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        ItemClickListener<RecordItem> listener = itemItemClickListener;
        if (listener != null) {
            ViewHolder holder = (ViewHolder) v.getTag(R.id.id_tag_recycler);
            int pos = holder.getAdapterPosition();
            listener.onItemLongClick(pos, mDataList.get(pos));
        }
        return true;
    }

    public static final class ViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public ImageView image;
        public TextView name;
        public TextView time;
        public TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.cardView = itemView.findViewById(R.id.card);
            this.image = itemView.findViewById(R.id.image);
            this.name = itemView.findViewById(R.id.name);
            this.time = itemView.findViewById(R.id.time);
            this.date = itemView.findViewById(R.id.date);
        }
    }

}
