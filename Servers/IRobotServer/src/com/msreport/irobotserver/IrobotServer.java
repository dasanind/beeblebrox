package com.msreport.irobotserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

public class IrobotServer {
	
	public static final String FIRE_DATATYPE= "fireData";
	public static final String MOTION_DATATYPE= "motionData";
	
	public static final String FIRE_DATA_FILENAME = "firedata.txt";
	public static final String MOTION_DATA_FILENAME = "motiondata.txt";
	
	public static final String FIRE_MITIGATION_FILENAME = "firemitigated.txt";
	public static final String MOTION_MITIGATION_FILENAME = "motionmitigated.txt";
	
	public static final String CHECK_FIRE_FILENAME = "checkfire.txt";
	public static final String CHECK_MOTION_FILENAME= "checkmotion.txt";
	
	public static final String CONFIG_FIRE_FILENAME = "configfire.txt";
	public static final String CONFIG_MOTION_FILENAME= "configmotion.txt";
	
	public static void writeIpToFile() {
		try {
			InetAddress addr = InetAddress.getLocalHost();    
			String address = addr.getHostAddress();
			System.out.println("address " + address);
			File file = new File(File.separator + "Users" + File.separator + "anindita" + File.separator
	                + "Dropbox" + File.separator + "Public" + File.separator + "BeebleBox" + File.separator +"ipaddress.txt");
	        // if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
				System.out.println(file.getName() + " is created!");
			}
			BufferedReader reader = null;
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			System.out.println("line " + line);
			if((line == null) || !(line.equalsIgnoreCase(address)) ) {
				System.out.println("Ip address is written!");
				FileWriter fstream = new FileWriter(file.getAbsoluteFile());
	            BufferedWriter ipaddress = new BufferedWriter(fstream);
	            ipaddress.write(address);
	            ipaddress.close();
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Connection connectToDb() {
		Connection con = null;
		try {
        String url = "jdbc:mysql://localhost:3306/";

        String db = "sensordata";
        String driver = "com.mysql.jdbc.Driver";
        String user = "anindita";
        String pass = "";
        
		Class.forName(driver).newInstance();
		
        con = DriverManager.getConnection(url + db, user, pass);
        
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}
	
	public static SensorInfo getSensorInfo(String dataType) {
		SensorInfo sensorInfo = new SensorInfo();
		try {
			Connection con = connectToDb();
			Statement st = con.createStatement();
			String sql = "SELECT * FROM SENSORINFO WHERE ISMITIGATED = 'false' AND SENSOR_DATATYPE = '" + dataType +"' ORDER BY TIME DESC ";
			System.out.println("getSensorInfo, Select sql = " + sql);
			ResultSet rs = st.executeQuery(sql);
			boolean exists = rs.first();
			System.out.println("getSensorInfo, exists " + exists);
			if(exists) {
				System.out.println("getSensorInfo, inside if exists = " + exists);
				sensorInfo.setTime(rs.getString("time"));
				System.out.println("getSensorInfo, time = " + rs.getString("time") );
				sensorInfo.setSensor_datatype(rs.getString("sensor_datatype"));
				System.out.println("getSensorInfo, sensor_datatype = " + rs.getString("sensor_datatype"));
				sensorInfo.setIsMitigated(rs.getString("ismitigated"));
				System.out.println("getSensorInfo, ismitigated = " +rs.getString("ismitigated"));
			}
			rs.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sensorInfo;
	}
	
	public static boolean checkSensorOrMitigationOrConfigDataExists(String fileName) {
		File sensorData = new File(File.separator + "Users" + File.separator + "anindita" + File.separator
                + "Dropbox" + File.separator + "Public" + File.separator + "BeebleBox" + File.separator +fileName);
		boolean fileExists = sensorData.exists();
		if(fileExists){
			System.out.println("checkSensorOrMitigationOrConfigDataExists, File " + sensorData.getName() + " exists. " );
			return fileExists;
		}else{
			System.out.println("checkSensorOrMitigationOrConfigDataExists, File " + sensorData.getName() + " not found! ");
			return fileExists;
		}
	}
	
	public static void writeCheckProblemToFile(SensorInfo sensorInfo, String problemFileName, String checkProblemFileName) {
		File sensorData = new File(File.separator + "Users" + File.separator + "anindita" + File.separator
                + "Dropbox" + File.separator + "Public" + File.separator + "BeebleBox" + File.separator +problemFileName);
		String line;
		BufferedReader reader = null;
		String time = "";
		String sensor_dataType = "";
		try {
			reader = new BufferedReader(new FileReader(sensorData));
			while ((line = reader.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line);
	        	sensor_dataType = st.nextToken();
	        	st.nextToken(); //firedata or motiondata
				  	st.nextToken(); //timeOfFireText or timeOfMotionText
				  	st.nextToken();//dateOfFireData or dateOfMotionData
				  	st.nextToken();//timeOfFireData or timeOfMotionData
				  	st.nextToken();//timeInLongText
				  	time = st.nextToken();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		if((time.equalsIgnoreCase(sensorInfo.getTime())) && (sensor_dataType.equalsIgnoreCase(sensorInfo.getSensor_datatype()))) {
			try {
//				File file = new File(File.separator + "Users" + File.separator + "anindita" + File.separator
//		                + "Dropbox" + File.separator + "Public" + File.separator + "BeebleBox" + File.separator +"checkfire.txt");
				File file = new File(File.separator + "Users" + File.separator + "anindita" + File.separator
		                + "Dropbox" + File.separator + "Public" + File.separator + "BeebleBox" + File.separator +checkProblemFileName);
		        // if file doesn't exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}
				FileWriter fstream = new FileWriter(file.getAbsoluteFile());
	            BufferedWriter checkProblem = new BufferedWriter(fstream);
	            if(sensor_dataType.equalsIgnoreCase(FIRE_DATATYPE)) {
	            	checkProblem.write("checkfire");
	            } if(sensor_dataType.equalsIgnoreCase(MOTION_DATATYPE)) {
	            	checkProblem.write("checkmotion");
	            }
	            checkProblem.close();
	            System.out.println(file.getName() + " is created!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void updateSensorInfo(SensorInfo sensorInfo) {
		String time = sensorInfo.getTime();
		String dataType = sensorInfo.getSensor_datatype();
		try {
			Connection con = connectToDb();
			Statement st = con.createStatement();
			String sql = "UPDATE SENSORINFO SET ISMITIGATED = 'true' WHERE TIME = '" + time + "' AND SENSOR_DATATYPE = '" + dataType + "'";
			System.out.println("updateSensorInfo, Update sql = " + sql);
			st.executeUpdate(sql);
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteProblemMitigatedFile(String mitigationFileName) {
		File problemMitigatedData = new File(File.separator + "Users" + File.separator + "anindita" + File.separator
                + "Dropbox" + File.separator + "Public" + File.separator + "BeebleBox" + File.separator +mitigationFileName);
		boolean fileExists = problemMitigatedData.exists();
		if(fileExists){
			if(problemMitigatedData.delete()){
    			System.out.println(problemMitigatedData.getName() + " is deleted!");
    		}else{
    			System.out.println("Delete operation is failed.");
    		}
		}
	}
	
	public static SensorInfo insertSensorInfo(String dataType, String problemFileName) {
		SensorInfo sensorInfo = null;
		File sensorData = new File(File.separator + "Users" + File.separator + "anindita" + File.separator
                + "Dropbox" + File.separator + "Public" + File.separator + "BeebleBox" + File.separator +problemFileName);
		String line;
		BufferedReader reader = null;
		String time = "";
		String sensor_dataType = "";
		try {
			reader = new BufferedReader(new FileReader(sensorData));
			while ((line = reader.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line);
	        	sensor_dataType = st.nextToken();
	        	st.nextToken(); //firedata or motiondata
				  	st.nextToken(); //timeOfFireText or timeOfMotionText
				  	st.nextToken();//dateOfFireData or dateOfMotionData
				  	st.nextToken();//timeOfFireData or timeOfMotionData
				  	st.nextToken();//timeInLongText
				  	time = st.nextToken();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		if(sensor_dataType.equalsIgnoreCase(dataType)) {
			try {
				Connection con = connectToDb();
				Statement st = con.createStatement();
				String sql = "INSERT INTO SENSORINFO (TIME, SENSOR_DATATYPE, ISMITIGATED) VALUES ('"+
				time + "', '" + sensor_dataType + "', 'false')";
				System.out.println("insertSensorInfo, Insert sql = " + sql);
				int row = st.executeUpdate(sql);
				
				System.out.println("insertSensorInfo, row = " + row);
				if(row >0) {
					sensorInfo = new SensorInfo();
					sensorInfo.setTime(time);
					sensorInfo.setSensor_datatype(sensor_dataType);
					sensorInfo.setIsMitigated("false");
				}
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return sensorInfo;
	}
	
	public static void mitigateProblem(String dataType, String problemFileName, String checkProblemFileName, 
			String mitigationFileName, String configFileName) {
//		SensorInfo sensorInfo = getSensorInfo(dataType);
		boolean sensorDataExists = checkSensorOrMitigationOrConfigDataExists(problemFileName);
		if(sensorDataExists) {
			SensorInfo sensorInfo = getSensorInfo(dataType);
			String time = sensorInfo.getTime();
			System.out.println("mitigateProblem, time = " + time );//+ " " + sensorInfo.getIsMitigated() + " " + sensorInfo.getSensor_datatype());
//			if(sensorDataExists && (time == null)) {
			if(time == null) {
				//check whether configuration file exists
				boolean configFileExists = checkSensorOrMitigationOrConfigDataExists(configFileName);
				//if exists then insert the record into the db
				//set sensor info and time to these new values
				if(configFileExists) {
					sensorInfo = insertSensorInfo(dataType, problemFileName);
					if(sensorInfo != null)
						time = sensorInfo.getTime();
					System.out.println("mitigateProblem, time when configfile exists = " + time );
				}
			}
//			if(sensorDataExists && (time != null)) {
			if(time != null) {
				writeCheckProblemToFile(sensorInfo, problemFileName, checkProblemFileName);
				
				int flag = 0;
				int index = 1;
				while(index !=flag) {
					boolean sensorMitigatedDataExists = checkSensorOrMitigationOrConfigDataExists(mitigationFileName);
					if(sensorMitigatedDataExists) {
						index = 0;
						updateSensorInfo(sensorInfo);
					}
				}
				deleteProblemMitigatedFile(mitigationFileName);
			}
		}
	}
	
	public static void main(String[] args) {    
		
		while(true) {
			writeIpToFile();
			
			mitigateProblem(FIRE_DATATYPE, FIRE_DATA_FILENAME, CHECK_FIRE_FILENAME, FIRE_MITIGATION_FILENAME, CONFIG_FIRE_FILENAME);
			mitigateProblem(MOTION_DATATYPE, MOTION_DATA_FILENAME, CHECK_MOTION_FILENAME, MOTION_MITIGATION_FILENAME, CONFIG_MOTION_FILENAME);
			
		}
	}
}
