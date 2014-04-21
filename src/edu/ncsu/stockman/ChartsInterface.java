package edu.ncsu.stockman;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.ncsu.stockman.model.Company;
import edu.ncsu.stockman.model.Game;
import edu.ncsu.stockman.model.Player;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class ChartsInterface {

	Context mContext;
	Company company;
	Player player;
	Game game;

	/** Instantiate the interface and set the context */
	public ChartsInterface(Context c, Company cc) {
		mContext = c;
		company = cc;
	}

	public ChartsInterface(Context c, Player p) {
		mContext = c;
		player = p;
	}
	
	public ChartsInterface(Context c, Game g) {
		mContext = c;
		game = g;
	}

	/** Show a toast from the web page */
	@JavascriptInterface
	public void showToast(String toast) {
		System.out.println(toast);
		if (toast.length() < 100)
			Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
	}

	@JavascriptInterface
	public double getCash() {
		return player.cash; // last hour
	}

	@JavascriptInterface
	public double getAssets() {
		return player.getAssets(); // last hour
	}
	
	@JavascriptInterface
	public String getGameGuesses() throws JSONException {
		List<Integer> correct = new ArrayList<Integer>();
		List<Integer> incorrect = new ArrayList<Integer>();
		for (int j = 0; j < game.players.size(); j++) {
			int count = 0, icount = 0;
			Player p = game.players.valueAt(j);
			for (int i = 0; i < p.guesses.size(); i++) {
				if (p.guesses.get(i).correct)
					count++;
				else
					icount++;
			}
			correct.add(count);
			incorrect.add(icount);
		}
		return new JSONObject().put("correct", new JSONArray(correct)).put("incorrect", new JSONArray(incorrect)).toString();
	}
	@JavascriptInterface
	public int getIncorrectGuesses() {
		int count = 0;
		for (int i = 0; i < player.guesses.size(); i++) {
			if (!player.guesses.get(i).correct)
				count++;
		}
		return count;
	}

	@JavascriptInterface
	public String getPlayersNames() {
		List<String> names = new ArrayList<String>();
		for (int j = 0; j < game.players.size(); j++) {
			names.add(game.players.valueAt(j).name);
		}
		return new JSONArray(names).toString();
	}
	@JavascriptInterface
	public int getCorrectGuesses() {
		int count = 0;
		for (int i = 0; i < player.guesses.size(); i++) {
			if (player.guesses.get(i).correct)
				count++;
		}
		return count;
	}

	@JavascriptInterface
	public int getFrom() {
		return 60; // last hour
	}

	@JavascriptInterface
	public int getTimeStamp() {
		return company.getTimeStamp();
	}
	@JavascriptInterface
	public long getDiffTime() {
		return TimeZone.getDefault().getOffset(new Date().getTime())/1000;
	}

	@JavascriptInterface
	public String getPrices() {

		List<Float> intList = new ArrayList<Float>();
		for (int i = 0; i <= company.getTimeStamp(); i++) {
			intList.add(company.price[i]);
		}

		return new JSONArray(intList).toString();
	}

}