package edu.ncsu.stockman;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import edu.ncsu.stockman.model.Company;
import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.Stock;

public class BuySharesFragment extends Fragment {

	public View rootView;
	ListView cardList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.buy_shares_fragment, container,
				false);

		// convert the sparse array to array
		Company[] companies = new Company[Main.companies.size()];
		for (int i = 0; i < Main.companies.size(); i++) {
			companies[i] = Main.companies.valueAt(i);
		}

		// set the text of buy
		TextView t_v = (TextView) rootView.findViewById(R.id.yourBalance);
		t_v.setText("Your Balance is " + Main.current_game.me.cash);

		// set the adapter
		cardList = (ListView) rootView.findViewById(R.id.cardList);
		cardList.setAdapter(new BuySharesListAdapter(getActivity()
				.getApplicationContext(), companies));

		// set the timer to refresh the stock prices
		timerHandler.postDelayed(timerRunnable, 0);

		return rootView;
	}

	// Timer to change the prices

	Handler timerHandler = new Handler();
	Runnable timerRunnable = new Runnable() {

		@Override
		public void run() {

			((BuySharesListAdapter) cardList.getAdapter())
					.notifyDataSetChanged();

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

	public void buyShares() {
		int selectedValue = 0;
		int amount = 0;
		
		

		// find the selected company from the radio list
		for (int i = 0; i < BuySharesListAdapter.radioList.size(); i++) {
			RadioButton current_radio = BuySharesListAdapter.radioList
					.get(i);
			if (current_radio.isChecked()) {
				selectedValue = i;
				break;
			}

		}
		Company selected_company = null;
		selected_company = (Company) BuySharesListAdapter.radioList.get(
				selectedValue).getTag();


		// get the amount of shares
		TextView tv = (TextView) rootView.findViewById(R.id.NoOfShares);
		try{
			amount = Integer.parseInt(tv.getText().toString());
			tv.setSelected(false);
	
			
			Stock.buyShares(getActivity(),selected_company,amount);
		} catch (NumberFormatException e){
			Toast.makeText(rootView.getContext(), "Amount must be nonempty, and integer.", Toast.LENGTH_SHORT).show();
		}
	}
}
