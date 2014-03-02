package edu.ncsu.stockman;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.TimeZone;

import edu.ncsu.stockman.model.Main;

import android.os.Bundle;
import android.os.Handler;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;

public class StockMarketActivity extends FragmentActivity implements ActionBar.TabListener {

	 private ViewPager viewPager;
	 private BuySellTabsAdapter buySellTabAdapter;
	 private ActionBar actionBar;
	 private String[] tabs = { "Buy Cards", "Sell Cards" };
	 
     //Timer to change the prices
     Handler timerHandler = new Handler();
     Runnable timerRunnable = new Runnable() {

         @Override
         public void run() {
        	 Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        	 int h = c.get(Calendar.HOUR_OF_DAY);
        	 int m = c.get(Calendar.MINUTE);
        	 int s = c.get(Calendar.SECOND);
        	 setTitle(Main.companies.get(1).price[h*60*2+m*2+(s<30?0:1)]+"");

             //timerTextView.setText(String.format("%d:%02d", minutes, seconds));

             timerHandler.postDelayed(this, 500);
         }
     };
     
     @Override
     public void onPause() {
         super.onPause();
         timerHandler.removeCallbacks(timerRunnable);
     }
     @Override
     public void onResume() {
         super.onResume();
         timerHandler.postDelayed(timerRunnable, 0);
     }
     
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// run timers
		timerHandler.postDelayed(timerRunnable, 0);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stock_market);
		viewPager = (ViewPager) findViewById(R.id.buySellTabPager);
        actionBar = getActionBar();
        buySellTabAdapter = new BuySellTabsAdapter(getSupportFragmentManager());

        viewPager.setAdapter(buySellTabAdapter);
        actionBar.setHomeButtonEnabled(false);
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
            }
         
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
         
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        
        
        
      		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stock_market, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
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

	public void logout(MenuItem c){
		SettingsActivity.logout(this);
	}
}
