package com.msreport.beeblebox;

import static com.msreport.beeblebox.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.msreport.beeblebox.CommonUtilities.EXTRA_BUNDLE;
import static com.msreport.beeblebox.CommonUtilities.SENDER_ID;
import static com.msreport.beeblebox.CommonUtilities.SERVER_URL;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
//import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

/**
 * Main UI for the app.
 */

//public class Beeblebox extends Activity {
public class Beeblebox extends ListActivity {

	private static final String TAG = "Beeblebox";
	private static final int EMAIL_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
	TextView mDisplay;
    AsyncTask<Void, Void, Void> mRegisterTask;
    ImageView myImage;
    
    private IncidentDbAdapter mDbHelper;
    private Long mRowId;
    private Cursor mIncidentCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "Start onCreate..."); 
        super.onCreate(savedInstanceState);
        checkNotNull(SERVER_URL, "SERVER_URL");
        checkNotNull(SENDER_ID, "SENDER_ID");
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);
        
        //open the db connection
        mDbHelper = new IncidentDbAdapter(this);
        mDbHelper.open();
        
        setContentView(R.layout.beeblebox);
        setTitle(R.string.app_name);
        ((TextView)((FrameLayout)((LinearLayout)((ViewGroup) getWindow().getDecorView()).getChildAt(0)).getChildAt(0)).getChildAt(0)).setGravity(Gravity.CENTER);
        ((TextView)((FrameLayout)((LinearLayout)((ViewGroup) getWindow().getDecorView()).getChildAt(0)).getChildAt(0)).getChildAt(0)).setTextColor(Color.WHITE);
        ((TextView)((FrameLayout)((LinearLayout)((ViewGroup) getWindow().getDecorView()).getChildAt(0)).getChildAt(0)).getChildAt(0)).setTextSize(15);
        View title = getWindow().findViewById(android.R.id.title);
        View titleBar = (View) title.getParent();
        titleBar.setBackgroundColor(Color.DKGRAY);
        
        /*mDisplay = (TextView) findViewById(R.id.display);
        myImage = (ImageView) findViewById(R.id.icon);*/
        registerReceiver(mHandleMessageReceiver,
                new IntentFilter(DISPLAY_MESSAGE_ACTION));
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            // Automatically registers application on startup.
            GCMRegistrar.register(this, SENDER_ID);
        } else {
            // Device is already registered on GCM, check server.
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
//                mDisplay.append(getString(R.string.already_registered) + "\n");
                Toast.makeText(Beeblebox.this, getString(R.string.already_registered), Toast.LENGTH_LONG).show();
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        ServerUtilities.register(context, regId);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                mRegisterTask.execute(null, null, null);
            }
        }
        
//        ListView lv = getListView();
//        LayoutInflater inflater = getLayoutInflater();
//        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.incident_header, lv, false);
//        lv.addHeaderView(header, null, false);
        fillData();
        registerForContextMenu(getListView());
    }

	private void fillData() {
        // Get all of the rows from the database and create the item list
    	mIncidentCursor = mDbHelper.fetchAllRecords();
        startManagingCursor(mIncidentCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{IncidentDbAdapter.KEY_SUBJECT, IncidentDbAdapter.KEY_MESSAGE, IncidentDbAdapter.KEY_DATE};
        // and an array of the fields we want to bind those fields to (in this case just text1)

        int[] to = new int[]{R.id.text1, R.id.text2, R.id.text3};
        
        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter positions = 
            new SimpleCursorAdapter(this, R.layout.incident_row, mIncidentCursor, from, to);
        setListAdapter(positions);
        positions.notifyDataSetChanged();
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, EMAIL_ID, 0, R.string.menu_email);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                long delRowId = info.id;
                Cursor delIncident = mDbHelper.fetchRecord(delRowId);
                startManagingCursor(delIncident);
                String delImagePath;
                delImagePath = delIncident.getString(
                		delIncident.getColumnIndexOrThrow(IncidentDbAdapter.KEY_IMAGEPATH));
                File file = new File(delImagePath);
                boolean deleted = file.delete();
                Log.d(TAG, "inside onContextItemSelected "+deleted);
                mDbHelper.deleteRecord(info.id);
                fillData();
                return true;
            case EMAIL_ID:
                AdapterContextMenuInfo info_incident = (AdapterContextMenuInfo) item.getMenuInfo();
            	long rowId = info_incident.id;
            	String subject;
            	String message;
            	String imagePath;
                	
            	//fetch the message and imagepath from the database
            	Cursor incident = mDbHelper.fetchRecord(rowId);
                startManagingCursor(incident);
                subject =incident.getString(
                		incident.getColumnIndexOrThrow(IncidentDbAdapter.KEY_SUBJECT));
                message = incident.getString(
                		incident.getColumnIndexOrThrow(IncidentDbAdapter.KEY_MESSAGE));
                imagePath = incident.getString(
                		incident.getColumnIndexOrThrow(IncidentDbAdapter.KEY_IMAGEPATH));
                //retrieving the message from the list
                try {
                	//sending email with the message
                    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                    emailIntent.setType("image/jpg");
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Message from Beeblebox");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Subject:"+subject+"\n"+"\n"+"\n"+"Message: "+message+"\n"
                    +"\n"+"\n"+"Sent from Beeblebox App");
                    
                    emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+imagePath));
                    startActivity(emailIntent);
                } catch (Exception e) {
                    Log.e(TAG, "sendSimpleEmail() failed to start activity.", e);
//                    Toast.makeText(this, "No handler", Toast.LENGTH_LONG).show();
                } 

                return true;
        }
        return super.onContextItemSelected(item);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            /*
             * Typically, an application registers automatically, so options
             * below are disabled. Uncomment them if you want to manually
             * register or unregister the device (you will also need to
             * uncomment the equivalent options on options_menu.xml).
             */
            /*
            case R.id.options_register:
                GCMRegistrar.register(this, SENDER_ID);
                return true;
            case R.id.options_unregister:
                GCMRegistrar.unregister(this);
                return true;
             */
//            case R.id.options_clear:
//                mDisplay.setText(null);
//                return true;
            case R.id.options_exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        unregisterReceiver(mHandleMessageReceiver);
        GCMRegistrar.onDestroy(this);
        super.onDestroy();
        if (mDbHelper != null) {
        	mDbHelper.close();
        }
    }

    private void checkNotNull(Object reference, String name) {
        if (reference == null) {
            throw new NullPointerException(
                    getString(R.string.error_config, name));
        }
    }

    private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	Bundle bundle = intent.getBundleExtra(EXTRA_BUNDLE);
//            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
        	String subject = bundle.getString(CommonUtilities.SUBJECT_HEADER);//"subject");
        	String message = bundle.getString(CommonUtilities.MESSAGE_HEADER);//"message");
            String imageUrl = bundle.getString(CommonUtilities.IMAGEURL_HEADER);//"imageUrl");
            String imagePath = "";
            if(StringUtils.isNotBlank(imageUrl)) {
            	imagePath = getImagePath(imageUrl);
            }
            //TODO this code is to be used in the next page
            /*mDisplay.append(message);//+ "\n");
            Bitmap myBitmap = ImageDownloader.downloadBitmap(imageUrl);
            if (myBitmap == null) {
        		Log.i("TAG", "message_bitmap equal to null: " + myBitmap + " image_url " + imageUrl); }
            else {
            	Log.i("TAG", "message_bitmap not equal to null: " + myBitmap + " image_url " + imageUrl); }
            myImage.setImageBitmap(myBitmap);*/
            String dateReceived = bundle.getString(CommonUtilities.DATE_HEADER);//"date");
            String timeReceived = bundle.getString(CommonUtilities.TIME_HEADER);//"time");
            String date = dateReceived + " " + timeReceived;
            String dataType = bundle.getString(CommonUtilities.DATATYPE_HEADER);
            String timeLong = bundle.getString(CommonUtilities.TIMEINLONG_HEADER);
            
            Log.d(TAG,"inside onRecieve " + date);
            saveIncident(subject, message, imageUrl, imagePath, date, dataType, timeLong); 
        }
    };
    
    protected String getImagePath(String imageUrl) {
    	String imagePath = "";
    	String extStorageDirectory;
    	extStorageDirectory = Environment.getExternalStorageDirectory().toString();
    	Log.d("extStorageDirectory", extStorageDirectory);
		FileOutputStream fileOutputStream = null;
	    String imageName = String.format(extStorageDirectory+"/%d.jpg",
	            System.currentTimeMillis());
		int quality=50;
		try {

			BitmapFactory.Options options=new BitmapFactory.Options();
			options.inSampleSize = 5;
			
			Bitmap myImage = ImageDownloader.downloadBitmap(imageUrl);
            if (myImage == null) {
        		Log.d("TAG", "message_bitmap equal to null: " + myImage + " image_url " + imageUrl); }
            else {
            	Log.d("TAG", "message_bitmap not equal to null: " + myImage + " image_url " + imageUrl); }
			
			fileOutputStream = new FileOutputStream(imageName);
							
  
			BufferedOutputStream bos = new BufferedOutputStream(
					fileOutputStream);

			myImage.compress(CompressFormat.JPEG, quality, bos);

			bos.flush();
			bos.close();
			
			imagePath = imageName.toString();
        } catch (FileNotFoundException e) { 
        	e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    	}
    	
    	return imagePath;
    }

    protected void saveIncident(String subject, String message, String imageUrl, String imagePath, String date,
    		String dataType, String timeLong) {
		long rowId = mDbHelper.createRecord(subject, message, imageUrl, imagePath, date, dataType, timeLong);
		if (rowId > 0) {
			mRowId = rowId;
		    Log.d(TAG, "mRowId inside saveIncident "+mRowId);
		}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }
    
    @Override
    protected void onResume() {
    	fillData();
    	super.onResume();
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
    	Cursor c = mIncidentCursor;
    	ArrayList<String> incidentDetails = new ArrayList<String>();
    	
    	String savedSubject = c.getString(c.getColumnIndexOrThrow(IncidentDbAdapter.KEY_SUBJECT));
    	String savedMessage = c.getString(c.getColumnIndexOrThrow(IncidentDbAdapter.KEY_MESSAGE));
    	String savedImageUrl = c.getString(c.getColumnIndexOrThrow(IncidentDbAdapter.KEY_IMAGEURL));
    	String savedImagePath = c.getString(c.getColumnIndexOrThrow(IncidentDbAdapter.KEY_IMAGEPATH));
    	String savedDate = c.getString(c.getColumnIndexOrThrow(IncidentDbAdapter.KEY_DATE));
    	String savedDataType = c.getString(c.getColumnIndexOrThrow(IncidentDbAdapter.KEY_DATATYPE));
    	String savedTimeLong = c.getString(c.getColumnIndexOrThrow(IncidentDbAdapter.KEY_TIME_LONG));
    	
    	incidentDetails.add(savedSubject);
    	incidentDetails.add(savedMessage);
    	incidentDetails.add(savedImageUrl);
    	incidentDetails.add(savedImagePath);
    	incidentDetails.add(savedDate);
    	incidentDetails.add(savedDataType);
    	incidentDetails.add(savedTimeLong);
    	
    	Bundle bundle= new Bundle();
    	Intent intent = new Intent();
    	bundle.putStringArrayList("incidentDetails", incidentDetails);
    	intent.setClass(Beeblebox.this, SendCommand.class);
      	intent.putExtras(bundle);
      	
      	startActivityForResult(intent,0);
    }
    
}