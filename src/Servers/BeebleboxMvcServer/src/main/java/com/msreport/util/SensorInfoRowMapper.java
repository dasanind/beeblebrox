package com.msreport.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.msreport.domain.SensorInfo;

public class SensorInfoRowMapper implements RowMapper<SensorInfo> {
	
	@Override  
	 public SensorInfo mapRow(ResultSet resultSet, int line) throws SQLException {  
		
		SensorInfo sensorInfo = new SensorInfo();  
	    
		sensorInfo.setTime(resultSet.getString(1)); 
		sensorInfo.setSensor_datatype(resultSet.getString(2));  
		sensorInfo.setIsMitigated(resultSet.getString(3));  
		    
		return sensorInfo; 
	 }
}
