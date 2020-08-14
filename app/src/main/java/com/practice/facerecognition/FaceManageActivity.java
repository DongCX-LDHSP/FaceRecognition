package com.practice.facerecognition;

import android.Manifest;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.arcsoft.imageutil.ArcSoftImageFormat;
import com.arcsoft.imageutil.ArcSoftImageUtil;
import com.arcsoft.imageutil.ArcSoftImageUtilError;
import com.practice.facerecognition.faceserver.FaceServer;
import com.practice.facerecognition.util.DatabaseHelper;
import com.practice.facerecognition.widget.ProgressDialog;

import java.io.File;
import java.io.FilenameFilter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 批量注册页面
 */
public class FaceManageActivity extends BaseActivity {
    //注册图所在的目录
    // 根目录/Faces/
    private static final String ROOT_DIR =
            Environment.getExternalStorageDirectory().getAbsolutePath() +
                    File.separator +
                    "Faces";
    // 根目录/Faces/Register
    private static final String REGISTER_DIR =
            ROOT_DIR +
                    File.separator +
                    "Register";
    // 根目录/Faces/Failed
    private static final String REGISTER_FAILED_DIR =
            ROOT_DIR +
                    File.separator +
                    "Failed";

    // 用于执行批量注册循环代码块
    private ExecutorService executorService;

    // 用于展示处理结果
    private TextView tvNotificationRegisterResult;

    // 进度弹窗
    ProgressDialog progressDialog = null;

    // 所需要的权限
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private static String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_manage);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        executorService = Executors.newSingleThreadExecutor();
        tvNotificationRegisterResult = findViewById(R.id.notification_register_result);
        progressDialog = new ProgressDialog(this);
        FaceServer.getInstance().init(this);

        // 创建存储路径
        dirCreate();
    }

    @Override
    protected void onDestroy() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        FaceServer.getInstance().unInit();
        super.onDestroy();
    }

    // todo 批量注册代码块 - start
    // 检测权限，满足则开始注册
    public void batchRegister(View view) {
        if (checkPermissions(NEEDED_PERMISSIONS)) {
            clearFaceInfoTableBeforeRegister();
        } else {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        }
    }

    // 获取权限之后开始注册
    @Override
    void afterRequestPermission(int requestCode, boolean isAllGranted) {
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            if (isAllGranted) {
                clearFaceInfoTableBeforeRegister();
            } else {
                showToast(getString(R.string.permission_denied));
            }
        }
    }

    // 存储路径创建
    private void dirCreate() {
        File dir = new File(ROOT_DIR);
        // Faces文件夹
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // Faces/Register
        dir = new File(REGISTER_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // Faces/Failed
        dir = new File(REGISTER_FAILED_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // 存储路径检测
    private File[] dirCheck() {
        File dir = new File(REGISTER_DIR);
        if (!dir.exists()) {
            showToast(getString(R.string.batch_process_path_is_not_exists, REGISTER_DIR));
            return null;
        }
        if (!dir.isDirectory()) {
            showToast(getString(R.string.batch_process_path_is_not_dir, REGISTER_DIR));
            return null;
        }

        // 获取jpg图片文件
        final File[] jpgFiles = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(FaceServer.IMG_SUFFIX);
            }
        });

        // 没有jpg文件
        if (jpgFiles.length == 0) {
            showToast(getString(R.string.batch_register_folder_is_empty));
            return null;
        }

        // 有jpg文件
        return jpgFiles;
    }

    // 复制图片到失败文件夹
    private void copyFileToFailedFolder(File jpgFile) {
        File failedFile = new File(REGISTER_FAILED_DIR + File.separator + jpgFile.getName());
        if (!failedFile.getParentFile().exists()) {
            failedFile.getParentFile().mkdirs();
        }
        jpgFile.renameTo(failedFile);
    }

    // 显示注册结果
    private void showRegisterResult(final int successCount, final int failureCount) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                tvNotificationRegisterResult.append(
                        getString(R.string.batch_process_finished_info,
                                successCount + failureCount,
                                successCount,
                                failureCount,
                                REGISTER_FAILED_DIR.replaceFirst(
                                        "/storage/emulated/0",
                                        "sdcard")));
            }
        });
    }

    // 批量注册前清空人脸信息表
    private void clearFaceInfoTableBeforeRegister() {
        if (FaceServer.getInstance().getFaceNumber(this) > 0) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.batch_process_notification)
                    .setMessage(R.string.clear_face_text)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int deleteCount = doClearFaces();
                            showToast(getString(R.string.batch_face_clear_tip, deleteCount));

                            // 开始执行批量注册
                            doRegister();
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        }
        // 人脸库当前就是空，无需清空
        else {
            // 开始执行批量注册
            doRegister();
        }
    }

    // 批量注册方法
    private void doRegister() {
        // 存储路径检测
        final File[] jpgFiles = dirCheck();
        if (jpgFiles == null) {
            return;
        }

        // 获取jpg图片数量
        final int totalCount = jpgFiles.length;

        // 批量注册块
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // 初始化成功数量
                int successCount = 0;

                // 呼出进度弹窗设置处理结果
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.setMaxProgress(totalCount);
                        progressDialog.show();
                        tvNotificationRegisterResult.setText("");
                        tvNotificationRegisterResult.append(getString(R.string.batch_process_processing_please_wait));
                    }
                });

                // 批量注册循环
                for (int i = 0; i < totalCount; i++) {
                    // 更新进度条状态
                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog != null) {
                                progressDialog.refreshProgress(finalI);
                            }
                        }
                    });

                    // 对单个文件进行处理的代码块
                    final File jpgFile = jpgFiles[i];

                    // todo 第1次转换：转换为位图
                    Bitmap bitmap = BitmapFactory.decodeFile(jpgFile.getAbsolutePath());

                    // 转换为位图失败，复制到失败文件夹，开始新一轮循环
                    if (bitmap == null) {
                        copyFileToFailedFolder(jpgFile);
                        continue;
                    }

                    // todo 第2次转换：猜测是调整角度
                    bitmap = ArcSoftImageUtil.getAlignedBitmap(bitmap, true);

                    // 调整失败，复制到失败文件夹，开始新一轮循环
                    if (bitmap == null) {
                        copyFileToFailedFolder(jpgFile);
                        continue;
                    }

                    // todo 第3次转换：应该是位图转换为字节数组，然后用字节数组生成注册依据
                    byte[] bgr24 = ArcSoftImageUtil.createImageData(
                            bitmap.getWidth(),
                            bitmap.getHeight(),
                            ArcSoftImageFormat.BGR24);
                    int transformCode = ArcSoftImageUtil.bitmapToImageData(
                            bitmap,
                            bgr24,
                            ArcSoftImageFormat.BGR24);

                    // 转换失败，return
                    if (transformCode != ArcSoftImageUtilError.CODE_SUCCESS) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                tvNotificationRegisterResult.append("");
                            }
                        });
                        return;
                    }

                    // 提取文件名中的学号
                    String filename = jpgFile.getName().substring(0, jpgFile.getName().lastIndexOf("."));

                    // 这里就是进行人脸注册了
                    // FaceServer类好像是使用了 单例模式
                    boolean success = FaceServer.getInstance().registerBgr24(
                            FaceManageActivity.this,
                            bgr24,
                            bitmap.getWidth(),
                            bitmap.getHeight(),
                            filename);

                    // 若注册成功，则将注册学号存入人脸信息表
                    if (success) {
                        DatabaseHelper dbHelper = new DatabaseHelper(FaceManageActivity.this);
                        dbHelper.insertFaceInfo(filename);
                    }

                    // 注册失败，复制到失败文件夹
                    if (!success) {
                        copyFileToFailedFolder(jpgFile);
                    }
                    else {
                        successCount++;
                    }
                }

                // 显示注册结果
                showRegisterResult(successCount, totalCount - successCount);

                // 这个应该是更新日志
                Log.i(FaceManageActivity.class.getSimpleName(), "run: " + executorService.isShutdown());
            }
        });
    }
    // todo 批量注册代码块 - end

    // todo 清空人脸库代码块 - start
    // 清空人脸库前端
    public void clearFaces(View view) {
        int faceNum = FaceServer.getInstance().getFaceNumber(this);
        if (faceNum == 0) {
            showToast(getString(R.string.batch_process_no_face_need_to_delete));
        }
        else {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.batch_process_notification)
                    .setMessage(getString(R.string.batch_process_confirm_delete, faceNum))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int deleteCount = doClearFaces();
                            showToast(getString(R.string.batch_face_clear_tip, deleteCount));
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .create();
            dialog.show();
        }
    }

    // 执行清空人脸库
    private int doClearFaces() {
        // 清空人脸信息识别库
        int deleteCount = FaceServer.getInstance().clearAllFaces(FaceManageActivity.this);

        // 清空人脸信息表的内容
        DatabaseHelper dbHelper = new DatabaseHelper(FaceManageActivity.this);
        dbHelper.clearFaceInfoTable();

        // 返回删除掉的人脸信息数量
        return deleteCount;
    }
    // todo 清空人脸库代码块 - end
}
