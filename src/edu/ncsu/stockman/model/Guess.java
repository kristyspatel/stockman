package edu.ncsu.stockman.model;

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
	
	
}
