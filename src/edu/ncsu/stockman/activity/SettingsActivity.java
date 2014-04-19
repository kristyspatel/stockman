package edu.ncsu.stockman.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.facebook.Session;
import com.facebook.SessionState;

import edu.ncsu.stockman.R;
import edu.ncsu.stockman.model.Main;

public class SettingsActivity extends FacebookActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		EditText e = (EditText)findViewById(R.id.settings_field_name);
		e.setText(Main.current_user.name);

		e = (EditText)findViewById(R.id.settings_field_email);
		e.setText(Main.current_user.email);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nomenu, menu);
		return true;
	}
	public static void goToSettings(Activity c){
		Intent intent = new Intent(c, SettingsActivity.class);
		c.startActivity(intent);
	}
	public void goToManageFriends(View c){
		Intent intent = new Intent(this, ManageFriendsActivity.class);
		startActivity(intent);
	}

	public void logout(MenuItem c){
		logout(this);
	}
	
	public static void login(Activity c){
		Intent intent = new Intent(c, Splash_screen.class);
		c.startActivity(intent);
	}
	public static void logout(Activity c){
		Session.getActiveSession().closeAndClearTokenInformation();
		Intent intent = new Intent(c, Splash_screen.class);
		Main.current_user = null;
		Main.current_game = null;
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		c.startActivity(intent);
	}

	//facebook
	@Override
	public void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isClosed()) {
	    	Intent intent = new Intent(this, Splash_screen.class);
	    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	Main.current_user = null;
	    	Main.current_game = null;
	    	startActivity(intent);
	    }
	}
}
