package org.dhbw.geo.ui;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;


import org.dhbw.geo.Map.GeofenceTransistionsIntentService;
import org.dhbw.geo.Map.Maps;
import org.dhbw.geo.R;
import org.dhbw.geo.database.*;
import org.dhbw.geo.hardware.HardwareController;
import org.dhbw.geo.hardware.NotificationFactory;
import org.dhbw.geo.hardware.SMSFactory;
import org.dhbw.geo.ui.RuleFragments.RuleContainer;

import java.util.Calendar;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";
    private static Context context; // the context for the app; needed for various things

    public static Context getContext(){
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ERROR In Emulator !?


        // Set correct radio button for wifi status
        HardwareController.getInstance().setContext(this);
        boolean wifiStatus = HardwareController.getInstance().isWifiEnabled();
        RadioButton startButton = (wifiStatus == true) ?
                (RadioButton) this.findViewById(R.id.radioButtonWifiEnable) :
                (RadioButton) this.findViewById(R.id.radioButtonWifiDisable);
        startButton.setChecked(true);

        Log.i(TAG, "Start wifi status is: " + wifiStatus);

        //


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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onWifiEnable(View view) {
        HardwareController.getInstance().setWifi(true);
    }

    public void onWifiDisable(View view) {
        HardwareController.getInstance().setWifi(false);
    }

    public void onSoundStreamMusic(View view) {
        // Set sound status radio group to correct music setting
        boolean currentStatus = HardwareController.getInstance().getAudioStatus(AudioManager.STREAM_MUSIC);
        RadioButton currentStatusButton = (currentStatus == true) ?
                (RadioButton) this.findViewById(R.id.radioButtonSoundOn) :
                (RadioButton) this.findViewById(R.id.radioButtonSoundMute);
        currentStatusButton.setChecked(true);
    }
    public void onSoundStreamRing(View view) {
        // Set sound status radio group to correct music setting
        boolean currentStatus = HardwareController.getInstance().getAudioStatus(AudioManager.STREAM_RING);
        RadioButton currentStatusButton = (currentStatus == true) ?
                (RadioButton) this.findViewById(R.id.radioButtonSoundOn) :
                (RadioButton) this.findViewById(R.id.radioButtonSoundMute);
        currentStatusButton.setChecked(true);
    }

    public void onNotificationClick(View view) {
        //Sends a test notification
        NotificationFactory.createNotification(this, "Test", "Hello world!", false);
    }

    public void onOngoingNotificationClick(View view) {
        //Sends a test permanent notification
        NotificationFactory.createNotification(this, "Test perm", "Hello world! Perm", true);
    }

    public void onTestSMSSend(View view) {
        //Sends a test SMS
        EditText inputNumber = (EditText) this.findViewById(R.id.editTextTestSMS);
        String number = inputNumber.getText().toString();
        SMSFactory.createSMS(number, "Hallo! Dies ist eine automatisch generierte test SMS von der App die wir gerade programmieren.");
    }

    public void onRuleClick (View view ){
        //Neues Intent anlegen
        Intent nextScreen = new Intent(getApplicationContext(), RuleContainer.class);

        //Intent mit den Daten fuellen
       // nextScreen.putExtra("Vorname", inputVorname.getText().toString());
       // nextScreen.putExtra("Nachname", inputNachname.getText().toString());

        // Log schreiben fuer Logausgabe
        //Log.e("n", inputVorname.getText()+"."+ inputNachname.getText());

        // Intent starten und zur zweiten Activity wechseln
        startActivity(nextScreen);
    }

    public void onOpenMapClick(View view){
        //Neues Intent anlegen
        Intent nextScreen = new Intent(getApplicationContext(), Maps.class);
        // Intent starten und zur zweiten Activity wechseln
        startActivity(nextScreen);
    }

    public void testDatabaseStuff(View view){
        // delete old data
        DBHelper dbHelper = DBHelper.getInstance();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + DBHelper.TABLE_ACTION_SIMPLE);
        db.execSQL("DELETE FROM " + DBHelper.TABLE_ACTION_SOUND);
        db.execSQL("DELETE FROM " + DBHelper.TABLE_ACTION_BRIGHTNESS);
        db.execSQL("DELETE FROM " + DBHelper.TABLE_ACTION_NOTIFICATION);
        db.execSQL("DELETE FROM " + DBHelper.TABLE_ACTION_MESSAGE);
        db.execSQL("DELETE FROM " + DBHelper.TABLE_CONDITION_FENCE);
        db.execSQL("DELETE FROM " + DBHelper.TABLE_FENCE);
        db.execSQL("DELETE FROM " + DBHelper.TABLE_CONDITION_TIME);
        db.execSQL("DELETE FROM " + DBHelper.TABLE_DAY_STATUS);
        db.execSQL("DELETE FROM " + DBHelper.TABLE_RULE_CONDITION);
        db.execSQL("DELETE FROM " + DBHelper.TABLE_RULE);
        // test DBRule
        /*DBRule testRule = new DBRule();
        testRule.setName("Test Rule 123");
        testRule.setActive(true);
        testRule.writeToDB();                               // DBRule Create
        dbHelper.logTable(DBHelper.TABLE_RULE);
        testRule.setName("Test");
        testRule.setActive(false);
        testRule.writeToDB();                               // DBRule Update
        dbHelper.logTable(DBHelper.TABLE_RULE);
        DBRule sameRule = DBRule.selectFromDB(testRule.getId());    // DBRule Read
        if(sameRule.getId() == testRule.getId() && sameRule.getName() == sameRule.getName()) Log.d("DBRule Read", "Erfolgreich!");
        testRule.deleteFromDB();                            // DBRule Delete
        testRule = DBRule.selectFromDB(testRule.getId());
        if(testRule == null) Log.d("DBRule Delete", "Erfolgreich!");

        // test conditiontime
        DBConditionTime time = new DBConditionTime();
        time.setStart(8, 30);
        time.setEnd(17, 30);
        time.setName("Working");
        time.addDay(Calendar.MONDAY);
        time.addDay(Calendar.TUESDAY);
        time.addDay(Calendar.THURSDAY);
        time.addDay(Calendar.WEDNESDAY);
        time.addDay(Calendar.FRIDAY);
        time.addDay(Calendar.SATURDAY);
        time.writeToDB(); // Insert
        time.removeDay(Calendar.SATURDAY); // Who wants to work on saturday anyway!?
        time.setName("Hard(ly) Working");
        time.writeToDB(); // update
        time = DBConditionTime.selectFromDB(time.getId());
        if(time.getStart().get(Calendar.MINUTE) == 30 && time.getStart().get(Calendar.HOUR_OF_DAY) == 8 && time.getEnd().get(Calendar.MINUTE) == 30 && time.getEnd().get(Calendar.HOUR_OF_DAY) == 17) Log.d("DBConditionTime Read 1", "Erfolgreich!");
        else Log.d("DBConditionTime Read 1", "Nicht erfolgreich! Start: " + time.getStart().get(Calendar.HOUR_OF_DAY) + ":" + time.getStart().get(Calendar.MINUTE) + ", Ende: " + time.getEnd().get(Calendar.HOUR_OF_DAY) + ":" + time.getEnd().get(Calendar.MINUTE));

        // test rule condition
        /*testRule = new DBRule();
        testRule.setName("Test Rule 1");
        testRule.setActive(true);
        testRule.writeToDB();
        testRule.addCondition(time);
        time.writeRuleToDB();
        DBRule rule2 = new DBRule();
        rule2.setName("Test Rule 2");
        rule2.writeToDB();
        rule2.addCondition(time);
        time.writeRuleToDB();
        DBConditionFence conditionFence = new DBConditionFence();
        conditionFence.setName("Test Condition Fence");
        conditionFence.setType(DBConditionFence.TYPE_ENTER);
        conditionFence.writeToDB();
        rule2.addCondition(conditionFence);
        conditionFence.writeRuleToDB();
        dbHelper.logDB();*/

        // test notification
        DBRule rule = new DBRule();
        rule.setName("Notification Rule");
        rule.setActive(true);
        rule.writeToDB();
        /*DBActionNotification notification = new DBActionNotification();
        notification.setMessage("Test Notification");
        rule.addAction(notification);
        notification.writeToDB();
        DBActionMessage msg = new DBActionMessage();
        msg.setNumber("01732541521");
        msg.setMessage("Hallo Matthias!");
        rule.addAction(msg);
        msg.writeToDB();
        /*DBActionSimple bt = new DBActionSimple();
        bt.setType(DBActionSimple.TYPE_BLUETOOTH);
        bt.setStatus(true);
        bt.setActive(true);
        rule.addAction(bt);
        bt.writeToDB();*/
        DBActionSound sound = new DBActionSound();
        sound.setActive(true);
        sound.setType(AudioManager.STREAM_MUSIC);
        sound.setStatus(DBActionSound.STATUS_SOUND);
        rule.addAction(sound);
        sound.writeToDB();
        rule.performAllActions();
    }

}
