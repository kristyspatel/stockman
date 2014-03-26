package edu.ncsu.stockman;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	LinkedHashMap<String,String> listDataHeader; 
    	ArrayList<String> listDataChild;
        setContentView(R.layout.activity_main);
        ExpandableListView expListView = (ExpandableListView) findViewById(R.id.shares);
        
        // preparing list data
        listDataHeader = new LinkedHashMap<String, String>();
        listDataHeader.put("Maroon", "0.05");
        listDataHeader.put("Blue", "0.5");
        listDataHeader.put("pink", "0.95");
        listDataHeader.put("yellow", "0.35");
        listDataChild = new ArrayList<String>();
        listDataChild.add("Maroon");
 
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //ExpandableListAdapter listAdapter = new ExpandableCardListAdapter(inflater, listDataHeader, listDataChild);
        // setting list adapter
        //expListView.setAdapter(listAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
