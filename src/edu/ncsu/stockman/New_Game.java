package edu.ncsu.stockman;



import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;

import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.MidLayer;
import edu.ncsu.stockman.model.User;

import android.os.Bundle;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

public class New_Game extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/**
		 * Fetch User's Friends
		 */
		setContentView(R.layout.activity_new_game);
		setFriends(Main.current_user.friends);
		
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
	
	public void setFriends(SparseArray<User> friends){
		LinearLayout friend_scroller = (LinearLayout) findViewById(R.id.friend_list);
		
		for(int i=0; i<Main.current_user.friends.size();i++)
		{
			CheckBox friend = new CheckBox(this);
			friend.setId(friends.keyAt(i));
			friend.setText(friends.get(friends.keyAt(i)).name);
			friend.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			friend.setTextColor(getResources().getColor(R.color.kulur_white));
			friend_scroller.addView(friend);
		}
	}
	
	
	public void startGame(View v)
	{
		//HashMap<String, String> data = new HashMap<String, String>();
		JSONObject data = new JSONObject();
		try{	
		data.put("access_token",  Session.getActiveSession().getAccessToken());
		
		//data.put("access_token", Session.getActiveSession().getAccessToken());//post
		ArrayList<String> members = new ArrayList<String>(); 
		for(int i=0;i<Main.current_user.friends.size();i++)
		{
			CheckBox checkBox = (CheckBox)findViewById(Main.current_user.friends.keyAt(i));
			if(checkBox.isChecked())
				members.add(Integer.toString((checkBox.getId())));
		}
		data.put("members", members);
		EditText game = (EditText)findViewById(R.id.gamename);
		data.put("name",game.getText());
		System.out.println(data.toString());
		}catch(JSONException e){
			e.printStackTrace();
		}

		MidLayer asyncHttpPost = new MidLayer(data,this) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				if (result.error != null)
					System.out.println(result.error.text);
				if(result.info != null){
					if(result.info.code == 0){
						System.out.println(result.info.text);
						Intent intent = new Intent(context,Timeline.class);
						startActivity(intent);	
					}
				}
			}
		};		
		asyncHttpPost.execute(getString(R.string.base_url)+"/game/create");
		

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
