package edu.ncsu.stockman.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;

import com.facebook.Session;

import edu.ncsu.stockman.R;
import edu.ncsu.stockman.model.Player.Player_status;

public class User {

	public String email;
	public String name;
	public SparseArray<Game> games;
	public Bitmap picture;
	public int id;
	
	public long facebook_id;
	public enum User_status {ACTIVE,INACTIVE};
	public User_status user_status;
	public SparseArray<User> friends;
	public SparseArray<Friend> facebook_friends= new SparseArray<Friend>();
	public SparseArray<Notification> notifications;

	//GCM
	public boolean new_friend =false;
	public boolean new_game =false;
	public boolean new_notification =false;
	
	
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
		this.notifications = new SparseArray<Notification>();
	}
	
	public User(String email, String name, int id, long facebook_id) {
		super();
		this.email = email;
		this.name = name;
		this.facebook_id = facebook_id;
		this.id = id;
		
		this.games = new SparseArray<Game>();
		this.friends = new SparseArray<User>();
		this.notifications = new SparseArray<Notification>();
	}
	
	public void setFriends(JSONArray friends)
	{
		for(int i=0;i<friends.length();i++)
		{
			try{
				JSONObject friend = friends.getJSONObject(i);
				User u = new User(friend);
				if(this.friends.get(u.id)==null){
					if(Main.users.get(u.id)==null){
						Main.users.put(u.id, u);
						this.friends.put(u.id, u);
					}
					else{
						User uu = Main.users.get(u.id);
						this.friends.put(uu.id,uu);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setNotifications(JSONArray notifications){
		this.notifications = new SparseArray<Notification>();
		for (int i = 0; i < notifications.length(); i++) {
			try {
				JSONObject notif = notifications.getJSONObject(i);
				Notification g = new Notification(notif);
				if(g.type.equals("CREATE_GAME"))
					this.notifications.put(g.id_notification, g);
				else if(g.game.me.status != Player_status.DECLINED && g.game.me.status != Player_status.INVITED)
					this.notifications.put(g.id_notification, g);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setFacebookFriends(JSONArray friends){
		this.facebook_friends = new SparseArray<Friend>();
		for (int i = 0; i < friends.length(); i++) {
			try {
				JSONObject friend = friends.getJSONObject(i);
				Friend f = new Friend(friend);
				this.facebook_friends.put(f.id,f);
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
				Game g = new Game(game.getJSONObject("info"));
				g.setPlayers(game.getJSONArray("players"));
				this.games.put(g.id, g);
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
	public static void fetchUserInfo(Context c, String google_id){
		JSONObject data = new JSONObject();
		if(Session.getActiveSession()==null)
			return;
		try{
			data.put("access_token", Session.getActiveSession().getAccessToken());//post
		}catch(JSONException e)
		{
			e.printStackTrace();
			return;
		}
		
		MidLayer asyncHttpPost = new MidLayer(data,c,Main.current_user==null?true:false) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				if(result.info.code == 0){
					
					try {
						JSONObject j = new JSONObject(result.info.text);
						User me = new User(j.optJSONObject("info"));
						Main.current_user = me;
						Main.users.put(me.id, me);
						//System.out.println(me.toString());
						me.setGames(j.optJSONArray("games"));
						me.setFriends(j.optJSONArray("friends"));
						me.setNotifications(j.optJSONArray("notifications"));
						if(!j.optJSONObject("info").getString("google_id").equals(bundle.getString("google_id").isEmpty()))
							registerGoogleId(context, bundle.getString("google_id"));
						//Change the activity components
						me.new_game = true;
						me.new_friend = true;
						me.new_notification=true;
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
			}
		};		
		asyncHttpPost.bundle.putString("google_id", google_id);
		asyncHttpPost.exec(c.getString(R.string.base_url)+"/user/get");
	}
	/**
	 * Register the user registeration id given by Google Cloud Service
	 */
	public static void registerGoogleId(Context c, String regId){
		JSONObject data = new JSONObject();
		try{		
			data.put("access_token", Session.getActiveSession().getAccessToken());//post
			data.put("registeration_id", regId);//post
		}catch(JSONException e)
		{
			e.printStackTrace();
		}
		
		MidLayer asyncHttpPost = new MidLayer(data,c,false) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				if(result.info.code == 0){
					Log.i("StockManServer", "The user registeration id is updated.");
				}
				else{
					Log.i("StockManServer", "Fail to update user's registeration id");
				}
			}
		};		
		asyncHttpPost.exec(c.getString(R.string.base_url)+"/user/updateGoogleId");
	}
	
	/**
	 * get facebook friends
	 */
	
	static public void getFriends(Context c){
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
				if(result.info.code == 0){
					
					try {
						JSONArray friends = new JSONArray(result.info.text);
						Main.current_user.setFacebookFriends(friends);
						Main.current_user.new_friend = true;
						//((ManageFriendsActivity)context).setFriendsView(Main.current_user.facebook_friends);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};		
		asyncHttpPost.exec(c.getString(R.string.base_url)+"/user/list_facebook_friends");
	}
}
