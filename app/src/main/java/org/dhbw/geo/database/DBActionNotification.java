package org.dhbw.geo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.dhbw.geo.hardware.NotificationFactory;
import org.dhbw.geo.services.ContextManager;
import org.dhbw.geo.ui.MainActivity;

import java.util.ArrayList;

/**
 * Created by Matthias on 12.06.2015.
 */
public class DBActionNotification extends DBAction {

    private String message;

    public static ArrayList<DBAction> selectAllFromDB(long ruleId){
        ArrayList<DBAction> actions = new ArrayList<DBAction>();
        // read from database
        SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
        String[] columns = {
                DBHelper.COLUMN_ACTION_NOTIFICATION_ID,
                DBHelper.COLUMN_MESSAGE,
                DBHelper.COLUMN_ACTIVE
        };
        String where = DBHelper.COLUMN_RULE_ID + " = ?";
        String[] whereArgs = {String.valueOf(ruleId)};
        Cursor cursor = db.query(DBHelper.TABLE_ACTION_NOTIFICATION, columns, where, whereArgs, null, null, null);
        // read result
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            DBActionNotification action = new DBActionNotification(cursor.getLong(0), cursor.getString(1), cursor.getInt(2) != 0);
            actions.add(action);
            cursor.moveToNext();
        }
        return actions;
    }

    public static DBActionNotification selectFromDB(long id) {
        // read from database
        SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
        String[] columns = {
                DBHelper.COLUMN_ACTION_NOTIFICATION_ID,
                DBHelper.COLUMN_MESSAGE,
                DBHelper.COLUMN_ACTIVE
        };
        String where = DBHelper.COLUMN_ACTION_NOTIFICATION_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        Cursor cursor = db.query(DBHelper.TABLE_ACTION_NOTIFICATION, columns, where, whereArgs, null, null, null);
        // read result
        cursor.moveToFirst();
        if(cursor.isAfterLast()) return null;
        DBActionNotification action = new DBActionNotification(cursor.getLong(0), cursor.getString(1), cursor.getInt(2) != 0);
        return action;
    }

    public DBActionNotification(){

    }

    @Override
    protected void doAction() {
        NotificationFactory.createNotification(ContextManager.getContext(), getRule().getName(), message, false);
    }

    public DBActionNotification(long id, String message, boolean active){
        super(id, active);
        this.message = message;
    }

    @Override
    protected long insertIntoDB(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_MESSAGE, message);
        values.put(DBHelper.COLUMN_ACTIVE, isActive());
        values.put(DBHelper.COLUMN_RULE_ID, getRule().getId());
        return db.insert(DBHelper.TABLE_ACTION_NOTIFICATION, null, values);
    }

    @Override
    protected void updateOnDB(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_MESSAGE, message);
        values.put(DBHelper.COLUMN_ACTIVE, isActive());
        values.put(DBHelper.COLUMN_RULE_ID, getRule().getId());
        String where = DBHelper.COLUMN_ACTION_NOTIFICATION_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.update(DBHelper.TABLE_ACTION_NOTIFICATION, values, where, whereArgs);
    }

    @Override
    public void deleteFromDB() {
        SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
        String where = DBHelper.COLUMN_ACTION_NOTIFICATION_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.delete(DBHelper.TABLE_ACTION_NOTIFICATION, where, whereArgs);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
