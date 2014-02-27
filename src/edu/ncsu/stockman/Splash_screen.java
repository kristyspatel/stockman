package edu.ncsu.stockman;

import java.util.Random;
import com.facebook.Session;
import edu.ncsu.stockman.model.User;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.View;

public class Splash_screen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		
		// check if user is already logged in
		if (Session.getActiveSession() != null && Session.getActiveSession().isOpened()){
			Intent intent = new Intent(this, Timeline.class);
			startActivity(intent);
		}
	}

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);

	
		if (Session.getActiveSession() != null && Session.getActiveSession().isOpened()){
			Intent intent = new Intent(this, Timeline.class);
			startActivity(intent);
		}
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		// TODO Auto-generated method stub
		return super.onCreateView(name, context, attrs);

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_splash_screen, menu);
		return true;
	}

	/**
	 * when facebook login button is pressed
	 */
	public void login(View v){
		Intent intent = new Intent(this, Timeline.class);
		startActivity(intent);
	}

}
