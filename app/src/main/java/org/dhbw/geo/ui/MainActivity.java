package org.dhbw.geo.ui;

import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;


import org.dhbw.geo.database.DBActionSimple;
import org.dhbw.geo.database.DBActionSound;
import org.dhbw.geo.database.DBHelper;
import org.dhbw.geo.R;
import org.dhbw.geo.database.DBRule;
import org.dhbw.geo.hardware.HardwareController;
import org.dhbw.geo.hardware.NotificationFactory;
import org.dhbw.geo.hardware.SMSFactory;
import org.dhbw.geo.ui.Rule;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DB Stuff
        DBHelper db = new DBHelper(this);
        // test DBRule
        db.logDB();
        DBRule testRule = new DBRule();
        testRule.setName("Test Rule 123");
        testRule.setActive(true);
        testRule.writeToDB();                               // DBRule Create
        db.logTable(DBHelper.TABLE_RULE);
        testRule.setName("Test");
        testRule.setActive(false);
        testRule.writeToDB();                               // DBRule Update
        db.logTable(DBHelper.TABLE_RULE);
        DBRule sameRule = DBRule.selectFromDB(testRule.getId());    // DBRule Read
        if(sameRule.getId() == testRule.getId() && sameRule.getName() == sameRule.getName()) Log.d("DBRule Read", "Erfolgreich!");
        testRule.deleteFromDB();                            // DBRule Delete
        testRule = DBRule.selectFromDB(testRule.getId());
        if(testRule == null) Log.d("DBRule Delete", "Erfolgreich!");

        /*DBActionSimple actionWiFi = new DBActionSimple();
        actionWiFi.setType(DBActionSimple.TYPE_WIFI);
        actionWiFi.setStatus(true);
        testRule.addAction(actionWiFi);
        actionWiFi.writeToDB();
        DBActionSound actionSound = new DBActionSound();
        actionSound.setType(DBActionSound.TYPE_PHONE);
        actionSound.setStatus(DBActionSound.STATUS_SOUND);
        actionSound.setVolume(20);
        testRule.addAction(actionSound);
        actionSound.writeToDB();
        testRule.setName("Test Rule");
        testRule.writeToDB();                                               // UPDATE*/

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
        boolean currentStatus = HardwareController.getInstance().getStatus(AudioManager.STREAM_MUSIC);
        RadioButton currentStatusButton = (currentStatus == true) ?
                (RadioButton) this.findViewById(R.id.radioButtonSoundOn) :
                (RadioButton) this.findViewById(R.id.radioButtonSoundMute);
        currentStatusButton.setChecked(true);
    }
    public void onSoundStreamRing(View view) {
        // Set sound status radio group to correct music setting
        boolean currentStatus = HardwareController.getInstance().getStatus(AudioManager.STREAM_RING);
        RadioButton currentStatusButton = (currentStatus == true) ?
                (RadioButton) this.findViewById(R.id.radioButtonSoundOn) :
                (RadioButton) this.findViewById(R.id.radioButtonSoundMute);
        currentStatusButton.setChecked(true);
    }

    public void onNotificationClick(View view) {
        //Sends a test notification
        NotificationFactory.createNotification(this, "Test", "Hello world!");
    }

    public void onOngoingNotificationClick(View view) {
        //Sends a test permanent notification
        NotificationFactory.createOngoingNotification(this, "Test perm", "Hello world! Perm");
    }

    public void onTestSMSSend(View view) {
        //Sends a test SMS
        EditText inputNumber = (EditText) this.findViewById(R.id.editTextTestSMS);
        String number = inputNumber.getText().toString();
        SMSFactory.createSMS(number, "Hallo! Dies ist eine automatisch generierte test SMS von der App die wir gerade programmieren.");
    }

    public void onRuleClick (View view ){
        //Neues Intent anlegen
        Intent nextScreen = new Intent(getApplicationContext(), Rule.class);

        //Intent mit den Daten fuellen
       // nextScreen.putExtra("Vorname", inputVorname.getText().toString());
       // nextScreen.putExtra("Nachname", inputNachname.getText().toString());

        // Log schreiben fuer Logausgabe
        //Log.e("n", inputVorname.getText()+"."+ inputNachname.getText());

        // Intent starten und zur zweiten Activity wechseln
        startActivity(nextScreen);
    }
}
