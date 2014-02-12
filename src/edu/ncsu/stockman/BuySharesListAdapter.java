package edu.ncsu.stockman;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BuySharesListAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] values;

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
