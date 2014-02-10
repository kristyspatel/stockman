package edu.ncsu.stockman;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class ChooseHangFriendActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_hang_friend);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_hang_friend, menu);
		return true;
	}
	
	/** Called when the user clicks on a player*/
	public void choosePlayer(View view) {
	    // Do something in response to button
		Intent intent = new Intent(this, HangFriendActivity.class);
		startActivity(intent);
	}


}
