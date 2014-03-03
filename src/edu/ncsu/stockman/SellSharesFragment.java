package edu.ncsu.stockman;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.Stock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

public class SellSharesFragment extends Fragment {
	ExpandableListView expListView ;
	ExpandableCardListAdapter listAdapter;
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	 
		 	
		 	
	        View rootView = inflater.inflate(R.layout.sell_shares_fragment, container, false);
	    	expListView = (ExpandableListView) rootView.findViewById(R.id.shares);
	 
	    	listAdapter = new ExpandableCardListAdapter(inflater, Main.current_player.stocks);
	        // setting list adapter
	        expListView.setAdapter(listAdapter);
	        return rootView;
	        
	    }
	 
		Handler timerHandler = new Handler();
		Runnable timerRunnable = new Runnable() {

			@Override
			public void run() {

				((ExpandableCardListAdapter)listAdapter).notifyDataSetChanged();
				
				timerHandler.postDelayed(this, 1000 * 20);
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

}
