package org.dhbw.geo.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import org.dhbw.geo.R;
import org.dhbw.geo.backend.BackendCallback;
import org.dhbw.geo.backend.BackendController;
import org.dhbw.geo.backend.JSONConverter;
import org.dhbw.geo.database.DBConditionFence;
import org.dhbw.geo.database.DBFence;
import org.dhbw.geo.database.DBHelper;
import org.dhbw.geo.database.DBRule;

import java.util.ArrayList;

/**
 * ImportActivity Class for importing LocationConditions from the server
 * @author Joern
 */
public class Import extends ActionBarActivity {
    ArrayList<DBConditionFence> groups = new ArrayList<>();
    ImportFilter adapter;
    Activity activity;
    long ruleID;

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
        setContentView(R.layout.activity_import);
        activity = this;

        Intent i = getIntent();
        //get ruleId
        ruleID = i.getLongExtra("DBRuleID",-1);

        //call backend
        BackendController controller = new BackendController(new BackendCallback() {
            public void actionPerformed(String result) {
                //do stuff on callback with result
                groups.addAll(JSONConverter.getFenceGroups(result));

               adapter.notifyDataSetChanged();

            }
        });
        controller.getAllFenceGroups();
        ///create ListAdapter
        adapter = new ImportFilter(activity,groups);
        ListView list = (ListView)findViewById(R.id.import_list);
        list.setAdapter(adapter);

        //handle inputFilter
        EditText search = (EditText)findViewById(R.id.import_search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //handle listview Event
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(activity)
                        .setTitle(getString(R.string.title_activity_import))
                        .setMessage(getString(R.string.import_dialog)+ ": "+adapter.getItem(position).getName()+"?")
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //download fences and add to db
                                final DBConditionFence conditionFence = adapter.getItem(position);
                                conditionFence.setRule(DBRule.selectFromDB(ruleID));
                                conditionFence.writeToDB();

                                //call backend for single fences in fencegroup
                                BackendController controller = new BackendController(new BackendCallback() {
                                    public void actionPerformed(String result) {
                                        //do stuff on callback with result
                                        ArrayList<DBFence> fences = JSONConverter.getFences(result);
                                        for (int i = 0; i < fences.size();i++){

                                            DBFence fence = fences.get(i);
                                            fence.setConditionFence(conditionFence);
                                            fence.writeToDB();
                                        }
                                    }
                                });
                                controller.getFencesForGroup(conditionFence.getServerId());


                                //Neues Intent anlegen
                                Intent nextScreen = getParentActivityIntent();
                                nextScreen.putExtra("RuleID", ruleID);
                                nextScreen.putExtra("ScreenID", 1);
                                // Intent starten und zur zweiten Activity wechseln
                                startActivity(nextScreen);
                            }
                        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {


                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_import, menu);
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
