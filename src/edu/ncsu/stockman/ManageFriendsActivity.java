package edu.ncsu.stockman;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;
import edu.ncsu.stockman.model.Friend;
import edu.ncsu.stockman.model.Friend.Friendship_status;
import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.MidLayer;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ManageFriendsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_friends);
		

		getFriends();
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
	   		else if(u.friendship_status == Friendship_status.NOTINVITIED){
	   			s.setBackgroundResource(R.drawable.rounded_rectangle_notinvitied);
	   			s.setTextColor(getResources().getColor(R.color.notinvitied));
	   			s.setText("Not Invited");
	   		}
	}
	private void setFriendsView(ArrayList<Friend> friends){
		if (friends == null){
			Log.i("ERROR","null friends");
			return;
		}
		LinearLayout main = (LinearLayout)findViewById(R.id.list_friends);
		for(Friend u: friends){
  	   		View v = getLayoutInflater().inflate(
					   R.layout.friend_item_list, main,false);
  	   		
  	   		//set the name
  	   		TextView t = (TextView) v.findViewById(R.id.friend_list_name);
  	   		t.setText(u.name);
  	   		
  	   		// set the picture
  	   		ImageView i = (ImageView) v.findViewById(R.id.friend_list_img);
  	   		new DownloadImageTask(i)
	  	   			.execute("http://graph.facebook.com/"+u.facebook_id+"/picture?type=square");
  	   		
  	   		//set the status
  	   		TextView s = (TextView) v.findViewById(R.id.friend_list_status);
  	   		s.setTag(u);
  	   		setStatusStyle(s,u);
  	   		s.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					Friend f = (Friend) v.getTag();
					TextView t = (TextView) v;
					
					if(hasFocus){
			  	   		if(f.friendship_status == Friendship_status.ACCEPTED){
			  	   			t.setText("Unfriend?");
			  	   		}
			  	   		else if(f.friendship_status == Friendship_status.PENDING){
			  	   			t.setText("Cancel?");
			  	   		}
			  	   		else if(f.friendship_status == Friendship_status.NOTINVITIED){
			  	   			t.setText("Invite?");
			  	   		}
					}
					else{
			  	   		if(f.friendship_status == Friendship_status.ACCEPTED){
			  	   			t.setText("Friends");
			  	   		}
			  	   		else if(f.friendship_status == Friendship_status.PENDING){
			  	   			t.setText("Pending");
			  	   		}
			  	   		else if(f.friendship_status == Friendship_status.NOTINVITIED){
			  	   			t.setText("Not invited");
			  	   		}
						
					}
				}
			});

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
					
					MidLayer asyncHttpPost = new MidLayer(data,v.getContext()) {
						@Override
						protected void resultReady(MidLayer.Result result) {
							if (result.error != null)
								System.out.println(result.error.text);
							if(result.info != null && result.info.code == 0){
								Friend f = (Friend) view.getTag();
								TextView t=(TextView) view;
								if(f.friendship_status == Friendship_status.ACCEPTED){
									f.friendship_status = Friendship_status.NOTINVITIED;
									setStatusStyle(t, f);
					  	   		}
					  	   		else if(f.friendship_status == Friendship_status.PENDING){
					  	   			f.friendship_status = Friendship_status.NOTINVITIED;
					  	   			setStatusStyle(t, f);
					  	   		}
					  	   		else if(f.friendship_status == Friendship_status.NOTINVITIED){
					  	   			f.friendship_status = Friendship_status.PENDING;
					  	   			setStatusStyle(t, f);
					  	   		}					
							}
						}
					};
					asyncHttpPost.view = v;
					if(f.friendship_status == Friendship_status.ACCEPTED){
						//asyncHttpPost.execute(getString(R.string.base_url)+"/user/list_facebook_friends");
		  	   		}
		  	   		else if(f.friendship_status == Friendship_status.PENDING){
		  	   			//asyncHttpPost.execute(getString(R.string.base_url)+"/user/list_facebook_friends");
		  	   		}
		  	   		else if(f.friendship_status == Friendship_status.NOTINVITIED){
		  	   			asyncHttpPost.execute(getString(R.string.base_url)+"/friend/add_friend/"+f.facebook_id);
		  	   		}					
				}
			});
  	   		
  	   		main.addView(v);
		}
	}
	private void getFriends(){
		JSONObject data = new JSONObject();
		try{		
		data.put("access_token", Session.getActiveSession().getAccessToken());//post
		}catch(JSONException e)
		{
			e.printStackTrace();
		}
		MidLayer asyncHttpPost = new MidLayer(data,this) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				if (result.error != null)
					System.out.println(result.error.text);
				if(result.info != null){
					if(result.info.code == 0){
						
						try {
							JSONArray friends = new JSONArray(result.info.text);
							Main.current_user.setFacebookFriends(friends);
							setFriendsView(Main.current_user.facebook_friends);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		};		
		asyncHttpPost.execute(getString(R.string.base_url)+"/user/list_facebook_friends");
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_friends, menu);
		return true;
	}

}
