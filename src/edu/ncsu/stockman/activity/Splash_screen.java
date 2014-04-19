package edu.ncsu.stockman.activity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;

import edu.ncsu.stockman.R;

public class Splash_screen extends FacebookActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		TextView tagline = (TextView)findViewById(R.id.tagline);
		Typeface externalFont = Typeface.createFromAsset(getAssets(), "fonts/Comic Sans.ttf");
		tagline.setTypeface(externalFont);
		try {
	        PackageInfo info =     getPackageManager().getPackageInfo("com.package.mypackage",     PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	            MessageDigest md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            String sign=Base64.encodeToString(md.digest(), Base64.DEFAULT);
	            Log.e("MY KEY HASH:", sign);
	            Toast.makeText(getApplicationContext(),sign,     Toast.LENGTH_LONG).show();
	        }
	} catch (NameNotFoundException e) {
	} catch (NoSuchAlgorithmException e) {
	}	
		// check if user is already logged in
		if (Session.getActiveSession() != null && Session.getActiveSession().isOpened()){
	    	Intent intent = new Intent(this, Timeline.class);
			startActivityForResult(intent, 101);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nomenu, menu);
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
