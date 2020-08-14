package com.practice.facerecognition;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.enums.RuntimeABI;
import com.practice.facerecognition.common.Constants;

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

public class ManagerMainActivity extends AppCompatActivity {
    private Button face_manage;
    private Button student_info;
    private Button get_info;
    private Button edit_status;
    private Button loginOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 调用资源文件夹的信息使用 `R.…….……`
        setContentView(R.layout.manager);

        // 绑定按钮视图
        face_manage = findViewById(R.id.face_manage);
        student_info = findViewById(R.id.student_info);
        get_info = findViewById(R.id.get_info);
        edit_status = findViewById(R.id.edit_status);
        edit_status = findViewById(R.id.loginOut);

    }

    // 响应批量注册按钮 -> 批量注册界面
    public void faceManageButtonClick(View view) {
        Intent jumpToFaceManageActivity = new Intent();
        jumpToFaceManageActivity.setClass(
                ManagerMainActivity.this,
                FaceManageActivity.class);
        startActivity(jumpToFaceManageActivity);
    }

    // 学生信息添加按钮事件
    public void studentInfoButtonClick(View view) {
        //跳转添加学生信息页面
        Intent jumpToLookResultActivity = new Intent();
        jumpToLookResultActivity.setClass(ManagerMainActivity.this, stuinfo.class);
        startActivity(jumpToLookResultActivity);
    }

    // 查询学生信息按钮事件
    public void getInfoButtonClick(View view) {
        //跳转查询学生信息页面
        Intent jumpToLookResultActivity = new Intent();
        jumpToLookResultActivity.setClass(ManagerMainActivity.this, CheckingInActivity.class);
        startActivity(jumpToLookResultActivity);
    }

    // 修改学生状态按钮事件
    public void editStatusButtonClick(View view) {
        //跳转修改学生状态页面
        Intent jumpToLookResultActivity = new Intent();
        jumpToLookResultActivity.setClass(ManagerMainActivity.this, Amchange.class);
        startActivity(jumpToLookResultActivity);
    }

    // 退出登录按钮事件
    public void loginOutButtonClick(View view) {
        Intent jumpToLookResultActivity = new Intent();
        jumpToLookResultActivity.setClass(ManagerMainActivity.this, LoginActivity.class);
        startActivity(jumpToLookResultActivity);
    }
}
