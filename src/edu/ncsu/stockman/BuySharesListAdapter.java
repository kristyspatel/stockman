package edu.ncsu.stockman;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class BuySharesListAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] values;
	static ArrayList<RadioButton> radioList = new ArrayList<RadioButton>();
	 

	public BuySharesListAdapter(Context context,String[] values) {
		super(context, R.layout.buy_share_listitem, values);
		this.context = context;
		this.values = values;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.buy_share_listitem, parent, false);
		
		RadioButton cardRadio = (RadioButton) rowView.findViewById(R.id.radio_shares);
		cardRadio.setTag(position);
		radioList.add(cardRadio);
		//cardRadio.set
		cardRadio.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//RadioButton r = (RadioButton) v;
				for(RadioButton r: radioList){
					if(r.getTag() != v.getTag())
						r.setChecked(false);
					else{
						r.setChecked(true);
					}
					
				}
				
				
			}
		});
		TextView cardName = (TextView) rowView.findViewById(R.id.cardName);
		cardName.setText(values[position]);
		ImageView cardImage = (ImageView) rowView.findViewById(R.id.cardImage);
		cardImage.setImageResource(R.drawable.ic_launcher);
		TextView previousPrice = (TextView) rowView.findViewById(R.id.previousPrice);
		previousPrice.setText("3.80");
		TextView currentPrice = (TextView) rowView.findViewById(R.id.currentPrice);
		currentPrice.setText("3.82");
		ImageView priceChangeIndicator = (ImageView) rowView.findViewById(R.id.priceChangeIndicator);
		priceChangeIndicator.setImageResource(R.drawable.up_arrow);
		return rowView;
	}
	
	
	
}
