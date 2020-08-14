package com.practice.facerecognition;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.practice.facerecognition.util.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.annotations.NonNull;


public class MainActivity extends AppCompatActivity {
    private Button faceRecognize;
    private Button lookHistory;
    private Button loginOut;

    // 当前登录学生的学号
    private String loginStudentNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 调用资源文件夹的信息使用 `R.…….……`
        setContentView(R.layout.activity_main);

        // 绑定按钮视图
        faceRecognize = findViewById(R.id.faceRecognize);
        lookHistory = findViewById(R.id.lookHistory);
        loginOut = findViewById(R.id.loginOut);

        // 接收上一页面传来的登录学号
        Intent receiveIntent = getIntent();
        loginStudentNum = receiveIntent.getStringExtra("username");
    }

    // Ctrl + O 呼出选择重写某一方法的对话框
    // 直接输入方法名就可以进行搜索

    // 跳转到查看签到记录界面
    public void lookHistoryButtonClick(View view) {
        Toast.makeText(MainActivity.this, "查看签到记录", Toast.LENGTH_LONG).show();
        Intent jumpToLookHistoryActivity = new Intent();
        jumpToLookHistoryActivity.setClass(
                MainActivity.this,
                lookHistoryActivity.class);
        jumpToLookHistoryActivity.putExtra("username", loginStudentNum);
        startActivity(jumpToLookHistoryActivity);
    }

    // todo 人脸签到代码块 - 1 start
    // 人脸签到界面
    public void faceRecognizeButtonClick(View view) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // 获取今天的日期
        @SuppressLint("SimpleDateFormat") String date =
                new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // 若尚未签到则跳转到人脸签到界面
        if (!dbHelper.isAlreadySignIn(loginStudentNum, date)) {
            Intent jumpToRecognizeAndRegisterActivity = new Intent();
            jumpToRecognizeAndRegisterActivity.setClass(
                    MainActivity.this,
                    RegisterAndRecognizeActivity.class);
            jumpToRecognizeAndRegisterActivity.putExtra("studentNum", loginStudentNum);
            startActivity(jumpToRecognizeAndRegisterActivity);
        }
        // 否则弹出提示对话框
        else {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("您已经签到了！")
                    .setPositiveButton("确定", null)
                    .show();
        }
    }
    // todo 人脸签到代码块 - 1 end

    public void loginOutButtonClick(View view) {
        Intent jumpToLookResultActivity = new Intent();
        jumpToLookResultActivity.setClass(MainActivity.this, LoginActivity.class);
        startActivity(jumpToLookResultActivity);
    }
}
