package edu.ncsu.stockman;

import java.util.concurrent.atomic.AtomicInteger;

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

import edu.ncsu.stockman.activity.Timeline;
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
	        	p.word = data.getString("word").toCharArray();
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
	        	p.status = Player_status.DECLINED;

	        	//check if the creator is the only one playing.
	        	if(g.isOver())
		        	for (int i = 0; i < g.players.size(); i++) {
		        		Player pla = g.players.valueAt(i);
		        		if(g.id_creator == pla.user.id)
		        			pla.status = Player_status.DECLINED;;
					}
	        	g.player_status_change = true;
	        	Main.current_user.new_game = true;
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
	        		Friend f = Main.current_user.facebook_friends.get(data.getInt("id_user2"));
	        		if (f!=null){
		        		f.friendship_status = Friendship_status.ACCEPTED;
		        		Main.current_user.friends.put(f.id, f.user);
		        		Main.current_user.new_friend = true;
	        		}
	        	}
	        }
	        else if(action.equals("CANCEL_FRIEND")){
	        	if(data.getInt("id_user1")==Main.current_user.id){
	        		Friend f = Main.current_user.facebook_friends.get(data.getInt("id_user2"));
	        		if (f!=null){
	        			f.friendship_status = Friendship_status.CANCELLED;
	        			Main.current_user.new_friend = true;
	        		}
	        	}
	        }
	        else if(action.equals("ADD_FRIEND")){
	        	if(data.getInt("id_user2")==Main.current_user.id){
	        		Friend f = Main.current_user.facebook_friends.get(data.getInt("id_user1"));
	        		if (f!=null){
	        			f.friendship_status = Friendship_status.PENDING;
	        			Main.current_user.new_friend = true;
	        		}
	        	}
	        }
	        
	        else if(action.equals("CREATE_GAME")){
	        	//TODO
	        	Game g = new Game(data.getJSONObject("info"));
	        	g.setPlayers(data.getJSONArray("players"));
	        	Main.current_user.games.put(g.id,g);
	        	Main.current_user.new_game = true;
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
	        		if(Player.getStatus(him.getInt("status"))==Player_status.LOST){
	        			p.die_date = him.getInt("die_date");
	        			p.status= Player_status.LOST;
	        			g.player_status_change =true;
	        			Main.current_user.new_game = true;
	        		}
	        		
	        		Player pp = g.players.get(me.getInt("id_player"));
	        		if(Player.getStatus(me.getInt("status"))==Player_status.WON){
	        			pp.die_date = me.getInt("die_date");
	        			pp.status= Player_status.WON;
	        			g.player_status_change =true;
	        			Main.current_user.new_game = true;
	        		}
	        		
	        		p.word_revealed = p.getRevealedWord(data.getInt("word_revealed"));
	        		p.new_letter_revealed = true;
	        	}
	        }
	        sendNotification(Notification.getText(getApplicationContext(), action, data));
        
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		Log.i(TAG, "New Notification:"+extras.toString());
    }
    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    AtomicInteger uniqueGenereator = new AtomicInteger();
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, Timeline.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.stockman_notification_icon)
        .setContentTitle("StockMan")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        Log.i(TAG,msg); 
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(uniqueGenereator.addAndGet(1), mBuilder.build());
    }
}
