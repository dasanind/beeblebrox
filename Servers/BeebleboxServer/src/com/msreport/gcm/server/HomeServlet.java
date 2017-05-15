package com.msreport.gcm.server;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

 /**
  * Servlet that adds display number of devices and button to send a message.
  * <p>
  * This servlet is used just by the browser (i.e., not device) and contains the
  * main page of the demo app.
  */
 @SuppressWarnings("serial")
 public class HomeServlet extends BaseServlet {

	 static final String ATTRIBUTE_STATUS = "status";
	 public static GcmMessage gcmMessage;
	 public static GcmMessage getGcmMessage() {
		 return gcmMessage;
	 }

	 public static void setGcmMessage(GcmMessage gcmMessage) {
		 HomeServlet.gcmMessage = gcmMessage;
	 }
	 /*Reseting all the static variables*/
	 public static void reset() {
		 setGcmMessage(new GcmMessage());
	 }


	 DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	 /**
	  * Displays the existing messages and offer the option to send a new one.
	  */
	 @Override
	 protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      	throws IOException {
		 resp.setContentType("text/html");
		 PrintWriter out = resp.getWriter();

		 out.print("<html><body>");
		 out.print("<head>");
		 out.print("  <title>Beeblebox Server</title>");
		 out.print("  <link rel='icon' href='favicon.png'/>");
		 out.print("</head>");
		 String status = (String) req.getAttribute(ATTRIBUTE_STATUS);
		 if (status != null) {
			 out.print(status);
		 }
		 int total = Datastore.getTotalDevices();
		 if (total == 0) {
			 out.print("<h2>No devices registered!</h2>");
		 } else {
			 out.print("<h2>" + total + " device(s) registered!</h2>");
//	         out.print("<form name='form' method='POST' action='sendAll'>");
//	         out.print("<input type='submit' value='Send Message' />");
//	         out.print("</form>");
//			 Datastore.unregister("APA91bGExl_ZH_0inL-bu6_ttB9IJd2PyqnAh18mJqcG_9FkpZDGlqwIua3mXCzCazPKpgbHGcpu7rNuSOHknpAf5fmBpUm3Jck1iLtU7Ae0eBc-w9UFmJ1kkyvT3vjvjMExCDNVxGSKrB0pA2dvvaHEy35sCmHwKQ");
			 sendFireNotification(req); 
			 sendMotionNotification(req); 
		 }
		 out.print("</body></html>");
		 resp.setStatus(HttpServletResponse.SC_OK);
	 }
	 
	 protected void sendFireNotification(HttpServletRequest req) {
		 logger.info("***start sendFireNotification....");
		 //Read a file and send response
		 BufferedReader reader = null;
	     try {
	    	 
	    	 //TODO need to make this configurable
	         URL fireDataUrl = new URL("https://dl.dropboxusercontent.com/u/9658850/BeebleBox/firedata.txt");
	         HttpURLConnection fireDataConnection = (HttpURLConnection) fireDataUrl.openConnection();
	         int responseCode = fireDataConnection.getResponseCode();
	         if(responseCode == 200) {
	        	 logger.info("File found. Yippee...");
		         reader = new BufferedReader(new InputStreamReader(fireDataConnection.getInputStream()));
		         String line;
		         int count = 0;
	         
		         while ((line = reader.readLine()) != null) {
		        	 count = count + 1;
		       	  	 logger.info("###############line " + line + " count " + count);
		       	  	 StringTokenizer st = new StringTokenizer(line);
		       	     String dataType = st.nextToken();
		        	 if(dataType.equalsIgnoreCase(ServerConstants.FIRE_DATATYPE)) {
		        		 String firedata = st.nextToken(); 
		        		 String timeOfFireText = st.nextToken();
		  				 String dateOfFireData = st.nextToken();
		  				 String timeOfFireData = st.nextToken();
		  				 String timeInLongText = st.nextToken();
		  				 String timeInLongData = st.nextToken();
		  				 String isRead = "false";
		  				 String isMessageSent = "false";
						 logger.info(dataType + " " + firedata + " " + timeOfFireText + " " + dateOfFireData +  
		  					  " " + timeOfFireData + " " + timeInLongText + " " + timeInLongData);
		  				 gcmMessage = new GcmMessage();
		        		 gcmMessage.setDatatype(dataType);
		        		 gcmMessage.setSubject(ServerConstants.FIRE_SUBJECT);
		        		 gcmMessage.setMessage(ServerConstants.FIRE_MESSAGE);
		        		 gcmMessage.setDate(dateOfFireData);//dateFormat.format(date));
		        		 gcmMessage.setTime(timeOfFireData);
		        		 gcmMessage.setImageUrl(ServerConstants.FIRE_IMAGEURL);
		        		 gcmMessage.setTimeInLong(timeInLongData);
		        		 //Date date = new Date();
		        		 
//				  		 String isdeleted = SensorData.deleteSensorData(timeInLongData);
//		  				 logger.info("isdeleted " + isdeleted);
		  				 Entity sensorData = SensorData.getSingleSensorData(timeInLongData);
		  				 logger.info("sensorData " + sensorData);
		  				 if(sensorData == null) {
		  					 isRead = "true";
		  					 logger.info("inside sensorData " + sensorData);
		  					//TODO uncomment this line 
		  					 SensorData.createSensorData(dataType, dateOfFireData, timeOfFireData, timeInLongData, isRead,
		  						isMessageSent);
		  					 sensorData = SensorData.getSingleSensorData(timeInLongData);
			  				 logger.info("sensorData after creation " + sensorData);
			        		 setGcmMessage(gcmMessage);
			        		 
			        		 sendMessage (req);
		  				 } else {
		  					 isRead = (String) sensorData.getProperty("isRead");
		  					 isMessageSent = (String) sensorData.getProperty("isMessageSent");
		  					 logger.info("isRead " + isRead + " isMessageSent " + isMessageSent);
		  					 if(isMessageSent.equalsIgnoreCase("false")) {
		  						 setGcmMessage(gcmMessage);
				        		 sendMessage (req);
		  					 }
		  				 }
		        	 }
		         }
		         reader.close();
	         } else if(responseCode == 404) {
	        	 logger.info("File not found. Try Again...");
	         } else {
	        	 logger.info("Connection error");
	         }
	
	     } catch (MalformedURLException e) {
	         logger.info("Malformed url");
	     } catch (IOException e) {
	    	 logger.info("IO exception");
	     }

	     finally {
	    	 try {
	    		 if (reader != null)
	    			 reader.close();
			 } catch (IOException ex) {
				 ex.printStackTrace();
			 }
	     }
	 }
	 
	 protected void sendMotionNotification(HttpServletRequest req) {
		 logger.info("***start sendMotionNotification....");
		 //Read a file and send response
		 BufferedReader reader = null;
	     try {
	    	 
	    	 //TODO need to make this configurable
	         URL motionDataUrl = new URL("https://dl.dropboxusercontent.com/u/9658850/BeebleBox/motiondata.txt");
	         HttpURLConnection motionDataConnection = (HttpURLConnection) motionDataUrl.openConnection();
	         int responseCode = motionDataConnection.getResponseCode();
	         if(responseCode == 200) {
	        	 logger.info("File found. Yippee...");
		         reader = new BufferedReader(new InputStreamReader(motionDataConnection.getInputStream()));
		         String line;
		         int count = 0;
		         
		         while ((line = reader.readLine()) != null) {
		        	 count = count + 1;
		       	  	 logger.info("********line " + line + " count " + count);
		       	  	 StringTokenizer st = new StringTokenizer(line);
		       	     String dataType = st.nextToken();
		        	 if(dataType.equalsIgnoreCase(ServerConstants.MOTION_DATATYPE)) {
		        		 String motiondata = st.nextToken(); 
		        		 String timeOfMotionText = st.nextToken();
		  				 String dateOfMotionData = st.nextToken();
		  				 String timeOfMotionData = st.nextToken();
		  				 String timeInLongText = st.nextToken();
		  				 String timeInLongData = st.nextToken();
		  				 String isRead = "false";
		  				 String isMessageSent = "false";
						 logger.info(dataType + " " + motiondata + " " + timeOfMotionText + " " + dateOfMotionData +  
		  					  " " + timeOfMotionData + " " + timeInLongText + " " + timeInLongData);
		  				 gcmMessage = new GcmMessage();
		        		 gcmMessage.setDatatype(dataType);
		        		 gcmMessage.setSubject(ServerConstants.MOTION_SUBJECT);
		        		 gcmMessage.setMessage(ServerConstants.MOTION_MESSAGE);
		        		 gcmMessage.setDate(dateOfMotionData);//dateFormat.format(date));
		        		 gcmMessage.setTime(timeOfMotionData);
		        		 gcmMessage.setImageUrl(ServerConstants.MOTION_IMAGEURL);
		        		 gcmMessage.setTimeInLong(timeInLongData);
		        		 //Date date = new Date();
		        		 
//					  	 String isdeleted = SensorData.deleteSensorData(timeInLongData);
//			  			 logger.info("isdeleted " + isdeleted);
		  				 Entity sensorData = SensorData.getSingleSensorData(timeInLongData);
		  				 logger.info("sensorData " + sensorData);
		  				 if(sensorData == null) {
		  					 isRead = "true";
		  					 logger.info("inside sensorData " + sensorData);
		  					 //TODO uncomment this line 
			  				 SensorData.createSensorData(dataType, dateOfMotionData, timeOfMotionData, timeInLongData, isRead,
			  					isMessageSent);
		  					 sensorData = SensorData.getSingleSensorData(timeInLongData);
			  				 logger.info("sensorData after creation " + sensorData);
			        		 setGcmMessage(gcmMessage);
			        		 
			        		 sendMessage (req);
		  				 } else {
		  					 isRead = (String) sensorData.getProperty("isRead");
		  					 isMessageSent = (String) sensorData.getProperty("isMessageSent");
		  					 logger.info("isRead " + isRead + " isMessageSent " + isMessageSent);
		  					 if(isMessageSent.equalsIgnoreCase("false")) {
		  						 setGcmMessage(gcmMessage);
				        		 sendMessage (req);
		  					 }
		  				 }
		        	 }
		         }
		         reader.close();
	         } else if(responseCode == 404) {
	        	 logger.info("File not found. Try Again...");
	         } else {
	        	 logger.info("Connection error");
	         }
		
	     } catch (MalformedURLException e) {
	         logger.info("Malformed url");
	     } catch (IOException e) {
	    	 logger.info("IO exception");
	     }

	     finally {
	    	 try {
	    		 if (reader != null)
	    			 reader.close();
			 } catch (IOException ex) {
				 ex.printStackTrace();
			 }
	     }
	 }

	 protected void sendMessage (HttpServletRequest req) {
	  
		 List<String> devices = Datastore.getDevices();
		 String status;
		 if (devices.isEmpty()) {
			 status = "Message ignored as there is no device registered!";
		 } else {
			 Queue queue = QueueFactory.getQueue("gcm");
			  // NOTE: check below is for demonstration purposes; a real application
			  // could always send a multicast, even for just one recipient
			 if (devices.size() == 1) {
				 
				 // send a single message using plain post
				 String device = devices.get(0);
				 queue.add(withUrl("/send").param(
						  SendMessageServlet.PARAMETER_DEVICE, device));
				 status = "Single message queued for registration id " + device;
			 } else {
				 // send a multicast message using JSON
				 // must split in chunks of 1000 devices (GCM limit)
		        int total1 = devices.size();
		        List<String> partialDevices = new ArrayList<String>(total1);
		        int counter = 0;
		        int tasks = 0;
		        for (String device : devices) {
		          counter++;
		          partialDevices.add(device);
		          int partialSize = partialDevices.size();
		          if (partialSize == Datastore.MULTICAST_SIZE || counter == total1) {
		            String multicastKey = Datastore.createMulticast(partialDevices);
		            logger.fine("Queuing " + partialSize + " devices on multicast " +
		                multicastKey);
		            TaskOptions taskOptions = TaskOptions.Builder
		                .withUrl("/send")
		                .param(SendMessageServlet.PARAMETER_MULTICAST, multicastKey)
		                .method(Method.POST);
		            queue.add(taskOptions);
		            partialDevices.clear();
		            tasks++;
		          }
		        }
		        status = "Queued tasks to send " + tasks + " multicast messages to " +
	            	total1 + " devices";
			 }
		 }
		 req.setAttribute(HomeServlet.ATTRIBUTE_STATUS, status.toString());
	  
  	}
  
  	@Override
  	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
  		throws IOException {
	  
	  doGet(req, resp);
  	}

 }
