package com.practice.facerecognition;



import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.practice.facerecognition.util.DatabaseHelper;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.app.ProgressDialog.show;
import static android.widget.Toast.*;

public class stuinfo extends AppCompatActivity {
//学生信息录入，判断有效性后出现提示框
    private Button btn;
    private EditText edtstuID,edtstuNa,edtstuclass,edtstulou,edtsturoom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stuinfo);
        btn=findViewById(R.id.btn);
        edtstuID=findViewById(R.id.edtstuID);
        edtstuNa=findViewById(R.id.edtstuNa);
        edtstuclass=findViewById(R.id.edtstuclass);
        edtstulou=findViewById(R.id.edtstulou);
        edtsturoom=findViewById(R.id.edtsturoom);

    }

    public void btnClick(View view) {
//        获取输入框内容
        final String studentNum = edtstuID.getText().toString();
        final List<Map<String ,String>> data =new ArrayList<>();
        String name = edtstuNa.getText().toString();
        String classNum = edtstuclass.getText().toString();
        String dormitoryNum = edtstulou.getText().toString();
        String roomNum = edtsturoom.getText().toString();
//        输入框判断是否为空
        if (TextUtils.isEmpty(studentNum)|| TextUtils.isEmpty(name)|| TextUtils.isEmpty(classNum)|| TextUtils.isEmpty(dormitoryNum)||TextUtils.isEmpty(roomNum)){
            new AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("录入信息不能为空")
                    .setPositiveButton("确定",null)
                    .show();

        }
        if (!TextUtils.isEmpty(studentNum)&&!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(classNum)&&!TextUtils.isEmpty(dormitoryNum)&&!TextUtils.isEmpty(roomNum)){
//            字符大小的判断
           if(studentNum.length()>9) //学号位数限制
            {
                new AlertDialog.Builder(this).setTitle("提示")
                        .setMessage("学号长度为九位数字，请检查输入内容")
                        .setPositiveButton("确定",null)
                        .show();
            }

           else if(name.length()>10) //姓名位数限制
            {
                new AlertDialog.Builder(this).setTitle("提示")
                        .setMessage("姓名最长为10位，请检查输入内容")
                        .setPositiveButton("确定",null)
                        .show();
            }

            else if(classNum.length()>7) //班号位数限制
            {
                new AlertDialog.Builder(this).setTitle("提示")
                        .setMessage("班号最长为7位，请检查输入内容")
                        .setPositiveButton("确定",null)
                        .show();
            }

            else {
                //正确了就存入数据库，并且跳转到下个页面

                //存数据库

               //数据存储
               int flag=1;//判断学号重复标志
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


               for(int i=0;i<data.size();i++)
               {

                   Map<String, String> map = data.get(i);
                   Iterator it = map.keySet().iterator();
                   while (it.hasNext()) {
                       String str = (String) it.next();
                       if (studentNum.equals(map.get(str))) {

                           new AlertDialog.Builder(this).setTitle("提示")
                                   .setMessage("学号已存在，请检查输入内容")
                                   .setPositiveButton("确定",null)
                                   .show();
//                           Toast.makeText(stuinfo.this, "学号存在", LENGTH_LONG).show();
                           flag=0;
                           break;
                       }//end if

                   }//end while

                   if(flag==0)//学号重复
                       break;

               }



       //检查学号不存在且内容符合
               if(flag!=0)
               {



               String insertStudentWithoutFaceInfoSql = "Insert Into Students(" +
                       "studentNum, " +
                       "name, " +
                       "classNum, " +
                       "dormitoryNum, " +
                       "roomNum) " +
                       "Values(?, ?, ?, ?, ?)";
               db.execSQL(insertStudentWithoutFaceInfoSql, new String[]{studentNum, name,classNum,dormitoryNum,roomNum });
               db.close();
               //提示框
               AlertDialog.Builder a = new AlertDialog.Builder(this);
               a.setTitle("保存成功");
               a.setMessage("信息已经保存");
               a.setPositiveButton("确定",
                       new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {



                               //界面跳转
                               Intent JunpTostupass = new Intent();
                               JunpTostupass.setClass(stuinfo.this, stupass.class);
                               //传学号作为用户名
                               JunpTostupass.putExtra("username", studentNum);
                               startActivity(JunpTostupass);

                           }
                       });

               a.show();






        }// end flag!=0




    }//end   else


        }
}}