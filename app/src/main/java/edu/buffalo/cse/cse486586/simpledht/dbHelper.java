package edu.buffalo.cse.cse486586.simpledht;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
//import android.support.annotation.Nullable;

import org.jetbrains.annotations.Nullable;


public class dbHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "database";
    public static final String TABLE_NAME = "kv_store";
    public static final String COL_1="id";
    public static final String COL_2 = "key";
    public static final String COL_3 = "value";

    public dbHelper(@Nullable Context context){
        super (context, DATABASE_NAME,null,1);

    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE "+TABLE_NAME+" (id INTEGER PRIMARY KEY AUTOINCREMENT,key TEXT,value TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
    }

}