package edu.ncsu.stockman.model;

import java.util.ArrayList;

import android.util.SparseArray;

public class User {

	public String email;
	public String name;
	public SparseArray<Game> games; 
	public int id;
	public SparseArray<User> friends;
	public ArrayList<String> notifications;
	
	public User(String email, String name, int id) {
		super();
		this.email = email;
		this.name = name;
		this.id = id;
		
		this.games = new SparseArray<Game>();
		this.friends = new SparseArray<User>();
		this.notifications = new ArrayList<String>();
	}
	
	public static boolean login(int id) // TODO change id to email and password
	{
		User u = Main.users.get(id);
		if (u == null)
			return false;
		Main.current_user = u;
		
		//add user's games
		for (int i = 0; i < Main.games.size(); i++) {
			Game g = Main.games.valueAt(i);
			if (g.players.get(u.id)!=null)
				u.games.put(g.id, g);
		}
		return true;
	}
}
