package com.msreport.dao;

import com.msreport.domain.SensorInfo;

public interface SensorInfoDAO {

	public void insertData(SensorInfo sensorInfo);
	public SensorInfo findSensorInfoTime(String time, String sensorDatatype);
}
