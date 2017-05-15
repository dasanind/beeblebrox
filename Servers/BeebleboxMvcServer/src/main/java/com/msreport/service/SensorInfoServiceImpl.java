package com.msreport.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.msreport.dao.SensorInfoDAO;
import com.msreport.domain.SensorInfo;

public class SensorInfoServiceImpl implements SensorInfoService {

	@Autowired  
	SensorInfoDAO sensorInfoDAO;
	
	@Override
	public void insertSensorInfoData(SensorInfo sensorInfo) {
		System.out.println("start insertSensorInfoData...");
		sensorInfoDAO.insertData(sensorInfo);
	}

	@Override
	public SensorInfo getSensorInfo(String time, String dataType) {
		System.out.println("start getSensorInfo....");
		return sensorInfoDAO.findSensorInfoTime(time, dataType);
	}

}
