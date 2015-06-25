package org.dhbw.geo.ui.RuleFragments;

import android.support.v4.app.FragmentPagerAdapter;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Adapter for the Tabs
 * @author Joern
 */
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