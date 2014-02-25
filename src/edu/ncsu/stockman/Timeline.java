package edu.ncsu.stockman;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import edu.ncsu.stockman.model.Game;
import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.Notification;
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
		addGames();
		addNotifications();
		
		// start Facebook Login
	  Session.openActiveSession(this, true, new Session.StatusCallback() {

	    // callback when session changes state
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	    	if (session.isOpened()) {
	    		// make request to the /me API
	    		Request.newMeRequest(session, new Request.GraphUserCallback() {

	    		  // callback after Graph API response with user object
	    		  @Override
	    		  public void onCompleted(GraphUser user, Response response) {
	    			  if (user != null) {
	    				  TextView welcome = (TextView) findViewById(R.id.welcome);
	    				  welcome.setText("Welcome: "+user.getName());
	    				  System.out.println(user);
	    				}
	    		  }
	    		}).executeAsync();
	    	}
	    }
	  });
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	
	public void addNotifications() {
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
	public void addGames() {
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
		//Intent intent = new Intent(this, New_Game.class);
		//startActivity(intent);
		User.test();
	}

	public void open_game(View v){
		Intent intent = new Intent(this, MainGameActivity.class);
		startActivity(intent);
		System.out.println(v.getTag());
		Main.current_game = (Game) v.getTag();
		Main.current_player = Main.current_game.players.get(Main.current_user.id);
	}
	public void logout(MenuItem c){
		SettingsActivity.logout(this);
	}
	public void goToSettings(MenuItem c){
		SettingsActivity.goToSettings(this);
	}

}
