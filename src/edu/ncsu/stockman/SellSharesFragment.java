package edu.ncsu.stockman;

import java.util.ArrayList;
import java.util.LinkedHashMap;

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
	        LinkedHashMap<String,String> listDataHeader; 
	    	ArrayList<String> listDataChild;
	        ExpandableListView expListView = (ExpandableListView) rootView.findViewById(R.id.shares);
	        
	        // preparing list data
	        listDataHeader = new LinkedHashMap<String, String>();
	        listDataHeader.put("Maroon", "0.05");
	        listDataHeader.put("Blue", "0.5");
	        listDataHeader.put("pink", "-0.95");
	        listDataHeader.put("yellow", "-0.35");
	        listDataChild = new ArrayList<String>();
	        listDataChild.add("Maroon");
	 
	        ExpandableListAdapter listAdapter = new ExpandableCardListAdapter(inflater, listDataHeader, listDataChild);
	        // setting list adapter
	        expListView.setAdapter(listAdapter);
	        return rootView;
	        
	    }
}
