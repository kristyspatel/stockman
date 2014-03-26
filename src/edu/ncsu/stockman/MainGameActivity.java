package edu.ncsu.stockman;

import java.text.DecimalFormat;
import java.util.PriorityQueue;
import com.facebook.Session;
import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.Player;
import edu.ncsu.stockman.model.Stock;
import edu.ncsu.stockman.model.Player.Player_status;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainGameActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(Main.current_game == null || Main.current_player == null){
			System.err.println("MainGameActivity current_player or current_game is null");
			finish();
			return;
		}
		setContentView(R.layout.activity_main_game);
		
		setTitle(Main.current_game.name);
		
		
		//set the player's word
		LinearLayout v = (LinearLayout) findViewById(R.id.main_myword);
		for (int i = 0; i < Main.wordLength; i++) {
			TextView t = new TextView(this);
			t.setEnabled(false);
			t.setPadding(40, 0, 40, 0);
			if(Main.current_player.word_revealed[i])
				t.setTextAppearance(this, R.style.letter_revealed);
			else
				t.setTextAppearance(this, R.style.letter_not_revealed);
			t.setText(Main.current_player.word[i]+"");
			v.addView(t);
		}
		
		//fetch player info from server
		Player.get(this);
	}
	
	private void showStanding() {
		// sort players on the their die_date 
		PriorityQueue<Player> sorted = new PriorityQueue<Player>(Main.current_game.players.size());
		
		for (int i = 0; i < Main.current_game.players.size(); i++) {
			Player p = Main.current_game.players.valueAt(i);
			System.out.print("?");
			sorted.add(p);
		}
				//show players in the standing ssection
		LinearLayout main = (LinearLayout)findViewById(R.id.standing_list);
		main.removeAllViews();
		int counter = 0;
		for (int i = 0; i < Main.current_game.players.size(); i++) {
			
			View view = getLayoutInflater().inflate(R.layout.player_in_standing, main,false);
			
			Player p = sorted.poll();
			System.out.println(p.user.name);
			
			Button b = (Button) view.findViewById(R.id.player_item);
			
			TextView t = (TextView) view.findViewById(R.id.player_desc);

			t.setText(p.user.name);
			b.setTag(p);
			b.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Player p = (Player) v.getTag();
//					if (p.status ==Player_status.OUT ){
						Intent intent = new Intent(MainGameActivity.this, ShowLogs.class);
						intent.putExtra("player_id", p.id);
						startActivity(intent);
	//				}
				}
			});
			
			main.addView(view);
			if(p.status == Player_status.OUT){
				b.setText(i +1 +"");
			}
			else{
				b.setText("?");
				
			}
		}
	}
	
	public void updateCashValues(){
		//set the player's current cash and possessions.
		TextView t = (TextView) findViewById(R.id.remaining_cash);
		DecimalFormat dc = new DecimalFormat("#.00");
		t.setText("$"+Double.valueOf(dc.format(Main.current_player.cash)));
		
		//set player's possessions
		double current_possissions = 0D;
		for (int i = 0; i < Main.current_player.stocks.size(); i++) {
			Stock s = Main.current_player.stocks.get(i);
			current_possissions += s.company.getPrice() * s.amount;
		}
		t = (TextView) findViewById(R.id.remaining_stocks);
		t.setText("$"+Double.valueOf(dc.format(current_possissions)));
		
		//set player's total
		t = (TextView) findViewById(R.id.cashText);
		t.setText("$"+Double.valueOf(dc.format(current_possissions+Main.current_player.cash)));
		
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
