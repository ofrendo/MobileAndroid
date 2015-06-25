package org.dhbw.geo.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ListView;



import org.dhbw.geo.R;

import org.dhbw.geo.database.*;

import org.dhbw.geo.services.ConditionService;
import org.dhbw.geo.services.ContextManager;
import org.dhbw.geo.ui.RuleFragments.RuleContainer;


import java.util.ArrayList;


/**
 * Mainactivity and First Screen for the User
 * Contains a dynamic ListView with all existing rules
 * @author Joern
 */
public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";
    ArrayList<DBRule> listItems=new ArrayList<>();
    ArrayAdapter<DBRule> adapter;

    public static PendingIntent gPendingIntent = null;




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

        final Activity activity = this;
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(activity)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(activity.getString(R.string.alert_title))
                        .setMessage(activity.getString(R.string.alert_text) + " " + activity.getString(R.string.alert_rule) + ": " + listItems.get(position).getName() + "?")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //delete and notify listadapter
                                listItems.get(position).deleteFromDB();
                                listItems.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
                return true;
            }
        });

        // TODO: Delete comments after testing
        // start GoogleApiClient in Service
        Intent mConditionService = new Intent(this, ConditionService.class);
        mConditionService.setAction(ConditionService.STARTAPP);
        //start ConditionService as Pending Intent
        gPendingIntent = PendingIntent.getService(this, 0, mConditionService, PendingIntent.FLAG_UPDATE_CURRENT);
        // add PendingIntent
        mConditionService.putExtra("PendingIntent", gPendingIntent);
        startService(mConditionService);
        //initialize listitems
        listItems.addAll(DBRule.selectAllFromDB());

        //create adapter for listview
        adapter = new DBRuleAdapter(this,android.R.layout.simple_list_item_1,listItems);
        listView.setAdapter(adapter);

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


        // Intent starten und zur zweiten Activity wechseln
        startActivity(nextScreen);
    }
}
