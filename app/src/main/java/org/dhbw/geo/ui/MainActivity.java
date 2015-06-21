package org.dhbw.geo.ui;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;


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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    ArrayList<DBRule> listItems=new ArrayList<DBRule>();
    ArrayAdapter<DBRule> adapter;




    @Override
    public void onBackPressed() {
        finishAffinity();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // register context
        ContextManager.setContext(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = (ListView)findViewById(R.id.main_listview);
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                Intent nextScreen = new Intent(getApplicationContext(), RuleContainer.class);
                nextScreen.putExtra("RuleID", listItems.get(position).getId());
                startActivity(nextScreen);
            }
        });
        // TODO: Delete comments after testing
        /*// start GoogleApiClient in Service
        Intent mConditionService = new Intent(this, ConditionService.class);
        mConditionService.setAction(ConditionService.STARTAPP);
        //start ConditionService as Pending Intent
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, mConditionService, PendingIntent.FLAG_UPDATE_CURRENT);
        // add PendingIntent
        mConditionService.putExtra("pendingIntent", pendingIntent);
        startService(mConditionService);*/
        //initialize listitems
        listItems = DBRule.selectAllFromDB();

        Log.e(TAG,"items vorhanden: "+listItems.size());

        //create adapter for listview
        adapter = new DBRuleAdapter(this,android.R.layout.simple_list_item_1,listItems);
        listView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        listItems = DBRule.selectAllFromDB();
        adapter.notifyDataSetChanged();
    }

    public void onTestPage(View view){
        Intent nextScreen = new Intent(getApplicationContext(), TestActivity.class);
        startActivity(nextScreen);
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

    public void onNewRule (View view ){
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
}
