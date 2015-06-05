package org.dhbw.geo.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.content.ContentValues;
import android.util.Log;

/**
 * Created by Matthias on 08.05.2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 5;
    private static final String DB_NAME = "GeoDB";
    // definition of table names
    public static final String TABLE_ACTION_SIMPLE = "ActionSimple";
    public static final String TABLE_ACTION_SOUND = "ActionSound";
    public static final String TABLE_ACTION_BRIGTHNESS = "ActionBrightness";
    public static final String TABLE_ACTION_NOTIFICATION = "ActionNotification";
    public static final String TABLE_ACTION_MESSAGE = "ActionMessage";
    public static final String TABLE_CONDITION_FENCE = "ConditionFence";
    public static final String TABLE_FENCE = "Fence";
    public static final String TABLE_CONDITION_TIME = "ConditionTime";
    public static final String TABLE_DAY_STATUS = "ActionSimple";
    public static final String TABLE_RULE = "DBRule";
    public static final String TABLE_RULE_CONDITION = "RuleCondition";
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
        String createTableRule = "CREATE TABLE " + TABLE_RULE + " ( " +
                "RuleID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Name VARCHAR NOT NULL, " +
                "Status BOOLEAN NOT NULL )";
        String createTableActionSimple = "CREATE TABLE " + TABLE_ACTION_SIMPLE + " ( " +
                "ActionID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Type VARCHAR NOT NULL, " +
                "Status BOOLEAN NOT NULL, " +
                "RuleID REFERENCES " + TABLE_RULE + "(RuleID) NOT NULL )";
        String createTableActionSound = "CREATE TABLE " + TABLE_ACTION_SOUND + " ( " +
                "ActionID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Type VARCHAR NOT NULL, " +
                "Status VARCHAR NOT NULL, " +
                "Volume INTEGER, " +
                "RuleID REFERENCES " + TABLE_RULE + "(RuleID) NOT NULL )";
        String createTableActionBrightness = "CREATE TABLE " + TABLE_ACTION_BRIGTHNESS + " ( " +
                "ActionID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Automatic BOOLEAN NOT NULL," +
                "Value INTEGER, " +
                "RuleID REFERENCES " + TABLE_RULE + "(RuleID) NOT NULL )";
        String createTableActionNotification = "CREATE TABLE " + TABLE_ACTION_NOTIFICATION + " ( " +
                "ActionID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Message TEXT NOT NULL, " +
                "RuleID REFERENCES " + TABLE_RULE + "(RuleID) NOT NULL )";
        String createTableActionMessage = "CREATE TABLE " + TABLE_ACTION_MESSAGE + " ( " +
                "ActionID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Number VARCHAR NOT NULL, " +
                "Message TEXT NOT NULL, " +
                "RuleID REFERENCES " + TABLE_RULE + "(RuleID) NOT NULL )";


        db.execSQL(createTableRule);
        db.execSQL(createTableActionSimple);
        db.execSQL(createTableActionSound);
        db.execSQL(createTableActionBrightness);
        db.execSQL(createTableActionNotification);
        db.execSQL(createTableActionMessage);
        Log.d("DBHelper", "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop old tables; take care of correct order!
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTION_SIMPLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTION_SOUND);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTION_BRIGTHNESS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTION_NOTIFICATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTION_MESSAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONDITION_FENCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FENCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONDITION_TIME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DAY_STATUS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RULE_CONDITION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RULE);

        // create database
        this.onCreate(db);
    }
}
