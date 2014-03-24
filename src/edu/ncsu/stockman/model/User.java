package edu.ncsu.stockman.model;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;

import edu.ncsu.stockman.R;
import edu.ncsu.stockman.Timeline;
import android.content.Context;
import android.util.SparseArray;

public class User {

	public String email;
	public String name;
	public SparseArray<Game> games; 
	public int id;
	
	public long facebook_id;
	public enum User_status {ACTIVE,INACTIVE};
	public User_status user_status;
	public SparseArray<User> friends;
	public ArrayList<Friend> facebook_friends= new ArrayList<Friend>();
	public ArrayList<Notification> notifications;

	
	public User(JSONObject info) {
		super();
		try {
			this.email = info.getString("email");
			this.name = info.getString("name");
			this.id = info.getInt("id_user");
			this.facebook_id = info.getLong("facebook_id");
			int s = info.getInt("user_status");
			user_status = (s == 1 ? User_status.ACTIVE : User_status.INACTIVE);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.games = new SparseArray<Game>();
		this.friends = new SparseArray<User>();
		this.notifications = new ArrayList<Notification>();
	}
	
	public User(String email, String name, int id, long facebook_id) {
		super();
		this.email = email;
		this.name = name;
		this.facebook_id = facebook_id;
		this.id = id;
		
		this.games = new SparseArray<Game>();
		this.friends = new SparseArray<User>();
		this.notifications = new ArrayList<Notification>();
	}
	
	public void setFriends(JSONArray friends)
	{
		for(int i=0;i<friends.length();i++)
		{
			try{
				JSONObject friend = friends.getJSONObject(i);
				User u = new User(friend);
				this.friends.append(u.id, u);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setNotifications(JSONArray notifications){
		this.notifications = new ArrayList<Notification>();
		for (int i = 0; i < notifications.length(); i++) {
			try {
				JSONObject notif = notifications.getJSONObject(i);
				Notification g = new Notification(notif);
				this.notifications.add(g);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setFacebookFriends(JSONArray friends){
		this.facebook_friends = new ArrayList<Friend>();
		for (int i = 0; i < friends.length(); i++) {
			try {
				JSONObject friend = friends.getJSONObject(i);
				Friend f = new Friend(friend);
				this.facebook_friends.add(f);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setGames(JSONArray games){
		this.games = new SparseArray<Game>();
		for (int i = 0; i < games.length(); i++) {
			try {
				JSONObject game = games.getJSONObject(i);
				Game g = new Game(game);
				this.games.append(g.id, g);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Server side functions
	 */
	
	/**
	 * Fetch User's info, games, notifications, invitations
	 */
	public static void fetchUserInfo(Context c){
		JSONObject data = new JSONObject();
		try{		
			data.put("access_token", Session.getActiveSession().getAccessToken());//post
		}catch(JSONException e)
		{
			e.printStackTrace();
		}
		
		MidLayer asyncHttpPost = new MidLayer(data,c,Main.current_user==null?true:false) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				if(result.info.code == 0){
					
					try {
						JSONObject j = new JSONObject(result.info.text);
						User me = new User(j.optJSONObject("info"));
						Main.current_user = me;
						//System.out.println(me.toString());
						me.setGames(j.optJSONArray("games"));
						me.setNotifications(j.optJSONArray("notifications"));
						me.setFriends(j.optJSONArray("friends"));
						
						//Change the activity components
						// TODO maybe change this to an adaptor
						((Timeline)context).setName(Main.current_user.name);
						((Timeline)context).setGames();
						((Timeline)context).setNotifications();
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
			}
		};		
		asyncHttpPost.exec(c.getString(R.string.base_url)+"/user/get");
	}
}
