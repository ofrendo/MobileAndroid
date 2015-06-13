package org.dhbw.geo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.TabActivity;
import android.widget.TabHost;

import org.dhbw.geo.R;


public class Rule extends TabActivity implements TabHost.OnTabChangeListener{
    TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule);
        // Get TabHost Reference
        tabHost = getTabHost();

        // Set TabChangeListener called when tab changed
        tabHost.setOnTabChangedListener(this);

        TabHost.TabSpec spec;
        Intent intent;

        /************* TAB1 ************/
        // Create  Intents to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, RuleGeneral.class);
        spec = tabHost.newTabSpec("First").setIndicator(getString(R.string.RuleGeneralTitle))
                .setContent(intent);

        //Add intent to tab
        tabHost.addTab(spec);

        /************* TAB2 ************/
        intent = new Intent().setClass(this, RuleCondition.class);
        spec = tabHost.newTabSpec("Second").setIndicator(getString(R.string.RuleConditionTitle))
                .setContent(intent);
        tabHost.addTab(spec);

        /************* TAB3 ************/
        intent = new Intent().setClass(this, RuleAction.class);
        spec = tabHost.newTabSpec("Third").setIndicator(getString(R.string.RuleActionTitle))
                .setContent(intent);
        tabHost.addTab(spec);

        // Set Tab1 as Default tab and change image
        tabHost.getTabWidget().setCurrentTab(0);

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

    @Override
    public void onTabChanged(String tabId) {
        /************ Called when tab changed *************/


    }

}
