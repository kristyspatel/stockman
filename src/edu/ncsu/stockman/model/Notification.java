package edu.ncsu.stockman.model;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import edu.ncsu.stockman.DownloadImageTask;
import edu.ncsu.stockman.R;
import edu.ncsu.stockman.model.Player.Player_status;

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
	public static void setPic(Context c, String type, JSONObject text, ImageView img){
		if(type.equals("GUESS")){
			try {
				Game g = Main.current_user.games.get(text.getInt("id_game"));
				Player me = g.players.get(text.getJSONObject("me").getInt("id_player"));
				DownloadImageTask.setFacebookImage(img, me.user);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(type.equals("BUY") || type.equals("SELL")){
			try {
				Game g = Main.current_user.games.get(text.getInt("id_game"));
				Player p = g.players.get(text.getInt("id_player"));
				DownloadImageTask.setFacebookImage(img, p.user);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(type.equals("ACCEPT") || type.equals("DECLINE")){
			try {
				Game g = Main.current_user.games.get(text.getInt("id_game"));
				Player p = g.players.get(text.getInt("id_player"));
				DownloadImageTask.setFacebookImage(img, p.user);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(type.equals("CREATE_GAME")){
			
			try {
				Game g = Main.current_user.games.get(text.getJSONObject("info").getInt("id_game"));
				if(Main.users.get(g.id_creator)!= null)
					DownloadImageTask.setFacebookImage(img, Main.users.get(g.id_creator));
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(type.equals("PICK_WORD")){
			
			try {
				Game g = Main.current_user.games.get(text.getInt("id_game"));
				Player p = g.players.get(text.getInt("id_player"));
				DownloadImageTask.setFacebookImage(img, p.user);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(type.equals("ACCEPT_FRIEND")){
			
			try {
				User u = Main.users.get(text.getInt("id_user2"));
				DownloadImageTask.setFacebookImage(img, u);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(type.equals("ADD_FRIEND")){
			
			try {
				Friend f = Main.current_user.facebook_friends.get(text.getInt("id_user1"));
				DownloadImageTask.setFacebookImage(img, f.user);				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		
	}
	public static String getText(Context c, String type, JSONObject text){
		if(type.equals("GUESS")){
			Game g;
			try {
				g = Main.current_user.games.get(text.getInt("id_game"));
				Player me = g.players.get(text.getJSONObject("me").getInt("id_player"));
				Player him = g.players.get(text.getJSONObject("him").getInt("id_player"));
				
				if(text.getBoolean("correct"))
					if(Player.getStatus(text.getJSONObject("him").getInt("status"))==Player_status.LOST)
						if(Player.getStatus(text.getJSONObject("me").getInt("status"))==Player_status.LOST)
							return c.getString(R.string.n_guess_won,
									him.user.id == Main.current_user.id ? "you":him.name,
								    me.user.id == Main.current_user.id ? "you":me.name,
									String.valueOf(him.word));
						else
							if(him.user.id != Main.current_user.id)
								return c.getString(R.string.n_guess_out,him.name,me.name,String.valueOf(him.word));
							else
								return c.getString(R.string.n_guess_out_me,
										"",
									    "",
										String.valueOf(him.word));
					else
						return c.getString(R.string.n_guess_correctly,
								him.user.id == Main.current_user.id ? "your":him.name+"'s",
							    me.user.id == Main.current_user.id ? "you":me.name,
								him.hideWord(text.getInt("word_revealed")));
				else
					return c.getString(R.string.n_guess_incorrectly,
							him.user.id == Main.current_user.id ? "your":him.name+"'s",
						    me.user.id == Main.current_user.id ? "you":me.name,
							him.hideWord(text.getInt("word_revealed")));
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(type.equals("BUY") || type.equals("SELL")){
			
			try {
				Game g = Main.current_user.games.get(text.getInt("id_game"));
				Player p = g.players.get(text.getInt("id_player"));
				Company company = Main.companies.get(text.getInt("id_company"));
				
				
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
					return c.getString(R.string.n_decline_game,p.name,g.name);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(type.equals("CREATE_GAME")){
			
			try {
				Game g = Main.current_user.games.get(text.getJSONObject("info").getInt("id_game"));
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
				if(f!=null)
					return c.getString(R.string.n_add_friend,f.name);
				else
					return c.getString(R.string.n_add_friend_no);
				
				
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
