package edu.ncsu.stockman.model;

import java.util.Calendar;

import android.util.SparseArray;

public class Main{

	public static SparseArray<User> users = new SparseArray<User>();
	public static SparseArray<Company> companies = new SparseArray<Company>();
	
	public static User current_user;
	public static Game current_game;

	public static int day;
	
	public static int wordLength = 6;
	public static double costOfGuess = 50;
	
	//flags for GCM
	//public static boolean user_fetched = true;
	
	static {
		day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

	}
	

}
