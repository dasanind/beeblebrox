package com.msreport.gcm.server;

import java.io.Serializable;

public class GcmMessage implements Serializable {
	private static final long serialVersionUID = -6855931308069261362L;
	protected String datatype;
	protected String subject;
	protected String message;
	protected String date;
	protected String time;
	protected String imageUrl;
	protected String timeInLong;
	
	public GcmMessage() {
		super();
	}
	
	public GcmMessage(String datatype, String subject, String message, 
			String date, String time, String imageUrl, String timeInLong) {
		super();
		setDatatype(datatype);
		setSubject(subject);
		setMessage(message);
		setDate(date);
		setTime(time);
		setImageUrl(imageUrl);
		setTimeInLong(timeInLong);
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getTimeInLong() {
		return timeInLong;
	}

	public void setTimeInLong(String timeInLong) {
		this.timeInLong = timeInLong;
	}
	
}
