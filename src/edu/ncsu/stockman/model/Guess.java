package edu.ncsu.stockman.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Guess {

	public int id;
	public Player me;
	public Player him;
	public boolean correct;
	public char letter;
	
	public Guess(Player me, Player him, boolean correct, char letter) {
		super();
		this.me = me;
		this.him = him;
		this.correct = correct;
		this.letter = letter;
	}
	
	
	public Guess(JSONObject j, Player me) {
		super();
		try {
		
			this.me = me;
			this.id = j.getInt("id_guess");
			this.him = me.game.players.get(j.getInt("him_player"));
			if(him == null)
				Log.d("StockMan",j.getInt("him_player")+" is not found");
			this.correct = j.getInt("correct") == 0 ? false: true;
			this.letter = j.getString("letter").charAt(0);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
