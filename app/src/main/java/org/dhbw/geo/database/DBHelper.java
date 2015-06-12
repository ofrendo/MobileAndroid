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
    // reference to the helper instance
    private static DBHelper helper;

    private static final int DB_VERSION = 10;
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
    public static final String TABLE_DAY_STATUS = "DayStatus";
    public static final String TABLE_RULE = "Rule";
    public static final String TABLE_RULE_CONDITION = "RuleCondition";
    // definition of column names
    public static final String COLUMN_RULE_ID = "RuleID";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_ACTIVE = "Active";
    public static final String COLUMN_ACTION_SIMPLE_ID = "ActionSimpleID";
    public static final String COLUMN_TYPE = "Type";
    public static final String COLUMN_STATUS = "Status";
    public static final String COLUMN_ACTION_SOUND_ID = "ActionSoundID";
    public static final String COLUMN_VOLUME = "Volume";
    // the database instance
    private SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d("DBHelper", "Constructor");
        helper = this;

    }

    public static DBHelper getHelper(){
        return helper;
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
                COLUMN_RULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " VARCHAR NOT NULL, " +
                COLUMN_ACTIVE + " BOOLEAN NOT NULL )";
        String createTableActionSimple = "CREATE TABLE " + TABLE_ACTION_SIMPLE + " ( " +
                COLUMN_ACTION_SIMPLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TYPE + " VARCHAR NOT NULL, " +
                COLUMN_STATUS + " BOOLEAN NOT NULL, " +
                COLUMN_RULE_ID + " INTEGER REFERENCES " + TABLE_RULE + "(RuleID) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL )";
        String createTableActionSound = "CREATE TABLE " + TABLE_ACTION_SOUND + " ( " +
                COLUMN_ACTION_SOUND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TYPE + " VARCHAR NOT NULL, " +
                COLUMN_STATUS + " VARCHAR NOT NULL, " +
                COLUMN_VOLUME + " INTEGER, " +
                COLUMN_RULE_ID + " INTEGER REFERENCES " + TABLE_RULE + "(RuleID) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL )";
        String createTableActionBrightness = "CREATE TABLE " + TABLE_ACTION_BRIGTHNESS + " ( " +
                "ActionBrightnessID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Automatic BOOLEAN NOT NULL," +
                "Value INTEGER, " +
                COLUMN_RULE_ID + " INTEGER REFERENCES " + TABLE_RULE + "(RuleID) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL )";
        String createTableActionNotification = "CREATE TABLE " + TABLE_ACTION_NOTIFICATION + " ( " +
                "ActionNotificationID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Message TEXT NOT NULL, " +
                COLUMN_RULE_ID + " INTEGER REFERENCES " + TABLE_RULE + "(RuleID) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL )";
        String createTableActionMessage = "CREATE TABLE " + TABLE_ACTION_MESSAGE + " ( " +
                "ActionMessageID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Number VARCHAR NOT NULL, " +
                "Message TEXT NOT NULL, " +
                COLUMN_RULE_ID + " INTEGER REFERENCES " + TABLE_RULE + "(RuleID) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL )";
        String createTableConditionFence = "CREATE TABLE " + TABLE_CONDITION_FENCE + " ( " +
                "ConditionFenceID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Name VARCHAR, " +
                "Type VARCHAR NOT NULL )";
        String createTableFence = "CREATE TABLE " + TABLE_FENCE + " ( " +
                "FenceID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ConditionFenceID INTEGER REFERENCES " + TABLE_CONDITION_FENCE + "(ConditionFenceID) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL," +
                "Latitude NUMERIC NOT NULL, " +
                "Longitude NUMERIC NOT NULL," +
                "Radius NUMERIC NOT NULL )";
        String createTableConditionTime = "CREATE TABLE " + TABLE_CONDITION_TIME + " ( " +
                "ConditionTimeID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Name VARCHAR, " +
                "Start DATETIME NOT NULL," +
                "End DATETIME )";
        String createTableDayStatus = "CREATE TABLE " + TABLE_DAY_STATUS + " ( " +
                "ConditionTimeID INTEGER REFERENCES " + TABLE_CONDITION_TIME + "(ConditionTimeID) ON UPDATE CASCADE ON DELETE CASCADE," +
                "Day VARCHAR NOT NULL," +
                "Status BOOLEAN NOT NULL )";
        String createTableRuleCondition = "CREATE TABLE " + TABLE_RULE_CONDITION + " ( " +
                COLUMN_RULE_ID + " INTEGER REFERENCES " + TABLE_RULE + "(RuleID) NOT NULL, " +
                "ConditionFenceID INTEGER REFERENCES " + TABLE_CONDITION_FENCE + "(ConditionFenceID) ON UPDATE CASCADE ON DELETE CASCADE, " +
                "ConditionTimeID INTEGER REFERENCES " + TABLE_CONDITION_TIME + "(ConditionTimeID) ON UPDATE CASCADE ON DELETE CASCADE )";

        db.execSQL(createTableRule);
        db.execSQL(createTableActionSimple);
        db.execSQL(createTableActionSound);
        db.execSQL(createTableActionBrightness);
        db.execSQL(createTableActionNotification);
        db.execSQL(createTableActionMessage);
        db.execSQL(createTableConditionFence);
        db.execSQL(createTableFence);
        db.execSQL(createTableConditionTime);
        db.execSQL(createTableDayStatus);
        db.execSQL(createTableRuleCondition);
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
