package edu.ncsu.stockman.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Friend {

	public String name;
	public int id;
	
	public long facebook_id;
	public enum Friendship_status {PENDING,ACCEPTED,NOTINVITIED};
	public Friendship_status friendship_status;

	
	public Friend(JSONObject info) {
		super();
		try {
			this.name = info.getString("name");
			this.id = info.getInt("id_user");
			this.facebook_id = info.getLong("facebook_id");
			
			int s = info.optInt("status");
			if (s==0)// because it is null
				friendship_status = Friendship_status.NOTINVITIED;
			else if (s==1)
				friendship_status = Friendship_status.PENDING;
			else if (s==2)
				friendship_status = Friendship_status.ACCEPTED;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
