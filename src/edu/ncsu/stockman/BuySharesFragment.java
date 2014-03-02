package edu.ncsu.stockman;

import edu.ncsu.stockman.model.Company;
import edu.ncsu.stockman.model.Main;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class BuySharesFragment extends Fragment {

	View rootView;
	ListView cardList;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        rootView = inflater.inflate(R.layout.buy_shares_fragment, container, false);
        cardList = (ListView) rootView.findViewById(R.id.cardList);
        Company[] companies = new Company[Main.companies.size()];
        for (int i = 0; i < Main.companies.size(); i++) {
        	companies[i] = Main.companies.valueAt(i);
		}
        
		cardList.setAdapter(new BuySharesListAdapter(getActivity().getApplicationContext(),companies));
		
		timerHandler.postDelayed(timerRunnable, 0);
		
        return rootView;
    }
    //Timer to change the prices
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
        	System.out.println(cardList);
        	for (int i = 0; i < Main.companies.size(); i++) {
        		View v = cardList.getAdapter().getView(i, null, cardList);
//        		View v = cardList.getChildAt(i);
        		//System.out.println(v);
        		TextView currentPrice = (TextView) v.findViewById(R.id.currentPrice);
        		currentPrice.setText(Main.companies.valueAt(i).price+"");
			}
            
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        

        //resultsAdapter.setRssData(rssData);
        //setListAdapter(resultsAdapter);
    }
}
