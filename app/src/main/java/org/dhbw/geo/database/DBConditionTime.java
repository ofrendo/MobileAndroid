package org.dhbw.geo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import org.dhbw.geo.services.AlarmReceiver;
import org.dhbw.geo.services.ContextManager;
import org.dhbw.geo.ui.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A time condition is a set of times or timeframes which are observed.
 * @author Matthias
 */
public class DBConditionTime extends DBCondition {

    private static final String TAG = "DBConditionTime";
    /**
     * the start time for the condition
     */
    private Calendar start;
    /**
     * the end time for the condition
     */
    private Calendar end;
    /**
     * the days ow week to which the time / timeframe applies
     */
    private ArrayList<Integer> days = new ArrayList<Integer>();
    /**
     * the alarm object to maintain the android alarm
     */
    private AlarmReceiver alarm;

    /***
     * Selects all time conditions from the database which are assigned to a given rule.
     * @param ruleId the id of the rule
     * @return an arraylist of the time conditions
     */
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
            // read weekdays
            time.loadAllWeekDays();
            conditions.add(time);
            cursor.moveToNext();
        }
        return conditions;
    }

    /**
     * Selects a single condition time from the database.
     * @param id the id of the condition time
     * @return the condition time fetched from the database
     */
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
        conditionTime.loadAllWeekDays();
        return conditionTime;
    }

    public DBConditionTime(){

    }

    /**
     * Creates a new time condition.
     * Use this to create time conditions fetched from the database.
     * @param id the id of the time condition
     * @param name the name of the time condition
     * @param start the start time of the time condition in milliseconds
     * @param end the end time of the time condition in milliseconds
     */
    public DBConditionTime(long id, String name, long start, long end){
        super(id, name);
        this.start = Calendar.getInstance();
        this.start.setTimeInMillis(start);
        if(end > 0) {
            this.end = Calendar.getInstance();
            this.end.setTimeInMillis(end);
        }
        updateAlarm();
    }

    /**
     * Loads all week days for this time condition from database.
     */
    public void loadAllWeekDays(){
        if(days.size() > 0) return;
        // read from database
        SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
        String[] columns = {
                DBHelper.COLUMN_DAY
        };
        String where = DBHelper.COLUMN_CONDITION_TIME_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        Cursor cursor = db.query(DBHelper.TABLE_DAY_STATUS, columns, where, whereArgs, null, null, null);
        // read result
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            days.add(cursor.getInt(0));
            cursor.moveToNext();
        }
    }

    /**
     * Gets the next week day which is in the days arraylist.
     * @param startDay
     * @return
     */
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

    /**
     * Gets the difference between two weekdays (always positive).
     * @param day1
     * @param day2
     * @return
     */
    private int getDaysDifference(int day1, int day2){
        int diff = day2 - day1;
        if(diff < 0){
            diff += 7;
        }
        return diff;
    }

    /**
     * Updates the alarm which is sent to the android api
     */
    public void updateAlarm(){
        // if it doesn't exist on database, there is no id, so no alarm can be set (will be done after inserting automatically)
        if(!existsOnDB()) return;
        // if there is no AlarmReceiver object yet, create one
        if(alarm == null){
            alarm = new AlarmReceiver();
        }
        // if a time is specified and at least one weekday is selected
        if(start != null && days.size() > 0){
            // set the day to the next weekday in the list
            Calendar now = Calendar.getInstance();
            Calendar alarm = Calendar.getInstance();
            alarm.set(Calendar.HOUR_OF_DAY, getStart().get(Calendar.HOUR_OF_DAY));
            alarm.set(Calendar.MINUTE, getStart().get(Calendar.MINUTE));
            alarm.set(Calendar.SECOND, 0);
            alarm.set(Calendar.MILLISECOND, 0);
            int dayNow = now.get(Calendar.DAY_OF_WEEK);
            // get the next workday in list
            int dayNext = getNextDay(dayNow);
            // if the next workday is the same day
            if(dayNext == dayNow){
                // if it is already later than the alarm time
                Log.d(TAG, "Now - alarm = " + String.valueOf(now.getTimeInMillis() - alarm.getTimeInMillis()));
                if(now.getTimeInMillis() >= alarm.getTimeInMillis()){
                    // increase the day to look for by one and find the next day corresponding to that new "dayNow" (which is in fact one day in the future)
                    dayNext = getNextDay(dayNow % 7 + 1);
                }
            }
            if(dayNext != dayNow){
                // it is another weekday so just set the alarm to that date
                alarm.add(Calendar.DATE, getDaysDifference(dayNow, dayNext));
            } else {
                // there is just this one weekday
                if(now.getTimeInMillis() >= alarm.getTimeInMillis()) {
                    alarm.add(Calendar.DATE, 7);
                }
            }
            Log.d("DBConditionTime", "Now: " + String.valueOf(now.get(Calendar.DAY_OF_WEEK)) + "; Next: " + String.valueOf(dayNext));
            // set the alarm
            this.start = alarm;
            this.alarm.setAlarm(ContextManager.getContext(), this);
        }
    }

    /**
     * Adds a weekday to the weekdays the time condition is applied to.
     * @param day the day to be added
     */
    public void addDay(int day){
        days.add(day);
        updateAlarm();
    }

    /**
     * Removes a weekday from the weekdays the time condition is applied to.
     * @param day the day to be removed
     */
    public void removeDay(int day){
        days.remove(days.indexOf(day));
        updateAlarm();
    }

    /**
     * Inserts the time condition into the database.
     * @param db the reference to the sqlite database
     * @return the id of the inserted condition time
     */
    @Override
    protected long insertIntoDB(SQLiteDatabase db) throws Exception {
        // Create ConditionTime entry
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, getName());
        if(start != null) values.put(DBHelper.COLUMN_START, start.getTimeInMillis());
        if(end != null) values.put(DBHelper.COLUMN_END, end.getTimeInMillis());
        long id = db.insert(DBHelper.TABLE_CONDITION_TIME, null, values);
        setId(id); // has to be done now because of the foreign keys in the next statement!
        // create DayStatus entries
        insertDayStatusIntoDB(db);
        if(getRule() != null){
            writeRuleToDB();
        }
        updateAlarm();
        return id;
    }

    /**
     * Updates the time condition on the database.
     * @param db the reference to the sqlite database
     */
    @Override
    protected void updateOnDB(SQLiteDatabase db) throws Exception {
        // update ConditionTime entry
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, getName());
        if(start != null) values.put(DBHelper.COLUMN_START, start.getTimeInMillis());
        if(end != null) values.put(DBHelper.COLUMN_END, end.getTimeInMillis());
        String where = DBHelper.COLUMN_CONDITION_TIME_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.update(DBHelper.TABLE_CONDITION_TIME, values, where, whereArgs);
        // recreate DayStatus entries
        deleteDayStatusFromDB(db);
        insertDayStatusIntoDB(db);
        if(getRule() != null){
            writeRuleToDB();
        }
    }

    /**
     * Deletes the time condition from the database.
     */
    @Override
    public void deleteFromDB() {
        try {
            SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
            db.execSQL("PRAGMA foreign_keys = ON;");
            // delete DayStatus entries
            deleteDayStatusFromDB(db);
            // delete ConditionTime entry
            String where = DBHelper.COLUMN_CONDITION_TIME_ID + " = ?";
            String[] whereArgs = {String.valueOf(getId())};
            db.delete(DBHelper.TABLE_CONDITION_TIME, where, whereArgs);
        } catch(Exception e){
            e.printStackTrace();
            Toast.makeText(ContextManager.getContext(), "Couldn't delete from time condition from database!", Toast.LENGTH_SHORT);
        }
    }

    /**
     * Removes the conjunction to the rule.
     * This doesn't delete the rule or the condition either.
     */
    @Override
    public void removeRuleFromDB() {
        try {
            SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
            String where = DBHelper.COLUMN_CONDITION_TIME_ID + " = ? AND " + DBHelper.COLUMN_RULE_ID + " = ?";
            String[] whereArgs = {String.valueOf(getId()), String.valueOf(getRule().getId())};
            db.delete(DBHelper.TABLE_RULE_CONDITION, where, whereArgs);
        } catch(Exception e){
            e.printStackTrace();
            Toast.makeText(ContextManager.getContext(), "Couldn't remove rule from time condition on database!", Toast.LENGTH_SHORT);
        }
    }

    /**
     * Inserts the conjunction of the condition with the assigned rule.
     */
    @Override
    protected void writeRuleToDB() {
        try {
            removeRuleFromDB(); // in case it was already written on the database; avoid duplicates
            SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN_RULE_ID, getRule().getId());
            values.put(DBHelper.COLUMN_CONDITION_TIME_ID, getId());
            db.insert(DBHelper.TABLE_RULE_CONDITION, null, values);
        } catch(Exception e){
            e.printStackTrace();
            Toast.makeText(ContextManager.getContext(), "Couldn't write rule to time condition on database!", Toast.LENGTH_SHORT);
        }
    }

    /**
     * Checks whether the time condition is fulfilled.
     * @return true if we are in the time frame, false otherwise
     */
    @Override
    public boolean isConditionMet() {
        if(start == null) return true;  // if the condition has no time, it is not a condition
        Calendar now = Calendar.getInstance();
        // check the weekday
        if(!days.contains(now.get(Calendar.DAY_OF_WEEK))){
            Log.d(TAG, "weekday invalid");
            return false;
        }
        // check the time
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, start.get(Calendar.HOUR_OF_DAY));
        startTime.set(Calendar.MINUTE, start.get(Calendar.MINUTE));
        startTime.set(Calendar.SECOND, start.get(Calendar.SECOND));
        startTime.set(Calendar.MILLISECOND, start.get(Calendar.MILLISECOND));
        if(end != null && (end.get(Calendar.HOUR_OF_DAY) != start.get(Calendar.HOUR_OF_DAY) || end.get(Calendar.MINUTE) != start.get(Calendar.MINUTE))){    // if it is a time frame
            Calendar endTime = Calendar.getInstance();
            endTime.set(Calendar.HOUR_OF_DAY, end.get(Calendar.HOUR_OF_DAY));
            endTime.set(Calendar.MINUTE, end.get(Calendar.MINUTE));
            endTime.set(Calendar.SECOND, end.get(Calendar.SECOND));
            endTime.set(Calendar.MILLISECOND, end.get(Calendar.MILLISECOND));
            if(startTime.getTimeInMillis() <= now.getTimeInMillis() && now.getTimeInMillis() <= endTime.getTimeInMillis()){
                return true;
            } else {
                Log.d(TAG, "time invalid 1; now - start = " + String.valueOf(now.getTimeInMillis()-startTime.getTimeInMillis()) + "; end - now = " + String.valueOf(endTime.getTimeInMillis() - now.getTimeInMillis()));
                return false;
            }
        } else {            // if it is a single time
            // make it a 1 minute frame to avoid problems caused by slow devices etc.
            long time1 = startTime.getTimeInMillis();
            long time2 = time1 + 1000 * 60;
            if(time1 <= now.getTimeInMillis() && now.getTimeInMillis() <= time2){
                return true;
            } else {
                Log.d(TAG, "time invalid 2");
                return false;
            }
        }
    }

    /**
     * Inserts all assigned workdays to the database.
     * @param db
     */
    private void insertDayStatusIntoDB(SQLiteDatabase db) throws Exception {
        for(int i = 0; i < days.size(); i++){
            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN_CONDITION_TIME_ID, getId());
            values.put(DBHelper.COLUMN_DAY, days.get(i));
            db.insert(DBHelper.TABLE_DAY_STATUS, null, values);
        }
    }

    /**
     * Deletes all assigned workdays from the database.
     * @param db
     */
    private void deleteDayStatusFromDB(SQLiteDatabase db) throws Exception {
        if(!existsOnDB()) return;   // if the ConditionTime doesn't exist, there shouldn't be any DayStatuses
        String where = DBHelper.COLUMN_CONDITION_TIME_ID + " = ?";
        String[] whereArgs = {String.valueOf(getId())};
        db.delete(DBHelper.TABLE_DAY_STATUS, where, whereArgs);
    }

    public void setStart(int hour, int minute){
        if(start == null) start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, hour);
        start.set(Calendar.MINUTE, minute);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        updateAlarm();
    }

    public void setEnd(int hour, int minute){
        if(end == null) end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY, hour);
        end.set(Calendar.MINUTE, minute);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);
    }

    public Calendar getStart() {
        return start;
    }

    public Calendar getEnd() {
        return end;
    }


    public ArrayList<Integer> getDays(){
        return days;
    }
}
