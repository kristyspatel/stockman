package edu.ncsu.stockman;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;

import edu.ncsu.stockman.model.Company;
import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.MidLayer;
import edu.ncsu.stockman.model.User;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
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
		rootView.findViewById(R.id.BuyShares).setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {

				int selectedValue=0;
				int company_id=0,amount=0,player_id=0,timeStamp=0;
				float price;
				double remaining_cash=0.0;
				JSONObject data = new JSONObject();
				try{	
				data.put("access_token",  Session.getActiveSession().getAccessToken());
				
				for (int i = 0; i < Main.companies.size(); i++) {
	        		View vi = cardList.getAdapter().getView(i, null, cardList);	        		
	        		RadioButton current_radio = (RadioButton) vi.findViewById(R.id.radio_shares);
	        		System.out.println(current_radio.isSelected());
	        		if(current_radio.isSelected())
	        		{
	        				selectedValue = i;
	        				System.out.println(i);
	        				break;
	        		}
	        		
				}
				System.out.println(Integer.toString(selectedValue));
				player_id = Main.current_player.id;
				company_id = Main.companies.valueAt(selectedValue).id;
				price = Main.companies.valueAt(selectedValue).getPrice();
				TextView tv = (TextView)rootView.findViewById(R.id.NoOfShares);
				amount = Integer.parseInt(tv.getText().toString());
				remaining_cash = Main.current_player.cash - amount*price; 
				
				
				timeStamp = Main.companies.valueAt(selectedValue).getTimeStamp();
				//data.put("company_id", Main.companies.valueAt(selectedValue).id);
				//data.put("price",Main.companies.valueAt(selectedValue).getPrice());
				//data.put("amount",rootView.findViewById(R.id.NoOfShares));				
    			System.out.println(data.toString());
				}catch(JSONException e){
					e.printStackTrace();
				}
				//System.out.println(data.toString());
				MidLayer asyncHttpPost = new MidLayer(data,getActivity()) {
					@Override
					protected void resultReady(MidLayer.Result result) {
						if (result.error != null)
							System.out.println(result.error.text);
						if(result.info != null){
							if(result.info.code == 0){
								
								try {
									System.out.println("Done dona done");
									TextView t_v = (TextView)rootView.findViewById(R.id.yourBalance);
									t_v.setText("Your Balance is" +Double.toString(bundle.getDouble("remaining_cash")));

								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
				};
				asyncHttpPost.bundle.putDouble("remaining_cash", remaining_cash);
				System.out.println(getString(R.string.base_url)+"/buy/"+player_id+"/"+company_id+"/"+amount+"/"+timeStamp);
				asyncHttpPost.execute(getString(R.string.base_url)+"buy/"+player_id+"/"+company_id+"/"+amount+"/"+timeStamp);
				
			
			
		}
		});
		
		timerHandler.postDelayed(timerRunnable, 0);
		
        return rootView;
    }
    //Timer to change the prices
	
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

        	for (int i = 0; i < Main.companies.size(); i++) {
        		View v = cardList.getAdapter().getView(i, null, cardList);
        		TextView currentPrice = (TextView) v.findViewById(R.id.currentPrice);
        		currentPrice.setText(Main.companies.valueAt(i).getPrice()+"");
        		TextView prevPrice = (TextView) v.findViewById(R.id.previousPrice);
        		prevPrice.setText(Main.companies.valueAt(i).getPrevPrice()+"");
			}
            
        	timerHandler.postDelayed(this, 1000*20);          
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
