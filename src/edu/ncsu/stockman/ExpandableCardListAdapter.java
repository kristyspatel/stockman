package edu.ncsu.stockman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

public class ExpandableCardListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private LinkedHashMap<String,String> shareHeader;
	private ArrayList<String> shareDetails;
	private LayoutInflater inflater;

	
	public ExpandableCardListAdapter(LayoutInflater inflater,LinkedHashMap<String,String> shareHeader,ArrayList<String> shareDetails)
	{
		this.inflater = inflater;
		this.shareHeader = shareHeader;
		this.shareDetails = shareDetails;
	}
	
	public void setInflater(LayoutInflater inflater) {
		this.inflater = inflater;
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return shareDetails.get(groupPosition);
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
		return (new ArrayList<String>(shareHeader.keySet())).get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return shareHeader.size();
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
		String shareName = (new ArrayList<String>(shareHeader.keySet())).get(groupPosition);
		shareNameTextView.setText(shareName);
		TextView priceChangeTextView = (TextView) convertView.findViewById(R.id.price_change);
		priceChangeTextView.setText(shareHeader.get(shareName) + "%");
		ImageView priceChangeIndicator = (ImageView)(convertView.findViewById(R.id.price_change_indicator));
		if(Double.parseDouble(shareHeader.get(shareName)) > 0)
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
	

}
