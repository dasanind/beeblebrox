package com.msreport.beeblebox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class CommonUtilities {

    /**
     * Base URL of the GCM Server (such as http://my_host:8080/gcm-server)
     */
    static final String SERVER_URL = "http://192.168.1.107:8080";

    /**
     * Google API project id registered to use GCM.
     */
//    static final String SENDER_ID = "474825000197";
//    static final String SENDER_ID = "468094111291";
    static final String SENDER_ID = "267367054096";

    static final String IP_ADDRESS = "https://dl.dropboxusercontent.com/u/9658850/BeebleBox/ipaddress.txt";
    
    static final String COMMAND_SERVER_PROTOCOL = "http://";
    
    static final String COMMAND_SERVER_PORT = ":8090";
    
    static final String COMMAND_SERVER_ENDPOINT = "/BeebleboxMvcServer";
    	
    /**
     * Base URL of the Tomcat Server (such as http://localhost:portnumber/BeebleboxMvcServer)
     */
    static final String COMMAND_SERVER_URL = "http://192.168.1.107:8090/BeebleboxMvcServer";
    
    /**
     * Tag used on log messages.
     */
    static final String TAG = "Beeblebox";

    /**
     * Intent used to display a message in the screen.
     */
    static final String DISPLAY_MESSAGE_ACTION =
            "com.msreport.beeblebox.DISPLAY_MESSAGE";
    
    static final String DISPLAY_MESSAGE_ACTION1 =
            "com.msreport.beeblebox.DISPLAY_MESSAGE1";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    static final String EXTRA_MESSAGE = "message";
    
    static final String EXTRA_BUNDLE = "bundle";

	public static final String SUBJECT_HEADER= "subject";
	public static final String MESSAGE_HEADER= "message";
	public static final String DATE_HEADER= "date";
	public static final String TIME_HEADER= "time";
	public static final String IMAGEURL_HEADER= "imageUrl";
	public static final String DATATYPE_HEADER= "dataType";
	public static final String TIMEINLONG_HEADER= "timeInLong";
	
	public static final String FIRE_DATATYPE= "fireData";
	public static final String MOTION_DATATYPE= "motionData";
	
    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION1);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
    
  static void displayMessageBundle(Context context, Bundle bundle) {
      Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
      intent.putExtra(EXTRA_BUNDLE, bundle);
      context.sendBroadcast(intent);
  }
}
