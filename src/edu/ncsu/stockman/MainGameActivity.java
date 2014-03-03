package edu.ncsu.stockman;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;

import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.MidLayer;
import edu.ncsu.stockman.model.Notification;
import edu.ncsu.stockman.model.Player;
import edu.ncsu.stockman.model.User;
import android.os.Bundle;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainGameActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_game);
		
		setTitle(Main.current_game.name);
		TextView cashText = (TextView) findViewById(R.id.cashText);
		cashText.setText("$"+Main.current_player.cash);
		
		//TODO clean, maybe one word with styling (html)
		((TextView) findViewById(R.id.main_game_letter0)).setText(Main.current_player.word[0]+"");
		((TextView) findViewById(R.id.main_game_letter1)).setText(Main.current_player.word[1]+"");
		((TextView) findViewById(R.id.main_game_letter2)).setText(Main.current_player.word[2]+"");
		((TextView) findViewById(R.id.main_game_letter3)).setText(Main.current_player.word[3]+"");
		((TextView) findViewById(R.id.main_game_letter4)).setText(Main.current_player.word[4]+"");
		((TextView) findViewById(R.id.main_game_letter5)).setText(Main.current_player.word[5]+"");
	
		
		// grab all player info from server
		JSONObject data = new JSONObject();
		try{		
		data.put("access_token", Session.getActiveSession().getAccessToken());//post
		}catch(JSONException e)
		{
			e.printStackTrace();
		}
		MidLayer asyncHttpPost = new MidLayer(data,this) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				if (result.error != null)
					System.out.println(result.error.text);
				if(result.info != null){
					if(result.info.code == 10){
						
						try {
							JSONObject j = new JSONObject(result.info.text);
							Player me = Main.current_player;
							//System.out.println(me.toString());
							me.setStocks(j.optJSONArray("stocks"));
							
							//TODO this guesses is not fully implemented
							//me.setGuesses(j.optJSONArray("gusses"));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		};		
		asyncHttpPost.execute(getString(R.string.base_url)+"/player/get/"+Main.current_player.id);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		TextView cashText = (TextView) findViewById(R.id.cashText);
		cashText.setText("$"+Main.current_player.cash);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_game, menu);
		return true;
	}
	
	/** Called when the user clicks the HangFirends Image button*/
	public void hangFriends(View view) {
	    // Do something in response to button
		Intent intent = new Intent(this, ChooseHangFriendActivity.class);
		startActivity(intent);		
	}
	
	public void stockMarket(View view) {
		Intent intent = new Intent(this, StockMarketActivity.class);
		startActivity(intent);
	}
	
	public void logout(MenuItem c){
		SettingsActivity.logout(this);
	}
	public void goToSettings(MenuItem c){
		SettingsActivity.goToSettings(this);
	}

}
