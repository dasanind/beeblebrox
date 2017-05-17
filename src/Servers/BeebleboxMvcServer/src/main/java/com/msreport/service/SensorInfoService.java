package com.msreport.service;

import com.msreport.domain.SensorInfo;

public interface SensorInfoService {

	public void insertSensorInfoData(SensorInfo sensorInfo);  
	public SensorInfo getSensorInfo(String time, String dataType);
	
}
