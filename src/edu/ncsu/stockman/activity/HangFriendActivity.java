package edu.ncsu.stockman.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.ncsu.stockman.R;
import edu.ncsu.stockman.model.Guess;
import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.Player;

public class HangFriendActivity extends Activity {

	Button[] buttons = new Button[26];
	Player him;
	boolean[] characters = new boolean[26];
	private int buttonPerRow = 6;
	
	TextView cash;
	// Timer to update comments if new comment from server
	Handler timerHandler = new Handler();
	Runnable timerRunnable = new Runnable() {

		@Override
		public void run() {

			if(him.new_letter_revealed){
				setWord();
				setOthersCorrectGuesses();
				setButtons();
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
		setContentView(R.layout.activity_hang_friend);
		
		
		him = Main.current_game.players.get(getIntent().getExtras().getInt("player_id"));
		if(him==null)
			Log.d("StockMan", getIntent().getExtras().getInt("player_id")+"is null");

		cash = (TextView) findViewById(R.id.cash);
		cash.setText("$"+String.format("%.2f",Main.current_game.me.cash));
		
		TextView t = (TextView) findViewById(R.id.hang_player_word_is);
		t.setText(him.name + "'s Word is:");
		
		setTitle("Hanging "+him.name);
		
		
		setWord();
		setMyGuesses();
		setOthersCorrectGuesses();
		setButtons();
	}

	private void setMyGuesses(){
		// set letters revealed
				// 1. from previous guesses
				for (int i = 0; i < Main.current_game.me.guesses.size(); i++) {
					Guess g = Main.current_game.me.guesses.get(i);
					System.out.println("Guess id"+g.id);
					if(him.id == g.him.id){
						int x  = getNumberForChar(Character.toLowerCase(g.letter));
						if(x==-1)
							System.err.println("getNumberForChar:"+Character.toLowerCase(g.letter));
						else
							characters[x] = true;
					}
				}
	}
	private void setOthersCorrectGuesses(){
		// 2. others revelation of letters
		for (int i = 0; i < Main.wordLength; i++) {
			if(him.word_revealed[i]){
				int x  = getNumberForChar(Character.toLowerCase(him.word[i]));
				if(x==-1)
					System.err.println("getNumberForChar:"+Character.toLowerCase(him.word[i]));
				else
					characters[x] = true;
				characters[getNumberForChar(Character.toLowerCase(him.word[i]))] = true;
			}
		}
	}
	private void setWord(){
		//set the word
		TextView t = (TextView) findViewById(R.id.hang_player_word);
		t.setText(him.hideWord());
	}
	private void setButtons(){
		LinearLayout l = (LinearLayout) findViewById(R.id.row1);
		l.removeAllViews();
		for (int i = 0; i < buttons.length; i++) {

			if (i == 4*buttonPerRow){
				l = (LinearLayout) findViewById(R.id.row5);
				l.removeAllViews();
			}
			else if (i == 3*buttonPerRow){
				l = (LinearLayout) findViewById(R.id.row4);
				l.removeAllViews();
			}
			else if (i == 2*buttonPerRow){
				l = (LinearLayout) findViewById(R.id.row3);
				l.removeAllViews();
			}
			else if (i == 1*buttonPerRow){
				l = (LinearLayout) findViewById(R.id.row2);
				l.removeAllViews();
			}
			
			Button b = new Button(this);
			
			b.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					TextView error = (TextView) findViewById(R.id.error_line);
					if(Main.current_game.me.cash < Main.costOfGuess){
						error.setText("No enough money to guess");
						error.setVisibility(View.VISIBLE);
						return;
					}

					boolean correct = Main.current_game.me.guess(him, ((String) v.getTag()).charAt(0),v.getContext());
					if(!correct){
						cash.setText("$"+String.format("%.2f",Main.current_game.me.cash));
					}
					((Button) v).setTextAppearance(v.getContext(), R.style.hang_letter_button_disabled);
					v.setEnabled(false);
				}
			});
			
			b.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,1f));
			
			//check if this letter is guessed before 
			if(characters[i]){
				b.setTextAppearance(this, R.style.hang_letter_button_disabled);
				b.setEnabled(false);
			}
			else{
				b.setTextAppearance(this, R.style.hang_letter_button_enabled);
				b.setEnabled(true);
			}
			b.setText(getCharForNumber(i));	
			b.setTag(getCharForNumber(i));
			buttons[i] = b;
			l.addView(b);
			
		}
	}
	private int getNumberForChar(char i) {
		return i >= 'a' && i <= 'z' ? i - 'a' : -1;
	}
	private String getCharForNumber(int i) {
	    return i >= 0 && i < 27 ? String.valueOf((char)(i + 'A')) : null;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nomenu, menu);
		return true;
	}
	public void logout(MenuItem c){
		SettingsActivity.logout(this);
	}
	public void goToSettings(MenuItem c){
		SettingsActivity.goToSettings(this);
	}

}
