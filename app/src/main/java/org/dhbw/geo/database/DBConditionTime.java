package org.dhbw.geo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.dhbw.geo.services.AlarmReceiver;
import org.dhbw.geo.ui.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Matthias on 13.06.2015.
 */
public class DBConditionTime extends DBCondition {

    private Calendar start = Calendar.getInstance();
    private Calendar end = Calendar.getInstance();
    private ArrayList<Integer> days = new ArrayList<Integer>();
    private AlarmReceiver alarm;

    public static ArrayList<DBCondition> selectAllFromDB(long ruleId){
        ArrayList<DBCondition> conditions = new ArrayList<DBCondition>();
        // read from database
        SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
        String query = "SELECT " +
                DBHelper.COLUMN_CONDITION_TIME_ID + ", " +
                DBHelper.TABLE_CONDITION_TIME + "." + DBHelper.COLUMN_NAME + " AS " + DBHelper.COLUMN_NAME + ", " +
                DBHelper.TABLE_CONDITION_TIME + "." + DBHelper.COLUMN_START + " AS " + DBHelper.COLUMN_START + ", " +
                DBHelper.TABLE_CONDITION_TIME + "." + DBHelper.COLUMN_END + " AS " + DBHelper.COLUMN_END +
                " FROM " + DBHelper.TABLE_CONDITION_TIME + " NATURAL JOIN " + DBHelper.TABLE_RULE_CONDITION + " WHERE " + DBHelper.COLUMN_RULE_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(ruleId)});
        // read result
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            DBConditionTime time = new DBConditionTime(cursor.getLong(0), cursor.getString(1), cursor.getLong(2), cursor.getLong(3));
            conditions.add(time);
            cursor.moveToNext();
        }
        return conditions;
    }

    public static DBConditionTime selectFromDB(long id) {
        // read from database
        SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
        String[] columns = {
                DBHelper.COLUMN_CONDITION_TIME_ID,
                DBHelper.COLUMN_NAME,
                DBHelper.COLUMN_START,
                DBHelper.COLUMN_END
        };
        String where = DBHelper.COLUMN_CONDITION_TIME_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        Cursor cursor = db.query(DBHelper.TABLE_CONDITION_TIME, columns, where, whereArgs, null, null, null);
        // read result
        cursor.moveToFirst();
        if(cursor.isAfterLast()) return null;
        DBConditionTime conditionTime = new DBConditionTime(cursor.getLong(0), cursor.getString(1), cursor.getLong(2), cursor.getLong(3));
        return conditionTime;
    }

    public DBConditionTime(){

    }

    public DBConditionTime(long id, String name, long start, long end){
        super(id, name);
        this.start.setTimeInMillis(start);
        this.end.setTimeInMillis(end);
    }

    private int getNextDay(int startDay){
        int dayNext = -1;
        int count = 0;
        while(dayNext == -1 && count < 7){
            int day = ((startDay + count - 1) % 7) + 1;
            if(days.contains(day)){
                dayNext = day;
            }
            count++;
        }
        return dayNext;
    }

    private int getDaysDifference(int day1, int day2){
        int diff = day2 - day1;
        if(diff < 0){
            diff += 7;
        }
        return diff;
    }

    private void updateAlarm(){
        // if there is no AlarmReceiver object yet, create one
        if(alarm == null){
            alarm = new AlarmReceiver();
        }
        // if a time is specified and at least one weekday is selected
        if(start != null && days.size() > 0){
            // set the day to the next weekday in the list
            // TODO: auch die uhrzeit mit einberechnen (ist die zeit für "heute" schon abgelaufen oder nicht) & calendar-werte (start + end) updaten!
            Calendar now = Calendar.getInstance();
            Calendar alarm = Calendar.getInstance();
            alarm.set(Calendar.HOUR_OF_DAY, getStart().get(Calendar.HOUR_OF_DAY));
            alarm.set(Calendar.MINUTE, getStart().get(Calendar.MINUTE));
            alarm.set(Calendar.SECOND, 0);
            int dayNow = now.get(Calendar.DAY_OF_WEEK);
            // get the next workday in list
            int dayNext = getNextDay(dayNow);
            // if the next workday is the same day
            if(dayNext == dayNow){
                // if it is already later than the alarm time
                if(now.getTimeInMillis() > alarm.getTimeInMillis()){
                    // increase the day to look for by one and find the next day corresponding to that new "dayNow" (which is in fact one day in the future)
                    dayNext = getNextDay(dayNow % 7 + 1);
                }
            }
            if(dayNext != dayNow){
                // it is another weekday so just set the alarm to that date
                alarm.add(Calendar.DATE, getDaysDifference(dayNow, dayNext));
            } else {
                // there is just this one weekday, so set Alarm one week in the future
                alarm.add(Calendar.DATE, 7);
            }
            Log.d("DBConditionTime", "Now: " + String.valueOf(now.get(Calendar.DAY_OF_WEEK)) + "; Next: " + String.valueOf(dayNext));
            // set the alarm
            this.alarm.setAlarm(MainActivity.getContext(), this);
        }
    }

    // TODO: remove this function and replace it with something more useful
    public void testAlarm(){
        updateAlarm();
    }

    public void addDay(int day){
        days.add(day);
    }
    public void removeDay(int day){
        days.remove(days.indexOf(day));
    }

    @Override
    protected long insertIntoDB(SQLiteDatabase db) {
        // Create ConditionTime entry
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, getName());
        values.put(DBHelper.COLUMN_START, start.getTimeInMillis());
        values.put(DBHelper.COLUMN_END, end.getTimeInMillis());
        long id = db.insert(DBHelper.TABLE_CONDITION_TIME, null, values);
        setId(id); // has to be done now because of the foreign keys in the next statement!
        // create DayStatus entries
        insertDayStatusIntoDB(db);
        writeRuleToDB();
        return id;
    }

    @Override
    protected void updateOnDB(SQLiteDatabase db) {
        // update ConditionTime entry
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, getName());
        values.put(DBHelper.COLUMN_START, start.getTimeInMillis());
        values.put(DBHelper.COLUMN_END, end.getTimeInMillis());
        String where = DBHelper.COLUMN_CONDITION_TIME_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.update(DBHelper.TABLE_CONDITION_TIME, values, where, whereArgs);
        // recreate DayStatus entries
        deleteDayStatusFromDB(db);
        insertDayStatusIntoDB(db);
        writeRuleToDB();
    }

    @Override
    public void deleteFromDB() {
        SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
        // delete DayStatus entries
        deleteDayStatusFromDB(db);
        // delete ConditionTime entry
        String where = DBHelper.COLUMN_CONDITION_TIME_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.delete(DBHelper.TABLE_CONDITION_TIME, where, whereArgs);
    }

    @Override
    public void removeRuleFromDB() {
        SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
        String where = DBHelper.COLUMN_CONDITION_TIME_ID + " = ? AND " + DBHelper.COLUMN_RULE_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId()), String.valueOf(getRule().getId())};
        db.delete(DBHelper.TABLE_RULE_CONDITION, where, whereArgs);
    }

    @Override
    public void writeRuleToDB() {
        removeRuleFromDB(); // in case it was already written on the database; avoid duplicates
        SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_RULE_ID, getRule().getId());
        values.put(DBHelper.COLUMN_CONDITION_TIME_ID, getId());
        db.insert(DBHelper.TABLE_RULE_CONDITION, null, values);
    }

    @Override
    public boolean isConditionMet() {
        // TODO: implement this!
        return false;
    }

    private void insertDayStatusIntoDB(SQLiteDatabase db){
        for(int i = 0; i < days.size(); i++){
            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN_CONDITION_TIME_ID, getId());
            values.put(DBHelper.COLUMN_DAY, days.get(i));
            db.insert(DBHelper.TABLE_DAY_STATUS, null, values);
        }
    }

    private void deleteDayStatusFromDB(SQLiteDatabase db){
        if(!existsOnDB()) return;   // if the ConditionTime doesn't exist, there shouldn't be any DayStatuses
        String where = DBHelper.COLUMN_CONDITION_TIME_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.delete(DBHelper.TABLE_DAY_STATUS, where, whereArgs);
    }

    public void setStart(int hour, int minute){
        start.set(Calendar.HOUR_OF_DAY, hour);
        start.set(Calendar.MINUTE, minute);
    }

    public void setEnd(int hour, int minute){
        end.set(Calendar.HOUR_OF_DAY, hour);
        end.set(Calendar.MINUTE, minute);
    }

    public Calendar getStart() {
        return start;
    }

    public void setStart(Calendar start) {
        this.start = start;
    }

    public Calendar getEnd() {
        return end;
    }

    public void setEnd(Calendar end) {
        this.end = end;
    }

    public ArrayList<Integer> getDays(){
        return days;
    }
}
