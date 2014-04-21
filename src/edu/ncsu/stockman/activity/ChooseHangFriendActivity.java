package edu.ncsu.stockman.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.ncsu.stockman.DownloadImageTask;
import edu.ncsu.stockman.R;
import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.Player;
import edu.ncsu.stockman.model.Player.Player_status;

public class ChooseHangFriendActivity extends Activity {

	 // Timer to update comments if new comment from server
	Handler timerHandler = new Handler();
	Runnable timerRunnable = new Runnable() {

		@Override
		public void run() {
 
			if(Main.current_game.player_status_change){
				addPlayers();
				Main.current_game.player_status_change= false;
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
		setContentView(R.layout.activity_choose_hang_friend);
	}

	@Override
	protected void onStart() {
		super.onStart();
		addPlayers();
	}

	
	private void addPlayers() {
		// dynamic adding players in the layout
		// TODO clean this mess
		
		LinearLayout main = (LinearLayout)findViewById(R.id.row1);
		main.removeAllViews();
		for (int i = 0; i < Main.current_game.players.size(); i++) {
		   Player p = Main.current_game.players.get(Main.current_game.players.keyAt(i));
		   if (p.id != Main.current_game.me.id){
			   View v = getLayoutInflater().inflate(
					   R.layout.player, main,false);
			   
			   ImageButton img = (ImageButton) v.findViewById(R.id.player_choose_button);
			   img.setTag(p);
			   img.setScaleType(ScaleType.FIT_XY);
			   
			   // Set the text
			   TextView t = (TextView) v.findViewById(R.id.player_choose_desc);
			   t.setText(p.name);
			   
			   if(p.status == Player_status.INVITED || p.status == Player_status.WAITING_FOR_WORD){
				   t.setText(p.name+" (Invited).");
				   img.setEnabled(false);
				   //get his profile picture
				   DownloadImageTask.setFacebookImage(img,p.user);
			   }
			   else if(p.status == Player_status.DECLINED){
				   continue;
			   }
			   else if(p.status == Player_status.LOST){
				   t.setText(p.name);
				   img.setEnabled(false);
				   img.setImageResource(R.drawable.hangman_dead_stage);
			   }
			   else if(p.status == Player_status.ENROLLED){
				   int counter = 0;
				   for (int j = 0; j < Main.wordLength; j++) {
					   if(p.word_revealed[j])
						   counter++;
				   }
				   if(counter==0)
					   img.setImageResource(R.drawable.hangman_0_stage);
				   else if(counter==1)
					   img.setImageResource(R.drawable.hangman_1_stage);
				   else if(counter==2)
					   img.setImageResource(R.drawable.hangman_2_stage);
				   else if(counter==3)
					   img.setImageResource(R.drawable.hangman_3_stage);
				   else if(counter==4)
					   img.setImageResource(R.drawable.hangman_4_stage);
				   else if(counter==5)
					   img.setImageResource(R.drawable.hangman_5_stage);
			   }
			   // set the pic
			   main.addView(v);
		   }
		   if (i == 1){
			   main = (LinearLayout)findViewById(R.id.row2);
			   main.removeAllViews();
		   }
		   if (i == 3){
			   main = (LinearLayout)findViewById(R.id.row3);
			   main.removeAllViews();
		   }
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nomenu, menu);
		return true;
	}
	
	/** Called when the user clicks on a player*/
	public void goToSettings(MenuItem m) {
		SettingsActivity.goToSettings(this);
	}
	
	public void choosePlayer(View view2) {
	    // move to another activity
		// TODO carry the player id
		Intent intent = new Intent(this, HangFriendActivity.class);
		intent.putExtra("player_id", ((Player) view2.getTag()).id);
		startActivity(intent);
	}

	public void logout(MenuItem c){
		SettingsActivity.logout(this);
	}


}
