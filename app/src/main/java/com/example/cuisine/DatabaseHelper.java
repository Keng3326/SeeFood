package com.example.cuisine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Cuisine.db";
    public static final String TABLE_NAME = "recognitoin_table";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public void insertData(String foodId, String recDate, byte[] recImg){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO " + TABLE_NAME + " VALUES (NULL, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1,foodId);
        statement.bindString(2,recDate);
        statement.bindBlob(3,recImg);

        statement.executeInsert();
    }

    public Cursor getData(){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME ;
        return database.rawQuery(sql,null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (REC_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "FOOD_ID VARCHAR, REC_DATE VARCHAR,REC_IMG BLOG)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        } else {
            return;
        }
        onCreate(db);
    }

}
