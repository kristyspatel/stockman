package edu.ncsu.stockman.model;

import android.util.SparseArray;

public class Main {

	public static SparseArray<Game> games = new SparseArray<Game>();
	public static SparseArray<User> users = new SparseArray<User>();

	public static User current_user;
	public static Game current_game;
	public static Player current_player;
	
	public static int wordLength = 6;
	public static double costOfGuess = 50;
	
	static {
		//set uo users
		users.put(1,new User("a.osaimy@gmail.com", "Abdul", 1));
		users.put(2,new User("kpatel6@ncsu.edu", "Kristy", 2));
		users.put(3,new User("gati@ncsu.edu", "Gati", 3));
		users.put(4,new User("faalteraiqi@meridith.edu", "Fattemah", 4));
		
		
		//set up first game
		games.put(1, new Game(1, "Team"));
		Game team = games.get(1);
		team.players.put(1,new Player(1,users.get(1),500,"action"));
		team.players.put(2,new Player(2,users.get(2),500,"random"));
		team.players.put(3,new Player(3,users.get(3),500,"folder"));
		
		//Some Activities
		Player Abdul = team.players.get(1);
		Player Kristy = team.players.get(2);
		Player Gati = team.players.get(3);
		Abdul.guess(Gati, 'l');
		Abdul.guess(Kristy, 'l');
		Abdul.guess(Kristy, 'm');
		Kristy.guess(Gati, 'm');
		Abdul.buy(Company.list.get(1), 1);
		Abdul.buy(Company.list.get(2), 2);
		Abdul.buy(Company.list.get(3), 1);
		Kristy.buy(Company.list.get(2), 1);
		Abdul.sell(Company.list.get(3), 1);
		
		//set up second game
		games.put(1, new Game(1, "Family"));
		Game family = games.get(1);
		family.players.put(1,new Player(1,users.get(1),500,"sweety"));
		family.players.put(4,new Player(4,users.get(4),500,"bander"));
		
		
	}
}
