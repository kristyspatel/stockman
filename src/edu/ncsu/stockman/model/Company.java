package edu.ncsu.stockman.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.facebook.Session;

import edu.ncsu.stockman.R;

public class Company {

	public float[] price = new float[60*24*2];
	public String name;
	public String abbr;
	public String pic; // TODO
	public int id;

	public static SparseArray<Company> list= new SparseArray<Company>();
	
//	static{
//		list.put(1,new Company(1,  "Black"));
//		list.put(2,new Company(2,  "White"));
//		list.put(3,new Company(3,  "Blue"));
//	}

	public Company( int id, String name) {
		super();
		this.name = name;
		this.id = id;
	}
	public Company( JSONObject j) {
		super();
		try {
			this.name = j.getString("company_name");
			this.id = j.getInt("id_company");
			this.abbr = j.getString("abbr");
			this.pic = j.getString("pic");
			JSONArray prices = j.getJSONArray("price");
			
			for (int i = 0; i < prices.length(); i++) {
				this.price[i] = (float) prices.getDouble(i);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public float getPrice(){	   	 
	   	 return price[getTimeStamp()];
	}
	public float getPrevPrice(){		
		return price[getTimeStamp()-1];
	}
	
	public int getPictureResourceID(){
		
		if(abbr.equals("AAPL"))
			return R.drawable.apple;
		else if(abbr.equals("MSFT"))
			return R.drawable.microsoft;
		else if(abbr.equals("GOOG"))
			return R.drawable.google;
		else if(abbr.equals("TWTR"))
			return R.drawable.twitter;
		else if(abbr.equals("HSY"))
			return R.drawable.hershey;
		else if(abbr.equals("KO"))
			return R.drawable.cocacola;
		else if(abbr.equals("MCD"))
			return R.drawable.mcdonalds;
		else if(abbr.equals("BKW"))
			return R.drawable.burger_king;
		else if(abbr.equals("SNE"))
			return R.drawable.sony;
		return -1;
	}
	public int getTimeStamp(){
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		int h = c.get(Calendar.HOUR_OF_DAY);
		int m = c.get(Calendar.MINUTE);
		int s = c.get(Calendar.SECOND);
		return (h*60*2+m*2+(s<30?0:1));
	}
	
	
	/**
	 * Server side functions
	 */
	
	public static void getPrices(Context c){

		//check if there is a file in the device
		
		boolean isFileExist = false;
		FileInputStream fos;
		try {
			fos = c.openFileInput("stocks");
			String s = "";
			byte[] buffer = new byte[1024]; 
			while (fos.read(buffer,0,buffer.length)!=-1){
				String str = new String(buffer, "UTF-8");
				s+=str;
			}
			JSONArray j = new JSONArray(s);
			for (int i = 0; i < j.length(); i++) {
				Company company = new Company(j.getJSONObject(i));
				Main.companies.put(company.id, company);
			}
			Log.i("StockMan", "Companies prices fetched from the local file in the device");
			fos.close();
			isFileExist = true;
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			isFileExist = false;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			System.err.println("The file stored in the local system for stockmarket is currupted (not in JSON format)");
			e.printStackTrace();
		}

		if(!isFileExist){
			JSONObject data = new JSONObject();
			try{		
				data.put("access_token", Session.getActiveSession().getAccessToken());//post
			}catch(JSONException e)
			{
				e.printStackTrace();
			}
			
			MidLayer asyncHttpPost2 = new MidLayer(data,c,true) {
				@Override
				protected void resultReady(MidLayer.Result result) {
					if(result.info.code == 0){
						
						try {
							FileOutputStream fos;
							try {
								fos = context.openFileOutput("stocks", Context.MODE_PRIVATE);
								fos.write(result.info.text.getBytes());
								fos.close();
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Log.i("StockMan", "Companies prices stored in the device");
							
							JSONArray j = new JSONArray(result.info.text);
							for (int i = 0; i < j.length(); i++) {
								Company c = new Company(j.getJSONObject(i));
								Main.companies.put(c.id, c);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			};	
			asyncHttpPost2.exec(c.getString(R.string.base_url)+"/stockmarket/get");
		}
	}
}
