package edu.ncsu.stockman.model;

import android.util.SparseArray;

public class Game {

	public String name;
	public int id;
	public Player creator;
	public SparseArray<Player> players = new SparseArray<Player>();;
	
	public Game(int id, String name) {
		super();
		this.name = name;
		this.id = id;
	}
	static boolean selectGame(int id)
	{
		Game g = Main.current_user.games.get(id);
		if (g == null)
			return false;
		Main.current_game = g;
		Main.current_player = g.players.get(Main.current_user.id);
		return true;
	}
}
