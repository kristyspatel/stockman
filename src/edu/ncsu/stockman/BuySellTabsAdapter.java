package edu.ncsu.stockman;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class BuySellTabsAdapter extends FragmentPagerAdapter {
 
	Fragment buy = new BuySharesFragment();
	Fragment sell = new SellSharesFragment();
    public BuySellTabsAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
 
        switch (index) {
        case 0:
            // Top Rated fragment activity
            return buy;
        case 1:
            // Games fragment activity
            return sell;
        }
 
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }
 
}
