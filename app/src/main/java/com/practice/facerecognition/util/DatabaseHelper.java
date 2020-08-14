package com.practice.facerecognition.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import io.reactivex.annotations.Nullable;


public class DatabaseHelper extends SQLiteOpenHelper {
    /**
     * - ApiInfo(appId:text, sdkKey:text)
     * - Users(sNum:text, password:text, isAdmin:text(值为0或1，1表示管理员))
     * - Students(studentNum:text, name:text, classNum:text, dormitoryNum:text, roomNum:text)
     * - FaceInfo(id:Integer, studentNum:text)
     * - SignResults(id:Integer, studentNum:text, time:text("xxxx-xx-xx xx:xx:xx"), result:text(0或1，默认值0表示缺勤))
     */

    // 创建人脸识别API信息表
    private String createApiInfoTableSql =
            "Create Table ApiInfo(" +
                    "appId text Not Null, " +
                    "sdkKey text Not Null)";

    // 创建用户信息表
    private String createUserInfoTableSql =
            "Create Table Users(" +
                    "sNum text Primary Key, " +
                    "password text Not Null, " +
                    "isAdmin text Not Null)";

    // 创建学生信息表
    private String createStudentInfoSql =
            "Create Table Students(" +
                    "studentNum text Primary Key, " +
                    "name text Not Null," +
                    "classNum text Not Null," +
                    "dormitoryNum text Not Null," +
                    "roomNum text Not Null)";

    // 创建签到结果表
    private String createSignResultTableSql =
            "Create Table SignResults(" +
                    "id Integer Primary Key AutoIncrement," +
                    "studentNum text Not Null, " +
                    "time text Not Null, " +
                    "result text Default \"0\")";

    // 创建人脸信息表
    private String createFaceInfoSql =
            "Create Table FaceInfo(" +
                    "id Integer Primary Key AutoIncrement, " +
                    "studentNum text Not Null, " +
                    "Foreign Key(studentNum) References Students(studentNum))";

    // 插入API信息
    private String insertApiInfoSql =
            "Insert Into ApiInfo(" +
                    "appId, " +
                    "sdkKey) " +
                    "Values(?, ?)";

    // 清空API信息表
    private String clearApiInfoSql = "Delete From ApiInfo";

    // 插入用户信息
    public String insertUserInfoSql =
            "Insert Into Users(" +
                    "sNum, " +
                    "password, " +
                    "isAdmin) " +
                    "Values(?, ?, ?)";

    // 插入学生信息
    public String insertStudentInfoSql =
            "Insert Into Students(" +
                    "studentNum, " +
                    "name, " +
                    "classNum, " +
                    "dormitoryNum, " +
                    "roomNum) " +
                    "Values(?, ?, ?, ?, ?)";

    // 插入签到结果信息
    public String insertSignResultSql =
            "Insert Into SignResults(" +
                    "studentNum, " +
                    "time, " +
                    "result) " +
                    "Values(?, ?, ?)";

    // 更新签到结果信息
    private String resetSignResultSql =
            "Update SignResults " +
                    "Set result = ?," +
                    "time = ? " +
                    "Where studentNum = ? And time like ?";

    // 检测今天是否已签到成功
    private String selectTodaySignInSql =
            "Select result " +
                    "From SignResults " +
                    "Where studentNum = ? " +
                    "And time Like ?";

    // 插入人脸信息
    public String insertFaceInfoSql =
            "Insert Into FaceInfo(" +
                    "studentNum) " +
                    "Values(?)";

    // 查询用户信息
    public String selectUserInfoSql = "Select sNum, password, isAdmin From Users";

    // 查询学生信息
    public String selectStudentInfoSql =
            "Select studentNum, " +
                    "name, " +
                    "classNum, " +
                    "dormitoryNum, " +
                    "roomNum " +
                    "From Students";

    // 基于学号查找学生姓名
    public String selectStudentNameByStudentNumSql =
            "Select name " +
                    "From Students " +
                    "Where studentNum = ?";

    // 查找人脸信息是否已存在
    public String faceInfoExistsSql =
            "Select studentNum " +
                    "From FaceInfo " +
                    "Where studentNum = ?";

    // 清空人脸信息表的内容
    private String clearFaceInfoSql = "Delete From FaceInfo";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "FaceRecognition.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // 创建人脸识别API信息表
        sqLiteDatabase.execSQL(createApiInfoTableSql);

        // 创建用户信息表
        sqLiteDatabase.execSQL(createUserInfoTableSql);

        // 创建学生信息表
        sqLiteDatabase.execSQL(createStudentInfoSql);

        // 创建人脸信息表
        sqLiteDatabase.execSQL(createFaceInfoSql);

        // 创建签到结果表
        sqLiteDatabase.execSQL(createSignResultTableSql);

        // 创建管理员账户
        createAdmin(sqLiteDatabase);

        // todo 插入一些测试数据，测试语句，注意删除
        createTestInfo(sqLiteDatabase);

        Log.d("DatabaseHelper","onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void updateApiInfo(String appId, String sdkKey) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(clearApiInfoSql);
        db.execSQL(insertApiInfoSql, new String[]{appId, sdkKey});
        db.close();
    }

    // 在内部创建管理员账户
    private void createAdmin(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(this.insertUserInfoSql, new String[]{"100000001", "abc", "1"});
        sqLiteDatabase.execSQL(this.insertUserInfoSql, new String[]{"100000002", "abc", "1"});
        sqLiteDatabase.execSQL(this.insertUserInfoSql, new String[]{"100000003", "abc", "1"});
    }

    // todo 插入测试数据 - start
    // 一次调用三个创建测试信息的方法
    private void createTestInfo(SQLiteDatabase sqLiteDatabase) {
        createTestStudentUser(sqLiteDatabase);
        createTestStudent(sqLiteDatabase);
        createTestSignResult(sqLiteDatabase);
    }

    // 创建测试学生用户
    private void createTestStudentUser(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(this.insertUserInfoSql, new String[]{"201810325", "abc", "0"});
        sqLiteDatabase.execSQL(this.insertUserInfoSql, new String[]{"201810324", "abc", "0"});
        sqLiteDatabase.execSQL(this.insertUserInfoSql, new String[]{"201810329", "abc", "0"});
    }

    // 创建测试学生信息
    private void createTestStudent(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(this.insertStudentInfoSql, new String[]{"201810325", "张三", "2018103", "5", "414"});
        sqLiteDatabase.execSQL(this.insertStudentInfoSql, new String[]{"201810326", "王五", "2018103", "5", "415"});
        sqLiteDatabase.execSQL(this.insertStudentInfoSql, new String[]{"201810229", "李四", "2018102", "5", "414"});
        sqLiteDatabase.execSQL(this.insertStudentInfoSql, new String[]{"201810324", "张三丰", "2018103", "5", "414"});
        sqLiteDatabase.execSQL(this.insertStudentInfoSql, new String[]{"201810327", "张无忌", "2018103", "5", "415"});
        sqLiteDatabase.execSQL(this.insertStudentInfoSql, new String[]{"201810329", "周杰伦", "2018103", "5", "415"});
    }

    // 创建测试学生签到信息
    private void createTestSignResult(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(this.insertSignResultSql, new String[]{"201810329", "2020-10-13 07:56:38", "0"});
        sqLiteDatabase.execSQL(this.insertSignResultSql, new String[]{"201810325", "2020-10-13 08:02:33", "0"});
        sqLiteDatabase.execSQL(this.insertSignResultSql, new String[]{"201810329", "2020-10-14 07:09:50", "1"});
        sqLiteDatabase.execSQL(this.insertSignResultSql, new String[]{"201810229", "2020-10-13 06:55:20", "1"});
        sqLiteDatabase.execSQL(this.insertSignResultSql, new String[]{"201810325", "2020-10-23 07:34:28", "1"});
        sqLiteDatabase.execSQL(this.insertSignResultSql, new String[]{"201810329", "2020-10-03 07:34:54", "1"});
        sqLiteDatabase.execSQL(this.insertSignResultSql, new String[]{"201810329", "2020-10-04 08:10:35", "1"});
        sqLiteDatabase.execSQL(this.insertSignResultSql, new String[]{"201810329", "2020-10-05 08:05:40", "1"});
        sqLiteDatabase.execSQL(this.insertSignResultSql, new String[]{"201810324", "2020-10-03 07:20:23", "1"});
        sqLiteDatabase.execSQL(this.insertSignResultSql, new String[]{"201810324", "2020-10-13 08:00:23", "0"});
        sqLiteDatabase.execSQL(this.insertSignResultSql, new String[]{"201810329", "2020-10-13 06:40:56", "0"});
    }
    // todo 插入测试数据 - end

    // 插入学生用户信息
    public void insertStudentUserInfo(String sNum, String password) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(this.insertUserInfoSql, new String[]{sNum, password, "0"});
        db.close();
    }

    // 插入学生信息
    public void insertStudentInfo(
            String studentNum,
            String name,
            String classNum,
            String dormitoryNum,
            String roomNum) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(this.insertStudentInfoSql, new String[]{
                studentNum,
                name,
                classNum,
                dormitoryNum,
                roomNum
        });
        db.close();
    }

    // 插入人脸信息
    public void insertFaceInfo(String studentNum) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(this.insertFaceInfoSql, new String[]{studentNum});
        db.close();
    }

    // 获取某一学生在date是否签到
    public boolean isAlreadySignIn(String studentNum, String date) {
        boolean alreadySignIn = false;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                this.selectTodaySignInSql,
                new String[]{
                        studentNum,
                        date + "%"
                });
        while (cursor.moveToNext()) {
            String result = cursor.getString(0);
            if (result.equals("1")) {
                alreadySignIn = true;
            }
        }

        // 关闭游标和数据库
        cursor.close();
        db.close();

        return alreadySignIn;
    }

    // 更新签到结果
    public void updateSignResultInfo(
            String studentNum,
            String time,
            String result) {

        // 查找是否已将今日的签到结果写入表中
        if (isAlreadyWriteSignResult(studentNum, time)) {
            resetSignResultOf(studentNum, time, result);
        }
        else {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL(this.insertSignResultSql, new String[]{
                    studentNum,
                    time,
                    result
            });
            db.close();
        }
    }

    // 是否已进行签到，不考虑是否签到成功
    public boolean isAlreadyWriteSignResult(
            String studentNum,
            String time) {
        boolean alreadyWrite = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                this.selectTodaySignInSql,
                new String[]{studentNum, time.substring(0, 10) + "%"});
        while (cursor.moveToNext()) {
            alreadyWrite = true;
        }

        // 关闭游标和数据库
        cursor.close();
        db.close();

        return alreadyWrite;
    }

    // 重置签到结果
    private void resetSignResultOf(String studentNum, String time, String result) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(this.resetSignResultSql,
                new String[]{
                        result,
                        time,
                        studentNum,
                        time.substring(0, 10) + "%"
                });
        db.close();
    }

    // 测试方法，输出当前表中的用户信息
    public void outputUserInfo() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(this.selectUserInfoSql, null);
        while(cursor.moveToNext()) {
            String sNum = cursor.getString(0);
            String password = cursor.getString(1);
            String isAdmin = cursor.getString(2);
            System.out.println(sNum + ", " + password + ", " + isAdmin);
        }
        cursor.close();
        db.close();
    }

    // 测试方法，输出学生信息
    public void outputStudentInfo() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(this.selectStudentInfoSql, null);
        while(cursor.moveToNext()) {
            String studentNum = cursor.getString(0);
            String name = cursor.getString(1);
            String classNum = cursor.getString(2);
            String dormitoryNum = cursor.getString(3);
            String roomNum = cursor.getString(4);
            System.out.println(
                    studentNum + ", " +
                            name + ", " +
                            classNum + ", " +
                            dormitoryNum + ", " +
                            roomNum);
        }
        cursor.close();
        db.close();
    }

    // 基于学号获取学生姓名
    public String getStudentNameByStudentNum(String studentNum) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(this.selectStudentNameByStudentNumSql, new String[]{studentNum});
        String name = null;
        while (cursor.moveToNext()) {
            name = cursor.getString(0);
        }
        cursor.close();
        db.close();

        return name;
    }

    // 查询某一学号的人脸信息是否存在
    public boolean faceInfoExists(String studentNum) {
        boolean faceExists = false;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(this.faceInfoExistsSql, new String[]{studentNum});
        while (cursor.moveToNext()) {
            // 只要进入循环说明就查找到了结果，也就是已存在该人脸信息
            faceExists = true;
        }

        db.close();
        cursor.close();

        return faceExists;
    }

    // 清空人脸信息表
    public void clearFaceInfoTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(this.clearFaceInfoSql);
        db.close();
    }
}
