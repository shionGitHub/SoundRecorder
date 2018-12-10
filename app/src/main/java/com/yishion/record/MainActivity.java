package com.yishion.record;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.yishion.record.frags.ListFileFragment;
import com.yishion.record.frags.RecordFragment;

import java.security.Permission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private List<String> mDatas;
    private List<Fragment> mFrags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mTabLayout = findViewById(R.id.tab);
        mViewPager = findViewById(R.id.viewpager);
        mDatas = new ArrayList<>();
        mFrags = new ArrayList<>();
        Collections.addAll(mDatas, getResources().getStringArray(R.array.arr));
        mFrags.add(new RecordFragment());
        mFrags.add(new ListFileFragment());

        Adapter adapter = new Adapter(getSupportFragmentManager(), mFrags, mDatas);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private static final String[] PERMS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_PERMISSION_CODE = 0x1024;

    @Override
    protected void onStart() {
        super.onStart();
        //这里选择在这个生命周期里面回调是因为，弹出的Dialog之后,Activity只会执行onPuase--onResume,
        //不会重复发生，
        //只有跳转到设置页面之后，返回时候又会请求一次，判断
        requestPrem();
    }

    //申请权限
    private void requestPrem() {
        //适配以下权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, PERMS[0])
                    != PackageManager.PERMISSION_GRANTED
                    ||
                    ActivityCompat.checkSelfPermission(this, PERMS[1])
                            != PackageManager.PERMISSION_GRANTED
                    ||
                    ActivityCompat.checkSelfPermission(this, PERMS[2])
                            != PackageManager.PERMISSION_GRANTED
                    ) {
                ActivityCompat.requestPermissions(this, PERMS, REQUEST_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_PERMISSION_CODE) return;
        boolean hasAllGranted = true;//已有所有权限
        boolean hasCancelTint = true;//未勾选不在提示
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                hasAllGranted = false;//说明存在没有授权的权限了
            }
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                hasCancelTint = false;//说明当前权限勾选了不在提示了
            }
        }

        //权限都过了
        //权限没有，勾选了不在提示的，要跳转到
        if (!hasAllGranted && !hasCancelTint) {
            Log.e("1234", "-------------------------1234");
            showDialog();
        }
        else if (!hasAllGranted && hasCancelTint) {//继续申请
            requestPrem();
        }
    }

    //用户勾选不在提示时候的显示
    private void showDialog() {
        //解释原因，并且引导用户至设置页手动授权
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.message_permission);
        builder.setPositiveButton(R.string.go_permission,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //引导用户至设置页手动授权
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton(R.string.dialog_action_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //取消按钮了，还的申请
                        requestPrem();
                    }
                });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                //其他方式取消也得申请；
                Log.e("1234", "----------------onCancel---------");
                requestPrem();
            }
        });
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.setting) {
            //todo
        }
        return true;
    }

    public static class Adapter extends FragmentPagerAdapter {

        private List<String> arr;
        private List<Fragment> mFrags;

        public Adapter(FragmentManager fm, List<Fragment> fragments, List<String> list) {
            super(fm);
            this.arr = list;
            this.mFrags = fragments;
        }

        @Override
        public Fragment getItem(int i) {
            return this.mFrags.get(i);
        }

        @Override
        public int getCount() {
            return this.mFrags.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return arr.get(position);
        }
    }


}
