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
 * This action sends a message via the Short Message Service (SMS).
 * @author Matthias
 */
public class DBActionMessage extends DBAction {
    /**
     * the phone number the message shall be sent to
     */
    private String number;
    /**
     * the specific message
     */
    private String message;

    /**
     * Selects all message actions from the database which are assigned to a given rule
     * @param ruleId the id of the rule for which the message actions shall be selected
     * @return an arraylist of the message actions fetched from the database
     */
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

    /**
     * Selects a specific message action from the database.
     * @param id the id of the message action
     * @return the fetched message action
     */
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

    /**
     * Sends the message via SMS.
     */
    @Override
    protected void doActionStart() {
        SMSFactory.createSMS(number, message);
    }

    /**
     * Creates a new message action.
     * Use this to create message actions fetched from the database.
     * @param id the message id
     * @param number the phone number
     * @param message the message
     * @param active the flag whether the action is active
     */
    public DBActionMessage(long id, String number, String message, boolean active){
        super(id, active);
        this.number = number;
        this.message = message;
    }

    /**
     * Inserts the message action into the database.
     * @param db the reference to the sqlite database
     * @return the id of the inserted message action
     */
    @Override
    protected long insertIntoDB(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NUMBER, number);
        values.put(DBHelper.COLUMN_MESSAGE, message);
        values.put(DBHelper.COLUMN_RULE_ID, getRule().getId());
        values.put(DBHelper.COLUMN_ACTIVE, isActive());
        return db.insert(DBHelper.TABLE_ACTION_MESSAGE, null, values);
    }

    /**
     * Updates the message action on the database.
     * @param db the reference to the sqlite database
     */
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

    /**
     * Deletes the message action from the database.
     */
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
