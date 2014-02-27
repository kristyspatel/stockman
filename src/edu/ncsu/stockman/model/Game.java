package edu.ncsu.stockman.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.SparseArray;

public class Game {

	public String name;
	public int id;
	public Player creator;
	public SparseArray<Player> players = new SparseArray<Player>();;
	
	public Game(JSONObject info) {
		super();
		try {
			this.name = info.getString("name");
			this.id = info.getInt("id_game");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
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
	public void setPlayers(JSONArray players){
		for (int i = 0; i < players.length(); i++) {
			try {
				JSONObject player = players.getJSONObject(i);
				Player p = new Player(player);
				this.players.append(p.id, p);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
