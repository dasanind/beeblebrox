package com.msreport.to;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

@JsonPropertyOrder({"dataType", "timeInLong"})
public class SensorTO {
	@JsonProperty(value="dataType")
	private String dataType;
	
	@JsonProperty(value="timeInLong")
	private String timeInLong;

	public SensorTO() {
		super();
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getTimeInLong() {
		return timeInLong;
	}

	public void setTimeInLong(String timeInLong) {
		this.timeInLong = timeInLong;
	}
	
}
