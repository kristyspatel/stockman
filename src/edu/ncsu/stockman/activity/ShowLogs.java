package edu.ncsu.stockman.activity;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.ncsu.stockman.ChartsInterface;
import edu.ncsu.stockman.DownloadImageTask;
import edu.ncsu.stockman.R;
import edu.ncsu.stockman.model.Activityy;
import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.Player;

public class ShowLogs extends Activity {

	public Player me;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_logs);
		
		me = Main.current_game.players.get(getIntent().getExtras().getInt("player_id"));
		

//		WebView guesses = (WebView)findViewById(R.id.gusses); 
//		guesses.getSettings().setJavaScriptEnabled(true);
//		guesses.setPadding(0, 0, 0, 0);
//		guesses.addJavascriptInterface(new ChartsInterface(this,Main.current_game), "and_data");
//		//priceFluctuationGraph.loadUrl("file:///android_asset/googlecharts.html");
//		guesses.loadUrl("file:///android_asset/chartsjs_guesses.html");
		
		//commented just to try charts here
		Player.getLogs(this, me.id);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nomenu, menu);
		return true;
	}
	
	private int dumbFunction(String s){
		if(s.equals("GUESS")){
			return R.style.guess_log;
		}
		else if(s.equals("REVEALED")){
			return R.style.reveal_log;
		}
		else if( s.equals("REVEALED_INCORRECT")){
			return R.style.reveal_incorrect_log;
		}
		else if(s.equals("GUESS_INCORRECT")){
			return R.style.guess_incorrect_log;
		}else if( s.equals("HANG")){
			return R.style.hanged_log;
		}else if(  s.equals("DIED" )){
			return R.style.died_log;
		}else if(  s.equals("OPEN") ){
			return R.style.open_log;
		}else if(  s.equals("INVITE") ){
			return R.style.invite_log;
		}else if(  s.equals("BUY" )){
			return R.style.buy_log;
		}else if(  s.equals("SELL" )){
			return R.style.sell_log;
		}else if(  s.equals("ACCEPT" )){
			return R.style.accept_log;
		}else if(  s.equals("PICKWORD" )){
			return R.style.pickword_log;
		}
	
		System.out.println(s);
		return -1;
	}
	public void setLogs(){
		LinearLayout l = (LinearLayout) findViewById(R.id.show_logs);
		
		for (int i = 0; i < me.logs.size(); i++) {
			RelativeLayout v = (RelativeLayout) getLayoutInflater().inflate(R.layout.log_record, l,false);
			ImageView r = (ImageView) v.findViewById(R.id.log_img);
			
			Activityy log = me.logs.get(i);
			
			if(
					log.subject.equals("GUESS") || 
					log.subject.equals("REVEALED") || 
					log.subject.equals("REVEALED_INCORRECT") || 
					log.subject.equals("GUESS_INCORRECT") || 
					log.subject.equals("HANG") || 
					log.subject.equals("DIED") 
					){
				if(log.extra != "null")
					DownloadImageTask.setFacebookImage(r,Main.current_game.players.get(Integer.valueOf(log.extra)).user);
			}
			else if (log.subject.equals("BUY") || log.subject.equals("SELL")){
				if(log.extra != "null"){
					r.setImageDrawable(getResources().getDrawable(Main.companies.get(Integer.valueOf(log.extra)).getPictureResourceID()));
					r.setScaleType(ScaleType.FIT_XY);
				}
			}
			else if(log.subject.equals("OPEN"))
				continue;
			
			Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			c.setTimeInMillis((long)log.date*1000);
			c.setTimeZone(TimeZone.getDefault());
			
			TextView t = (TextView) v.findViewById(R.id.log_time);
			t.setText(c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) );
			t.setTextAppearance(this, dumbFunction(log.subject));

			t = (TextView) v.findViewById(R.id.log_date);
			t.setText(new SimpleDateFormat("EE").format(c.getTime()) + " " + c.get(Calendar.DAY_OF_MONTH) );
			t.setTextAppearance(this, dumbFunction(log.subject));
			
			t = (TextView) v.findViewById(R.id.log_text);
			t.setText(log.text);
			t.setTextAppearance(this, dumbFunction(log.subject));
			
			l.addView(v);
		}
	}

}
