package edu.ncsu.stockman.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.Session;

import edu.ncsu.stockman.DownloadImageTask;
import edu.ncsu.stockman.R;
import edu.ncsu.stockman.model.Friend;
import edu.ncsu.stockman.model.Friend.Friendship_status;
import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.MidLayer;
import edu.ncsu.stockman.model.User;

public class ManageFriendsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_friends);
		
		User.getFriends(this);
	}

	
	// Timer to update comments if new comment from server
	Handler timerHandler = new Handler();
	Runnable timerRunnable = new Runnable() {

		@Override
		public void run() {

			if(Main.current_user.new_friend){
				setFriendsView(Main.current_user.facebook_friends);
				Main.current_user.new_friend= false;
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

	
	private void setStatusStyle(TextView s, Friend u){
	   		if(u.friendship_status == Friendship_status.ACCEPTED){
	   			s.setBackgroundResource(R.drawable.rounded_rectangle_friend);
	   			s.setTextColor(getResources().getColor(R.color.friend));
	   			s.setText("Friend");
	   		}
	   		else if(u.friendship_status == Friendship_status.PENDING){
	   			s.setBackgroundResource(R.drawable.rounded_rectangle_pending);
	   			s.setTextColor(getResources().getColor(R.color.pending));
	   			s.setText("Pending");
	   		}
	   		else if(u.friendship_status == Friendship_status.REQUEST_SENT){
	   			s.setBackgroundResource(R.drawable.rounded_rectangle_pending);
	   			s.setTextColor(getResources().getColor(R.color.pending));
	   			s.setText("Request Sent");
	   		}
	   		else if(u.friendship_status == Friendship_status.NOTINVITIED){
	   			s.setBackgroundResource(R.drawable.rounded_rectangle_notinvitied);
	   			s.setTextColor(getResources().getColor(R.color.notinvitied));
	   			s.setText("Not Invited");
	   		}
	}
	public void setFriendsView(SparseArray<Friend> friends){
		if (friends == null){
			Log.i("ERROR","null friends");
			return;
		}
		LinearLayout main = (LinearLayout)findViewById(R.id.list_friends);
		main.removeAllViews();
		for (int i = 0; i < friends.size(); i++) {
			
			Friend u = friends.valueAt(i);
  	   		View v = getLayoutInflater().inflate(
					   R.layout.friend_item_list, main,false);
  	   		
  	   		//set the name
  	   		TextView t = (TextView) v.findViewById(R.id.friend_list_name);
  	   		t.setText(u.name);
  	   		
  	   		// set the picture
  	   		ImageView img = (ImageView) v.findViewById(R.id.friend_list_img);
  	   		DownloadImageTask.setFacebookImage(img,u.user);
  	   		
  	   		//set the status
  	   		TextView s = (TextView) v.findViewById(R.id.friend_list_status);
  	   		s.setTag(u);
  	   		setStatusStyle(s,u);

  	   		s.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Friend f = (Friend) v.getTag();
					TextView t = (TextView) v;
					
					t.setId(f.id);
					JSONObject data = new JSONObject();
					try{		
					data.put("access_token", Session.getActiveSession().getAccessToken());//post
					}catch(JSONException e)
					{
						e.printStackTrace();
					}
					
					MidLayer asyncHttpPost = new MidLayer(data,v.getContext(),true) {
						@Override
						protected void resultReady(MidLayer.Result result) {
							if (result.error != null)
								System.out.println(result.error.text);
							if(result.info != null && result.info.code == 0){
								Friend f = (Friend) view.getTag();
								TextView t=(TextView) view;
								if(f.friendship_status == Friendship_status.ACCEPTED){
									//f.friendship_status = Friendship_status.NOTINVITIED;
									//setStatusStyle(t, f);
					  	   		}
					  	   		else if(f.friendship_status == Friendship_status.PENDING){
					  	   			f.friendship_status = Friendship_status.ACCEPTED;
					  	   			setStatusStyle(t, f);
					  	   			Main.current_user.friends.put(f.id, f.user);
					  	   		}
					  	   		else if(f.friendship_status == Friendship_status.NOTINVITIED){
					  	   			f.friendship_status = Friendship_status.REQUEST_SENT;
					  	   			setStatusStyle(t, f);
					  	   		}					
					  	   		else if(f.friendship_status == Friendship_status.REQUEST_SENT){
					  	   			//f.friendship_status = Friendship_status.ACCEPTED;
					  	   			//setStatusStyle(t, f);
					  	   		}					
							}
						}
					};
					asyncHttpPost.view = v;
					if(f.friendship_status == Friendship_status.ACCEPTED){
						//asyncHttpPost.exec(getString(R.string.base_url)+"/user/list_facebook_friends");
		  	   		}
		  	   		else if(f.friendship_status == Friendship_status.PENDING){
		  	   			asyncHttpPost.exec(getString(R.string.base_url)+"friend/accept/"+f.id);
		  	   		}
		  	   		else if(f.friendship_status == Friendship_status.REQUEST_SENT){
		  	   			//asyncHttpPost.exec(getString(R.string.base_url)+"/friend/add_friend/"+f.facebook_id);
		  	   		}					
		  	   		else if(f.friendship_status == Friendship_status.NOTINVITIED){
		  	   			asyncHttpPost.exec(getString(R.string.base_url)+"friend/add_friend/"+f.facebook_id);
		  	   		}					
				}
			});
  	   		
  	   		main.addView(v);
		}
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nomenu, menu);
		return true;
	}

}
