package edu.ncsu.stockman;



import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class New_Game extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_game);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_game, menu);
		return true;
	}

	public void create_game(View v){
		Intent intent = new Intent(this, PickWordActivity.class);
		startActivity(intent);
	}
	
	public void OnHomeMenuItem(MenuItem v){
		Intent intent = new Intent(this, Timeline.class);
		startActivity(intent);
	}
	
	public void onBackMenuItem(MenuItem v){
		Intent intent = new Intent(this, Timeline.class);
		startActivity(intent);
	}
	
}
