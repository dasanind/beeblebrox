package com.msreport.beeblebox;

import static com.msreport.beeblebox.CommonUtilities.SENDER_ID;
import static com.msreport.beeblebox.CommonUtilities.displayMessage;
import static com.msreport.beeblebox.CommonUtilities.displayMessageBundle;

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {
	
    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
        Log.d(TAG, "GCMIntentService init");
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, getString(R.string.gcm_registered,
                registrationId));
        ServerUtilities.register(context, registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.d(TAG, "Received message. Extras: " + intent.getExtras());
//        String message = getString(R.string.gcm_message);
        //08/04/2013
//        String message = intent.getStringExtra("subject");
//        String image_url = intent.getStringExtra("image_url");
        String message = null;
        String image_url = null;
        if (intent != null) {
        	//Check the bundle for the pay load body and title
    		Bundle bundle = intent.getExtras();
//    		displayMessage(context, "Message bundle: " +  bundle);
    		displayMessageBundle(context, bundle); 
    		Log.i(TAG, "Message bundle: " +  bundle);
			message = bundle.getString("message");   
			Log.i(TAG, "Message : " +  message);
//			title = bundle.getString("title");	
//			url = bundle.getString("url");	 	   		
			image_url = bundle.getString("image_url");
        }
        //commented on 08/04/2013
//        displayMessage(context, message + "\n" +image_url);
        // notifies user
//        generateNotification(context, message + "\n" +image_url);
        generateNotification(context, message);
//        generateImageNotification(context, message,image_url); 
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.ic_stat_gcm;
        long when = System.currentTimeMillis();
        Random randomGenerator = new Random();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, Beeblebox.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
       // notificationManager.notify(0, notification);
        notificationManager.notify(randomGenerator.nextInt(), notification);
    }
 
}
