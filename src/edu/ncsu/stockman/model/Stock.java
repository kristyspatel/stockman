package edu.ncsu.stockman.model;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.SparseArray;
import edu.ncsu.stockman.model.User.User_status;

public class Stock {

	public Company company;
	public int amount;
	public float price;
	public int id_stock;
	
	public Stock(Company company, int amount) {
		super();
		this.company = company;
		this.amount = amount;
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
}
