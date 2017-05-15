package com.msreport.beeblebox;

import static com.msreport.beeblebox.CommonUtilities.COMMAND_SERVER_ENDPOINT;
import static com.msreport.beeblebox.CommonUtilities.COMMAND_SERVER_PORT;
import static com.msreport.beeblebox.CommonUtilities.COMMAND_SERVER_PROTOCOL;
import static com.msreport.beeblebox.CommonUtilities.IP_ADDRESS;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.GsonBuilder;

public class SendCommand extends Activity {
	
	private static final String TAG = "SendCommand";
	public ArrayList<String> result = new ArrayList<String>();
	protected Button mitigateButton;
	protected Button callButton;
	protected Button mainMenuButton;
	public String imagePath;
	ImageView myImage;
	public String dataType;
	public String timeLong;
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.sendcommand);
            setTitle(R.string.app_name);
            ((TextView)((FrameLayout)((LinearLayout)((ViewGroup) getWindow().getDecorView()).getChildAt(0)).getChildAt(0)).getChildAt(0)).setGravity(Gravity.CENTER);
            ((TextView)((FrameLayout)((LinearLayout)((ViewGroup) getWindow().getDecorView()).getChildAt(0)).getChildAt(0)).getChildAt(0)).setTextColor(Color.WHITE);
            ((TextView)((FrameLayout)((LinearLayout)((ViewGroup) getWindow().getDecorView()).getChildAt(0)).getChildAt(0)).getChildAt(0)).setTextSize(15);
            View title = getWindow().findViewById(android.R.id.title);
            View titleBar = (View) title.getParent();
            titleBar.setBackgroundColor(Color.DKGRAY);
            
            Bundle bun = getIntent().getExtras();
            result = bun.getStringArrayList("incidentDetails");
            
            mitigateButton = (Button) findViewById(R.id.mitigate_button);
            callButton = (Button) findViewById(R.id.call_button);
            mainMenuButton = (Button) findViewById(R.id.main_menu_button);
            
            int size = result.size();
         	String[] records = new String[size];
         	Iterator it = result.iterator();
         	int i = 0;
         	while(it.hasNext()){
         		Object element = it.next();
         		records[i]=""+element;
         		Log.v("records["+i+"]", records[i]);
         		records[i]=records[i].replace("[", "").replace("]", "");
         		Log.d(TAG, "records["+i+"] = " + records[i]);
         		i++;
         	}
         	imagePath = records[3];
         	Bitmap myBitmap = BitmapFactory.decodeFile(imagePath);
            myImage = (ImageView) findViewById(R.id.icon);
            myImage.setImageBitmap(myBitmap);
            
            dataType = records[5];
            timeLong = records[6];
            
            /*if(dataType.equalsIgnoreCase(FIRE_DATATYPE)) {
            	mitigateButton.setText("Send Command");
            } else if(dataType.equalsIgnoreCase(MOTION_DATATYPE)) {
            	mitigateButton.setText("Call");
            }*/
            
            mitigateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	
                	execute(dataType, timeLong);
                	String send_command_complete = "done";
                	Bundle bundle= new Bundle();
                	Intent intent = new Intent();
                	bundle.putString("send_command_complete", send_command_complete);
                	intent.setClass(SendCommand.this, Beeblebox.class);
                  	intent.putExtras(bundle);
                  	
                  	startActivityForResult(intent,0);
                }
            });
            
            callButton.setOnClickListener(new View.OnClickListener() {
    	    	
                public void onClick(View view) {
                                                     
                	String send_command_call = "call";
                	Bundle bundle= new Bundle();
                	Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                    startActivity(dialIntent);
                	Intent intent = new Intent();
                	bundle.putString("send_command_call", send_command_call);
                	intent.setClass(SendCommand.this, Beeblebox.class);
                  	intent.putExtras(bundle);
                  	
                  	startActivityForResult(intent,0);
                }
            });

            mainMenuButton.setOnClickListener(new View.OnClickListener() {
    	    	
                public void onClick(View view) {
                                                     
                	String send_command_cancelled = "cancel";
                	Bundle bundle= new Bundle();
                	Intent intent = new Intent();
                	bundle.putString("send_command_cancelled", send_command_cancelled);
                	intent.setClass(SendCommand.this, Beeblebox.class);
                  	intent.putExtras(bundle);
                  	
                  	startActivityForResult(intent,0);
                }
            });
	 }
	 
	 public static void execute(String dataType, String timeLong) {
		 Map<String, String> sensor = new HashMap<String, String>();
		 sensor.put(CommonUtilities.DATATYPE_HEADER, dataType);
		 sensor.put(CommonUtilities.TIMEINLONG_HEADER, timeLong);
		 String json = new GsonBuilder().create().toJson(sensor, Map.class);

	     String ipaddress = TextDownloader.downloadText(IP_ADDRESS);
	     if(ipaddress != null) {
	    	 String serverUrl = COMMAND_SERVER_PROTOCOL + ipaddress.trim() + COMMAND_SERVER_PORT
		    		 + COMMAND_SERVER_ENDPOINT + "/androidMessage";
	    	 HttpResponse response =makeRequest(serverUrl, json);
	    	 int statusCode = response.getStatusLine().getStatusCode();
	    	 Log.v("statusCode", ""+statusCode);
	     }
		 
		 /*String serverUrl = COMMAND_SERVER_URL + "/androidMessage";
		 HttpResponse response =makeRequest(serverUrl, json);
		 int statusCode = response.getStatusLine().getStatusCode();
		 Log.v("statusCode", ""+statusCode);*/
		 
	 }

		public static HttpResponse makeRequest(String uri, String json) {
		    try {
		        HttpPost httpPost = new HttpPost(uri);
		        httpPost.setEntity(new StringEntity(json));
		        httpPost.setHeader("Accept", "application/json");
		        httpPost.setHeader("Content-type", "application/json");
		        Log.v("uri", uri);
		        Log.v("json", json);
		        return new DefaultHttpClient().execute(httpPost);
		    } catch (UnsupportedEncodingException e) {
		        e.printStackTrace();
		    } catch (ClientProtocolException e) {
		        e.printStackTrace();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		    return null;
		}
}
