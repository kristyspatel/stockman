package edu.ncsu.stockman;

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

public class ChooseHangFriendActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_hang_friend);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// dynamic adding of a layout
		// TODO clean this mess
		// TODO what if more than 3 players?
		LinearLayout main = (LinearLayout)findViewById(R.id.row1);
		for (int i = 0; i < Main.current_game.players.size(); i++) {
		   Player p = Main.current_game.players.get(Main.current_game.players.keyAt(i));
		   if (p.id != Main.current_player.id){
			   View v = getLayoutInflater().inflate(
					   R.layout.player, main,false);
			   v.findViewById(R.id.player_choose_button).setTag(p);
			   TextView t = (TextView) v.findViewById(R.id.player_choose_desc);
			   t.setText(p.user.name);
			   
			   main.addView(v);
		   }
		   if (i == 2)
			   main = (LinearLayout)findViewById(R.id.row2);
		   if (i == 5)
			   main = (LinearLayout)findViewById(R.id.row3);
		}
		
//		LinearLayout main = (LinearLayout)findViewById(R.id.row1);
//		main.addView(getLayoutInflater().inflate(R.layout.player, main,false));

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_hang_friend, menu);
		return true;
	}
	
	/** Called when the user clicks on a player*/
	public void goToSettings(MenuItem m) {
		SettingsActivity.goToSettings(this);
	}
	
	public void choosePlayer(View view2) {
	    // move to another activity
		// TODO carry the player id
		Intent intent = new Intent(this, HangFriendActivity.class);
		startActivity(intent);

		
	}
	public void logout(MenuItem c){
		SettingsActivity.logout(this);
	}


}
