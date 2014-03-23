package edu.ncsu.stockman;

import java.text.DecimalFormat;

import com.facebook.Session;
import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.Player;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainGameActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(Main.current_game == null || Main.current_player == null){
			System.err.println("MainGameActivity current_player or current_game is null");
			finish();
			return;
		}
		setContentView(R.layout.activity_main_game);
		
		setTitle(Main.current_game.name);
		
		LinearLayout v = (LinearLayout) findViewById(R.id.main_myword);
		
		for (int i = 0; i < Main.wordLength; i++) {
			TextView t = new TextView(this);
			//t.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
			t.setEnabled(false);
			t.setPadding(40, 0, 40, 0);
			if(Main.current_player.word_revealed[i])
				t.setTextAppearance(this, R.style.letter_revealed);
			else
				t.setTextAppearance(this, R.style.letter_not_revealed);
			t.setText(Main.current_player.word[i]+"");
			v.addView(t);
		}
		
		
		//fetch player info from server
		Player.get(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		//set
		TextView cashText = (TextView) findViewById(R.id.cashText);
		DecimalFormat dc = new DecimalFormat("#.00");
		cashText.setText("$"+Double.valueOf(dc.format(Main.current_player.cash)));
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_game, menu);
		return true;
	}
	
	/** Called when the user clicks the HangFirends Image button*/
	public void hangFriends(View view) {
		// Do something in response to button
		Intent intent = new Intent(this, ChooseHangFriendActivity.class);
		startActivity(intent);		
	}
	
	public void stockMarket(View view) {
		Intent intent = new Intent(this, StockMarketActivity.class);
		startActivity(intent);
	}
	
	public void logout(MenuItem c){
		SettingsActivity.logout(this);
	}
	public void goToSettings(MenuItem c){
		SettingsActivity.goToSettings(this);
	}

}
