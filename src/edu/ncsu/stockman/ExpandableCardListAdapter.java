package edu.ncsu.stockman;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;

import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.MidLayer;
import edu.ncsu.stockman.model.Player;
import edu.ncsu.stockman.model.Stock;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ExpandableCardListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private ArrayList<Stock> stocks;
	private LayoutInflater inflater;

	
	public ExpandableCardListAdapter(LayoutInflater inflater,ArrayList<Stock> shareHeader)
	{
		this.inflater = inflater;
		this.stocks = shareHeader;
	}
	
	public void setInflater(LayoutInflater inflater) {
		this.inflater = inflater;
	}
	
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stubs
		if(convertView == null)
			convertView = inflater.inflate(R.layout.card_details, null);
		ImageView priceFluctuationGraph = (ImageView)convertView.findViewById(R.id.priceFluctuationGraph);
		Button sellButton  = (Button)convertView.findViewById(R.id.sellButton);
		sellButton.setTag(stocks.get(groupPosition));
		sellButton.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Stock s = (Stock) v.getTag();
				
				JSONObject data = new JSONObject();
				try{		
				data.put("access_token", Session.getActiveSession().getAccessToken());//post
				}catch(JSONException e)
				{
					e.printStackTrace();
				}
				double remaining_cash = Main.current_player.cash + 1
						* s.company.getPrice();
				MidLayer asyncHttpPost = new MidLayer(data,v.getContext()) {
					@Override
					protected void resultReady(MidLayer.Result result) {
						if (result.error != null)
							System.out.println(result.error.text);
						if(result.info != null){
							if(result.info.code == 0){
								
									DecimalFormat dc = new DecimalFormat("#.00");
									Toast toast = Toast.makeText(context, "The stock has been sold. The remaining balance is: "+
											Double.valueOf(dc.format(bundle.getDouble("remaining_cash")))
											, Toast.LENGTH_SHORT);
									toast.show();
									//TODO
									int stock = -1;
									for (int i = 0; i < Main.current_player.stocks.size(); i++) {
										if (Main.current_player.stocks.get(i).id_stock == bundle.getInt("id_stock")){
											stock = i;
											break;
										}
									}
									
									Main.current_player.stocks.get(stock).amount -= 1;
									if (Main.current_player.stocks.get(stock).amount  == 0)
										Main.current_player.stocks.remove(stock);
									notifyDataSetChanged();
							}
						}
					}
				};
				asyncHttpPost.bundle.putDouble("remaining_cash",remaining_cash);
				asyncHttpPost.bundle.putInt("id_stock",s.id_stock);
				asyncHttpPost.execute(v.getContext().getString(R.string.base_url)+"stockmarket/sell/"+Main.current_player.id+"/"+s.id_stock+"/1/"+s.company.getTimeStamp());
				
			}
		});
		priceFluctuationGraph.setImageResource(R.drawable.graph);
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return stocks.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return stocks.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		if(convertView == null)
			convertView = inflater.inflate(R.layout.expandable_cards, null);
		
		TextView shareNameTextView = (TextView) convertView.findViewById(R.id.share_card);
		Stock stock = stocks.get(groupPosition);
		shareNameTextView.setText(stock.amount + " x "+stock.company.name);
		
		TextView priceChangeTextView = (TextView) convertView.findViewById(R.id.price_change);
		float change = (stock.company.getPrice()-stock.price) / stock.company.getPrice();
		
		DecimalFormat dc = new DecimalFormat("#.00");
		
		priceChangeTextView.setText( Double.valueOf(dc.format(change)) + "%");
		
		ImageView priceChangeIndicator = (ImageView)(convertView.findViewById(R.id.price_change_indicator));
		if(change > 0)
			priceChangeIndicator.setImageResource(R.drawable.up_arrow);
		else
			priceChangeIndicator.setImageResource(R.drawable.down_arrow);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}
	

}
