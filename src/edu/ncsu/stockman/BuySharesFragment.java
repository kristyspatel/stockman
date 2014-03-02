package edu.ncsu.stockman;

import edu.ncsu.stockman.model.Company;
import edu.ncsu.stockman.model.Main;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class BuySharesFragment extends Fragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.buy_shares_fragment, container, false);
        ListView cardList = (ListView) rootView.findViewById(R.id.cardList);
		//String[] values = new String[] { "Maroon", "Blue", "green","yellow","violet","pink","red","purple"};
        Company[] companies = new Company[Main.companies.size()];
        for (int i = 0; i < Main.companies.size(); i++) {
        	companies[i] = Main.companies.valueAt(i);
		}
        
		cardList.setAdapter(new BuySharesListAdapter(getActivity().getApplicationContext(),companies));
        return rootView;
    }
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        

        //resultsAdapter.setRssData(rssData);
        //setListAdapter(resultsAdapter);
    }
}
