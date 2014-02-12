package edu.ncsu.stockman.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Player{

	public int id;
	public User user;
	public double cash;
	public HashMap<Company, Stock> stocks = new HashMap<Company, Stock>();
	public char[] word; 
	public ArrayList<Guess> guesses;
	public boolean[] word_revealed; // ______
	
	public void setWord(String w){
		if (w.length() == 6)
			word = w.toCharArray();
	}

	public boolean wordContainLetter(char[] w, char l){
		for (int i = 0; i < w.length; i++) {
			if (w[i]==l)
				return true;
		}	
		return false;
	}
	public boolean guess(Player p, char letter){
		if(wordContainLetter(p.word,letter)){
			p.guesses.add(new Guess(this,p,true));
			for (int i = 0; i < word.length; i++) {
				if (word[i]==letter){
					p.word_revealed[i] = true;  
				}
			}
			return true;
		}
		else{
			p.guesses.add(new Guess(this,p,true));
			p.cash -= Main.costOfGuess;
			return false;
		}
	}
	
	public Player(int id, User u, float cash, String word) {
		user = u;
		this.id = id;
		this.cash = cash;
		this.stocks = new HashMap<Company, Stock>();
		if (word.length()==6)
			this.word = word.toCharArray();
		else
			this.word = new char[6];
		this.word_revealed = new boolean[6];
		this.guesses = new ArrayList<Guess>();
	}

	public boolean buy(Company c, int amount){
		if (c.price * amount < cash)
			return false;
		else {
			Stock s = stocks.get(c);
			if (s != null){
				s.amount += amount;
				cash -= amount * c.price;
			}
			else{
				stocks.put(c,new Stock(c, amount));	
			}
			return true;
		}
	}
	
	public boolean sell(Company c, int amount){
		Stock s = stocks.get(c);
		if (s != null){
			if (s.amount < amount)
				return false;
			s.amount -= amount;
			cash += amount * c.price;
		}
		return false;
	}
	
	

	
}
