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

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "GeoDB";
    private static final String TABLE_LOG = "log";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d("DBHelper", "Constructor");
    }

    public void testLog() {
        // insert entry
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Action", "StartApp"); // get author

        db.insert(TABLE_LOG, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        db.close();

        // get all entries
        String query = "SELECT  * FROM " + TABLE_LOG;

        // 2. get reference to writable DB
        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        if (cursor.moveToFirst()) {
            do {
                Log.d("DBHelper", cursor.getString(0) + ": " + cursor.getString(1));
            } while (cursor.moveToNext());
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createLogTable = "CREATE TABLE " + TABLE_LOG + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, Action VARCHAR(32) NOT NULL)";
        db.execSQL(createLogTable);
        Log.d("DBHelper", "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop old tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOG);

        // create database
        this.onCreate(db);
    }
}
