package org.dhbw.geo.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.CompoundButton;
import android.widget.GridLayout;

import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import org.dhbw.geo.R;
import org.dhbw.geo.database.DBConditionTime;
import org.dhbw.geo.database.DBRule;

import java.util.ArrayList;
import java.util.Calendar;

public class Time extends ActionBarActivity {

    Activity activity;
    long ruleID;
    DBConditionTime time;

    @Override
    public void onBackPressed() {
        Intent parent = getParentActivityIntent();
        //pls enter ruleID
        parent.putExtra("RuleID",ruleID);
        parent.putExtra("ScreenID",1);
        startActivity(parent);
        finish();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        activity = this;

        Intent i = getIntent();
        //get ruleId
        ruleID = i.getLongExtra("DBRuleID",-1);
        long timeID = i.getLongExtra("DBConditionTimeID", -1);
        if (timeID != -1){
            time = DBConditionTime.selectFromDB(timeID);
        }
        else{
            time = new DBConditionTime();
            time.setRule(DBRule.selectFromDB(ruleID));
            time.setName(i.getStringExtra("DBConditionTimeName"));
            time.setStart(0, 0);
            time.setEnd(0,1);
            time.writeToDB();

            i.putExtra("DBConditionTimeID", time.getId());
        }

        setTitle("" + time.getName());

        final TextView textEnd = (TextView)findViewById(R.id.timeEnd);
        final TextView textStart = (TextView)findViewById(R.id.timeStart);


        final Calendar calendarStart = time.getStart();
        final Calendar calendarEnd = time.getEnd();
        textEnd.setText(new StringBuilder()
                .append(pad(calendarEnd.get(Calendar.HOUR_OF_DAY))).append(":")
                .append(pad(calendarEnd.get(Calendar.MINUTE))));

        //startTime
        textStart.setText(new StringBuilder()
                .append(pad(calendarStart.get(Calendar.HOUR_OF_DAY))).append(":")
                .append(pad(calendarStart.get(Calendar.MINUTE))));



        //disable/enable timeframe
        final Switch enabler = (Switch)findViewById(R.id.time_enableFrame);
        if (calendarStart.get(Calendar.HOUR_OF_DAY) == calendarEnd.get(Calendar.HOUR_OF_DAY)&&
                calendarStart.get(Calendar.MINUTE) == calendarEnd.get(Calendar.MINUTE)){
                enabler.setChecked(false);
                textEnd.setEnabled(false);
        }else
        {
            enabler.setChecked(true);
        }

        enabler.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                textEnd.setEnabled(isChecked);
                if (!isChecked) {
                    //set Endtime = starttime
                    textEnd.setText(textStart.getText());
                    setHour(calendarEnd, getHour(calendarStart));
                    setMinute(calendarEnd, getMinute(calendarStart));
                    time.setEnd(getHour(calendarEnd), getMinute(calendarStart));
                    time.writeToDB();
                }
            }
        });



        //timeDialog for Starttime
        textStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog pickStart = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        time.setStart(hourOfDay, minute);

                        textStart.setText(pad(hourOfDay) + ":" + pad(minute));
                        setHour(calendarStart, hourOfDay);
                        setMinute(calendarStart, minute);

                        if (!enabler.isChecked()) {
                            time.setEnd(hourOfDay, minute);
                            textEnd.setText(pad(hourOfDay) + ":" + pad(minute));
                            setHour(calendarEnd, hourOfDay);
                            setMinute(calendarEnd, minute);
                        }

                        //if end is smaller start
                        if (!isEndGreaterStart(hourOfDay, minute, getHour(calendarEnd), getMinute(calendarEnd))){
                            time.setEnd(hourOfDay, minute);
                            textEnd.setText(pad(hourOfDay) + ":" + pad(minute));
                            setHour(calendarEnd, hourOfDay);
                            setMinute(calendarEnd, minute);
                            showAlert();
                        }
                        time.writeToDB();
                    }
                }, getHour(calendarStart), getMinute(calendarStart), true);
                pickStart.show();
            }

        });


        //timeDialog for endtime
        textEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog pickStart = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (!isEndGreaterStart(getHour(calendarStart),getMinute(calendarStart),hourOfDay,minute)){
                            hourOfDay = getHour(calendarStart);
                            minute = getMinute(calendarStart);
                            showAlert();
                        }
                        time.setEnd(hourOfDay, minute);
                        time.writeToDB();
                        textEnd.setText(pad(hourOfDay) + ":" + pad(minute));
                        setHour(calendarEnd, hourOfDay);
                        setMinute(calendarEnd, minute);

                    }
                }, getHour(calendarEnd), getMinute(calendarEnd), true);
                pickStart.show();
            }

        });




        //weekday togglebuttons
        FlowLayout weekdayLayout = (FlowLayout) findViewById(R.id.time_weekdays);
        String [] weekdays = {getString(R.string.time_mon),getString(R.string.time_tue),getString(R.string.time_wed),getString(R.string.time_thu),getString(R.string.time_fri),
                getString(R.string.time_sat),getString(R.string.time_sun)};
        final int [] indWeekdays = {Calendar.MONDAY,Calendar.TUESDAY,Calendar.WEDNESDAY,Calendar.THURSDAY,Calendar.FRIDAY,Calendar.SATURDAY,Calendar.SUNDAY};

        ArrayList<Integer> activeDays = time.getDays();

        // Build button for each day.
        for (int index = 0; index < 7; index++) {
            Log.d("Time", "add button" + String.valueOf(index + 1));
            final ToggleButton button = new ToggleButton(this);
            button.setText(weekdays[index]);
            button.setTextOn(weekdays[index]);
            button.setTextOff(weekdays[index]);
            button.setScaleX((float) 0.8);
            button.setScaleY((float) 0.8);
            weekdayLayout.addView(button);
            //setActive
            if (activeDays.contains(new Integer(indWeekdays[index]))){
                button.setChecked(true);
            }

            //onClickHandler
            final int finalIndex = index;
            button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        time.addDay(indWeekdays[finalIndex]);
                    }
                    else {
                        time.removeDay(indWeekdays[finalIndex]);
                    }
                    time.writeToDB();
                }
            });
        }
        Log.d("Time", "Number of buttons: " + String.valueOf(weekdayLayout.getChildCount()));


    }
    private void showAlert(){
        new AlertDialog.Builder(this)
                .setTitle(this.getString(R.string.alert_title))
                .setMessage(this.getString(R.string.alert_start_greater_end))
                .setPositiveButton(R.string.ok,null)
                .show();
    }
    private boolean isEndGreaterStart(int startHour,int startMinute,int endHour, int endMinute){
        if (endHour>startHour){
            return true;
        }
        else if (endHour == startHour && endMinute>=startMinute){
            return true;
        }
        else {
            return false;
        }
    }
    private void setHour(Calendar c,int hour){
        c.set(Calendar.HOUR_OF_DAY,hour);
    }
    private void setMinute(Calendar c, int min){
        c.set(Calendar.MINUTE,min);
    }
    private int getMinute(Calendar c){
        return c.get(Calendar.MINUTE);
    }
    private int getHour(Calendar c){
        return c.get(Calendar.HOUR_OF_DAY);
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
