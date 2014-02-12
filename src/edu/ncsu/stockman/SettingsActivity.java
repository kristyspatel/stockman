package edu.ncsu.stockman;

import edu.ncsu.stockman.model.Main;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

public class SettingsActivity extends Activity {

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
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	public static void goToSettings(Activity c){
		Intent intent = new Intent(c, SettingsActivity.class);
		c.startActivity(intent);
	}

	public void logout(MenuItem c){
		logout(this);
	}
	
	public static void logout(Activity c){
		Intent intent = new Intent(c, Splash_screen.class);
		c.startActivity(intent);
	}

}
