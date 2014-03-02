package edu.ncsu.stockman;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;

import edu.ncsu.stockman.model.Company;
import edu.ncsu.stockman.model.Game;
import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.MidLayer;
import edu.ncsu.stockman.model.Notification;
import edu.ncsu.stockman.model.Player;
import edu.ncsu.stockman.model.User;
import android.os.Bundle;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Timeline extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		
		
		/**
		 * Fetch User's info, games, notifications, invitations
		 */
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("access_token", Session.getActiveSession().getAccessToken());//post
		MidLayer asyncHttpPost = new MidLayer(data,this) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				if (result.error != null)
					System.out.println(result.error.text);
				if(result.info != null){
					if(result.info.code == 0){
						
						try {
							JSONObject j = new JSONObject(result.info.text);
							User me = new User(j.optJSONObject("info"));
							me.setGames(j.optJSONArray("games"));
							me.setNotifications(j.optJSONArray("notifications"));
							Main.current_user = me;
							
							//Change the activity components
							((Timeline)context).setName(Main.current_user.name);
							((Timeline)context).setGames();
							((Timeline)context).setNotifications();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		};		
		asyncHttpPost.execute(getString(R.string.base_url)+"/user/get");
		MidLayer asyncHttpPost2 = new MidLayer(data,this) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				if (result.error != null)
					System.out.println(result.error.text);
				if(result.info != null){
					if(result.info.code == 0){
						
						try {
							JSONArray j = new JSONArray(result.info.text);
							for (int i = 0; i < j.length(); i++) {
								Company c = new Company(j.getJSONObject(i));
								Main.companies.append(c.id, c);
							}
							System.out.println("companies"+Main.companies);
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		};		
		asyncHttpPost2.execute(getString(R.string.base_url)+"/stockmarket/get");

	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	
	public void setNotifications() {
		LinearLayout l = (LinearLayout) findViewById(R.id.notification_list);
		
		for(Notification n: Main.current_user.notifications){
			TextView t = new TextView(l.getContext());
			t.setText(n.text);
			t.setTextSize(15);
			t.setPadding(0,5,0,5);
			t.setTextColor(Color.parseColor("#ffffff"));
			l.addView(t,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		}
	}
	public void setName(String s) {
		TextView t = (TextView) findViewById(R.id.welcome);
		t.setText("Welcome:"+s);
	}
	public void setGames() {
		super.onStart();
		//TODO clean this mess
		LinearLayout main = (LinearLayout)findViewById(R.id.games_list);
		for (int i = 0; i < Main.current_user.games.size(); i++) {
			Game g = Main.current_user.games.valueAt(i);
			View v = getLayoutInflater().inflate(R.layout.game_in_timeline, main,false);
			Button b = (Button) v.findViewById(R.id.game_item);
			b.setTag(g);
			b.setText(g.name.charAt(0)+"");
			TextView t = (TextView) v.findViewById(R.id.game_desc);
			t.setText(g.name);
			main.addView(v);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_timeline, menu);
		return true;
	}
	
	public void create_new_game(View v){
		Intent intent = new Intent(this, ManageFriendsActivity.class);
		startActivity(intent);
	}

	public void open_game(View v){
		
		Main.current_game = (Game) v.getTag();
		
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("access_token", Session.getActiveSession().getAccessToken());//post
		MidLayer asyncHttpPost = new MidLayer(data,this) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				if (result.error != null)
					System.out.println(result.error.text);
				if(result.info != null){
					if(result.info.code == 10){
						
						try {
							JSONObject j = new JSONObject(result.info.text);
							Player me = new Player(j.optJSONObject("me"));
							Main.current_game.setPlayers(j.optJSONArray("players"));
							
							Main.current_player = me;
							
							System.out.println(Main.current_player);
							Intent intent = new Intent(context, MainGameActivity.class);
							startActivity(intent);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		};		
		asyncHttpPost.execute(getString(R.string.base_url)+"/game/get/"+Main.current_game.id);
		
	}
	public void logout(MenuItem c){
		SettingsActivity.logout(this);
	}
	public void goToSettings(MenuItem c){
		SettingsActivity.goToSettings(this);
	}

}
