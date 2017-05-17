package com.msreport.irobotserver;

public class SensorInfo {
	
	String time;
	String sensor_datatype;
	String isMitigated;
	
	public SensorInfo() {
		super();
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSensor_datatype() {
		return sensor_datatype;
	}

	public void setSensor_datatype(String sensor_datatype) {
		this.sensor_datatype = sensor_datatype;
	}

	public String getIsMitigated() {
		return isMitigated;
	}

	public void setIsMitigated(String isMitigated) {
		this.isMitigated = isMitigated;
	}
	
}
