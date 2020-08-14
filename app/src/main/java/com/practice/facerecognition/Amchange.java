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

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;

public class Amchange extends AppCompatActivity {
    private Button btn;
    private EditText edtstuid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amchange);
        btn=findViewById(R.id.btn);
        edtstuid=findViewById(R.id.edtstuid);

    }

    public void btnchange(View view) {
        String studentId = edtstuid.getText().toString();
        if(TextUtils.isEmpty(studentId)){
            new AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("学号不能为空")
                    .setPositiveButton("确定",null)
                    .show();
        }
        else{
            // todo 在数据库中查找
            final List<Map<String ,String>> data =new ArrayList<>();
            int flag=0;//判断学号存在标志
            DatabaseHelper helper= new DatabaseHelper(this);
            SQLiteDatabase db=helper.getWritableDatabase();

            //判断是否学号已存在
            String checkStuNum="select distinct studentNum" +
                    " from Students";

            Cursor c =db.rawQuery(checkStuNum,null);
            while (c.moveToNext())
            {
                String Num =c.getString(0);
                Map<String,String> row= new HashMap<>();
                row.put("stuNum",Num);
                data.add(row);
            }
            c.close(); //关闭游标
            db.close();


            for(int i = 0; i < data.size(); i++)
            {

                Map<String, String> map = data.get(i);
                Iterator it = map.keySet().iterator();
                while (it.hasNext()) {
                    String str = (String) it.next();
                    if (studentId.equals(map.get(str))) {
                        flag = 1;
                        break;
                    }//end if

                }//end while

                //学号存在
                if(flag == 1)
                    break;
            }

            //不存在
            if(flag == 0)
            {
                new AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("学号不存在,请检查输入内容")
                    .setPositiveButton("确定",null)
                    .show();
            }
            else
            {
                Intent jumpToStuInfo = new Intent();
                jumpToStuInfo.setClass(Amchange.this, changeResult.class);
                jumpToStuInfo.putExtra("username", studentId);
                startActivity(jumpToStuInfo);
            }
       }//end 大else
    }//end button
}