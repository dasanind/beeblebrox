package com.msreport.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.msreport.domain.SensorInfo;
import com.msreport.util.SensorInfoRowMapper;

public class SensorInfoDAOImpl implements SensorInfoDAO {

	@Autowired 
	DataSource dataSource;
	 
	@Override
	public void insertData(SensorInfo sensorInfo) {
		System.out.println("start insertData...");
		String sql = "INSERT INTO SENSORINFO " +
		"(TIME, SENSOR_DATATYPE, ISMITIGATED) VALUES (?, ?, ?)";
		System.out.println("Insert sql -- " + sql);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);  
		jdbcTemplate.update(sql,  
		    new Object[] { sensorInfo.getTime(),  
		    		sensorInfo.getSensor_datatype(), sensorInfo.getIsMitigated() }); 
	}


	@Override
	public SensorInfo findSensorInfoTime(String time, String sensorDatatype) {
		System.out.println("start findSensorInfoTime...");
		List<SensorInfo> sensorInfoList = new ArrayList<SensorInfo>();  
		SensorInfo sensorInfo = null;
		String sql = "SELECT * FROM SENSORINFO WHERE TIME = " + time + " AND SENSOR_DATATYPE = '" + sensorDatatype + "'";  
		System.out.println("Select sql -- " + sql);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);  
		sensorInfoList = jdbcTemplate.query(sql, new SensorInfoRowMapper()); 
		System.out.println("size of sensorInfoList " + sensorInfoList.size());
		if(sensorInfoList.size() > 0) {
			sensorInfo = sensorInfoList.get(0);
		}
		return sensorInfo;  
	}

}
