package com.example.vipul.mytodolist;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TODOListDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "TODOList";
    private static final int DB_VERSION = 4;


    public TODOListDatabaseHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE NEWTASK4(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "TASK_NAME TEXT, " + "START_DATE NUMERIC, " + "END_DATE NUMERIC, " + "START_TIME NUMERIC, " + "END_TIME NUMERIC, " + "REPEAT_TASK NUMERIC, " + "REPEAT_AFTER TEXT, " + "DESCRIPTION TEXT, " + "PRIORITY INTEGER, " + "IMAGE BLOB, " + "REMINDER NUMERIC, "+"MODIFIED_DATE NUMERIC);");
    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion<=3){
            db.execSQL("CREATE TABLE NEWTASK4(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "TASK_NAME TEXT, " + "START_DATE NUMERIC, " + "END_DATE NUMERIC, " + "START_TIME NUMERIC, " + "END_TIME NUMERIC, " + "REPEAT_TASK NUMERIC, " + "REPEAT_AFTER TEXT, " + "DESCRIPTION TEXT, " + "PRIORITY INTEGER, " + "IMAGE BLOB, " + "REMINDER NUMERIC, "+"MODIFIED_DATE NUMERIC);");
        }
    }
}
