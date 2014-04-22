package edu.ncsu.stockman;

import java.io.InputStream;
import java.net.HttpURLConnection;

import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.User;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    User user;

    private DownloadImageTask(ImageView bmImage, User user) {
        this.bmImage = bmImage;
        this.user = user;
    }
    
    static public void setFacebookImage(ImageView bmImage, User user){
    	if(user.picture!=null)
    		bmImage.setImageBitmap(user.picture);
    	else{
    		new DownloadImageTask(bmImage, user).execute("https://graph.facebook.com/"+user.facebook_id+"/picture?type=square");
    	}
    }

    
    @Override
	protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
            user.picture = mIcon11;
        } catch (Exception e) {
            Log.e("Error in downloading image", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    @Override
	protected void onPostExecute(Bitmap result) {
    	bmImage.setScaleType(ScaleType.FIT_XY);
        bmImage.setImageBitmap(result);
    }
}