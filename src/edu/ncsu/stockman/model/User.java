package edu.ncsu.stockman.model;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
	
	public User(String email, String name, int id) {
		super();
		this.email = email;
		this.name = name;
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
	public void setGames(JSONArray games){
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
}
