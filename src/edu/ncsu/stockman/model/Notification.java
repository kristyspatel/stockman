package edu.ncsu.stockman.model;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import edu.ncsu.stockman.R;
import edu.ncsu.stockman.model.Player.Player_status;
import android.content.Context;
import android.util.Log;

public class Notification {

	public String type;
	public JSONObject text;
	public boolean seen;
	public int id_notification;
	public Game game;
	public ArrayList<Comment> comments = new ArrayList<Comment>();
	
	//this to allow activity to update the comments
	public boolean new_comment = false;
	
	public Notification(JSONObject j) {
		super();
		Log.d("StockMan", j.toString());
		try {
			this.text = new JSONObject(j.optString("text"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.type = j.optString("type");
		this.seen = (j.optString("seen") == "1"?true : false);
		this.id_notification = j.optInt("id_notification");

		try {
			game = Main.current_user.games.get(j.getInt("id_game"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getText(Context c){
		return getText(c, type, text);
	}
	public static String getText(Context c, String type, JSONObject text){
		if(type.equals("GUESS")){
			Game g;
			try {
				g = Main.current_user.games.get(text.getInt("id_game"));
				Player me = g.players.get(text.getJSONObject("me").getInt("id_player"));
				Player him = g.players.get(text.getJSONObject("him").getInt("id_player"));
				
				if(text.getBoolean("correct"))
					if(Player.getStatus(text.getJSONObject("him").getInt("status"))==Player_status.OUT)
						return c.getString(R.string.n_guess_out,him.name,me.name,him.hideWord());
					else
						return c.getString(R.string.n_guess_correctly,him.name,me.name,him.hideWord());
				else
					return c.getString(R.string.n_guess_incorrectly,him.name,me.name,him.hideWord());
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(type.equals("BUY") || type.equals("SELL")){
			
			try {
				Game g = Main.current_user.games.get(text.getInt("id_game"));
				if(g==null)
					return "Game is null!";
				Player p = g.players.get(text.getInt("id_player"));
				if(p==null)
					return "Player is null!";
				
				Company company = Main.companies.get(text.getInt("id_company"));
				if(company==null)
					return "Company is null!";
				
				
				if(type.equals("BUY"))
					return c.getString(R.string.n_buy,p.name,text.getInt("amount"),company.name);
				else if(type.equals("SELL"))
					return c.getString(R.string.n_sell,p.name,text.getInt("amount"),company.name);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(type.equals("ACCEPT") || type.equals("DECLINE")){
			
			try {
				Game g = Main.current_user.games.get(text.getInt("id_game"));
				Player p = g.players.get(text.getInt("id_player"));
				
				if(type.equals("ACCEPT"))
					return c.getString(R.string.n_accept_game,p.name,g.name);
				else if(type.equals("DECLINE"))
					return c.getString(R.string.n_accept_game,p.name,g.name);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(type.equals("CREATE_GAME")){
			
			try {
				Game g = Main.current_user.games.get(text.getJSONObject("info").getInt("id_game"));
				Main.current_user.games.put(g.id, g);
				if(g.id_creator == Main.current_user.id)
					return c.getString(R.string.n_create_game_for_creator,g.name);
				else if(Main.users.get(g.id_creator)!= null)
					return c.getString(R.string.n_create_game_for_other,g.name,Main.users.get(g.id_creator).name);
				else
					return c.getString(R.string.n_create_game_for_other,g.name,text.get("creator_name"));
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(type.equals("PICK_WORD")){
			
			try {
				Game g = Main.current_user.games.get(text.getInt("id_game"));
				Player p = g.players.get(text.getInt("id_player"));
				return c.getString(R.string.n_pick_word,g.name,p.name);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(type.equals("COMMENT")){
			
			try {
				User u = Main.users.get(text.getInt("id_user"));
				return c.getString(R.string.n_comment,u.name);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(type.equals("ACCEPT_FRIEND")){
			
			try {
				User u = Main.users.get(text.getInt("id_user2"));
				
				return c.getString(R.string.n_accept_friend,u.name);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(type.equals("ADD_FRIEND")){
			
			try {
				Friend f = Main.current_user.facebook_friends.get(text.getInt("id_user1"));
				
				return c.getString(R.string.n_add_friend,f.name);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		
		return "Debug:"+type + ":"+text;
	}

	
	/**
	 * Serverside
	 */
//	
	
	//Later. not now.
	
//	public void setUpComments(Context c)
//	{
//		JSONObject data = new JSONObject();
//		try{		
//		data.put("access_token", Session.getActiveSession().getAccessToken());//post
//		}catch(JSONException e)
//		{
//			e.printStackTrace();
//		}
//		MidLayer asyncHttpPost = new MidLayer(data,c,true) {
//			@Override
//			protected void resultReady(MidLayer.Result result) {
//				if(result.info.code == 10){
//					
//					try {
//						JSONArray j = new JSONArray(result.info.text);
//						setComments(j);
//						setNotification();
//						setContent();
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				if(result.info.code == 1)
//				{
//					setNotification();
//					setNoComments();
//				}
//			}
//		};
//		asyncHttpPost.execute(getString(R.string.base_url)+"/user/get_comment/"+getIntent().getExtras().getInt("notification_id"));
//
//	}
}
