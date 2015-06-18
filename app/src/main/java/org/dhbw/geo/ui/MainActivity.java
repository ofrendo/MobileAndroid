package org.dhbw.geo.ui;

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


import org.dhbw.geo.Map.Maps;
import org.dhbw.geo.R;
import org.dhbw.geo.backend.BackendController;
import org.dhbw.geo.database.*;
import org.dhbw.geo.hardware.HardwareController;
import org.dhbw.geo.hardware.NotificationFactory;
import org.dhbw.geo.hardware.SMSFactory;
import org.dhbw.geo.services.ConditionService;
import org.dhbw.geo.services.ContextManager;
import org.dhbw.geo.ui.RuleFragments.RuleContainer;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // register context
        ContextManager.setContext(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ERROR In Emulator !?


        // Set correct radio button for wifi status
        //HardwareController.getInstance().setContext(this);
        boolean wifiStatus = HardwareController.getInstance().isWifiEnabled();
        RadioButton startButton = (wifiStatus == true) ?
                (RadioButton) this.findViewById(R.id.radioButtonWifiEnable) :
                (RadioButton) this.findViewById(R.id.radioButtonWifiDisable);
        startButton.setChecked(true);

        Log.i(TAG, "Start wifi status is: " + wifiStatus);

        // Test an api call
        BackendController backendController = new BackendController();
        backendController.getAllFenceGroups();
        // start GoogleApiClient in Service
        Intent mConditionService = new Intent(this, ConditionService.class);
        startService(mConditionService);
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

        // test action start and stop
        /*DBRule rule = new DBRule();
        rule.setActive(true);
        rule.setName("Test rule");
        rule.writeToDB();
        DBConditionTime conditionTime = new DBConditionTime();
        Calendar now = Calendar.getInstance();
        conditionTime.addDay(now.get(Calendar.DAY_OF_WEEK));*/
        /*now.add(Calendar.MINUTE, 1);
        conditionTime.setStart(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));
        now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 2);
        conditionTime.setEnd(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));*/
        /*conditionTime.setStart(0, 0);
        conditionTime.setEnd(12, 0);
        rule.addCondition(conditionTime);
        conditionTime.writeToDB();
        DBActionSimple wifi = new DBActionSimple();
        wifi.setActive(true);
        wifi.setType(DBActionSimple.TYPE_WIFI);
        wifi.setStatus(true);
        rule.addAction(wifi);
        wifi.writeToDB();
        DBActionNotification notification = new DBActionNotification();
        notification.setActive(true);
        notification.setMessage("Let's rule!");
        rule.addAction(notification);
        notification.writeToDB();
        dbHelper.logDB();
        conditionTime.updateAlarm();*/

        DBRule fenceRule = new DBRule();
        fenceRule.setActive(true);
        fenceRule.setName("Test rule");
        fenceRule.writeToDB();
        DBConditionFence conditionFence = new DBConditionFence();
        conditionFence.setType(DBConditionFence.TYPE_ENTER);
        fenceRule.addCondition(conditionFence);
        conditionFence.writeToDB();
        DBFence fence = new DBFence();
        fence.setLatitude(49.474292);
        fence.setLongitude(8.534501);
        fence.setRadius(30);
        conditionFence.addFence(fence);
        fence.writeToDB();
        dbHelper.logDB();
        if(conditionFence.isConditionMet()) Log.d("Main", "im Fence!");

    }

}
