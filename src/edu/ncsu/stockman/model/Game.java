package edu.ncsu.stockman.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.facebook.Session;

import edu.ncsu.stockman.R;
import edu.ncsu.stockman.activity.MainGameActivity;
import edu.ncsu.stockman.activity.PickWordActivity;
import edu.ncsu.stockman.model.Player.Player_status;

public class Game {

	public String name;
	public int id;
	public int id_creator;
	public Player me;
	public SparseArray<Player> players = new SparseArray<Player>();;
	
	//GCM
	public boolean player_status_change = false;
	
	public Game(JSONObject info) {
		super();
		try {
			this.name = info.getString("name");
			this.id = info.getInt("id_game");
			//this.me = new Player(info,this);
			this.id_creator = info.getInt("creator_id_user");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Game(int id, String name) {
		super();
		this.name = name;
		this.id = id;
	}
	public boolean isOver(){
		int over = 0;
		for (int i = 0; i < players.size(); i++) {
			Player p = players.valueAt(i);
			if(p.status==Player_status.LOST || p.status == Player_status.DECLINED){
				over++;
			}
		}
		if(over == players.size()-1){
			return true;
		}
		return false;
	}
	public void setPlayers(JSONArray players){
		for (int i = 0; i < players.length(); i++) {
			try {
				JSONObject player = players.getJSONObject(i);
				Player p = new Player(player,this);
				if(p.user.id == Main.current_user.id)
					me = p;
				this.players.put(p.id, p);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(me==null)
			Log.e("Model","me in a game is null?!");
		//setting guesses needs all players to be set up.
		for (int i = 0; i < players.length(); i++) {
			try {
				JSONObject player = players.getJSONObject(i);
				this.players.get(player.getInt("id_player")).setGuesses(player.getJSONArray("guesses"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Server side functions
	 */
	public static void createGame(Context c, ArrayList<String> members,String name){
		JSONObject data = new JSONObject();
		try{	
			data.put("access_token",  Session.getActiveSession().getAccessToken());
			data.put("members", members);
			data.put("name",name);
		}catch(JSONException e){
			e.printStackTrace();
		}

		MidLayer asyncHttpPost = new MidLayer(data,c,true) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				if(result.info.code == 10){
					//System.out.println(result.info.text);
					Toast toast = Toast.makeText(context, "A new game has been created.", Toast.LENGTH_LONG);
					toast.show();
					((Activity) context).finish();
					
					Main.current_user.new_game = true;
					JSONObject data;
					try {
						data = new JSONObject(result.info.text);
						Game g =  new Game(data.getJSONObject("info"));
						g.setPlayers(data.getJSONArray("players"));
						Main.current_user.games.put(g.id,g);
						Main.current_game = g;
						Intent intent = new Intent(context, PickWordActivity.class);
						context.startActivity(intent);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};		
		asyncHttpPost.exec(c.getString(R.string.base_url)+"/game/create");

	}
	@Deprecated
	public static void fetchGame(Context c){
		// fetch the game information before switching to the activity.
		JSONObject data = new JSONObject();
		try{
			data.put("access_token", Session.getActiveSession().getAccessToken());//post
		}catch(JSONException e){
			e.printStackTrace();
		}
		MidLayer asyncHttpPost = new MidLayer(data,c,true) {
			@Override
			protected void resultReady(MidLayer.Result result) {
				if(result.info.code == 10){
					//try {
						//JSONObject j = new JSONObject(result.info.text);
						
						//Main.current_game.setPlayers(j.getJSONArray("players"));
						//Main.current_game.me = Main.current_game.players.get(j.getInt("me_id"));
						//Main.current_player = me;
						
						if(Main.current_game.me.status == Player.Player_status.WAITING_FOR_WORD){
							Intent intent = new Intent(context, PickWordActivity.class);
							context.startActivity(intent);
						}
						else if(Main.current_game.me.status == Player.Player_status.ENROLLED){
							Intent intent = new Intent(context, MainGameActivity.class);
							context.startActivity(intent);
						}
						else if(Main.current_game.me.status == Player.Player_status.LOST || Main.current_game.me.status == Player.Player_status.WON){
							//TODO lang
							Intent intent = new Intent(context, MainGameActivity.class);
							context.startActivity(intent);
							Toast toast = Toast.makeText(context, "This game is now over.", Toast.LENGTH_LONG);
							toast.show();
						}
						
					//} catch (JSONException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					//}
				}
			}
		};
		asyncHttpPost.exec(c.getString(R.string.base_url)+"/game/get/"+Main.current_game.id);
	}
}
