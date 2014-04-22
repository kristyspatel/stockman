package edu.ncsu.stockman.activity;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.ncsu.stockman.R;
import edu.ncsu.stockman.model.Game;
import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.User;

public class New_Game extends Activity {

	CheckBox[] members;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_Holo);
		/**
		 * Fetch User's Friends
		 */
		setContentView(R.layout.activity_new_game);
		setFriends(Main.current_user.friends);
		
		//TODO change game name listener
		TextView game_name = (TextView) findViewById(R.id.gamename);
		game_name.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				onChange(getBaseContext());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			@Override
			public void afterTextChanged(Editable s) {}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nomenu, menu);
		return true;
	}

	
	public void setFriends(SparseArray<User> friends){
		LinearLayout friend_scroller = (LinearLayout) findViewById(R.id.friend_list);
		
		members = new CheckBox[Main.current_user.friends.size()];
		
		for(int i=0; i<Main.current_user.friends.size();i++)
		{
			CheckBox friend = new CheckBox(this);
			friend.setId(friends.keyAt(i));
			friend.setText(friends.get(friends.keyAt(i)).name);
			friend.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			friend.setTextColor(getResources().getColor(R.color.kulur_white));
			
			friend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					onChange(buttonView.getContext());
				}
			});
			members[i] = friend;
			friend_scroller.addView(friend);
		}
	}
	
	public void onChange(Context c){
		TextView game_name = (TextView) findViewById(R.id.gamename);
		Button invite = (Button) findViewById(R.id.invite);
		for (int j = 0; j < members.length; j++) {
			if(members[j].isChecked()){
				if(game_name.getText().toString().trim().length() > 0 ){
					invite.setEnabled(true);
					invite.setTextAppearance(c, R.style.button_enabled);
					return;
				}
			}
		}
		invite.setEnabled(false);
		invite.setTextAppearance(c, R.style.button_disabled);
	}
	
	public void startGame(View v)
	{
		System.out.println("Creating new game");
		ArrayList<String> members_array = new ArrayList<String>(); 
		for(int i=0;i<members.length;i++)
		{
			if(members[i].isChecked())
				members_array.add(Integer.toString((members[i].getId())));
		}
		EditText game = (EditText)findViewById(R.id.gamename);
		
		//first letter to upper case and trim
		String s = game.getText().toString().trim();
		char[] stringArray = s.trim().toCharArray();
        stringArray[0] = Character.toUpperCase(stringArray[0]);
        s = new String(stringArray);
        
		Game.createGame(this, members_array, s);
		//finish();
	}
	
}
