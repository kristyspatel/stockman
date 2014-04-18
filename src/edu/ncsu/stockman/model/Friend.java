package edu.ncsu.stockman.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Friend {

	public String name;
	public int id; // be careful, this id is not id_friend, as there is no such thing. It is id_user of the friend
	public User user;
	
	public long facebook_id;
	public enum Friendship_status {REQUEST_SENT,PENDING,ACCEPTED,NOTINVITIED,CANCELLED};
	public Friendship_status friendship_status;

	
	public Friend(JSONObject info) {
		super();
		try {
			this.name = info.getString("name");
			this.id = info.getInt("id_user");
			if(Main.users.get(id)==null){
				user= new User(info);
				Main.users.put(id, user);
			}
			else{
				user= Main.users.get(id);
			}
			
			this.facebook_id = info.getLong("facebook_id");
			
			int s = info.optInt("status");
			
			if (s==1)
				friendship_status = Friendship_status.REQUEST_SENT;
			else if (s==2)
				friendship_status = Friendship_status.ACCEPTED;
			else if (s==3)
				friendship_status = Friendship_status.PENDING;
			else if (s==4)
				friendship_status = Friendship_status.NOTINVITIED;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
