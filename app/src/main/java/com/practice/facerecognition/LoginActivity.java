package com.practice.facerecognition;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.practice.facerecognition.util.DatabaseHelper;


public class LoginActivity extends AppCompatActivity {
    // 登录按钮
    private Button loginButton;
    private Button forgetPasswordButton;
    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 绑定按钮视图
        forgetPasswordButton = findViewById(R.id.forgetPasswordButton);
        loginButton = findViewById(R.id.loginButton);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
    }

    public void loginButtonClick(View view) {
        // 获取用户名和密码输入
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "请输入用户名或密码！", Toast.LENGTH_LONG).show();
        }
        else if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            int flag = 0;
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String readUserInfoSql = "Select sNum, password, isAdmin From Users";

            // 读取信息
            Cursor cursor = db.rawQuery(readUserInfoSql, null);
            while(cursor.moveToNext()) {
                String sNum = cursor.getString(0);
                String pass = cursor.getString(1);
                String isAdmin = cursor.getString(2);

                // 用户合法
                if(username.equals(sNum) && password.equals(pass)){
                    // 学生用户
                    if (isAdmin.equals("0")) {
                        flag = 1;
                    }
                    // 管理员用户
                    else {
                        flag = 2;
                    }
                    break;
                }
            }

            // 关闭游标和数据库
            cursor.close();
            db.close();

            // 用户名或密码错误
            if (flag == 0) {
                // 警告对话框
                new AlertDialog.Builder(this).setTitle("提示")
                        .setMessage("用户名或密码错误！")
                        .setPositiveButton("确定", null)
                        .setNegativeButton("取消", null)
                        .show();
            }
            // 跳转到主界面
            else {
                Intent jumpToMainWindowActivity = new Intent();

                // 跳转到学生界面
                if (flag == 1) {
                    jumpToMainWindowActivity.setClass(LoginActivity.this, MainActivity.class);
                    jumpToMainWindowActivity.putExtra("username", username);
                }
                // 跳转到管理员界面
                else {
                    jumpToMainWindowActivity.setClass(LoginActivity.this, ManagerMainActivity.class);
                }
                startActivity(jumpToMainWindowActivity);
            }
        }
    }

    public void forgetPasswordClick(View view) {
        Intent intent = new Intent(LoginActivity.this, SetPasswordActivity.class);
        startActivity(intent);
    }
}
