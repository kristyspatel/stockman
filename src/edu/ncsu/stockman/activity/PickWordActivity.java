package edu.ncsu.stockman.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import edu.ncsu.stockman.R;
import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.Player;

public class PickWordActivity extends Activity {

	String[] dict; 
	EditText[] editTexts = new EditText[Main.wordLength];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pick_word);
		
		// get dictionary file
		if(dict==null){
			readDictFile();
		}
		
		//add EditText fields
		LinearLayout l = (LinearLayout) findViewById(R.id.pick_myword);
		
		for (int i = 0; i < Main.wordLength; i++) {
			EditText e = new EditText(this);
			e.setTextAppearance(this, R.style.pickword_letter);
			e.setLayoutParams(new LinearLayout.LayoutParams(20,LayoutParams.WRAP_CONTENT,1f));
			
			InputFilter[] filterArray = new InputFilter[1];
			filterArray[0] = new InputFilter.LengthFilter(1);
			e.setFilters(filterArray);
			editTexts[i] = e;
			l.addView(e);
		}
		for (int i = 0; i < Main.wordLength; i++) {
			editTexts[i].addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					if(s.length() == 1){
						EditText e = (EditText) getCurrentFocus();
						for (int i = 0; i < Main.wordLength; i++) {
							if(e == editTexts[i]){
								if(i+1 < Main.wordLength){
									editTexts[i+1].setSelection(editTexts[i+1].getText().length());
									editTexts[i+1].requestFocus();
								}
								break;
							}
						}
					}
					if(s.length() == 0){
						EditText e = (EditText) getCurrentFocus();
						for (int i = 0; i < Main.wordLength; i++) {
							if(e == editTexts[i]){
								if(i-1 >= 0){
									editTexts[i-1].setSelection(editTexts[i-1].getText().length());
									editTexts[i-1].requestFocus();
								}
								break;
							}
						}
					}
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
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nomenu, menu);
		return true;
	}

	public void submit(View v){
		String s = "";
		for (int i = 0; i < Main.wordLength; i++) {
			//editTexts[i].clearFocus();
			s += editTexts[i].getText();
		}
		
		if(s.length() != Main.wordLength){
			System.err.println("word length is not correct! [Impossible]");
			return;
		}
		
		//hide the keyboard
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
		
		boolean b = false;
		for (int i = 0; i < dict.length; i++) {
			if(s.equals(dict[i])){
				b = true;
				break;
			}
		}
		
		if(!b){
			Toast toast = Toast.makeText(this, "The word has to be a dictionary word", Toast.LENGTH_LONG);
			toast.show();
			return;
		}
		else{
			Main.current_game.me.word = s.toCharArray();
			Player.setWord(this,s);
		}
			
	}
	public void random(View v){
		if(dict == null){
			System.err.println("no dict");
			return;
		}
		Random r = new Random();
		String word = dict[r.nextInt(dict.length)];
		
		for (int i = 0; i < Main.wordLength; i++) {
			editTexts[i].setText(word.charAt(i)+"");
		}
	}
	
	private void readDictFile(){
		try {
			Scanner input = new Scanner(getResources().getAssets().open("6letters.dict"));
			//assume first line has number of lines
			dict = new String[input.nextInt()];
			for (int j = 0; j < dict.length; j++) {
				dict[j] = input.next(); 
			}
			input.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
