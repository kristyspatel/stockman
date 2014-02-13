package edu.ncsu.stockman.model;

import android.util.SparseArray;

public class Company {

	public float price;
	public String name;
	public String pic; // TODO
	public float accelerator; // TODO
	public float velocity; // TODO
	public int id;

	public static SparseArray<Company> list= new SparseArray<Company>();
	
	static{
		list.put(1,new Company(1, 100, "Black"));
		list.put(2,new Company(2, 112, "White"));
		list.put(3,new Company(3, 125, "Blue"));
	}

	public Company( int id, float price, String name) {
		super();
		this.price = price;
		this.name = name;
		this.id = id;
	}
	
}
