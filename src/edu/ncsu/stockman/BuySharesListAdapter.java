package edu.ncsu.stockman;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import edu.ncsu.stockman.model.Company;

public class BuySharesListAdapter extends ArrayAdapter<Company>{
	private final Context context;
	private final Company[] companies;
	static ArrayList<RadioButton> radioList = new ArrayList<RadioButton>();

	public BuySharesListAdapter(Context context,Company[] companies) {
		
		super(context, R.layout.buy_share_listitem, companies);
		this.context = context;
		this.companies = companies;
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.buy_share_listitem, parent, false);
		
		RadioButton cardRadio = (RadioButton) rowView.findViewById(R.id.radio_shares);
		cardRadio.setTag(companies[position]);
		rowView.setTag(companies[position]);
		radioList.add(cardRadio);
		//cardRadio.set
		cardRadio.setClickable(false);
		rowView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//RadioButton r = (RadioButton) v;
				for(RadioButton r: radioList){
					if(  (((Company)r.getTag()).id)  != ((Company)v.getTag()).id)
						r.setChecked(false);
					else{
						r.setChecked(true);
					}
				}
			}
		});
		TextView cardName = (TextView) rowView.findViewById(R.id.cardName);
		cardName.setText(companies[position].abbr);
		
		//TODO set the company logo
		ImageView cardImage = (ImageView) rowView.findViewById(R.id.cardImage);
		cardImage.setImageResource(companies[position].getPictureResourceID());
		
		TextView previousPrice = (TextView) rowView.findViewById(R.id.previousPrice);
		previousPrice.setText(Float.toString(companies[position].getPrevPrice()));
		TextView currentPrice = (TextView) rowView.findViewById(R.id.currentPrice);
		currentPrice.setText(Float.toString(companies[position].getPrice()));
		
		ImageView priceChangeIndicator = (ImageView) rowView.findViewById(R.id.priceChangeIndicator);
		priceChangeIndicator.setImageResource(
				(companies[position].getPrice() > companies[position].getPrevPrice() ? R.drawable.up_arrow : R.drawable.down_arrow)
				);
		return rowView;
	}
	
	
	
}
