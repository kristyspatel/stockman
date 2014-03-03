package edu.ncsu.stockman;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import edu.ncsu.stockman.model.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

public class SellSharesFragment extends Fragment {
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	 
		 	
		 	
	        View rootView = inflater.inflate(R.layout.sell_shares_fragment, container, false);
	    	ExpandableListView expListView = (ExpandableListView) rootView.findViewById(R.id.shares);
	        
	 
	        ExpandableListAdapter listAdapter = new ExpandableCardListAdapter(inflater, Main.current_player.stocks);
	        // setting list adapter
	        expListView.setAdapter(listAdapter);
	        return rootView;
	        
	    }
}
