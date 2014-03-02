package edu.ncsu.stockman.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.SparseArray;

public class Company {

	public float[] price = new float[60*24*2];
	public String name;
	public String abbr;
	public String pic; // TODO
	public int id;

	public static SparseArray<Company> list= new SparseArray<Company>();
	
//	static{
//		list.put(1,new Company(1,  "Black"));
//		list.put(2,new Company(2,  "White"));
//		list.put(3,new Company(3,  "Blue"));
//	}

	public Company( int id, String name) {
		super();
		this.name = name;
		this.id = id;
	}
	public Company( JSONObject j) {
		super();
		try {
			this.name = j.getString("company_name");
			this.id = j.getInt("id_company");
			this.abbr = j.getString("abbr");
			this.pic = j.getString("pic");
			JSONArray prices = j.getJSONArray("price");
			
			for (int i = 0; i < prices.length(); i++) {
				this.price[i] = (float) prices.getDouble(i);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
