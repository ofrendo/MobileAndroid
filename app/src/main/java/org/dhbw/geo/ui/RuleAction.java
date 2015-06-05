package org.dhbw.geo.ui;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import org.dhbw.geo.ListView.*;
import org.dhbw.geo.R;

import java.util.ArrayList;


public class RuleAction extends Activity {
    // more efficient than HashMap for mapping integers to objects
    ArrayList<Group> groups = new ArrayList<Group>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_action);
        createData();
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.expandableListView);
        MyExpandableListAdapter adapter = new MyExpandableListAdapter(this,
                groups);
        listView.setAdapter(adapter);
    }

    public void createData() {
        /*for (int j = 0; j < 5; j++) {
            Group group = new Group("Test " + j);
            for (int i = 0; i < 5; i++) {
                group.children.add(new Child("child" + i, false));
            }
            groups.append(j, group);
        }*/

        Group group = new Group ("Wlan");
        group.add(new Child("Active", true));
        group.add(new Child("Status",true));

        Group group2 = new Group("Sound");
        group2.add(new Child("Active",true));

        groups.add(group);
        groups.add(group2);


    }


}


