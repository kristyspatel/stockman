package edu.ncsu.stockman.model;

import org.json.JSONObject;

public class Notification {

	public String text;
	public boolean seen;
	public int id_notification;
	
	public Notification(String text) {
		super();
		this.text = text;
		this.seen = false;
	}
	
	public Notification(JSONObject j) {
		super();
		this.text = j.optString("text");
		this.seen = (j.optString("seen") == "1"?true : false);
		this.id_notification = j.optInt("id_notification");
	}
	
	
}
