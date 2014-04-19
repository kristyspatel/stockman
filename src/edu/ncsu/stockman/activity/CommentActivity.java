package edu.ncsu.stockman.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.Session;

import edu.ncsu.stockman.DownloadImageTask;
import edu.ncsu.stockman.R;
import edu.ncsu.stockman.RoundedImageView;
import edu.ncsu.stockman.model.Comment;
import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.MidLayer;
import edu.ncsu.stockman.model.Notification;

public class CommentActivity extends Activity {

	Notification notification;
	Button postButton;
	
	// Timer to update comments if new comment from server
	Handler timerHandler = new Handler();
	Runnable timerRunnable = new Runnable() {

		@Override
		public void run() {

			if(notification.new_comment){
				setContent();
				notification.new_comment = false;
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
		notification = Main.current_user.notifications.get(getIntent().getExtras().getInt("notification_id"));
		setContentView(R.layout.activity_comment);
		Button postButton = (Button)findViewById(R.id.post);
		postButton.setVisibility(View.GONE);
		EditText et = (EditText)findViewById(R.id.comment_box);
		
		
		et.addTextChangedListener(new TextWatcher() {
			
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(s.length() != 0)
					setPostButtonVisibility(1);
				else
					setPostButtonVisibility(0);
					
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
	    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	    
		//TextView tv = (TextView) findViewById(R.id.notification);
		//tv.setText(Main.current_user.notifications(savedInstanceState.getString("notification_id")));
		JSONObject data = new JSONObject();
		try{		
		data.put("access_token", Session.getActiveSession().getAccessToken());//post
		}catch(JSONException e)
		{
			e.printStackTrace();
		}
		MidLayer asyncHttpPost = new MidLayer(data,this,true) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				if(result.info.code == 10){
					
					try {
						JSONArray j = new JSONArray(result.info.text);
						setComments(j);
						setNotification();
						setContent();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(result.info.code == 1)
				{
					setNotification();
					//setNoComments();
				}
			}
		};
		asyncHttpPost.execute(getString(R.string.base_url)+"/user/get_comment/"+notification.id_notification);
	}
	public void setPostButtonVisibility(int i)
	{
		Button postButton = (Button)findViewById(R.id.post);
		if(i==0)
			postButton.setVisibility(View.GONE);
		else
			postButton.setVisibility(0);
	}
	public void setUpComments()
	{
		JSONObject data = new JSONObject();
		try{		
		data.put("access_token", Session.getActiveSession().getAccessToken());//post
		}catch(JSONException e)
		{
			e.printStackTrace();
		}
		MidLayer asyncHttpPost = new MidLayer(data,this,true) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				if(result.info.code == 10){
					
					try {
						JSONArray j = new JSONArray(result.info.text);
						setComments(j);
						setNotification();
						setContent();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(result.info.code == 1)
				{
					setNotification();
					setNoComments();
				}
			}
		};
		asyncHttpPost.execute(getString(R.string.base_url)+"/user/get_comment/"+getIntent().getExtras().getInt("notification_id"));

	}
	
	public void setNotification()
	{
		TextView tv = (TextView) findViewById(R.id.notification);
		String txt = notification.getText(this);
		tv.setText(txt);
	}
	
	public void setNoComments()
	{
		LinearLayout lv = (LinearLayout)findViewById(R.id.comments_list);
		TextView tv = new TextView(this);
		//tv.setId(comments.get(0).getId());
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
			     LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(5, 0, 0, 0);
		tv.setLayoutParams(layoutParams);
		tv.setPadding(5, 5, 2, 0);
		tv.setText("No Comments");
		lv.addView(tv);
		//ScrollView sv = (ScrollView)findViewById(R.id.comment_scroller);
		//sv.addView(lv);
	}
	
	public void setContent()
	{   
		LinearLayout lv = (LinearLayout)findViewById(R.id.comments_list);
		//lv.setOrientation(LinearLayout.VERTICAL);
		//LinearLayout.LayoutParams lin_layout_params = new LinearLayout.LayoutParams(
			     //LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		//lv.setLayoutParams(lin_layout_params);
		lv.removeAllViews();
		for(int i=0;i<notification.comments.size();i++)	
		{
			View v = getLayoutInflater().inflate(
					   R.layout.comment, lv,false);

			v.getBackground().setAlpha(200);
			
			TextView comment = (TextView) v.findViewById(R.id.comment_text);
			comment.setText(notification.comments.get(i).getComment());
			
			TextView user_name = (TextView) v.findViewById(R.id.comment_user);
			user_name.setText(notification.comments.get(i).getUser().name+":");

			RoundedImageView user_pic = (RoundedImageView) v.findViewById(R.id.profile_img);
			DownloadImageTask.setFacebookImage(user_pic, notification.comments.get(i).getUser());
			
			lv.addView(v);
		}
	}

	public void setComments(JSONArray comments)
	{
		ArrayList<Comment> comment_list = new ArrayList<Comment>();
		for(int i=0;i<comments.length();i++)
		{
			comment_list.add(new Comment(comments.optJSONObject(i)));
		}
		notification.comments = comment_list;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nomenu, menu);
		return true;
	}

	public void postComment(View v){
		
		JSONObject data = new JSONObject();
		try{		
		data.put("access_token", Session.getActiveSession().getAccessToken());//post
		data.put("text",((EditText)findViewById(R.id.comment_box)).getText());
		}catch(JSONException e)
		{
			e.printStackTrace();
		}
		MidLayer asyncHttpPost = new MidLayer(data,this,true) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				if(result.info.code == 0){
					try{
						String text = (this.mData.getString("text"));
						//TODO get new comment id from server
						notification.comments.add(new Comment(Main.current_user,text , 0, notification));
						add_comment(text);
					}catch(Exception e)
					{
						e.printStackTrace();
					}
					
				}
			}
		};
		asyncHttpPost.execute(getString(R.string.base_url)+"/user/comment/"+getIntent().getExtras().getInt("notification_id"));

	}
	
	public void add_comment(String comment_text)
	{
		LinearLayout lv = (LinearLayout)findViewById(R.id.comments_list);
		View v = getLayoutInflater().inflate(
				   R.layout.comment, lv,false);

		v.getBackground().setAlpha(200);
		
		TextView comment = (TextView) v.findViewById(R.id.comment_text);
		comment.setText(comment_text);
		
		TextView user_name = (TextView) v.findViewById(R.id.comment_user);
		user_name.setText(Main.current_user.name);

		RoundedImageView user_pic = (RoundedImageView) v.findViewById(R.id.profile_img);
		DownloadImageTask.setFacebookImage(user_pic, Main.current_user);
		
		lv.addView(v);

		EditText et = (EditText)findViewById(R.id.comment_box);
		et.setText("");
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
