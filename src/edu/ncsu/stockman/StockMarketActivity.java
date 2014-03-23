package edu.ncsu.stockman;

import android.os.Bundle;
import android.app.ActionBar.Tab;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class StockMarketActivity extends FragmentActivity implements ActionBar.TabListener {

	 private ViewPager viewPager;
	 public BuySellTabsAdapter buySellTabAdapter;
	 private ActionBar actionBar;
	 private String[] tabs = { "Buy Cards", "Sell Cards" };
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// run timers
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stock_market);
		viewPager = (ViewPager) findViewById(R.id.buySellTabPager);
        actionBar = getActionBar();
        buySellTabAdapter = new BuySellTabsAdapter(getSupportFragmentManager());

        viewPager.setAdapter(buySellTabAdapter);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);        
 
        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
        
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        	 
            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
                ((ExpandableCardListAdapter)((SellSharesFragment) ((BuySellTabsAdapter) viewPager.getAdapter()).sell).listAdapter).notifyDataSetChanged();
            }
         
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
         
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stock_market, menu);
		return true;
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
	}
	public void goToSettings(MenuItem c){
		SettingsActivity.goToSettings(this);
	}

	public void buyShares(View v) {
		buySellTabAdapter.buy.buyShares();
	}
	
	public void logout(MenuItem c){
		SettingsActivity.logout(this);
	}
	
}
