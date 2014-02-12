package edu.ncsu.stockman;

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
		String[] values = new String[] { "Maroon", "Blue", "green","yellow","violet","pink","red","purple"};
		cardList.setAdapter(new BuySharesListAdapter(getActivity().getApplicationContext(),values));
        return rootView;
    }
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        

        //resultsAdapter.setRssData(rssData);
        //setListAdapter(resultsAdapter);
    }
}
