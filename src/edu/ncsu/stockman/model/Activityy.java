package edu.ncsu.stockman.model;

import org.json.JSONObject;

public class Activityy {

	public String text;
	public String subject;
	public long date;
	public Player p;
	public int id_activity;
	public String extra;
	
	public Activityy(String text) {
		super();
		this.text = text;
	}
	
	public Activityy(JSONObject j, Player p) {
		super();
		this.text = j.optString("text");
		this.subject = j.optString("subject");
		this.extra = j.optString("extra");
		this.date = j.optLong("date");
		this.id_activity = j.optInt("id_activity");
		this.p = p;
	}
	
	
}
