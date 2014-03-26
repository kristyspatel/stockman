package edu.ncsu.stockman.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.facebook.Session;
import edu.ncsu.stockman.MainGameActivity;
import edu.ncsu.stockman.PickWordActivity;
import edu.ncsu.stockman.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;
import android.widget.Toast;

public class Game {

	public String name;
	public int id;
	public Player creator;
	public Player me;
	public SparseArray<Player> players = new SparseArray<Player>();;
	
	public Game(JSONObject info) {
		super();
		try {
			this.name = info.getString("name");
			this.id = info.getInt("id_game");
			this.me = new Player(info);
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
	static boolean selectGame(int id)
	{
		Game g = Main.current_user.games.get(id);
		if (g == null)
			return false;
		Main.current_game = g;
		Main.current_player = g.players.get(Main.current_user.id);
		return true;
	}
	public void setPlayers(JSONArray players){
		for (int i = 0; i < players.length(); i++) {
			try {
				JSONObject player = players.getJSONObject(i);
				Player p = new Player(player);
				this.players.append(p.id, p);
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
				if(result.info.code == 0){
					//System.out.println(result.info.text);
					Toast toast = Toast.makeText(context, "A new game has been created.", Toast.LENGTH_LONG);
					toast.show();
					((Activity) context).finish();	
				}
			}
		};		
		asyncHttpPost.exec(c.getString(R.string.base_url)+"/game/create");

	}
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
					try {
						JSONObject j = new JSONObject(result.info.text);
						Player me = new Player(j.getJSONObject("me"));
						
						Main.current_game.setPlayers(j.getJSONArray("players"));
						
						Main.current_player = me;
						
						if(me.status == Player.Player_status.WAITING_FOR_WORD){
							Intent intent = new Intent(context, PickWordActivity.class);
							context.startActivity(intent);
						}
						else if(me.status == Player.Player_status.ENROLLED){
							Intent intent = new Intent(context, MainGameActivity.class);
							context.startActivity(intent);
						}
						else if(me.status == Player.Player_status.OUT){
							//TODO lang
							Toast toast = Toast.makeText(context, "Sorry, but your are out of this game.", Toast.LENGTH_LONG);
							toast.show();
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		asyncHttpPost.exec(c.getString(R.string.base_url)+"/game/get/"+Main.current_game.id);
	}
}
