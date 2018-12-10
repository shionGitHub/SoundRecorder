package com.yishion.record.frags;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yishion.record.R;
import com.yishion.record.bean.RecordItem;
import com.yishion.record.db.RecordDbUtils;

import java.io.File;

//录音文件的浏览，分享，重命名，删除
public class RecordFileViewerFragment extends DialogFragment implements View.OnClickListener {


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

    private static final String NAME = "NAME";
    private static final String PATH = "PATH";
    private static final String UUID = "UUID";
    private static final String ITEM = "ITEM";
    private TextView tvShare, tvRename, tvDelete, tvCancel;
    private String mFilePath;//文件路径
    private String mFileName;//文件名称
    private String mUUID;//文件的id
    private RecordItem item;

    private FileObserver observer;//文件观察者

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        String path = context.getExternalFilesDir("MySounder").toString();
        observer = new FileObserver(path) {
            @Override
            public void onEvent(int event,
                                String path) {
                Log.e("1234", "-----------event: " + event + "---------\n path: " + path);
            }
        };
    }

    public static RecordFileViewerFragment newInstance(RecordItem item) {
        RecordFileViewerFragment fragment = new RecordFileViewerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(NAME, item.recordName);
        bundle.putString(PATH, item.recordPath);
        bundle.putString(UUID, item.uuid);
        bundle.putSerializable(ITEM, item);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mUUID = bundle.getString(UUID);
            mFileName = bundle.getString(NAME);
            mFilePath = bundle.getString(PATH);
            item = (RecordItem) bundle.getSerializable(ITEM);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recordfile_viewer, container, false);
        tvShare = view.findViewById(R.id.tvShare);
        tvRename = view.findViewById(R.id.tvRename);
        tvDelete = view.findViewById(R.id.tvDelete);
        tvCancel = view.findViewById(R.id.tvCancel);
        tvShare.setOnClickListener(this);
        tvRename.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel: {
                dismiss();
            }
            break;
            case R.id.tvShare: {
                shareFile();
                dismiss();
            }
            break;
            case R.id.tvDelete: {
                deleteFile();
                dismiss();
            }
            break;
            case R.id.tvRename: {
                renameFile(getContext());
                dismiss();
            }
            break;
        }
    }

    //分享文件,让邮箱通过代理分享出去
    private void shareFile() {
        if (getContext() != null && !TextUtils.isEmpty(mFilePath)) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(mFilePath)));
            shareIntent.setType("audio/mp4");
            getContext().startActivity(Intent.createChooser(shareIntent,
                    getContext().getResources().getText(R.string.send_to)));

        }
    }

    //删除录音
    public void deleteFile() {
        // File delete confirm
        if (getContext() == null || TextUtils.isEmpty(mUUID)) return;
        Context mContext = getContext();
        AlertDialog.Builder confirmDelete = new AlertDialog.Builder(getContext());
        confirmDelete.setTitle(mContext.getString(R.string.dialog_title_delete));
        confirmDelete.setMessage(mContext.getString(R.string.dialog_text_delete));
        confirmDelete.setCancelable(true);
        confirmDelete.setPositiveButton(mContext.getString(R.string.dialog_action_yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //这里删除指定的uuid
                        RecordItem item = new RecordItem();
                        item.uuid = mUUID;
                        RecordDbUtils.deleteData(item);
                        dialog.cancel();
                    }
                });
        confirmDelete.setNegativeButton(mContext.getString(R.string.dialog_action_no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = confirmDelete.create();
        alert.show();
    }


    //修改录音名称
    public void renameFile(final Context context) {
        // File rename dialog
        AlertDialog.Builder renameFileBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_rename_file, null);

        final EditText input = view.findViewById(R.id.new_name);

        renameFileBuilder.setTitle(context.getString(R.string.dialog_title_rename));
        renameFileBuilder.setCancelable(true);
        renameFileBuilder.setPositiveButton(context.getString(R.string.dialog_action_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            String content = input.getText().toString();
                            if (TextUtils.isEmpty(content)) {
                                Toast.makeText(context, "文件名称不能为空!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            else {
                                String value = content + ".3gp";
                                rename(context, value);
                            }
                        } catch (Exception e) {
                            Log.e("1234", "exception", e);
                        }

                        dialog.cancel();
                    }
                });
        renameFileBuilder.setNegativeButton(context.getString(R.string.dialog_action_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        renameFileBuilder.setView(view);
        AlertDialog alert = renameFileBuilder.create();
        alert.show();

    }

    //这个方法是修改文件名称
    private void rename(Context context, String fileName) {
        File commonFile = context.getExternalFilesDir("MySounder");
        File file = new File(commonFile, fileName);
        if (file.isDirectory() || file.exists()) {
            String format = context.getResources().getString(R.string.toast_file_exists);
            Toast.makeText(context, String.format(format, fileName), Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            File oldFile = new File(mFilePath);
            boolean isRenameSuccessful = oldFile.renameTo(file);
            if (isRenameSuccessful) {
                Toast.makeText(context, "修改成功！", Toast.LENGTH_SHORT).show();
                item.recordPath = oldFile.getAbsolutePath();
                item.recordName = fileName;
                RecordDbUtils.updateData(item);
            }
            else {
                Toast.makeText(context, "修改不成功！", Toast.LENGTH_SHORT).show();
            }

        }
    }


}
