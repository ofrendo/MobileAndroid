package org.dhbw.geo.ui.RuleFragments;

import java.util.Locale;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.dhbw.geo.R;
import org.dhbw.geo.database.DBRule;

/**
 * RuleContainer Activity which contains 3 slidable Tabs
 * @author Joern
 */
public class RuleContainer extends ActionBarActivity implements ActionBar.TabListener, RuleGeneral.OnFragmentInteractionListener, RuleCondition.OnFragmentInteractionListener,RuleAction.OnFragmentInteractionListener{


    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    String [] tabs;

    ActionBar actionBar;
    RuleAdapter mAdapter;
    DBRule rule;

    @Override
    public void onBackPressed() {
        startActivity(getParentActivityIntent());
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tabs = new String[3];
        tabs[0] = this.getString(R.string.rule_tab_general);
        tabs [1] = this.getString(R.string.rule_tab_condition);
        tabs[2] = this.getString(R.string.rule_tab_action);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_container);

        Intent i = getIntent();
        long id = i.getLongExtra("RuleID",-1);

        if (id != -1){
            rule = DBRule.selectFromDB(id);
        }
        else{
            rule = new DBRule();
            rule.setActive(true);
            rule.setName("");
            rule.writeToDB();

            //add extra for optional resizing
            i.putExtra("RuleID",rule.getId());
        }
        setTitle(rule.getName());






        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        actionBar = getSupportActionBar();

        //create Adapter
        mAdapter = new RuleAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mAdapter);
        //actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(android.app.ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }


        //add Swipe Listener
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });


        //set screen if specified
        int screen = i.getIntExtra("ScreenID",-1);
        if (screen == 1){
            mViewPager.setCurrentItem(screen);
        }

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



    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }




    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_rule_container, container, false);
            return rootView;
        }
    }

}
