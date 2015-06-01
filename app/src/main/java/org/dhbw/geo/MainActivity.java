package org.dhbw.geo;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import org.dhbw.geo.hardware.HardwareController;


public class MainActivity extends ActionBarActivity {

    private HardwareController hardwareController;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DB Stuff
        DBHelper db = new DBHelper(this);
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
        hardwareController.getInstance().setWifi(false);
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
