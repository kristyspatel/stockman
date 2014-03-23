package edu.ncsu.stockman;

import java.util.Calendar;
import edu.ncsu.stockman.model.Company;
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
		
		System.out.println(Math.random());
		//Fetch companies and there prices (only if not fetched before or if the prices has changed)
		if(Main.companies.size()==0 || Main.day < Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
			Company.getPrices(this);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		//Fetch User's info, games, notifications, invitations
		User.fetchUserInfo(this);
	}
	
	public void setNotifications() {
		LinearLayout l = (LinearLayout) findViewById(R.id.notification_list);
		l.removeAllViews();
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
		
		LinearLayout main = (LinearLayout)findViewById(R.id.games_list);
		main.removeAllViews();
		
		// New Button
		View v = getLayoutInflater().inflate(R.layout.game_in_timeline, main,false);
		
		Button b = (Button) v.findViewById(R.id.game_item);
		b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getBaseContext(), New_Game.class);
				startActivity(intent);
				
			}
		});
		b.setText("New");
		
		main.addView(v);
		
		
		for (int i = 0; i < Main.current_user.games.size(); i++) {
			
			//get game from model
			Game g = Main.current_user.games.valueAt(i);
			
			//use the template
			v = getLayoutInflater().inflate(R.layout.game_in_timeline, main,false);
			
			//onClick
			//change the text of button and set tag
			b = (Button) v.findViewById(R.id.game_item);
			b.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					open_game(v);
				}
			});
			
			b.setTag(g);
			b.setText(g.name.charAt(0)+"");
			//change the textview text
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
	
	@Override
	public void onBackPressed() {
		System.out.println("onBack");
		setResult(1);
		super.onBackPressed();
	}

	public void open_game(View v){
		
		Main.current_game = (Game) v.getTag();
		
		//TODO use cache
		//server request, and if it's sucessful, it will start Main Activity
		Game.fetchGame(this);
		
	}
	public void logout(MenuItem c){
		SettingsActivity.logout(this);
	}

	public void goToSettings(MenuItem c){
		SettingsActivity.goToSettings(this);
	}
}
