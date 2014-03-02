package edu.ncsu.stockman;

import edu.ncsu.stockman.model.Company;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ListAdapter;

public class BuySharesListAdapter extends ArrayAdapter<Company>{
	private final Context context;
	private final Company[] companies;

	public BuySharesListAdapter(Context context,Company[] companies) {
		
		super(context, R.layout.buy_share_listitem, companies);
		this.context = context;
		this.companies = companies;
	}
	
	
	public View getView(int position, View convertView, ViewGroup parent){
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.buy_share_listitem, parent, false);
		TextView cardName = (TextView) rowView.findViewById(R.id.cardName);
		cardName.setText(companies[position].name);
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
