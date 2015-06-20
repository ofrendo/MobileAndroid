package org.dhbw.geo.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.content.ContentValues;
import android.util.Log;

import org.dhbw.geo.services.ContextManager;
import org.dhbw.geo.ui.MainActivity;

/**
 * The helper manages the connection to the sqlite database.
 * @author Matthias
 */
public class DBHelper extends SQLiteOpenHelper {
    /**
     * reference to the helper instance
     */
    private static DBHelper helper;
    /**
     * the database version (this has to be increased)
     */
    private static final int DB_VERSION = 18;
    /**
     * the name of the sqlite database (the file it is stored in).
     */
    private static final String DB_NAME = "GeoDB";
    // definition of table names
    public static final String TABLE_ACTION_SIMPLE = "ActionSimple";
    public static final String TABLE_ACTION_SOUND = "ActionSound";
    public static final String TABLE_ACTION_BRIGHTNESS = "ActionBrightness";
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
    public static final String COLUMN_VALUE = "Value";
    public static final String COLUMN_AUTOMATIC = "Automatic";
    public static final String COLUMN_ACTION_BRIGHTNESS_ID = "ActionBrightnessID";
    public static final String COLUMN_ACTION_NOTIFICATION_ID = "ActionNotificationID";
    public static final String COLUMN_MESSAGE = "Message";
    public static final String COLUMN_NUMBER = "Number";
    public static final String COLUMN_ACTION_MESSAGE_ID = "ActionMessageID";
    public static final String COLUMN_CONDITION_FENCE_ID = "ConditionFenceID";
    public static final String COLUMN_FENCE_ID = "FenceID";
    public static final String COLUMN_LATITUDE = "Latitude";
    public static final String COLUMN_LONGITUDE = "Longitude";
    public static final String COLUMN_RADIUS = "Radius";
    public static final String COLUMN_CONDITION_TIME_ID = "ConditionTimeID";
    public static final String COLUMN_DAY = "Day";
    public static final String COLUMN_START = "Start";
    public static final String COLUMN_END = "End";
    // the database instance
    private SQLiteDatabase db;

    /**
     * Creates the helper instance (Singleton)
     */
    private DBHelper() {
        super(ContextManager.getContext(), DB_NAME, null, DB_VERSION);
        Log.d("DBHelper", "Constructor");
        helper = this;

    }

    /**
     * Gets the helper instance (Singleton)
     * @return the helper instance
     */
    public static DBHelper getInstance(){
        if(helper == null){
            helper = new DBHelper();
        }
        return helper;
    }

    /**
     * writes all database tables and its contents to the log.
     */
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

    /**
     * Writes a specific database table and its rows to the log.
     * @param name the table name
     */
    public void logTable(String name) {
        db = this.getWritableDatabase();
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

    /**
     * Creates the database schema.
     * Automatically called by android.
     * @param db the sqlite database
     */
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
                COLUMN_ACTIVE + " BOOLEAN NOT NULL, " +
                COLUMN_RULE_ID + " INTEGER REFERENCES " + TABLE_RULE + "(RuleID) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL )";
        String createTableActionSound = "CREATE TABLE " + TABLE_ACTION_SOUND + " ( " +
                COLUMN_ACTION_SOUND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TYPE + " VARCHAR NOT NULL, " +
                COLUMN_STATUS + " VARCHAR NOT NULL, " +
                COLUMN_VOLUME + " INTEGER, " +
                COLUMN_ACTIVE + " BOOLEAN NOT NULL, " +
                COLUMN_RULE_ID + " INTEGER REFERENCES " + TABLE_RULE + "(RuleID) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL )";
        String createTableActionBrightness = "CREATE TABLE " + TABLE_ACTION_BRIGHTNESS + " ( " +
                COLUMN_ACTION_BRIGHTNESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_AUTOMATIC + " BOOLEAN NOT NULL," +
                COLUMN_VALUE + " INTEGER, " +
                COLUMN_ACTIVE + " BOOLEAN NOT NULL, " +
                COLUMN_RULE_ID + " INTEGER REFERENCES " + TABLE_RULE + "(RuleID) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL )";
        String createTableActionNotification = "CREATE TABLE " + TABLE_ACTION_NOTIFICATION + " ( " +
                COLUMN_ACTION_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MESSAGE + " TEXT NOT NULL, " +
                COLUMN_ACTIVE + " BOOLEAN NOT NULL, " +
                COLUMN_RULE_ID + " INTEGER REFERENCES " + TABLE_RULE + "(RuleID) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL )";
        String createTableActionMessage = "CREATE TABLE " + TABLE_ACTION_MESSAGE + " ( " +
                COLUMN_ACTION_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NUMBER + " VARCHAR NOT NULL, " +
                COLUMN_MESSAGE + " TEXT NOT NULL, " +
                COLUMN_ACTIVE + " BOOLEAN NOT NULL, " +
                COLUMN_RULE_ID + " INTEGER REFERENCES " + TABLE_RULE + "(RuleID) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL )";
        String createTableConditionFence = "CREATE TABLE " + TABLE_CONDITION_FENCE + " ( " +
                COLUMN_CONDITION_FENCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " VARCHAR, " +
                COLUMN_TYPE + " VARCHAR NOT NULL )";
        String createTableFence = "CREATE TABLE " + TABLE_FENCE + " ( " +
                COLUMN_FENCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CONDITION_FENCE_ID + " INTEGER REFERENCES " + TABLE_CONDITION_FENCE + "(ConditionFenceID) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL," +
                COLUMN_LATITUDE + " NUMERIC NOT NULL, " +
                COLUMN_LONGITUDE + " NUMERIC NOT NULL," +
                COLUMN_RADIUS + " NUMERIC NOT NULL )";
        String createTableConditionTime = "CREATE TABLE " + TABLE_CONDITION_TIME + " ( " +
                COLUMN_CONDITION_TIME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " VARCHAR, " +
                COLUMN_START + " INTEGER," +
                COLUMN_END + " INTEGER )";
        String createTableDayStatus = "CREATE TABLE " + TABLE_DAY_STATUS + " ( " +
                COLUMN_CONDITION_TIME_ID + " INTEGER REFERENCES " + TABLE_CONDITION_TIME + "(ConditionTimeID) ON UPDATE CASCADE ON DELETE CASCADE," +
                COLUMN_DAY + " INTEGER NOT NULL )";
        String createTableRuleCondition = "CREATE TABLE " + TABLE_RULE_CONDITION + " ( " +
                COLUMN_RULE_ID + " INTEGER REFERENCES " + TABLE_RULE + "(RuleID) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL, " +
                COLUMN_CONDITION_FENCE_ID + " INTEGER REFERENCES " + TABLE_CONDITION_FENCE + "(ConditionFenceID) ON UPDATE CASCADE ON DELETE CASCADE, " +
                COLUMN_CONDITION_TIME_ID + " INTEGER REFERENCES " + TABLE_CONDITION_TIME + "(ConditionTimeID) ON UPDATE CASCADE ON DELETE CASCADE )";

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

    /**
     * Upgrades the database schema.
     * Called when the version of the db changes
     * @param db the sqlite database
     * @param oldVersion the old version number
     * @param newVersion the new version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop old tables; take care of correct order!
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTION_SIMPLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTION_SOUND);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTION_BRIGHTNESS);
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
