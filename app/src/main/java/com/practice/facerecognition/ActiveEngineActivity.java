package com.practice.facerecognition;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.enums.RuntimeABI;
import com.practice.facerecognition.util.DatabaseHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ActiveEngineActivity extends BaseActivity {
    private static final String TAG = "ActiveEngineActivity";
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    // 在线激活所需的权限
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE
    };
    boolean libraryExists = true;
    // Demo 所需的动态库文件
    private static final String[] LIBRARIES = new String[]{
            // 人脸相关
            "libarcsoft_face_engine.so",
            "libarcsoft_face.so",
            // 图像库相关
            "libarcsoft_image_util.so",
    };

    // API信息输入框
    private EditText appIdEditText;
    private EditText sdkKeyEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_engine);

        appIdEditText = findViewById(R.id.appIdEditText);
        sdkKeyEditText = findViewById(R.id.sdkKeyEditText);
    }

    public void doActiveEngineButtonClick(View view) {
        String appId = appIdEditText.getText().toString();
        String sdkKey = sdkKeyEditText.getText().toString();

        DatabaseHelper dbHelper = new DatabaseHelper(ActiveEngineActivity.this);
        String isActivated = dbHelper.getApiInfo()[2];

        // 未激活
        if (isActivated.equals("0")) {
            // 输入信息为空，不再进一步执行
            if (appId.equals("") || sdkKey.equals("")) {
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("请完整地填写API信息！")
                        .setPositiveButton(getString(R.string.ok), null)
                        .show();
                return;
            }

            dbHelper.updateApiInfo(appId, sdkKey, "0");

            libraryExists = checkSoFile(LIBRARIES);
            activeEngine(null);
        }
        // 已激活
        else {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("引擎已激活，无需再次激活！")
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
    }

    /**
     * 检查能否找到动态链接库，如果找不到，请修改工程配置
     *
     * @param libraries 需要的动态链接库
     * @return 动态库是否存在
     */
    private boolean checkSoFile(String[] libraries) {
        File dir = new File(getApplicationInfo().nativeLibraryDir);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return false;
        }
        List<String> libraryNameList = new ArrayList<>();
        for (File file : files) {
            libraryNameList.add(file.getName());
        }
        boolean exists = true;
        for (String library : libraries) {
            exists &= libraryNameList.contains(library);
        }
        return exists;
    }

    @Override
    void afterRequestPermission(int requestCode, boolean isAllGranted) {
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            if (isAllGranted) {
                activeEngine(null);
            } else {
                showToast(getString(R.string.permission_denied));
            }
        }
    }

    /**
     * 激活引擎
     *
     * @param view
     */
    public void activeEngine(final View view) {
        // 数据库中没有API信息，弹出失败提示
        final DatabaseHelper dbHelper = new DatabaseHelper(this);
        String[] apiInfo = dbHelper.getApiInfo();
        if (apiInfo.length == 0 || (apiInfo[0].equals("") || apiInfo[1].equals(""))) {
            showToast("引擎初始化失败：找不到APP ID和SDK KEY！");
            return;
        }
        final String appId = apiInfo[0];
        final String sdkKey = apiInfo[1];

        if (!libraryExists) {
            showToast(getString(R.string.library_not_found));
            return;
        }
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            return;
        }
        if (view != null) {
            view.setClickable(false);
        }
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) {
                RuntimeABI runtimeABI = FaceEngine.getRuntimeABI();
                Log.i(TAG, "subscribe: getRuntimeABI() " + runtimeABI);

                long start = System.currentTimeMillis();
                int activeCode = FaceEngine.activeOnline(ActiveEngineActivity.this, appId, sdkKey);
                Log.i(TAG, "subscribe cost: " + (System.currentTimeMillis() - start));
                emitter.onNext(activeCode);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer activeCode) {
                        if (activeCode == ErrorInfo.MOK) {
                            // 更新数据库信息
                            dbHelper.updateApiAsActivated();

                            showToast(getString(R.string.active_success));
                        } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                            showToast(getString(R.string.already_activated));
                        } else {
                            showToast(getString(R.string.active_failed, activeCode));
                        }

                        if (view != null) {
                            view.setClickable(true);
                        }
                        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
                        int res = FaceEngine.getActiveFileInfo(ActiveEngineActivity.this, activeFileInfo);
                        if (res == ErrorInfo.MOK) {
                            Log.i(TAG, activeFileInfo.toString());
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast(e.getMessage());
                        if (view != null) {
                            view.setClickable(true);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}