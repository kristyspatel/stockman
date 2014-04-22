package edu.ncsu.stockman.activity;

import java.util.PriorityQueue;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.Session;

import edu.ncsu.stockman.ChartsInterface;
import edu.ncsu.stockman.DownloadImageTask;
import edu.ncsu.stockman.R;
import edu.ncsu.stockman.RoundedImageView;
import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.Player;
import edu.ncsu.stockman.model.Player.Player_status;

public class MainGameActivity extends Activity {

	WebView webview;
	// Timer to update comments if new comment from server
	Handler timerHandler = new Handler();
	Runnable timerRunnable = new Runnable() {

		@Override
		public void run() {

			if(Main.current_game.player_status_change){
				showStanding();
				Main.current_game.player_status_change= false;
			}
			else if(Main.current_game.me.new_letter_revealed){
				showPlayerWord();
				Main.current_game.me.new_letter_revealed= false;
			}
			timerHandler.postDelayed(this, 1000);
		}
	};
	@Override
	public void onPause() {
		super.onPause();
		timerHandler.removeCallbacks(timerRunnable);
	}

	@Override
	public void onResume() {
		super.onResume();
		timerHandler.postDelayed(timerRunnable, 0);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(Main.current_game.me == null){
			System.err.println("MainGameActivity current_game.me is null");
			finish();
			return;
		}
		if(Main.current_game == null){
			System.err.println("MainGameActivity current_game is null");
			finish();
			return;
		}
		setContentView(R.layout.activity_main_game);
		
		setTitle(Main.current_game.name);

		if(Main.current_game.me.status==Player_status.LOST ||
				Main.current_game.me.status==Player_status.WON){
			LinearLayout v = (LinearLayout) findViewById(R.id.action_group);
			v.setVisibility(LinearLayout.GONE);
		}
		
		showPlayerWord();
		//fetch player info from server
		//Player.get(this);
	}
	
	private void showPlayerWord() {
		//set the player's word
		LinearLayout v = (LinearLayout) findViewById(R.id.main_myword);
		v.removeAllViews();
		for (int i = 0; i < Main.wordLength; i++) {
			TextView t = new TextView(this);
			t.setEnabled(false);
			t.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT,
					1));
			if(Main.current_game.me.word_revealed[i]){
				t.setTextAppearance(this, R.style.letter_revealed);
				t.setPaintFlags(t.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			}
			else{
				t.setTextAppearance(this, R.style.letter_not_revealed);
			}
			t.setText(Main.current_game.me.word[i]+"");
			v.addView(t);
		}
				
	}
	private void showStanding() {
		// sort players on the their die_date 
		PriorityQueue<Player> sorted = new PriorityQueue<Player>(Main.current_game.players.size());
		
		for (int i = 0; i < Main.current_game.players.size(); i++) {
			Player p = Main.current_game.players.valueAt(i);
			sorted.add(p);
		}
				//show players in the standing ssection
		LinearLayout main = (LinearLayout)findViewById(R.id.standing_list);
		main.removeAllViews();
		for (int i = 0; i < Main.current_game.players.size(); i++) {
			
			View view = getLayoutInflater().inflate(R.layout.player_in_standing, main,false);
			
			Player p = sorted.poll();
			
			RoundedImageView b = (RoundedImageView) view.findViewById(R.id.player_item);
			//DownloadImageTask.setFacebookImage(b, p.user);
			TextView t = (TextView) view.findViewById(R.id.player_desc);

			t.setText(p.user.name);
			b.setTag(p);
			b.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Player p = (Player) v.getTag();
					Intent intent = new Intent(MainGameActivity.this, ShowLogs.class);
					intent.putExtra("player_id", p.id);
					startActivity(intent);
				}
			});
			
			
			if(p.status == Player_status.LOST){
				DownloadImageTask.setFacebookImage(b, p.user);
				RoundedImageView crossing = (RoundedImageView) view.findViewById(R.id.forcrossing);
				crossing.setVisibility(View.VISIBLE);
				crossing.setImageResource(R.drawable.crossing_image);
			}
			else if(p.status == Player_status.WON){
				DownloadImageTask.setFacebookImage(b, p.user);
				RoundedImageView crossing = (RoundedImageView) view.findViewById(R.id.forcrossing);
				crossing.setVisibility(View.VISIBLE);
				crossing.setImageResource(R.drawable.won);
			}
			else if(p.status == Player_status.INVITED){
				b.setImageResource(R.drawable.invited_standing);
			}
			else if(p.status == Player_status.WAITING_FOR_WORD){
				b.setImageResource(R.drawable.invited_standing);
			}
			else if(p.status == Player_status.ENROLLED){
				DownloadImageTask.setFacebookImage(b, p.user);
			}
			else if(p.status == Player_status.DECLINED){
				continue;
			}
			main.addView(view);
		}
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	public void updateCashValues(){
//		set the player's current cash and possessions.
		TextView t = (TextView) findViewById(R.id.remaining_cash);
		t.setText("Cash:\n $"+String.format("%.2f",Main.current_game.me.cash));
		
		//set player's possessions
		double current_possissions = Main.current_game.me.getAssets();
		t = (TextView) findViewById(R.id.remaining_stocks);
		t.setText("Assets:\n $"+String.format("%.2f",current_possissions));
		
		//set player's total
		t = (TextView) findViewById(R.id.cashText);
		t.setText("$"+String.format("%.2f",current_possissions+Main.current_game.me.cash));
		
		webview = (WebView)findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setPadding(0, 0, 0, 0);
		webview.setBackgroundColor(0x00000000);
		webview.addJavascriptInterface(new ChartsInterface(this,Main.current_game.me), "and_data");
		//priceFluctuationGraph.loadUrl("file:///android_asset/googlecharts.html");
		webview.loadUrl("file:///android_asset/chartsjs_cash.html");

	}
	@Override
	protected void onStart() {
		super.onStart();
		updateCashValues();
		showStanding();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_timeline, menu);
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
	public void manageFriends(MenuItem c){
		Intent intent = new Intent(getBaseContext(), ManageFriendsActivity.class);
		startActivity(intent);
	}

}
