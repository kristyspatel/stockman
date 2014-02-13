package edu.ncsu.stockman.model;

public class Stock {

	public Company company;
	public int amount;
	
	public Stock(Company company, int amount) {
		super();
		this.company = company;
		this.amount = amount;
	}
}
