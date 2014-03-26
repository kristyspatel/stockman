package edu.ncsu.stockman.model;

import java.util.ArrayList;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.Session;

import edu.ncsu.stockman.MainGameActivity;
import edu.ncsu.stockman.R;

public class Player implements Comparable<Player>{

	public int id;
	public User user;
	public Game game;
	public double cash;
	public String name;
	public int die_date ;
	public enum Player_status {INVITED,WAITING_FOR_WORD,ENROLLED,OUT};
	public Player_status status;
	
	// TODO public enum status
	public ArrayList<Stock> stocks = new ArrayList<Stock>();
	public char[] word = new char[6]; 
	public ArrayList<Guess> guesses = new ArrayList<Guess>();
	public boolean[] word_revealed = new boolean[6]; // ______
	
	/**
	 * Constructor
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
		this.stocks = new ArrayList<Stock>();
		if (word.length()==6)
			this.word = word.toUpperCase(Locale.ENGLISH).toCharArray();
	}
	public Player(JSONObject info) {
		try {
			this.id = info.getInt("id_player");
			int s = info.getInt("player_status");
			
			this.die_date = info.getInt("die_date");
			this.word = info.getString("word").toCharArray();
			this.cash = info.getDouble("cash");
			
			if(Main.current_user != null && Main.current_user.id == info.getInt("id_user")){
				this.name = Main.current_user.name;
				this.user = Main.current_user;
			}
			else{
				this.name = info.getString("name");
				this.user = new User(info.getString("email"),info.getString("name"),info.getInt("id_user"),info.getLong("facebook_id"));
			}
			if (s == 1)
				status = Player_status.INVITED;
			else if (s == 2)
				status = Player_status.WAITING_FOR_WORD;
			else if (s == 3)
				status = Player_status.ENROLLED;
			else if (s == 4)
				status = Player_status.OUT;
			
			int word_r = info.getInt("word_revealed");
			for (int i = word_revealed.length-1; i >= 0; i--) {
				if(word_r%2 == 0)
					word_revealed[i] = false;
				else
					word_revealed[i] = true;
				word_r /= 2;
			}
			System.out.println(word_r);
			
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
			if (Character.toLowerCase(w[i])==Character.toLowerCase(l))
				return true;
		}	
		return false;
	}
	
	public boolean guess(Player p, char letter, Context c){
		TextView error = (TextView) ((Activity) c ).findViewById(R.id.error_line);
		
		if(wordContainLetter(p.word,letter)){
			int count_revealed = 0;
			char [] str = new char[Main.wordLength];
			
			guesses.add(new Guess(this,p,true,letter));
			for (int i = 0; i < p.word.length; i++) {
				if (Character.toLowerCase(p.word[i]) == Character.toLowerCase(letter)){
					p.word_revealed[i] = true;  
				}
				if (p.word_revealed[i])
					count_revealed++;
				str[i] = (!p.word_revealed[i] ? '_' : p.word[i] );
			}
			if (count_revealed == Main.wordLength){
				//notifyOthers(p.user.name +" is dead. "+user.name+"'s just revealed his last letter. His word is:"+String.copyValueOf(p.word));
				error.setText(p.user.name +" is dead. You just revealed his last letter. His word is:"+String.copyValueOf(p.word));
				error.setVisibility(View.VISIBLE);
			}
			else{
				//notifyOthers(user.name +" guessed "+p.user.name+"'s word correclty.("+letter+" ) "+p.user.name+"'s word is now: "+String.copyValueOf(str));
				error.setText("Correct guess.("+letter+" )");
				error.setVisibility(View.VISIBLE);
			}

			String s = "";
			for (int i = 0; i < Main.wordLength; i++) {
				if(p.word_revealed[i])
					s += p.word[i]+" ";
				else
					s += "_ ";
			}
			TextView t2 = (TextView) ((Activity) c ).findViewById(R.id.hang_player_word);
			t2.setText(s);
			guessWord(p, letter, c, true);
			return true;
		}
		else{
			guesses.add(new Guess(this,p,true,letter));
			//notifyOthers(user.name +" guessed "+p.user.name+"'s word incorreclty.");
			error.setText("Incorrect guess.("+letter+" )");
			error.setVisibility(View.VISIBLE);
			cash -= Main.costOfGuess;
			guessWord(p, letter, c, false);
			return false;
		}
	}
	
	public void setStocks(JSONArray stocks){
		this.stocks = new ArrayList<Stock>();
		for (int i = 0; i < stocks.length(); i++) {
			try {
				JSONObject stock = stocks.getJSONObject(i);
				Stock s = new Stock(stock);
				if(s.amount != 0)
					this.stocks.add(s);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setGuesses(JSONArray gusses){
		this.guesses = new ArrayList<Guess>();
		for (int i = 0; i < gusses.length(); i++) {
			try {
				JSONObject guess = gusses.getJSONObject(i);
				Guess s = new Guess(guess);
				this.guesses.add(s);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	public void notifyOthers(String noti){
		
//		for (int i = 0; i < game.players.size(); i++) {
//			Player p = game.players.valueAt(i);
//			if (p != this) //notify only OTHERS
//				p.user.notifications.add(new Notification(noti));
//		}
	}
	
	/**
	 * Server Side functions
	 * 
	 */
	public static void setWord(Context c, String word){
		// grab player info from server
		JSONObject data = new JSONObject();
		try{		
			data.put("access_token", Session.getActiveSession().getAccessToken());//post
			data.put("word", word);//post
		}catch(JSONException e)
		{
			e.printStackTrace();
		}
		
		MidLayer asyncHttpPost = new MidLayer(data,c,false) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				if(result.info.code == 0){
					Toast t = Toast.makeText(context, "Your word has been updated", Toast.LENGTH_LONG);
					t.show();
					
					Intent intent = new Intent(context, MainGameActivity.class);
					context.startActivity(intent);
				}
			}
		};		
		asyncHttpPost.exec(c.getString(R.string.base_url)+"/player/pick_word/"+Main.current_player.id);
	}
	public static void get(Context c){
		// grab player info from server
		JSONObject data = new JSONObject();
		try{		
		data.put("access_token", Session.getActiveSession().getAccessToken());//post
		}catch(JSONException e)
		{
			e.printStackTrace();
		}
		
		MidLayer asyncHttpPost = new MidLayer(data,c,false) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				if(result.info.code == 10){
					try {
						JSONObject j = new JSONObject(result.info.text);
						Player me;
						
						if(Main.current_player==null)
							me = new Player(j.getJSONObject("info"));
						else
							me = Main.current_player;
						
						me.setStocks(j.getJSONArray("stocks"));
						
						me.setGuesses(j.optJSONArray("guesses"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};		
		asyncHttpPost.exec(c.getString(R.string.base_url)+"/player/get/"+Main.current_player.id);
	}
	public static void guessWord(Player p, char letter, Context c, boolean isCorrect){
		// grab player info from server
		JSONObject data = new JSONObject();
		try{		
			data.put("access_token", Session.getActiveSession().getAccessToken());//post
			data.put("letter", Character.toLowerCase(letter)+"");
		}catch(JSONException e)
		{
			e.printStackTrace();
		}
		
		MidLayer asyncHttpPost = new MidLayer(data,c,false) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				boolean isCorrect = bundle.getBoolean("isCorrect");
				if(result.info.code == 10 && !isCorrect){
					System.err.println("The guess is incorrect but the server says it is correct");
				}
				if(result.info.code == 1 && isCorrect){
					System.err.println("The guess is correct but the server says it is incorrect"+result.info.text);
				}
			}
		};
		asyncHttpPost.bundle.putBoolean("isCorrect", isCorrect);
		asyncHttpPost.exec(c.getString(R.string.base_url)+"player/guess/"+p.id);
	}
	public static void changeStatus(Context c, Game g, Player_status new_status){

		// grab player info from server
		JSONObject data = new JSONObject();
		try{		
			data.put("access_token", Session.getActiveSession().getAccessToken());//post
		}catch(JSONException e)
		{
			e.printStackTrace();
		}
		
		MidLayer asyncHttpPost = new MidLayer(data,c,false) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				if(result.info.code == 1){
					System.err.println("The player status was not updated.");
				}
			}
		};
		if(new_status == Player_status.OUT)
			asyncHttpPost.exec(c.getString(R.string.base_url)+"user/decline_invitation/"+g.id);
		else if(new_status == Player_status.WAITING_FOR_WORD)
			asyncHttpPost.exec(c.getString(R.string.base_url)+"user/accept_invitation/"+g.id);
	}
	@Override
	public int compareTo(Player another) {
		if (this.die_date > another.die_date)
			return 1;
		else
			return -1;
	}
	
}
