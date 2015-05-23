package org.dhbw.geo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.content.ContentValues;
import android.util.Log;

/**
 * Created by Matthias on 08.05.2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "GeoDB";
    // definition of table names
    private static final String TABLE_RULE = "Rule";
    private static final String TABLE_TASK = "Task";
    private static final String TABLE_CONDITION = "Condition";
    // the database instance
    private SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d("DBHelper", "Constructor");
    }

    public void logDB() {
        db = this.getWritableDatabase();
        // get all entries
        String query = "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String tableName = cursor.getString(0);
                logTable(tableName);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public void logTable(String name) {
        String query = "SELECT * FROM " + name;
        Cursor cursor = db.rawQuery(query, null);
        // log table head
        String log = name + ":\n";
        String[] columns = cursor.getColumnNames();
        for(int i = 0; i < columns.length; i++) {
            if (i > 0) log += " | ";
            log += columns[i];
        }
        log += "\n";
        // log table body
        if (cursor.moveToFirst()) {
            do {
                for(int i = 0; i < cursor.getColumnCount(); i++){
                    if(i > 0) log += " | ";
                    log += cursor.getString(i);
                }
                log += "\n";
            } while (cursor.moveToNext());
        }
        log += "\n";
        Log.d("DBHelper", log);
        cursor.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createRuleTable = "CREATE TABLE " + TABLE_RULE + " ( " +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Name VARCHAR(30) NOT NULL, " +
                "Priority INT NOT NULL)";
        String createConditionTable = "CREATE TABLE " + TABLE_CONDITION + " (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "RuleID NOT NULL REFERENCES Rule(ID) ON UPDATE CASCADE ON DELETE CASCADE, " +
                "Property INT NOT NULL, " +
                "Comparison VARCHAR(30) NOT NULL," +
                "Value TEXT NOT NULL)";
        String createTaskTable = "CREATE TABLE " + TABLE_TASK + " (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "RuleID NOT NULL REFERENCES Rule(ID) ON UPDATE CASCADE ON DELETE CASCADE)";
        db.execSQL(createRuleTable);
        db.execSQL(createConditionTable);
        db.execSQL(createTaskTable);
        Log.d("DBHelper", "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop old tables; take care of correct order!
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONDITION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RULE);

        // create database
        this.onCreate(db);
    }
}
