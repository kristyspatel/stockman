package edu.ncsu.stockman.model;

public class Guess {

	public Player me;
	public Player him;
	public boolean correct;
	public Guess(Player me, Player him, boolean correct) {
		super();
		this.me = me;
		this.him = him;
		this.correct = correct;
	}
	
	
}
