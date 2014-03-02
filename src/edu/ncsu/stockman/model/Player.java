package edu.ncsu.stockman.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

public class Player{

	public int id;
	public User user;
	public Game game;
	public double cash;
	public String name;
	public enum Player_status {INVITED,WAITING_FOR_WORD,ENROLLED,OUT};
	public Player_status status;
	
	// TODO public enum status
	public HashMap<Company, Stock> stocks = new HashMap<Company, Stock>();
	public char[] word = new char[6]; 
	public ArrayList<Guess> guesses = new ArrayList<Guess>();
	public boolean[] word_revealed = new boolean[6]; // ______
	
	/**
	 * Constrator
	 * @param id : id of the player in db
	 * @param u: the user obj
	 * @param cash: current cash amount
	 * @param word: picked word
	 * @param g: game he is belonging to
	 */
	public Player(int id, User u, float cash, String word, Game g) {
		user = u;
		this.id = id;
		this.cash = cash;
		game = g;
		this.stocks = new HashMap<Company, Stock>();
		if (word.length()==6)
			this.word = word.toUpperCase(Locale.ENGLISH).toCharArray();
	}
	public Player(JSONObject info) {
		
		super();
		try {
			this.id = info.getInt("id_user");
			int s = info.getInt("player_status");
			this.word = info.getString("word").toCharArray();
			this.cash = info.getDouble("cash");
			this.name = info.getString("name");
			this.user = new User(info.getString("email"),info.getString("name"),info.getInt("id_user"),info.getLong("facebook_id"));
			if (s == 1)
				status = Player_status.INVITED;
			else if (s == 2)
				status = Player_status.WAITING_FOR_WORD;
			else if (s == 3)
				status = Player_status.ENROLLED;
			else if (s == 4)
				status = Player_status.OUT;
				
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
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
			int count_revealed = 0;
			char [] str = new char[Main.wordLength];
			
			p.guesses.add(new Guess(this,p,true,letter));
			for (int i = 0; i < p.word.length; i++) {
				if (p.word[i] == letter){
					p.word_revealed[i] = true;  
				}
				if (p.word_revealed[i])
					count_revealed++;
				str[i] = (!p.word_revealed[i] ? '_' : p.word[i] );
			}
			if (count_revealed == Main.wordLength)
				notifyOthers(p.user.name +" is dead. "+user.name+"'s just revealed his last letter. His word is:"+String.copyValueOf(p.word));
			else{
				notifyOthers(user.name +" guessed "+p.user.name+"'s word correclty.("+letter+" ) "+p.user.name+"'s word is now: "+String.copyValueOf(str));
			}
			return true;
		}
		else{
			p.guesses.add(new Guess(this,p,true,letter));
			notifyOthers(user.name +" guessed "+p.user.name+"'s word incorreclty.");
			p.cash -= Main.costOfGuess;
			return false;
		}
	}

//	public boolean buy(Company c, int amount){
//		if (c.price * amount < cash)
//			return false;
//		else {
//			Stock s = stocks.get(c);
//			if (s != null){
//				s.amount += amount;
//				cash -= amount * c.price;
//			}
//			else{
//				stocks.put(c,new Stock(c, amount));	
//			}
//			notifyOthers(user.name +" bought "+amount+" of "+c.name + " share(s).");
//			return true;
//		}
//	}
//	
//	public boolean sell(Company c, int amount){
//		Stock s = stocks.get(c);
//		if (s != null){
//			if (s.amount < amount)
//				return false;
//			s.amount -= amount;
//			cash += amount * c.price;
//			notifyOthers(user.name +" sold "+amount+" of "+c.name + " share(s).");
//			return true;
//		}
//		return false;
//	}
	
	public void notifyOthers(String noti){
		for (int i = 0; i < game.players.size(); i++) {
			Player p = game.players.valueAt(i);
			if (p != this) //notify only OTHERS
				p.user.notifications.add(new Notification(noti));
		}
	}
	
	//TODO USER:invite to a game
	//TODO USER:accept/reject invitation	
}
