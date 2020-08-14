package com.practice.facerecognition;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.practice.facerecognition.util.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckingInSearchResultActivity extends AppCompatActivity {
    private ListView searchResultListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checking_in_search_result);

        // 绑定视图
        searchResultListView = findViewById(R.id.lvKaoQin);

        final String[] signResult = new String[]{"已签到", "未签到"};

        // 接收上一页面传入数据
        Intent receiveData = getIntent();
        String stuStatus = receiveData.getStringExtra("stu_status");
        String stuRoomFull = receiveData.getStringExtra("stu_room");

        // 将公寓信息拆分为公寓号和寝室号
        String dormitoryNum = "";  // 公寓号
        String roomNum = "";   // 寝室号
        if (!TextUtils.isEmpty(stuRoomFull)) {
            String[] tmp = stuRoomFull.split("-");
            dormitoryNum = tmp[0];
            roomNum = tmp[1];
        }

        // 查询数据库
        List<Map<String, String>> infoList = new ArrayList<>();
        DatabaseHelper helper = new DatabaseHelper(this);
        String searchSql = "Select s.studentNum, s.name, k.result " +
                "From Students s, SignResults k " +
                "Where s.studentNum = k.studentNum " +
                "And s.dormitoryNum = ? " +
                "And s.roomNum = ? ";
        // 已签到
        if ("0".equals(stuStatus)) {
            searchSql += " AND k.result = '1'";
        }
        // 未签到
        else if ("1".equals(stuStatus)) {
            searchSql += " AND k.result = '0'";
        }

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(searchSql, new String[]{dormitoryNum, roomNum});
        while (c.moveToNext()) {
            // 把查询的结果按ListView中数据的要求进行组装
            Map<String, String> row = new HashMap<>();
            row.put("stu_no", c.getString(0));
            row.put("stu_name", c.getString(1));
            row.put("stu_signin", c.getString(2).equals("0") ? signResult[1] : signResult[0]);
            infoList.add(row);
        }

        c.close();
        db.close();

        if (infoList.isEmpty()){
            Toast.makeText(this,"学生信息不存在",Toast.LENGTH_LONG).show();
        }

        // 设置适配器
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                infoList,
                R.layout.layout_item,
                new String[]{
                        "stu_no",
                        "stu_name",
                        "stu_signin"
                },
                new int[]{
                        R.id.tvStuNo,
                        R.id.tvStuName,
                        R.id.tvStuSignIn
                });
        searchResultListView.setAdapter(adapter);
    }
}