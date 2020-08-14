package com.practice.facerecognition;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.practice.facerecognition.util.DatabaseHelper;

import io.reactivex.annotations.NonNull;

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

        // 检查引擎状态
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        if (dbHelper.getApiInfo()[2].equals("0")) {
            new AlertDialog.Builder(this)
                    .setTitle("引擎初始化")
                    .setMessage("引擎未激活，请进入菜单激活引擎！")
                    .setPositiveButton(getString(R.string.ok), null)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 绑定资源文件中创建的Menu
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activateEngine:
                jumpToActiveEngineActivity();
                break;
            default:
                break;
        }
        return true;
    }

    // 响应管理员激活引擎菜单项 -> 激活引擎页面
    private void jumpToActiveEngineActivity() {
        DatabaseHelper dbHelper = new DatabaseHelper(ManagerMainActivity.this);
        String isActivated = dbHelper.getApiInfo()[2];

        // 已激活则不转入
        if (isActivated.equals("1")) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("引擎已激活，无需再次激活！")
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
        // 未激活则转入
        else {
            Intent jumpToActiveEngineActivity = new Intent();
            jumpToActiveEngineActivity.setClass(
                    ManagerMainActivity.this,
                    ActiveEngineActivity.class);
            startActivity(jumpToActiveEngineActivity);
        }
    }

    // 响应批量注册按钮 -> 批量注册界面
    public void faceManageButtonClick(View view) {
        DatabaseHelper dbHelper = new DatabaseHelper(ManagerMainActivity.this);
        String isActivated = dbHelper.getApiInfo()[2];

        // 未激活则不转入
        if (isActivated.equals("0")) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("引擎尚未激活，请激活引擎后再操作！")
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
        else {
            Intent jumpToFaceManageActivity = new Intent();
            jumpToFaceManageActivity.setClass(
                    ManagerMainActivity.this,
                    FaceManageActivity.class);
            startActivity(jumpToFaceManageActivity);
        }
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
