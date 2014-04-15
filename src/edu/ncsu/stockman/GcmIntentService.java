package edu.ncsu.stockman;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import edu.ncsu.stockman.activity.MainActivity;
import edu.ncsu.stockman.model.Comment;
import edu.ncsu.stockman.model.Friend;
import edu.ncsu.stockman.model.Friend.Friendship_status;
import edu.ncsu.stockman.model.Game;
import edu.ncsu.stockman.model.Main;
import edu.ncsu.stockman.model.Notification;
import edu.ncsu.stockman.model.Player;
import edu.ncsu.stockman.model.Player.Player_status;
import edu.ncsu.stockman.model.User;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    static final String TAG = "GCMService";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Post notification of received message.
                //sendNotification("Received: " + extras.toString());
                Log.i(TAG, "Received: " + extras.toString());
                
                doAction(extras);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void doAction(Bundle extras) {
    	final String TAG = "DoAction";
    	final String action = extras.getString("action");
    	JSONObject data,user;
		try {
			data = new JSONObject(extras.getString("data"));
			user = new JSONObject(extras.getString("user"));
			
			
	        if(action.equals("PICK_WORD")){
	        	Game g = Main.current_user.games.get(data.getInt("id_game"));
	        	if(g==null){
	        		Log.w(TAG, "The game is not available."+data.getInt("id_game"));
	        		return;
	        	}
	        	Player p = g.players.get(data.getInt("id_player"));
	        	if(p==null){
	        		Log.w(TAG, "The player is not available."+data.getInt("id_player"));
	        		return;
	        	}
	        	p.status = Player_status.ENROLLED;
	        	g.player_status_change = true;
	        	Log.i(TAG, "The player status has been changed");
	        }
	        
	        else if(action.equals("ACCEPT")){
	        	Game g = Main.current_user.games.get(data.getInt("id_game"));
	        	if(g==null){
	        		Log.w(TAG, "The game is not available."+data.getInt("id_game"));
	        		return;
	        	}
	        	Player p = g.players.get(data.getInt("id_player"));
	        	if(p==null){
	        		Log.w(TAG, "The player is not available."+data.getInt("id_player"));
	        		return;
	        	}
	        	p.status = Player_status.WAITING_FOR_WORD;
	        	g.player_status_change = true;
	        	Log.i(TAG, "The player status has been changed");
	        }
	        
	        else if(action.equals("DECLINE")){
	        	Game g = Main.current_user.games.get(data.getInt("id_game"));
	        	if(g==null){
	        		Log.w(TAG, "The game is not available."+data.getInt("id_game"));
	        		return;
	        	}
	        	Player p = g.players.get(data.getInt("id_player"));
	        	if(p==null){
	        		Log.w(TAG, "The player is not available."+data.getInt("id_player"));
	        		return;
	        	}
	        	p.status = Player_status.OUT;
	        	g.player_status_change = true;
	        	Log.i(TAG, "The player status has been changed");
	        }
	        
	        else if(action.equals("COMMENT")){
	        	Notification n = Main.current_user.notifications.get(data.getInt("id_notification"));
	        	User u = Main.users.get(data.getInt("id_user"));
	        	if (n==null){
	        		Log.w(TAG, "Comment was not added because the notification object is not fetched");
	        	}
	        	else{
	        		if(u==null){
		        		u = new User(user);
		        		Main.users.put(u.id, u);
		        	}
	        		n.comments.add(new Comment(u, data.getString("text"), data.getInt("id_comment"), n));
	        		n.new_comment =true;
	        	}
	        }
	        
	        else if(action.equals("ACCEPT_FRIEND")){
	        	if(data.getInt("id_user1")==Main.current_user.id){
	        		Main.current_user.facebook_friends.get(data.getInt("id_user2")).friendship_status = Friendship_status.ACCEPTED;
	        		Friend f = Main.current_user.facebook_friends.get(data.getInt("id_user2"));
	        		Main.current_user.friends.put(f.id, f.user);
	        		Main.current_user.new_friend = true;
	        	}
	        }
	        else if(action.equals("CANCEL_FRIEND")){
	        	if(data.getInt("id_user1")==Main.current_user.id){
	        		Main.current_user.facebook_friends.get(data.getInt("id_user2")).friendship_status = Friendship_status.CANCELLED;
	        		Main.current_user.new_friend = true;
	        	}
	        }
	        
	        else if(action.equals("CREATE_GAME")){
	        	//TODO
	        	//Main.current_user.games.put(b.getInt("id_game"), new Game(b.getInt("id_game"), b.getString("name")));
	        }
	        
	        else if(action.equals("GUESS")){
	        	JSONObject me = data.getJSONObject("me");
	        	JSONObject him = data.getJSONObject("him");
	        	Game g = Main.current_user.games.get(data.getInt("id_game"));
	        	if(g==null){
	        		Log.w(TAG, "Game not found for action GUESS"+data.getInt("id_game"));
	        		return;
	        	}
	        	if(data.getBoolean("correct")){
	        		Player p = g.players.get(him.getInt("id_player"));
	        		if(him.getInt("status")==4){
	        			p.die_date = him.getInt("die_date");
	        			p.status= Player_status.OUT;
	        			g.player_status_change =true;
	        		}
	        		
	        		Player pp = g.players.get(me.getInt("id_player"));
	        		if(me.getInt("status")==4){
	        			pp.die_date = me.getInt("die_date");
	        			pp.status= Player_status.OUT;
	        			g.player_status_change =true;
	        		}
	        		
	        		p.setRevealedWord(data.getInt("word_revealed"));
	        		p.new_letter_revealed = true;
	        	}
	        }
	        sendNotification(Notification.getText(getApplicationContext(), action, data));
        
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
        
    }
    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.stockman_icon)
        .setContentTitle("StockMan")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        Log.i(TAG,msg); 
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}