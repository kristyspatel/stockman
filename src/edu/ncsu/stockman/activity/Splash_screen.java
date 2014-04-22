package edu.ncsu.stockman.activity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;

import edu.ncsu.stockman.R;

public class Splash_screen extends FacebookActivity{

	private ViewPager mPager;
	private static final int NUM_PAGES = 8;
	LayoutInflater inflater;	//Used to create individual pages
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		TextView tagline = (TextView)findViewById(R.id.tagline);
		Typeface externalFont = Typeface.createFromAsset(getAssets(), "fonts/Comic Sans.ttf");
		tagline.setTypeface(externalFont);
		try {
	        PackageInfo info =     getPackageManager().getPackageInfo("com.package.mypackage",     PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	            MessageDigest md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            String sign=Base64.encodeToString(md.digest(), Base64.DEFAULT);
	            Log.e("MY KEY HASH:", sign);
	            Toast.makeText(getApplicationContext(),sign,     Toast.LENGTH_LONG).show();
	        }
	} catch (NameNotFoundException e) {
	} catch (NoSuchAlgorithmException e) {
	}	
		// check if user is already logged in
		if (Session.getActiveSession() != null && Session.getActiveSession().isOpened()){
	    	Intent intent = new Intent(this, Timeline.class);
			startActivityForResult(intent, 101);
		}
		
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPager = (ViewPager) findViewById(R.id.pager);
		for (int i = 0; i < NUM_PAGES; i++) {
			View view = getLayoutInflater().inflate(R.layout.fragment_screen_slide_page, mPager,false);
			mPager.addView(view);
		}
		mPager.setAdapter(new MyPagesAdapter());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nomenu, menu);
		return true;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		
		if (requestCode == 101) {
			System.out.println(requestCode + ","+resultCode);
	        if (resultCode == 1) 
	           this.finish();
	    }
	}
	
	@Override
	void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
	    if (state.isOpened()) {
	    	Intent intent = new Intent(this, Timeline.class);
	    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	startActivityForResult(intent, 101);
	    }
	}
	
	
	//Implement PagerAdapter Class to handle individual page creation
    class MyPagesAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            //Return total pages, here one for each data item
            return NUM_PAGES;
        }
        //Create the given page (indicated by position)
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View page = inflater.inflate(R.layout.fragment_screen_slide_page, null);
            switch (position+1) {
			case 1:
				((ImageView)page.findViewById(R.id.intro_image)).setImageResource(R.drawable.intro1);;
				break;
			case 2:
				((ImageView)page.findViewById(R.id.intro_image)).setImageResource(R.drawable.intro2);;
				break;
			case 3:
				((ImageView)page.findViewById(R.id.intro_image)).setImageResource(R.drawable.intro3);;
				break;
			case 4:
				((ImageView)page.findViewById(R.id.intro_image)).setImageResource(R.drawable.intro4);;
				break;
			case 5:
				((ImageView)page.findViewById(R.id.intro_image)).setImageResource(R.drawable.intro5);;
				break;
			case 6:
				((ImageView)page.findViewById(R.id.intro_image)).setImageResource(R.drawable.intro6);;
				break;
			case 7:
				((ImageView)page.findViewById(R.id.intro_image)).setImageResource(R.drawable.intro7);;
				break;
			case 8:
				((ImageView)page.findViewById(R.id.intro_image)).setImageResource(R.drawable.intro8);;
				break;
			default:
				break;
			}
            //Add the page to the front of the queue
            ((ViewPager) container).addView(page, 0);
            return page;
        }
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            //See if object from instantiateItem is related to the given view
            //required by API
            return arg0==(View)arg1;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
          ((ViewPager) container).removeView((View) object);
          object=null;
        }
    }

}
