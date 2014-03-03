package edu.ncsu.stockman.model;

import org.json.JSONException;
import org.json.JSONObject;

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
	
	
	public Guess(JSONObject j) {
		super();
		try {
			this.id = j.getInt("id_guess");
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
