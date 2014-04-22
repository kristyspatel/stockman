package edu.ncsu.stockman.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;

import edu.ncsu.stockman.R;
import edu.ncsu.stockman.activity.MainGameActivity;
import edu.ncsu.stockman.activity.ShowLogs;

public class Player implements Comparable<Player>{

	public int id;
	public User user;
	public Game game;
	public double cash;
	public String name;
	public int die_date ;
	public enum Player_status {INVITED,WAITING_FOR_WORD,ENROLLED,LOST,WON,DECLINED};
	public Player_status status;
	
	public ArrayList<Activityy> logs = new ArrayList<Activityy>();
	public ArrayList<Stock> stocks = new ArrayList<Stock>();
	public char[] word = new char[6]; 
	public ArrayList<Guess> guesses = new ArrayList<Guess>();
	public boolean[] word_revealed = new boolean[6]; // ______
	
	//GCM
	public boolean new_letter_revealed=false;
	
	/**
	 * Constructor
	 * @param id : id of the player in db
	 * @param u: the user obj
	 * @param cash: current cash amount
	 * @param word: picked word
	 * @param g: game he is belonging to
	 */
	public Player(JSONObject info,Game g) {
		try {
			this.id = info.getInt("id_player");
			int s = info.getInt("player_status");
			
			this.die_date = info.getInt("die_date");
			if(!info.getString("word").equals(""))
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
			
			status = getStatus(s);
			
			this.game = g;
			this.word_revealed=  getRevealedWord(info.getInt("word_revealed"));
			
			if(info.has("stocks"))
				setStocks(info.getJSONArray("stocks"));
			else
				Log.i("StockMan","no stocks");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public static Player_status getStatus(int s){
		if (s == 1)
			return Player_status.INVITED;
		else if (s == 2)
			return Player_status.WAITING_FOR_WORD;
		else if (s == 3)
			return Player_status.ENROLLED;
		else if (s == 4)
			return Player_status.LOST;
		else if (s == 5)
			return Player_status.WON;
		else if (s == 6)
			return Player_status.DECLINED;

		return Player_status.INVITED;//default :/
	}

	public double getAssets(){
		double current_possissions = 0D;
		for (int i = 0; i < Main.current_game.me.stocks.size(); i++) {
			Stock s = stocks.get(i);
			current_possissions += s.company.getPrice() * s.amount;
		}
		return current_possissions;
	}
	public boolean[] getRevealedWord(int word_r){
		boolean[] word_revealed = new boolean[6];
		for (int i = word_revealed.length-1; i >= 0; i--) {
			if(word_r%2 == 0)
				word_revealed[i] = false;
			else
				word_revealed[i] = true;
			word_r /= 2;
		}
		return word_revealed;
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
				p.status = Player_status.LOST;
				p.game.player_status_change = true;
				if(p.game.isOver()){
					Main.current_user.new_game = true;
					p.status = Player_status.WON;
				}
				p.die_date = (int) System.currentTimeMillis() / 1000;
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
	public String hideWord(){
		String s = "";
		for (int i = 0; i < Main.wordLength; i++) {
			if(word_revealed[i])
				s += word[i]+" ";
			else
				s += "_ ";
		}
		return s;
	}
	public String hideWord(int wordi){
		boolean[] word_revealed = getRevealedWord(wordi);
		String s = "";
		for (int i = 0; i < Main.wordLength; i++) {
			if(word_revealed[i])
				s += word[i]+" ";
			else
				s += "_ ";
		}
		return s;
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
				e.printStackTrace();
			}
		}
	}
	
	public void setGuesses(JSONArray gusses){
		this.guesses = new ArrayList<Guess>();
		for (int i = 0; i < gusses.length(); i++) {
			try {
				JSONObject guess = gusses.getJSONObject(i);
				Guess s = new Guess(guess, this);
				this.guesses.add(s);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	public void setActivies(JSONArray logs){
		this.logs = new ArrayList<Activityy>();
		for (int i = 0; i < logs.length(); i++) {
			try {
				JSONObject log = logs.getJSONObject(i);
				Activityy s = new Activityy(log,this);
				this.logs.add(s);
			} catch (JSONException e) {
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
					//TODO maybe I need the player id to check
					Main.current_game.me.status = Player_status.ENROLLED;
					Main.current_game.player_status_change = true;
					Intent intent = new Intent(context, MainGameActivity.class);
					context.startActivity(intent);
				}
			}
		};		
		asyncHttpPost.exec(c.getString(R.string.base_url)+"/player/pick_word/"+Main.current_game.me.id);
	}
	public static void getLogs(Context c,int id_player){
		// grab player info from server
		JSONObject data = new JSONObject();
		try{		
			data.put("access_token", Session.getActiveSession().getAccessToken());//post
		}catch(JSONException e)
		{
			e.printStackTrace();
		}
		
		MidLayer asyncHttpPost = new MidLayer(data,c,true) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				if(result.info.code == 10){
					try {
						JSONArray j = new JSONArray(result.info.text);
						int id_player = bundle.getInt("id_player");
						Main.current_game.players.get(id_player).setActivies(j);
						((ShowLogs) context).setLogs();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		};		
		asyncHttpPost.bundle.putInt("id_player", id_player);
		asyncHttpPost.exec(c.getString(R.string.base_url)+"/player/list_logs/"+id_player);
	}
	
	@Deprecated
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
						
						if(Main.current_game.me==null){
							Log.e("StockManModel","Main.current_player is null while it should not");
							return;
						}
						
						me = Main.current_game.me;
						
						me.setStocks(j.getJSONArray("stocks"));
						((MainGameActivity) context).updateCashValues();
						
						me.setGuesses(j.optJSONArray("guesses"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		};		
		asyncHttpPost.exec(c.getString(R.string.base_url)+"/player/get/"+Main.current_game.me.id);
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
		if(new_status == Player_status.DECLINED)
			asyncHttpPost.exec(c.getString(R.string.base_url)+"user/decline_invitation/"+g.id);
		else if(new_status == Player_status.WAITING_FOR_WORD)
			asyncHttpPost.exec(c.getString(R.string.base_url)+"user/accept_invitation/"+g.id);
	}
	@Override
	public int compareTo(Player another) {
		if(another.die_date == 0)
			return 1;
		
		if(die_date == 0)
			return -1;
		
		if (this.die_date > another.die_date)
			return -1;
		else
			return 1;
	}
	
}
