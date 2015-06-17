package org.dhbw.geo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.dhbw.geo.hardware.NotificationFactory;
import org.dhbw.geo.hardware.SMSFactory;
import org.dhbw.geo.services.ContextManager;
import org.dhbw.geo.ui.MainActivity;

import java.util.ArrayList;

/**
 * Created by Matthias on 12.06.2015.
 */
public class DBActionMessage extends DBAction {

    private String number;
    private String message;

    public static ArrayList<DBAction> selectAllFromDB(long ruleId){
        ArrayList<DBAction> actions = new ArrayList<DBAction>();
        // read from database
        SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
        String[] columns = {
                DBHelper.COLUMN_ACTION_MESSAGE_ID,
                DBHelper.COLUMN_NUMBER,
                DBHelper.COLUMN_MESSAGE,
                DBHelper.COLUMN_ACTIVE
        };
        String where = DBHelper.COLUMN_RULE_ID + " = ?";
        String[] whereArgs = {String.valueOf(ruleId)};
        Cursor cursor = db.query(DBHelper.TABLE_ACTION_MESSAGE, columns, where, whereArgs, null, null, null);
        // read result
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            DBActionMessage action = new DBActionMessage(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3) != 0);
            actions.add(action);
            cursor.moveToNext();
        }
        return actions;
    }

    public static DBActionMessage selectFromDB(long id) {
        // read from database
        SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
        String[] columns = {
                DBHelper.COLUMN_ACTION_MESSAGE_ID,
                DBHelper.COLUMN_NUMBER,
                DBHelper.COLUMN_MESSAGE,
                DBHelper.COLUMN_ACTIVE
        };
        String where = DBHelper.COLUMN_ACTION_MESSAGE_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        Cursor cursor = db.query(DBHelper.TABLE_ACTION_MESSAGE, columns, where, whereArgs, null, null, null);
        // read result
        cursor.moveToFirst();
        if(cursor.isAfterLast()) return null;
        DBActionMessage action = new DBActionMessage(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3) != 0);
        return action;
    }

    public DBActionMessage(){

    }

    @Override
    protected void doActionStart() {
        // to avoid costs for anybody without a flatrate don't send any SMS!
        // TODO: Change it back!

        /* // // SMSFactory.createSMS(number, message);*/
        NotificationFactory.createNotification(ContextManager.getContext(), "SMS would've been sent to: " + number, message, false);
    }

    @Override
    protected void doActionStop() {
        // nothing to do here as it is a one time action
    }

    public DBActionMessage(long id, String number, String message, boolean active){
        super(id, active);
        this.number = number;
        this.message = message;
    }

    @Override
    protected long insertIntoDB(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NUMBER, number);
        values.put(DBHelper.COLUMN_MESSAGE, message);
        values.put(DBHelper.COLUMN_RULE_ID, getRule().getId());
        values.put(DBHelper.COLUMN_ACTIVE, isActive());
        return db.insert(DBHelper.TABLE_ACTION_MESSAGE, null, values);
    }

    @Override
    protected void updateOnDB(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NUMBER, number);
        values.put(DBHelper.COLUMN_MESSAGE, message);
        values.put(DBHelper.COLUMN_RULE_ID, getRule().getId());
        values.put(DBHelper.COLUMN_ACTIVE, isActive());
        String where = DBHelper.COLUMN_ACTION_MESSAGE_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.update(DBHelper.TABLE_ACTION_MESSAGE, values, where, whereArgs);
    }

    @Override
    public void deleteFromDB() {
        SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
        String where = DBHelper.COLUMN_ACTION_MESSAGE_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.delete(DBHelper.TABLE_ACTION_MESSAGE, where, whereArgs);
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
