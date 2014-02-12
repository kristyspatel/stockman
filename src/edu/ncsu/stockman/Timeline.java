package edu.ncsu.stockman;

import edu.ncsu.stockman.model.Game;
import edu.ncsu.stockman.model.Main;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Timeline extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);

		//TODO clean this mess
		/*LinearLayout main = (LinearLayout)findViewById(R.id.games_list);
		for (int i = 0; i < Main.current_user.games.size(); i++) {
			Game g = Main.current_user.games.valueAt(i);
			View v = getLayoutInflater().inflate(R.layout.game_in_timeline, main,false);
			Button b = (Button) v.findViewById(R.id.game_item);
			b.setText(g.name.charAt(0)+"");
			TextView t = (TextView) v.findViewById(R.id.game_desc);
			t.setText(g.name);
			main.addView(v);
		}*/
		
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.timeline, menu);
		return true;
	}
	
	public void create_new_game(View v){
		Intent intent = new Intent(this, New_Game.class);
		startActivity(intent);
		
	}

	public void open_game(View v){
		Intent intent = new Intent(this, MainGameActivity.class);
		startActivity(intent);
		//Main.current_game = Main.games.get(Main.current_user.games.get(1).id);
	}
	public void logout(MenuItem c){
		//SettingsActivity.logout(this);
	}

}
