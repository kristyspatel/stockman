package edu.ncsu.stockman;


import java.util.Random;

import com.facebook.Session;

import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.User;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		EditText e = (EditText)findViewById(R.id.settings_field_name);
		e.setText(Main.current_user.name);

		e = (EditText)findViewById(R.id.settings_field_email);
		e.setText(Main.current_user.email);
		
		System.out.println(Session.getActiveSession().getAccessToken());
		
		if (Session.getActiveSession() == null || Session.getActiveSession().isClosed()){
			Intent intent = new Intent(this, Splash_screen.class);
			startActivity(intent);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);

	  // check if user is logged out
		if (Session.getActiveSession() == null || Session.getActiveSession().isClosed()){
			Intent intent = new Intent(this, Splash_screen.class);
			startActivity(intent);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
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
		Intent intent = new Intent(c, Splash_screen.class);
		c.startActivity(intent);
	}

}
