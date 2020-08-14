package com.practice.facerecognition;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.practice.facerecognition.util.DatabaseHelper;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

public class stupass extends AppCompatActivity {
    private Button btnpass;
    private EditText edtstuID1, edtstuID2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stupass);
        edtstuID1 = findViewById(R.id.edtstuID1);
        edtstuID2 = findViewById(R.id.edtstuID2);
    }

    public void btnpassClick(View view) {
        String studentID1 = edtstuID1.getText().toString();
        String studentID2 = edtstuID2.getText().toString();
        if(TextUtils.isEmpty(studentID1)|| TextUtils.isEmpty(studentID2)){
            new AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("密码不能为空")
                    .setPositiveButton("确定",null)
                    .show();
        }
        else if (studentID1.equals(studentID2)) {
//            将密码录入数据库
            DatabaseHelper helper= new DatabaseHelper(this);
            SQLiteDatabase db=helper.getWritableDatabase();
            Intent receiveData = getIntent();
            String username = receiveData.getStringExtra("username");
            String insertUserInfoSql = "Insert Into Users(" +
                    "sNum, " +
                    "password, " +
                    "isAdmin) " +
                    "Values(?, ?, ?)";
            db.execSQL(insertUserInfoSql, new String[]{username, studentID1,"0" });
            db.close();


//        跳转到信息录入

            AlertDialog.Builder a = new AlertDialog.Builder(this);
            a.setTitle("保存成功");
            a.setMessage("信息已经保存");
            a.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //界面跳转
                            Intent JumptostuPass = new Intent();
                            JumptostuPass.setClass(stupass.this, ManagerMainActivity.class);
                            startActivity(JumptostuPass);

                        }
                    });

            a.show();

        } else {
            new AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("两次输入密码不一致")
                    .setPositiveButton("确定", null)
                    .show();
        }
    }
}