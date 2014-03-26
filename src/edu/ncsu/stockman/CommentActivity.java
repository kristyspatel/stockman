package edu.ncsu.stockman;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;

import edu.ncsu.stockman.model.Comment;
import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.MidLayer;
import edu.ncsu.stockman.model.Notification;
import edu.ncsu.stockman.model.User;
import android.R.color;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;

public class CommentActivity extends Activity {

	ArrayList<Comment> comments;
	Button postButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		System.out.println(data.toString());
		MidLayer asyncHttpPost = new MidLayer(data,this) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				if (result.error != null)
					System.out.println(result.error.text);
				if(result.info != null){
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
			}
		};
		System.out.println("/user/get_comment/"+getIntent().getExtras().getInt("notification_id"));
		asyncHttpPost.execute(getString(R.string.base_url)+"/user/get_comment/"+getIntent().getExtras().getInt("notification_id"));
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
		System.out.println(data.toString());
		MidLayer asyncHttpPost = new MidLayer(data,this) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				if (result.error != null)
					System.out.println(result.error.text);
				if(result.info != null){
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
			}
		};
		System.out.println("/user/get_comment/"+getIntent().getExtras().getInt("notification_id"));
		asyncHttpPost.execute(getString(R.string.base_url)+"/user/get_comment/"+getIntent().getExtras().getInt("notification_id"));

	}
	
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}
	public void setNotification()
	{
		TextView tv = (TextView) findViewById(R.id.notification);
		for(Notification not:Main.current_user.notifications)
		{		
			System.out.println(not.id_notification);
			if(not.id_notification == getIntent().getExtras().getInt("notification_id"))
			{
				
				tv.setText(not.text);
			}
		}
	}
	
	public void setNoComments()
	{
		LinearLayout lv = (LinearLayout)findViewById(R.id.comments_list);
		TextView tv = new TextView(this);
		//tv.setId(comments.get(0).getId());
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
			     LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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
		System.out.println("set colour");
		LinearLayout lv = (LinearLayout)findViewById(R.id.comments_list);
		//lv.setOrientation(LinearLayout.VERTICAL);
		//LinearLayout.LayoutParams lin_layout_params = new LinearLayout.LayoutParams(
			     //LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		//lv.setLayoutParams(lin_layout_params);
		for(int i=0;i<comments.size();i++)	
		{
			System.out.println(comments.get(i).getComment());
			TextView user_name = new TextView(this);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				     LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(5, 2, 0, 0);		
			user_name.setLayoutParams(layoutParams);
			user_name.setPadding(5, 5, 2, 0);
			user_name.setText(comments.get(i).getUser().name+":");
			
			user_name.setTextColor(this.getResources().getColor(edu.ncsu.stockman.R.color.kulur_purple));
			user_name.setTypeface(null, Typeface.BOLD);
			TextView comment = new TextView(this);
			LinearLayout.LayoutParams commentParams = new LinearLayout.LayoutParams(
				     LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			commentParams.setMargins(5, 0, 0, 0);		
			comment.setLayoutParams(commentParams);
			comment.setPadding(5, 5, 2, 0);
			comment.setText(comments.get(i).getComment());
			lv.addView(user_name);
			lv.addView(comment);			
		}
	}

	public void setComments(JSONArray comments)
	{
		ArrayList<Comment> comment_list = new ArrayList<Comment>();
		for(int i=0;i<comments.length();i++)
		{
			comment_list.add(new Comment(comments.optJSONObject(i)));
			System.out.println(comment_list.get(i).getComment());
		}
		this.comments = comment_list;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.comment, menu);
		return true;
	}

	public void postComment(View v){
		
		JSONObject data = new JSONObject();
		try{		
		data.put("access_token", Session.getActiveSession().getAccessToken());//post
		System.out.println(((EditText)findViewById(R.id.comment_box)).getText());
		data.put("text",((EditText)findViewById(R.id.comment_box)).getText());
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
						try{
							System.out.println(this.mData.get("text"));
							add_comment((this.mData.get("text")).toString());
						}catch(Exception e)
						{
							e.printStackTrace();
						}
						
					}
				}
			}
		};
		System.out.println("/user/comment/"+getIntent().getExtras().getInt("notification_id"));
		asyncHttpPost.execute(getString(R.string.base_url)+"/user/comment/"+getIntent().getExtras().getInt("notification_id"));

	}
	
	public void add_comment(String comment_text)
	{
		LinearLayout lv = (LinearLayout)findViewById(R.id.comments_list);
		TextView user_name = new TextView(this);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
			     LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(5, 2, 0, 0);		
		user_name.setLayoutParams(layoutParams);
		user_name.setPadding(5, 5, 2, 0);
		user_name.setText(Main.current_user.name);
		
		user_name.setTextColor(this.getResources().getColor(edu.ncsu.stockman.R.color.kulur_purple));
		user_name.setTypeface(null, Typeface.BOLD);
		TextView comment = new TextView(this);
		//tv.setId(comments.get(0).getId());
		LinearLayout.LayoutParams commentParams = new LinearLayout.LayoutParams(
			     LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		commentParams.setMargins(5, 0, 0, 0);		
		comment.setLayoutParams(commentParams);
		comment.setPadding(5, 5, 2, 0);
		comment.setText(comment_text);
		lv.addView(user_name);
		lv.addView(comment);
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
