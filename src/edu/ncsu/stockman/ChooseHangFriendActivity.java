package edu.ncsu.stockman;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class ChooseHangFriendActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_hang_friend);
		
		// dynamic adding of a layout
		LinearLayout main = (LinearLayout)findViewById(R.id.row1);
        View view = getLayoutInflater().inflate(R.layout.player, main,false);
        main.addView(view);
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


}
