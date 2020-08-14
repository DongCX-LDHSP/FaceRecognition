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


public class SetPasswordActivity extends AppCompatActivity {
    // 忘记密码按钮
    private Button setButton;
    private EditText usernameEditText;
    private EditText passwordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);

        // 绑定按钮视图
        setButton = findViewById(R.id.setButton);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
    }

    public void SetClick(View view) {
        // 获取用户名和密码输入
        String username = usernameEditText.getText().toString();
        String word = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(word)) {
            Toast.makeText(SetPasswordActivity.this, "请输入用户名或密码！", Toast.LENGTH_LONG).show();
        }
        else if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(word)) {
            // 初始化是否修改成功的标志
            boolean updateSuccess = false;

            // 读取信息
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String readUserInfoSql = "Select sNum From Users";
            Cursor cursor = db.rawQuery(readUserInfoSql, null);
            while (cursor.moveToNext()) {
                String sNum = cursor.getString(0);
                if(username.equals(sNum)){
                    updateSuccess = true;

                    // 更新数据
                    String updateData = "Update Users Set password = ? Where sNum = ?";
                    db.execSQL(updateData, new String[]{word, username});

                    // 修改成功提示
                    new AlertDialog.Builder(this).setTitle("提示")
                            .setMessage("修改成功！")
                            .setPositiveButton("确定", null)
                            .show();
                    break;
                }
            }

            // 关闭游标和数据库
            cursor.close();
            db.close();

            // 用户名不存在警告框
            if(!updateSuccess) {
                new AlertDialog.Builder(this).setTitle("提示")
                        .setMessage("无该用户名！")
                        .setPositiveButton("确定", null)
                        .setNegativeButton("取消", null)
                        .show();
            }
        }
    }
}
