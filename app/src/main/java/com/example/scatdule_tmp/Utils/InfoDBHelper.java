package com.example.scatdule_tmp.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InfoDBHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "info.db";

    public InfoDBHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Info(exp INT, level INT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Info");
        onCreate(db);
    }

    public void insert(int exp, int level) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO Info VALUES('" + exp + "', '" + level + "')");
        db.close();
    }

    public void Update(int exp, int level) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE Info SET exp = " + exp + ", level = " + level);
        db.close();
    }

    public String getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM Info", null);
        while (cursor.moveToNext()) {
            result += "exp : "
                    + cursor.getInt(0)
                    + ", level : "
                    + cursor.getInt(1)
                    + "\n";
        }

        return result;
    }

}
