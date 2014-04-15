package edu.ncsu.stockman.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.facebook.Session;
import com.facebook.SessionState;

import edu.ncsu.stockman.R;

public class Splash_screen extends FacebookActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		
		// check if user is already logged in
		if (Session.getActiveSession() != null && Session.getActiveSession().isOpened()){
	    	Intent intent = new Intent(this, Timeline.class);
			startActivityForResult(intent, 101);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_splash_screen, menu);
		return true;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		
		if (requestCode == 101) {
			System.out.println(requestCode + ","+resultCode);
	        if (resultCode == 1) 
	           this.finish();
	     }
	}
	
	@Override
	void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
	    if (state.isOpened()) {
	    	Intent intent = new Intent(this, Timeline.class);
	    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	startActivityForResult(intent, 101);
	    }
	}

}
