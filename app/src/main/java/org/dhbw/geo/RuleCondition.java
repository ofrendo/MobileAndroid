package org.dhbw.geo;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class RuleCondition extends ListActivity {
    ArrayList<String> listItems=new ArrayList<String>();


    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        listItems.add("hallo");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_condition);
        //set adapter for dynamical listview
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listItems);
        setListAdapter(adapter);


        //on click listener for listviewelements
        ListView listView = getListView();
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Intent nextScreen = new Intent(getApplicationContext(), Rule.class);
                startActivity(nextScreen);
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        //add items to listItems and notifyChange
        listItems.add("hallo2");
        adapter.notifyDataSetChanged();
    }

}
