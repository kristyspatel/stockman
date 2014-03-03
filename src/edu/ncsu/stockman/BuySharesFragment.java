package edu.ncsu.stockman;

import java.text.DecimalFormat;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import com.facebook.Session;
import edu.ncsu.stockman.model.Company;
import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.MidLayer;
import edu.ncsu.stockman.model.Stock;
import edu.ncsu.stockman.model.User;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class BuySharesFragment extends Fragment {

	View rootView;
	ListView cardList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.buy_shares_fragment, container,
				false);
		cardList = (ListView) rootView.findViewById(R.id.cardList);
		Company[] companies = new Company[Main.companies.size()];
		for (int i = 0; i < Main.companies.size(); i++) {
			companies[i] = Main.companies.valueAt(i);
		}

		TextView t_v = (TextView) rootView
				.findViewById(R.id.yourBalance);
		t_v.setText("Your Balance is "+Main.current_player.cash);
		
		cardList.setAdapter(new BuySharesListAdapter(getActivity()
				.getApplicationContext(), companies));
		rootView.findViewById(R.id.BuyShares).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						int selectedValue = 0;
						int amount = 0, player_id = Main.current_player.id, timeStamp = 0;
						float price;
						double remaining_cash = 0.0;
						JSONObject data = new JSONObject();
						Company selected_company=null;
						try {
							data.put("access_token", Session.getActiveSession()
									.getAccessToken());

							for (int i = 0; i < BuySharesListAdapter.radioList
									.size(); i++) {
								RadioButton current_radio = BuySharesListAdapter.radioList
										.get(i);
								if (current_radio.isChecked()) {
									selectedValue = i;
									break;
								}

							}

							selected_company = (Company) BuySharesListAdapter.radioList
									.get(selectedValue).getTag();

							price = selected_company.getPrice();
							TextView tv = (TextView) rootView
									.findViewById(R.id.NoOfShares);
							amount = Integer.parseInt(tv.getText().toString());
							remaining_cash = Main.current_player.cash - amount
									* price;

							timeStamp = selected_company.getTimeStamp();
							
							tv.setSelected(false);

						} catch (JSONException e) {
							e.printStackTrace();
						}
						// System.out.println(data.toString());
						MidLayer asyncHttpPost = new MidLayer(data,
								getActivity()) {
							@Override
							protected void resultReady(MidLayer.Result result) {
								if (result.error != null){
									Toast toast = Toast.makeText(context, result.error.text, Toast.LENGTH_SHORT);
									toast.show();
									return;
								}
								if (result.info != null) {
									if (result.info.code == 0) {

										try {
											TextView t_v = (TextView) rootView
													.findViewById(R.id.yourBalance);
											DecimalFormat dc = new DecimalFormat("#.00");
											t_v.setText("Your Balance is "
													+ Double.valueOf(dc.format(bundle.getDouble("remaining_cash"))));
											TextView tv = (TextView) rootView
													.findViewById(R.id.NoOfShares);
											tv.setText("");
											
											Toast toast = Toast.makeText(context, "The stock has been bought", Toast.LENGTH_SHORT);
											toast.show();
											
											
											Main.current_player.stocks.add(new Stock(new JSONObject(result.info.text)));

										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								}
							}
						};
						asyncHttpPost.bundle.putDouble("remaining_cash",
								remaining_cash);
						System.out.println(getString(R.string.base_url)
								+ "stockmarket/buy/" + player_id + "/"
								+ selected_company.id + "/" + amount + "/"
								+ timeStamp);
						asyncHttpPost.execute(getString(R.string.base_url)
								+ "stockmarket/buy/" + player_id + "/"
								+ selected_company.id + "/" + amount + "/"
								+ timeStamp);
						
					}
				});

		timerHandler.postDelayed(timerRunnable, 0);

		return rootView;
	}

	// Timer to change the prices

	Handler timerHandler = new Handler();
	Runnable timerRunnable = new Runnable() {

		@Override
		public void run() {

			((BuySharesListAdapter)cardList.getAdapter()).notifyDataSetChanged();
			
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

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// resultsAdapter.setRssData(rssData);
		// setListAdapter(resultsAdapter);
	}

}
