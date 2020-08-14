package com.practice.facerecognition;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.practice.facerecognition.util.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class CheckingInActivity extends AppCompatActivity {
    private Spinner spinner;
    private CheckBox alreadyCheckingInCheckBox;
    private CheckBox notCheckingInCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checking_in);

        // 绑定视图
        spinner = findViewById(R.id.spinner);
        alreadyCheckingInCheckBox = findViewById(R.id.alreadyCheckingInCheckBox);
        notCheckingInCheckBox = findViewById(R.id.notCheckingInCheckBox);

        // 查询数据库
        final List<String> infoList = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String searchSql = "Select Distinct dormitoryNum, roomNum From Students";
        Cursor searchCursor = db.rawQuery(searchSql, null);
        while(searchCursor.moveToNext()) {
            String dormitoryNum = searchCursor.getString(0);
            String roomNum = searchCursor.getString(1);
            infoList.add(dormitoryNum + "-" + roomNum);
        }

        searchCursor.close();
        db.close();

        // 将数据放入适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                infoList
        );

        // 设置适配器
        spinner.setAdapter(adapter);
    }

    public void searchButtonClick(View view) {
        // 考察复选框的状态
        if (!alreadyCheckingInCheckBox.isChecked() &&
            !notCheckingInCheckBox.isChecked()) {
            Toast.makeText(this, "请选择签到状态", Toast.LENGTH_LONG).show();
        }
        else {
            // 跳转到查找结果界面
            Intent jumpToSearchResultActivity = new Intent();
            jumpToSearchResultActivity.setClass(
                    CheckingInActivity.this,
                    CheckingInSearchResultActivity.class);

            // 全部选中
            if (alreadyCheckingInCheckBox.isChecked() &&
                notCheckingInCheckBox.isChecked()) {
                jumpToSearchResultActivity.putExtra("stu_status", "2");
            }
            // 选中了已签到
            else if (alreadyCheckingInCheckBox.isChecked()) {
                jumpToSearchResultActivity.putExtra("stu_status", "0");
            }
            // 选中了未签到
            else if (notCheckingInCheckBox.isChecked()){
                jumpToSearchResultActivity.putExtra("stu_status", "1");
            }

            // 把房间号传递给查找结果页面
            jumpToSearchResultActivity.putExtra("stu_room", spinner.getSelectedItem().toString());

            startActivity(jumpToSearchResultActivity);
        }
    }
}