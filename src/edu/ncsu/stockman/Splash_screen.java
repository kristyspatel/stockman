package edu.ncsu.stockman;

import java.util.Random;

import edu.ncsu.stockman.model.User;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class Splash_screen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		// Show the Up button in the action bar.
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}

	/**
	 * when facebook login button is pressed
	 */
	public void login(View v){
		Intent intent = new Intent(this, Timeline.class);
		User.login(new Random().nextInt(3)+1);
		startActivity(intent);
	}

}
