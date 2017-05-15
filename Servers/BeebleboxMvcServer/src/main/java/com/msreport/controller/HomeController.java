package com.msreport.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.msreport.domain.SensorInfo;
import com.msreport.service.SensorInfoService;
import com.msreport.to.SensorTO;
import com.msreport.util.ServerConstants;
 
@Controller
public class HomeController {
 
	@Autowired  
	SensorInfoService sensorInfoService;  
	
	@RequestMapping(value="/welcome", method = RequestMethod.GET)
	public String welcome(ModelMap model) {
 
		model.addAttribute("message", "Maven Web Project + Spring 3 MVC - welcome()");
		System.out.println("message " );
		//Spring uses InternalResourceViewResolver and return back index.jsp
		return "index";
 
	}
 
	@RequestMapping(value="/welcome/{name}", method = RequestMethod.GET)
	public String welcomeName(@PathVariable String name, ModelMap model) {
 
		model.addAttribute("message", "Maven Web Project + Spring 3 MVC - " + name);
		return "index";
 
	}
	
	@RequestMapping(value="/androidMessage", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public SensorTO androidMessage(HttpServletRequest request, HttpServletResponse response,
			@RequestBody SensorTO sensorTO, ModelMap model) throws IOException {
		System.out.println("start androidMessage...");
		String dataType = sensorTO.getDataType();
		String time = sensorTO.getTimeInLong();
		System.out.println("dataType = " + dataType + "time " + time);
		SensorInfo sensorInfo = new SensorInfo();
//		if(dataType.equalsIgnoreCase(ServerConstants.DATATYPE)) {
			System.out.println("inside if = " + dataType);
			SensorInfo existingSensorInfo = sensorInfoService.getSensorInfo(time, dataType);
			if(existingSensorInfo == null) {
				System.out.println("inside existing = " + dataType);
				sensorInfo.setSensor_datatype(dataType);
				sensorInfo.setTime(time);
				sensorInfo.setIsMitigated("false");
				sensorInfoService.insertSensorInfoData(sensorInfo);
			}
//		}
		return sensorTO;
	}
	
}