package org.dhbw.geo.ui.RuleFragments;

/**
 * Created by Joern on 13.06.2015.
 */

import android.support.v4.app.FragmentPagerAdapter;


import org.dhbw.geo.ui.RuleFragments.RuleGeneral;
import org.dhbw.geo.ui.RuleFragments.RuleCondition;
//import info.androidhive.tabsswipe.GamesFragment;
//import info.androidhive.tabsswipe.MoviesFragment;
//import info.androidhive.tabsswipe.TopRatedFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class RuleAdapter extends FragmentPagerAdapter {

    public RuleAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new RuleGeneral();
            case 1:
                // Games fragment activity
                return new RuleCondition();
            case 2:
                // Movies fragment activity
                return new RuleAction();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

}