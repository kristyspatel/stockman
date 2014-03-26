package edu.ncsu.stockman;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.facebook.Session;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import edu.ncsu.stockman.model.Company;
import edu.ncsu.stockman.model.Game;
import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.MidLayer;
import edu.ncsu.stockman.model.Notification;
import edu.ncsu.stockman.model.Player;
import edu.ncsu.stockman.model.User;
import android.os.Bundle;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Timeline extends Activity {

	SwipeListView swipelistview;
	NewsFeedAdapter adapter;
	List<NewsFeedRow> itemData;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		swipelistview=(SwipeListView)findViewById(R.id.example_swipe_lv_list);
		itemData=new ArrayList<NewsFeedRow>();
		adapter=new NewsFeedAdapter(this,R.layout.newsfeed_row,itemData);
		swipelistview.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
            }
 
            @Override
            public void onClosed(int position, boolean fromRight) {
            }
 
            @Override
            public void onListChanged() {
            }
 
            @Override
            public void onMove(int position, float x) {
            }
 
            @Override
            public void onStartOpen(int position, int action, boolean right) {
                //Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
            }
 
            @Override
            public void onStartClose(int position, boolean right) {
                //Log.d("swipe", String.format("onStartClose %d", position));
            }
 
            @Override
            public void onClickFrontView(int position) {
                //Log.d("swipe", String.format("onClickFrontView %d", position));
 
                swipelistview.openAnimate(position); //when you touch front view it will open
 
            }
 
            @Override
            public void onClickBackView(int position) {
                //Log.d("swipe", String.format("onClickBackView %d", position));
 
                swipelistview.closeAnimate(position);//when you touch back view it will close
            }
 
            @Override
            public void onDismiss(int[] reverseSortedPositions) {
 
            }
 
        });
 
        //These are the swipe listview settings. you can change these
        //setting as your requirement
        swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT); // there are five swiping modes
        swipelistview.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL); //there are four swipe actions
        //swipelistview.setSwipeActionRight(SwipeListView.SWIPE_ACTION_DISMISS);
        swipelistview.setOffsetLeft(convertDpToPixel(234f)); // left side offset
        //swipelistview.setOffsetRight(convertDpToPixel(40f)); // right side offset
        swipelistview.setAnimationTime(50); // Animation time
        swipelistview.setSwipeOpenOnLongPress(true); // enable or disable SwipeOpenOnLongPress
 
        swipelistview.setAdapter(adapter);
 
		/**
		 * Fetch User's info, games, notifications, invitations
		 */
		JSONObject data = new JSONObject();
		try{		
		data.put("access_token", Session.getActiveSession().getAccessToken());//post
		}catch(JSONException e)
		{
			e.printStackTrace();
		}
		System.out.println(data.toString());
		MidLayer asyncHttpPost = new MidLayer(data,this) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				if (result.error != null)
					System.out.println(result.error.text);
				if(result.info != null){
					if(result.info.code == 0){
						
						try {
							JSONObject j = new JSONObject(result.info.text);
							System.out.println("Userdata");
							System.out.println(j.toString());
							User me = new User(j.optJSONObject("info"));
							//System.out.println(me.toString());
							me.setGames(j.optJSONArray("games"));
							me.setNotifications(j.optJSONArray("notifications"));
							me.setFriends(j.optJSONArray("friends"));
							Main.current_user = me;
							
							//Change the activity components
							((Timeline)context).setName(Main.current_user.name);
							((Timeline)context).setGames();
							((Timeline)context).setNotifications();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		};		
		asyncHttpPost.execute(getString(R.string.base_url)+"/user/get");
		MidLayer asyncHttpPost2 = new MidLayer(data,this) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				if (result.error != null)
					System.out.println(result.error.text);
				if(result.info != null){
					if(result.info.code == 0){
						
						try {
							JSONArray j = new JSONArray(result.info.text);
							for (int i = 0; i < j.length(); i++) {
								Company c = new Company(j.getJSONObject(i));
								Main.companies.append(c.id, c);
							}
							System.out.println("companies"+Main.companies);
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		};		
		asyncHttpPost2.execute(getString(R.string.base_url)+"/stockmarket/get");

	}
	
	public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }
	
	//TODO delete it and test it.
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	
	public void setNotifications() {
		for(Notification n: Main.current_user.notifications)
        {
            itemData.add(new NewsFeedRow(n)); 
        }
        adapter.notifyDataSetChanged();
		//LinearLayout l = (LinearLayout) findViewById(R.id.notification_list);
		
		//for(Notification n: Main.current_user.notifications){
			//TextView t = new TextView(l.getContext());
			//t.setText(n.text);
			//t.setTextSize(15);
			//t.setPadding(0,5,0,5);
			//t.setTextColor(Color.parseColor("#ffffff"));
			//l.addView(t,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		//}
	}
	public void setName(String s) {
		TextView t = (TextView) findViewById(R.id.welcome);
		t.setText("Welcome:"+s);
	}
	public void setGames() {
		super.onStart();
		//TODO clean this mess
		LinearLayout main = (LinearLayout)findViewById(R.id.games_list);
		for (int i = 0; i < Main.current_user.games.size(); i++) {
			Game g = Main.current_user.games.valueAt(i);
			View v = getLayoutInflater().inflate(R.layout.game_in_timeline, main,false);
			Button b = (Button) v.findViewById(R.id.game_item);
			b.setTag(g);
			b.setText(g.name.charAt(0)+"");
			TextView t = (TextView) v.findViewById(R.id.game_desc);
			t.setText(g.name);
			main.addView(v);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_timeline, menu);
		return true;
	}
	
	public void create_new_game(View v){
		Intent intent = new Intent(this, New_Game.class);
		startActivity(intent);
	}

	public void open_game(View v){
		
		Main.current_game = (Game) v.getTag();
		
		JSONObject data = new JSONObject();
		try{
		data.put("access_token", Session.getActiveSession().getAccessToken());//post
		}catch(JSONException e){
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
							Player me = new Player(j.optJSONObject("me"));
							Main.current_game.setPlayers(j.optJSONArray("players"));
							
							Main.current_player = me;
							
							System.out.println(Main.current_player);
							Intent intent = new Intent(context, MainGameActivity.class);
							startActivity(intent);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		};		
		asyncHttpPost.execute(getString(R.string.base_url)+"/game/get/"+Main.current_game.id);
		
	}
	public void logout(MenuItem c){
		SettingsActivity.logout(this);
	}
	public void goToSettings(MenuItem c){
		SettingsActivity.goToSettings(this);
	}

}
