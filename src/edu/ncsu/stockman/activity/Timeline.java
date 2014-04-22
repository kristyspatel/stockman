package edu.ncsu.stockman.activity;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import edu.ncsu.stockman.NewsFeedAdapter;
import edu.ncsu.stockman.NewsFeedRow;
import edu.ncsu.stockman.R;
import edu.ncsu.stockman.model.Company;
import edu.ncsu.stockman.model.Game;
import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.Player;
import edu.ncsu.stockman.model.Player.Player_status;
import edu.ncsu.stockman.model.User;

public class Timeline extends MainActivity {


	SwipeListView swipelistview;
	NewsFeedAdapter adapter;
	List<NewsFeedRow> itemData;

	TextView noNotification;
	SparseArray<LinearLayout> l;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setTheme(android.R.style.Theme_Holo);
		setContentView(R.layout.activity_timeline);

		noNotification = (TextView) findViewById(R.id.noNotifications);
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
 
		

		//Fetch companies and there prices (only if not fetched before or if the prices has changed)
		if(Main.companies.size()==0 || Main.day < Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
			Main.day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
			Company.getPrices(this);
		}
		
		if(Session.getActiveSession()!=null)
			System.out.println(Session.getActiveSession().getAccessToken());
		else{
			Log.w("StockMan", "Facebook Session is Inactive! Why?");
			finish();
		}
		//Fetch User's info, games, notifications, invitations
		//User.fetchUserInfo(this);
		if(Main.current_user==null)
			User.fetchUserInfo(this,regid);
		else{
			setName(Main.current_user.name);
			setGames();
			setNotifications();
		}
	}
	
	public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }
	
	//TODO delete it and test it.
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	// Timer to update comments if new comment from server
	Handler timerHandler = new Handler();
	Runnable timerRunnable = new Runnable() {

		@Override
		public void run() {
			if(Main.current_user!=null){
				if(Main.current_user.new_game){
					setName(Main.current_user.name);
					setGames();
					//User.fetchUserInfo(Timeline.this);
					Main.current_user.new_game = false;
				}
				if(Main.current_user.new_notification){
					setNotifications();
					Main.current_user.new_notification = false;
				}
				for (int i = 0; i < Main.current_user.games.size(); i++) {
					if(Main.current_user.games.valueAt(i).player_status_change){
						setGames();
						break;
					}
						
				}
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
	
	public void setNotifications() {
    	if(Main.current_user.notifications.size()==0){
    		noNotification.setVisibility(View.VISIBLE);
        }
        else{
        	noNotification.setVisibility(View.GONE);
        }
    	
		itemData.clear();
		for (int i = Main.current_user.notifications.size()-1; i >= 0; i--) {
            itemData.add(new NewsFeedRow(Main.current_user.notifications.valueAt(i))); 
        }
        adapter.notifyDataSetChanged();
        
        	
	}
	public void setName(String s) {
		TextView t = (TextView) findViewById(R.id.welcome);
		t.setText("Welcome:"+s);
	}
	public void setGames() {
		
		LinearLayout main = (LinearLayout)findViewById(R.id.games_list);
		main.removeAllViews();
		
		// New Button
		View v = getLayoutInflater().inflate(R.layout.game_in_timeline, main,false);
		
		Button b = (Button) v.findViewById(R.id.game_item);
		b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getBaseContext(), New_Game.class);
				startActivity(intent);
				
			}
		});
		b.setText("New");
		b.setTextSize(15);
		main.addView(v);
		
		//set the property list variable
		l = new SparseArray<LinearLayout>(Main.current_user.games.size());
		
		for (int i = 0; i < Main.current_user.games.size(); i++) {
			
			//get game from model
			Game g = Main.current_user.games.valueAt(i);
			if(g ==null){
				Log.d("Activity", "Timeline: game is null");
			}
			//use the template
			v = getLayoutInflater().inflate(R.layout.game_in_timeline, main,false);
			l.put(g.id, (LinearLayout) v);
			
			//onClick
			//change the text of button and set tag
			
			b = (Button) v.findViewById(R.id.game_item);
			
			
			b.setTag(g);
			b.setText(g.name.charAt(0)+"");
			//change the textview text
			TextView t = (TextView) v.findViewById(R.id.game_desc);
			
			if(g.me.status == Player_status.INVITED){
				t.setTextAppearance(this, R.style.game_button_inviatation);
				b.setBackgroundResource(R.drawable.circle_button_pending);
				b.setTextAppearance(this, R.style.game_button_inviatation);
				b.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onInvitedGameClick(v);
					}
				});
			}
			else if(g.me.status == Player_status.DECLINED){
				continue;
			}
			else if(g.me.status == Player_status.WON || 
					g.me.status == Player_status.LOST){
				t.setTextAppearance(this, R.style.game_button_inviatation);
				b.setBackgroundResource(R.drawable.circle_button_out);
				b.setTextAppearance(this, R.style.game_button_inviatation);
				b.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						open_game(v);
					}
				});
			}
			else{
				b.setTextAppearance(this, R.style.game_button);
				b.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						open_game(v);
					}
				});
			}
			t.setText(g.name);
			
			main.addView(v);
		}
	}
	View button; //sorry I could not find another way
	public void onInvitedGameClick(View button) {
		this.button = button;
        PopupMenu popup = new PopupMenu(this, button);
        popup.getMenuInflater().inflate(R.menu.game_dropdown, popup.getMenu());
        
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
			public boolean onMenuItemClick(MenuItem item) {
            	
            	if(item.getItemId() == R.id.game_invitation_accept){
            		//UI
            		Game g = (Game) Timeline.this.button.getTag();
            		LinearLayout ll = Timeline.this.l.get(g.id);
            		Button b = (Button) ll.findViewById(R.id.game_item);
            		b.setBackgroundResource(R.drawable.circle_button);
            		b.setTextAppearance(Timeline.this, R.style.game_button);
            		TextView t = (TextView) ll.findViewById(R.id.game_desc);
            		t.setTextAppearance(Timeline.this, R.style.game_button);
            		b.setOnClickListener(new View.OnClickListener() {
            			@Override
            			public void onClick(View v) {
            				open_game(v);
            			}
            		});
            		
            		Main.current_game = g;
            		
            		//TODO use cache
            		//server request, and if it's sucessful, it will start Main Game Activity
            		//Game.fetchGame(Timeline.this);
            		
            		
            		//model 
            		g.me.status = Player_status.WAITING_FOR_WORD;
            		Player.changeStatus(Timeline.this, g, Player_status.WAITING_FOR_WORD);
            		

            		if(Main.current_game.me.status == Player.Player_status.WAITING_FOR_WORD){
            			Intent intent = new Intent(Timeline.this, PickWordActivity.class);
            			startActivity(intent);
            		}
            		else if(Main.current_game.me.status == Player.Player_status.ENROLLED){
            			Intent intent = new Intent(Timeline.this, MainGameActivity.class);
            			startActivity(intent);
            		}
            		else if(Main.current_game.me.status == Player.Player_status.LOST || Main.current_game.me.status == Player.Player_status.WON){
            			//TODO lang
            			Intent intent = new Intent(Timeline.this, MainGameActivity.class);
            			startActivity(intent);
            			Toast toast = Toast.makeText(context, "This game is now over.", Toast.LENGTH_LONG);
            			toast.show();
            		}
            	}
            	else{
            		//UI
            		Game g = (Game) Timeline.this.button.getTag();
            		LinearLayout ll = Timeline.this.l.get(g.id);
            		ll.setVisibility(View.GONE);

            		//model
            		g.me.status = Player_status.DECLINED;
            		Player.changeStatus(Timeline.this, g, Player_status.DECLINED);
            	}
                return true;
            }
        });
 
        popup.show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_timeline, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
		setResult(1);
		super.onBackPressed();
	}

	public void open_game(View v){
		
		Main.current_game = (Game) v.getTag();
		
		//TODO use cache
		//server request, and if it's sucessful, it will start Main Game Activity
		//Game.fetchGame(this);
		
		if(Main.current_game.me.status == Player.Player_status.WAITING_FOR_WORD){
			Intent intent = new Intent(this, PickWordActivity.class);
			startActivity(intent);
		}
		else if(Main.current_game.me.status == Player.Player_status.ENROLLED){
			Intent intent = new Intent(this, MainGameActivity.class);
			startActivity(intent);
		}
		else if(Main.current_game.me.status == Player.Player_status.LOST || Main.current_game.me.status == Player.Player_status.WON){
			//TODO lang
			Intent intent = new Intent(this, MainGameActivity.class);
			startActivity(intent);
			Toast toast = Toast.makeText(context, "This game is now over.", Toast.LENGTH_LONG);
			toast.show();
		}
	}
	public void logout(MenuItem c){
		SettingsActivity.logout(this);
	}
	public void manageFriends(MenuItem c){
		Intent intent = new Intent(getBaseContext(), ManageFriendsActivity.class);
		startActivity(intent);
	}

	public void goToSettings(MenuItem c){
		SettingsActivity.goToSettings(this);
	}
}
