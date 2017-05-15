package com.msreport.gcm.server;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

import java.util.List;

/**
 * This class handles all the CRUD operations related to
 * Sensor Data entity.
 *
 */
public class SensorData {
	
	static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	/**
	 * Create the sensorData
	 * @param dataType: type of sensor data
	 * @param dateOfFireData : date of occurrence
	 * @param timeOfFireData : time of occurrence
	 * @param timeInLongData : time of occurrence (in long) 
	 * @param isRead : whether sensor data read or not 
	 * @return  added sensorData
	 */
	public static void createSensorData(String dataType, String dateOfFireData,
		  String timeOfFireData, String timeInLongData, String isRead, String isMessageSent) {
		Key sensorDataKey = KeyFactory.createKey("SensorData", timeInLongData);
		Entity sensorData = new Entity("SensorData", sensorDataKey);
		sensorData.setProperty("timeInLongData", timeInLongData);
		sensorData.setProperty("dataType", dataType);
		sensorData.setProperty("dateOfFireData", dateOfFireData);
		sensorData.setProperty("timeOfFireData", timeOfFireData);
		sensorData.setProperty("isRead", isRead);
		sensorData.setProperty("isMessageSent", isMessageSent);
		datastore.put(sensorData); //save it
	}

	/**
	 * Update the sensorData
	 * @param timeInLongData : time of occurrence (in long) 
	 * @param isRead : whether sensor data read or not 
	 * @return  updated sensorData
	 */
	@SuppressWarnings("deprecation")
	public static void updateSensorDataRead(String timeInLongData, String isRead) {
		Query query = new Query("SensorData");
		query.addFilter("timeInLongData", FilterOperator.EQUAL, timeInLongData);
		PreparedQuery pq = datastore.prepare(query);
		Entity sensorData = pq.asSingleEntity();
	 
		sensorData.setProperty("timeInLongData", timeInLongData);
		sensorData.setProperty("isRead", isRead);
  	  	datastore.put(sensorData); //save it
	}
	
	/**
	 * Update the sensorData
	 * @param timeInLongData : time of occurrence (in long) 
	 * @param isMessageSent : whether sensor data sent or not 
	 * @return  updated sensorData
	 */
	@SuppressWarnings("deprecation")
	public static String updateSensorDataSent(String timeInLongData, String isMessageSent) {
		Query query = new Query("SensorData");
		query.addFilter("timeInLongData", FilterOperator.EQUAL, timeInLongData);
		PreparedQuery pq = datastore.prepare(query);
		Entity sensorData = pq.asSingleEntity();
		if(sensorData != null) {
			sensorData.setProperty("timeInLongData", timeInLongData);
			sensorData.setProperty("isMessageSent", isMessageSent);
	  	  	datastore.put(sensorData); //save it
	  	  return "Sensor data updated successfully";
		}
  	  return "No Sensor data to update";
	}
  
	/**
	 * Delete sensorData
	 * @param timeInLongData: timeInLongData to be deleted
	 * @return status string
	 */
	@SuppressWarnings("deprecation")
	public static String deleteSensorData(String timeInLongData) {
		Query query = new Query("SensorData");
		query.addFilter("timeInLongData", FilterOperator.EQUAL, timeInLongData);
		PreparedQuery pq = datastore.prepare(query);
		Entity sensorData = pq.asSingleEntity();
		if(sensorData != null) {
			datastore.delete(sensorData.getKey()); //delete it
			return "Sensor data deleted successfully";
		}
		/*List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(10));
		for (Entity entity : results) {
			datastore.delete(entity.getKey());
		}*/
		
		return "No Sensor data to delete";
	}
  
	/**
	 * get sensorData with timeIn Long
	 * @param timeInLongData: get timeInLong
	 * @return  sensorData entity
	 */
	@SuppressWarnings("deprecation")
	public static Entity getSingleSensorData(String timeInLongData) {
		Query query = new Query("SensorData");
		query.addFilter("timeInLongData", FilterOperator.EQUAL, timeInLongData);
		List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(10));
		System.out.println("Number of records found " + results.size());
		if (!results.isEmpty()) {
			return (Entity)results.get(0);
		}
		return null;
	}
  
	/**
	 * get sensorData with timeIn Long
	 * @param timeInLongData: get timeInLong
	 * @return  sensorData entity
	 *//*
	@SuppressWarnings("deprecation")
	public static List<Entity> getAllSensorData(String dataType) {
		Query query = new Query("SensorData");
		query.addFilter("dataType", FilterOperator.EQUAL, dataType);
		query.addSort("timeInLongData", Query.SortDirection.DESCENDING);
		List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		System.out.println("Number of records found " + results.size());
		if (!results.isEmpty()) {
			return results;
		}
		return null;
	}*/
}
