package edu.ncsu.stockman.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Comment {

	User user;
	
	String comment;
	int id;
	Notification notification;
	
	public Comment(JSONObject comment)
	{
		try{
		this.user = new User(comment);
		this.notification = new Notification(comment);
		this.comment = comment.getString("ctext");
		this.id = comment.getInt("id_comment");
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}			
	}
	
	public Comment(User user, String comment, int id, Notification notification) {
		super();
		this.user = user;
		this.comment = comment;
		this.id = id;
		this.notification = notification;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User u) {
		this.user = u;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}

	
	
	
	
	
	
}
