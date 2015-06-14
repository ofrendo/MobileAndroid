package org.dhbw.geo.ui;

import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Spinner;


import org.dhbw.geo.Map.Maps;
import org.dhbw.geo.database.DBActionSimple;
import org.dhbw.geo.database.DBHelper;
import org.dhbw.geo.R;
import org.dhbw.geo.database.DBRule;
import org.dhbw.geo.hardware.HardwareController;
import org.dhbw.geo.ui.Rule;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DB Stuff
        DBHelper db = new DBHelper(this);
        // test rule
        DBRule testRule = new DBRule();
        testRule.setName("Test Rule 123");
        testRule.setActive(true);
        testRule.writeToDB();                                               // INSERT
        testRule.setName("Test Rule");
        testRule.writeToDB();                                               // UPDATE
        if(testRule.getId() > 1){
            DBRule testRule2 = DBRule.selectFromDB(testRule.getId() - 1);   // SELECT
            if(testRule2 != null){
                testRule2.deleteFromDB();                                   // DELETE
            }
        }
        // test action simple
        DBActionSimple actionSimple = new DBActionSimple();
        actionSimple.setType(DBActionSimple.TYPE_WIFI);
        actionSimple.setStatus(true);
        testRule.addAction(actionSimple);
        actionSimple.writeToDB();                                           // CREATE
        if(actionSimple.getId() > 1){
            DBActionSimple actionSimple1 = DBActionSimple.selectFromDB(actionSimple.getId() - 1);   // SELECT
            if(actionSimple1 != null){
                actionSimple1.setRule(testRule);
                actionSimple1.setType(DBActionSimple.TYPE_BLUETOOTH);
                actionSimple1.setStatus(false);
                actionSimple1.writeToDB();                                  // UPDATE
            }
            if(actionSimple.getId() > 2){
                DBActionSimple actionSimple2 = DBActionSimple.selectFromDB(actionSimple.getId() - 2);
                if(actionSimple2 != null){
                    actionSimple2.deleteFromDB();                           // DELETE
                }
            }
        }
        db.logDB();

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

    public void onOpenMapClick (View view){
        //Neues Intent anlegen
        Intent mapScreen = new Intent(getApplicationContext(), Maps.class);
        // Intent starten
        startActivity(mapScreen);
    }
}
