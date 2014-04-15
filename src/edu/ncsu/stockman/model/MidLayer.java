package edu.ncsu.stockman.model;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

abstract public class MidLayer extends AsyncTask<String,Integer, JSONObject>{
	
	public JSONObject mData = null;
	public Bundle bundle = new Bundle();
	boolean waiting;
	public View view; //the view that 
	//private HashMap<String, String> mData = null;// post data
	ProgressDialog progress;
	public Context context;

    //public MidLayer(HashMap<String, String> data, Context c) {
	public MidLayer(JSONObject data, Context c,boolean waiting) {
        mData = data;
        context = c;
        this.waiting = waiting;
    }
	
    

	public void exec(String url){
		System.out.println(url);
		execute(url);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(waiting)
			progress = ProgressDialog.show(context, "Contacting Server",
					"Please wait..", true);
	}
	@Override
	protected JSONObject doInBackground(String... params) {
		byte[] result = null;
		JSONObject json = null;
        String str = "";
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(params[0]);// in this case, params[0] is URL
        try {
            // set up post data
            ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            Iterator<String> it = mData.keys();
            while (it.hasNext()) {
                String key = it.next();
                nameValuePair.add(new BasicNameValuePair(key, mData.get(key).toString()));
            }
        	//mData.keys()
        	//StringEntity se = new StringEntity(mData.toString());
        	//se.setContentType("application/json");
        	//post.setEntity(se);
            post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
            HttpResponse response = client.execute(post);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK){
                result = EntityUtils.toByteArray(response.getEntity());
                str = new String(result, "UTF-8");
                json= new JSONObject(str);
            }
            else{
            	Log.e("ServerError", new String(EntityUtils.toByteArray(response.getEntity()),"UTF-8"));
            }
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
        	System.out.print("Not correctly formatted JSON. The output is:");
        	System.out.println(str);
        	e.printStackTrace();
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        return json;
	}
	@Override
	protected void onPostExecute(JSONObject result){
		if (result == null){
			if(waiting)
				progress.dismiss();
			
			System.out.println("No proper result from the server.");
			return;
		}
		Result r = new Result();
		if(result.optJSONObject("error") != null){
			r.error = new Content(result.optJSONObject("error").optInt("code"),
					result.optJSONObject("error").optString("context"),
					result.optJSONObject("error").optString("text"),
					TYPE.ERROR);
			//TODO show the result
			Toast toast = Toast.makeText(context, "Error "+r.error.code+": "+r.error.text, Toast.LENGTH_LONG);
			toast.show();
			
			if(waiting)
				progress.dismiss();
			return;
		}
		if(result.optJSONObject("debug") != null)
			r.debug = new Content(result.optJSONObject("debug").optInt("code"),
					result.optJSONObject("debug").optString("context"),
					result.optJSONObject("debug").optString("text"),
					TYPE.DEBUG);
		if(result.optJSONObject("info") != null)
			r.info = new Content(result.optJSONObject("info").optInt("code"),
					result.optJSONObject("info").optString("context"),
					result.optJSONObject("info").optString("text"),
					TYPE.INFO);
		if(result.optJSONObject("notify") != null){
			Notification n = new Notification(result.optJSONObject("notify"));
			Main.current_user.notifications.put(n.id_notification, n);
			Main.current_user.new_notification = true;
		}
			
		if(waiting)
			progress.dismiss();
		resultReady(r);
	}
	
	abstract protected void resultReady(Result result);

	public class Result{
		public TYPE type;
		public Content error;
		public Content debug;
		public Content info;
	}
	public class Content{
		public int code;
		public String context;
		public String text;
		public TYPE type;
		
		public Content(int code, String context, String text, TYPE type) {
			super();
			this.code = code;
			this.context = context;
			this.text = text;
			this.type = type;
		}
		
	}
}
enum TYPE{
	ERROR,
	DEBUG,
	INFO
};