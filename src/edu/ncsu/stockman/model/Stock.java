package edu.ncsu.stockman.model;

import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;

import edu.ncsu.stockman.R;
import edu.ncsu.stockman.activity.StockMarketActivity;

public class Stock {

	public Company company;
	public int amount;
	public float price;
	public int id_stock;
	
	public Stock(Company company, int amount, int id_stock, float price) {
		super();
		this.company = company;
		this.amount = amount;
		this.id_stock = id_stock;
		this.price = price;
	}
	
	public Stock(JSONObject j) {
		super();
		try {
			this.company = Main.companies.get(j.getInt("id_company"));
			this.amount = j.getInt("amount");
			this.id_stock = j.getInt("id_stock");
			this.price = (float) j.getDouble("price");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/***
	 * Server side
	 */
	
	public static void buyShares(Context c,Company company, int amount){

		double 	remaining_cash = Main.current_game.me.cash - amount * company.getPrice();
		//add the access token to the request
				JSONObject data = new JSONObject();
				try {
					data.put("access_token", Session.getActiveSession()
							.getAccessToken());
				} catch (JSONException e) {
					e.printStackTrace();
				}
		
		MidLayer asyncHttpPost = new MidLayer(data, c,true) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				if (result.info.code == 0) {
	
					try {
						//get the root view
						View rootView = ((StockMarketActivity) context).buySellTabAdapter.buy.rootView;
						
						//change the balance
						TextView t_v = (TextView) rootView.findViewById(R.id.yourBalance);
						Main.current_game.me.cash = Double.valueOf(String.format("%.2f",bundle
								.getDouble("remaining_cash")));
						t_v.setText("Your Balance is " + Main.current_game.me.cash);
						
						TextView tv = (TextView) rootView
								.findViewById(R.id.NoOfShares);
						tv.setText("");
	
						Toast toast = Toast.makeText(context,
								"The stock has been bought",
								Toast.LENGTH_SHORT);
						toast.show();
	
						Main.current_game.me.stocks.add(new Stock(
								new JSONObject(result.info.text)));
	
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		
		asyncHttpPost.bundle.putDouble("remaining_cash", remaining_cash);
		asyncHttpPost.exec(c.getString(R.string.base_url) + "stockmarket/buy/"
				+ Main.current_game.me.id + "/" + company.id + "/" + amount + "/"
				+ company.getTimeStamp());

	}
	
	public static void sellShares(Context c,Stock s, int amount){
		JSONObject data = new JSONObject();
		try{		
		data.put("access_token", Session.getActiveSession().getAccessToken());//post
		}catch(JSONException e)
		{
			e.printStackTrace();
		}
		double remaining_cash = Main.current_game.me.cash + 1
				* s.company.getPrice();
		MidLayer asyncHttpPost = new MidLayer(data,c,true) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				if(result.info.code == 0){
					Main.current_game.me.cash = Double.valueOf(String.format("%.2f",bundle.getDouble("remaining_cash")));
					
					Toast toast = Toast.makeText(context, "The stock has been sold. The remaining balance is: "+Main.current_game.me.cash
							, Toast.LENGTH_LONG);
					toast.show();
					//TODO
					int stock = -1;
					for (int i = 0; i < Main.current_game.me.stocks.size(); i++) {
						if (Main.current_game.me.stocks.get(i).id_stock == bundle.getInt("id_stock")){
							stock = i;
							break;
						}
					}
					
					Main.current_game.me.stocks.get(stock).amount -= 1;
					if (Main.current_game.me.stocks.get(stock).amount  == 0)
						Main.current_game.me.stocks.remove(stock);
					((StockMarketActivity) context).buySellTabAdapter.sell.listAdapter.notifyDataSetChanged();
				}
				
			}
		};
		asyncHttpPost.bundle.putDouble("remaining_cash",remaining_cash);
		asyncHttpPost.bundle.putInt("id_stock",s.id_stock);
		asyncHttpPost.exec(c.getString(R.string.base_url)+"stockmarket/sell/"+Main.current_game.me.id+"/"+s.id_stock+"/1/"+s.company.getTimeStamp());
		
	}
	
}
