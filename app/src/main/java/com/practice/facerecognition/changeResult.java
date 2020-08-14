package com.practice.facerecognition;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
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


public class changeResult extends AppCompatActivity {

    private ListView infoListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_result);

        // 绑定视图
        infoListView = findViewById(R.id.changeListView);

        // todo 学生学号获取
        final String[] reason = new String[]{"已签到", "未签到"};
        Intent receiveData = getIntent();
        final String username = receiveData.getStringExtra("username");

        // todo 获取全部签到数据
        // 数据格式：{"日期": "看数据库", "姓名": xxx,"签到状态"：已签到，未签到}
        final List<Map<String, String>> infoList = new ArrayList<>();
        // 查询数据库
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String readAllHistorySql = "Select R.time, S.name, R.result,R.studentNum,R.id " +
                "From Students S, SignResults R " +
                "Where R.studentNum =S.studentNum " +
//                "AND R.result='1' "+
                "order by S.studentNum"
                ;

        //声明游标
        Cursor readHistoryInfoCursor = db.rawQuery(readAllHistorySql, null);
        while(readHistoryInfoCursor.moveToNext()) {

            String time = readHistoryInfoCursor.getString(0);
            String stuName = readHistoryInfoCursor.getString(1);
            String status = readHistoryInfoCursor.getString(2);
            String studentNum = readHistoryInfoCursor.getString(3);
            String id = readHistoryInfoCursor.getString(4);

            Map<String, String> row = new HashMap<>();

            if(username.equals(studentNum))
            {

                row.put("time", time);
                row.put("name", stuName);


                if(status.equals("0"))
                    row.put("result", reason[1]);
                else
                    row.put("result", reason[0]);

                row.put("id", id);

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
                R.layout.layout_change_result_item,
                new String[]{
                        "time",
                        "name",
                        "result"
                },
                new int[]{
                        R.id.Time1,
                        R.id.name1,
                        R.id.status

                });

        // 设置适配器
        infoListView.setAdapter(adapter);

        //todo 点击事件

        // Item点击事件
        infoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {


                final String[] reason1 = new String[]{"1", "0"};
                new AlertDialog.Builder(changeResult.this)
                        .setItems(reason, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int j) {
                                DatabaseHelper helper = new DatabaseHelper(changeResult.this);
                                SQLiteDatabase db = helper.getWritableDatabase();

                                String sql_kaoqin3 = "Update SignResults Set result = ? Where id = ?";
                                db.execSQL(sql_kaoqin3, new String[]{reason1[j], infoList.get(i).get("id")});

                                db.close();

                                // 使用reason数组中的值替换掉ListView中数据

                                infoList.get(i).put("result",reason[j] );
                                // 刷新ListView
                                adapter.notifyDataSetChanged();
                            }
                        }).show();
            }
        });

        //todo 点击事件
    }//end oncreat

    public void returnHome(View view) {
        Intent jumpToLookResultActivity = new Intent();
        jumpToLookResultActivity.setClass(changeResult.this,ManagerMainActivity.class);
        startActivity(jumpToLookResultActivity);
    }

  
}//end class
