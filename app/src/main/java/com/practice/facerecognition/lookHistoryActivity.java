package com.practice.facerecognition;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListView;
import android.widget.SimpleAdapter;

import android.widget.Toast;

import com.practice.facerecognition.util.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class lookHistoryActivity extends AppCompatActivity {

    // todo 查看签到记录：签到日期，姓名，签到状态
    private ListView HistoryListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_history);

        HistoryListView=findViewById(R.id.HistoryListView);

        // todo 学生学号获取
        final String[] reason = new String[]{"已签到", "未签到"};
        Intent receiveData = getIntent();
        String u = receiveData.getStringExtra("username");

        // todo 获取全部签到数据
        // 数据格式：{"日期": "看数据库", "姓名": xxx,"签到状态"：已签到，未签到}
        final List<Map<String, String>> infoList = new ArrayList<>();
        // 查询数据库
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //
        String readAllHistorySql = "Select R.time, S.name, R.result,R.studentNum " +
                "From Students S, SignResults R " +
                "Where R.studentNum = S.studentNum " +
//                "AND R.result='1' "+
                "order by S.studentNum"
                ;
//      声明游标

        Cursor readHistoryInfoCursor = db.rawQuery(readAllHistorySql, null);
        while(readHistoryInfoCursor.moveToNext()) {

            String time = readHistoryInfoCursor.getString(0);
            String stuName = readHistoryInfoCursor.getString(1);
            String status = readHistoryInfoCursor.getString(2);
            String studentNum = readHistoryInfoCursor.getString(3);

            Map<String, String> row = new HashMap<>();

            if(u.equals(studentNum))
            {

                row.put("time", time);
                row.put("name", stuName);
                row.put("result", status.equals("0") ? reason[1] : reason[0]);

                infoList.add(row);
            }
        }

        // 关闭游标和数据库
        readHistoryInfoCursor.close();
        db.close();

        // 将数据加入适配器   视图 --- 适配器 --- 数据
        // 参数说明：上下文、数据源、布局文件、键值对中的key、放到哪
        final SimpleAdapter adapter = new SimpleAdapter(
                this,
                infoList,
                R.layout.layout_history_item,
                new String[]{
                        "time",
                        "name",
                        "result"
                },
                new int[]{
                        R.id.tvSignTime,
                        R.id.stu_name,
                        R.id.stu_status
                });

        // 设置适配器
        HistoryListView.setAdapter(adapter);
    }
}